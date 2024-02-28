package com.example.d308_android.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d308_android.R;
import com.example.d308_android.database.Repository;
import com.example.d308_android.entities.Excursion;
import com.example.d308_android.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VacationDetails extends AppCompatActivity {
    String name;
    double price;
    String hotelName;
    int vacationID;
    int excursionID;
    EditText editName;
    EditText editPrice;
    EditText editHotelName;
    EditText editStartDate;
    EditText editEndDate;
    Repository repository;
    Vacation currentVacation;
    int numExcursions;
    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    private Calendar myCalendarStart = Calendar.getInstance();
    private Calendar myCalendarEnd = Calendar.getInstance();
    long startTimeInMillis = myCalendarStart.getTimeInMillis();
    long endTimeInMillis = myCalendarEnd.getTimeInMillis();
    String vacationTitle = "My Vacation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        //this populates the Vacation Details screen when vacation is clicked on from Vacation List
        editName = findViewById(R.id.titletext);
        editPrice = findViewById(R.id.price);
        editHotelName = findViewById(R.id.hotelname);
        editStartDate = findViewById(R.id.startdate);
        editEndDate = findViewById(R.id.enddate);


        vacationID = getIntent().getIntExtra("vacationID", -1);
        name = getIntent().getStringExtra("vacationName");
        price = getIntent().getDoubleExtra("vacationPrice", 0.0);
        hotelName = getIntent().getStringExtra("hotelName");


        editName.setText(name);
        editPrice.setText(Double.toString(price));
        editHotelName.setText(hotelName);
        excursionID = getIntent().getIntExtra("excursionID", -1);

        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getVacationID() == vacationID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);


        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacationID", vacationID);
                startActivity(intent);
            }
        });


        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        String startDateStr = getIntent().getStringExtra("startDate");


        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                Date date = sdf.parse(startDateStr);
                Log.d(getApplicationContext().getPackageName(), "Parsed start date: " + date);
                myCalendarStart.setTime(date);
                updateLabel(editStartDate, myCalendarStart);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        String endDateStr = getIntent().getStringExtra("endDate");
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                Date date = sdf.parse(endDateStr);
                myCalendarEnd.setTime(date);
                updateLabel(editEndDate, myCalendarEnd);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }


        DatePickerDialog.OnDateSetListener startDateListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, monthOfYear);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(editStartDate, myCalendarStart);
        };


        DatePickerDialog.OnDateSetListener endDateListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            validateDates();
            updateLabel(editEndDate, myCalendarEnd);
        };



        editStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(VacationDetails.this, startDateListener,
                    myCalendarStart.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                    myCalendarStart.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


        editEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(VacationDetails.this, endDateListener,
                    myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                    myCalendarEnd.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        }

    private void validateDates() {
        Date startDate = myCalendarStart.getTime();
        Date endDate = myCalendarEnd.getTime();
        if (endDate.before(startDate)) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            myCalendarEnd.setTime(myCalendarStart.getTime());
            updateLabel(editEndDate, myCalendarEnd);
        }
    }


    private void updateLabel(EditText editText, Calendar calendar) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (itemId == R.id.vacationsave) {
            Vacation vacation;
            if (vacationID == -1) {
                if (repository.getAllVacations().size() == 0) vacationID = 1;
                else
                    vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;

                vacation = new Vacation(vacationID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), editHotelName.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.insert(vacation);
                this.finish();
            } else {
                vacation = new Vacation(vacationID, editName.getText().toString(), Double.parseDouble(editPrice.getText().toString()), editHotelName.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.update(vacation);
                this.finish();
            }
            this.finish();
            return true;

        }

        if (itemId == R.id.vacationdelete) {
            for (Vacation vacation : repository.getAllVacations()) {
                if (vacation.getVacationID() == vacationID) currentVacation = vacation;
            }
            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
                VacationDetails.this.finish();
            }
            if(numExcursions==0){
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName()  + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a product with parts", Toast.LENGTH_LONG).show();
            }
            this.finish();
            return true;
        }


        else if (itemId == R.id.addSampleExcursions) {
            Toast.makeText(VacationDetails.this, "Put in sample data", Toast.LENGTH_LONG).show();
            return true;

        } else if (itemId == R.id.share) {

            String name = editName.getText().toString();
            String price = editPrice.getText().toString();
            String hotelName = editHotelName.getText().toString();
            String startDate = editStartDate.getText().toString();
            String endDate = editEndDate.getText().toString();

            if(name.isEmpty() || price.isEmpty() || hotelName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all details before sharing.", Toast.LENGTH_LONG).show();
                return true;
            }

            String shareText = "Vacation Details:\n" +
                    "Name: " + name + "\n" +
                    "Price: $" + price + "\n" +
                    "Hotel: " + hotelName + "\n" +
                    "Start Date: " + startDate + "\n" +
                    "End Date: " + endDate;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            String startDateFromScreen = editStartDate.getText().toString();
            String endDateFromScreen = editEndDate.getText().toString();

            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = sdf.parse(startDateFromScreen);
                endDate = sdf.parse(endDateFromScreen);
            } catch (androidx.core.net.ParseException | java.text.ParseException e) {
                e.printStackTrace();
            }

            if (startDate != null && endDate != null) {
                Calendar startTimeCalendar = Calendar.getInstance();
                startTimeCalendar.setTime(startDate);
                startTimeCalendar.set(Calendar.HOUR_OF_DAY, 0); // Start time at midnight
                startTimeCalendar.set(Calendar.MINUTE, 0);
                startTimeCalendar.set(Calendar.SECOND, 0);

                Calendar endTimeCalendar = Calendar.getInstance();
                endTimeCalendar.setTime(endDate);
                endTimeCalendar.set(Calendar.HOUR_OF_DAY, 23); // End time at 11:59 PM
                endTimeCalendar.set(Calendar.MINUTE, 59);
                endTimeCalendar.set(Calendar.SECOND, 59);

                long startTimeInMillis = startTimeCalendar.getTimeInMillis();
                long endTimeInMillis = endTimeCalendar.getTimeInMillis();

                String vacationTitle = "My Vacation";

                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                intent.setAction("VACATION_ALERT");
                intent.putExtra("start_time", startTimeInMillis);
                intent.putExtra("end_time", endTimeInMillis);
                intent.putExtra("vacation_title", vacationTitle);
                intent.putExtra("key", "message I want to see");

                int pendingIntentId = generateRandomNumber();

                PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTimeInMillis, sender);
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(1000);
    }
    @Override
    protected void onResume() {

        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getVacationID() == vacationID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }


}
