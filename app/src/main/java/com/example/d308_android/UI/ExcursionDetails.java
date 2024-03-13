package com.example.d308_android.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ParseException;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.d308_android.database.DatabaseTask;


import com.example.d308_android.database.VacationDatabaseBuilder;

import com.example.d308_android.R;
import com.example.d308_android.dao.ExcursionDAO;
import com.example.d308_android.database.Repository;
import com.example.d308_android.entities.Excursion;
import com.example.d308_android.entities.Vacation;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;


public class ExcursionDetails extends AppCompatActivity {


    String name;
    String vacationName;
    Double price;
    int excursionID;
    int vacationID;
    EditText editName;
    EditText editPrice;
    EditText editNote;
    EditText editDate;
    private int lastExcursionID = -1;
    Repository repository;

    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();

    private static final int EXCURSION_PENDING_INTENT_ID = 0;

    private ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        repository=new Repository(getApplication());

        name = getIntent().getStringExtra("excursionName");
        editName = findViewById(R.id.excursionName);
        editName.setText(name);
        price = getIntent().getDoubleExtra("excursionPrice", -1.0);
        editPrice = findViewById(R.id.excursionPrice);
        editPrice.setText(Double.toString(price));

        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        editNote=findViewById(R.id.note);
        editDate=findViewById(R.id.date);

        new Thread(() -> {
            int lastExcursionID = getLastExcursionIDFromDatabase();
            runOnUiThread(() -> {
            });
        }).start();



        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        String excursionStartDate = getIntent().getStringExtra("startDate");


        startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel(editDate, myCalendarStart);
            }
        };

        String startDateStr = getIntent().getStringExtra("startDate");

        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                Date date = sdf.parse(startDateStr);
                Log.d(getApplicationContext().getPackageName(), "Parsed start date: " + date);
                myCalendarStart.setTime(date);
                updateLabel(editDate, myCalendarStart);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Date date;
                String info=editDate.getText().toString();
                if(info.equals(""))info="03/05/24";
                try{
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(ExcursionDetails.this, startDate, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(EditText editText, Calendar calendar) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }


        if (item.getItemId() == R.id.excursionsave) {

            if (vacationID <= 0) {
                Toast.makeText(ExcursionDetails.this, "Please save associated vacation before attempting to save excursion.", Toast.LENGTH_LONG).show();
                return true;
            }

            Vacation associatedVacation = repository.getVacationById(vacationID);
            String vacationStartDate = associatedVacation.getStartDate();
            String vacationEndDate = associatedVacation.getEndDate();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            String excursionDateStr = editDate.getText().toString();
            try {
                Date excursionDate = sdf.parse(excursionDateStr);
                Date startDate = sdf.parse(vacationStartDate);
                Date endDate = sdf.parse(vacationEndDate);

                if (excursionDate.before(startDate) || excursionDate.after(endDate)) {
                    Toast.makeText(this, "Excursion date must be within the vacation timeframe.", Toast.LENGTH_LONG).show();
                    return true;
                }

                Excursion excursion;
                if (excursionID == -1) {
                    if (repository.getAllExcursions().isEmpty()) {
                        excursionID = 1;
                    } else {
                        excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                    }
                    excursion = new Excursion(excursionID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationID, editDate.getText().toString());
                    repository.insert(excursion);
                     Toast.makeText(this, "Excursion saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    excursion = new Excursion(excursionID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), vacationID, editDate.getText().toString());
                    repository.update(excursion);
                     Toast.makeText(this, "Excursion updated successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;

            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
                return false;
            }
        }


        if (itemId == R.id.excursiondelete) {
            Excursion currentExcursion = null;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getExcursionID() == excursionID) {
                    currentExcursion = excursion;
                    break;
                }
            }
            if (currentExcursion != null) {
                repository.delete(currentExcursion);
                Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionName() + " was deleted.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ExcursionDetails.this, "Excursion not found.", Toast.LENGTH_LONG).show();
            }
            finish();
            return true;
        }

        if (itemId == R.id.share) {
        Intent sentIntent = new Intent();
        sentIntent.setAction(Intent.ACTION_SEND);
        sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
        sentIntent.putExtra(Intent.EXTRA_TITLE, "Message Title");
        sentIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sentIntent, null);
        startActivity(shareIntent);
        return true;
        }


        if (item.getItemId() == R.id.notify) {
            String dateFromScreen = editDate.getText().toString();
            String excursionName = getIntent().getStringExtra("excursionName");

            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
            }

            Long trigger = myDate.getTime();
            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            intent.putExtra("key", "Your excursion '" + excursionName + "' is starting on " + dateFromScreen);

            int pendingIntentId = generateRandomNumber();

            PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, pendingIntentId, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private int getLastExcursionIDFromDatabase() {
        int[] result = new int[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(getApplicationContext());
                ExcursionDAO excursionDao = db.excursionDAO();
                result[0] = excursionDao.getLastExcursionID();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(1000);
    }
}