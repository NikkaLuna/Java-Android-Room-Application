package com.example.d308_android.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.d308_android.R;
import com.example.d308_android.database.Repository;

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

    //problem area - are all these keys correctly matched?
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

        return super.onOptionsItemSelected(item);
    }
}