package com.example.dennisjohansson8.tracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class StopWatchActivity extends AppCompatActivity {
    TextView hours, minutes, seconds, milliseconds;
    Button temp;
    Timer timer;
    final Handler handler = new Handler();
    String timeCorrection, time;
    int h = 0, min = 0, sec = 0, millisec = 0, permissionCode = 10;
    GPS GPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        SQLiteDatabase tempDB = openOrCreateDatabase("tracker", Context.MODE_PRIVATE, null);
        Util.db = new LocalDB(tempDB);
        //insertStuff();


        hours = (TextView) findViewById(R.id.stopwatch_hours);
        minutes = (TextView) findViewById(R.id.stopwatch_minutes);
        seconds = (TextView) findViewById(R.id.stopwatch_seconds);
        milliseconds = (TextView) findViewById(R.id.stopwatch_miliseconds);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "don't have permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, permissionCode);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                GPS = new GPS(this, this);
            }
        } else {
            GPS = new GPS(this, this);
        }
        checkGPS();
    }

    public void switchToMenu(View view) {
        Intent toMenu = new Intent(this, MenuActivity.class);
        startActivity(toMenu);
    }

    /**
     * @param view starts the stopwatch
     */
    public void startWatch(View view) {
        Log.d("debug", "start");
        timer = new Timer();
        GPS.start();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            updateTime();
                        } catch (Exception e) {
                            Log.d("debug", "timer task failed");
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10);
        //disables startButton
        temp = (Button) findViewById(R.id.stopwatch_start);
        temp.setAlpha((float) 0.73);
        temp.setClickable(false);

        //switch to pause Button
        temp = (Button) findViewById(R.id.stopwatch_stop);
        temp.setText(R.string.pause);
        temp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pauseWatch(v);
                                    }
                                }
        );
    }

    /**
     * handel the time logic
     */
    void updateTime() {
        millisec++;

        if (millisec == 100) {
            millisec = 0;
            timeCorrection = "0";
            time = timeCorrection + millisec;
            milliseconds.setText(time);
            sec++;
            if (sec == 60) {
                sec = 0;
                timeCorrection = "0";
                time = timeCorrection + sec;
                seconds.setText(time);
                min++;
                if (min == 60) {
                    min = 0;
                    timeCorrection = "0";
                    time = timeCorrection + min;
                    minutes.setText(time);

                    h++;
                    timeCorrection = (h < 10) ? "0" : "";
                    time = timeCorrection + h;
                    hours.setText(time);
                } else {
                    timeCorrection = (min < 10) ? "0" : "";
                    time = timeCorrection + min;
                    minutes.setText(time);
                }
            } else {
                timeCorrection = (sec < 10) ? "0" : "";
                time = timeCorrection + sec;
                seconds.setText(time);
            }
        } else {
            timeCorrection = (millisec < 10) ? "0" : "";
            time = timeCorrection + millisec;
            milliseconds.setText(time);
        }
    }

    /**
     * @param view pauses the stopwatch
     */
    public void pauseWatch(View view) {
        GPS.pause();
        timer.cancel();
        //enable startButton
        temp = (Button) findViewById(R.id.stopwatch_start);
        temp.setAlpha(1);
        temp.setClickable(true);
        //switch to reset button
        temp = (Button) findViewById(R.id.stopwatch_stop);
        temp.setText(R.string.reset);
        temp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        resetWatch(v);
                                    }
                                }
        );

    }

    /**
     * @param view restarts the stopwatch and gps
     */
    public void resetWatch(View view) {
        GPS.reset();
        h = 0;
        min = 0;
        sec = 0;
        millisec = 0;

        hours.setText(R.string.default_clock_text);
        minutes.setText(R.string.default_clock_text);
        seconds.setText(R.string.default_clock_text);
        milliseconds.setText(R.string.default_clock_text);
        Log.d("debug", "reset");
    }

    /* void insertStuff() {
         try {
             Util.db.execute("INSERT INTO lap (m_p_s,location) VALUES ('0┼0┼0┼3┼4┼7┼9┼5┼4┼3┼','54,96,2┼')");
         } catch (Exception e) {
             Log.d("debug", "can't connect to db");
         }
     }*/
    void checkGPS() {
        Log.d("debug", "1");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.d("debug", "3");
        builder.setMessage("For this app to be able to track your speed GPS is required, turn on GPS?");
        Log.d("debug", "4");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        turnOnGPS();
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Log.d("debug", "5");
        builder.show();
        Log.d("debug", "6");
    }

    void turnOnGPS() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
/*        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        sendBroadcast(intent);*/
    }
}
