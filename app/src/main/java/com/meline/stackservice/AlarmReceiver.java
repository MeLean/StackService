package com.meline.stackservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String START_ALARM = "milen.com.stackservice.START_ALARM";
    private static final String RESTART_ALARM = "milen.com.stackservice.RESTART_ALARM";
    private SharedPreferencesUtils utilsSP;
    AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        utilsSP = new SharedPreferencesUtils(context, context.getString(R.string.sh_name));
        switch (action){
            case START_ALARM:
                calculateNextDate(context);
                Intent intentLoad = new Intent(context, WebViewActivity.class);
                intentLoad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLoad);
                break;

            case "android.intent.action.BOOT_COMPLETED" :
                if(utilsSP.getBooleanFromSharedPreferences(context.getString(R.string.sh_isServerRunning))){
                    AlarmReceiver singleAlarm = new AlarmReceiver();
                    long waitingTimeLeft = manageWaitingTime(context);
                    Toast.makeText(context, String.format("The %s has been restarted\n It is going to load url again after %d seconds",
                            context.getString(R.string.app_name), waitingTimeLeft/1000), Toast.LENGTH_LONG).show();
                    singleAlarm.setSingleShotAlarm(context, waitingTimeLeft);
                }
                break;

            case RESTART_ALARM:
                AlarmReceiver alarm = new AlarmReceiver();
                calculateNextDate(context);
                alarm.setRepeatedlyAlarm(context, manageWaitingTime(context));
                break;
            default:
                break;
        }
    }

    public void setRepeatedlyAlarm(Context context, long waitingTime) {
        Intent startIntent = new Intent(context, this.getClass());
        startIntent.setAction(START_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, 0);
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), waitingTime, pendingIntent);
    }

    public void setSingleShotAlarm(Context context, long waitingTime) {
        Intent restartIntent = new Intent(context, this.getClass());
        restartIntent.setAction(RESTART_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, restartIntent, 0);
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + waitingTime, pendingIntent);
    }

    public void CancelAlarm(Context context) {
        Intent cancelIntent = new Intent(context, this.getClass());
        cancelIntent.setAction(START_ALARM);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private void calculateNextDate(Context context) {
        utilsSP = new SharedPreferencesUtils(context, context.getString(R.string.sh_name));
        int timeWaiting = utilsSP.getIntFromSharedPreferences(context.getString(R.string.sh_time));
        Date nextDate = CalendarUtils.addTimeToCurrentTime(timeWaiting);
        utilsSP.putStringInSharedPreferences(context.getString(R.string.sh_nextDate), CalendarUtils.stringifyDateInFormat(nextDate));
    }

    long manageWaitingTime(Context context) {
        utilsSP = new SharedPreferencesUtils(context, context.getString(R.string.sh_name));
        String nextDateStr = utilsSP.getStringFromSharedPreferences(context.getString(R.string.sh_nextDate));
        Date currentDate = CalendarUtils.getCurrentDate();
        Date nextDate = CalendarUtils.parseDateInFormat(nextDateStr);

        return nextDate.getTime() - currentDate.getTime();
    }
}
