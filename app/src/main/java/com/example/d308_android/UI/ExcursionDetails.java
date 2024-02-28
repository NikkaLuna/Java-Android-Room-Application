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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.d308_android.R;
import com.example.d308_android.database.Repository;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ExcursionDetails extends AppCompatActivity {


    String name;
    Double price;
    int excursionID;
    int vacationID;
    EditText editName;
    EditText editPrice;
    EditText editNote;
    TextView editDate;
    Repository repository;

    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();



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

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Date date;
                String info=editDate.getText().toString();
                if(info.equals(""))info="02/20/24";
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
        startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabelStart();
            }
        };
    }
    private void updateLabelStart() {
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
            intent.putExtra("key", "message I want to see");

            int pendingIntentId = generateRandomNumber();

            PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, pendingIntentId, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(1000);
    }
}