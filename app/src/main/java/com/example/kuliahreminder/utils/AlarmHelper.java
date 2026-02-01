package com.example.kuliahreminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.kuliahreminder.model.Schedule;
import com.example.kuliahreminder.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";
    private static final int REMINDER_MINUTES_BEFORE = 15; // 15 menit sebelum

    /**
     * Set alarm untuk schedule
     */
    public static void setAlarm(Context context, Schedule schedule) {
        if (!schedule.isNotificationEnabled()) {
            Log.d(TAG, "Notification disabled for schedule: " + schedule.getId());
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Put schedule data
        intent.putExtra("schedule_id", schedule.getId());
        intent.putExtra("nama_matkul", schedule.getNamaMatkul());
        intent.putExtra("waktu", schedule.getWaktuMulai());
        intent.putExtra("ruangan", schedule.getRuangan());
        intent.putExtra("jenis", schedule.getJenis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                schedule.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Calculate alarm time
        Calendar alarmTime = getAlarmTime(schedule);

        if (alarmTime != null) {
            long alarmTimeMillis = alarmTime.getTimeInMillis();
            long currentTimeMillis = System.currentTimeMillis();

            // Format tanggal untuk log
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss",
                    new Locale("id", "ID"));
            String alarmTimeStr = sdf.format(new Date(alarmTimeMillis));
            String currentTimeStr = sdf.format(new Date(currentTimeMillis));

            Log.d(TAG, "===== SETTING ALARM =====");
            Log.d(TAG, "Schedule: " + schedule.getNamaMatkul());
            Log.d(TAG, "Day: " + schedule.getHari());
            Log.d(TAG, "Time: " + schedule.getWaktuMulai());
            Log.d(TAG, "Current Time: " + currentTimeStr);
            Log.d(TAG, "Alarm Time: " + alarmTimeStr);
            Log.d(TAG, "Time until alarm: " + ((alarmTimeMillis - currentTimeMillis) / 1000 / 60) + " minutes");
            Log.d(TAG, "========================");

            // check if alarm time is in the future
            if (alarmTimeMillis > currentTimeMillis) {
                try {
                    // Set exact alarm
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                alarmTime.getTimeInMillis(),
                                pendingIntent
                        );
                    } else {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                alarmTime.getTimeInMillis(),
                                pendingIntent
                        );
                    }

                    Log.d(TAG, "Alarm set for: " + schedule.getNamaMatkul() +
                            " at " + alarmTime.getTime().toString());
                } catch (SecurityException e) {
                    Log.e(TAG, "❌ SecurityException: " + e.getMessage());
                    Toast.makeText(context, "Gagal mengatur alarm. Periksa izin aplikasi.",
                            Toast.LENGTH_LONG).show();
                }
            }else {
                Log.w(TAG, "⚠️ Alarm time is in the past, skipping...");
            }
        }else {
            Log.e(TAG, "❌ Failed to calculate alarm time");
        }
    }

    /**
     * Cancel alarm untuk schedule
     */
    public static void cancelAlarm(Context context, int scheduleId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                scheduleId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Alarm cancelled for schedule ID: " + scheduleId);
    }

    /**
     * Calculate alarm time (15 menit sebelum jadwal dimulai)
     */
    private static Calendar getAlarmTime(Schedule schedule) {
        try {
            // Parse waktu mulai (format: HH:mm)
            String[] timeParts = schedule.getWaktuMulai().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Get day of week
            int dayOfWeek = getDayOfWeek(schedule.getHari());

            // Set calendar
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // Kurangi 15 menit
            calendar.add(Calendar.MINUTE, -REMINDER_MINUTES_BEFORE);

            // Jika waktu sudah lewat minggu ini, set untuk minggu depan
            Calendar now = Calendar.getInstance();
            if (calendar.before(now)) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            return calendar;

        } catch (Exception e) {
            Log.e(TAG, "Error calculating alarm time: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert hari string to Calendar day constant
     */
    private static int getDayOfWeek(String hari) {
        switch (hari) {
            case "Minggu":
                return Calendar.SUNDAY;
            case "Senin":
                return Calendar.MONDAY;
            case "Selasa":
                return Calendar.TUESDAY;
            case "Rabu":
                return Calendar.WEDNESDAY;
            case "Kamis":
                return Calendar.THURSDAY;
            case "Jumat":
                return Calendar.FRIDAY;
            case "Sabtu":
                return Calendar.SATURDAY;
            default:
                return Calendar.MONDAY;
        }
    }

    /**
     * Set all alarms untuk semua schedule user
     */
    public static void setAllAlarms(Context context, java.util.List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            setAlarm(context, schedule);
        }
        Log.d(TAG, "Set alarms for " + schedules.size() + " schedules");
    }

    /**
     * Cancel all alarms untuk semua schedule user
     */
    public static void cancelAllAlarms(Context context, java.util.List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            cancelAlarm(context, schedule.getId());
        }
        Log.d(TAG, "Cancelled alarms for " + schedules.size() + " schedules");
    }
}
