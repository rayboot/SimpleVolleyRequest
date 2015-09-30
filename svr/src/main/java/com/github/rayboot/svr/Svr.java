package com.github.rayboot.svr;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.github.rayboot.svr.stateview.StateView;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * adb shell setprop log.tag.Svr VERBOSE
 *
 * @author rayboot
 */
public class Svr<T> {
    int mMethod = Request.Method.POST;
    String mUrl;
    String mTag;
    Class<T> mClazz;
    View mClickView = null;
    Map<String, String> mParams = new HashMap<String, String>();
    VolleyRequest.FinishListener<T> mFinishListener;
    Map<String, String> mHeaders;
    StateView mErrorView = null;
    final int WIFI_TIMEOUT_TIME = 15 * 1000;
    final int MOBILE_TIMEOUT_TIME = 60 * 1000;
    int mCustomTimeOut = -1;
    boolean shouldCache = true;
    boolean returnCache = false;

    public static <T> Svr<T> builder(Context context, Class<T> classOfT) {
        return new Svr<T>().with(context).gsonClass(classOfT);
    }

    public Svr<T> method(int method) {
        this.mMethod = method;
        return this;
    }

    public Svr<T> timeout(int timeout) {
        this.mCustomTimeOut = timeout;
        return this;
    }

    public Svr<T> url(String url) {
        this.mUrl = url;
        return this;
    }

    private Svr<T> gsonClass(Class<T> mClazz) {
        this.mClazz = mClazz;
        return this;
    }


    public Svr<T> shouldCache(boolean shouldCache) {
        this.shouldCache = shouldCache;
        return this;
    }


    public Svr<T> returnCache(boolean returnCache) {
        this.returnCache = returnCache;
        return this;
    }

    public Svr<T> finishListener(VolleyRequest.FinishListener<T> listener) {
        this.mFinishListener = listener;
        return this;
    }

    public Svr<T> clickView(View clickView) {
        this.mClickView = clickView;
        return this;
    }

    public Svr<T> requestParams(Map<String, String> params) {
        this.mParams = params;
        return this;
    }

    public Svr<T> setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
        return this;
    }

    public Svr<T> stateView(StateView view) {
        this.mErrorView = view;
        return this;
    }

    public Svr<T> tag(String tag) {
        this.mTag = tag;
        return this;
    }

    private Svr<T> with(Context context) {
        if (context instanceof INetWork) {
            mTag = ((INetWork) context).getVolleyTag();
        }
        return this;
    }

    public void post2Queue() {
        post2Queue(mTag);
    }

    public void post2Queue(String tag) {
        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("url must not be null.");
        }
        if (mClazz == null) {
            throw new IllegalArgumentException("class must not be null.");
        }
        if (tag == null) {
            throw new IllegalArgumentException(
                    "tag must not be null.you can use .tag() or .with(INetWork context) to add tag");
        }


        VolleyRequest<T> gsonRequest = new VolleyRequest<T>(this.mMethod, this.mUrl,
                this.mClazz, mParams, mHeaders, mCustomTimeOut > 0 ? mCustomTimeOut : NetworkUtil.isWifiConnected(SvrVolley.getMainContext()) ? WIFI_TIMEOUT_TIME : MOBILE_TIMEOUT_TIME);

        gsonRequest.oneClickView(mClickView);
        gsonRequest.finishListener(mFinishListener);
        gsonRequest.errorView(mErrorView);
        gsonRequest.setShouldCache(shouldCache);

        if (VolleyLog.DEBUG) {
            VolleyLog.v("volley req url =  %s", NetworkUtil.getFullUrl(mUrl, mParams));
        }

        SvrVolley.getInstance().addToRequestQueue(gsonRequest, tag);
        if (returnCache && mFinishListener != null) {
            checkCache();
        }
    }

    private void checkCache() {
        Cache.Entry entry = SvrVolley.getInstance().getRequestQueue().getCache().get(NetworkUtil.getFullUrl(mUrl, mParams));
        if (entry != null) {
            try {
                mFinishListener.onCacheResult(SvrVolley.getInstance().getJsonParser().parseJson(new String(entry.data, "UTF-8"), mClazz));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
