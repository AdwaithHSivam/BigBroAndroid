package in.gotiit.bigbro;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import in.gotiit.bigbro.db.Chat;
import in.gotiit.bigbro.db.Question;
import in.gotiit.bigbro.db.QuestionWithUser;
import in.gotiit.bigbro.util.App;
import in.gotiit.bigbro.util.User;
import in.gotiit.bigbro.ws_util.AcceptQRequest;
import in.gotiit.bigbro.ws_util.CloseQRequest;


public class ChatsFragment extends Fragment {

    public static final String QUESTION = "question";
    private QuestionWithUser question;
    private LiveData<QuestionWithUser> liveData;
    private View sendBtn;
    private View acceptBtn;
    private EditText chatMessageEt;

    Integer closeChatItemId = null;

    public ChatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            question = (QuestionWithUser) getArguments().getSerializable(QUESTION);
            liveData = App.instance().getRepository().mQDao.getLiveQuestion(question.id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        Context context = view.getContext();
        setHasOptionsMenu(true);

        RecyclerView recyclerView = view.findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ChatAdapter(this, question.id));

        chatMessageEt = view.findViewById(R.id.et_chat_message);
        chatMessageEt.setOnEditorActionListener((v, actionId, event) -> {
            if (event==null) {
                if (actionId != EditorInfo.IME_ACTION_SEND) {
                    return false;
                }
            }
            else if (actionId==EditorInfo.IME_NULL) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                }
            }
            else return false;
            send();
            return true;
        });
        sendBtn = view.findViewById(R.id.iv_chat_send);
        acceptBtn = view.findViewById(R.id.iv_chat_accept);
        sendBtn.setOnClickListener(v -> send());
        acceptBtn.setOnClickListener(v -> accept());

        liveData.observe(getViewLifecycleOwner(), q -> {
            this.question = q;
            onQuestionChanged();
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d(App.TAG, "onCreateOptionsMenu: " + (question==null?0:question.id));
        //menu.clear();
        if (question.uid == 0) {
            closeChatItemId = menu.add("Close Chat").getItemId();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (question.uid != 0) return false;
        if (item.getItemId() == closeChatItemId) {
            App.instance().getRepository().getRequestHandler().post(
                new CloseQRequest(question.globalId)
            );
        }
        return true;
    }

    private void onQuestionChanged() {
        if (App.instance().getUser().su < User.SU_USER && question.mentorId == null && question.status == Question.STATUS_OPEN) {
            acceptBtn.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.GONE);
            chatMessageEt.setText("");
            chatMessageEt.setHint(R.string.hint_accept_chat);
            chatMessageEt.setInputType(EditorInfo.TYPE_NULL);
        } else {
            acceptBtn.setVisibility(View.GONE);
            sendBtn.setVisibility(View.VISIBLE);
            if (question.status == Question.STATUS_CLOSED) {
                chatMessageEt.setText("");
                chatMessageEt.setHint(R.string.hint_chat_closed);
                chatMessageEt.setInputType(EditorInfo.TYPE_NULL);
            } else if ((question.mentorId == null || question.mentorId != 0) && question.uid != 0) {
                chatMessageEt.setText("");
                chatMessageEt.setHint(R.string.hint_not_your_chat);
                chatMessageEt.setInputType(EditorInfo.TYPE_NULL);
            } else {
                chatMessageEt.setHint(R.string.hint_send_message);
                chatMessageEt.setInputType(EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE);
            }
        }
    }

    private void send() {
        if (question.status == Question.STATUS_CLOSED) return;
        if ((question.mentorId == null || question.mentorId != 0) && question.uid != 0) return;

        String text = chatMessageEt.getText().toString();
        if (text.isEmpty()) return;

        Chat chat = new Chat(question.id, text);
        App.instance().getRepository().insertChat(chat);
        chatMessageEt.setText("");
    }

    private  void accept() {
        if (App.instance().getUser().su == User.SU_USER) return;
        if (question.mentorId != null) return;
        if (question.status == Question.STATUS_CLOSED) return;
        App.instance().getRepository().getRequestHandler().post(
                new AcceptQRequest(question.globalId)
        );
    }

}