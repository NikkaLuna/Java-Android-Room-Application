package com.example.d308_android.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.d308_android.R;
import com.example.d308_android.database.Repository;
import com.example.d308_android.entities.Excursion;
import com.example.d308_android.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);
        FloatingActionButton fab=findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        repository = new Repository(getApplication());
        List<Vacation> allVacations = repository.getAllVacations();
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sample) {
            repository=new Repository(getApplication());
            //Toast.makeText(VacationList.this, "put in sample data",Toast.LENGTH_LONG).show();
            Vacation vacation=new Vacation(0, "Thailand", 1500, "Hilton Hotel", "02/01/24", "02/20/24");
            repository.insert(vacation);
            vacation=new Vacation(0, "Japan", 1500, "Miaki Hotel", "02/02/24", "02/19/24");
            repository.insert(vacation);
            Excursion excursion=new Excursion(0, "Garden Tour", 300, 1);
            repository.insert(excursion);
            excursion=new Excursion(0, "Cheese Tour", 400, 1);
            repository.insert(excursion);
            return true;
        }
        if(item.getItemId()==android.R.id.home) {
            this.finish();
            return true;
        }
        return true;
    }


}