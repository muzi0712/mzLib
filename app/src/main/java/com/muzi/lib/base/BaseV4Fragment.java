package com.muzi.lib.base;

import android.support.v4.app.Fragment;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 * 
 * @author baoy
 * @version 1.0 create by 15/7/6 下午8:15
 */
public class BaseV4Fragment extends Fragment {

	public MZActionBarActivity getCurrentActivity() {
		if (getActivity() != null
				&& getActivity() instanceof MZActionBarActivity) {
			return (MZActionBarActivity) getActivity();
		}
		return null;
	}
}
