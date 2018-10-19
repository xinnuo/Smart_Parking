package com.amap.api;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

/**
 * 前台定位service
 */
public class LocationForegoundService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private static final int NOTIFICATION_ID = 520;
    private static final int NOTIFICATION_REQUEST = 110;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Android O上才显示通知栏
        if(Build.VERSION.SDK_INT >= 26) showNotify();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true); //停止前台服务--参数：表示是否移除之前的通知
        super.onDestroy();
    }

    //显示通知栏
    public void showNotify() {

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_REQUEST,
                new Intent(/*点击时跳转的Activity*/),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the info for the views that show in the notification panel.
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), 0/*resourcesId资源图片*/))
                .setSmallIcon(0/*resourcesId资源图片*/)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setContentTitle("正在后台定位")
                .setContentText("定位进行中")
                .setOngoing(true)
                .build();
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;

        // Send the notification. 设置前台服务
        startForeground(NOTIFICATION_ID, notification);
    }

    public class LocalBinder extends Binder {
        LocationForegoundService getService() {
            return LocationForegoundService.this;
        }
    }
}
