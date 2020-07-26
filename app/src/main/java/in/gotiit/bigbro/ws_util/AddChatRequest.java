package in.gotiit.bigbro.ws_util;

import org.json.JSONException;
import org.json.JSONObject;

import in.gotiit.bigbro.db.ChatWithQ;

public class AddChatRequest implements Request {

    ChatWithQ chat;

    public AddChatRequest(ChatWithQ chat) {
        this.chat = chat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddChatRequest)) return false;
        AddChatRequest that = (AddChatRequest) o;
        return chat.id ==  that.chat.id;
    }

    @Override
    public int hashCode() {
        return chat.id;
    }

    @Override
    public String requestString() {
        try {
            JSONObject body = new JSONObject()
                    .put("local_cid",chat.id)
                    .put("qid",chat.question.globalId)
                    .put("text", chat.getText());
            return Request.wrapBody("add_c", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
