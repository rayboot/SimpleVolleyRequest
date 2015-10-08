/**
 * Copyright 2013 Ognyan Bankov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.rayboot.svr;

import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.github.rayboot.svr.stateview.ErrorViewContent;
import com.github.rayboot.svr.stateview.HttpStatusCodes;
import com.github.rayboot.svr.stateview.StateView;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author rayboot
 */
public class VolleyRequest<T> extends Request<T> {
    protected Class<T> mClazz;
    protected IFinishListener<T> finishListener;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    View clickView = null;
    StateView errorView = null;
    private int curTimeout = 15 * 1000;

    /**
     * 带有超时时间的request
     *
     * @param method
     * @param url
     * @param clazz
     * @param params
     * @param timeout       设置超时时间
     */
    public VolleyRequest(int method, String url, Class<T> clazz,
                         Map<String, String> params, Map<String, String> headers, int timeout) {
        super(method, url, null);
        mClazz = clazz;
        this.params = params == null ? new HashMap<String, String>() : params;
        this.curTimeout = timeout;
        this.headers = headers;
        setRetryPolicy(getRetryPolicy());
    }

    public VolleyRequest<T> oneClickView(View clickView) {
        this.clickView = clickView;
        if (clickView != null) {
            clickView.setEnabled(false);
            clickView.setClickable(false);
        }
        return this;
    }

    public VolleyRequest<T> finishListener(IFinishListener<T> finishListener) {
        this.finishListener = finishListener;
        return this;
    }

    public VolleyRequest<T> errorView(StateView errorView) {
        this.errorView = errorView;
        return this;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(curTimeout, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null || headers.size() == 0) {
            return SvrVolley.getInstance().getCommonHeaders();
        } else {
            headers.putAll(SvrVolley.getInstance().getCommonHeaders());
            return headers;
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        int errorRes = R.string.state_error_network;
        if (error instanceof NoConnectionError) {
            errorRes = R.string.state_error_no_connect;
        } else if (error instanceof TimeoutError) {
            errorRes = R.string.state_error_timeout;
        } else if (error instanceof ServerError) {
            errorRes = R.string.state_error_server_error;
        } else if (error instanceof ParseError) {
            errorRes = R.string.state_error_parse_error;
        }

        if (clickView != null) {
            this.clickView.setClickable(true);
            this.clickView.setEnabled(true);
        }
        if (errorView != null) {
            if (error.networkResponse == null) {
                errorView.setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_CONNECT));
            } else if (error.networkResponse.statusCode > 0) {
                errorView.setState(ErrorViewContent.getContentObj(error.networkResponse.statusCode));
            } else {
                errorView.setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_CONNECT));
            }
        }
        if (finishListener != null) {
            finishListener.onFinishResponse(false, null, error);
        } else {
            Toast.makeText(SvrVolley.getMainContext(), errorRes, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (clickView != null) {
            this.clickView.setClickable(true);
            this.clickView.setEnabled(true);
        }
        if (errorView != null) {
            errorView.setState(ErrorViewContent.getContentObj(HttpStatusCodes.FINISH));
        }

        if (finishListener != null) {
            finishListener.onFinishResponse(true, response, null);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json =
                    NetworkUtil.isGzipSupport(response) ? GzipUtil.decompress(
                            response.data) : new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
            VolleyLog.v("Response Class name = %s  json = %s", mClazz.getSimpleName(), json);
            return Response.success(SvrVolley.getInstance().getJsonParser().parseJson(json, mClazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getCacheKey() {
        return NetworkUtil.getFullUrl(this.getUrl(), params);
    }
}