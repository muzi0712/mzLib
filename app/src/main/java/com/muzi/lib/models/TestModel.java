package com.muzi.lib.models;

import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.muzi.lib.net.GSonRequest;
import com.muzi.lib.net.RequestParamsWrapper;

public class TestModel extends BaseModel {

	public class ButlerEntity {
		// 实体类
	}

	public TestModel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 从服务器获取管家列表
	 * 
	 * @param callback
	 *            请求的回调
	 * @return
	 */
	public Request obtainModelFromServer(final Context ct,
			GSonRequest.Callback<ButlerEntity> callback) {

		final String url = "url";// 接口地址
		GSonRequest<ButlerEntity> request = new GSonRequest<ButlerEntity>(
				Request.Method.POST, url, ButlerEntity.class, callback) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new RequestParamsWrapper<Object>(
						ct).getRequestParams(getMethodName(url));
				Log.d("Request Params", params.toString());
				return params;
			}
		};
		return request;
	}
}
