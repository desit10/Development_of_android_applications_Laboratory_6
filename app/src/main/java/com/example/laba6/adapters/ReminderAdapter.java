package com.example.laba6.adapters;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laba6.MainActivity;
import com.example.laba6.R;
import com.example.laba6.ReminderActivity;
import com.example.laba6.classes.AlarmReceiver;
import com.example.laba6.classes.DatabaseHelper;
import com.example.laba6.models.Reminder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    Context context;
    List<Reminder> reminderList;
    SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    AlarmManager alarmManager;

    public ReminderAdapter(Context context, List<Reminder> reminderList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.reminderList = reminderList;
        this.databaseHelper = databaseHelper;
    }

    //Какой дизайн
    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reminderItem = LayoutInflater.from(context).inflate(R.layout.activity_reminder_item, parent, false);

        return new ReminderAdapter.ReminderViewHolder(reminderItem);
    }

    //Взаимодействие с объектом и его элементами
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //Подставляем данные
        holder.reminderDateTime.setText(reminderList.get(position).getDateTime());
        holder.reminderTitle.setText(reminderList.get(position).getTitle());
        holder.reminderDescription.setText(reminderList.get(position).getDescription());

        //Переход на активити уведомления
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Создаём намерение
                Intent intent = new Intent(context, ReminderActivity.class);

                //Передаём данные
                intent.putExtra("reminderId", reminderList.get(position).getId());
                intent.putExtra("reminderDateTime", reminderList.get(position).getDateTime());
                intent.putExtra("reminderTitle", reminderList.get(position).getTitle());
                intent.putExtra("reminderDescription", reminderList.get(position).getDescription());

                //Переходим к активити
                context.startActivity(intent);
            }
        });

        //Удаление напоминания
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Открываем соединение с БД
                db = databaseHelper.getReadableDatabase();

                //Удаляем указанное уведомление
                db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(reminderList.get(position).getId())});

                //Закрываем соединение с БД
                db.close();

                //Создаем менеджер оповещений, который был создан при первоначальном создании напоминания
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                //Создаём намерение, которое было создано при первоначальном создании напоминания
                Intent intent = new Intent(context, AlarmReceiver.class);

                //Создаём ожидаемое намерение, которое было создано при первоначальном создании напоминания
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        reminderList.get(position).getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                //Удаляем оповещение
                alarmManager.cancel(pendingIntent);

                //Удаляем напоминание
                reminderList.remove(position);

                Toast.makeText(context, "Напоминание удалено!", Toast.LENGTH_SHORT).show();

                //Обновляем список
                notifyDataSetChanged();
            }
        });

        //Обновление напоминания
        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View addReminderWindow = inflater.inflate(R.layout.add_reminder_window, null);

                AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                        .setTitle("Обновить напоминание")
                        .setView(addReminderWindow);

                TextInputEditText btnDateTime = addReminderWindow.findViewById(R.id.dataTime);
                TextInputEditText title = addReminderWindow.findViewById(R.id.title);
                TextInputEditText description = addReminderWindow.findViewById(R.id.description);

                btnDateTime.setText(reminderList.get(position).getDateTime());
                title.setText(reminderList.get(position).getTitle());
                description.setText(reminderList.get(position).getDescription());

                Calendar newCalender = Calendar.getInstance();
                Calendar newDate = Calendar.getInstance();
                btnDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                                Calendar newTime = Calendar.getInstance();
                                TimePickerDialog time = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
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
                                            Toast.makeText(context,"Время не может быть меньше текущего!",Toast.LENGTH_SHORT).show();
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

                dialog.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(btnDateTime.getText().toString().trim().length() > 0 && title.getText().toString().trim().length() > 0
                                && description.getText().toString().trim().length() > 0){

                            Reminder reminder = new Reminder(
                                    reminderList.get(position).getId(),
                                    title.getText().toString().trim(),
                                    description.getText().toString().trim(),
                                    newDate.getTime().toString()
                            );

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(newDate.getTime());
                            calendar.set(Calendar.SECOND,0);

                            ///Удаляем старое оповещение///
                            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            Intent OldIntent = new Intent(context, AlarmReceiver.class);

                            PendingIntent OldPendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    reminderList.get(position).getId(),
                                    OldIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager.cancel(OldPendingIntent);
                            /////////////////////////////////////

                            ///Cоздаём новое оповещение///
                            Intent NewIntent = new Intent(context, AlarmReceiver.class);
                            NewIntent.putExtra("id", reminder.getId());
                            NewIntent.putExtra("Title", reminder.getTitle());
                            NewIntent.putExtra("Descriptions", reminder.getDescription());
                            NewIntent.putExtra("ReminderDate", btnDateTime.getText().toString().trim());

                            PendingIntent NewPendingIntent = PendingIntent.getBroadcast(
                                    context, reminder.getId(), NewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), NewPendingIntent);
                            ////////////////////////////////////

                            //Обновляем напоминание в БД
                            ContentValues cv = new ContentValues();
                            cv.put(DatabaseHelper.COLUMN_DATETIME, btnDateTime.getText().toString().trim());
                            cv.put(DatabaseHelper.COLUMN_TITLE, title.getText().toString().trim());
                            cv.put(DatabaseHelper.COLUMN_DESCRIPTION, description.getText().toString().trim());

                            db = databaseHelper.getReadableDatabase();
                            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + reminder.getId(), null);
                            db.close();

                            //Обновляем данные напоминания в приложении
                            reminderList.get(position).setDateTime(btnDateTime.getText().toString().trim());
                            reminderList.get(position).setTitle(title.getText().toString().trim());
                            reminderList.get(position).setDescription(description.getText().toString().trim());

                            Toast.makeText(context, "Напоминание обновлено!", Toast.LENGTH_SHORT).show();

                            //Обновляем список
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    //С какими элементами в дизайне работать
    public static final class ReminderViewHolder extends RecyclerView.ViewHolder{
        ImageButton btnDelete;
        AppCompatButton btnUpdate;
        TextView reminderDateTime, reminderTitle, reminderDescription;
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            reminderDateTime = itemView.findViewById(R.id.reminderDateTime);
            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            reminderDescription = itemView.findViewById(R.id.reminderDescription);

            btnDelete = itemView.findViewById(R.id.btnDeleteReminder);
            btnUpdate = itemView.findViewById(R.id.btnUpdateReminder);
        }
    }
}
