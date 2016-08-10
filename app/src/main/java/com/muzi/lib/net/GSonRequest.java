package com.muzi.lib.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.muzi.lib.exception.UnknowException;
import com.muzi.lib.exception.ValidateException;

/**
 * <Pre>
 * 封装了使用Google GSon库解析数据的请求对象
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/4/21 下午8:47
 */
public class GSonRequest<T> extends Request<T> {

    private final Response.Listener<T> mListener;

    private BaseResponseWrapper mResponseWrapper;
    private Type mType;

    private String url;

    /**
     * 指定http动词的请求
     *
     * @param method        http 动词
     * @param url           请求url地址
     * @param type          响应数据类型，没有返回值这里使用EmptyEntity.class
     * @param listener      成功响应监听方法， 没有返回值这里使用EmptyEntity.class
     * @param errorListener 错误响应监听方法
     */
    public GSonRequest(int method, String url, Type type, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mType = type;
        mResponseWrapper = new BaseResponseWrapper();
        mListener = listener;
        this.url = url;
    }

    public GSonRequest(int method, String url, Type type, Callback<T> callback) {
        super(method, url, callback);
        mType = type;
        mResponseWrapper = new BaseResponseWrapper();
        mListener = callback;
        this.url = url;
    }

    /**
     * 默认请求是get的
     *
     * @param url           请求url地址
     * @param type          响应数据类型
     * @param listener      成功响应监听方法
     * @param errorListener 错误响应监听方法
     */
    public GSonRequest(String url, Type type, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.GET, url, type, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json");               //增加默认的请求头，设置请求数据是json类型
        return header;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {

            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            Log.d("NetworkResponse", "request:" + url +
                    "\nstatusCode: " + response.statusCode +
                    "\nnotModified:" + response.notModified +
                    "\nnetworkTimeMs:" + response.networkTimeMs +
                    "\nheaders:" + response.headers +
                    "\ndata:" + jsonString);

            T result = mResponseWrapper.parse(jsonString, mType);


            //成功处理
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {

            return Response.error(new ParseError(e));

        } catch (JsonSyntaxException e) {

            return Response.error(new ParseError(e));

        } catch (VolleyError volleyError) {
            //验证错误，4位错误码
            if (volleyError instanceof ValidateException) {
                //业务验证错误
                return Response.error(new ValidateException(mResponseWrapper.getCode(), mResponseWrapper.getMsg()));

            } else if (volleyError instanceof ServerError) {
                //服务器异常
                return Response.error(new ServerError(response));
            }
        }

        return Response.error(new UnknowException());
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    public interface Callback<T> extends Response.Listener<T>, Response.ErrorListener {
    }
}
