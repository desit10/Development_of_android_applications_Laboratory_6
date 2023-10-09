package com.example.laba6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laba6.adapters.ReminderAdapter;
import com.example.laba6.classes.AlarmReceiver;
import com.example.laba6.models.Reminder;

public class ReminderActivity extends AppCompatActivity {

    private int reminderId;
    private TextView reminderDateTime, reminderTitle, reminderDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        reminderDateTime = findViewById(R.id.reminderActivityDateTime);
        reminderTitle = findViewById(R.id.reminderActivityTitle);
        reminderDescription = findViewById(R.id.reminderActivityDescription);

        reminderId = getIntent().getIntExtra("reminderId", 0);
        reminderDateTime.setText(getIntent().getStringExtra("reminderDateTime"));
        reminderTitle.setText(getIntent().getStringExtra("reminderTitle"));
        reminderDescription.setText(getIntent().getStringExtra("reminderDescription"));
    }
}