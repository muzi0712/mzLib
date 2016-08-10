package com.muzi.lib.net;

import com.android.volley.VolleyError;

import java.lang.reflect.Type;

/**
 * <Pre>
 * 响应参数包装器
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/4/20 上午11:09
 */
public interface ResponseWrapper {

    /**
     * 解析返回的json数据,转换封装类型
     * 适用于List,Map等集合类型的转换
     *
     * @param response                  json串
     * @param x                         转换的实体类型Token
     * @param <T>
     * @return
     */
    public <T> T parse(String response, Type x) throws VolleyError;


    /**
     * 解析返回的json数据,转换封装类型
     * 适用于单个封装实体类型
     *
     * @param response                  json串
     * @param classOfT                  转换的class对象
     * @param <T>
     * @return
     */
    public <T> T parse(String response, Class<T> classOfT) throws VolleyError;


}
