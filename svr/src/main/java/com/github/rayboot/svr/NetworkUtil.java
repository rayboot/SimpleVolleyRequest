package com.github.rayboot.svr;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.NetworkResponse;

import java.util.Map;

/**
 * @author rayboot
 */
public class NetworkUtil {
    /**
     * 判断返回数据是否经过 gzip 压缩
     *
     * @param req
     * @return boolean 值
     */
    public static boolean isGzipSupport(NetworkResponse req) {
        for (Map.Entry<String, String> entry : req.headers.entrySet()) {
            if (entry.getKey().equals("Data-Type")) {
                return entry.getValue().equals("gzip");
            }
        }
        return false;
    }


    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
