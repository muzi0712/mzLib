package com.muzi.lib.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * <Pre>
 * Adapter基类，简化了Adapter代码中ViewHolder的写法<br/>
 * 子类只需实现getView()方法即可，在getView()中 使用obtainViewFromViewHolder()方法获取<br/>
 * 例：<br/>
 *  getView(View v,int p,ViewGroup p){
 *
 *      if(v == null){
 *          ...
 *      }
 *
 *      TextView tv = obtainViewFromViewHolder(v,R.id.xxx);
 *
 *  }
 *
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 *
 * @Create by 14/11/4 下午5:28
 */
@SuppressWarnings("unchecked")
public abstract class AdapterBase<T> extends BaseAdapter {

    protected Context mContext;
    private List<T> mDataList;
    private LayoutInflater mInflater;

    public AdapterBase(List<T> dataList, Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is not allow null!");
        }
        this.mDataList = dataList;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        if (dataList == null) {
            this.mDataList = new ArrayList();
        }
    }

    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 快速获取view
     *
     * @param container 容器
     * @param id        组件id
     * @param <T>
     * @return
     */
    public <T extends View> T obtainViewFromViewHolder(View container, int id) {
        return ViewHolder.get(container, id);
    }

    @Override
    public int getCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDataList != null && mDataList.size() > 0) {
            return mDataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * <Pre>
     * 通用viewHolder类,使用散列数组存储view对象,简化getView操作
     * </Pre>
     *
     * @author baoy
     * @version 1.0
     *          <p/>
     *          Create by 14/11/4 下午5:10
     */
    public static class ViewHolder {

        /**
         * 快速获取以findViewById取得的子view的实例
         *
         * @param v   被获取的view
         * @param id  子view的id
         * @param <T>
         * @return
         */
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View v, int id) {

            if (v == null) {
                return null;
            }

            SparseArray<View> holder = (SparseArray<View>) v.getTag();
            if (holder == null) {
                holder = new SparseArray<View>();
                v.setTag(holder);
            }

            View childView = holder.get(id);
            if (childView == null) {
                childView = v.findViewById(id);
                holder.put(id, childView);
            }

            return (T) childView;
        }

    }
}
