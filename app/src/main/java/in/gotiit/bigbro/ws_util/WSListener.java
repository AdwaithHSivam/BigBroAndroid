package in.gotiit.bigbro.ws_util;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import in.gotiit.bigbro.util.App;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WSListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    public WSListener() {}

    @Override
    public void onOpen(WebSocket webSocket, @NotNull Response response) {
        webSocket.send("");
        App.instance().getRepository().onWebSocketOpen();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        output("Receiving : " + text);
        try {
            in.gotiit.bigbro.ws_util.Response.parse(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, ByteString bytes) {
        output("Receiving bytes : " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, @NotNull String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        App.instance().getRepository().onWebSocketClose();
        output("Closing : " + code + " / " + reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        if(response != null && response.code() == 401) {
            Log.d(App.TAG, "Auth failure");//TODO: handle this
        }
        App.instance().getRepository().onWebSocketClose();
        output("Error : " + t.getMessage());
    }

    private void output(String s) {
        Log.d(App.TAG, "output: " + s);
    }
}
