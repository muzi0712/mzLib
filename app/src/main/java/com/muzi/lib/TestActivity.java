package com.muzi.lib;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.muzi.lib.base.MZActionBarActivity;
import com.muzi.lib.models.TestModel;
import com.muzi.lib.models.TestModel.ButlerEntity;
import com.muzi.lib.net.GSonRequest.Callback;
import com.muzi.muzilib.R;

public class TestActivity extends MZActionBarActivity {
	private TestModel model = new TestModel();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);
	}

	@Override
	protected void initView() {
		Request request = model.obtainModelFromServer(this,
				new Callback<TestModel.ButlerEntity>() {

					@Override
					public void onResponse(ButlerEntity response) {

					}

					@Override
					public void onErrorResponse(VolleyError error) {
						showErrorMsg(error);

					}
				});

		performRequest(request);// 加入请求队列中

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public String setTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
