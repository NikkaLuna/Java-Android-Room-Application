package com.example.d308_android.database;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.d308_android.entities.Excursion;

public class DatabaseTask extends AsyncTask<Void, Void, Boolean> {

    Context context;
    int vacationID;
    Repository repository;
    String excursionDateStr;
    Excursion excursion;

    public DatabaseTask(Context context, int vacationID, Repository repository, String excursionDateStr, Excursion excursion) {
        this.context = context;
        this.vacationID = vacationID;
        this.repository = repository;
        this.excursionDateStr = excursionDateStr;
        this.excursion = excursion;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (vacationID == -1) {
                repository.insert(excursion);
            } else {
                repository.update(excursion);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
