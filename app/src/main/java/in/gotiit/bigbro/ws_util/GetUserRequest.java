package in.gotiit.bigbro.ws_util;

import org.json.JSONException;
import org.json.JSONObject;

import in.gotiit.bigbro.db.Contact;

public class GetUserRequest implements Request{

    Contact user;

    public GetUserRequest(Contact contact) {
        user = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GetUserRequest)) return false;
        GetUserRequest that = (GetUserRequest) o;
        return user.id == that.user.id;
    }

    @Override
    public int hashCode() {
        return user.id;
    }

    @Override
    public String requestString() {
        try {
            JSONObject body = new JSONObject()
                    .put("uid",user.id);
            return Request.wrapBody("get_u", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
