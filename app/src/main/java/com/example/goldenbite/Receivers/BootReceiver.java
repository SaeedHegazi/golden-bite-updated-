package com.example.goldenbite.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            ComponentName component = new ComponentName(context, BootReceiver.class);
            int status = context.getPackageManager().getComponentEnabledSetting(component);

            if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                scheduleAlarm(context);
            }
        }
    }
    private void scheduleAlarm(Context context){
        Intent intent = new Intent(context, OrderReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null){
            alarmManager.cancel(pendingIntent);
            long triggerTime = System.currentTimeMillis() + 5 * 1000;
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }
}
