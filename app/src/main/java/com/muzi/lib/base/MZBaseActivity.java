package com.muzi.lib.base;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.muzi.lib.exception.ValidateException;
import com.muzi.lib.utils.AppManager;
import com.muzi.lib.utils.CircleBitmapProcessor;
import com.muzi.lib.utils.CommonUtils;
import com.muzi.muzilib.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * <Pre>
 * Activity抽象基类
 * </Pre>
 * 
 * @author baoy
 * @version 1.0
 *          <p/>
 *          Create by 14/11/5 下午9:18
 */
public abstract class MZBaseActivity extends AppCompatActivity implements
		MZViewBase {

	private static final String TAG = "XTBaseActivity";

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			return false;
		}
	});

	protected NotificationManager mNotificationManager;
	/**
	 * Toast对象实例
	 */
	private Toast mToast = null;

	private SweetAlertDialog mProgressDialog;

	private InputMethodManager inputMethodManager;
	/**
	 * Volley请求队列
	 */
	private RequestQueue mRequestQueue = null;

	/**
	 * Activity 的唯一标识，用于Volley请求标识
	 */
	private Object tag;
	/**
	 * 网络连接改变广播接收器
	 */
	private ConnectionChangeReceiver mConnectChangeReceiver;
	/**
	 * 当前网络连接状态
	 */
	private boolean currentConnState;
	/**
	 * 是否关注网络状态改变
	 */
	private boolean careNetState = false;

	protected Context context;

	/*--------------------------------------- 重载，重写的方法  --------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		this.inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		this.tag = setTag();

		// 注册网络状态改变的receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mConnectChangeReceiver = new ConnectionChangeReceiver();
		registerReceiver(mConnectChangeReceiver, filter);

		// 如果当前sdk版本大于19，则设置status bar透明
		if (Build.VERSION.SDK_INT >= 19) {
			setTranslucentStatus(true);
		}
		// 设置status bar 透明背景
		// setStatusBarResource(android.R.color.transparent);
		AppManager.getAppManager().addActivity(this);
		context = this;
		initView();
		initData();
		setListeners();

	}

	/**
	 * 抽象方法子类必须实现
	 */
	protected abstract void initView();

	/**
	 * 
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 设置监听
	 */
	protected abstract void setListeners();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 释放一系列资源
		removeProgressDialog();
		careNetState = false;
		if (mRequestQueue != null && tag != null) {
			Log.d(TAG, "Cancel all request for tag :[" + tag + "]");
			mRequestQueue.cancelAll(tag);
		}

		unregisterReceiver(mConnectChangeReceiver);
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();
		hideSoftKeyboard();
	}

	protected void hideSoftKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/*------------------------------------------ 私有方法  ------------------------------------------*/

	/*
	 * 设置状态栏透明
	 */
	private void setTranslucentStatus(boolean on) {
		// Window win = getWindow();
		// WindowManager.LayoutParams winParams = win.getAttributes();
		// final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		// if (on) {
		// winParams.flags |= bits;
		// } else {
		// winParams.flags &= ~bits;
		// }
		// win.setAttributes(winParams);
	}

	/*---------------------------------- 可被子类重写的方法,抽象方法  ----------------------------------*/

	/**
	 * 设置Activity唯一标识
	 * 
	 * @return
	 */
	public abstract String setTag();

	/**
	 * 网络连接状态发生改变 <br/>
	 * 子类只有调用了registerNetStateChanged()方法 该方法实现才会被调用
	 * 
	 * @param b
	 *            标记是否关心网络状态改变
	 */
	protected abstract void onNetStateChanged(boolean b);

	/**
	 * 显示无数据view
	 */
	protected abstract void onShowEmptyView(OnReloadListener listener);

	/**
	 * 显示错误view
	 */
	protected abstract void onShowErrorView(VolleyError volleyError,
			OnReloadListener listener);

	/**
	 * 显示加载view
	 */
	protected abstract void onShowLoadingView();

	/**
	 * 数据加载完成之后调用
	 */
	protected abstract void onLoadingComplete();

	/**
	 * 显示错误消息
	 * 
	 * @param volleyError
	 */
	public void showErrorMsg(VolleyError volleyError) {
		String msg;
		if (volleyError instanceof AuthFailureError) {
			// Error indicating that there was an authentication failure when
			// performing a Request.
			msg = getString(R.string.http_connect_auth_failure_error_msg);

		} else if (volleyError instanceof NoConnectionError) {

			msg = getString(R.string.http_connect_no_connection_error_msg);
		} else if (volleyError instanceof NetworkError) {
			// Indicates that there was a network error when performing a Volley
			// request.
			msg = getString(R.string.http_connect_network_error_msg);

		} else if (volleyError instanceof ParseError) {
			// Indicates that the server's response could not be parsed.
			msg = getString(R.string.http_connect_parse_error_msg);

		} else if (volleyError instanceof ServerError) {
			// Indicates that the server responded with an error response.
			msg = getString(R.string.http_connect_server_error_msg);

		} else if (volleyError instanceof TimeoutError) {
			// Indicates that the connection or the socket timed out.
			msg = getString(R.string.http_connect_timeout_error_msg);

		} else if (volleyError instanceof ValidateException) {
			// 验证错误
			msg = ((ValidateException) volleyError).getMsg();
		} else {
			// 未知的错误
			msg = getString(R.string.http_connect_unknown_error_msg);
		}

		showToast(msg, Toast.LENGTH_SHORT);
	}

	/*------------------------------------------ 公有方法  ------------------------------------------*/

	/**
	 * 注册网络连接状态变更通知
	 */
	protected void registerNetStateChanged() {
		careNetState = true;
	}

	/**
	 * 获取Activity唯一标识
	 * 
	 * @return
	 */
	public String getTag() {
		return String.valueOf(this.tag);
	}

	/**
	 * 获取请求队列
	 * 
	 * @return
	 */
	public RequestQueue getRequestQueue() {
		return this.mRequestQueue;
	}

	/**
	 * 显示Toast,页面中重复Toast不会重复实例化Toast对象
	 * 
	 * @param charSequence
	 *            String
	 * @param duration
	 *            显示时间
	 */
	protected void showToast(CharSequence charSequence, int duration) {

		if (mToast == null) {
			mToast = Toast
					.makeText(MZBaseActivity.this, charSequence, duration);
		} else {
			mToast.setText(charSequence);
			mToast.setDuration(duration);
		}

		if (mToast.getView().isShown()) {
			mToast.cancel();
		}

		mToast.show();
	}

	/**
	 * 显示Toast,页面中重复Toast不会重复实例化Toast对象
	 * 
	 * @param resId
	 *            String资源ID
	 * @param duration
	 *            显示时间
	 */
	protected void showToast(int resId, int duration) {

		if (mToast == null) {
			mToast = Toast.makeText(MZBaseActivity.this, resId, duration);
		} else {
			mToast.setText(resId);
			mToast.setDuration(duration);
		}

		if (mToast.getView().isShown()) {
			mToast.cancel();
		}

		mToast.show();
	}

	/**
	 * 取消Toast显示
	 */
	public void cancelToast() {
		if (mToast != null && mToast.getView().isShown()) {
			mToast.cancel();
		}
	}

	@Override
	public void setXTContentView(int resId) {
		setContentView(resId);
	}

	@Override
	public void setXTContentView(View view) {
		setContentView(view);
	}

	@Override
	public void performRequest(Request<?>... request) {
		if (request != null) {
			for (int i = 0; i < request.length; i++) {
				addRequest(request[i]);
			}

		}
	}

	/**
	 * 添加request到请求队列中
	 * 
	 * @param request
	 */
	protected void addRequest(Request<?> request) {
		setRequestTag(request);
		getRequestQueue().add(request);
	}

	/**
	 * 设置request的tag标记
	 * 
	 * @param request
	 */
	protected void setRequestTag(Request<?> request) {
		request.setTag(getTag());
	}

	@Override
	public void showProgressDialog() {
		showProgressDialog(null);
	}

	@Override
	public void showProgressDialog(int resId) {
		String message = getResources().getString(resId);
		showProgressDialog(message);

	}

	@Override
	public void showProgressDialog(String message) {
		showProgressDialog(message, true);
	}

	@Override
	public void showProgressDialog(String message, boolean cancelable) {
		showProgressDialog(message, cancelable, null);
	}

	@Override
	public void showProgressDialog(String message, boolean cancelable,
			SweetAlertDialog.OnSweetClickListener cancelListener) {
		mProgressDialog = new SweetAlertDialog(this,
				SweetAlertDialog.PROGRESS_TYPE);
		mProgressDialog.getProgressHelper().setBarColor(
				Color.parseColor("#A5DC86"));
		if (!TextUtils.isEmpty(message)) {
			mProgressDialog.setTitleText(message);
		}
		mProgressDialog.setCancelable(cancelable);
		mProgressDialog.setCanceledOnTouchOutside(cancelable);
		if (cancelListener != null) {
			mProgressDialog.setCancelClickListener(cancelListener);
		}
		mProgressDialog.show();
	}

	@Override
	public void removeProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	public void showAlertDialog(String title, String content) {

		new SweetAlertDialog(this).setTitleText(title).setContentText(content)
				.show();

	}

	@Override
	public void showAlertDialog(String title) {
		new SweetAlertDialog(this).setTitleText(title).show();
	}

	@Override
	public void showAlertDialog(String title, String content, int type) {
		new SweetAlertDialog(this, type).setTitleText(title)
				.setContentText(content).show();
	}

	@Override
	public void showAlertDialog(String title, String message,
			String confirmText,
			SweetAlertDialog.OnSweetClickListener confirmClick) {
		new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
				.setTitleText(title).setContentText(message)
				.setConfirmText(confirmText)
				.setConfirmClickListener(confirmClick).show();
	}

	@Override
	public void showPromptDialog(String title, String message,
			String confirmText,
			SweetAlertDialog.OnSweetClickListener confirmClick) {
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText(title).setContentText(message)
				.setConfirmText(confirmText)
				.setConfirmClickListener(confirmClick).show();
	}

	@Override
	public void showPromptDialog(String title, String message,
			String confirmText, String cancelText,
			SweetAlertDialog.OnSweetClickListener cancelClick,
			SweetAlertDialog.OnSweetClickListener confirmClick) {
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText(title).setContentText(message)
				.setCancelText(cancelText).setConfirmText(confirmText)
				.showCancelButton(true).setCancelClickListener(cancelClick)
				.setConfirmClickListener(confirmClick).show();
	}

	/**
	 * 启动一个新的Activity
	 * 
	 * @param cls
	 *            Activity class
	 * @param args
	 *            参数
	 */
	public void startActivity(Class<?> cls, Bundle args) {
		Intent intent = new Intent();
		if (args != null) {
			intent.putExtras(args);
		}
		intent.setClass(this, cls);
		startActivity(intent);
	}

	/**
	 * 启动一个新的Activity for result
	 * 
	 * @param cls
	 *            Activity class
	 * @param args
	 *            参数
	 * @param reqCode
	 *            request code
	 */
	public void startActivityForResult(Class<?> cls, Bundle args, int reqCode) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (args != null) {
			intent.putExtras(args);
		}
		startActivityForResult(intent, reqCode);
	}

	/**
	 * 判断当前网络状态是否可用
	 * 
	 * @return
	 */
	protected boolean isNetWorkAvailable() {
		return CommonUtils.isNetWorkAvailable(this);
	}

	/**
	 * 网络连接改变广播接收器
	 */
	public class ConnectionChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean success = true;
			try {

				if (!isNetWorkAvailable()) {
					success = false;
				}

			} catch (Exception e) {
				success = false;
			}

			if (!success) {
				//TODO

			} else {

			}
			// 如果子类注册了网络状态监听，则通知界面更新
			if (careNetState) {
				// 通知界面更新
				onNetStateChanged(success);
			}
			currentConnState = success;

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelToast();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private static final int notifyId = 11;
	private DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
			.postProcessor(new CircleBitmapProcessor())
			.showImageOnFail(R.drawable.default_avatar)
			.showImageForEmptyUri(R.drawable.default_avatar)
			.cacheInMemory(true).cacheOnDisk(true).build();

}
