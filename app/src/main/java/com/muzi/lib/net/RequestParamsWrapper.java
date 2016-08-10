package com.muzi.lib.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;

/**
 * <Pre>
 * 请求参数封装
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/7/16 下午2:38
 */
public class RequestParamsWrapper<T> {

    public final static String tf = "yyMMddHHmmssSSS";

    protected String method;           //接口类名

    public RequestParamsWrapper(Context ctx, T recdata) {

    }

    public RequestParamsWrapper(Context ctx) {
        this(ctx, null);

    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public JSONObject toJSONObject() {

        Gson gson = null;
        JSONObject jobj = null;

        try {
            gson = new Gson();
            jobj = new JSONObject(gson.toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    /**
     * 组装Post请求参数,包含请求头及数据体
     *
     * @param method method
     * @return
     */
    public Map<String, String> getRequestParams(String method) {

        setMethod(method);
        Map<String, String> params = new HashMap<String, String>();
//        params.put("REQTIME", getReqtime());
//        params.put("SIGN", getSign());
//        params.put("VERSION", getVersion());
//        params.put("TOKEN", getToken());
//        params.put("COMMUNITYID", getCommunityid());
//        params.put("METHOD", getMethod());
//        params.put("CTYPE", getCtype());
        JSONObject recdata = toJSONObject();
        try {
            params.put("RECDATA", recdata.getString("recdata"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }


    public static String getRequestTime() {

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();

        SimpleDateFormat format = new SimpleDateFormat(tf);

        return format.format(date);
    }
}
