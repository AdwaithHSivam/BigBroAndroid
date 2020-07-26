package in.gotiit.bigbro.util;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;

import java.util.List;


import in.gotiit.bigbro.QuestionsFragment;
import in.gotiit.bigbro.db.Chat;
import in.gotiit.bigbro.db.ChatDao;
import in.gotiit.bigbro.db.ChatWithUser;
import in.gotiit.bigbro.db.Contact;
import in.gotiit.bigbro.db.ContactDao;
import in.gotiit.bigbro.db.Question;
import in.gotiit.bigbro.db.QuestionDao;
import in.gotiit.bigbro.db.QuestionWithUser;
import in.gotiit.bigbro.db.RoomDb;
import in.gotiit.bigbro.ws_util.AddChatRequest;
import in.gotiit.bigbro.ws_util.AddQRequest;
import in.gotiit.bigbro.ws_util.GetChatRequest;
import in.gotiit.bigbro.ws_util.GetQRequest;
import in.gotiit.bigbro.ws_util.GetUserRequest;
import in.gotiit.bigbro.ws_util.RequestQueue;

public class Repository {
    public QuestionDao mQDao;
    public ChatDao mChatDao;
    public ContactDao mContactDao;
    private RequestQueue requestQueue;

    private Runnable ping = this::webSocketPing;
    private static final long PING_DELAY = 45000;
    private static final long RESTART_DELAY = 3000;

    Repository(Context context) {
        RoomDb db = RoomDb.getDatabase(context);
        mQDao = db.questionDao();
        mChatDao = db.chatDao();
        mContactDao = db.contactDao();
        initRequestQueue();
    }

    public Handler getRequestHandler() {
        return requestQueue.getHandler();
    }

    private void initRequestQueue() {
        requestQueue = new RequestQueue();
        requestQueue.initRequest(AddQRequest::new, mQDao.getQuestionsToUpload());
        requestQueue.initRequest(AddChatRequest::new, mChatDao.getChatsToUpload());
        requestQueue.initRequest(GetUserRequest::new, mContactDao.getContactsToFetch());
    }

    private void webSocketPing() {//this ping is to stop aws from terminating the connection
        App.instance().getWebSocket();
        App.instance().getHandler().postDelayed(ping, PING_DELAY);
    }

    public void onWebSocketOpen() {
        App.instance().getHandler().post(ping);
        RoomDb.databaseWriteExecutor.execute(() -> {
            List<Integer> openQIds = mQDao.getOpenQIds();
            requestQueue.getHandler().post(() -> {
                for (Integer qid : openQIds) {
                    new GetQRequest(qid).runOnce();
                    new GetChatRequest(qid).runOnce();
                }
            });
        });
        requestQueue.onWebSocketOpen();
    }

    public void onWebSocketClose() {
        App.instance().getHandler().removeCallbacks(ping);
        App.instance().getHandler().postDelayed(App.instance()::getWebSocket, RESTART_DELAY);
        requestQueue.onWebSocketClose();
    }

    public void firstTime() {
        RoomDb.databaseWriteExecutor.execute(() -> {
            RoomDb.firstTime();
            Contact contact = Contact.getSample();
            Question question = Question.getSample();
            Chat[] chats = Chat.getSamples();
            mContactDao.insert(contact);
            mQDao.insert(question);
            for (Chat chat : chats) {
                mChatDao.insert(chat);
            }
        });
    }

    public LiveData<List<QuestionWithUser>> getQuestions(int source) {
        if (source == QuestionsFragment.SOURCE_HOME) {
            return mQDao.getHomeQuestions();
        } else {//source == QuestionsFragment.SOURCE_FEED
            return mQDao.getFeedQuestions();
        }
    }

    public LiveData<List<ChatWithUser>> getChats(int qid){
        return mChatDao.getChat(qid);
    }

    public void insertQuestion(final Question question) {
        RoomDb.databaseWriteExecutor.execute(() -> mQDao.insert(question));
    }

    public void updateQuestion(final Question question) {
        RoomDb.databaseWriteExecutor.execute(() -> {
            if (question.uid == 0) {
                mQDao.updateLocal(
                        question.id,
                        question.globalId,
                        question.mentorId,
                        question.status,
                        question.timeUploaded
                );
            } else {
                question.id = 0;
                mQDao.updateOrInsertGlobal(question);
            }
        });
    }

    public void insertChat(final Chat chat) {
        RoomDb.databaseWriteExecutor.execute(() -> mChatDao.insert(chat));
    }

    public void updateChat(final Chat chat) {
        RoomDb.databaseWriteExecutor.execute(() -> {
            if (chat.uid == 0) {
                mChatDao.updateLocal(
                        chat.id,
                        chat.globalId,
                        chat.timeUploaded
                    );
            } else {
                Integer qid = mQDao.getLocalQid(chat.qid);
                if (qid != null) {
                    chat.qid = qid;
                    chat.id = 0;
                    mChatDao.insertIfNotExist(chat);
                }
            }
        });
    }

    public void insertContact(final Contact contact) {
        RoomDb.databaseWriteExecutor.execute(() -> mContactDao.insert(contact));
    }

    public void updateContact(final Contact contact) {
        RoomDb.databaseWriteExecutor.execute(() -> mContactDao.setUsername(contact.id, contact.username));
    }

}
