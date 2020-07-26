package in.gotiit.bigbro.ws_util;

import org.json.JSONException;
import org.json.JSONObject;

import in.gotiit.bigbro.db.Question;

public class AddQRequest implements Request {

    Question question;

    public AddQRequest(Question q) {
        question = q;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddQRequest)) return false;
        AddQRequest that = (AddQRequest) o;
        return question.id == that.question.id;
    }

    @Override
    public int hashCode() {
        return question.id;
    }

    @Override
    public String requestString() {
        try {
            JSONObject body = new JSONObject()
                    .put("local_qid",question.id)
                    .put("title", question.getTitle());
            return Request.wrapBody("add_q", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
