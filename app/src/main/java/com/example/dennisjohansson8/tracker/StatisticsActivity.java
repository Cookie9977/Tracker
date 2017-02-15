package com.example.dennisjohansson8.tracker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {
    int id;
    LapLogic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        id = getIntent().getExtras().getInt("id");
        logic = new LapLogic();
        insertStatistics();
    }

    public void switchToStopwatch(View view) {
        Intent intent = new Intent(this, StopWatchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    //TODO incase of implementation of steps uncomment
    private void insertStatistics() {
        TextView name = (TextView) findViewById(R.id.statistics_name);
        TextView totTime = (TextView) findViewById(R.id.statistics_time);
        TextView totDistance = (TextView) findViewById(R.id.statistics_distance);
        TextView maxSpeed = (TextView) findViewById(R.id.statistics_fastest);
        TextView minSpeed = (TextView) findViewById(R.id.statistics_slowest);
        //TextView nrSteps = (TextView) findViewById(R.id.statistics_nrSteps);
        TextView avgSpeed = (TextView) findViewById(R.id.statistics_avg_speed);
        TextView avgSpeedZero = (TextView) findViewById(R.id.statistics_avg_speed_zero);
        TextView medianSpeed = (TextView) findViewById(R.id.statistics_median_speed);
        TextView medianSpeedZero = (TextView) findViewById(R.id.statistics_median_speed_zero);
        try {
            String sql = "SELECT  DATETIME(time_end, 'localtime'), m_p_s " +/* ,  location, steps*/ "FROM lap WHERE id = " + id;
            Cursor list = Util.db.select(sql);

            if (list.moveToFirst()) {
                String nameString = list.getString(0);
                String[] speedArray = list.getString(1).split("┼");
                //String steps = list.getString(2);
                //String locationArray = list.getString(2);
                name.setText(nameString);
                totTime.setText(logic.timeFormat((speedArray.length)));
                totDistance.setText(Util.decimalformat.format(logic.simpleDistanceFormat(speedArray)));
                maxSpeed.setText(Util.decimalformat.format(logic.findMaxSpeed(speedArray)));
                minSpeed.setText(Util.decimalformat.format(logic.findMinSpeed(speedArray)));
                //nrSteps.setText(steps);
                avgSpeed.setText(Util.decimalformat.format(logic.averageSpeed(speedArray)));
                avgSpeedZero.setText(Util.decimalformat.format(logic.averageSpeedZero(speedArray)));
                medianSpeed.setText(Util.decimalformat.format(logic.medianSpeed(speedArray)));
                medianSpeedZero.setText(Util.decimalformat.format(logic.medianSpeedZero(speedArray)));
            }
        } catch (Exception e) {
            Log.d("catch", "can't connect to database");
        }
    }
}
