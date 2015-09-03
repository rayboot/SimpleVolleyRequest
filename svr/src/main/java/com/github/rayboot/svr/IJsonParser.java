package com.github.rayboot.svr;

/**
 * Created by rayboot on 15/9/3.
 */
public interface IJsonParser {
    <T> T parseJson(String jsonString, Class<T> mClazz);
}
