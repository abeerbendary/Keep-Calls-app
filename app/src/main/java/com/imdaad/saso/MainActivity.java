package com.imdaad.saso;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imdaad.saso.model.CallReceivey;
import com.imdaad.saso.model.MyReceiverReboot;
import com.imdaad.saso.model.MyService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
public class MainActivity extends AppCompatActivity {
TextView textView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEditor;
    SharedPreferences sp ;
   // public static File dir = new File(new File(Environment.getExternalStorageDirectory(), "bleh"), "bleh");
int x=0;
    String s=  Environment.DIRECTORY_DCIM;
    private String stringFile = Environment.getExternalStoragePublicDirectory(s).getAbsolutePath() + "/Android/data" + File.separator + "saso.txt";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG},PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
sp= getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        ifHuaweiAlert();
        if("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !sp.getBoolean("protected",false)) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(this);
            builder.setTitle("huawei_headline").setMessage("huawei_text")
                    .setPositiveButton("go_to_protected", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
                            sp.edit().putBoolean("protected",true).commit();
                        }
                    }).create().show();
        }
//        textView=findViewById(R.id.logs);
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri uriCallLogs = Uri.parse("content://call_log/calls");
        Cursor cursorCallLogs = getContentResolver().query(uriCallLogs, null,null,null);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},369);
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//            Geolocator.openLocationSettings();
        }
//        sharedPreferences = this.getSharedPreferences("SharedPreference", MODE_PRIVATE);
//        prefsEditor = sharedPreferences.edit();
//        // replace value of empId with userID
//        prefsEditor.putBoolean("broadcastTriggerd", false);
//        prefsEditor.commit();
      //unregisterReceiver(new CallReceivey());
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onStart() {
        super.onStart();
        int x=0;
        CallReceivey myReceiver = new CallReceivey();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.EXTRA_STATE);
        registerReceiver(myReceiver,intentFilter);

        MyReceiverReboot myReceiverReboot = new MyReceiverReboot();
        IntentFilter intentFilters = new IntentFilter();
        intentFilters.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(myReceiverReboot,intentFilters);
        ////////////////////////

        String rootPath = Environment.getExternalStoragePublicDirectory(s).getAbsolutePath() + "/Android/data";
        String stringOutput="";
        File root = new File(rootPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
               return;
            }
            if (!root.exists()) {
                root.mkdirs();
            }
        }

        File file = new File(rootPath,
                "saso.txt");
        FileOutputStream fos = null;
        if (!file.exists()) {
            try {
                root.mkdirs();
                file.createNewFile();
                Toast.makeText(this, "creat file right", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fos = new FileOutputStream(file,true);
            fos.write(stringOutput.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent serviceIntent = new Intent(this,
                MyService.class);
        startService(serviceIntent);


    }

    @Override
    protected void onStop() {
        super.onStop();
        CallReceivey myReceiver = new CallReceivey();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.EXTRA_STATE);
        registerReceiver(myReceiver,intentFilter);

    }

    public void play(View view) {
        x++;
        if(x==7) {
            File file = new File(stringFile);
            if (!file.exists()) {
                Toast.makeText(this, "File doesn't exists", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("text/plain");
            intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));
            startActivity(Intent.createChooser(intentShare, "Share the file ..."));
        }
    }

    private void ifHuaweiAlert() {
        final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(this);
                dontShowAgain.setText("Do not show again");
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Protected Apps", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }
}