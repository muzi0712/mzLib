package com.muzi.lib.utils;

import java.util.Calendar;

import android.content.Context;
import android.widget.Toast;
/**
 * 
  * @ClassName: TispToastFactory
  * @Description: TODO  防止多次点击 重复出现  Toast
  * @author muzi08168@163.com
  * @date 2016-6-27 下午5:50:57
  *
 */
public class TispToastFactory {
	private static Context context = null;
	private static Toast toast = null;
	public static final int MIN_CLICK_DELAY_TIME = 1000;
	private static long lastClickTime = 0;

	public static Toast getToast(Context context, String hint) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;
			if (TispToastFactory.context == context) {

				toast.setText(hint);
				System.out.println("没有新创建");
			} else {
				System.out.println("创建了一个新的toast");
				TispToastFactory.context = context;
				toast = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
			}
		} else {
			toast.cancel();
		}

		return toast;
	}
}
