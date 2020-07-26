package in.gotiit.bigbro.ws_util;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import in.gotiit.bigbro.util.App;

public class RequestQueue {

    static final int TIMEOUT = 10000;
    private final List<List<Request>> requests = new ArrayList<>(3);
    private Handler mHandler = new Handler();//TODO move this to a different thread
    @SuppressWarnings("rawtypes")
    private List<ObserverPair> observerList = new ArrayList<>();

    public<T> void initRequest(Function<T, Request> requestNew, LiveData<List<T>> liveData) {
        int index = requests.size();
        ObserverPair<T> observerPair = new ObserverPair<>(liveData, tList -> {
            List<Request> list = new ArrayList<>();
            for (T t:tList) {
                list.add(requestNew.apply(t));
            }
            onListChanged(index, list);
        });
        requests.add(new ArrayList<>());
        observerList.add(observerPair);
    }

    @SuppressWarnings("rawtypes")
    public void onWebSocketOpen() {
        App.instance().getHandler().post(() -> {
            Log.d(App.TAG, "onWebSocketOpen: ");
            for (ObserverPair observerPair: observerList) {
                observerPair.onWebSocketOpen();
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public void onWebSocketClose() {
        App.instance().getHandler().post(() -> {
            removeAllRequests();
            for (ObserverPair observerPair: observerList) {
                observerPair.onWebSocketClose();
            }
        });
    }

    private void onListChanged(int index, List<Request> list) {
        List<Request> queue = requests.get(index);
        if (queue == null) return;
        for (Request request: list) {
            if (!queue.contains(request)) {
                queue.add(request);
                mHandler.post(request);
            }
        }
        for (int i = queue.size()-1; i >= 0; i--) {
            Request request = queue.get(i);
            if (!list.contains(request)) {
                queue.remove(i);
                mHandler.removeCallbacks(request);
            }
        }
    }

    public void removeAllRequests() {
        for (List<Request> queue : requests) {
            for (Request request : queue) {
                mHandler.removeCallbacks(request);
            }
            queue.clear();
        }
    }

    public Handler getHandler() {
        return mHandler;
    }


    private static class ObserverPair<T> {
        LiveData<List<T>> liveData;
        Observer<List<T>> observer;

        public ObserverPair(LiveData<List<T>> liveData, Observer<List<T>> observer) {
            this.liveData = liveData;
            this.observer = observer;
        }

        public void onWebSocketOpen() {
            liveData.observeForever(observer);
        }

        public void onWebSocketClose() {
            liveData.removeObserver(observer);
        }
    }

}
