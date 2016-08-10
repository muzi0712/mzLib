package com.muzi.lib.exception;

import com.android.volley.VolleyError;

/**
 * <Pre>
 * 验证异常
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/6/15 下午2:46
 */
public class ValidateException extends VolleyError{

    private String code;
    private String msg;

    public ValidateException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

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
}
