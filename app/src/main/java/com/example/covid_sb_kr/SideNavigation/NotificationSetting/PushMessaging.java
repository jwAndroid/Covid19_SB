package com.example.covid_sb_kr.SideNavigation.NotificationSetting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.covid_sb_kr.Fragment.HomeFragment;
import com.example.covid_sb_kr.MainActivity;
import com.example.covid_sb_kr.R;
import com.example.covid_sb_kr.Utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushMessaging extends FirebaseMessagingService {

    /*Reference : D:pushMessage 프로젝트확인 */

    boolean flag;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FCM_TEST", s);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        flag = true;
        System.out.println("fcm 온크레이이트 플래그 : " + flag);
    }

    private boolean getPreferences(String key){
        SharedPreferences settings = getSharedPreferences("SPFS", 0);
        boolean value = settings.getBoolean(key, false);
        return value;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        boolean state = getPreferences("switchkey");
        System.out.println("스테이트 : " + state);
        flag = state;

        if (flag){

            if (remoteMessage.getNotification() != null){

                String title = remoteMessage.getNotification().getTitle();//firebase에서 보낸 메세지의 title
                String message = remoteMessage.getNotification().getBody();//firebase에서 보낸 메세지의 내용

                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String channel = "채널";
                    String channel_nm = "채널명";

                    NotificationManager notichannel = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                    channelMessage.setDescription("채널에 대한 설명.");
                    channelMessage.enableLights(true);
                    channelMessage.enableVibration(true);
                    channelMessage.setShowBadge(false);
                    channelMessage.setVibrationPattern(new long[]{1000, 1000});
                    notichannel.createNotificationChannel(channelMessage);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this, channel)
                                    .setSmallIcon(R.drawable.icon1)
                                    .setContentTitle(title)//푸시알림의 제목
                                    .setContentText(message)//푸시알림의 내용
                                    .setChannelId(channel)
                                    .setAutoCancel(true)//선택시 자동으로 삭제되도록 설정.
                                    .setContentIntent(pendingIntent)//알림을 눌렀을때 실행할 인텐트 설정.
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(9999, notificationBuilder.build());

                } else {
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this, "")
                                    .setSmallIcon(R.drawable.icon1)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(9999, notificationBuilder.build());

                }
            }

        }else{
            System.out.println("플래그는 ? : " + flag);
        }

    }
}
