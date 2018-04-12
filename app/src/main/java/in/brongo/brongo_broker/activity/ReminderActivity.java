package in.brongo.brongo_broker.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.ClientDetailsModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderActivity extends AppCompatActivity implements OnMapReadyCallback,NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private CalendarView calendarView;
    private EditText reminder_edit_note,reminder_edit_area;
    private SupportMapFragment mapfragment;
    private LinearLayout map_layout,parentLayout;
    private GoogleMap map;
    private Calendar c;
    private Marker marker;
    private TextView add_time_text,time_text,addLocation,reminder_edit;
    private ImageView reminder_back;
    private Button reminder_save_btn;
    private Context context;
    private DatePicker datePicker;
    private SharedPreferences pref;
    private double lat_value,long_value;
    private ArrayList<Double> arrayList;
   private String selected_date,selected_time,notes,area_name,prop_id,client_mobile;
    private String pm_am = "";
    private String pre_date,pre_time,pre_note,pre_meetingLocation;
    private Double preLat,preLong;
    private Date strDate = new Date();
    private Date meetingDate = new Date();
    private boolean shouldRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getExtras() != null){
            Bundle data = getIntent().getExtras();
            prop_id = data.getString("prop_id","");
            client_mobile = data.getString("client_mobile","");
            pre_date = data.getString("meeting_date","");
            pre_time = data.getString("meeting_time","");
            pre_note = data.getString("meeting_notes","");
            preLat = data.getDouble("meeting_lat",0.0d);
            preLong = data.getDouble("meeting_long",0.0d);
            pre_meetingLocation = data.getString("meeting_location","");
        }
        arrayList = new ArrayList<>();
        onNewIntent(getIntent());
        setContentView(R.layout.activity_reminder);
        initialise();
        setListener();
    }
    private void initialise(){
        try {
            context = this;
            selected_date = selected_time = notes=area_name="";
            parentLayout = (LinearLayout)findViewById(R.id.reminder_parent_linear);
            pref = getSharedPreferences(AppConstants.PREF_NAME,0);
            calendarView = (CalendarView)findViewById(R.id.reminder_calender);
            reminder_edit = (TextView)findViewById(R.id.reminder_edit_btn);
            reminder_back = (ImageView)findViewById(R.id.reminder_back);
            c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getDefault());
            long systemDate = System.currentTimeMillis();
            String timezone = c.getTimeZone().getDisplayName();
            //   calendarView.setMinDate(c.getTimeInMillis());
            reminder_edit_note = (EditText)findViewById(R.id.reminder_edit_notes);
            reminder_edit_area = (EditText)findViewById(R.id.reminder_edit_area);
            add_time_text = (TextView)findViewById(R.id.reminder_add_time);
            time_text = (TextView)findViewById(R.id.time_text);
            addLocation = (TextView)findViewById(R.id.add_lcation);
            datePicker = (DatePicker) findViewById(R.id.datepicker);
            reminder_save_btn = (Button)findViewById(R.id.reminder_save_btn);
            map_layout = (LinearLayout)findViewById(R.id.reminder_map_linear);
            mapfragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reminder_map_fragment));
            mapfragment.getMapAsync(this);
            setDatePicker();
            if(pre_date != null && !pre_date.isEmpty()){
                if(isFutureDate(pre_date,pre_time)){
                    meetingDate = strDate;
                    selected_date = pre_date;
                   setPreviousData(pre_time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setListener(){
        try {
            add_time_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTimeDialog();
                }
            });
            reminder_save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitMeetingDetails();
                }
            });
            addLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context,MapActivity.class));
                }
            });
            reminder_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            reminder_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLocation.setEnabled(true);
                    datePicker.setEnabled(true);
                    add_time_text.setEnabled(true);
                    reminder_edit_note.setEnabled(true);
                    reminder_edit_area.setEnabled(true);
                    reminder_save_btn.setVisibility(View.VISIBLE);
                    reminder_edit.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openTimeDialog(){
        try {
            Calendar mcurrentTime = Calendar.getInstance();
            mcurrentTime.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            int second = mcurrentTime.get(Calendar.SECOND);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                   setTime(selectedHour,selectedMinute);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle data = intent.getExtras();
        try {
            if (data != null) {
               lat_value = data.getDouble("lat_value",0.0d);
               long_value = data.getDouble("long_value",0.0d);
               if(lat_value != 0.0d && long_value != 0.0d){
                   arrayList.clear();
                   arrayList.add(lat_value);
                   arrayList.add(long_value);
                   LatLng current_location = new LatLng(lat_value, long_value);
                   map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14));
                   if(marker != null){
                       marker.remove();
                   }
                   marker = map.addMarker(new MarkerOptions().position(current_location).draggable(true));
                   map_layout.setVisibility(View.VISIBLE);
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        try {
            Intent intent = new Intent(context,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("shouldRefresh",shouldRefresh);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            if(isFutureDate(pre_date,pre_time)) {
                if (preLat != 0.0d && preLong != 0.0d) {
                    arrayList.clear();
                    arrayList.add(preLat);
                    arrayList.add(preLong);
                    LatLng current_location = new LatLng(preLat, preLong);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14));
                    map.addMarker(new MarkerOptions()
                            .position(current_location)).showInfoWindow();
                    marker = map.addMarker(new MarkerOptions().position(current_location));
                    map_layout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void submitMeetingDetails() {
        try {
            notes = reminder_edit_note.getText().toString();
            area_name = reminder_edit_area.getText().toString();
            if(isFutureDate(selected_date,selected_time)) {
                if (selected_date.length() == 0 || selected_time.length() == 0 || arrayList.size() < 2 || prop_id.length() == 0 || client_mobile.length() == 0) {
                    Utils.setSnackBar(parentLayout, "Data shouldn't be empty");
                } else {
                    if (Utils.isNetworkAvailable(context)) {
                        Utils.LoaderUtils.showLoader(context);
                        RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                        ClientDetailsModel.MeetingModel meetingModel = new ClientDetailsModel.MeetingModel();
                        String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                        String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                        String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                        meetingModel.setBrokerMobileNo(mobileNo);
                        meetingModel.setClientMobileNo(client_mobile);
                        meetingModel.setDateOfVisit(selected_date);
                        meetingModel.setPropertyId(prop_id);
                        meetingModel.setNote(notes);
                        meetingModel.setMeetAt(area_name);
                        meetingModel.setTimeOfVisit(selected_time);
                        meetingModel.setLatLong(arrayList);
                        Call<ApiModel.ResponseModel> call = retrofitAPIs.meetingApi(tokenaccess, "android", deviceId, meetingModel);
                        call.enqueue(new Callback<ApiModel.ResponseModel>() {
                            @Override
                            public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                                Utils.LoaderUtils.dismissLoader();
                                if (response != null) {
                                    if (response.isSuccessful()) {
                                        ApiModel.ResponseModel responseModel = response.body();
                                        int statusCode = responseModel.getStatusCode();
                                        String message = responseModel.getMessage();
                                        if (statusCode == 200) {
                                            if(!isFinishing()) {
                                                successDialog(context, message, "Success");
                                            }
                                        }
                                    } else {
                                        shouldRefresh = false;
                                        String responseString = null;
                                        try {
                                            responseString = response.errorBody().string();
                                            JSONObject jsonObject = new JSONObject(responseString);
                                            String message = jsonObject.optString("message");
                                            int statusCode = jsonObject.optInt("statusCode");
                                            if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                                if(!isFinishing()) {
                                                    openTokenDialog(context);
                                                }
                                            } else {
                                                Utils.setSnackBar(parentLayout,message);
                                            }
                                        } catch (IOException | JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                                shouldRefresh = false;
                                Utils.LoaderUtils.dismissLoader();
                                if (t.getMessage().equals("Too many follow-up requests: 21")) {
                                   if(!isFinishing()){
                                       openTokenDialog(context);
                                   }
                                }else {
                                    Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                                }
                            }
                        });
                    } else {
                        if(!isFinishing()) {
                            Utils.internetDialog(context, this);
                        }
                    }
                }
            }else{
                Utils.setSnackBar(parentLayout,"Select future time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        submitMeetingDetails();
    }
    private void setData(){
        try {
            int month = c.get(Calendar.MONTH);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int year = c.get(Calendar.YEAR);
            month = month+1;
            String month1 = month+"";
            String day = dayOfMonth+"";
            if(month < 10){
                month1 = "0"+month;
            }
            if(dayOfMonth < 10){
                day = "0"+dayOfMonth;
            }
            selected_date = year+"-"+month1+"-"+day;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isFutureDate(String pre_date,String pre_time){
        boolean isFutureDate = true;
        String meeting_date = pre_date+" "+pre_time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        try {
            strDate = sdf.parse(meeting_date);
            long systemDate = System.currentTimeMillis();
            if (System.currentTimeMillis() > strDate.getTime()) {
               isFutureDate = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isFutureDate;
    }
    private void setPreviousData(String pre_time){
        try {
            String[] timeArray = pre_time.split(":");
            if(timeArray.length == 3) {
                int meetingHour = Integer.parseInt(timeArray[0]);
                int meetingMinute = Integer.parseInt(timeArray[1]);
                setTime(meetingHour,meetingMinute);
            }
            calendarView.setDate(meetingDate.getTime(),true,true);
            reminder_edit_note.setText(pre_note);
            reminder_edit_area.setText(pre_meetingLocation);
            setDate();
            addLocation.setEnabled(false);
            datePicker.setEnabled(false);
            add_time_text.setEnabled(false);
            reminder_edit_note.setEnabled(false);
            reminder_edit_area.setEnabled(false);
            reminder_save_btn.setVisibility(View.GONE);
            reminder_edit.setVisibility(View.VISIBLE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    private void setTime(int selectedHour, int selectedMinute){
        try {
            String minute="";
            if(selectedMinute < 10){
                minute = "0"+selectedMinute;
            }else{
                minute = ""+selectedMinute;
            }
            if(selectedHour < 10){
                selected_time = "0"+selectedHour+":"+minute+":"+"00";
            }else{
                selected_time = selectedHour+":"+minute+":"+"00";
            }
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
            String time =selectedHour + ":" + minute+" "+pm_am;
            time_text.setText(time);
            time_text.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setDate(){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(meetingDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
            int month = calendar.get(Calendar.MONTH);
            int Year = calendar.get(Calendar.YEAR);
            datePicker.updateDate(Year,month,day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setDatePicker(){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            datePicker.setMinDate(System.currentTimeMillis() - 5000);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            month = month+1;
            String month1 = month+"";
            String day = dayOfMonth+"";
            if(month < 10){
                month1 = "0"+month;
            }
            if(dayOfMonth < 10){
                day = "0"+dayOfMonth;
            }
            selected_date = year+"-"+month1+"-"+day;
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    month = month+1;
                    String month1 = month+"";
                    String day = dayOfMonth+"";
                    if(month < 10){
                        month1 = "0"+month;
                    }
                    if(dayOfMonth < 10){
                        day = "0"+dayOfMonth;
                    }
                    selected_date = year+"-"+month1+"-"+day;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openTokenDialog(Context context){
        try {
            Utils.tokenDialog(context,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryRegenerate() {
     getToken(context);
    }
    private void getToken(Context context){
        new AllUtils().getToken(context,this);
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            submitMeetingDetails();
        }else{
            Utils.LoaderUtils.dismissLoader();
            if(!isFinishing()) {
                openTokenDialog(context);
            }
        }
    }
    private void successDialog(final Context context, final String message, String title){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.alertdialog);
            dialog.setCanceledOnTouchOutside(false);
            final TextView title_text = (TextView) dialog.findViewById(R.id.alert_title);
            final TextView message_text = (TextView) dialog.findViewById(R.id.alert_message);
            title_text.setText(title);
            message_text.setText(message);
            final Button ok_btn = (Button) dialog.findViewById(R.id.alert_ok_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    shouldRefresh = true;
                    Utils.setSnackBar(parentLayout,message);
                    onBackPressed();
                }
            });
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
