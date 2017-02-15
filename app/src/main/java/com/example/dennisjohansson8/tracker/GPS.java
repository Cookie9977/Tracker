package com.example.dennisjohansson8.tracker;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GPS extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleClient;
    LocationRequest request;
    public Location location;
    final int GPS_MIN_UPDATE_TIME = 1000;
    Context context;
    Activity activity;
    LapLogic logic;
    boolean yellowFlag = false, redFlag = false;
    double speed, lastNonRepeatedSpeed;

    GPS(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
        logic = new LapLogic();
        buildApi();
        createRequest();
    }

    /**
     * creates the google api
     */
    protected synchronized void buildApi() {
        mGoogleClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d("debug", "hello create");

    }

    /**
     * creates customised request for gps
     */
    protected synchronized void createRequest() {
        request = new LocationRequest();
        request.setInterval(GPS_MIN_UPDATE_TIME);
        request.setFastestInterval(GPS_MIN_UPDATE_TIME / 2);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        Log.d("debug", "hello start");
        mGoogleClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("debug", "hello stop");
        mGoogleClient.disconnect();
        super.onStop();
    }

    //don't work on emulators
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("debug", "hello connected");
        try {
            Log.d("debug", "1");
            this.location = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
            Log.d("debug", "1");
            Util.locationArray.add(null);
            Log.d("debug", "1");
            Util.locationArray.add(this.location);
            Log.d("debug", "1");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, request, this);
            Log.d("debug", "Location service started");
        } catch (SecurityException e) {
            Log.d("catch", "permissions required");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("debug", "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("debug", "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (this.location != null) {
            int deltaTime = (int) Math.round((double) (location.getElapsedRealtimeNanos() - this.location.getElapsedRealtimeNanos()) / 1000000000);
            Log.d("debug", "new time:" + location.getTime());
            Log.d("debug", "time: " + deltaTime);

            if (location.hasSpeed()) {
                speed = location.getSpeed();
                yellowFlag = false;
            } else {
                speed = calcSpeed(deltaTime, logic.calcDistance(this.location, location));
                yellowFlag = true;
            }
            if (speed < 0.2) {
                speed = 0;
            }
            if (logic.inBounds(lastNonRepeatedSpeed, speed, deltaTime)) {
                for (int i = 0; i < deltaTime; i++) {
                    Util.speedArray.add(speed);
                }
                redFlag = false;
                updateSpeed(speed);
            } else {
                for (int i = 0; i < deltaTime; i++) {
                    Util.speedArray.add(lastNonRepeatedSpeed);
                }
                redFlag = true;
                updateSpeed(speed);
            }
            lastNonRepeatedSpeed = (speed != lastNonRepeatedSpeed) ? speed : lastNonRepeatedSpeed;
            this.location = location;

            Log.d("debug", "size: " + Util.speedArray.size());
            Util.locationArray.add(this.location);
        } else {
            this.location = location;
        }
        Util.locationArray.add(location);
    }

    private double calcSpeed(int time, double distance) {
        return distance / time;
    }

    /**
     * sets speedometers in stopwatch to the current speed
     *
     * @param speed double
     */
    private void updateSpeed(double speed) {
        TextView ms = (TextView) activity.findViewById(R.id.stopwatch_meters_per_seconds);
        TextView kmh = (TextView) activity.findViewById(R.id.stopwatch_kilometers_per_hour);

        ms.setText(Util.decimalformat.format(speed));
        kmh.setText(Util.decimalformat.format(speed * 3.6));


        if (redFlag) {
            ms.setTextColor(ContextCompat.getColor(context, R.color.textRed));
            kmh.setTextColor(ContextCompat.getColor(context, R.color.textRed));
        } else if (yellowFlag) {
            ms.setTextColor(ContextCompat.getColor(context, R.color.textYellow));
            kmh.setTextColor(ContextCompat.getColor(context, R.color.textYellow));
        } else {
            ms.setTextColor(ContextCompat.getColor(context, R.color.text));
            kmh.setTextColor(ContextCompat.getColor(context, R.color.text));
        }
    }


    /**
     * starts google api
     */
    public void start() {
        mGoogleClient.connect();
    }

    /**
     * pauses google api
     */
    public void pause() {
        mGoogleClient.disconnect();
    }

    /**
     * resets everything to do with the gps and post to db
     */
    public void reset() {
        //TODO incase of implementation of maps uncomment stuff
        //save stuff to db
        StringBuilder speedBuilder = new StringBuilder();
/*        StringBuilder locationBuilder = new StringBuilder();
        for (int i = 0; i < Util.locationArray.size(); i++) {
            if (Util.locationArray.get(i) != null) {
                locationBuilder.append(Util.locationArray.get(i).getLongitude());
                locationBuilder.append(",");
                locationBuilder.append(Util.locationArray.get(i).getLatitude());
                locationBuilder.append(",");
                locationBuilder.append(Util.locationArray.get(i).getAltitude());
            } else {
                locationBuilder.append("-99");
            }
            locationBuilder.append("┼");
        }*/
        boolean moved = false;
        for (int j = 0; j < Util.speedArray.size(); j++) {
            speedBuilder.append(Util.speedArray.get(j));
            speedBuilder.append("┼");
            if (Util.speedArray.get(j) > 0) {
                moved = true;
            }
        }

        if (moved) {
            try {
                Util.db.execute("INSERT INTO lap (m_p_s " + /*, location*/ ") VALUES ('" + speedBuilder.toString() + ")");/*','" + locationBuilder.toString() + "')");*/
            } catch (Exception e) {
                Log.d("catch", "can't insert into db");
            }
        } else {
            Log.d("debug", "skip on insert");
        }

        //reset stuff
//        Util.locationArray = new ArrayList<>();
        Util.speedArray = new ArrayList<>();
        this.location = null;
    }
}
