package com.kernal.plateid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utills.CameraFragment;
import utills.CheckPermission;
import utills.PermissionActivity;
import utills.Utils;
import view.VerticalSeekBar;
import view.ViewfinderView;

/**
 * 
 * 
 * 项目名称：plate_id_sample_service 类名称：MemoryCameraActivity 类描述： 视频扫描界面 扫描车牌并识别
 * （与视频流的拍照识别同一界面） 创建人：张志朋 创建时间：2016-1-29 上午10:55:28 修改人：user 修改时间：2016-1-29
 * 上午10:55:28 修改备注：
 * 
 * @version
 * 
 */
public class MemoryCameraActivity extends Activity {




	private ImageButton back_btn, flash_btn, back, take_pic;
	private ViewfinderView myview;
	private RelativeLayout re;
	private int width, height;
	private String number = "", color = "";
	private Vibrator mVibrator;
	private PlateRecognitionParameter prp = new PlateRecognitionParameter();;
	private boolean recogType;// 记录进入此界面时是拍照识别还是视频识别 true:视频识别 false:拍照识别
	private String path;// 圖片保存的路徑
	private SensorManager sensorManager;
	private float x,y,z;
	//向左旋转
	public boolean Rotate_left = false;
	//正向旋转
	public boolean Rotate_top = true;
	//向右旋转
	public boolean Rotate_right = false;
	//倒置旋转
	public boolean Rotate_bottom = false;
	private CameraFragment fragment;
	private byte[] feedbackData;
	public int[] areas = new int[4];
	private SeekBar seekBar;
	private VerticalSeekBar verticalSeekBar;
	private LayoutParams layoutParams;
	private int recordProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_carmera);

		RecogService.recogModel = true;

		recogType = getIntent().getBooleanExtra("camera", true);
		RecogService.initializeType = recogType;
		Point srcPixs=Utils.getScreenSize(this);
		width=srcPixs.x;
		height=srcPixs.y;
		findiew();
		initRecogView();


		if(new File("/sdcard/AndroidWT/wt.lsc").exists()){
			((Button)findViewById(R.id.btn_jihuo)).setVisibility(View.GONE);
		}

		((Button)findViewById(R.id.btn_jihuo)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
              jihuo();
			}
		});

	}


	static final String[] PERMISSION = new String[] {Manifest.permission.CAMERA,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
			Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.VIBRATE, Manifest.permission.INTERNET,
	};
	private void jihuo(){

		//激活程序按钮
		if (Build.VERSION.SDK_INT >= 23) {
			CheckPermission checkPermission = new CheckPermission(MemoryCameraActivity.this);
			if (checkPermission.permissionSet(PERMISSION)) {
				PermissionActivity.startActivityForResult(MemoryCameraActivity.this,0,"AuthService",  PERMISSION);
			}
			CreatViewtoAuthservice();
		}else{
			CreatViewtoAuthservice();
		}

	}

	private int ReturnAuthority = -1;
	public AuthService.MyBinder authBinder;
	public ServiceConnection authConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			authBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			authBinder = (AuthService.MyBinder) service;
			try {
				PlateAuthParameter pap = new PlateAuthParameter();
				pap.sn = sn;
				ReturnAuthority = authBinder.getAuth(pap);
				if (ReturnAuthority != 0) {
					Toast.makeText(getApplicationContext(),getString(R.string.license_verification_failed)+":"+ReturnAuthority,Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(),R.string.license_verification_success,Toast.LENGTH_LONG).show();
				}
			}catch (Exception e) {
				Toast.makeText(getApplicationContext(), R.string.failed_check_failure, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}finally{
				if (authBinder != null) {
					unbindService(authConn);
				}
			}

		}
	};
	private EditText editText;
	private String sn="";
	public void CreatViewtoAuthservice(){

		editText = new EditText(getApplicationContext());
		editText.setTextColor(Color.BLACK);

		editText.setText("");
		editText.setHint("请输入激活码");
		new  AlertDialog.Builder(MemoryCameraActivity.this)
				.setTitle(R.string.dialog_title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)

				.setPositiveButton(R.string.license_verification, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						sn = editText.getText().toString().toUpperCase();
						System.out.println("sn:"+sn);
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						System.out.println("imm:"+imm.isActive());
						if (imm.isActive()) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						}
						Intent authIntent = new Intent(MemoryCameraActivity.this, AuthService.class);
						bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
						dialog.dismiss();

					}
				})
				.setNegativeButton(R.string.offline_activation ,  new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						if (imm.isActive()) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						}

						String sdDir = null;
						boolean sdCardExist = Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED);
						if (sdCardExist) {
							String PATH = Environment.getExternalStorageDirectory().toString() + "/AndroidWT";
							File file = new File(PATH);
							if (!file.exists()) {
								file.mkdir();
							}
							sdDir = PATH+"/wt.dev";
							String deviceId;
							String androId;
							TelephonyManager telephonyManager;
							telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
							StringBuilder sb = new StringBuilder();
							sb.append(telephonyManager.getDeviceId());
							deviceId = sb.toString();

							StringBuilder sb1 = new StringBuilder();
							sb1.append(Settings.Secure.getString(getContentResolver(),
									Settings.Secure.ANDROID_ID));
							androId = sb1.toString();
							File newFile = new File(sdDir);
							String idString = deviceId+";"+androId;
							try {
								newFile.delete();
								newFile.createNewFile();
								FileOutputStream fos = new FileOutputStream(newFile);
								StringBuffer sBuffer = new StringBuffer();
								sBuffer.append(idString);
								fos.write(sBuffer.toString().getBytes());
								fos.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
						dialog.dismiss();
						new  AlertDialog.Builder(MemoryCameraActivity.this)
								.setTitle(R.string.dialog_alert)
								.setMessage(R.string.dialog_message_one)
								.setPositiveButton(R.string.confirm ,  new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();

									}
								} )
								.show();

					}
				})
				.show();

	}

	/**
	 *
	 * @param feedbackData  被识别的帧数据
	 */
	public void SendfeedbackData(byte[] feedbackData) {
		this.feedbackData = feedbackData;
	}

	void initRecogView(){
		GetScreenDirection();
		if (!recogType) {
			fragment.setRecogModle(true,false);
			// 拍照按钮
			take_pic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					fragment.setRecogModle(true,true);
				}

			});
		}
	}
	@SuppressLint("NewApi")
	private void findiew() {
		// TODO Auto-generated method stub
		flash_btn = (ImageButton) findViewById(R.id.flash_camera);
		back = (ImageButton) findViewById(R.id.back);
		take_pic = (ImageButton) findViewById(R.id.take_pic_btn);
		re = (RelativeLayout) findViewById(R.id.memory);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		verticalSeekBar = (VerticalSeekBar) findViewById(R.id.verticalSeekBar);
		fragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.sampleFragment);
		if (recogType) {
			take_pic.setVisibility(View.GONE);
		} else {
			take_pic.setVisibility(View.VISIBLE);
		}
		int back_w;
		int back_h;
		int flash_w;
		int flash_h;
		int Fheight;
		int take_h;
		int take_w;
		back.setVisibility(View.VISIBLE);
		back_h = (int) (height * 0.066796875);
		back_w = (int) (back_h * 1);
		layoutParams = new LayoutParams(back_w, back_h);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
		layoutParams.topMargin = (int) (height*0.025);
		layoutParams.leftMargin = (int) (width * 0.050486111111111111111111111111111);
		back.setLayoutParams(layoutParams);

		flash_h = (int) (height * 0.066796875);
		flash_w = (int) (flash_h * 1);
		layoutParams = new LayoutParams(flash_w, flash_h);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);

		layoutParams.topMargin = (int) (height*0.025);
		layoutParams.rightMargin = (int) (width * 0.050486111111111111111111111111111);
		flash_btn.setLayoutParams(layoutParams);

		take_h = (int) (height * 0.105859375);
		take_w = (int) (take_h * 1);
		layoutParams = new LayoutParams(take_w, take_h);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		layoutParams.bottomMargin = (int) (height * 0.025);
		take_pic.setLayoutParams(layoutParams);

		layoutParams = new LayoutParams(width*23/24, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.topMargin = (height*2/3);
		seekBar.setLayoutParams(layoutParams);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				recordProgress = progress;
				fragment.setFocallength((int)(fragment.getFocal()*progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				fragment.setRecogsuspended(true);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				fragment.setRecogsuspended(false);
			}
		});
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, height*7/12);
		layoutParams.leftMargin = (width/10);
		layoutParams.topMargin = ( height * 5/24);
		verticalSeekBar.setLayoutParams(layoutParams);
		verticalSeekBar.getFragment(fragment);
		verticalSeekBar.setVisibility(View.GONE);
		verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				recordProgress = progress;
				fragment.setFocallength((int)(fragment.getFocal()*progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				fragment.setRecogsuspended(true);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				fragment.setRecogsuspended(false);
			}
		});
				// 竖屏状态下返回按钮
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub\
//				fragment.backLastActivtiy();
  finish();
			}
		});
		// 闪光灯监听事件
		flash_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// b = true;
				// TODO Auto-generated method stub
				fragment.setFlash();

			}

		});
	}

	/**
	 * 根据重力感应  获取屏幕状态
	 */
	public void GetScreenDirection(){
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
	}
	private SensorEventListener listener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			x = event.values[SensorManager.DATA_X];
			y = event.values[SensorManager.DATA_Y];
			z = event.values[SensorManager.DATA_Z];
			if(x>7){   //&&y<7
				if(!Rotate_left){
					System.out.println("向左旋转");
					Rotate_bottom = false;
					Rotate_right = false;
					Rotate_top = false;
					Rotate_left =  true;
					rotateAnimation(90,90,take_pic,flash_btn,back);
					ChangView(MemoryCameraActivity.this,false);
				}

			}else if(x<-7){  //&&y<7
				if(!Rotate_right){
					System.out.println("向右旋转");
					Rotate_bottom = false;
					Rotate_right = true;
					Rotate_top = false;
					Rotate_left =  false;
					rotateAnimation(-90,90,take_pic,flash_btn,back);
					ChangView(MemoryCameraActivity.this,false);
				}

			}else if(y<-7){  //&&x<7&&x>-7
				if(!Rotate_bottom){
					System.out.println("倒置旋转");
					Rotate_bottom = true;
					Rotate_right = false;
					Rotate_top = false;
					Rotate_left =  false;
					rotateAnimation(180,90,take_pic,flash_btn,back);
					ChangView(MemoryCameraActivity.this,true);
				}
			}else if(y>7){
				if(!Rotate_top){
					System.out.println("竖屏状态");
					Rotate_bottom = false;
					Rotate_right = false;
					Rotate_top =true;
					Rotate_left =  false;
					rotateAnimation(0,0,take_pic,flash_btn,back);
					ChangView(MemoryCameraActivity.this,true);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	/***
	 * 旋转动画
	 * @param toDegrees
	 * 旋转的结束角度
	 * @param
	 *
	 */
	private void rotateAnimation(int toDegrees,int toDegrees2, View view1,View view2,View view3 ) {
		view1.animate().rotation(toDegrees).setDuration(500).start();
		view2.animate().rotation(toDegrees).setDuration(500).start();
		view3.animate().rotation(toDegrees2).setDuration(500).start();
	}

	/**
	 *
	 * @param context   改变屏幕布局  根据横竖屏状态修改布局
	 * @param isPortrait
     */
	public void ChangView(Context context,boolean isPortrait){
		fragment.ChangeState(Rotate_left,Rotate_right,Rotate_top,Rotate_bottom,isPortrait);
		if(Rotate_left){
			seekBar.setVisibility(View.GONE);
			verticalSeekBar.setVisibility(View.VISIBLE);
			layoutParams.leftMargin = (width/10);
			verticalSeekBar.setLayoutParams(layoutParams);
			verticalSeekBar.setProgress(recordProgress);
		}else if(Rotate_right){
			seekBar.setVisibility(View.GONE);
			verticalSeekBar.setVisibility(View.VISIBLE);
			layoutParams.leftMargin = (width*4/5);
			verticalSeekBar.setLayoutParams(layoutParams);
			verticalSeekBar.setProgress(recordProgress);
		}else{
			seekBar.setVisibility(View.VISIBLE);
			verticalSeekBar.setVisibility(View.GONE);
			seekBar.setProgress(recordProgress);
		}

	}
	/**
	 * 拿到结果之后的处理逻辑
	 * @Title: getResult
	 * @Description: TODO(获取结果)
	 * @param @param fieldvalue 调用识别接口返回的数据
	 * @return void 返回类型
	 * @throwsbyte[]picdata
	 */

	public void getResult(String[] fieldvalue,String path) {
		     this.path=path;
		Intent intent1=new Intent();
			if (fieldvalue[0] != null && !fieldvalue[0].equals("")) {
				/**
				 * 识别到车牌之后的处理方法
				 */
				String []resultString =  fieldvalue[0].split(";");
				String []resultColor = fieldvalue[1].split(";");
				int length = resultString.length;
				if (length == 1) {
					mVibrator = (Vibrator) getApplication()
							.getSystemService(
									Service.VIBRATOR_SERVICE);
					mVibrator.vibrate(100);
					Intent intent = new Intent(MemoryCameraActivity.this,
							MemoryResultActivity.class);
					number = fieldvalue[0];
					color = fieldvalue[1];
					int left = Integer.valueOf(fieldvalue[7]);
					int top = Integer.valueOf(fieldvalue[8]);
					int w = Integer.valueOf(fieldvalue[9])
							- Integer.valueOf(fieldvalue[7]);
					int h = Integer.valueOf(fieldvalue[10])
							- Integer.valueOf(fieldvalue[8]);
					intent.putExtra("number", number);
					intent.putExtra("color", color);
					intent.putExtra("path", path);
					intent.putExtra("left", left);
					intent.putExtra("top", top);
					intent.putExtra("width", w);
					intent.putExtra("height", h);
					intent.putExtra("recogType", recogType);

					if(!TextUtils.isEmpty(number)){


						intent1.putExtra("num",number.split(";")[0]);

					}else {
						intent1.putExtra("num","空");
					}

					setResult(101,intent1);
					finish();

//					startActivity(intent);
//					MemoryCameraActivity.this.finish();
				}else {
					String itemString = "";
					String itemColor = "";
					mVibrator = (Vibrator) getApplication()
							.getSystemService(
									Service.VIBRATOR_SERVICE);
					mVibrator.vibrate(100);
					Intent intent = new Intent(
							MemoryCameraActivity.this,
							MemoryResultActivity.class);
					for (int i = 0; i < length; i++) {
						itemString = fieldvalue[0];
						itemColor = fieldvalue[1];
						resultString = itemString.split(";");
						resultColor =  itemColor.split(";");
						number += resultString[i] + ";\n";
						color += resultColor[i] + ";\n";
						itemString = fieldvalue[11];
						resultString = itemString.split(";");
					}
					intent.putExtra("number", number);
					intent.putExtra("color", color);
					intent.putExtra("time", resultString);
					intent.putExtra("recogType", recogType);


					if(!TextUtils.isEmpty(number)){


						intent1.putExtra("num",number.split(";")[0]);

					}else {
						intent1.putExtra("num","空");
					}

					setResult(101,intent1);
					finish();

//					MemoryCameraActivity.this.finish();
//					startActivity(intent);
				}
			} else{
			    // 未检测到车牌时执行下列代码
				//预览识别执行下列代码 不是预览识别 不做处理等待下一帧
				mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
				mVibrator.vibrate(100);
				Intent intent = new Intent(MemoryCameraActivity.this, MemoryResultActivity.class);
				number = fieldvalue[0];
				color = fieldvalue[3];
				if (fieldvalue[0] == null) {
					number = "null";
				}
				if (fieldvalue[1] == null) {
					color = "null";
				}
				int left = this.areas[0];
				int top = this.areas[1];
				int w = this.areas[2] - this.areas[0];
				int h = this.areas[3] - this.areas[1];
				intent.putExtra("number", number);
				intent.putExtra("color", color);
				intent.putExtra("path", path);
				intent.putExtra("left", left);
				intent.putExtra("top", top);
				intent.putExtra("width", w);
				intent.putExtra("height", h);
				intent.putExtra("time", fieldvalue[11]);
				intent.putExtra("recogType", recogType);

				if(!TextUtils.isEmpty(number)){


					intent1.putExtra("num",number.split(";")[0]);

				}else {
					intent1.putExtra("num","空");
				}

				setResult(101,intent1);
				finish();

//				MemoryCameraActivity.this.finish();
//				startActivity(intent);
			}
	}

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// fragment.backLastActivtiy();
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	protected void onDestroy() {
		if (sensorManager != null)
			sensorManager.unregisterListener(listener);
		super.onDestroy();
	}

}
