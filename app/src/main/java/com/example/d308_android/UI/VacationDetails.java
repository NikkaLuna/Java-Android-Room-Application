package com.example.d308_android.UI;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.View;

import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.example.d308_android.R;
import com.example.d308_android.dao.ExcursionDAO;
import com.example.d308_android.dao.VacationDAO;
import com.example.d308_android.database.Repository;
import com.example.d308_android.database.VacationDatabaseBuilder;
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

    private Date startDate = new Date();
    private Date endDate = new Date();
    private Calendar myCalendarStart = Calendar.getInstance();
    private Calendar myCalendarEnd = Calendar.getInstance();

    private static final int VACATION_START_PENDING_INTENT_ID = 1;
    private static final int VACATION_END_PENDING_INTENT_ID = 2;

    private VacationDAO vacationDAO;
    private ExcursionDAO excursionDAO;
    private VacationDatabaseBuilder vacationDatabase;
    public VacationDetails(Repository repository) {
        this.repository = repository;
    }

    public VacationDetails() {
    }

    private Vacation getVacationDetails(int vacationId) {
        return vacationDAO.getVacationById(vacationId);
    }

    private void displayVacationDetails(int vacationId) {
        Vacation vacation = getVacationDetails(vacationId);
        if (vacation != null) {
            Log.d("VacationDetails", "Vacation Name: " + vacation.getVacationName());
            Log.d("VacationDetails", "Start Date: " + vacation.getStartDate());
            Log.d("VacationDetails", "End Date: " + vacation.getEndDate());
        } else {
            Log.d("VacationDetails", "Vacation not found.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

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
            startDate = myCalendarStart.getTime();
            updateLabel(editStartDate, myCalendarStart);
        };

        DatePickerDialog.OnDateSetListener endDateListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarEnd.set(Calendar.YEAR, year);
            myCalendarEnd.set(Calendar.MONTH, monthOfYear);
            myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endDate = myCalendarEnd.getTime();
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



        private void validateEndDate() {
        Date startDate = myCalendarStart.getTime();
        Date endDate = myCalendarEnd.getTime();

        if (endDate.before(startDate)) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            myCalendarEnd.setTime(myCalendarStart.getTime());
            updateLabel(editEndDate, myCalendarEnd);
        }
    }

    private void validateStartDate() {
        Date startDate = myCalendarStart.getTime();
        Date endDate = myCalendarEnd.getTime();

        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show();
            myCalendarStart.setTime(myCalendarEnd.getTime());
            updateLabel(editStartDate, myCalendarStart);
        }
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
                if (vacation.getVacationID() == vacationID) {
                    currentVacation = vacation;
                    break;
                }
            }
            int numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationID() == vacationID) {
                    ++numExcursions;
                }
            }
            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions.", Toast.LENGTH_LONG).show();
            }

        } else if (itemId == R.id.addSampleExcursions) {
            Toast.makeText(VacationDetails.this, "Put in sample data", Toast.LENGTH_LONG).show();
        }


        if (itemId == R.id.share) {
            shareVacationDetails();
            return true;
        }

        if (item.getItemId() == R.id.notify) {
            String startDateFromScreen = editStartDate.getText().toString();
            String endDateFromScreen = editEndDate.getText().toString();
            String vacationName = getIntent().getStringExtra("vacationName");

            Calendar currentCalendar = Calendar.getInstance();

            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            Date startDate = null;
            Date endDate = null;

            try {
                startDate = sdf.parse(startDateFromScreen);
                endDate = sdf.parse(endDateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            long startTrigger = startCalendar.getTimeInMillis();

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            long endTrigger = endCalendar.getTimeInMillis();

            boolean isStartDate = currentCalendar.get(Calendar.YEAR) == startCalendar.get(Calendar.YEAR) &&
                    currentCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH) &&
                    currentCalendar.get(Calendar.DAY_OF_MONTH) == startCalendar.get(Calendar.DAY_OF_MONTH);

            boolean isEndDate = currentCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) &&
                    currentCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) &&
                    currentCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH);

            Intent startIntent = null;
            Intent endIntent = null;

            if (isStartDate) {
                startIntent = new Intent(VacationDetails.this, MyReceiver.class);
                startIntent.putExtra("key2", "Your vacation '" + vacationName + "' is starting on " + startDateFromScreen);

                PendingIntent startSender = PendingIntent.getBroadcast(VacationDetails.this, VACATION_START_PENDING_INTENT_ID, startIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, startSender);
            }

            if (isEndDate) {
                endIntent = new Intent(VacationDetails.this, MyReceiver.class);
                endIntent.putExtra("key3", "Your vacation '" + vacationName + "' is ending on " + endDateFromScreen);

                PendingIntent endSender = PendingIntent.getBroadcast(VacationDetails.this, VACATION_END_PENDING_INTENT_ID, endIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, endSender);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareVacationDetails() {
        String name = editName.getText().toString();
        String price = editPrice.getText().toString();
        String hotelName = editHotelName.getText().toString();
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();

        if (name.isEmpty() || price.isEmpty() || hotelName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all details before sharing.", Toast.LENGTH_LONG).show();
            return;
        }

        repository.getAllVacationsAsync(new Repository.Callback<List<Vacation>>() {

            @Override
            public void onResult(List<Vacation> vacations) {
                Vacation currentVacation = repository.findVacationById(vacations, vacationID);
                if (currentVacation != null) {
                    repository.getAssociatedExcursionsAsync(currentVacation.getVacationID(), new Repository.Callback<List<Excursion>>() {
                        @Override
                        public void onResult(List<Excursion> excursions) {
                            String shareText = createShareText(name, price, hotelName, startDate, endDate, currentVacation, excursions);
                            shareVacation(shareText);
                        }
                        @Override
                        public void onError(Exception e) {
                            handleShareError("Error retrieving associated excursions", e);
                        }
                    });
                } else {
                    handleShareError("Could not find vacation details", null);
                }
            }
            @Override
            public void onError(Exception e) {
                handleShareError("Error retrieving vacations", e);
            }
        });
    }

    private String createShareText(String name, String price, String hotelName, String startDate, String endDate, Vacation vacation, List<Excursion> excursions) {
        String vacationDetails = "Vacation Name: " + vacation.getVacationName() + "\n" +
                "Start Date: " + vacation.getStartDate() + "\n" +
                "End Date: " + vacation.getEndDate() + "\n";

        String excursionString;
        if (!excursions.isEmpty()) {
            excursionString = "Excursions:\n";
            for (Excursion excursion : excursions) {
                excursionString += "- " + excursion.getExcursionName() + " (" + excursion.getStartDate() + ")\n";
            }
        } else {
            excursionString = "No excursions found for this vacation.\n";
        }

        return "Vacation Details:\n" +
                vacationDetails +
                "Price: $" + price + "\n" +
                "Hotel: " + hotelName + "\n" +
                excursionString;
    }

    private void shareVacation(String shareText) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void handleShareError(String message, Exception e) {
        if (e != null) {
            Log.e(TAG, message, e);
        } else {
            Log.e(TAG, message);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

