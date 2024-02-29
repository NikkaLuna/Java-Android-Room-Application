package com.example.d308_android.database;


import android.content.Context;

public class DatabaseManager {
    public static VacationDatabaseBuilder getVacationDatabase(Context context) {
        return VacationDatabaseBuilder.getDatabase(context);
    }
}
