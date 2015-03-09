package com.skyware.sdk.test.ui;

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
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.entity.biz.CmdInfoWatarPot;
import com.skyware.sdk.entity.biz.DevDataBroadlink;
import com.skyware.sdk.entity.biz.DevDataCair;
import com.skyware.sdk.entity.biz.DevDataRoyalstar;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;

public class TestRoyalstarActivity extends Activity {

	// private Button discoverButton;
	// private Button stopButton;
	// private Button onButton1;
	// private Button onButton2;
	// private Button offButton1;
	// private Button offButton2;
	private Button connButton;
	private Button disconnButton;
//	private Button loginButton;
//	private Button checkButton;
	private TextView resultTextView;
	private TextView devInfoTextView;
	private TextView devTempTextView;
	private TextView devErrorTextView;
	private TextView devStatTextView;
	private TextView devTimeTextView;
	private TextView devNetStatTv;
	
	private RadioButton powerOnRadioButton;
	private RadioButton powerOffRadioButton;
	private RadioButton lightOnRadioButton;
	private RadioButton lightOffRadioButton;

	private RadioGroup modeRadioGroup;

	private RadioButton modeAutoRadioButton;
	private RadioButton modeManualAddRadioButton;
	private RadioButton modeManualHeatRadioButton;
	private RadioButton modeTea1RadioButton;
	private RadioButton modeTea2RadioButton;
	private RadioButton modeTea3RadioButton;
	private RadioButton modeTea4RadioButton;
	private RadioButton modeTea5RadioButton;
	
	private View layoutSmartPot;
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
		setContentView(R.layout.activity_test_royalstar);
		
		layoutSmartPot = (View) findViewById(R.id.layout_dev_smartpot);
		layoutSmartPot.setVisibility(View.GONE);

		resultTextView = (TextView) findViewById(R.id.tv_result);
		resultTextView.setOnClickListener(mClickListener);
		// resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		resultScrollView = (ScrollView) findViewById(R.id.sv_result);

		devInfoTextView = (TextView) findViewById(R.id.tv_dev_info);
		devTempTextView = (TextView) findViewById(R.id.tv_dev_temp_value);
		devErrorTextView = (TextView) findViewById(R.id.tv_dev_error_value);
		devStatTextView = (TextView) findViewById(R.id.tv_dev_stat_value);
		devTimeTextView = (TextView) findViewById(R.id.tv_dev_time_value);
		
		connButton = (Button) findViewById(R.id.btn_start);
		disconnButton = (Button) findViewById(R.id.btn_stop);
//		loginButton = (Button) findViewById(R.id.btn_login);
//		checkButton = (Button) findViewById(R.id.btn_check);
		
//		devNetStatTv = (TextView) findViewById(R.id.tv_dev_net_status);
		
		powerOnRadioButton = (RadioButton) findViewById(R.id.rb_power_on);
		powerOffRadioButton = (RadioButton) findViewById(R.id.rb_power_off);

		modeRadioGroup = (RadioGroup) findViewById(R.id.rg_mode);

		modeAutoRadioButton 	= (RadioButton) findViewById(R.id.rb_mode_auto);
		modeManualAddRadioButton = (RadioButton) findViewById(R.id.rb_mode_manual_add);
		modeManualHeatRadioButton = (RadioButton) findViewById(R.id.rb_mode_manual_heat);
		modeTea1RadioButton = (RadioButton) findViewById(R.id.rb_mode_tea1);
		modeTea2RadioButton = (RadioButton) findViewById(R.id.rb_mode_tea2);
		modeTea3RadioButton = (RadioButton) findViewById(R.id.rb_mode_tea3);
		modeTea4RadioButton = (RadioButton) findViewById(R.id.rb_mode_tea4);
		modeTea5RadioButton = (RadioButton) findViewById(R.id.rb_mode_tea5);

		connButton.setOnClickListener(mClickListener);
		disconnButton.setOnClickListener(mClickListener);
//		loginButton.setOnClickListener(clickListener);
//		checkButton.setOnClickListener(clickListener);
		
//		pluginPowerOnButton.setOnClickListener(clickListener);
//		pluginPowerOffButton.setOnClickListener(clickListener);

		powerOnRadioButton.setOnClickListener(mClickListener);
		powerOffRadioButton.setOnClickListener(mClickListener);

//		modeRadioGroup.setClickable(false);

		modeAutoRadioButton.setOnClickListener(mClickListener);
		modeManualAddRadioButton.setOnClickListener(mClickListener);
		modeManualHeatRadioButton.setOnClickListener(mClickListener);
		modeTea1RadioButton.setOnClickListener(mClickListener);
		modeTea2RadioButton.setOnClickListener(mClickListener);
		modeTea3RadioButton.setOnClickListener(mClickListener);
		modeTea4RadioButton.setOnClickListener(mClickListener);
		modeTea5RadioButton.setOnClickListener(mClickListener);
		
		SDKConfig config = new SDKConfig();
//		config.setUserId("734691121").setIsLogin(true);	合众测试机
		config.setEnablePush(false).setHasCrashCollect(true).setProductType(SDKConst.PRODUCT_ROYALSTAR);
		
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
		layoutSmartPot.setVisibility(View.VISIBLE);
//		devInfoTextView.setTag("ACCF232C6F26");
		
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
	
	
	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			CmdInfoWatarPot cmd = new CmdInfoWatarPot(SDKConst.PRODUCT_ROYALSTAR);
			boolean sendCmd = false;
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
			case R.id.rb_power_off:
				if (((CompoundButton) v).isChecked()) {
					cmd.setPowerOff();
					sendCmd = true;
				}
				break;
			case R.id.rb_power_on:
				if (((CompoundButton) v).isChecked()) {
					cmd.setPowerOn();
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_auto:
				if (((CompoundButton) v).isChecked()) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_AUTO);
					sendCmd = true;
				} 
				break;
			case R.id.rb_mode_manual_heat:
				if (((CompoundButton) v).isChecked()) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT);
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_manual_add:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_ADD);
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_tea1:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT1);
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_tea2:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT2);
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_tea3:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT3);
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_tea4:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT4);
					sendCmd = true;
				}
				break;
			case R.id.rb_mode_tea5:
				if (((CompoundButton) v).isChecked() == true) {
					cmd.setMode(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT5);
					sendCmd = true;
				}
				break;
			default:
				break;
			}
			if (sendCmd) {
				SkySDK.sendCmdToDevice((String) devInfoTextView.getTag(),
						cmd, getSn());
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

			layoutSmartPot.setVisibility(View.VISIBLE);

//				powerSwitch.setChecked(false);
//				childLockSwitch.setChecked(false);
//				uvSwitch.setChecked(false);
//				anionSwitch.setChecked(false);

			devInfoTextView.setText(devShow);
			devInfoTextView.setTag(info.getMac());
		}
		
		@Override
		public void onDevNetStatChange(DeviceInfo info) {
			String mac = info.getMac();
			
//			if (mac.equals(devInfoTextView.getTag())) {
//				if(!info.isAccess()){
//					devNetStatTv.setText(devNetStatTv.getText() + "离线");
//				} else {
//					if (info.isLocalOnline() && info.isRemoteOnline()) {
//						devNetStatTv.setText(devNetStatTv.getText() + "远程在线 | 局域网在线");
//					} else if (info.isLocalOnline() && !info.isRemoteOnline()) {
//						devNetStatTv.setText(devNetStatTv.getText() + "远程离线 | 局域网在线");
//					} else if (!info.isLocalOnline() && info.isRemoteOnline()) {
//						devNetStatTv.setText(devNetStatTv.getText() + "远程在线 | 局域网离线");
//					}
//				}
//				
//			}
			
		}

		@Override
		public void onConnectDeviceResult(String mac, boolean success,
				ErrorInfo errorInfo) {
			if (mac == null) {
				Toast.makeText(TestRoyalstarActivity.this, "设备MAC:" + mac + "连接失败！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (success) {
				if (mac.equals(devInfoTextView.getTag())) {
					connButton.setEnabled(false);
					disconnButton.setEnabled(true);
					Toast.makeText(TestRoyalstarActivity.this, "设备MAC:" + mac + "已连接！",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				if (mac.equals(devInfoTextView.getTag())) {
					connButton.setEnabled(true);
					disconnButton.setEnabled(false);
					Toast.makeText(TestRoyalstarActivity.this, "设备MAC:" + mac + "连接失败！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onDeviceDisconnected(String mac, ErrorInfo errorInfo) {
			if (mac.equals(devInfoTextView.getTag())) {
				connButton.setEnabled(true);
				disconnButton.setEnabled(false);
				Toast.makeText(TestRoyalstarActivity.this, "设备MAC:" + mac + "已断线！",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onSendDevCmdResult(String mac, int sn, boolean success,
				ErrorInfo errorInfo) {
			if (success) {
				Toast.makeText(TestRoyalstarActivity.this,
						"发送命令成功, mac: " + mac + " ,sn:" + sn,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TestRoyalstarActivity.this, "发送失败", Toast.LENGTH_SHORT)
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
			
			//如果是荣事达的协议
			if (status.devData instanceof DevDataRoyalstar) {
				DevDataRoyalstar devData = (DevDataRoyalstar) status.devData;

				String power = devData.getPower();
				String mode = devData.getMode();
				String temp = devData.getTemp();
				String err = devData.getError();
				String stat = devData.getStat();
				String time = devData.getRemainTime();
				
				if (mac.equals(devInfoTextView.getTag())) {
					if (power != null && !power.equals("")) {
						if (power.equals(DevDataRoyalstar.VALUE_ON_STATE)) {
							powerOnRadioButton.setChecked(true);
						} else if (power.equals(DevDataRoyalstar.VALUE_OFF_STATE)) {
							powerOffRadioButton.setChecked(true);
						}
					}
					if (mode != null && !mode.equals("")) {
						if (mode.equals(DevDataRoyalstar.VALUE_MODE_AUTO)) {
							modeAutoRadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_ADD)) {
							modeManualAddRadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT)) {
							modeManualHeatRadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT1)) {
							modeTea1RadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT2)) {
							modeTea2RadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT3)) {
							modeTea3RadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT4)) {
							modeTea4RadioButton.setChecked(true);
						} else if (mode.equals(DevDataRoyalstar.VALUE_MODE_MANUAL_HEAT5)) {
							modeTea5RadioButton.setChecked(true);
						} 
					}
					try {
						if (temp != null && !temp.equals("")) {
							int tempInt = Integer.parseInt(temp);
							devTempTextView.setText(tempInt + " ℃");
						}
						if (err != null && !err.equals("")) {
							int errId = Integer.parseInt(err);
							if (errId <= 5 && errId >= 0) {
								devErrorTextView.setText(getResources().getStringArray(R.array.royalstar_err)[errId]);
							}
						}
						if (stat != null && !stat.equals("")) {
							int statId = Integer.parseInt(stat);
							if (statId <= 4 && statId >= 0) {
								devStatTextView.setText(getResources().getStringArray(R.array.royalstar_stats)[statId]);
							}
						}
						if (time != null && !time.equals("")) {
							int timeInt = Integer.parseInt(time);
							devTimeTextView.setText(timeInt + " 秒");
						}
					} catch (NumberFormatException e) {
						
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
