package com.example.laba6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TriggeredReminder extends AppCompatActivity {

    private int reminderId;
    private TextView reminderDateTime, reminderTitle, reminderDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triggered_reminder);

        reminderDateTime = findViewById(R.id.reminderTriggeredDateTime);
        reminderTitle = findViewById(R.id.reminderTriggeredTitle);
        reminderDescription = findViewById(R.id.reminderTriggeredDescription);

        reminderId = getIntent().getIntExtra("ID", 0);
        reminderDateTime.setText(getIntent().getStringExtra("DATE"));
        reminderTitle.setText(getIntent().getStringExtra("TITLE"));
        reminderDescription.setText(getIntent().getStringExtra("DESC"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent);
    }
}