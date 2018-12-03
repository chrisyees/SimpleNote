package com.cs180.simplenote.simplenoteapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class PickTime extends AppCompatActivity {

    private Button setTimeButton;
    private DatePicker datePicker;
    private Spinner hourBar;
    private Spinner minuteBar;
    final List<String> hourList = new ArrayList<String>();
    final List<String> minuteList = new ArrayList<String>();

    private ArrayAdapter<String> hourAdapter;
    private ArrayAdapter<String> minuteAdapter;

    private String hourSelected;
    private String minuteSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);

        setTimeButton = findViewById(R.id.pick_time_button);
        datePicker = findViewById(R.id.pick_date);
        hourBar = findViewById(R.id.hour_bar);
        minuteBar = findViewById(R.id.minute_bar);
        hourList.clear();
        minuteList.clear();

        for (int j = 0; j < 24; ++j)
        {
            hourList.add(Integer.toString(j));
        }

        for (int j = 0; j < 60; ++j)
        {
            if (j < 10)
            {
                minuteList.add("0"+Integer.toString(j));
            }
            else
            {
                minuteList.add(Integer.toString(j));
            }
        }

        hourAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hourList );
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourBar.setAdapter(hourAdapter);
        hourBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hourSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }
        });

        minuteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, minuteList );
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteBar.setAdapter(minuteAdapter);
        minuteBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minuteSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }
        });

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reminderTime = datePicker.getMonth() + " " + datePicker.getDayOfMonth() + " " + datePicker.getYear()
                        + " " + hourSelected + " " + minuteSelected;

                Intent intent = new Intent();
                intent.putExtra("timePicked", reminderTime);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
