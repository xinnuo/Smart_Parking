package com.ruanmeng.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ruanmeng.park_inspector.MessageActivity;
import com.ruanmeng.park_inspector.R;
import com.ruanmeng.utils.SoundHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        SoundHelper soundHelper = SoundHelper.getInstande(context);

        Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

            // processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            try {
                JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                if (!json.isNull("yxtype")) {
                    String yxtype = json.optString("yxtype");
                    switch (yxtype) {
                        case "1":
                            soundHelper.palyOne();
                            break;
                        case "2":
                            soundHelper.palyTwo();
                            break;
                        case "3":
                            soundHelper.palyThree();
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            try {
                JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                if (!json.isNull("type")) {
                    String push_type = json.optString("type");
                    if ("REGION".equals(push_type)) {
                        intent = new Intent(context, MessageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[JPushReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                        Log.i(TAG, "This message has no Extra data");
                        continue;
                    }

                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next();
                            sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Get message extra JSON error!");
                    }

                    break;
                default:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 实现自定义推送声音
     */
    private void processCustomMessage(Context context, Bundle bundle) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "channel_name",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.canBypassDnd();                                 //是否绕过请勿打扰模式
            channel.enableLights(true);                             //闪光灯
            channel.setLockscreenVisibility(VISIBILITY_SECRET);     //锁屏显示通知
            channel.setLightColor(Color.RED);                       //闪关灯的灯光颜色
            channel.canShowBadge();                                 //桌面launcher的消息角标
            channel.enableVibration(true);                          //是否允许震动
            channel.getAudioAttributes();                           //获取系统通知响铃声音的配置
            channel.getGroup();                                     //获取通知取到组
            channel.setBypassDnd(true);                             //设置可绕过  请勿打扰模式
            channel.shouldShowLights();                             //是否会有灯光
            channel.setVibrationPattern(new long[]{100, 200, 300, 400}); //设置震动模式
            channel.setSound(
                    Uri.parse("android.resource://" + context.getPackageName() + "/raw/park_ring01"),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "channel_id");

        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String messageId = bundle.getString(JPushInterface.EXTRA_MSG_ID);

        notification.setAutoCancel(true)
                .setContentText(message)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.park_ring01));

        Intent mIntent = new Intent(context, MessageActivity.class);
        mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        notification.setContentIntent(pendingIntent);
        assert messageId != null;
        notificationManager.notify(messageId.hashCode(), notification.build());
    }

}
