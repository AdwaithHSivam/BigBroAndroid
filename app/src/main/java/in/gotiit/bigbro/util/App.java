package in.gotiit.bigbro.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;


import in.gotiit.bigbro.cookie_util.SessionCookieJar;
import in.gotiit.bigbro.ws_util.WSListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class App extends Application {

    private static final String SHARED_PREFS_NAME = "prefs";
    public static final String BASE_URL = "bigbro-dev.ap-south-1.elasticbeanstalk.com";
    public static final String BACKEND_URL = "http://" + BASE_URL;
    private static final String WS_URL = "ws://" + BASE_URL;
    public static final String TAG = "abcde";

    OkHttpClient okHttpClient;
    public static User user = null;
    private static App instance;
    private SharedPreferences sharedPreferences;
    private Handler mHandler;
    private Repository repository;
    private volatile WebSocket webSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @NotNull
    public SharedPreferences getPrefs() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public User getUser() {
        if (user == null) {
            user = User.loadPref();
        }
        return user;
    }

    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    @NotNull
    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new SessionCookieJar())
                    .build();
        }
        return okHttpClient;
    }

    @NotNull
    public Repository getRepository() {
        if (repository == null) {
            repository = new Repository(this);
        }
        return repository;
    }

    @NotNull
    public WebSocket getWebSocket() {//TODO: this is not permanent remember
        if (webSocket == null || !webSocket.send("")) {
            Request request = new Request.Builder().url(WS_URL)
                    .addHeader("Sec-WebSocket-Protocol", String.format("[\"access_token\", \"%s\"]", getUser().jwToken)).build();
            webSocket = getOkHttpClient().newWebSocket(request, new WSListener());
        }
        return webSocket;
    }

    public static App instance() {
        return instance;
    }
}
