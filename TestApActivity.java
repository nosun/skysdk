package com.skyware.sdk.test.ui;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skyware.sdk.R;
import com.skyware.sdk.api.SDKConfig;
import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.entity.biz.DevDataGreen;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;

public class TestApActivity extends Activity {

	private TextView pm25mTextView, pm10mTextView, pm25hTextView, pm10hTextView, 
				pm25dTextView, pm10dTextView;
	private TextView tempTextView, humTextView, co2TextView, vocTextView;
	private ToggleButton switchWifiBtn;
	// private View layoutDev2;

	public int getSn() {
		return new Random().nextInt(SocketConst.SN_MAX);
	}

	// private int deviceCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_monitor);
		
		pm25mTextView = (TextView) findViewById(R.id.tv_monitor_pm2_5_m);
		pm10mTextView = (TextView) findViewById(R.id.tv_monitor_pm10_m);
		pm25hTextView = (TextView) findViewById(R.id.tv_monitor_pm2_5_h);
		pm10hTextView = (TextView) findViewById(R.id.tv_monitor_pm10_h);
		pm25dTextView = (TextView) findViewById(R.id.tv_monitor_pm2_5_d);
		pm10dTextView = (TextView) findViewById(R.id.tv_monitor_pm10_d);
		tempTextView = (TextView) findViewById(R.id.tv_monitor_temp);
		humTextView = (TextView) findViewById(R.id.tv_monitor_hum);
		co2TextView = (TextView) findViewById(R.id.tv_monitor_co2);
		vocTextView = (TextView) findViewById(R.id.tv_monitor_voc);
		
		switchWifiBtn = (ToggleButton) findViewById(R.id.btn_switch_wifi);
		switchWifiBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundbutton, boolean isCheck) {
				if (isCheck) {
					SkySDK.startConnectAp();
				} else {
					SkySDK.stopConnectAp();
				}
			}
		});
	}


	@Override
	protected void onStart() {
		super.onStart();
		SDKConfig config = new SDKConfig();
		config.setApMode(true)
			.setApSSID(SDKConst.PROTOCOL_GREEN_WIFI_SSID)
			.setApPasswd(null)
			.setApIp(SDKConst.PROTOCOL_GREEN_WIFI_AP_IP);
//			.setApPort(SDKConst.PROTOCOL_GREEN_WIFI_AP_PORT);
		
		SkySDK.startSDK(getApplication(), new SDKCallbackAP(), config);
		SkySDK.startConnectAp();
	}

	@Override
	protected void onStop() {
		super.onStop();
		SkySDK.stopConnectAp();
		SkySDK.stopSDK();
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
				pm25mTextView.setText("minute "+ data.getPm2_5AvgMin() + "μg/m3");
				pm10mTextView.setText("minute "+ data.getPm10AvgMin() + "μg/m3");
				pm25hTextView.setText("hour "+ data.getPm2_5AvgHour() + "μg/m3");
				pm10hTextView.setText("hour "+ data.getPm10AvgHour() + "μg/m3");
				pm25dTextView.setText("day "+ data.getPm2_5AvgDay() + "μg/m3");
				pm10dTextView.setText("day "+ data.getPm10AvgDay() + "μg/m3");
				tempTextView.setText("温度 "+ data.getTemperature() + "℃");
				humTextView.setText("湿度 "+ data.getHumidity() + "RH%");
				co2TextView.setText("CO2 "+ data.getCo2() + "ppm");
				vocTextView.setText("VOC "+ data.getVoc() + "μg/m3");
			}
			
		}
	}
	
}
