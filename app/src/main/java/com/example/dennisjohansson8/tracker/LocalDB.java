package com.example.dennisjohansson8.tracker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class LocalDB extends Activity {
    private SQLiteDatabase db;

    LocalDB(SQLiteDatabase db) {
        //TODO incase of implementation of steps and maps, uncomment stuff
        this.db = db;
        //create db
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS lap (id INTEGER PRIMARY KEY AUTOINCREMENT, favorite BOOLEAN DEFAULT FALSE, time_end TIMESTAMP DEFAULT CURRENT_TIMESTAMP, m_p_s TEXT NOT NULL)");/*,location TEXT NOT NULL,steps INTEGER DEFAULT 0)");*/
        } catch (Exception e) {
            Log.d("debug", "db creation failed");
        }

    }

    /**
     * @param sql an sql query
     * @return Cursor with results
     */
    Cursor select(String sql) {
        return db.rawQuery(sql, null);
    }

    /**
     * executes an sql query
     *
     * @param sql an sql query
     */
    void execute(String sql) {
        db.execSQL(sql);
    }

/*    *//**
     * @param sql an sql query
     * @return id of the insert query
     *//*
    int insertId(String sql) {
        int id = -1;
        String column = sql.split(" ")[2];
        db.rawQuery(sql, null);
        sql = "SELECT MAX(id) FROM " + column;
        dbCursor = db.rawQuery(sql, null);
        if (dbCursor.moveToFirst()) {
            id = dbCursor.getInt(0);
        }
        dbCursor.close();
        return id;
    }*/
}
