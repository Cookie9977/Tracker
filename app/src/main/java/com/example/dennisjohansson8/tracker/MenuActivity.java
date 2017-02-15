package com.example.dennisjohansson8.tracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MenuActivity extends AppCompatActivity {
    LapLogic logic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        logic = new LapLogic();
        fill();
    }

    public void fill() {
        LinearLayout menu_table = (LinearLayout) findViewById(R.id.menu_table);
        Cursor list;
        menu_table.removeAllViews();
        try {
            list = Util.db.select("SELECT id,favorite, DATETIME(time_end, 'localtime'),m_p_s FROM lap ORDER BY favorite DESC, time_end DESC");
            if (list.moveToFirst()) {
                do {
                    LinearLayout temp = (LinearLayout) getLayoutInflater().inflate(R.layout.table_entry, menu_table, false);
                    final int id = list.getInt(0);
                    final int favorite = list.getInt(1);
                    String name = list.getString(2);
                    String[] speedArray = list.getString(3).split("┼");

                    String timeString = logic.timeFormat(speedArray.length);
                    double distance = logic.simpleDistanceFormat(speedArray);
                    int picId = (favorite == 0) ? R.drawable.non_favorite : R.drawable.favorite;
                    try {
                        TextView entryName = (TextView) temp.getChildAt(0);
                        TextView entryTime = (TextView) ((LinearLayout) ((LinearLayout) temp.getChildAt(1)).getChildAt(0)).getChildAt(1);
                        TextView entryAvgSpeed = (TextView) ((LinearLayout) ((LinearLayout) ((LinearLayout) temp.getChildAt(1)).getChildAt(1)).getChildAt(0)).getChildAt(1);
                        final ImageView entryFavorite = (ImageView) ((RelativeLayout) ((LinearLayout) ((LinearLayout) temp.getChildAt(1)).getChildAt(1)).getChildAt(1)).getChildAt(0);
                        TextView entryDistance = (TextView) ((LinearLayout) ((LinearLayout) temp.getChildAt(1)).getChildAt(2)).getChildAt(1);

                        entryName.setText(name);
                        entryTime.setText(timeString);
                        entryAvgSpeed.setText(Util.decimalformat.format(logic.averageSpeed(speedArray)));
                        entryFavorite.setImageResource(picId);
                        entryFavorite.setTag(favorite);
                        entryFavorite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("debug", "id is: " + id);
                                if (favorite == 0 || entryFavorite.getTag().equals(0)) {
                                    //set favorite
                                    Util.db.execute("UPDATE lap SET favorite = 1 WHERE id = " + id);
                                    entryFavorite.setImageResource(R.drawable.favorite);
                                    entryFavorite.setTag(1);
                                } else {
                                    Util.db.execute("UPDATE lap SET favorite = 0 WHERE id = " + id);
                                    entryFavorite.setImageResource(R.drawable.non_favorite);
                                    entryFavorite.setTag(0);
                                }
                            }
                        });
                        entryDistance.setText(Util.decimalformat.format(distance));
                    } catch (Exception ex) {
                        Log.d("catch", "couldn't get family tree");
                    }
                    temp.setId(id);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switchToStatistics(id);
                        }
                    });
                    menu_table.addView(temp);
                } while (list.moveToNext());
            }
        } catch (Exception e) {
            Log.d("catch", "can't connect to database");
        }

    }

    private void switchToStatistics(int id) {
        Intent toStatistics = new Intent(this, StatisticsActivity.class);
        toStatistics.putExtra("id", id);
        startActivity(toStatistics);
    }

    public void switchToStopwatch(View v) {
        finish();
    }

}
