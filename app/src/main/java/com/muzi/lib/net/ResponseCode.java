package com.muzi.lib.net;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/7/23 下午2:51
 */
public class ResponseCode {

    //接口正常响应
    public static final String RESP_CODE_SUCCESS = "000";
    //业务响应错误码位数，只需要判断位数，显示相应消息
    public static final int RESP_CODE_BIZ_ERROR_COUNT = 4;
    //业务服务器错误码位数,同上
    public static final int RESP_CODE_SERVER_ERROR_COUNT = 5;

    //用户未登录
    public static final String RESP_CODE_ERROR_UNLOGIN = "10007";
}
