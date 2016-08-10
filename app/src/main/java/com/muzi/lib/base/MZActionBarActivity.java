package com.muzi.lib.base;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.muzi.lib.exception.ValidateException;
import com.muzi.lib.weight.BetterViewAnimator;
import com.muzi.lib.weight.MZActionBar;
import com.muzi.muzilib.R;

/**
 * <Pre>
 * 使用ActionBar的Activity
 * </Pre>
 * 
 * @author baoy
 * @version 1.0
 *          <p/>
 *          Create by 14/11/5 下午9:19
 */
@SuppressWarnings("deprecation")
public abstract class MZActionBarActivity extends MZBaseActivity {

	// 显示错误view
	private static final int MSG_SHOW_ERROR_VIEW = 0x10;
	// 显示空view
	private static final int MSG_SHOW_EMPTY_VIEW = 0x11;
	// 显示加载中view
	private static final int MSG_SHOW_LOADING_VIEW = 0x12;
	// 加载完成
	private static final int MSG_LOADING_COMPLETE = 0x13;

	/**
	 * 根布局
	 */
	private RelativeLayout mRootLayout;

	private BetterViewAnimator mViewAnimator;

	/**
	 * 内容父容器
	 */
	private FrameLayout mBaseContentLayout;
	/**
	 * 加载布局
	 */
	private LinearLayout mProgressContainer;
	/**
	 * 错误布局
	 */
	private LinearLayout mErrorContainer;
	/**
	 * 空布局
	 */
	private LinearLayout mEmptyContainer;

	private ImageView mProgress;

	/**
	 * ActionBar
	 */
	private MZActionBar mXTActionBar;

	/**
	 * 网络连接状态改变布局
	 */
	private LinearLayout mNetStateLayout;

	private TextView mEmptyText;

	/**
	 * 默认布局参数
	 */
	private LinearLayout.LayoutParams mDefaultLayoutParamsMM = null;

	private LayoutInflater mInflater;

	private Handler mHttpHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_SHOW_ERROR_VIEW:

				break;
			case MSG_SHOW_EMPTY_VIEW:

				break;
			case MSG_SHOW_LOADING_VIEW:

				break;
			case MSG_LOADING_COMPLETE:

				break;
			}
			return false;
		}
	});

	/*--------------------------------------- 重载，重写的方法  --------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setTheme(R.style.AppTheme_ActionBarDefaults);
		setContentView(R.layout.activity_base_actionbar);
		mInflater = LayoutInflater.from(MZActionBarActivity.this);

		mRootLayout = (RelativeLayout) findViewById(R.id.base_root_layout);
		mViewAnimator = (BetterViewAnimator) findViewById(R.id.view_animator);
		mBaseContentLayout = (FrameLayout) findViewById(R.id.base_content_container);
		mProgressContainer = (LinearLayout) findViewById(R.id.progress_container);
		mErrorContainer = (LinearLayout) findViewById(R.id.error_container);
		mEmptyContainer = (LinearLayout) findViewById(R.id.empty_container);
		mProgress = (ImageView) findViewById(R.id.progress);

		mDefaultLayoutParamsMM = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		mRootLayout.setBackgroundColor(getResources().getColor(R.color.gray));// 有背景了??透明背景实现不了
		mRootLayout.setFitsSystemWindows(true);
		mRootLayout.setClipToPadding(true);

		mBaseContentLayout.setPadding(0, 0, 0, 0);

		// 初始化actionbar
		mXTActionBar = new MZActionBar(MZActionBarActivity.this);
		RelativeLayout.LayoutParams actionbarLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		actionbarLayoutParams.height = getResources().getDimensionPixelSize(
				R.dimen.actionbar_height);
		actionbarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mRootLayout.addView(mXTActionBar, actionbarLayoutParams);

		RelativeLayout.LayoutParams baseContentLayoutParams = (RelativeLayout.LayoutParams) mViewAnimator
				.getLayoutParams();
		baseContentLayoutParams.addRule(RelativeLayout.BELOW,
				mXTActionBar.getId());

		initNetStateChangedLayer();

		RelativeLayout.LayoutParams netLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		netLayoutParams.addRule(RelativeLayout.BELOW, mXTActionBar.getId());
		netLayoutParams.height = getResources().getDimensionPixelSize(
				R.dimen.actionbar_height);
		mRootLayout.addView(mNetStateLayout, netLayoutParams);

		initXTActionBar();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onNetStateChanged(boolean b) {
		mNetStateLayout.setVisibility(b ? View.GONE : View.VISIBLE);
	}

	/*
	 * 初始化网络状态变化布局
	 */

	private void initNetStateChangedLayer() {

		mNetStateLayout = new LinearLayout(this);
		mNetStateLayout.setBackgroundColor(getResources().getColor(
				R.color.gl_net_state_changed_bg_color));

		TextView tv = new TextView(this);
		tv.setText(getResources().getString(R.string.network_not_available_msg));
		tv.setTextColor(getResources().getColor(R.color.gl_text_color));
		tv.setTextSize(16f);
		if (Build.VERSION.SDK_INT > 17) {
			tv.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.ic_warning), null, null, null);
		} else {

			tv.setCompoundDrawables(
					getResources().getDrawable(R.drawable.ic_warning), null,
					null, null);
		}
		tv.setCompoundDrawablePadding(10);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;

		mNetStateLayout.addView(tv, lp);

		mNetStateLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打开系统网络设置界面
				Intent intent = null;
				// 判断手机系统的版本 3.0或以上版本
				if (Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}

				startActivity(intent);
			}
		});

		mNetStateLayout.setVisibility(View.GONE);

	}

	/**
	 * 初始化ActionBar基础样式
	 */
	private void initXTActionBar() {
		mXTActionBar.setLeftImage(R.drawable.ic_back);
		mXTActionBar.setLeftOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public void finish() {
		super.finish();
		// overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setXTContentView(int resId) {
		View v = mInflater.inflate(resId, null);
		// mContentLayout.removeAllViews();
		mBaseContentLayout.removeAllViews();
		mBaseContentLayout.addView(v, mDefaultLayoutParamsMM);
	}

	@Override
	public void setXTContentView(View view) {
		mBaseContentLayout.removeAllViews();
		mBaseContentLayout.addView(view, mDefaultLayoutParamsMM);
	}

	@Override
	public void performRequest(Request<?>... request) {

		super.performRequest(request);

	}

	@Override
	protected void onShowLoadingView() {

		mViewAnimator.setDisplayedChildId(R.id.progress_container);

		AnimationDrawable animationDrawable = (AnimationDrawable) mProgress
				.getBackground();

		// 动画是否正在运行
		if (!animationDrawable.isRunning()) {
			// 开始或者继续动画播放
			animationDrawable.start();
		}

		// mEmptyContainer.setVisibility(View.GONE);
		// mBaseContentLayout.setVisibility(View.GONE);
		// mProgressContainer.setVisibility(View.VISIBLE);
		// mErrorContainer.setVisibility(View.GONE);

	}

	@Override
	protected void onShowErrorView(VolleyError volleyError,
			final OnReloadListener listener) {
		Message msg;
		Bundle data = new Bundle();

		if (volleyError instanceof AuthFailureError) {
			// Error indicating that there was an authentication failure when
			// performing a Request.
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_CONNECT_AUTHFAILURE_ERROR;

		} else if (volleyError instanceof NoConnectionError) {

			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_CONNECT_NOCONNECTION_ERROR;

		} else if (volleyError instanceof NetworkError) {
			// Indicates that there was a network error when performing a Volley
			// request.
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_CONNECT_NETWORK_ERROR;

		} else if (volleyError instanceof ParseError) {
			// Indicates that the server's response could not be parsed.
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_CONNECT_PARSE_ERROR;

		} else if (volleyError instanceof ServerError) {
			// Indicates that the server responded with an error response.
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_CONNECT_SERVER_ERROR;

		} else if (volleyError instanceof TimeoutError) {
			// Indicates that the connection or the socket timed out.
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_CONNECT_TIMEOUT_ERROR;

		} else if (volleyError instanceof ValidateException) {
			// 验证错误
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_VALIDATE_ERROR;
		} else {
			// 未知的错误
			msg = mHttpHandler.obtainMessage();
			msg.arg1 = HTTP_UNKNOWN_FAILED;
		}
		msg.what = MSG_SHOW_ERROR_VIEW;
		mHttpHandler.sendMessage(msg);

		mViewAnimator.setDisplayedChildId(R.id.error_container);
		AnimationDrawable animationDrawable = (AnimationDrawable) mProgress
				.getBackground();
		// 动画是否正在运行
		if (animationDrawable.isRunning()) {
			// 开始或者继续动画播放
			animationDrawable.stop();
		}

		// mEmptyContainer.setVisibility(View.GONE);
		// mBaseContentLayout.setVisibility(View.GONE);
		// mProgressContainer.setVisibility(View.GONE);
		// mErrorContainer.setVisibility(View.VISIBLE);
		mErrorContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onReload();
			}
		});

	}

	@Override
	protected void onShowEmptyView(final OnReloadListener listener) {

		mViewAnimator.setDisplayedChildId(R.id.empty_container);
		AnimationDrawable animationDrawable = (AnimationDrawable) mProgress
				.getBackground();
		// 动画是否正在运行
		if (animationDrawable.isRunning()) {
			// 开始或者继续动画播放
			animationDrawable.stop();
		}

		// mEmptyContainer.setVisibility(View.VISIBLE);
		// mBaseContentLayout.setVisibility(View.GONE);
		// mProgressContainer.setVisibility(View.GONE);
		// mErrorContainer.setVisibility(View.GONE);

		mEmptyContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onReload();
			}
		});
	}

	@Override
	protected void onLoadingComplete() {

		mViewAnimator.setDisplayedChildId(R.id.base_content_container);
		AnimationDrawable animationDrawable = (AnimationDrawable) mProgress
				.getBackground();
		// 动画是否正在运行
		if (animationDrawable.isRunning()) {
			// 开始或者继续动画播放
			animationDrawable.stop();
		}

		// mEmptyContainer.setVisibility(View.GONE);
		// mBaseContentLayout.setVisibility(View.VISIBLE);
		// mProgressContainer.setVisibility(View.GONE);
		// mErrorContainer.setVisibility(View.GONE);

	}

	/**
	 * 设置界面为空时显示字符串
	 * 
	 * @param s
	 */
	public void setEmptyText(String s) {
		if (this.mEmptyText == null) {
			this.mEmptyText = (TextView) findViewById(R.id.empty_text);
			mEmptyText.setVisibility(View.VISIBLE);
		}
		mEmptyText.setText(s);
	}

	/**
	 * 设置界面为空时显示字符串
	 * 
	 * @param s
	 * @param listener
	 */
	public void setEmptyText(String s, View.OnClickListener listener) {
		setEmptyText(s);
		this.mEmptyText.setOnClickListener(listener);
	}

	/**
	 * 获取 ActionBar
	 * 
	 * @return
	 */
	public MZActionBar getXTActionBar() {
		return mXTActionBar;
	}

	/**
	 * 显示ActionBar
	 */
	public void showXTActionBar() {
		this.mXTActionBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏ActionBar
	 */
	public void hideXTActionBar() {
		this.mXTActionBar.setVisibility(View.GONE);
	}

	/**
	 * 设置根布局padding
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setPadding(int left, int top, int right, int bottom) {
		// 只有sdk版本大于19才设置padding
		if (Build.VERSION.SDK_INT >= 19) {
			// 设置根布局距离顶部padding
			mRootLayout.setPadding(left, top, right, bottom);
		}
	}

	/**
	 * 获取mRootLayout
	 * 
	 * @return
	 */
	public RelativeLayout getmRootLayout() {

		return mRootLayout;
	}

}
