package com.example.karkhanaapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class SetFarmLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

        Spinner districtSpinner = findViewById(R.id.districtSpinner);
        Spinner villageSpinner = findViewById(R.id.villageSpinner);

        String[] districts = {"Nashik", "Pune", "Nagpur", "Aurangabad"};
        String[] villages = {"Taluka 1", "Taluka 2", "Taluka 3"};

//        ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_spinner_item,
//                districts
//        ) {
//            @Override
//            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
//                android.view.View view = super.getView(position, convertView, parent);
//                ((android.widget.TextView) view).setTextColor(android.graphics.Color.BLACK);
//                return view;
//            }
//
//            @Override
//            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
//                android.view.View view = super.getDropDownView(position, convertView, parent);
//                ((android.widget.TextView) view).setTextColor(android.graphics.Color.BLACK);
//                view.setBackgroundColor(android.graphics.Color.WHITE);
//                return view;
//            }
//        };
//        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        districtSpinner.setAdapter(districtAdapter);
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                districts
        );
        districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        districtSpinner.setAdapter(districtAdapter);
        ArrayAdapter<String> villageAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, villages);
        villageAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        villageSpinner.setAdapter(villageAdapter);
    }
}