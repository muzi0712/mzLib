package com.muzi.lib.base;

import android.app.Fragment;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 * 
 * @author baoy
 * @version 1.0 create by 15/7/6 下午8:15
 */
public class BaseFragment extends Fragment {

	public MZActionBarActivity getCurrentActivity() {
		if (getActivity() != null
				&& getActivity() instanceof MZActionBarActivity) {
			return (MZActionBarActivity) getActivity();
		}
		return null;
	}
}
