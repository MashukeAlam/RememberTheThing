package com.jimx.remeberthething;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

public class ClipSenseService extends Service {
    int startMode;       // indicates how to behave if the service is killed
    IBinder binder;      // interface for clients that bind
    boolean allowRebind; // indicates whether onRebind should be used
    Timer clipCheckTimer = new Timer();

    @Override
    public void onCreate() {
        // The service is being created
        Toast.makeText(getApplicationContext(), "Service Started!", Toast.LENGTH_LONG).show();
        Log.d("JIM", "LLLL");
        starTimer();
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

    }

    private void starTimer() {
        clipCheckTimer.scheduleAtFixedRate(new Check(), 0, 2000);
    }

    private class Check extends TimerTask

    {
        String previous = "";
        public void run()
        {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = clipboardManager.getPrimaryClip();
            ClipData.Item item = data.getItemAt(0);
//            Toast.makeText(getApplicationContext(), item.getText().toString() , Toast.LENGTH_LONG).show();
//            Log.d("JIM", item.getText().toString());
            String now = item.getText().toString();
            if(!previous.equals(now)) {
                previous = now.toString();
                writeToFile(item.getText().toString(), getApplicationContext());

            }
            Log.d("JIM", readFromFile(getApplicationContext()));

        }

        private void writeToFile(String data,Context context) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_APPEND));
                outputStreamWriter.append(data + "\n");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        private String readFromFile(Context context) {

            String ret = "";

            try {
                InputStream inputStream = context.openFileInput("config.txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        Log.d("JIM", receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            return ret;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        return Service.START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Toast.makeText(getApplicationContext(), "Service Destroyed!", Toast.LENGTH_LONG).show();
    }
}