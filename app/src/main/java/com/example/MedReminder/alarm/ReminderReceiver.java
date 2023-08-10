package com.example.MedReminder.alarm;

import static android.app.Service.START_STICKY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.example.MedReminder.R;

public class ReminderReceiver extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // קוד טיפול בתזכורות והפעלת ההתראות ייכתב כאן


        // לדוגמה:
        Toast.makeText(this, "ReminderService is running", Toast.LENGTH_SHORT).show();

        // חשוב להחזיר ערך של START_STICKY כדי להבטיח שהשירות יופעל מחדש אם יעצור באופן לא רגיל
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showHeadsUpNotification(Context context) {
        // יצירת עצם של ההתראה
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_id"; // ערך ייחודי לערוץ ההתראה

        // ייצור ערוץ התראה (רק ב-Android 8.0 ומעלה)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel"; // שם ערוץ ההתראה
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // הכנת פרטי ההתראה
        Notification.Builder notificationBuilder = null;// עדיפות גבוהה של ההתראה
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context, channelId)
                    .setSmallIcon(R.drawable.icon_blister) // תמונת הצגת ההתראה בסמל האפליקציה
                    .setContentTitle("הודעת תזכורת") // כותרת ההתראה
                    .setContentText("טקסט של תזכורת") // תוכן ההתראה
                    .setPriority(Notification.PRIORITY_HIGH);
        }

        // פעולת התגובה של ההתראה כאשר המשתמש לוחץ עליה
        Intent intent = new Intent(context, ReminderActivity.class); // YourActivity יכול להיות פעילות או פעולת BroadcastReceiver
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // הצגת ההתראה
        notificationManager.notify(0, notificationBuilder.build());
    }
}