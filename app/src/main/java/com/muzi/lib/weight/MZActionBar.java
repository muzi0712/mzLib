package com.muzi.lib.weight;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.muzi.lib.utils.ViewUtils;
import com.muzi.lib.views.FocusedTextView;
import com.muzi.muzilib.R;


/**
 * <Pre>
 * 自定义ActionBar
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *          <p/>
 *          Create by 14/11/6 下午7:21
 */
@SuppressWarnings("deprecation")
public class MZActionBar extends FrameLayout {

    private Context mContext;

    /**
     * 标题文本的对齐参数.
     */
    private LayoutParams mTitleTextLayoutParams = null;
    /**
     * 标题文字
     */
    private FocusedTextView mTitleTextView = null;
    /**
     * 左边ImageView
     */
    private View mLeftView = null;
    /**
     * 阴影线
     */
    private View mShadowLine = null;
    /**
     * 右边布局的的对齐参数.
     */
    private LayoutParams mLeftViewLayoutParams = null;
    /**
     * 右边的布局
     */
    private LinearLayout mRightViewLayout = null;
    /**
     * 右边布局的的对齐参数.
     */
    private LayoutParams mRightViewLayoutParams = null;
    /**
     * 下拉列表弹出窗
     */
    private PopupWindow mDropDownWindow = null;

    private LayoutInflater mInflater = null;

    /*
     * 默认布局参数
     */
    private LinearLayout.LayoutParams mDefaultLayoutParamsWW = null;
    private LinearLayout.LayoutParams mDefaultLayoutParamsWM = null;
    private LinearLayout.LayoutParams mDefaultLayoutParamsMW = null;
    private LinearLayout.LayoutParams mDefaultLayoutParamsMM = null;

    /**
     * 标题宽度
     */
    private float mTitleTextWidth;
    /**
     * 标题字体大小
     */
    private float mTitleTextSize;
    /**
     * 标题字体颜色
     */
    private int mTitleTextColor;

    /**
     * 激活阴影线
     */
    private boolean mEnableShadowLine;
    /**
     * 阴影线高度
     */
    private float mShadowLineHeight;
    /**
     * 阴影线背景
     */
    private Drawable mShadowLineDrawable;

    /**
     * 边距长度
     */
    private float mHorizontalPadding;


    public MZActionBar(Context context) {
        this(context, null);
    }

    public MZActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.xtActionBarStyle);
    }

    public MZActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Load defaults from resources
        final Resources res = getResources();
        final float defaultTitleWidth = res.getDimension(R.dimen.default_title_text_width);
        final float defaultTitleSize = res.getDimensionPixelSize(R.dimen.default_title_text_size);
        final int defaultTitleColor = res.getColor(R.color.default_title_text_color);
        final float defaultShadowLineHeight = res.getDimension(R.dimen.default_shadow_line_height);
        final boolean defaultEnableShadowLine = res.getBoolean(R.bool.enable_shadow_line);
        final int defaultShadowLineColor = res.getColor(R.color.default_shadow_line_color);

        //Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MZActionBar, defStyleAttr, 0);
        mTitleTextWidth = a.getDimension(R.styleable.MZActionBar_xtTitleTextWidth, defaultTitleWidth);
        mTitleTextSize = a.getDimension(R.styleable.MZActionBar_xtTitleTextSize, defaultTitleSize);
        mTitleTextColor = a.getColor(R.styleable.MZActionBar_xtTitleTextColor, defaultTitleColor);
        mShadowLineHeight = a.getDimension(R.styleable.MZActionBar_shadow_line_height, defaultShadowLineHeight);
        mEnableShadowLine = a.getBoolean(R.styleable.MZActionBar_enable_shadow_line, defaultEnableShadowLine);
        if (a.getDrawable(R.styleable.MZActionBar_shadow_drawable) != null) {
            mShadowLineDrawable = a.getDrawable(R.styleable.MZActionBar_shadow_drawable);
        } else {
            mShadowLineDrawable = new ColorDrawable(a.getColor(R.styleable.MZActionBar_shadow_drawable, defaultShadowLineColor));
        }


        a.recycle();
        init();
        initActionBar(context);
    }

    /**
     * 属性初始化
     */
    private void init() {

        //取得预设的属性
        mHorizontalPadding = getResources().getDimension(R.dimen.default_view_horizontal_padding);
    }

    /**
     * init actionbar
     */
    private void initActionBar(Context context) {

        this.setId(1);
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;

        mDefaultLayoutParamsWW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mDefaultLayoutParamsWM = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mDefaultLayoutParamsMW = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mDefaultLayoutParamsMM = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mDefaultLayoutParamsWW.gravity = Gravity.CENTER_VERTICAL;

        //初始化imageView布局属性
        mLeftViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mLeftViewLayoutParams.gravity = Gravity.LEFT;
        //初始化titleLayout 布局属性
//        int titleWidth = mTitleTextWidth;
        mTitleTextLayoutParams = new LayoutParams((int) mTitleTextWidth, LayoutParams.MATCH_PARENT);
        mTitleTextLayoutParams.gravity = Gravity.CENTER;
        //初始化rightLayout 布局属性
        mRightViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mRightViewLayoutParams.gravity = Gravity.RIGHT;


        //初始化TitleLayout布局
        LinearLayout mTitleTextLayout = new LinearLayout(context);
        mTitleTextLayout.setOrientation(LinearLayout.VERTICAL);
        mTitleTextLayout.setGravity(Gravity.CENTER);
        mTitleTextLayout.setPadding(0, 0, 0, 0);

        //初始化文本View
        mTitleTextView = new FocusedTextView(context);
        mTitleTextView.setTextColor(mTitleTextColor);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
        mTitleTextView.setPadding(10, 0, 10, 0);
        mTitleTextView.setGravity(Gravity.CENTER);
        mTitleTextView.setBackgroundResource(android.R.color.transparent);
        mTitleTextView.setSingleLine();
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTitleTextView.setMarqueeRepeatLimit(-1);
        mTitleTextView.setFocusable(true);
        mTitleTextView.setFocusableInTouchMode(true);
        mTitleTextView.setHorizontallyScrolling(true);
//        int maxlength = 12;
//        mTitleTextView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxlength) });
//        int maxWidth = DensityUtil.dip2px(getContext(),100f);
//        mTitleTextView.setMaxWidth(maxWidth);

        mTitleTextLayout.addView(mTitleTextView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        //初始化左边图片View
        mLeftView = new ImageView(context);
        mLeftView.setVisibility(View.GONE);
        mLeftView.setBackgroundResource(R.drawable.xt_actionbar_btn_bg);
        mLeftView.setPadding((int) mHorizontalPadding, 0, (int) mHorizontalPadding, 0);

        // 初始化右边Layout的布局
        mRightViewLayout = new LinearLayout(context);
        mRightViewLayout.setOrientation(LinearLayout.HORIZONTAL);
        mRightViewLayout.setPadding(0, 0, 0, 0);
        mRightViewLayout.setHorizontalGravity(Gravity.RIGHT);
        mRightViewLayout.setGravity(Gravity.CENTER_VERTICAL);
        mRightViewLayout.setVisibility(View.GONE);

        //初始化阴影线
        mShadowLine = new View(context);
        mShadowLine.setBackgroundDrawable(mShadowLineDrawable);
        if (!mEnableShadowLine) {
            mShadowLine.setVisibility(GONE);
        }

        LayoutParams shadowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        shadowLayoutParams.height = (int) mShadowLineHeight;
        shadowLayoutParams.gravity = Gravity.BOTTOM;


        this.addView(mLeftView, mLeftViewLayoutParams);
        this.addView(mTitleTextLayout, mTitleTextLayoutParams);
        this.addView(mRightViewLayout, mRightViewLayoutParams);
        this.addView(mShadowLine, shadowLayoutParams);
    }

    /**
     * 设置标题栏的背景
     *
     * @param res 背景图资源ID
     */
    public void setActionBarBackground(@DrawableRes int res) {
        this.setBackgroundResource(res);
    }

    /**
     * 设置标题栏的背景
     *
     * @param d 背景图
     */
    public void setActionBarBackgroundDrawable(Drawable d) {
        this.setBackgroundDrawable(d);
    }

    /**
     * 设置标题栏的背景
     *
     * @param color 背景颜色值
     */
    public void setActionBarBackgroundColor(int color) {
        this.setBackgroundColor(color);
    }

    /**
     * 设置标题文本内容
     *
     * @param text 文本
     */
    public void setTitleText(String text) {
        mTitleTextView.setText(text);
    }

    /**
     * 设置标题文本内容
     *
     * @param resId 文本的资源ID
     */
    public void setTitleText(int resId) {
        mTitleTextView.setText(resId);
    }

    /**
     * 设置标题文字边距
     *
     * @param left   the left
     * @param top    the top
     * @param right  the right
     * @param bottom the bottom
     */
    public void setTitleTextMargin(int left, int top, int right, int bottom) {
        this.mTitleTextLayoutParams.setMargins(left, top, right, bottom);
    }


    /**
     * 设置标题文字大小
     *
     * @param titleTextSize 文字大小
     */
    public void setTitleTextSize(int titleTextSize) {
        this.mTitleTextSize = titleTextSize;
        this.mTitleTextView.setTextSize(titleTextSize);
    }

    /**
     * 设置标题字体粗体.
     *
     * @param bold true 粗体  false 标准
     */
    public void setTitleTextBold(boolean bold) {
        TextPaint paint = mTitleTextView.getPaint();
        if (bold) {
            //粗体
            paint.setFakeBoldText(true);
        } else {
            paint.setFakeBoldText(false);
        }

    }

    /**
     * 设置标题字体颜色
     *
     * @param color
     */
    public void setTitleTextColor(int color) {
        this.mTitleTextColor = color;
        this.mTitleTextView.setTextColor(color);
    }

    /**
     * 设置标题drawable
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setTitleDrawable(int left, int top, int right, int bottom){
        this.mTitleTextView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }


    public void setTitleTextSelector(){
        this.mTitleTextView.setBackgroundResource(R.drawable.xt_actionbar_btn_bg);
    }

    public void clearTitleTextSelector(){
        this.mTitleTextView.setBackgroundResource(android.R.color.transparent);
    }

    /**
     * 设置ActionBar左边的View
     *
     * @param drawable drawable资源
     */
    public void setLeftImage(Drawable drawable) {
        mLeftView.setVisibility(View.VISIBLE);
        if (mLeftView instanceof ImageView) {
            ((ImageView) mLeftView).setImageDrawable(drawable);
        }

    }

    /**
     * 设置ActionBar左边的View
     *
     * @param resId 资源ID
     */
    public void setLeftImage(int resId) {
        mLeftView.setVisibility(View.VISIBLE);
        if (mLeftView instanceof ImageView) {
            ((ImageView) mLeftView).setImageResource(resId);
        }
    }

    /**
     * 设置指定view对象到ActionBar左边布局
     *
     * @param v
     */
    public void setLeftView(View v) {
        this.removeView(mLeftView);
        mLeftView = v;
        mLeftView.setVisibility(View.VISIBLE);
        mLeftView.setBackgroundResource(R.drawable.xt_actionbar_btn_bg);
        mLeftView.setPadding((int) mHorizontalPadding, 0,
                (int) mHorizontalPadding, 0);

        this.addView(mLeftView, mLeftViewLayoutParams);
    }

    /**
     * 隐藏ActionBar左边View
     */
    public void hideLeftView() {
        mLeftView.setVisibility(View.GONE);
        mLeftView.setOnClickListener(null);
    }


    /**
     * 添加指定View对象到ActionBar右边布局
     *
     * @param rv 指定的View
     */
    public void addRightView(View rv) {
        mRightViewLayout.setVisibility(View.VISIBLE);
        rv.setBackgroundResource(R.drawable.xt_actionbar_btn_bg);
        rv.setPadding((int) mHorizontalPadding, 0, (int) mHorizontalPadding, 0);
        mDefaultLayoutParamsWM.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        mRightViewLayout.addView(rv, mDefaultLayoutParamsWM);
    }

    /**
     * 添加指定View资源ID到ActionBar右边布局
     *
     * @param resId 指定的View的资源ID
     */
    public void addRightView(int resId) {
        mRightViewLayout.setVisibility(View.VISIBLE);
        View rv = mInflater.inflate(resId, null);
        rv.setBackgroundResource(R.drawable.xt_actionbar_btn_bg);
        rv.setPadding((int) mHorizontalPadding, 0, (int) mHorizontalPadding, 0);
        mDefaultLayoutParamsWM.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        mRightViewLayout.addView(rv, mDefaultLayoutParamsWM);
    }

    /**
     * 添加指定View对象到ActionBar右边布局中
     *
     * @param rv       View对象
     * @param listener 点击事件
     */
    public void addRightView(View rv, OnClickListener listener) {
        if (rv != null) {
            rv.setOnClickListener(listener);
        }
        this.addRightView(rv);
    }

    /**
     * 设置右边带文字按钮
     */
    public void addRightButtonWithText(String txt, OnClickListener ll) {


        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout btnView = new LinearLayout(mContext);

        TextView btnTxt = new TextView(mContext);
        btnTxt.setText(txt);
        btnTxt.setTextSize(20);
        btnTxt.setTextColor(mContext.getResources().getColor(android.R.color.white));

        btnView.addView(btnTxt);
        btnView.setClickable(true);
        btnView.setLayoutParams(btnParams);
        btnView.setGravity(Gravity.CENTER);
        btnView.setOnClickListener(ll);

        addRightView(btnView);
    }

    /**
     * 替换ActionBar右边视图
     *
     * @param newView
     */
    public void replaceRightView(View newView) {
        mRightViewLayout.setVisibility(VISIBLE);
        mRightViewLayout.removeAllViews();
        addRightView(newView);
    }

    /**
     * 替换ActionBar右边视图
     *
     * @param newView
     */
    public void replaceRightView(View newView, OnClickListener listener) {
        mRightViewLayout.setVisibility(VISIBLE);
        mRightViewLayout.removeAllViews();
        addRightView(newView, listener);
    }


    /**
     * 删除ActionBar右边的View.
     */
    public void clearRightView() {
        mRightViewLayout.removeAllViews();
        mRightViewLayout.setVisibility(GONE);
    }

    /**
     * 获取标题文本View
     *
     * @return
     */
    public TextView getTitleTextView() {
        return mTitleTextView;
    }


    /**
     * 获取LeftView
     *
     * @return
     */
    public View getLeftView() {
        return mLeftView;
    }

    /**
     * 寻找指定id的子视图。如果Layout具有给定的ID，返回这个view。
     *
     * @param id
     * @return
     */
    public View getRightView(int id) {
        return mRightViewLayout.findViewById(id);
    }

    /**
     * 寻找指定id的子视图。如果Layout具有给定的TAG，返回这个view。
     *
     * @param tag
     * @return
     */
    public View getRightView(Object tag) {
        return mRightViewLayout.findViewWithTag(tag);
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     * @param view       要显示的View
     * @param offsetMode 偏移模式
     */
    public void showDropDownWindow(View parent, View view, boolean offsetMode) {
        ViewUtils.measureView(view);
        int popWidth = parent.getMeasuredWidth();
        int popMargin = (this.getMeasuredHeight() - parent.getMeasuredHeight()) / 2;
        if (view.getMeasuredWidth() > parent.getMeasuredWidth()) {
            popWidth = view.getMeasuredWidth();
        }
        if (offsetMode) {
            mDropDownWindow = new PopupWindow(view, popWidth + 10, LayoutParams.WRAP_CONTENT, true);
        } else {
            mDropDownWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        }

        mDropDownWindow.setFocusable(true);
        mDropDownWindow.setOutsideTouchable(true);
        mDropDownWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        mDropDownWindow.showAsDropDown(parent, 0, popMargin + 2);
    }

    /**
     * 隐藏popupWindow
     */
    public void hideDropDownWindow() {
        if (mDropDownWindow != null) {
            mDropDownWindow.dismiss();
        }

    }

    /**
     * 设置标题下拉的View
     *
     * @param view
     */
    public void setTitleDropDown(final View view) {
        if (view == null) {
            return;
        }
        setTitleTextOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDropDownWindow(mTitleTextView, view, true);
            }
        });
    }

    /**
     * 显示阴影
     */
    public void enableShadow() {
        this.mEnableShadowLine = true;
        this.mShadowLine.setVisibility(VISIBLE);
    }

    /**
     * 隐藏阴影
     */
    public void disableShadow() {
        this.mEnableShadowLine = false;
        this.mShadowLine.setVisibility(GONE);
    }

    /**
     * 设置阴影颜色
     *
     * @param resid
     */
    public void setShadowColor(@DrawableRes int resid) {

        this.mShadowLine.setBackgroundResource(resid);
    }


    /**
     * 设置左边Image点击事件
     *
     * @param onClickListener 点击回调事件
     */
    public void setLeftOnClickListener(OnClickListener onClickListener) {
        mLeftView.setOnClickListener(onClickListener);
    }

    /**
     * 设置标题的点击事件
     *
     * @param onClickListener 点击回调事件
     */
    public void setTitleTextOnClickListener(OnClickListener onClickListener) {
        mTitleTextView.setClickable(true);
        mTitleTextView.setFocusable(true);
        mTitleTextView.setFocusableInTouchMode(true);
        mTitleTextView.requestFocus();
        mTitleTextView.setOnClickListener(onClickListener);
    }

}
