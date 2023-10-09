package com.example.laba6.classes;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.laba6.MainActivity;
import com.example.laba6.R;
import com.example.laba6.ReminderActivity;
import com.example.laba6.TriggeredReminder;
import com.example.laba6.models.Reminder;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Создание объекта напоминания
        Reminder reminder = new Reminder(
                intent.getIntExtra("id", 0),
                intent.getStringExtra("Title"),
                intent.getStringExtra("Descriptions"),
                intent.getStringExtra("ReminderDate")
        );

        //Создание рингтона
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        //Создание намерения для перехода и передача данных
        Intent notificationIntent = new Intent(context, TriggeredReminder.class);
        notificationIntent.putExtra("ID", reminder.getId());
        notificationIntent.putExtra("TITLE", reminder.getTitle());
        notificationIntent.putExtra("DESC", reminder.getDescription());
        notificationIntent.putExtra("DATE", reminder.getDateTime().toString());

        //Ставим флаги на открытие|закрытие задачи
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Создаем ожидаемое намерение
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //Создаём канал для уведомлений
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("101","Reminders", NotificationManager.IMPORTANCE_HIGH);
        }

        //Настраиваем уведомление
        Notification notification = builder
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setSound(alarmsound)
                .setContentTitle(reminder.getTitle())
                .setContentText(reminder.getDescription())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setChannelId("101")
                .build();

        //Создаём уведомление
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(reminder.getId(), notification);
    }
}
