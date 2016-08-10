
package com.muzi.lib.utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.muzi.lib.common.SysConstants;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * <Pre>
 * 通用工具类
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          create by 15/6/15 下午1:45
 */
@SuppressWarnings("deprecation")
public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();
    //点分隔
    private static final String DOT = ".";

    /** 网络不可用 */
    public static final int NO_NETWORK = 0;
    /** 是wifi连接 */
    public static final int WIFI = 1;
    /** 不是wifi连接 */
    public static final int NO_WIFI = 2;

    /**
     * 获取AndroidManifest.xml 里的meta-data值
     */
    public static String getMetaDataValue(Context context,String key) {
        Bundle metaData = null;
        String value = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                value = metaData.getString(key);

            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return value;
    }

    /**
     * 获取当前网络连接类型
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {
        if (!isNetWorkAvailable(context)) {
            return CommonUtils.NO_NETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting())
            return CommonUtils.WIFI;
        else
            return CommonUtils.NO_WIFI;
    }

    /**
     * 判断当前网络状态是否可用
     * @param context
     * @return
     */
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否当前ctx是否在后台运行
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * @param context
     * @param activityName
     * @return
     * @Description 判断是否是顶部activity
     */
    public static boolean isTopActivy(Context context, String activityName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cName = am.getRunningTasks(1).size() > 0 ? am
                .getRunningTasks(1).get(0).topActivity : null;

        if (null == cName)
            return false;
        return cName.getClassName().equals(activityName);
    }

    /**
     * @return
     * @Description 判断存储卡是否存在
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    /**
     * 获取android拍照，录像目录
     *
     * @return
     */
    public static String getDCMIPath() {
        String path = "";
        //检测sd卡是否存在
        if (checkSDCard()) {
            File sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            path = sdPath.getAbsolutePath() + "/Camera/";
            File f = new File(path);
            boolean flag = false;
            if (!f.exists()) {
                flag = f.mkdirs();
            }

        }
        return path;
    }

    /**
     * 清理缓存
     */
    public static void clearCache(Context ctx) {
        File cacheDir = getAppCacheDir(ctx);
//        File sysCacheDir = ctx.getCacheDir();
        if (cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) {
            for (File item : cacheDir.listFiles()) {
                item.delete();
            }
        }

    }

    /**
     * 计算应用缓存大小
     *
     * @return
     */
    public static long calcCacheSize(Context ctx) {
        File cacheDir = getAppCacheDir(ctx);
//        File sysCacheDir = ctx.getCacheDir();
        long total = 0;
        if (cacheDir != null) {
            try {
                total = new ConcurrentTotalFileSizeWLatch()
                        .getTotalSizeOfFile(cacheDir.getAbsolutePath());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return total;
    }

    /**
     * 获取缓存文件
     *
     * @param fileName 缓存文件文件名
     * @return
     */
    public static File getAppCacheFile(String fileName, Context ctx) throws Exception {

        File cacheDir = getAppCacheDir(ctx);

        if (cacheDir != null && cacheDir.exists()) {

            return new File(cacheDir, fileName);
        }

        return null;

    }

    /**
     * 获取app缓存目录
     *
     * @return
     * @throws Exception
     */
    public static File getAppCacheDir(Context ctx) {

        boolean mountFlag = true;

        File cacheFile = null;

        if (!checkSDCard()) {
            Log.e(TAG, "SD card did not mounted");
            mountFlag = false;

            cacheFile = ctx.getCacheDir();      //使用系统默认的缓存路径
        }


        if (mountFlag) {
            File storageRoot = Environment.getExternalStorageDirectory();
            cacheFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat("muzilib"+ File.separator + "cache"));


            if (!cacheFile.exists()) {

                boolean result = cacheFile.mkdirs();

                if (result) {
                    return cacheFile;
                } else {
                    return null;
                }

            }
        }

        return cacheFile;
    }

    /**
     * 获取临时文件
     *
     * @param fileName 临时文件文件名
     * @return
     */
    public static File getAppTmpFile(String fileName) throws Exception {

        if (!checkSDCard()) {
            Log.e(TAG, "SD card did not mounted");
            throw new Exception("SD card did not mounted");
        }

        File storageRoot = Environment.getExternalStorageDirectory();
        File tmpFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(SysConstants.APP_TMP_CATALOG));

        if (!tmpFile.exists()) {

            boolean result = tmpFile.mkdirs();

            if (result) {
                return new File(tmpFile, fileName);
            }

            return null;
        }

        return new File(tmpFile, fileName);
    }

    /**
     * 获取缩略图文件
     *
     * @param fileName 临时文件文件名
     * @return
     */
    public static File getAppThumbnailCacheFile(String fileName) throws Exception {

        if (!checkSDCard()) {
            Log.e(TAG, "SD card did not mounted");
            throw new Exception("SD card did not mounted");
        }

        File storageRoot = Environment.getExternalStorageDirectory();
        File tmpFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(SysConstants.APP_THUMBNAIL_CATALOG));

        if (!tmpFile.exists()) {

            boolean result = tmpFile.mkdirs();

            if (result) {
                return new File(tmpFile, fileName);
            }

            return null;
        }

        return new File(tmpFile, fileName);
    }


    /**
     * 获取日志文件
     *
     * @param fileName 获取日志文件
     * @return
     */
    public static File getLogFile(String fileName) {

        if (!checkSDCard()) {
            Log.e(TAG, "SD card did not mounted");
            return null;
        }

        File storageRoot = Environment.getExternalStorageDirectory();
        File tmpFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(SysConstants.APP_LOG));

        if (!tmpFile.exists()) {

            boolean result = tmpFile.mkdirs();

            if (result) {
                return new File(tmpFile, fileName);
            }

            return null;
        }

        return new File(tmpFile, fileName);
    }

    /**
     * 生成缓存文件名称
     *
     * @param type 文件类型
     * @return
     */
    public static String generateFileName(SysConstants.FileType type) {

        String fileName = UUID.randomUUID().toString();

        switch (type) {
            case FILE_TYPE_IMAGE:     //图片
                fileName = fileName.concat(DOT).concat(SysConstants.SUFFIX_IMAGE_FILE);
                break;
            case FILE_TYPE_AUDIO:     //音频
                fileName = fileName.concat(DOT).concat(SysConstants.SUFFIX_AUDIO_FILE);
                break;
            case FILE_TYPE_VIDEO:     //视频
                fileName = fileName.concat(DOT).concat(SysConstants.SUFFIX_VIDEO_FILE);
                break;
            case FILE_TYPE_TMP:       //临时文件
                fileName = fileName.concat(DOT).concat(SysConstants.SUFFIX_TMP_FILE);
                break;
            case FILE_TYPE_THUMBNAIL://缩略图
                fileName = fileName.concat(DOT).concat(SysConstants.SUFFIX_IMAGE_THUMBNAIL);
                break;
        }

        return fileName;

    }




    /**
     * 获取sdcard可用空间的大小
     *
     * @return 可用的sdkcard大小 单位MB
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 获取sdcard容量
     *
     * @return sdcard容量 单位MB
     */
    @SuppressWarnings({"deprecation", "unused"})
    private static long getSDAllSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    /**
     * 整形转换字节
     *
     * @param n
     * @return byte[]
     */
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

    /**
     * 浮点数转换字节
     *
     * @param f
     * @return byte[]
     */
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 将byte数组转换为int数据
     *
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int byteArray2int(byte[] b) {
        return (((int) b[0]) << 24) + (((int) b[1]) << 16)
                + (((int) b[2]) << 8) + b[3];
    }

    /**
     * @param text
     * @return
     * @Description 判断是否是url
     */
    private static String matchUrl(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Pattern p = Pattern.compile(
                "[http]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    /**
     * @param text
     * @param cmpUrl
     * @return
     * @Description 返回匹配到的URL
     */
    private static String getMatchUrl(String text, String cmpUrl) {
        String url = matchUrl(text);
        if (url != null && url.contains(cmpUrl)) {
            return url;
        } else {
            return null;
        }
    }


    public static String getImageSavePath(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        final File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "MGJ-IM"
                + File.separator
                + "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath() + File.separator + fileName;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


//    public static int getElementSize(Context context) {
//        if (context != null) {
//            DisplayMetrics dm = context.getResources().getDisplayMetrics();
//            int screenHeight = px2dip(dm.heightPixels, context);
//            int screenWidth = px2dip(dm.widthPixels, context);
//            int size = screenWidth / 6;
//            if (screenWidth >= 800) {
//                size = 60;
//            } else if (screenWidth >= 650) {
//                size = 55;
//            } else if (screenWidth >= 600) {
//                size = 50;
//            } else if (screenHeight <= 400) {
//                size = 20;
//            } else if (screenHeight <= 480) {
//                size = 25;
//            } else if (screenHeight <= 520) {
//                size = 30;
//            } else if (screenHeight <= 570) {
//                size = 35;
//            } else if (screenHeight <= 640) {
//                if (dm.heightPixels <= 960) {
//                    size = 35;
//                } else if (dm.heightPixels <= 1000) {
//                    size = 45;
//                }
//            }
//            return size;
//        }
//        return 40;
//    }


    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideInputKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 获取系统版本
     *
     * @param ctx
     * @return
     */
    public static String getVersionName(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {

            return "未知版本";
        }
    }

    /**
     * 获取系统版本名称
     *
     * @param ctx
     * @return
     */
    public static int getVersionCode(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static String getUUID(Context ctx){
        TelephonyManager tManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tManager.getDeviceId();
    }

    /**
     * 获取Android SKD 版本
     *
     * @return
     */
    public static String getAndroidSDK() {
        return Build.VERSION.SDK;
    }


    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;

    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= 19;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(final float density, final float dpValue) {
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(final float density, final float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, final float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, final float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 获取照相机支持的拍照分辨率列表
     *
     * @param camera
     * @return
     */
    public static List<Camera.Size> getResolutionList(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }

    public static class ResolutionComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height != rhs.height)
                return lhs.height - rhs.height;
            else
                return lhs.width - rhs.width;
        }

    }

}
