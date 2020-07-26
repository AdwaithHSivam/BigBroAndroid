package in.gotiit.bigbro.ws_util;

import org.json.JSONException;
import org.json.JSONObject;

public class AcceptQRequest implements Request{

    int qid;

    public AcceptQRequest(int qid) {
        this.qid = qid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcceptQRequest)) return false;
        AcceptQRequest that = (AcceptQRequest) o;
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
            return Request.wrapBody("acc_q", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
