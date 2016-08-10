package com.muzi.lib.models;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/7/16 下午6:37
 */
public class BaseModel {


    /**
     * 获取接口方法名称
     * @param url
     */
    public String getMethodName(String url) {
        String name = new String();
        name = url.substring(url.indexOf("=")+1,url.length());

        return name;
    }

    /**
     * 获取请求URL地址
     * @param url
     * @return
     */
    public String getUrl(String url){

        String name = url.split("\\?")[0];

        return name;
    }


}
