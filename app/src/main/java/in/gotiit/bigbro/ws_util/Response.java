package in.gotiit.bigbro.ws_util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import in.gotiit.bigbro.db.Chat;
import in.gotiit.bigbro.db.Contact;
import in.gotiit.bigbro.db.Question;
import in.gotiit.bigbro.db.RoomDb;
import in.gotiit.bigbro.util.App;
import in.gotiit.bigbro.util.Repository;

public interface Response {

    static void parse(String res) throws JSONException {
        JSONObject obj = new JSONObject(res);
        String req = obj.getString("req");
        JSONObject body = obj.getJSONObject("body");
        switch (req) {
            case "get_q":
                parseNewQ(body);
                break;
            case "get_c":
                parseNewChat(body);
                break;
            case "get_u":
                parseNewContact(body);
                break;
            case "upd_q":
                parseQUpdates(body);
        }
    }

    static void parseQUpdates(JSONObject body) throws JSONException {
        Repository repo = App.instance().getRepository();
        JSONArray qidArray = body.getJSONArray("qid");
        List<Integer> qidList = new ArrayList<>(qidArray.length());
        for (int i = 0; i < qidArray.length(); i++) {
            qidList.add(qidArray.getInt(i));
        }
        RoomDb.databaseWriteExecutor.execute(() -> {
            List<Integer> list = repo.mQDao.getExistingQFrom(qidList);
            qidList.removeAll(list);
            repo.getRequestHandler().post(() -> {
                for (int qid:qidList) {
                    new GetQRequest(qid).runOnce();
                }
            });
        });
    }

    static void parseNewQ(JSONObject body) throws JSONException {
        Repository repo = App.instance().getRepository();
        int uid = body.getInt("uid");
        Integer mid = body.isNull("mid") ? null : body.getInt("mid");
        if (uid == App.instance().getUser().uid) {
            uid = 0;
        } else {
            Contact contact = new Contact(uid, null);
            repo.insertContact(contact);
        }
        if (mid != null)
        if (mid == App.instance().getUser().uid) {
            mid = 0;
        } else {
            Contact contact = new Contact(mid, null);
            repo.insertContact(contact);
        }
        Question question = new Question(
                body.getInt("local_qid"),
                body.getInt("qid"),
                uid,
                body.getString("title"),
                body.getInt("status"),
                mid,
                body.getString("createdAt")
        );
        repo.updateQuestion(question);
    }

    static void parseNewChat(JSONObject body) throws JSONException {
        Repository repo = App.instance().getRepository();
        int uid = body.getInt("uid");
        if (uid == App.instance().getUser().uid) {
            uid = 0;
        } else {
            Contact contact = new Contact(uid, null);
            repo.insertContact(contact);
        }
        Chat chat = new Chat(
                body.getInt("local_cid"),
                body.getInt("cid"),
                body.getInt("qid"),
                uid,
                body.getString("text"),
                body.getString("createdAt")
        );
        repo.updateChat(chat);
    }

    static void parseNewContact(JSONObject body) throws JSONException {
        Repository repo = App.instance().getRepository();
        int id = body.getInt("uid");
        String username = body.getString("username");
        repo.updateContact(new Contact(id, username));
    }

}
