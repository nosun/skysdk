/*package com.skyware.sdk.test.ui;

import java.util.Random;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyware.sdk.R;
import com.skyware.sdk.api.SDKConfig;
import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.DeviceInfo.DevType;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.entity.biz.CmdInfoAirPurifier;
import com.skyware.sdk.entity.biz.DevDataBroadlink;
import com.skyware.sdk.entity.biz.DevDataCair;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;

public class TestCairActivity extends Activity {

	// private Button discoverButton;
	// private Button stopButton;
	// private Button onButton1;
	// private Button onButton2;
	// private Button offButton1;
	// private Button offButton2;
	private Button connButton;
	private Button disconnButton;
	private Button loginButton;
	private Button checkButton;
//	private Button pluginPowerOnButton;
//	private Button pluginPowerOffButton;
	private TextView resultTextView;
	private TextView devInfoTextView;
	private TextView devFilterTextView;
//	private TextView devFilterLevelTextView;
//	private TextView devTempTextView;
//	private TextView devHumTextView;
//	private TextView devPmTextView;
	private TextView devPmLevelTextView;
	private TextView devNetStatTv;
	
	private RadioButton powerOnRadioButton;
	private RadioButton powerOffRadioButton;
	private RadioButton lightOnRadioButton;
	private RadioButton lightOffRadioButton;

	private RadioGroup modeRadioGroup;
	private RadioGroup timerOnRadioGroup;
	private RadioGroup timerOffRadioGroup;
	private RadioGroup fanSpeedRadioGroup;

//	private RadioButton modeManualRadioButton;
//	private RadioButton modeQuietRadioButton;
//	private RadioButton modeWorkRadioButton;
//	private RadioButton modeFastRadioButton;
//	private RadioButton modeAutoRadioButton;
	
	private RadioButton timerOn0RadioButton;
	private RadioButton timerOn1RadioButton;
	private RadioButton timerOn2RadioButton;
	private RadioButton timerOn3RadioButton;
	private RadioButton timerOn4RadioButton;
	private RadioButton timerOn5RadioButton;
	private RadioButton timerOn6RadioButton;
	private RadioButton timerOn7RadioButton;
	private RadioButton timerOn8RadioButton;
	private RadioButton timerOn9RadioButton;
	private RadioButton timerOn10RadioButton;
	private RadioButton timerOn11RadioButton;
	private RadioButton timerOn12RadioButton;

	private RadioButton timerOff0RadioButton;
	private RadioButton timerOff1RadioButton;
	private RadioButton timerOff2RadioButton;
	private RadioButton timerOff3RadioButton;
	private RadioButton timerOff4RadioButton;
	private RadioButton timerOff5RadioButton;
	private RadioButton timerOff6RadioButton;
	private RadioButton timerOff7RadioButton;
	private RadioButton timerOff8RadioButton;
	private RadioButton timerOff9RadioButton;
	private RadioButton timerOff10RadioButton;
	private RadioButton timerOff11RadioButton;
	private RadioButton timerOff12RadioButton;
	
	private RadioButton fanspeed1RadioButton;
	private RadioButton fanspeed2RadioButton;
	private RadioButton fanspeed3RadioButton;
	private RadioButton fanspeed4RadioButton;
	private RadioButton fanspeed5RadioButton;

	private View layoutDevAirPurifier;
	private View layoutDevPlugin;
	private ScrollView resultScrollView;
	
	MyDialogFragment mDialogFragment;
	// private View layoutDev2;

	public int getSn() {
		return new Random().nextInt(SocketConst.SN_MAX);
	}

	// private int deviceCount = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_cair);
		// layoutDev1 = getLayoutInflater().inflate(R.id.layout_dev1,null);
		// layoutDev2 = getLayoutInflater().inflate(R.id.layout_dev2,null);
		layoutDevAirPurifier = (View) findViewById(R.id.layout_dev_airpurifier);
		layoutDevAirPurifier.setVisibility(View.GONE);
//		layoutDevPlugin = (View) findViewById(R.id.layout_dev_plugin);
//		layoutDevPlugin.setVisibility(View.GONE);

		resultTextView = (TextView) findViewById(R.id.tv_result);
		resultTextView.setOnClickListener(clickListener);
		// resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		resultScrollView = (ScrollView) findViewById(R.id.sv_result);

		devInfoTextView = (TextView) findViewById(R.id.tv_dev_info);
		devFilterTextView = (TextView) findViewById(R.id.tv_dev_filter_value);
//		devFilterLevelTextView = (TextView) findViewById(R.id.tv_dev_filter_level_value);
//		devTempTextView = (TextView) findViewById(R.id.tv_dev_temp_value);
//		devHumTextView = (TextView) findViewById(R.id.tv_dev_hum_value);
//		devPmTextView = (TextView) findViewById(R.id.tv_dev_pm_value);
		devPmLevelTextView = (TextView) findViewById(R.id.tv_dev_pm_level_value);

		connButton = (Button) findViewById(R.id.btn_start);
		disconnButton = (Button) findViewById(R.id.btn_stop);
		loginButton = (Button) findViewById(R.id.btn_login);
		checkButton = (Button) findViewById(R.id.btn_check);
		
		devNetStatTv = (TextView) findViewById(R.id.tv_dev_net_status);
		
		powerOnRadioButton = (RadioButton) findViewById(R.id.rb_power_on);
		powerOffRadioButton = (RadioButton) findViewById(R.id.rb_power_off);
		lightOnRadioButton = (RadioButton) findViewById(R.id.rb_colorscreen_on);
		lightOffRadioButton = (RadioButton) findViewById(R.id.rb_colorscreen_off);
		
//		pluginPowerOnButton = (Button) findViewById(R.id.btn_power_on_plugin);
//		pluginPowerOffButton = (Button) findViewById(R.id.btn_power_off_plugin);

		modeRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_mode);
		timerOnRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_timer_on);
		timerOffRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_timer_off);
		fanSpeedRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_fanspeed);

//		modeManualRadioButton = (RadioButton) findViewById(R.id.radio_mode_manual);
//		modeQuietRadioButton = (RadioButton) findViewById(R.id.radio_mode_quiet);
//		modeWorkRadioButton = (RadioButton) findViewById(R.id.radio_mode_work);
//		modeFastRadioButton = (RadioButton) findViewById(R.id.radio_mode_fast);
//		modeAutoRadioButton = (RadioButton) findViewById(R.id.radio_mode_auto);
		
		timerOn0RadioButton = (RadioButton) findViewById(R.id.radio_timer0_on);
		timerOn1RadioButton = (RadioButton) findViewById(R.id.radio_timer1_on);
		timerOn2RadioButton = (RadioButton) findViewById(R.id.radio_timer2_on);
		timerOn3RadioButton = (RadioButton) findViewById(R.id.radio_timer3_on);
		timerOn4RadioButton = (RadioButton) findViewById(R.id.radio_timer4_on);
		timerOn5RadioButton = (RadioButton) findViewById(R.id.radio_timer5_on);
		timerOn6RadioButton = (RadioButton) findViewById(R.id.radio_timer6_on);
		timerOn7RadioButton = (RadioButton) findViewById(R.id.radio_timer7_on);
		timerOn8RadioButton = (RadioButton) findViewById(R.id.radio_timer8_on);
		timerOn9RadioButton = (RadioButton) findViewById(R.id.radio_timer9_on);
		timerOn10RadioButton = (RadioButton) findViewById(R.id.radio_timer10_on);
		timerOn11RadioButton = (RadioButton) findViewById(R.id.radio_timer11_on);
		timerOn12RadioButton = (RadioButton) findViewById(R.id.radio_timer12_on);
		
		timerOff0RadioButton = (RadioButton) findViewById(R.id.radio_timer0_off);
		timerOff1RadioButton = (RadioButton) findViewById(R.id.radio_timer1_off);
		timerOff2RadioButton = (RadioButton) findViewById(R.id.radio_timer2_off);
		timerOff3RadioButton = (RadioButton) findViewById(R.id.radio_timer3_off);
		timerOff4RadioButton = (RadioButton) findViewById(R.id.radio_timer4_off);
		timerOff5RadioButton = (RadioButton) findViewById(R.id.radio_timer5_off);
		timerOff6RadioButton = (RadioButton) findViewById(R.id.radio_timer6_off);
		timerOff7RadioButton = (RadioButton) findViewById(R.id.radio_timer7_off);
		timerOff8RadioButton = (RadioButton) findViewById(R.id.radio_timer8_off);
		timerOff9RadioButton = (RadioButton) findViewById(R.id.radio_timer9_off);
		timerOff10RadioButton = (RadioButton) findViewById(R.id.radio_timer10_off);
		timerOff11RadioButton = (RadioButton) findViewById(R.id.radio_timer11_off);
		timerOff12RadioButton = (RadioButton) findViewById(R.id.radio_timer12_off);

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

		connButton.setOnClickListener(clickListener);
		disconnButton.setOnClickListener(clickListener);
		loginButton.setOnClickListener(clickListener);
		checkButton.setOnClickListener(clickListener);
		
//		pluginPowerOnButton.setOnClickListener(clickListener);
//		pluginPowerOffButton.setOnClickListener(clickListener);

		powerOnRadioButton.setOnClickListener(clickListener);
		powerOffRadioButton.setOnClickListener(clickListener);
		lightOnRadioButton.setOnClickListener(clickListener);
		lightOffRadioButton.setOnClickListener(clickListener);

//		modeRadioGroup.setClickable(false);

//		modeManualRadioButton.setOnClickListener(clickListener);
//		modeQuietRadioButton.setOnClickListener(clickListener);
//		modeWorkRadioButton.setOnClickListener(clickListener);
//		modeFastRadioButton.setOnClickListener(clickListener);
//		modeAutoRadioButton.setOnClickListener(clickListener);
		
		timerOnRadioGroup.setClickable(false);
		timerOffRadioGroup.setClickable(false);
		
		timerOn0RadioButton.setOnClickListener(clickListener);
		timerOn1RadioButton.setOnClickListener(clickListener);
		timerOn2RadioButton.setOnClickListener(clickListener);
		timerOn3RadioButton.setOnClickListener(clickListener);
		timerOn4RadioButton.setOnClickListener(clickListener);
		timerOn5RadioButton.setOnClickListener(clickListener);
		timerOn6RadioButton.setOnClickListener(clickListener);
		timerOn7RadioButton.setOnClickListener(clickListener);
		timerOn8RadioButton.setOnClickListener(clickListener);
		timerOn9RadioButton.setOnClickListener(clickListener);
		timerOn10RadioButton.setOnClickListener(clickListener);
		timerOn11RadioButton.setOnClickListener(clickListener);
		timerOn12RadioButton.setOnClickListener(clickListener);

		timerOff0RadioButton.setOnClickListener(clickListener);
		timerOff1RadioButton.setOnClickListener(clickListener);
		timerOff2RadioButton.setOnClickListener(clickListener);
		timerOff3RadioButton.setOnClickListener(clickListener);
		timerOff4RadioButton.setOnClickListener(clickListener);
		timerOff5RadioButton.setOnClickListener(clickListener);
		timerOff6RadioButton.setOnClickListener(clickListener);
		timerOff7RadioButton.setOnClickListener(clickListener);
		timerOff8RadioButton.setOnClickListener(clickListener);
		timerOff9RadioButton.setOnClickListener(clickListener);
		timerOff10RadioButton.setOnClickListener(clickListener);
		timerOff11RadioButton.setOnClickListener(clickListener);
		timerOff12RadioButton.setOnClickListener(clickListener);
		
		fanSpeedRadioGroup.setClickable(false);

		fanspeed1RadioButton.setOnClickListener(clickListener);
		fanspeed2RadioButton.setOnClickListener(clickListener);
		fanspeed3RadioButton.setOnClickListener(clickListener);
		fanspeed4RadioButton.setOnClickListener(clickListener);
		fanspeed5RadioButton.setOnClickListener(clickListener);
		
		SDKConfig config = new SDKConfig();
//		config.setUserId("734691121").setIsLogin(true);	合众测试机
		config.setEnablePush(false).setHasCrashCollect(true);
		
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
		super.onDestroy();
		
		SkySDK.stopSDK();
		
//		layoutDevAirPurifier.setVisibility(View.GONE);
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
			CmdInfoAirPurifier cmd = new CmdInfoAirPurifier(SDKConst.PRODUCT_CAIR);
			switch (v.getId()) {
			case R.id.btn_start:
				SkySDK.startConnectDevice((String) devInfoTextView.getTag());
				break;
			case R.id.btn_stop:
				SkySDK.stopConnectDevice((String) devInfoTextView.getTag());
				break;
			case R.id.btn_login:
				SkySDK.doLoginDevice((String) devInfoTextView.getTag(), getSn());
				break;
			case R.id.btn_check:
				SkySDK.checkDeviceStatus((String) devInfoTextView.getTag(), getSn());
				break;
			case R.id.tv_result:
				showDialog();
				break;
//			case R.id.btn_power_on_plugin:
//				CmdInfoPlugin cmd2 = new CmdInfoPlugin();
//				cmd2.setPowerOn();
//				SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(), cmd2,
//						getSn());
//				break;
//			case R.id.btn_power_off_plugin:
//				CmdInfoPlugin cmd3 = new CmdInfoPlugin();
//				cmd3.setPowerOff();
//				SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(), cmd3,
//						getSn());
//				break;
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
			case R.id.rb_colorscreen_on:
				if (((CompoundButton) v).isChecked()) {
					cmd.setLightOn();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				} 
				break;
			case R.id.rb_colorscreen_off:
				if (((CompoundButton) v).isChecked()) {
					cmd.setLightOff();
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
//			case R.id.rb_uv_off:
//				if (((CompoundButton) v).isChecked()) {
//					cmd.setUvOff();
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				}
//				break;
//			case R.id.rb_uv_on:
//				if (((CompoundButton) v).isChecked()) {
//					cmd.setUvOn();
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				}
//				break;
//			case R.id.rb_anion_off:
//				if (((CompoundButton) v).isChecked()) {
//					cmd.setAnionOff();
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				} 
//				break;
//			case R.id.rb_anion_on:
//				if (((CompoundButton) v).isChecked()) {
//					cmd.setAnionOn();
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				}
//				break;
//			case R.id.radio_mode_manual:
//				if (((CompoundButton) v).isChecked() == true) {
//					cmd.setMode(MODE.MANUAL);
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				}
//				break;
//			case R.id.radio_mode_auto:
//				if (((CompoundButton) v).isChecked() == true) {
//					cmd.setMode(MODE.AUTO);
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				}
//				break;
//			case R.id.radio_mode_sleep:
//				if (((CompoundButton) v).isChecked() == true) {
//					cmd.setMode(MODE.SLEEP);
//					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
//							cmd, getSn());
//				}
//				break;
			case R.id.radio_timer0_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(0);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer1_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(1);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer2_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(2);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer3_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(3);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer4_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(4);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer5_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(5);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer6_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(6);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer7_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(7);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer8_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(8);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer9_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(9);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer10_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(10);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer11_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(11);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer12_on:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOnTimer(12);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer0_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(0);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer1_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(1);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer2_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(2);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer3_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(3);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer4_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(4);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer5_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(5);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer6_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(6);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer7_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(7);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer8_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(8);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer9_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(9);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer10_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(10);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer11_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(11);
					SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
							cmd, getSn());
				}
				break;
			case R.id.radio_timer12_off:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setOffTimer(12);
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
			if(mDialogFragment != null && mDialogFragment.isVisible()){
				mDialogFragment.appendContent("\n" + data);
			}
		}

		@Override
		@Deprecated
		public void onTCPSend(String data) {
			// resultTextView.setText(resultTextView.getText() + "\n" + data);
			resultTextView.append("\n" + data);
			scrollToBottom(resultScrollView, resultTextView);
			if(mDialogFragment != null && mDialogFragment.isVisible()){
				mDialogFragment.appendContent(data);
			}
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

		@Override
		public void onConnectDeviceResult(String mac, boolean success,
				ErrorInfo errorInfo) {
			if (success) {
				if (mac.equals(devInfoTextView.getTag())) {
					connButton.setEnabled(false);
					disconnButton.setEnabled(true);
					Toast.makeText(TestCairActivity.this, "设备MAC:" + mac + "已连接！",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				if (mac.equals(devInfoTextView.getTag())) {
					connButton.setEnabled(true);
					disconnButton.setEnabled(false);
					Toast.makeText(TestCairActivity.this, "设备MAC:" + mac + "连接失败！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onDeviceDisconnected(String mac, ErrorInfo errorInfo) {
			if (mac.equals(devInfoTextView.getTag())) {
				connButton.setEnabled(true);
				disconnButton.setEnabled(false);
				Toast.makeText(TestCairActivity.this, "设备MAC:" + mac + "已断线！",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onSendDevCmdResult(String mac, int sn, boolean success,
				ErrorInfo errorInfo) {
			if (success) {
				Toast.makeText(TestCairActivity.this,
						"发送命令成功, mac: " + mac + " ,sn:" + sn,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TestCairActivity.this, "发送失败", Toast.LENGTH_SHORT)
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
			String mac = status.key;
			
			//如果是合众的协议
			if (status.devData instanceof DevDataCair) {
				DevDataCair devData = (DevDataCair) status.devData;

				String power = devData.getPower();
				String light = devData.getLight();
				String timerOn = devData.getTimerOn();
				String timerOff = devData.getTimerOff();
				
				String fanSpeed = devData.getFanSpeed();
				
				String filter = devData.getFilterUseTime();
				String pm25Level = devData.getPm25Level();
				
				if (mac.equals(devInfoTextView.getTag())) {
					if (power != null && !power.equals("")) {
						if (power.equals(DevDataCair.VALUE_ON_STATE)) {
							powerOnRadioButton.setChecked(true);
						} else if (power.equals(DevDataCair.VALUE_OFF_STATE)) {
							powerOffRadioButton.setChecked(true);
						}
					}
					if (light != null && !light.equals("")) {
						if (light.equals(DevDataCair.VALUE_ON_STATE)) {
							lightOnRadioButton.setChecked(true);
						} else if (light
								.equals(DevDataCair.VALUE_OFF_STATE)) {
							lightOffRadioButton.setChecked(true);
						}
					}
					if (timerOn != null && !timerOn.equals("")) {
						if (timerOn.equals("0")) {
							timerOn0RadioButton.setChecked(true);
						} else if (timerOn.equals("1")) {
							timerOn1RadioButton.setChecked(true);
						} else if (timerOn.equals("2")) {
							timerOn2RadioButton.setChecked(true);
						} else if (timerOn.equals("3")) {
							timerOn3RadioButton.setChecked(true);
						} else if (timerOn.equals("4")) {
							timerOn4RadioButton.setChecked(true);
						} else if (timerOn.equals("5")) {
							timerOn5RadioButton.setChecked(true);
						} else if (timerOn.equals("6")) {
							timerOn6RadioButton.setChecked(true);
						} else if (timerOn.equals("7")) {
							timerOn7RadioButton.setChecked(true);
						} else if (timerOn.equals("8")) {
							timerOn8RadioButton.setChecked(true);
						} else if (timerOn.equals("9")) {
							timerOn9RadioButton.setChecked(true);
						} else if (timerOn.equals("10")) {
							timerOn10RadioButton.setChecked(true);
						} else if (timerOn.equals("11")) {
							timerOn11RadioButton.setChecked(true);
						} else if (timerOn.equals("12")) {
							timerOn12RadioButton.setChecked(true);
						}
					}
					if (timerOff != null && !timerOff.equals("")) {
						if (timerOff.equals("0")) {
							timerOff0RadioButton.setChecked(true);
						} else if (timerOff.equals("1")) {
							timerOff1RadioButton.setChecked(true);
						} else if (timerOff.equals("2")) {
							timerOff2RadioButton.setChecked(true);
						} else if (timerOff.equals("3")) {
							timerOff3RadioButton.setChecked(true);
						} else if (timerOff.equals("4")) {
							timerOff4RadioButton.setChecked(true);
						} else if (timerOff.equals("5")) {
							timerOff5RadioButton.setChecked(true);
						} else if (timerOff.equals("6")) {
							timerOff6RadioButton.setChecked(true);
						} else if (timerOff.equals("7")) {
							timerOff7RadioButton.setChecked(true);
						} else if (timerOff.equals("8")) {
							timerOff8RadioButton.setChecked(true);
						} else if (timerOff.equals("9")) {
							timerOff9RadioButton.setChecked(true);
						} else if (timerOff.equals("10")) {
							timerOff10RadioButton.setChecked(true);
						} else if (timerOff.equals("11")) {
							timerOff11RadioButton.setChecked(true);
						} else if (timerOff.equals("12")) {
							timerOff12RadioButton.setChecked(true);
						}
					}
					if (fanSpeed != null && !fanSpeed.equals("")) {
						if (fanSpeed.equals(DevDataCair.VALUE_FANSPEED_PRE + "1")) {
							fanspeed1RadioButton.setChecked(true);
						} else if (fanSpeed.equals(DevDataCair.VALUE_FANSPEED_PRE + "2")) {
							fanspeed2RadioButton.setChecked(true);
						} else if (fanSpeed.equals(DevDataCair.VALUE_FANSPEED_PRE + "3")) {
							fanspeed3RadioButton.setChecked(true);
						} else if (fanSpeed.equals(DevDataCair.VALUE_FANSPEED_PRE + "4")) {
							fanspeed4RadioButton.setChecked(true);
						} else if (fanSpeed.equals(DevDataCair.VALUE_FANSPEED_PRE + "5")) {
							fanspeed5RadioButton.setChecked(true);
						}
					}

					if (filter != null && !filter.equals("")) {
						devFilterTextView.setText(filter);
					}
					if (pm25Level != null && !pm25Level.equals("")) {
						devPmLevelTextView.setText(pm25Level);
					}
				}
			} else if (status.devData instanceof DevDataBroadlink) {
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
	
    void showDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
//        ft.addToBackStack(null);

        // Create and show the dialog.
        mDialogFragment = MyDialogFragment.newInstance(resultTextView.getText().toString());
        mDialogFragment.show(ft, "dialog");
    }
    
	public static class MyDialogFragment extends DialogFragment {
		private String content;
		private TextView tv;
		private ScrollView sv;
		
		public void setContent(String content) {
			this.content = content;
			tv.setText(content);
		}
		public void appendContent(String content) {
			tv.append(content);
		}
		
        static MyDialogFragment newInstance(String content) {
        	MyDialogFragment f = new MyDialogFragment();
			Bundle args = new Bundle();
			args.putString("content", content);
			f.setArguments(args);
            return f;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            content = getArguments().getString("content");
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_dialog, container, false);
            sv = (ScrollView) v.findViewById(R.id.fd_scrollv);
            tv = (TextView) v.findViewById(R.id.fd_text);
            tv.setText(content);
            scrollToBottom(sv, tv);
            return v;
        }
    }
}
*/