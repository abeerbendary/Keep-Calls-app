package com.imdaad.saso.model;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.RequiresApi;

import static android.content.Context.MODE_PRIVATE;

public class CallReceivey extends BroadcastReceiver {
    public Context c;
    int s = 0;
    boolean broadcastTriggerd = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEditor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = "";
        sharedPreferences = context.getSharedPreferences("SharedPreference", MODE_PRIVATE);
        broadcastTriggerd = sharedPreferences.getBoolean("broadcastTriggerd", false);
        c = context;

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String action = intent.getAction();
        if (state == null) {
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i("tag", "Outgoing number : " + number);
        }
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
       //       Toast.makeText(context, "Ringing..." + number, Toast.LENGTH_SHORT).show();
//            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
//            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        if (
                state.equals(TelephonyManager.EXTRA_STATE_IDLE)
//                 action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
//                 && intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)
        ) {
            final Calendar[] calendar = new Calendar[1];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            calendar[0] = Calendar.getInstance();
            calendar[0].add(Calendar.SECOND, 2);
            String currentDate = sdf.format(calendar[0].getTime());
            String history = sharedPreferences.getString("day", "");
            try {
                if (history == "" || (sdf.parse(currentDate).getTime() - sdf.parse(history).getTime()) / 1000 > 30) {
                    Log.e("Zed", "idle");
                  //      Toast.makeText(context, "Abeer" + number, Toast.LENGTH_LONG).show();
                    final String[] stringOutput = {""};
                    prefsEditor = sharedPreferences.edit();
                    calendar[0] = Calendar.getInstance();
                    prefsEditor.putString("day", sdf.format(calendar[0].getTime()));
                    prefsEditor.commit();
                   //Handler handler = new Handler();
                  //handler.postDelayed(new Runnable() {
                 //   public void run() {
                            Uri uriCallLogs = Uri.parse("content://call_log/calls");
                           // Cursor cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null, android.provider.CallLog.Calls.DATE + " DESC ");
                    Cursor cursorCallLogs = context.getContentResolver().query(uriCallLogs, null, null, null, android.provider.CallLog.Calls.DATE +  " DESC ");

                            cursorCallLogs.moveToNext();
                            @SuppressLint("Range") String stringNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
                            @SuppressLint("Range") String stringRepeate = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.LAST_MODIFIED));
//                         @SuppressLint("Range") String stringRepeate = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DATE));
                            @SuppressLint("Range") String stringName = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME));
                            @SuppressLint("Range") String stringDuration = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION));
                            @SuppressLint("Range") String stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
                            @SuppressLint("Range") String dates = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DATE));
                            cursorCallLogs.close();
                            if (stringType.equals("1")) {
                                stringType = "incoming";
                            } else if (stringType.equals("2")) {
                                stringType = "outgoing";
                            } else if (stringType.equals("5")) {
                                stringType = "Rejected";
                            }

                            double Duration = Double.parseDouble(stringDuration);
                            if (Duration >= 3600) {
                                Duration = Duration / 3600;
                                stringDuration = Duration + "H";
                            } else if (Duration >= 60) {
                                Duration = Duration / 60;
                                stringDuration = Duration + "m";
                            } else if (Duration < 60) {
                                stringDuration = Duration + "S";
                            }
//                                stringOutput[0] = "Number: " + stringNumber
//                                        + "\nTime: " + sdf.format(new Date(Long.valueOf(dates)))
//                                        + "\nName: " + stringName
//                                        + "\nDuration: " + stringDuration
//                                        + "\n Type: " + stringType
//                                        + "\n Repeate: " + stringRepeate
//                                        +"\n\n"
//                                        + "*********************" + "\n\n";
                            /////////////////////////////////////
                            Date c = Calendar.getInstance().getTime();
                            System.out.println("Current time => " + c);

                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
                            final String formattedDate = df.format(c);
//
//                            long timeInMilliseconds = c.getTime();
//                            long timeInMiliDayBefore = timeInMilliseconds - 86400000;

//                            String[] projectionMissed = {CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE};
//                            String whereMissed = CallLog.Calls.NUMBER + "=" + stringNumber + " AND " + CallLog.Calls.DATE + ">=" + timeInMiliDayBefore;
//                            Cursor m = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projectionMissed, whereMissed, null, null);
//                            m.moveToFirst();


//                                String[] projectionIncoming = { CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE };
//                                String whereIncoming = CallLog.Calls.TYPE+"="+CallLog.Calls.INCOMING_TYPE+ " AND " + CallLog.Calls.DATE + ">=" + timeInMiliDayBefore;
//                                Cursor i = getContentResolver().query(CallLog.Calls.CONTENT_URI, projectionIncoming,whereIncoming , null, null);
//                                i.moveToFirst();
//
//                                String[] projectionOutgoing = { CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE };
//                                String whereOutgoing = CallLog.Calls.TYPE+"="+CallLog.Calls.OUTGOING_TYPE+ " AND " + CallLog.Calls.DATE + ">=" + timeInMiliDayBefore;
//                                Cursor o = getContentResolver().query(CallLog.Calls.CONTENT_URI, projectionOutgoing,whereOutgoing , null, null);
//                                o.moveToFirst();

//                                String numberOfIn = String.valueOf(i.getCount());
//                                String numberOfOut = String.valueOf(o.getCount());
                         //   String numberOfMiss = String.valueOf(m.getCount());
                            stringOutput[0] = "Number: " + stringNumber
                                    + "\nTime: " + sdf.format(new Date(Long.valueOf(dates)))
                                    + "\nName: " + stringName
                                    + "\nDuration: " + stringDuration
                                    + "\n Type: " + stringType
//                                    + "\n Repeate: " + numberOfMiss
                                    + "\n\n"
                                    + "*********************" + "\n\n";
///////////////////////////////////////////
                       //    Toast.makeText(context, stringOutput[0], Toast.LENGTH_SHORT).show();
                            //device abeer
                            String s = Environment.DIRECTORY_DCIM;
                            String rootPath = Environment.getExternalStoragePublicDirectory(s).getAbsolutePath() + "/Android/data";

                            File root = new File(rootPath);
                            if (!root.exists()) {
                                root.mkdirs();
                            }
                            File file = new File(rootPath,
                                    "saso.txt");
                            FileOutputStream fos = null;
                            if (!file.exists()) {
                                try {
                                    root.mkdirs();
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                fos = new FileOutputStream(file, true);
                                fos.write(stringOutput[0].getBytes("UTF-8"));
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
               //    }, 4000);

                    state = null;
                    //return;
             // }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

}
