package in.gotiit.bigbro.ws_util;

import org.json.JSONException;
import org.json.JSONObject;


import in.gotiit.bigbro.util.App;


public interface Request extends Runnable {

    default void run() {
        App.instance().getWebSocket().send(requestString());
        int timeOut = getTimeOut();
        App.instance().getRepository().getRequestHandler().postDelayed(this, timeOut);

    }

    default void runOnce() {
        App.instance().getWebSocket().send(requestString());
    }

    default int getTimeOut(){
        return 10000;
    }

    String requestString();

    static String wrapBody(String req, JSONObject body) throws JSONException {
        return new JSONObject()
                .put("req", req)
                .put("body", body)
                .put("uid", App.instance().getUser().uid)
                .toString();
    }

}
