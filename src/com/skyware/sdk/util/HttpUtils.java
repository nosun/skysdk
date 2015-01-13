package com.skyware.sdk.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 *	整理的HTTP工具类 
 *	包括get、post、put、delete方法以及上传文件
 *
 *	@author wangyf 2014-12-25
 */
public class HttpUtils {

	public enum HttpMethod {
		GET, POST, PUT, DELETE
	}
	
	public static final int HTTP_POLICY_FAST = 0x01;
	public static final int HTTP_POLICY_APACHE = 0x02;
	private static int httpPolicy = HTTP_POLICY_APACHE;
	private static int timeout = 5000;
	private static String charset = "UTF-8";
	
	public static String httpPostFiles(String url,
			List<String> files) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity reqEntity = new MultipartEntity();
		try {
			//if (params != null) {
				// List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				/*for (Entry<String, String> param : params.entrySet()) {
					// formparams.add(new BasicNameValuePair(en.getKey(),
					// en.getValue()));
					reqEntity.addPart(
							param.getKey(),
							new StringBody(param.getValue(), "text/plain", Charset
									.forName(charset)));
				}*/
				for (String file : files) {
					reqEntity.addPart("file", new FileBody(new File(file)));
				}
				httpPost.setEntity(reqEntity);
			//}
			HttpResponse response = httpClient.execute(httpPost);
			// HttpEntity entity = response.getEntity();
			String resultstring = EntityUtils.toString(response.getEntity());
			switch (response.getStatusLine().getStatusCode()) {
			case 200:
				return resultstring;
			default:
				System.out.println("http error: "
						+ response.getStatusLine().getStatusCode());
				break;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 【Apache实现】
	 * 
	 * @param method
	 *            Http方法
	 * @param url
	 *            请求地址
	 * @param params
	 *            Http请求参数
	 * @param encode
	 *            编码格式
	 * @return Web站点响应的Apache实体
	 * @throws IOException 
	 */
	public static HttpResponse httpRequestApache(HttpMethod method, String url,
			Map<String, String> params, String encode, int timeout) throws IOException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		UrlEncodedFormEntity entity = null;
		HttpClient client = null;
		try {
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					// 解析Map传递的参数，使用一个键值对对象BasicNameValuePair保存。
					list.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				// 实现将请求 的参数封装封装到HttpEntity中。
				entity = new UrlEncodedFormEntity(list, encode);
			}
			
			// 设置HTTP请求方式
			HttpRequestBase httpRequest = null;
			switch (method) {
			case GET:
				httpRequest = new HttpGet(url);
				break;
			case POST:
				httpRequest = new HttpPost(url);
				// 设置请求参数到Form中。
				((HttpPost) httpRequest).setEntity(entity);
				break;
			case PUT:
				httpRequest = new HttpPut(url);
				((HttpPut) httpRequest).setEntity(entity);
				break;
			case DELETE:
				httpRequest = new HttpDelete(url);
				break;
			default:
				break;
			}

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout); // 连接超时
			HttpConnectionParams.setSoTimeout(httpParams, timeout); // 响应超时
			httpRequest.setParams(httpParams);
			
			// 实例化一个默认的Http客户端，使用的是AndroidHttpClient
//			client = AndroidHttpClient.newInstance("");
			client = new DefaultHttpClient();
			
			// 执行请求，并获得响应数据
			HttpResponse httpResponse = client.execute(httpRequest);
			// 判断是否请求成功，为200时表示成功，其他均问有问题。
			if (httpResponse != null) {
//				switch (httpResponse.getStatusLine().getStatusCode()) {
//				case 200:
//					// 通过HttpEntity获得响应流
//					InputStream inputStream = httpResponse.getEntity()
//							.getContent();
//					return changeInputStream(inputStream, encode);
//				}
				return httpResponse;
			}
			return null;
			
		} catch (IOException e) {
			throw e;
		} finally {
//			if(client != null && client instanceof AndroidHttpClient) {
//				((AndroidHttpClient)client).close();
//			}
		}
	}

	/**
	 * 【URLConnection 实现】
	 * 
	 * @param params
	 *            请求参数
	 * @param encode
	 *            编码格式
	 * @return 
	 * @throws IOException 
	 */
	public static HttpURLConnection httpRequestUrl(HttpMethod method, String url, 
			Map<String, String> params, String encode, int timeout) throws IOException {

		OutputStream outputStream = null;
		StringBuffer buffer = null;
		byte[] mydata = null;
		
		try {
			URL mUrl = new URL(url);
			HttpURLConnection urlConnection = (HttpURLConnection) mUrl
					.openConnection();
			urlConnection.setConnectTimeout(timeout);
			urlConnection.setReadTimeout(timeout); // 响应超时
			
			// 设置允许输入输出
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			if (params != null && !params.isEmpty()) {
				buffer = new StringBuffer();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					buffer.append(entry.getKey())
							.append("=")
							.append(URLEncoder.encode(entry.getValue(), encode))
							.append("&");// 请求的参数之间使用&分割。
				}
				buffer.deleteCharAt(buffer.length() - 1);
				// System.out.println(buffer.toString());
			}
			
			if (buffer != null) {
				mydata = buffer.toString().getBytes();
				// 设置请求报文头，设定请求数据类型
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// 设置请求数据长度
				urlConnection.setRequestProperty("Content-Length",
						String.valueOf(mydata.length));
			}
			

			// 设置HTTP请求方式
			switch (method) {
			case GET:
				urlConnection.setRequestMethod("GET");
				break;
			case POST:
				urlConnection.setRequestMethod("POST");
				outputStream = urlConnection.getOutputStream();
				outputStream.write(mydata);
				outputStream.flush();
				break;
			case PUT:
				urlConnection.setRequestMethod("PUT");
				outputStream = urlConnection.getOutputStream();
				outputStream.write(mydata);
				outputStream.flush();
				break;
			case DELETE:
				urlConnection.setRequestMethod("DELETE");
				break;
			default:
				break;
			}
			
			return urlConnection;
//			switch (urlConnection.getResponseCode()) {
//			case 200:
//				return changeInputStream(urlConnection.getInputStream(),
//						encode);
//			} 
		} catch (IOException e) {
			throw e;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	/**
	 * 把服务端返回的输入流转换成字符串格式
	 * 
	 * @param inputStream
	 *            服务器返回的输入流
	 * @param encode
	 *            编码格式
	 * @return 解析后的字符串
	 */
	private static String changeInputStream(InputStream inputStream,
			String encode) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), encode);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
	

	public static HttpResult doGet(String url) throws IOException {
		Log.e("url","==="+url);
		HttpResult result = new HttpResult();
		switch (httpPolicy) {
		case HTTP_POLICY_FAST:
			HttpURLConnection connection = httpRequestUrl(HttpMethod.GET, url, null, charset, timeout);
			if (connection != null) {
				result.setStatusCode(connection.getResponseCode());
				result.setRespString(changeInputStream(connection.getInputStream(), charset));
				return result;
			}
		case HTTP_POLICY_APACHE:
			HttpResponse resp = httpRequestApache(HttpMethod.GET, url, null, charset, timeout);
			if (resp != null) {
				result.setStatusCode(resp.getStatusLine().getStatusCode());
				result.setRespString(changeInputStream(resp.getEntity().getContent(), charset));
				return result;
			}
		}
		return null;
	}

	public static HttpResult doPost(String url, Map<String, String> params) throws IOException {
		HttpResult result = new HttpResult();
		switch (httpPolicy) {
		case HTTP_POLICY_FAST:
			HttpURLConnection connection = httpRequestUrl(HttpMethod.POST, url, params, charset, timeout);
			if (connection != null) {
				result.setStatusCode(connection.getResponseCode());
				result.setRespString(changeInputStream(connection.getInputStream(), charset));
				return result;
			}
		case HTTP_POLICY_APACHE:
			HttpResponse resp = httpRequestApache(HttpMethod.POST, url, params, charset, timeout);
			if (resp != null) {
				result.setStatusCode(resp.getStatusLine().getStatusCode());
				result.setRespString(changeInputStream(resp.getEntity().getContent(), charset));
				return result;
			}
		}
		return null;
	}

	public static HttpResult doPut(String url, Map<String, String> params) throws IOException {
		HttpResult result = new HttpResult();
		switch (httpPolicy) {
		case HTTP_POLICY_FAST:
			HttpURLConnection connection = httpRequestUrl(HttpMethod.PUT, url, params, charset, timeout);
			if (connection != null) {
				result.setStatusCode(connection.getResponseCode());
				result.setRespString(changeInputStream(connection.getInputStream(), charset));
				return result;
			}
		case HTTP_POLICY_APACHE:
			HttpResponse resp = httpRequestApache(HttpMethod.PUT, url, params, charset, timeout);
			if (resp != null) {
				result.setStatusCode(resp.getStatusLine().getStatusCode());
				result.setRespString(changeInputStream(resp.getEntity().getContent(), charset));
				return result;
			}
		}
		return null;
	}
	
	public static HttpResult doDelete(String url) throws IOException {
		HttpResult result = new HttpResult();
		switch (httpPolicy) {
		case HTTP_POLICY_FAST:
			HttpURLConnection connection = httpRequestUrl(HttpMethod.DELETE, url, null, charset, timeout);
			if (connection != null) {
				result.setStatusCode(connection.getResponseCode());
				result.setRespString(changeInputStream(connection.getInputStream(), charset));
				return result;
			}
		case HTTP_POLICY_APACHE:
			HttpResponse resp = httpRequestApache(HttpMethod.DELETE, url, null, charset, timeout);
			if (resp != null) {
				result.setStatusCode(resp.getStatusLine().getStatusCode());
				result.setRespString(changeInputStream(resp.getEntity().getContent(), charset));
				return result;
			}
		}
		return null;
	}
	
	
	public static class HttpResult{
		private int statusCode;
		private String respString;
		
		public int getStatusCode() {
			return statusCode;
		}
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		public String getRespString() {
			return respString;
		}
		public void setRespString(String respString) {
			this.respString = respString;
		}
	}
	
	
	
	//liudanhua 写的httpget请求
	public static String getRequest(String url) throws Exception {
		String s = "";
		String tmp = "";
		try {
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = new StringBuilder();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			//Log.i("json_str==", builder.toString());
			//Log.i("url==", url);
			tmp = builder.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("MalformedURLException    " + e.getMessage());

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException    " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception    " + e.getMessage());

		}
		return tmp;
	}
}
