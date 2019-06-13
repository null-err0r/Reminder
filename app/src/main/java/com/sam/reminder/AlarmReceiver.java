package com.sam.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;


import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {

    private final int MINUTES = 1, HOURLY = 2, DAILY = 3, WEEKLY = 4, MONTHLY = 5, YEARLY = 6;

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id", 0);
        String title = intent.getStringExtra("title");
        String msg = intent.getStringExtra("msg");

        reminderDatabase database = new reminderDatabase(context);
        Cursor cursor = database.getItem(id);
        cursor.moveToFirst();

        int frequency = cursor.getInt(cursor.getColumnIndex(reminderDatabase.DB_COLUMN_FREQUENCY));
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(reminderDatabase.DB_COLUMN_TIME)));

        if (frequency > 0) {
            if (frequency == MINUTES) {
               time.add(Calendar.MINUTE,5);
            }
            else if (frequency == HOURLY) {
                time.add(Calendar.HOUR, 1);

            } else if (frequency == DAILY) {
                time.add(Calendar.DAY_OF_MONTH, 1);

            } else if (frequency == WEEKLY) {
                time.add(Calendar.WEEK_OF_MONTH, 1);
            } else if (frequency == MONTHLY) {
                time.add(Calendar.MONTH, 1);

            } else if (frequency == YEARLY) {
                time.add(Calendar.YEAR, 1);

            }
            database.updateTime(id, time.getTimeInMillis());
            Intent setAlarm = new Intent(context, AlarmService.class);
            setAlarm.putExtra("id", id);
            setAlarm.setAction(AlarmService.CREATE);
            context.startService(setAlarm);
        }

        Intent result = new Intent(context, createOrEditAlert.class);
        result.putExtra("ID", id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(createOrEditAlert.class);
        stackBuilder.addNextIntent(result);
        PendingIntent clicked = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_calendar_check_black_48dp)
                .setContentTitle(title)
                .setContentText(" ")
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setContentIntent(clicked)
                .setAutoCancel(true)

                .build();


        n.defaults |= Notification.DEFAULT_VIBRATE;
        n.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        n.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, n);


    }

}
