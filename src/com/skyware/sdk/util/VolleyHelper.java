package com.skyware.sdk.util;

import java.io.IOException;
import java.util.Map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyHelper {
	
	private Context mContext;
	private RequestQueue mQueue;
	private VolleyListner mListner;
	
	public VolleyHelper() {
	}
	public VolleyHelper(Context context) {
		mContext = context;
		mQueue = Volley.newRequestQueue(context);  
	}
	public VolleyHelper(Context context, VolleyListner listner) {
		mContext = context;
		mQueue = Volley.newRequestQueue(context);  
		mListner = listner;
	}
	

	// 异步Volley请求
	public void doGet(String url, final VolleyListner listner) throws IOException{
		if (mQueue == null) {
			if (mContext != null) {
				mQueue = Volley.newRequestQueue(mContext);
			} else {
				return;
			}
		}
		doGet(mQueue, url, listner);
	}
	
	public static void doGet(RequestQueue queue, String url, final VolleyListner listner)
			throws IOException {
		
		final VolleyResult result = new VolleyResult();
		
		StringRequest stringRequest = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						result.setSuccess(true);
						result.setRespString(response);
						listner.onReqFinished(result);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Log.e("TAG", error.getMessage(), error);
						result.setSuccess(false);
						result.setException(error);
						result.setErrMsg(error.getLocalizedMessage());
						listner.onReqFinished(result);
					}
				});
		queue.add(stringRequest);
	}

	public void doPost(String url, final Map<String, String> params, final VolleyListner listner) throws IOException{
		if (mQueue == null) {
			if (mContext != null) {
				mQueue = Volley.newRequestQueue(mContext);
			} else {
				return;
			}
		}
		doPost(mQueue, url, params, listner);
	}
	public static void doPost(RequestQueue queue, String url, final Map<String, String> params, final VolleyListner listner)
			throws IOException {
		final VolleyResult result = new VolleyResult();
		StringRequest postRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// response
						result.setSuccess(true);
						result.setRespString(response);
						listner.onReqFinished(result);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// error
						result.setSuccess(false);
						result.setException(error);
						result.setErrMsg(error.getLocalizedMessage());
						listner.onReqFinished(result);
					}
				}) {
			// 匿名类重载
			@Override
			protected Map<String, String> getParams() {
				return params;
			}
		};
		queue.add(postRequest);
	}

	public void doPut(String url, final Map<String, String> params, final VolleyListner listner) throws IOException{
		if (mQueue == null) {
			if (mContext != null) {
				mQueue = Volley.newRequestQueue(mContext);
			} else {
				return;
			}
		}
		doPut(mQueue, url, params, listner);
	}
	public static void doPut(RequestQueue queue, String url, final Map<String, String> params, final VolleyListner listner)
			throws IOException {
		final VolleyResult result = new VolleyResult();
		StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						result.setSuccess(true);
						result.setRespString(response);
						listner.onReqFinished(result);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Log.e("TAG", error.getMessage(), error);
						result.setSuccess(false);
						result.setException(error);
						result.setErrMsg(error.getLocalizedMessage());
						listner.onReqFinished(result);
					}
				}){
			// 匿名类重载
			@Override
			protected Map<String, String> getParams() {
				return params;
			}
		};
		queue.add(stringRequest);
	}

	public void doDelete(String url, final VolleyListner listner) throws IOException{
		if (mQueue == null) {
			if (mContext != null) {
				mQueue = Volley.newRequestQueue(mContext);
			} else {
				return;
			}
		}
		doDelete(mQueue, url, listner);
	}
	public static void doDelete(RequestQueue queue, String url, final VolleyListner listner)
			throws IOException {
		final VolleyResult result = new VolleyResult();
		StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						result.setSuccess(true);
						result.setRespString(response);
						listner.onReqFinished(result);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Log.e("TAG", error.getMessage(), error);
						result.setSuccess(false);
						result.setException(error);
						result.setErrMsg(error.getLocalizedMessage());
						listner.onReqFinished(result);
					}
				});
		queue.add(stringRequest);
	}
	
	
	public static interface VolleyListner{
		public void onReqFinished(VolleyResult result);
	}
	
	
	public static class VolleyResult {
		private boolean isSuccess;
		private String respString;
		private VolleyError exception;
		private String errMsg;

		
		public boolean isSuccess() {
			return isSuccess;
		}
		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}
		public String getRespString() {
			return respString;
		}
		public void setRespString(String respString) {
			this.respString = respString;
		}
		public String getErrMsg() {
			return errMsg;
		}
		public void setErrMsg(String errMsg) {
			this.errMsg = errMsg;
		}
		public void setException(VolleyError exception) {
			this.exception = exception;
		}
		public Exception getException() {
			return exception;
		}
	}
}
