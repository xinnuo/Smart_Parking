package com.kernal.plateid;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utills.CheckPermission;
import utills.LogcatHelper;
import utills.PermissionActivity;
import utills.Utils;

public class MainActivity extends Activity implements OnClickListener{
	public static final String PATH = Environment.getExternalStorageDirectory()	.toString();
    public static final String TAG = "TestPlateServiceDemo";
    String pic;

    String sn;
    String authfile;


    private EditText editText;

    
    private Button buttonActivation,buttonSelectPic,buttonExit;
    
    private int ReturnAuthority = -1;
    String[] fieldvalue = new String[14];
    public AuthService.MyBinder authBinder;
    public String sdDir;
    private Button CameraButton,customCameraButton;
    private final int SYSTEM_RESULT_CODE = 2;
    private final int SELECT_RESULT_CODE = 3;
    private Button videoReg;
    private  int index=0;
    private boolean isTouch = false;


    private Button butstart;

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
            finish();
        }
    };
    static final String[] PERMISSION = new String[] {permission.CAMERA,
		permission.WRITE_EXTERNAL_STORAGE,// 写入权限
		permission.READ_EXTERNAL_STORAGE, // 读取权限
		 permission.READ_PHONE_STATE,
		permission.VIBRATE, permission.INTERNET,
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.test_plate_activity);

//		LogcatHelper.getInstance(this).start();

        findViews();
//        String sdStatus = Environment.getExternalStorageState();
//        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
//        	sdDir = Environment.getExternalStorageDirectory().toString()+"/kernalimage";
//        }
//		File dir = new File(sdDir);
//		if (!dir.exists()) {
//			dir.mkdir();
//		}
        CameraButton.setOnClickListener(this);
        customCameraButton.setOnClickListener(this);
        buttonActivation.setOnClickListener(this);
        buttonSelectPic.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
        videoReg.setOnClickListener(this);

		butstart.setOnClickListener(this);



		try{
			Intent cameraintent = new Intent(MainActivity.this,MemoryCameraActivity.class);
			if (Build.VERSION.SDK_INT >= 23) {
				CheckPermission checkPermission = new CheckPermission(MainActivity.this);
				if (checkPermission.permissionSet(PERMISSION)) {
					PermissionActivity.startActivityForResult(MainActivity.this,0,"false",  PERMISSION);
				} else {
					cameraintent.putExtra("camera", false);
					startActivity(cameraintent);
				}
			} else {
				cameraintent.putExtra("camera", false);
				startActivity(cameraintent);
			}
		}catch (Exception e){



		}





		jihuo();


    }

    private void jihuo(){

		//激活程序按钮
		if (Build.VERSION.SDK_INT >= 23) {
			CheckPermission checkPermission = new CheckPermission(MainActivity.this);
			if (checkPermission.permissionSet(PERMISSION)) {
				PermissionActivity.startActivityForResult(MainActivity.this,0,"AuthService",  PERMISSION);
			}
			CreatViewtoAuthservice();
		}else{
			CreatViewtoAuthservice();
		}

	}

    private void findViews() {
        CameraButton = (Button) findViewById(R.id.buttoncamera);
        customCameraButton = (Button) findViewById(R.id.customcamerabutton);
        buttonActivation = (Button) findViewById(R.id.butactivation);
        buttonSelectPic = (Button) findViewById(R.id.butselectpic);
        buttonExit = (Button) findViewById(R.id.butclose);
        videoReg= (Button) findViewById(R.id.videoReg);

		butstart=(Button)findViewById(R.id.butstart);

//		buttonActivation.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
		//拍照识别入口
		if(getResources()
				.getIdentifier("buttoncamera", "id", this.getPackageName())==v.getId()){
			Intent cameraintent = new Intent(MainActivity.this,MemoryCameraActivity.class);
        	if (Build.VERSION.SDK_INT >= 23) {
				CheckPermission checkPermission = new CheckPermission(MainActivity.this);
				if (checkPermission.permissionSet(PERMISSION)) {
				PermissionActivity.startActivityForResult(MainActivity.this,0,"false",  PERMISSION);
				} else {
					cameraintent.putExtra("camera", false);
		        	startActivity(cameraintent);
				}
			} else {
	        	cameraintent.putExtra("camera", false);
	        	startActivity(cameraintent);
			}
			finish();
		}else if(getResources()
				.getIdentifier("videoReg", "id", this.getPackageName())==v.getId()){
			//视频识别入口
			CreatDialog();
		}else if(getResources()
				.getIdentifier("butclose", "id", this.getPackageName())==v.getId()){
			//返回
					finish();
		}else if(getResources()
				.getIdentifier("butactivation", "id", this.getPackageName())==v.getId()){
			//激活程序按钮
			if (Build.VERSION.SDK_INT >= 23) {
    				CheckPermission checkPermission = new CheckPermission(MainActivity.this);
    				if (checkPermission.permissionSet(PERMISSION)) {
    					PermissionActivity.startActivityForResult(MainActivity.this,0,"AuthService",  PERMISSION);
    				}
    					CreatViewtoAuthservice();
    			}else{
    				CreatViewtoAuthservice();
    			}
		}else if(getResources()
				.getIdentifier("butselectpic", "id", this.getPackageName())==v.getId()){
			//选择识别入口
			Intent selectIntent = new Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			Intent wrapperIntent = Intent.createChooser(selectIntent,"请选择一张图片");
        	if (Build.VERSION.SDK_INT >= 23) {
				CheckPermission checkPermission = new CheckPermission(MainActivity.this);
				if (checkPermission.permissionSet(PERMISSION)) {
					PermissionActivity.startActivityForResult(MainActivity.this,0,"choice",  PERMISSION);
				} else {
					try {
						startActivityForResult(wrapperIntent, SELECT_RESULT_CODE);
					} catch (Exception e) {
						Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				try {
					startActivityForResult(wrapperIntent, SELECT_RESULT_CODE);
				} catch (Exception e) {
					Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
				}
			}

		}

    }
    /** 
	* @Title: CreatDDialog 
	* @Description: TODO(这里用一句话描述这个方法的作用) 创建选择对话框，选择识别模式,只在进入视频识别时进行选择，拍照识别默认快速识别模式
	* @return void    返回类型
	* @throws 
	*/
	private void CreatDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择识别模式");
        final String[] model = {"精准模式", "快速模式"};
     
        //    设置一个单项选择下拉框
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，0表示默认'精准模式' 会被勾选上   1 表示快速模式
         * 第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(model, index, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                index = which;
               isTouch = true;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            	isTouch = false;
            	Intent video_intent = new Intent();
            	video_intent.putExtra("camera", true);          
            	if(index==1){
					//  false：快速模式
            		RecogService.recogModel = false;
            	}else{
					//true  精准模式
            		RecogService.recogModel = true;
            	}
            	video_intent = new Intent(MainActivity.this,MemoryCameraActivity.class);
            	if (Build.VERSION.SDK_INT >= 23) {
    				CheckPermission checkPermission = new CheckPermission(MainActivity.this);
    				if (checkPermission.permissionSet(PERMISSION)) {
    					PermissionActivity.startActivityForResult(MainActivity.this,0,"true",  PERMISSION);

    				} else {
    					video_intent.setClass(getApplicationContext(), MemoryCameraActivity.class);
						video_intent.putExtra("camera", true);
    	            	startActivity(video_intent);
						finish();
    				}
    			} else {				
    				video_intent.setClass(getApplicationContext(), MemoryCameraActivity.class);
					video_intent.putExtra("camera", true);
                	startActivity(video_intent);
					finish();
    			}
                dialog.dismiss();
              
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            	if(isTouch){
            		if(index==0){
            			index = 1;
            		}else{
            			index =0;
            		}
            		isTouch = false;
            	}
            	  dialog.dismiss();
            }
        });
        builder.show();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (SYSTEM_RESULT_CODE == requestCode && resultCode == Activity.RESULT_OK) {			
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("recogImagePath", pic);
            startActivity(intent);
			finish();
		}
		if (requestCode == SELECT_RESULT_CODE && resultCode == Activity.RESULT_OK) {
			String picPathString = null;
			Uri uri = data.getData();
			picPathString = Utils.getPath(getApplicationContext(), uri);
			if(picPathString != null && !picPathString.equals("") && !picPathString.equals(" ") && !picPathString.equals("null")){
				File file = new File(picPathString);
				Intent intentResult = new Intent(getApplicationContext(),ResultActivity.class);
				if(!file.exists()||file.isDirectory()){
					Toast.makeText(this, "请选择一张正确的图片", Toast.LENGTH_SHORT).show();
				}else{
					if (picPathString.endsWith(".jpg") || picPathString.endsWith(".JPG")|| picPathString.endsWith(".PNG")|| picPathString.endsWith(".png")) {
						intentResult.putExtra("recogImagePath", picPathString);
						startActivity(intentResult);
						finish();
					} else {
						Toast.makeText(this, "请选择一张正确的图片,暂不支持PNG图像识别", Toast.LENGTH_SHORT).show();
					}
				}

			}


		}

	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
        	finish();
        }
        return super.onKeyDown(keyCode, event);
    }
   public void CreatViewtoAuthservice(){

   		editText = new EditText(getApplicationContext());
	   	editText.setTextColor(Color.BLACK);

	   	editText.setText("TQGBQRWMFFUY39PYY5LBYY987");
			new  AlertDialog.Builder(MainActivity.this)
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
		            Intent authIntent = new Intent(MainActivity.this, AuthService.class);
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
			        new  AlertDialog.Builder(MainActivity.this)    
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		LogcatHelper.getInstance(this).stop();
	}
}
