package appface.brongo.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import appface.brongo.R;
import appface.brongo.util.AppConstants;
import appface.brongo.util.Utils;

public class ReminderActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private EditText reminder_edit_note;
    private TextView add_time_text,time_text,meet_client_location,meet_prop_location;
    private Button btn_15,btn_30,btn_60,reminder_save_btn;
    private Context context;
    private SharedPreferences pref;
    private String notes = "";
    private String before_remin_time = "";
    private String pm_am = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        initialise();
        setListener();
    }
    private void initialise(){
        context = this;
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        calendarView = (CalendarView)findViewById(R.id.reminder_calender);
        Calendar c = Calendar.getInstance();
        calendarView.setMinDate(c.getTimeInMillis());
        reminder_edit_note = (EditText)findViewById(R.id.reminder_edit_notes);
        add_time_text = (TextView)findViewById(R.id.reminder_add_time);
        time_text = (TextView)findViewById(R.id.time_text);
        meet_client_location = (TextView)findViewById(R.id.reminder_client_location);
        meet_prop_location = (TextView)findViewById(R.id.reminder_property_location);
        btn_15 = (Button)findViewById(R.id.reminder_15_btn);
        btn_30 = (Button)findViewById(R.id.reminder_30_btn);
        btn_60 = (Button)findViewById(R.id.reminder_60_btn);
        reminder_save_btn = (Button)findViewById(R.id.reminder_save_btn);
    }
    private void setListener(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Utils.showToast(context,year+"/"+month+"/"+dayOfMonth);
            }
        });
        add_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });
        btn_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                before_remin_time = "15 min";
            }
        });
        btn_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                before_remin_time = "30 min";
            }
        });
        btn_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                before_remin_time = "60 min";
            }
        });
        reminder_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    private void openTimeDialog(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour == 0) {

                    selectedHour += 12;

                    pm_am = "AM";
                }
                else if (selectedHour == 12) {

                    pm_am = "PM";

                }
                else if (selectedHour > 12) {

                    selectedHour -= 12;

                    pm_am = "PM";

                }
                else {

                    pm_am = "AM";
                }
                time_text.setText( selectedHour + ":" + selectedMinute+" "+pm_am);
                time_text.setVisibility(View.VISIBLE);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}
