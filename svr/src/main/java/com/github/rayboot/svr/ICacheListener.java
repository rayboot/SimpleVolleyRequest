package com.github.rayboot.svr;


/**
 * author: rayboot  Created on 15/10/8.
 * email : sy0725work@gmail.com
 */
public interface ICacheListener<T> {
    void onCacheResponse(T response);
}
