package com.mosect.app.boxrender;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * 高版本Android MediaProjection需要在startForeground方法调用之后获取，因此需要把获取MediaProject逻辑放在服务中
 */
public class CaptureService extends Service {

    private final static String NOTIFICATION_CHANNEL_ID = "capture";
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            if (null == channel) {
                channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "录屏服务", NotificationManager.IMPORTANCE_LOW);
                nm.createNotificationChannel(channel);
            }
        }
        int intentFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            intentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, intentFlags);
        notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("屏幕捕获服务")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("正在录屏中……")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Intent data = intent.getParcelableExtra("data");
            if (null != data) {
                MediaProjectionManager manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                MediaProjection mediaProjection = manager.getMediaProjection(Activity.RESULT_OK, data);
                if (null != mediaProjection && null != OpenglActivity.getCurrent()) {
                    OpenglActivity.getCurrent().setMediaProjection(mediaProjection);
                }
            }
        }
        return START_STICKY_COMPATIBILITY;
    }
}
