/*package com.skyware.sdk.test.ui;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyware.sdk.R;
import com.skyware.sdk.api.SDKConfig;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.DeviceInfo.DevType;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.entity.biz.CmdInfoAirPurifier;
import com.skyware.sdk.entity.biz.CmdInfoPlugin;
import com.skyware.sdk.entity.biz.DevDataBroadlink;
import com.skyware.sdk.entity.biz.DevDataHezhong;
import com.skyware.sdk.entity.biz.DevDataHezhong.MODE;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;

public class TestActivity extends Activity {

	// private Button discoverButton;
	// private Button stopButton;
	// private Button onButton1;
	// private Button onButton2;
	// private Button offButton1;
	// private Button offButton2;
//	private Button connButton;
//	private Button disconnButton;
	private Button pluginPowerOnButton;
	private Button pluginPowerOffButton;
	private TextView resultTextView;
	private TextView devInfoTextView;
	private TextView devFilterTextView;
	private TextView devFilterLevelTextView;
	private TextView devTempTextView;
	private TextView devHumTextView;
	private TextView devPmTextView;
	private TextView devPmLevelTextView;
	private TextView devNetStatTv;
	
	private RadioButton powerOnRadioButton;
	private RadioButton powerOffRadioButton;
	private RadioButton childOnLockRadioButton;
	private RadioButton childOffLockRadioButton;
	private RadioButton uvOnRadioButton;
	private RadioButton uvOffRadioButton;
	private RadioButton anionOnRadioButton;
	private RadioButton anionOffRadioButton;

	private RadioGroup modeRadioGroup;
	private RadioGroup timerRadioGroup;
	private RadioGroup fanSpeedRadioGroup;

	private RadioButton modeManualRadioButton;
	private RadioButton modeAutoRadioButton;
	private RadioButton modeSleepRadioButton;
	
	private RadioButton timer0RadioButton;
	private RadioButton timer1RadioButton;
	private RadioButton timer2RadioButton;
	private RadioButton timer3RadioButton;
	private RadioButton timer4RadioButton;
	private RadioButton timer5RadioButton;
	private RadioButton timer6RadioButton;
	private RadioButton timer7RadioButton;
	private RadioButton timer8RadioButton;
	private RadioButton timer9RadioButton;
	private RadioButton timer10RadioButton;
	private RadioButton timer11RadioButton;
	private RadioButton timer12RadioButton;

	private RadioButton fanspeed1RadioButton;
	private RadioButton fanspeed2RadioButton;
	private RadioButton fanspeed3RadioButton;
	private RadioButton fanspeed4RadioButton;
	private RadioButton fanspeed5RadioButton;

	private View layoutDevAirPurifier;
	private View layoutDevPlugin;
	private ScrollView resultScrollView;

	// private View layoutDev2;

	public int getSn() {
		return new Random().nextInt(SocketConst.SN_MAX);
	}

	// private int deviceCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_airpal);
		// layoutDev1 = getLayoutInflater().inflate(R.id.layout_dev1,null);
		// layoutDev2 = getLayoutInflater().inflate(R.id.layout_dev2,null);
		layoutDevAirPurifier = (View) findViewById(R.id.layout_dev_airpurifier);
		layoutDevAirPurifier.setVisibility(View.GONE);
		layoutDevPlugin = (View) findViewById(R.id.layout_dev_plugin);
		layoutDevPlugin.setVisibility(View.GONE);

		resultTextView = (TextView) findViewById(R.id.tv_result);
		// resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		resultScrollView = (ScrollView) findViewById(R.id.sv_result);

		devInfoTextView = (TextView) findViewById(R.id.tv_dev_info);
		devFilterTextView = (TextView) findViewById(R.id.tv_dev_filter_value);
		devFilterLevelTextView = (TextView) findViewById(R.id.tv_dev_filter_level_value);
		devTempTextView = (TextView) findViewById(R.id.tv_dev_temp_value);
		devHumTextView = (TextView) findViewById(R.id.tv_dev_hum_value);
		devPmTextView = (TextView) findViewById(R.id.tv_dev_pm_value);
		devPmLevelTextView = (TextView) findViewById(R.id.tv_dev_pm_level_value);

//		connButton = (Button) findViewById(R.id.btn_start);
//		disconnButton = (Button) findViewById(R.id.btn_stop);
		devNetStatTv = (TextView) findViewById(R.id.tv_dev_net_status);
		
		powerOnRadioButton = (RadioButton) findViewById(R.id.rb_power_on);
		powerOffRadioButton = (RadioButton) findViewById(R.id.rb_power_off);
		childOnLockRadioButton = (RadioButton) findViewById(R.id.rb_childlock_on);
		childOffLockRadioButton = (RadioButton) findViewById(R.id.rb_childlock_off);
		uvOnRadioButton = (RadioButton) findViewById(R.id.rb_uv_on);
		uvOffRadioButton = (RadioButton) findViewById(R.id.rb_uv_off);
		anionOnRadioButton = (RadioButton) findViewById(R.id.rb_anion_on);
		anionOffRadioButton = (RadioButton) findViewById(R.id.rb_anion_off);
		
		pluginPowerOnButton = (Button) findViewById(R.id.btn_power_on_plugin);
		pluginPowerOffButton = (Button) findViewById(R.id.btn_power_off_plugin);

		modeRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_mode);
		timerRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_timer);
		fanSpeedRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_fanspeed);

		modeManualRadioButton = (RadioButton) findViewById(R.id.radio_mode_manual);
		modeAutoRadioButton = (RadioButton) findViewById(R.id.radio_mode_auto);
		modeSleepRadioButton = (RadioButton) findViewById(R.id.radio_mode_sleep);
		
		timer0RadioButton = (RadioButton) findViewById(R.id.radio_timer0);
		timer1RadioButton = (RadioButton) findViewById(R.id.radio_timer1);
		timer2RadioButton = (RadioButton) findViewById(R.id.radio_timer2);
		timer3RadioButton = (RadioButton) findViewById(R.id.radio_timer3);
		timer4RadioButton = (RadioButton) findViewById(R.id.radio_timer4);
		timer5RadioButton = (RadioButton) findViewById(R.id.radio_timer5);
		timer6RadioButton = (RadioButton) findViewById(R.id.radio_timer6);
		timer7RadioButton = (RadioButton) findViewById(R.id.radio_timer7);
		timer8RadioButton = (RadioButton) findViewById(R.id.radio_timer8);
		timer9RadioButton = (RadioButton) findViewById(R.id.radio_timer9);
		timer10RadioButton = (RadioButton) findViewById(R.id.radio_timer10);
		timer11RadioButton = (RadioButton) findViewById(R.id.radio_timer11);
		timer12RadioButton = (RadioButton) findViewById(R.id.radio_timer12);

		fanspeed1RadioButton = (RadioButton) findViewById(R.id.radio_fanspeed1);
		fanspeed2RadioButton = (RadioButton) findViewById(R.id.radio_fanspeed2);
		fanspeed3RadioButton = (RadioButton) findViewById(R.id.radio_fanspeed3);
		fanspeed4RadioButton = (RadioButton) findViewById(R.id.radio_fanspeed4);
		fanspeed5RadioButton = (RadioButton) findViewById(R.id.radio_fanspeed5);

		// discoverButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Intent intent = new Intent(MainActivity.this, SDKService.class);
		// // intent.putExtra(EXTRA_CMD, CMD_DISCOVER_START);
		// // startService(intent);
		// SkySDK.startDiscoverDevice();
		// }
		// });
		//
		// stopButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Intent intent = new Intent(MainActivity.this, SDKService.class);
		// // intent.putExtra(EXTRA_CMD, CMD_DISCOVER_STOP);
		// // startService(intent);
		// SkySDK.stopDiscoverDevice();
		// }
		// });
		//
		// connButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // String [] cmd = {"power::0"};
		// CmdInfo cmd = new CmdInfo();
		// cmd.setPowerOn();
		// SkySDK.sendCmdToDevice((String)devMacTextView.getTag(), cmd,
		// getSn());
		// }
		// });
		//
		//
		// offButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// CmdInfo cmd = new CmdInfo();
		// cmd.setPowerOff();
		// SkySDK.sendCmdToDevice((String)devMacTextView.getTag(), cmd,
		// getSn());
		// }
		// });
//		powerSwitch.setClickable(false);
//		childLockSwitch.setClickable(false);
//		uvSwitch.setClickable(false);
//		anionSwitch.setClickable(false);

//		connButton.setOnClickListener(clickListener);
//		disconnButton.setOnClickListener(clickListener);

		pluginPowerOnButton.setOnClickListener(clickListener);
		pluginPowerOffButton.setOnClickListener(clickListener);

		powerOnRadioButton.setOnClickListener(clickListener);
		powerOffRadioButton.setOnClickListener(clickListener);
		childOnLockRadioButton.setOnClickListener(clickListener);
		childOffLockRadioButton.setOnClickListener(clickListener);
		uvOnRadioButton.setOnClickListener(clickListener);
		uvOffRadioButton.setOnClickListener(clickListener);
		anionOnRadioButton.setOnClickListener(clickListener);
		anionOffRadioButton.setOnClickListener(clickListener);

		modeRadioGroup.setClickable(false);

		modeManualRadioButton.setOnClickListener(clickListener);
		modeAutoRadioButton.setOnClickListener(clickListener);
		modeSleepRadioButton.setOnClickListener(clickListener);
		
		timerRadioGroup.setClickable(false);

		timer0RadioButton.setOnClickListener(clickListener);
		timer1RadioButton.setOnClickListener(clickListener);
		timer2RadioButton.setOnClickListener(clickListener);
		timer3RadioButton.setOnClickListener(clickListener);
		timer4RadioButton.setOnClickListener(clickListener);
		timer5RadioButton.setOnClickListener(clickListener);
		timer6RadioButton.setOnClickListener(clickListener);
		timer7RadioButton.setOnClickListener(clickListener);
		timer8RadioButton.setOnClickListener(clickListener);
		timer9RadioButton.setOnClickListener(clickListener);
		timer10RadioButton.setOnClickListener(clickListener);
		timer11RadioButton.setOnClickListener(clickListener);
		timer12RadioButton.setOnClickListener(clickListener);

		fanSpeedRadioGroup.setClickable(false);

		fanspeed1RadioButton.setOnClickListener(clickListener);
		fanspeed2RadioButton.setOnClickListener(clickListener);
		fanspeed3RadioButton.setOnClickListener(clickListener);
		fanspeed4RadioButton.setOnClickListener(clickListener);
		fanspeed5RadioButton.setOnClickListener(clickListener);
		
		SDKConfig config = new SDKConfig();
		config.setUserId("568912921").setIsLogin(true);
		
		SkySDK.startSDK(getApplication(), new SDKCallback(), config);
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		
		SkySDK.resumeSDK(getApplicationContext());
		
//		String devShow = info.getIp() + "   " + info.getKey();
//		// deviceCount++;

//		if (info.getDevType() != null
//				&& info.getDevType() == DevType.AIR_PURIFIER) {
		layoutDevAirPurifier.setVisibility(View.VISIBLE);
		devInfoTextView.setTag("ACCF232C6F26");
		
//			powerSwitch.setChecked(false);
//			childLockSwitch.setChecked(false);
//			uvSwitch.setChecked(false);
//			anionSwitch.setChecked(false);

//		} else if (info.getDevType() != null
//				&& info.getDevType() == DevType.PLUGIN) {
//			layoutDevPlugin.setVisibility(View.VISIBLE);
//
//		}

//		devInfoTextView.setText(devShow);
//		devInfoTextView.setTag(info.getKey());
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		SkySDK.pauseSDK();
	}
	
	@Override
	protected void onDestroy() {
		
		SkySDK.stopSDK();
		
		layoutDevAirPurifier.setVisibility(View.GONE);
		// layoutDev2.setVisibility(View.GONE);

		// deviceCount = 0;

//		powerSwitch.setChecked(false);
//		childLockSwitch.setChecked(false);
//		uvSwitch.setChecked(false);
//		anionSwitch.setChecked(false);
	}
	
	
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			CmdInfoAirPurifier cmd = new CmdInfoAirPurifier();
			switch (v.getId()) {
//			case R.id.btn_start:
//				SkySDK.startConnectDevice((String) devInfoTextView.getTag());
//				break;
//			case R.id.btn_stop:
//				SkySDK.stopConnectDevice((String) devInfoTextView.getTag());
//				break;
			case R.id.btn_power_on_plugin:
				CmdInfoPlugin cmd2 = new CmdInfoPlugin();
				cmd2.setPowerOn();
				SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(), cmd2,
						getSn());
				break;
			case R.id.btn_power_off_plugin:
				CmdInfoPlugin cmd3 = new CmdInfoPlugin();
				cmd3.setPowerOff();
				SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(), cmd3,
						getSn());
				break;
			case R.id.rb_power_off:
				if (((CompoundButton) v).isChecked()) {
					cmd.setPowerOff();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.rb_power_on:
				if (((CompoundButton) v).isChecked()) {
					cmd.setPowerOn();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.rb_childlock_off:
				if (((CompoundButton) v).isChecked()) {
					cmd.setChildLockOff();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				} 
				break;
			case R.id.rb_childlock_on:
				if (((CompoundButton) v).isChecked()) {
					cmd.setChildLockOn();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.rb_uv_off:
				if (((CompoundButton) v).isChecked()) {
					cmd.setUvOff();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.rb_uv_on:
				if (((CompoundButton) v).isChecked()) {
					cmd.setUvOn();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.rb_anion_off:
				if (((CompoundButton) v).isChecked()) {
					cmd.setAnionOff();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				} 
				break;
			case R.id.rb_anion_on:
				if (((CompoundButton) v).isChecked()) {
					cmd.setAnionOn();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_mode_manual:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(MODE.MANUAL);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_mode_auto:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(MODE.AUTO);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_mode_sleep:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(MODE.SLEEP);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer0:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(0);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer1:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(1);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer2:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(2);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer3:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(3);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer4:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(4);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer5:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(5);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer6:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(6);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer7:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(7);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer8:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(8);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer9:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(9);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer10:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(10);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer11:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(11);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer12:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setTimer(12);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_fanspeed1:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setFanSpeed(1);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_fanspeed2:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setFanSpeed(2);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_fanspeed3:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setFanSpeed(3);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_fanspeed4:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setFanSpeed(4);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_fanspeed5:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setFanSpeed(5);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			default:
				break;
			}
		}
	};



	
//	@Override protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//
//	 	resultTextView.setText(resultTextView.getText() + "\n" +
//	 	intent.getStringExtra(SDKConst.EXTRA_NAME_DISCOVER)); 
//	 }
//	 
	
	
	private class SDKCallback extends SkySDK.Callback {

		@Override
		@Deprecated
		public void onBroadcastReceive(String data) {
			// resultTextView.setText(resultTextView.getText() + "\n" + data);
		}

		@Override
		@Deprecated
		public void onTCPReceive(String data) {
			// resultTextView.setText(resultTextView.getText() + "\n" + data);
			resultTextView.append("\n" + data);
			scrollToBottom(resultScrollView, resultTextView);
		}

		@Override
		@Deprecated
		public void onTCPSend(String data) {
			// resultTextView.setText(resultTextView.getText() + "\n" + data);
			resultTextView.append("\n" + data);
			scrollToBottom(resultScrollView, resultTextView);
		}

		@Override
		public void onDiscoverNewDevice(DeviceInfo info) {
			String devShow = info.getIp() + "   " + info.getMac();
			// deviceCount++;

			if (info.getDevType() != null
					&& info.getDevType() == DevType.AIR_PURIFIER) {
				layoutDevAirPurifier.setVisibility(View.VISIBLE);

//				powerSwitch.setChecked(false);
//				childLockSwitch.setChecked(false);
//				uvSwitch.setChecked(false);
//				anionSwitch.setChecked(false);

			} else if (info.getDevType() != null
					&& info.getDevType() == DevType.PLUGIN) {
				layoutDevPlugin.setVisibility(View.VISIBLE);

			}

			devInfoTextView.setText(devShow);
			devInfoTextView.setTag(info.getMac());
		}
		
		@Override
		public void onDevNetStatChange(DeviceInfo info) {
			String mac = info.getMac();
			
			if (mac.equals(devInfoTextView.getTag())) {
				if(!info.isAccess()){
					devNetStatTv.setText(devNetStatTv.getText() + "离线");
				} else {
					if (info.isLocalOnline() && info.isRemoteOnline()) {
						devNetStatTv.setText(devNetStatTv.getText() + "远程在线 | 局域网在线");
					} else if (info.isLocalOnline() && !info.isRemoteOnline()) {
						devNetStatTv.setText(devNetStatTv.getText() + "远程离线 | 局域网在线");
					} else if (!info.isLocalOnline() && info.isRemoteOnline()) {
						devNetStatTv.setText(devNetStatTv.getText() + "远程在线 | 局域网离线");
					}
				}
				
			}
			
		}

//		@Override
//		public void onConnectDeviceResult(String mac, boolean success,
//				ErrorInfo errorInfo) {
//			if (success) {
//				if (mac.equals(devInfoTextView.getTag())) {
//					connButton.setEnabled(false);
//					disconnButton.setEnabled(true);
//					Toast.makeText(TestActivity.this, "设备MAC:" + mac + "已连接！",
//							Toast.LENGTH_SHORT).show();
//				}
//
//			} else {
//				if (mac.equals(devInfoTextView.getTag())) {
//					connButton.setEnabled(true);
//					disconnButton.setEnabled(false);
//					Toast.makeText(TestActivity.this, "设备MAC:" + mac + "连接失败！",
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//		}

//		@Override
//		public void onDeviceDisconnected(String mac, ErrorInfo errorInfo) {
//			if (mac.equals(devInfoTextView.getTag())) {
//				connButton.setEnabled(true);
//				disconnButton.setEnabled(false);
//				Toast.makeText(TestActivity.this, "设备MAC:" + mac + "已断线！",
//						Toast.LENGTH_SHORT).show();
//			}
//		}

		@Override
		public void onSendDevCmdResult(String mac, int sn, boolean success,
				ErrorInfo errorInfo) {
			if (success) {
				Toast.makeText(TestActivity.this,
						"发送命令成功, mac: " + mac + " ,sn:" + sn,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TestActivity.this, "发送失败", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onRecvDevStatus(String json) {
			 Log.e("JSON", json + "");
		}

		@Override
		@Deprecated
		public void onRecvDevStatus(DevStatus status) {
			String mac = status.getKey();
			
			//如果是合众的协议
			if (status.getDevData() instanceof DevDataHezhong) {
				DevDataHezhong devData = (DevDataHezhong) status.getDevData();

				String power = devData.getPower();
				String childLock = devData.getChildLock();
				String anion = devData.getAnion();
				String uv = devData.getUv();
				String mode = devData.getMode();
				String timer = devData.getTimer();
				String fanSpeed = devData.getFanSpeed();
				
				String filter = devData.getFilterRemainTime();
				String filterLevel = devData.getFilterRemainLevel();
				String temp = devData.getTemperature();
				String hum = devData.getHumidity();
				String pm25 = devData.getPm25();
				String pm25Level = devData.getPm25Level();
				
				if (mac.equals(devInfoTextView.getTag())) {
					if (power != null && !power.equals("")) {
						if (power.equals(DevDataHezhong.VALUE_ON_STATE)) {
							powerOnRadioButton.setChecked(true);
						} else if (power.equals(DevDataHezhong.VALUE_OFF_STATE)) {
							powerOffRadioButton.setChecked(true);
						}
					}
					if (childLock != null && !childLock.equals("")) {
						if (childLock.equals(DevDataHezhong.VALUE_ON_STATE)) {
							childOnLockRadioButton.setChecked(true);
						} else if (childLock
								.equals(DevDataHezhong.VALUE_OFF_STATE)) {
							childOffLockRadioButton.setChecked(true);
						}
					}
					if (anion != null && !anion.equals("")) {
						if (anion.equals(DevDataHezhong.VALUE_ON_STATE)) {
							anionOnRadioButton.setChecked(true);
						} else if (anion.equals(DevDataHezhong.VALUE_OFF_STATE)) {
							anionOffRadioButton.setChecked(true);
						}
					}
					if (uv != null && !uv.equals("")) {
						if (uv.equals(DevDataHezhong.VALUE_ON_STATE)) {
							uvOnRadioButton.setChecked(true);
						} else if (uv.equals(DevDataHezhong.VALUE_OFF_STATE)) {
							uvOffRadioButton.setChecked(true);
						}
					}
					if (mode != null && !mode.equals("")) {
						if (mode.equals("0")){
							modeManualRadioButton.setChecked(true);
						} else if (mode.equals("1")) {
							modeAutoRadioButton.setChecked(true);
						} else if (mode.equals("2")) {
							modeSleepRadioButton.setChecked(true);
						}
					}

					if (timer != null && !timer.equals("")) {
						if (timer.equals("00")) {
							timer0RadioButton.setChecked(true);
						} else if (timer.equals("01")) {
							timer1RadioButton.setChecked(true);
						} else if (timer.equals("02")) {
							timer2RadioButton.setChecked(true);
						} else if (timer.equals("03")) {
							timer3RadioButton.setChecked(true);
						} else if (timer.equals("04")) {
							timer4RadioButton.setChecked(true);
						} else if (timer.equals("05")) {
							timer5RadioButton.setChecked(true);
						} else if (timer.equals("06")) {
							timer6RadioButton.setChecked(true);
						} else if (timer.equals("07")) {
							timer7RadioButton.setChecked(true);
						} else if (timer.equals("08")) {
							timer8RadioButton.setChecked(true);
						} else if (timer.equals("09")) {
							timer9RadioButton.setChecked(true);
						} else if (timer.equals("10")) {
							timer10RadioButton.setChecked(true);
						} else if (timer.equals("11")) {
							timer11RadioButton.setChecked(true);
						} else if (timer.equals("12")) {
							timer12RadioButton.setChecked(true);
						}
					}
					if (fanSpeed != null && !fanSpeed.equals("")) {
						if (fanSpeed.equals("1")) {
							fanspeed1RadioButton.setChecked(true);
						} else if (fanSpeed.equals("2")) {
							fanspeed2RadioButton.setChecked(true);
						} else if (fanSpeed.equals("3")) {
							fanspeed3RadioButton.setChecked(true);
						} else if (fanSpeed.equals("4")) {
							fanspeed4RadioButton.setChecked(true);
						} else if (fanSpeed.equals("5")) {
							fanspeed5RadioButton.setChecked(true);
						}
					}

					if (filter != null && !filter.equals("")) {
						devFilterTextView.setText(filter);
					}
					if (filterLevel != null && !filterLevel.equals("")) {
						devFilterLevelTextView.setText(filterLevel);
					}
					if (temp != null && !temp.equals("")) {
						devTempTextView.setText(temp);
					}
					if (hum != null && !hum.equals("")) {
						devHumTextView.setText(hum);
					}
					if (pm25 != null && !pm25.equals("")) {
						devPmTextView.setText(pm25);
					}
					if (pm25Level != null && !pm25Level.equals("")) {
						devPmLevelTextView.setText(pm25Level);
					}
				}
			} else if (status.getDevData() instanceof DevDataBroadlink) {
				//TODO 改变UI
				
			}
		}

	
	}

	public static void scrollToBottom(final View scroll, final View inner) {
		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}
}
*/