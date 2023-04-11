package com.imdaad.saso.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class MyReceiverReboot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state= intent.getStringExtra(Intent.ACTION_BOOT_COMPLETED);
        if(intent.getAction() =="android.intent.action.BOOT_COMPLETED"){
          //  Toast.makeText(context, "BOOT_COMPLETED", Toast.LENGTH_LONG).show();
            CallReceivey myReceiver = new CallReceivey();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(TelephonyManager.EXTRA_STATE);
            context.getApplicationContext().registerReceiver(myReceiver,intentFilter);
        }
        if(intent.getAction() =="android.intent.action.REBOOT"){
         //   Toast.makeText(context, "REBOOT_COMPLETED", Toast.LENGTH_LONG).show();
            CallReceivey myReceiver = new CallReceivey();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(TelephonyManager.EXTRA_STATE);
            context.getApplicationContext().registerReceiver(myReceiver,intentFilter);
        }
    }
}