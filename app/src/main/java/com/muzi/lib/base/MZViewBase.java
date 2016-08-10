package com.muzi.lib.base;

import android.view.View;

import com.android.volley.Request;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * <Pre>
 * view接口
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/4/8 下午4:56
 */
public interface MZViewBase {

    //http返回未知错误
    int HTTP_UNKNOWN_FAILED = 0x0051;
    //授权失败
    int HTTP_CONNECT_AUTHFAILURE_ERROR = 0x0060;
    //网络错误
    int HTTP_CONNECT_NETWORK_ERROR = 0x0061;
    //无连接
    int HTTP_CONNECT_NOCONNECTION_ERROR = 0x0062;
    //数据转换失败
    int HTTP_CONNECT_PARSE_ERROR = 0x0063;
    //服务器错误
    int HTTP_CONNECT_SERVER_ERROR = 0x0064;
    //连接超时
    int HTTP_CONNECT_TIMEOUT_ERROR = 0x0065;
    //业务逻辑错误
    int HTTP_VALIDATE_ERROR = 0x0066;

    /**
     * 设置子Activity 或 Fragment的布局
     * @param resId
     */
    void setXTContentView(int resId);

    /**
     * 设置子Activity 或 Fragment的布局
     * @param view
     */
    void setXTContentView(View view);

    /**
     * 请求网络数据
     * @param request
     */
    void performRequest(Request<?>... request);

    /**
     * 显示进度框
     */
    void showProgressDialog();

    /**
     * 显示进度框
     * @param message
     */
    void showProgressDialog(String message);

    /**
     * 显示进度框
     * @param resId
     */
    void showProgressDialog(int resId);

    /**
     * 显示进度框
     * @param message
     * @param cancelable
     */
    void showProgressDialog(String message, boolean cancelable);

    /**
     * 显示进度对话框
     * @param message
     * @param cancelable
     * @param cancelClick
     */
    void showProgressDialog(String message, boolean cancelable, SweetAlertDialog.OnSweetClickListener cancelClick);

    /**
     * 移除进度框
     */
    void removeProgressDialog();

    /**
     * 警告框，只显示标题
     * @param title
     */
    void showAlertDialog(String title);

    /**
     * 默认警告框，只显示标题与内容
     * @param title                     标题
     * @param content                   内容
     */
    void showAlertDialog(String title, String content);

    /**
     * 警告框，指定显示样式
     * @param title
     * @param content
     * @param type
     */
    void showAlertDialog(String title, String content, int type);


    void showAlertDialog(String title, String message, String confirmText, SweetAlertDialog.OnSweetClickListener confirmClick);

    /**
     * 显示确任的对话框,确认事件绑定
     *
     * @param title                         标题
     * @param message                       内容
     * @param confirmText                   确认文件
     * @param confirmClick                  确认事件
     */
    void showPromptDialog(String title, String message, String confirmText, SweetAlertDialog.OnSweetClickListener confirmClick);

    /**
     * 显示确认，取消的对话框，确认，取消事件绑定
     * @param title
     * @param message
     * @param confirmText
     * @param cancelText
     * @param cancelClick
     * @param confirmClick
     */
    void showPromptDialog(String title, String message, String confirmText, String cancelText, SweetAlertDialog.OnSweetClickListener cancelClick, SweetAlertDialog.OnSweetClickListener confirmClick);
}
