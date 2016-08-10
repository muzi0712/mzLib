package com.muzi.lib.net;

import java.lang.reflect.Type;

import android.text.TextUtils;

import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muzi.lib.exception.ValidateException;

/**
 * <Pre>
 * <p/>
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/4/20 上午11:13
 */
public class BaseResponseWrapper implements ResponseWrapper {

    //响应码
    private String code;

    //响应消息
    private String msg;

    //签名
    private String sign;

    //响应时间
    private String resptime;

    //token
    private String token;

    //point
    private String point;

    //响应数据
    private String data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getResptime() {
        return resptime;
    }

    public void setResptime(String resptime) {
        this.resptime = resptime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public <T> T parse(String response, Class<T> classOfT) throws VolleyError {
        return parse(response, (Type) classOfT);
    }

    @Override
    public <T> T parse(String response, Type x) throws VolleyError {
        Gson gson = new Gson();
        TypeToken<BaseResponseWrapper> typeToken = new TypeToken<BaseResponseWrapper>() {
        };
        BaseResponseWrapper responseWrapper = gson.fromJson(response, typeToken.getType());
        if (responseWrapper != null) {

            setCode(responseWrapper.getCode());
            setMsg(responseWrapper.getMsg());
            setData(responseWrapper.getData());
            setSign(responseWrapper.getSign());
            setResptime(responseWrapper.getResptime());
            setToken(responseWrapper.getToken());
            setPoint(responseWrapper.getPoint());

            //先解析头信息，code , sign
            if (ResponseCode.RESP_CODE_SUCCESS.equals(getCode())) {
                //成功响应,去解析数据体
                return parseBody(gson, responseWrapper.getData(), x);

            }else if(getCode().length() == ResponseCode.RESP_CODE_BIZ_ERROR_COUNT){
                //4位验证码，业务验证错误
                throw new ValidateException(getCode(), getMsg());
            }else if(getCode().length() == ResponseCode.RESP_CODE_SERVER_ERROR_COUNT){
                //5位验证码，系统级错误
                throw new ServerError();
            }

        }
        return null;

    }


    /**
     * 解析响应数据体
     *
     * @param gson
     * @param data
     * @param x
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T parseBody(Gson gson, String data, Type x) {

        if (!TextUtils.isEmpty(data) &&
                !data.equals("\"\"") &&
                !data.equals("{}") &&
                !data.equals("[]")) {

            return gson.fromJson(data, x);
        } else {

            if (x instanceof Class) {
                if (((Class) x).getName().equals(EmptyEntity.class.getName())) {
                    //如果type是空实体，则转换成EmptyEntity
                    EmptyEntity emptyEntity = new EmptyEntity();
                    return (T) emptyEntity;
                }
            }
        }
        return null;
    }

    public static class EmptyEntity {

    }


}
