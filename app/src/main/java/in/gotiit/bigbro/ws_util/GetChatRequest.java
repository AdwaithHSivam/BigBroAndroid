package in.gotiit.bigbro.ws_util;

import org.json.JSONException;
import org.json.JSONObject;

public class GetChatRequest implements  Request{

    int qid;

    public GetChatRequest(int qid) {//global qid
        this.qid = qid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GetChatRequest)) return false;
        GetChatRequest that = (GetChatRequest) o;
        return qid == that.qid;
    }

    @Override
    public int hashCode() {
        return qid;
    }

    @Override
    public void run() {
        runOnce();
    }

    @Override
    public String requestString() {
        try {
            JSONObject body = new JSONObject()
                    .put("qid",qid);
            return Request.wrapBody("get_c", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
