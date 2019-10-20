package com.apps.juzhihua.notes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {
    String datetime, currentDateandTime, title, body;
    NoteDbHelper NoteDB;
    LabelDbHelper LabelDB;
    Bundle b;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NoteDB = new NoteDbHelper(context);
        LabelDB = new LabelDbHelper(context);

        //获取当前的日期时间，并显示在文本框Current_Creation_Datetime上
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        currentDateandTime = sdf.format(new Date());


        Cursor data = NoteDB.Notifications_getListContents();
        if (data.getCount() == 0) {

        } else {
            while (data.moveToNext()) {
                if (data.getString(5).equals(currentDateandTime)) {
                    if (data.getString(1).isEmpty() || data.getString(1).equals(null) || data.getString(1).equals("")) {
                        //什么也不做
                    } else {
                        title = data.getString(1);
                        body = data.getString(2);
                        datetime = " (" + data.getString(5) + ") ";

                        // 处理通知
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_alarm)
                                .setContentTitle(title + datetime)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_time_up))
                                .setContentText(body);
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(contentIntent);
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                        mNotifyMgr.notify(m, mBuilder.build());

                        //从设置中获取设置中的默认值
                        SharedPreferences sharedPreferences = context.getSharedPreferences("设置", MODE_PRIVATE);
                        Uri default_notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        String link = sharedPreferences.getString("提醒声音", default_notification_sound.toString());
                        Uri uri = Uri.parse(link);
                        Ringtone rt = RingtoneManager.getRingtone(context, uri);
                        rt.play();
                        break;
                    }
                }
            }
        }
    }
}