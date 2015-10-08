package com.github.rayboot.svr;

import com.android.volley.VolleyError;

/**
 * Callback interface for delivering errorView responses.
 * author: rayboot  Created on 15/10/8.
 * email : sy0725work@gmail.com
 */
public interface IFinishListener<T> {
    /**
     * Callback method that an errorView has been occurred with the
     * provided errorView code and optional user-readable message.
     */
    void onFinishResponse(boolean success, T response, VolleyError error);
}