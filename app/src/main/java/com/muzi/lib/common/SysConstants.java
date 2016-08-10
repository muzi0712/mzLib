package com.muzi.lib.common;

import java.io.File;

/**
 * <Pre>
 * 系统常量
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/6/15 下午4:35
 */
public class SysConstants {

    /*
     * app存储目录
     */
    public static final String PARAM_APP_CATALOG = "muzilib";                                                //应用存储根目录
    public static final String APP_CACHE_CATALOG = PARAM_APP_CATALOG + File.separator + "cache";            //应用缓存存储目录
    public static final String APP_THUMBNAIL_CATALOG = PARAM_APP_CATALOG + File.separator + "thumbnail";    //应用图片缩略图存储目录
    public static final String APP_TMP_CATALOG = PARAM_APP_CATALOG + File.separator + "tmp";                //应用临时文件目录
    public static final String APP_LOG = PARAM_APP_CATALOG + File.separator + "log";                        //日志文件目录
    public static final String APP_SOUND = PARAM_APP_CATALOG + File.separator + "sound";                    //音频文件目录
    public static final String APP_VIDEO = PARAM_APP_CATALOG + File.separator + "video";                    //视频文件目录
    public static final String APP_IMAGE = PARAM_APP_CATALOG + File.separator + "image";                    //图片文件目录

    public static final String DCMI_CAMERA_PATH = "/Camera/";                                               //android 系统拍照录像目录

    /*
     * 文件后缀相关常量
     */
    public static final String SUFFIX_TMP_FILE = "tmp";                 //临时文件后缀
    public static final String SUFFIX_AUDIO_FILE = "arm";             //声音文件后缀
    public static final String SUFFIX_VIDEO_FILE = "mp4";             //视频文件后缀
    public static final String SUFFIX_IMAGE_FILE = "png";               //图片文件后缀
    public static final String SUFFIX_IMAGE_THUMBNAIL = "thumbnail";    //图片缩略图后缀


    /**
     * 文件类型
     */
    public enum FileType {
        FILE_TYPE_AUDIO,                //音频文件
        FILE_TYPE_VIDEO,                //视频文件
        FILE_TYPE_IMAGE,                //图片文件
        FILE_TYPE_TMP,                  //临时文件
        FILE_TYPE_THUMBNAIL,             //图片缩略图
        FILE_TYPE_HEAD                  //头像图片

    }

    /**
     * 模块代码
     */
    public static final class moduleCode {

        public static final String HOME = "home";                         //首页
        public static final String CIRCLE = "circle";                     //圈子
        public static final String MINE = "mine";                         //我的
        public static final String SETTING = "setting";                   //系统设置
        public static final String VOTE = "vote";                         //投票
        public static final String COMPLAINT = "complaintmodule";               //投诉
        public static final String NOTICE = "noticemodule";                     //社区通知
        public static final String REPAIR = "repairmodule";                     //报修
        public static final String CONVENIENCE = "conveniencemodule";           //便民服务
        public static final String LOTTERY = "lotterymodule";                   //积分抽奖
        public static final String PROPERTYPAY = "propertypaymodule";           //在线缴费
        public static final String BUTLER = "butlermodule";                     //我的管家
        public static final String INTEGRALSHOP = "integralshopmodule";         //积分商城
        public static final String OTHER = "othermodule";                       //敬起期待
        public static final String OPEN_DOOR = "opendoormodule";                //手机开门
        public static final String SHOP = "shopmodule";                         //团购
        public static final String FINANCE = "financemodule";                         //金穗
        public static final String RENTAL = "rentalmodule";                         //租赁
        public static final String VISITOR = "visitorregmodule";               //访客
        public static final String EXPRESS="expressmodule";                            //快递代收
        public static final String Club = "clubmodule";                         //会所预定

    }
}
