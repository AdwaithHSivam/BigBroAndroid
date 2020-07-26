package in.gotiit.bigbro.cookie_util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SessionCookieJar implements CookieJar {

    private Set<IdentifiableCookie> cookies;

    public SessionCookieJar() {
        cookies = new HashSet<>();
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        List<Cookie> validCookies = new ArrayList<>();

        for (IdentifiableCookie cookie : cookies) {
            Cookie currentCookie = cookie.getCookie();

            if (currentCookie.matches(httpUrl)) {
                validCookies.add(currentCookie);
            }
        }

        return validCookies;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        for (IdentifiableCookie cookie : IdentifiableCookie.decorateAll(list)) {
            this.cookies.remove(cookie);
            this.cookies.add(cookie);
        }
    }

    synchronized public void clearSession() {
        cookies.clear();
    }

}