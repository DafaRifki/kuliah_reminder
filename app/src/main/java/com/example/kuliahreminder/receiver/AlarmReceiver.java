package com.example.kuliahreminder.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.activity.MainActivity;

public class AlarmReceiver extends BroadcastReceiver{
    private static final String CHANNEL_ID = "schedule_reminder_channel";
    private static final String CHANNEL_NAME = "Schedule Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get data from intent
        String namaMatkul = intent.getStringExtra("nama_matkul");
        String waktu = intent.getStringExtra("waktu");
        String ruangan = intent.getStringExtra("ruangan");
        String jenis = intent.getStringExtra("jenis");
        int scheduleId = intent.getIntExtra("id", -1);

        // Create notification
        showNotification(context, namaMatkul, waktu, ruangan, jenis, scheduleId);
    }

    private void showNotification(Context context, String namaMatkul, String waktu,
                                  String ruangan, String jenis, int scheduleId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for schedule reminders");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to open app when notification is clicked
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                scheduleId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification message
        String contentText = String.format("Waktu: %s", waktu);
        if (ruangan != null && !ruangan.isEmpty()) {
            contentText += String.format(" | Ruangan: %s", ruangan);
        }

        // Notification sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentTitle("🔔 " + namaMatkul)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText + "\n" + jenis))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setContentIntent(pendingIntent);

        // Show notification
        notificationManager.notify(scheduleId, builder.build());
    }
}
