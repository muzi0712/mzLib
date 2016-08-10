package com.muzi.lib.weight;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.muzi.lib.utils.ViewUtils;
import com.muzi.muzilib.R;

/**
 * <Pre>
 * 仿ios底部tab按钮</br>
 * 实现功能
 * 1.tab button 的选中时效果
 * 2.tab button 的消息指示器
 * </Pre>
 * 
 * @author baoy
 * @version 1.0 create by 15/6/23 下午3:05
 */
@SuppressWarnings("deprecation")
public class MZTabButton extends RelativeLayout {

	private TextView mLabelTextView;
	private ImageView mTabIconImageView;
	private View mTabIndicatorView;
	private TextView mBadgeTextView;

	/** 标签显示内容 */
	private String mLabelText;
	/** 标签显示字体大小 */
	private float mLabelTextSize;
	/** 枝条显示字的颜色 */
	// private int mLabelTextColor;
	private ColorStateList mLabelTextColor;

	/** 标签显示图标 */
	private Drawable mTabIconDrawable;

	/** 标签选中指示器图标 */
	private Drawable mTabIndicatorDrawable;
	/** 是否显示标签选中指示器 */
	private boolean mTabIndicator;

	/** 消息标识图标 */
	private Drawable mBadgeDrawable;
	/** 是否显示消息标识图标 */
	private boolean mIsShowBadge;

	private TabButtonMode tabButtonMode;

	public MZTabButton(Context context) {
		this(context, null);
	}

	public MZTabButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.xtTabButtonStyle);
	}

	public MZTabButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// Load defaults from resources
		final Resources res = getResources();
		final float defaultTextSize = res
				.getDimensionPixelSize(R.dimen.default_tab_text_size);
		final int defaultTextColor = res
				.getColor(R.color.default_tab_text_color);
		final int defaultIndicatorBackgroundColor = res
				.getColor(R.color.default_tab_indicator_bg);
		final int defaultBadgeBackgroundColor = res
				.getColor(R.color.default_badge_bg);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MZTabButton, defStyleAttr, 0);

		this.mLabelText = a.getString(R.styleable.MZTabButton_tabLabelText);
		this.mLabelTextSize = a.getDimension(
				R.styleable.MZTabButton_tabLabelTextSize, defaultTextSize);
		// this.mLabelTextColor =
		// a.getColor(R.styleable.XTTabButton_tabLabelTextColor,
		// defaultTextColor);

		this.mLabelTextColor = a
				.getColorStateList(R.styleable.MZTabButton_tabLabelTextColor);
		this.mTabIconDrawable = a
				.getDrawable(R.styleable.MZTabButton_tabIconDrawable);

		if (a.getDrawable(R.styleable.MZTabButton_tabIndicatorDrawable) != null) {
			this.mTabIndicatorDrawable = a
					.getDrawable(R.styleable.MZTabButton_tabIndicatorDrawable);
		} else {
			this.mTabIndicatorDrawable = new ColorDrawable(a.getColor(
					R.styleable.MZTabButton_tabIndicatorDrawable,
					defaultIndicatorBackgroundColor));
		}

		this.mTabIndicator = a.getBoolean(
				R.styleable.MZTabButton_enableTabIndicator, true);

		if (a.getDrawable(R.styleable.MZTabButton_badgeDrawable) != null) {
			this.mBadgeDrawable = a
					.getDrawable(R.styleable.MZTabButton_badgeDrawable);
		} else {
			this.mBadgeDrawable = new ColorDrawable(a.getColor(
					R.styleable.MZTabButton_badgeDrawable,
					defaultBadgeBackgroundColor));
		}

		a.recycle();
		initTabButton();
	}

	/**
	 * 初始化tab button
	 */
	private void initTabButton() {

		tabButtonMode = TabButtonMode.NORMAL;// 设置默认样式

		// 初始化图标
		mTabIconImageView = new ImageView(getContext());
		mTabIconImageView.setId(1);
		mTabIconImageView.setImageDrawable(mTabIconDrawable);

		// 初始化标签文本
		mLabelTextView = new TextView(getContext());
		mLabelTextView.setId(2);
		mLabelTextView.setText(mLabelText);
		mLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelTextSize);
		mLabelTextView.setTextColor(mLabelTextColor);
		mLabelTextView.setGravity(Gravity.CENTER);
		mLabelTextView.setBackgroundDrawable(null);
		mLabelTextView.setSingleLine();

		// 初始化标签指示器
		mTabIndicatorView = new View(getContext());
		mTabIndicatorView.setId(3);
		mTabIndicatorView.setBackgroundDrawable(mTabIndicatorDrawable);
		mTabIndicatorView.setVisibility(INVISIBLE);

		// 初始化消息指示器
		mBadgeTextView = new TextView(getContext());
		mBadgeTextView.setId(4);
		mBadgeTextView.setBackgroundDrawable(mBadgeDrawable);
		mBadgeTextView.setTextColor(getResources().getColor(
				android.R.color.white));
		mBadgeTextView.setVisibility(GONE);
		mBadgeTextView.setGravity(Gravity.CENTER);
		mBadgeTextView.setTextSize(10);

		View shadow = new View(getContext());
		shadow.setId(5);
		shadow.setBackgroundColor(Color.rgb(204, 204, 204));

		RelativeLayout.LayoutParams labelLayoutParam = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		labelLayoutParam.addRule(RelativeLayout.BELOW,
				mTabIconImageView.getId());
		labelLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);

		RelativeLayout.LayoutParams tabIconLayoutParam = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tabIconLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tabIconLayoutParam.topMargin = ViewUtils.dip2px(getContext(), 6f);

		RelativeLayout.LayoutParams tabIndicatorLayoutParam = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, ViewUtils.dip2px(getContext(), 3f));
		tabIndicatorLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		// RelativeLayout.LayoutParams tabBadgeLayoutParam = new
		// RelativeLayout.LayoutParams(ViewUtils.dip2px(getContext(),20f),ViewUtils.dip2px(getContext(),20f));
		RelativeLayout.LayoutParams tabBadgeLayoutParam = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tabBadgeLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP
				| RelativeLayout.ALIGN_PARENT_RIGHT);
		tabBadgeLayoutParam.setMargins(0, ViewUtils.dip2px(getContext(), 5f),
				ViewUtils.dip2px(getContext(), 8f), 0);

		RelativeLayout.LayoutParams shadowLayoutParam = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 1);
		shadowLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		addView(shadow, shadowLayoutParam);
		addView(mLabelTextView, labelLayoutParam);
		addView(mTabIconImageView, tabIconLayoutParam);
		addView(mTabIndicatorView, tabIndicatorLayoutParam);
		addView(mBadgeTextView, tabBadgeLayoutParam);

	}

	/**
	 * 设置控件选中
	 * 
	 * @param s
	 */
	public void setSelected(boolean s) {
		super.setSelected(s);
		this.mLabelTextView.setSelected(s);
		this.mTabIconImageView.setSelected(s);
		this.mTabIndicatorView.setVisibility(mTabIndicator && s
				&& tabButtonMode == TabButtonMode.NORMAL ? VISIBLE : INVISIBLE);
	}

	/**
	 * 显示消息指示器
	 * 
	 * @param num
	 */
	public void showBadge(int num) {

		if (num > 0) {
			mBadgeTextView.setVisibility(VISIBLE);
			mBadgeTextView.setText(String.valueOf(num));
		} else {
			mBadgeTextView.setVisibility(GONE);
			mBadgeTextView.setText("");
		}

	}

	public void setLabelText(CharSequence s) {
		this.mLabelTextView.setText(s);
	}

	public void setLabelTextSize(float s) {
		this.mLabelTextView.setTextSize(s);
	}

	public void setLabelTextColor(int c) {
		this.mLabelTextView.setTextColor(c);
	}

	public void setTabIconResource(int id) {
		this.mTabIconImageView.setImageResource(id);
	}

	public void setTabIconBackground(int id) {
		this.mTabIconImageView.setBackgroundResource(id);
	}

	public void setTabIndicatorResource(int id) {
		this.mTabIndicatorView.setBackgroundResource(id);
	}

	public void setButtonMode(TabButtonMode mode) {

		switch (mode) {
		case NORMAL:
			break;
		case ONLY_ICON:
			this.mLabelTextView.setVisibility(GONE);
			this.mTabIndicatorView.setVisibility(INVISIBLE);

			RelativeLayout.LayoutParams lp = (LayoutParams) this.mTabIconImageView
					.getLayoutParams();
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			lp.topMargin = 0;
			this.mTabIconImageView.setLayoutParams(lp);
			break;
		case ONLY_TEXT:
			this.mTabIconImageView.setVisibility(GONE);
			this.mTabIndicatorView.setVisibility(INVISIBLE);

			RelativeLayout.LayoutParams textviewLp = (LayoutParams) this.mLabelTextView
					.getLayoutParams();
			textviewLp.addRule(RelativeLayout.CENTER_IN_PARENT);
			textviewLp.topMargin = 0;
			this.mLabelTextView.setLayoutParams(textviewLp);
			break;
		}

	}

	/**
	 * TabButton 样式
	 */
	public enum TabButtonMode {
		NORMAL, // 正常
		ONLY_TEXT, // 只有文字
		ONLY_ICON // 只有图标
	}

}
