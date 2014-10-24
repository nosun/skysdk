package com.skyware.sdk.test.ui;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.skyware.sdk.R;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.entity.SDKConfig;
import com.skyware.sdk.entity.biz.DevDataGreen;
import com.skyware.sdk.manage.SkySDK;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;

public class TestMonitorActivity extends Activity {

	private TextView pm25TextView;
	private TextView pm10TextView;
	private TextView tempTextView;
	private TextView humTextView;
	private TextView co2TextView;
	private TextView vocTextView;

	// private View layoutDev2;

	public int getSn() {
		return new Random().nextInt(SocketConst.SN_MAX);
	}

	// private int deviceCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_monitor);
		
		pm25TextView = (TextView) findViewById(R.id.tv_monitor_pm2_5);
		pm10TextView = (TextView) findViewById(R.id.tv_monitor_pm10);
		tempTextView = (TextView) findViewById(R.id.tv_monitor_temp);
		humTextView = (TextView) findViewById(R.id.tv_monitor_hum);
		co2TextView = (TextView) findViewById(R.id.tv_monitor_co2);
		vocTextView = (TextView) findViewById(R.id.tv_monitor_voc);
	}

	@Override
	protected void onStop() {
		super.onStop();

		SkySDK.stopSDK();
	}

	@Override
	protected void onStart() {
		super.onStart();

		SDKConfig config = new SDKConfig();
		config.setApMode(true);
		
		SkySDK.startSDK(getApplication(), new SDKCallbackAP(), config);
	}

	/*
	 * @Override protected void onNewIntent(Intent intent) {
	 * super.onNewIntent(intent);
	 * 
	 * resultTextView.setText(resultTextView.getText() + "\n" +
	 * intent.getStringExtra(SDKConst.EXTRA_NAME_DISCOVER)); }
	 */

	private class SDKCallbackAP extends SkySDK.CallbackAP {

		@Override
		public void onConnectApResult(boolean success, ErrorInfo errorInfo) {
			Log.e("CallbackAP","onConnectApResult: " + success);
		}

		@Override
		public void onApDisconnected(ErrorInfo errorInfo) {
			Log.e("CallbackAP","onApDisconnected: " + errorInfo);
		}

		@Override
		public void onRecvDevStatus(String json) {
//			Log.e("CallbackAP","onRecvDevStatus: " + json);
		}
		
		@Override
		public void onRecvDevStatus(DevStatus status) {
//			Log.e("CallbackAP","onRecvDevStatus: " + status);
			if (status.getDevData() != null && status.getDevData() instanceof DevDataGreen) {
				DevDataGreen data = (DevDataGreen)status.getDevData();
				pm25TextView.setText("PM2.5 "+ data.getPm2_5() + "μg/m3");
				pm10TextView.setText("PM10 "+ data.getPm10() + "μg/m3");
				tempTextView.setText("温度 "+ data.getTemperature() + "℃");
				humTextView.setText("湿度 "+ data.getHumidity() + "RH%");
				co2TextView.setText("CO2 "+ data.getCo2() + "ppm");
				vocTextView.setText("VOC "+ data.getVoc() + "μg/m3");
			}
			
		}
	}
	
}
