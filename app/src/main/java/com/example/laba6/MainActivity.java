package com.example.laba6;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.laba6.adapters.ReminderAdapter;
import com.example.laba6.classes.AlarmReceiver;
import com.example.laba6.classes.DatabaseHelper;
import com.example.laba6.databinding.ActivityMainBinding;
import com.example.laba6.models.Reminder;
import com.google.android.material.textfield.TextInputEditText;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private RecyclerView reminderRecycler;
    private ReminderAdapter reminderAdapter;
    private ImageButton btnSettings, btnAddReminder, btnExitToApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Создание БД
        databaseHelper = new DatabaseHelper(getApplicationContext());
        //db = databaseHelper.getReadableDatabase();

        //Удаление всех данных
        //db.execSQL("DELETE FROM " + DatabaseHelper.TABLE);

        //Вывод списка напоминаний
        setReminderRecycler();

        //Настройки
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Добавление нового напоминания
        btnAddReminder = findViewById(R.id.btnAddReminder);
        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
            }
        });

        //Выход из приложения
        btnExitToApp = findViewById(R.id.btnExitToApp);
        btnExitToApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.close();
                userCursor.close();
                finishAndRemoveTask();
                System.exit(0);
            }
        });
    }
    private void setReminderRecycler() {
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);

        //Список напоминаний
        List<Reminder> reminderList = new ArrayList<>();

        //Добавление напоминаний из бд в список
        while (userCursor.moveToNext()){
            reminderList.add(
                    new Reminder(
                        userCursor.getInt(0),
                        userCursor.getString(1),
                        userCursor.getString(2),
                        userCursor.getString(3)));
        }

        //Закрытие соединения с бд и курсора
        db.close();
        userCursor.close();

        //Указываем настройки для отображения списка напоминаний
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        reminderRecycler = findViewById(R.id.reminderRecycler);
        reminderRecycler.setLayoutManager(layoutManager);

        reminderAdapter = new ReminderAdapter(MainActivity.this, reminderList, databaseHelper);
        reminderRecycler.setAdapter(reminderAdapter);
    }
    private void addReminder(){

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View addReminderWindow = inflater.inflate(R.layout.add_reminder_window, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Добавить напоминание")
                .setView(addReminderWindow);

        TextInputEditText btnDateTime = addReminderWindow.findViewById(R.id.dataTime);
        TextInputEditText title = addReminderWindow.findViewById(R.id.title);
        TextInputEditText description = addReminderWindow.findViewById(R.id.description);


        Calendar newCalender = Calendar.getInstance();
        Calendar newDate = Calendar.getInstance();
        btnDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        Calendar newTime = Calendar.getInstance();
                        TimePickerDialog time = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                newDate.set(year,month,dayOfMonth,hourOfDay,minute,0);
                                Calendar tem = Calendar.getInstance();
                                if(newDate.getTimeInMillis() - tem.getTimeInMillis()>0) {
                                    String dayView = String.valueOf(dayOfMonth);
                                    String monthView = newDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                                    String yearView = String.valueOf(year);
                                    String hourView = String.valueOf(hourOfDay);
                                    String minuteView = String.valueOf(minute).length() == 1
                                            ? "0" + minute : String.valueOf(minute);

                                    btnDateTime.setText(
                                            dayView + " "
                                            + monthView + " "
                                            + yearView + " "
                                            + hourView + ":"
                                            + minuteView);
                                } else
                                    Toast.makeText(MainActivity.this,"Время не может быть меньше текущего!",Toast.LENGTH_SHORT).show();

                            }
                        }, newTime.get(Calendar.HOUR_OF_DAY),newTime.get(Calendar.MINUTE),true);
                        time.show();
                    }
                }, newCalender.get(Calendar.YEAR),newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();

            }
        });

        dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(btnDateTime.getText().toString().trim().length() > 0 && title.getText().toString().trim().length() > 0
                        && description.getText().toString().trim().length() > 0){

                    //Создаём объект напоминания
                    Reminder reminder = new Reminder(
                            reminderAdapter.getItemCount() + 1,
                            title.getText().toString().trim(),
                            description.getText().toString().trim(),
                            newDate.getTime().toString()
                    );

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(newDate.getTime());
                    calendar.set(Calendar.SECOND,0);

                    //Создаём намерение с передачей данных в класс уведомленимний
                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    intent.putExtra("id", reminder.getId());
                    intent.putExtra("Title", reminder.getTitle());
                    intent.putExtra("Descriptions", reminder.getDescription());
                    intent.putExtra("ReminderDate", btnDateTime.getText().toString().trim());

                    //Создаём ожидаемое намерение
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            MainActivity.this, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //Создаём оповещение
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    //Добавляем напоминание в БД
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseHelper.COLUMN_ID, reminder.getId());
                    cv.put(DatabaseHelper.COLUMN_DATETIME, btnDateTime.getText().toString().trim());
                    cv.put(DatabaseHelper.COLUMN_TITLE, title.getText().toString().trim());
                    cv.put(DatabaseHelper.COLUMN_DESCRIPTION, description.getText().toString().trim());

                    db = databaseHelper.getReadableDatabase();
                    db.insert(DatabaseHelper.TABLE, null, cv);
                    db.close();

                    Toast.makeText(MainActivity.this, "Напоминание добавлено!", Toast.LENGTH_SHORT).show();

                    //Обновляем список
                    setReminderRecycler();
                } else {
                    addReminder();
                    Toast.makeText(MainActivity.this, "Заполните все поля!", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }
}