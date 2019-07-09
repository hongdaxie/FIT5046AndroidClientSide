package com.example.assignment3;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Report.class},version = 4,exportSchema = false)
public abstract class ReportDatabase extends RoomDatabase {
    public abstract ReportDao reportDao();

    private static volatile ReportDatabase INSTANCE;
    static ReportDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (ReportDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ReportDatabase.class, "report_database")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }


}
