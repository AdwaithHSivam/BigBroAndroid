package in.gotiit.bigbro.util;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class User {
    private static final String
            KEY_UID = "uid",
            KEY_SU = "su",
            KEY_USERNAME = "username",
            KEY_PASSWORD = "password",
            KEY_JWT = "jwt",
            KEY_NAME = "full_name",
            KEY_EMAIL = "email",
            KEY_MOBILE = "mobile",
            KEY_FIRST_TIME = "first";

    public static final int SU_ADMIN = 0;
    public static final int SU_MENTOR = 1;
    public static final int SU_USER = 2;

    private static final String VALIDATE_URL = App.BACKEND_URL+"/validate/";

    public int uid;
    public int su;
    public String userName;
    public String password;
    public String jwToken;
    public String fullName;
    public String email;
    public String mobile;

    private User(){

    }

    static User userFromJson(String json, String userName, String password) {
        User user = new User();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject body = jsonObj.getJSONObject("body");
            user.uid = body.getInt(KEY_UID);
            user.su = body.getInt(KEY_SU);
            user.userName = userName;
            user.password = password;
            user.jwToken = body.getString(KEY_JWT);
            user.fullName = body.getString(KEY_NAME);
            user.email = body.getString(KEY_EMAIL);
            user.mobile = body.getString(KEY_MOBILE);
        } catch (JSONException e) {
            return null;
        }
        return user;
    }

    public static User loadPref() {
        SharedPreferences prefs = App.instance().getPrefs();
        int uid = prefs.getInt(KEY_UID, -1);
        if(uid == -1) {
            return null;
        }
        User user = new User();
        user.uid = uid;
        user.su = prefs.getInt(KEY_SU, 1);
        user.userName = prefs.getString(KEY_USERNAME, "");
        user.password = prefs.getString(KEY_PASSWORD, "");
        user.jwToken = prefs.getString(KEY_JWT, "");
        user.fullName = prefs.getString(KEY_NAME, "");
        user.email = prefs.getString(KEY_EMAIL, "");
        user.mobile = prefs.getString(KEY_MOBILE, "");
        return user;
    }



    public static void validate(String username, String password, final MyCallback callback) {
        OkHttpClient client = App.instance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add(KEY_USERNAME, username)
                .add(KEY_PASSWORD, password)
                .build();
        Request request = new Request.Builder()
                .url(VALIDATE_URL)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                App.instance().getHandler().post(() -> callback.OnFailure("Couldn't sent Request"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ResponseBody body;
                if (response.isSuccessful() && (body = response.body()) != null) {
                    String res = body.string();
                    User user = User.userFromJson(res, username, password);
                    if (user != null) {
                        App.instance().getHandler().post(() -> {
                            App.user = user;
                            user.savePref();
                            firstTime();
                            callback.OnSuccess();
                        });
                        return;
                    }
                }
                App.instance().getHandler().post(() -> callback.OnFailure("Invalid Credentials"));

            }
        });
    }

    private static void firstTime() {
        SharedPreferences prefs = App.instance().getPrefs();
        int firstTime = prefs.getInt(KEY_FIRST_TIME, 0);
        if (firstTime == 0) {
            App.instance().getRepository().firstTime();
        }
    }

    public void savePref() {
        SharedPreferences prefs = App.instance().getPrefs();
        prefs.edit().putInt(KEY_UID, uid).apply();
        prefs.edit().putInt(KEY_SU, su).apply();
        prefs.edit().putString(KEY_USERNAME, userName).apply();
        prefs.edit().putString(KEY_PASSWORD, password).apply();
        prefs.edit().putString(KEY_JWT, jwToken).apply();
        prefs.edit().putString(KEY_NAME, fullName).apply();
        prefs.edit().putString(KEY_EMAIL, email).apply();
        prefs.edit().putString(KEY_MOBILE, mobile).apply();
    }

    public void clearPref() {
        SharedPreferences prefs = App.instance().getPrefs();
        prefs.edit().remove(KEY_UID).apply();
        prefs.edit().remove(KEY_SU).apply();
        prefs.edit().remove(KEY_USERNAME).apply();
        prefs.edit().remove(KEY_PASSWORD).apply();
        prefs.edit().remove(KEY_JWT).apply();
        prefs.edit().remove(KEY_NAME).apply();
        prefs.edit().remove(KEY_EMAIL).apply();
        prefs.edit().remove(KEY_MOBILE).apply();
    }
}
