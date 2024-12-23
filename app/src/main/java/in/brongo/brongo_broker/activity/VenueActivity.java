package in.brongo.brongo_broker.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.CustomSpinnerAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.SignUpModel;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VenueActivity extends AppCompatActivity implements OnMapReadyCallback, NoInternetTryConnectListener {
    private GoogleMap map;
    private boolean doubleBackToExitPressedOnce = false;
    private LinearLayout pop_up_address;
    private MaterialBetterSpinner date_spinner, time_spinner;
    private CustomSpinnerAdapter customSpinnerAdapter;
    private Button venue_submit, venue_cancel, map_btn;
    private SupportMapFragment mapFragment;
    private Context context;
    private ScrollView parentLayout;
    private HashMap<String, ArrayList<SignUpModel.SlotModel>> slotMap;
    ArrayList<SignUpModel.SlotModel> slotList;
    ArrayList<String> timelist;
    ArrayList<Integer> countList;
    private int TASK_IMCOMPLETE = 0;
    private SharedPreferences pref;
    private String selected_date, selected_slot;
    private final int FINE_PERMISSION_REQUEST = 1000;
    private ArrayList<String> date_list;
    private ArrayAdapter<String> dateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        initialise();
    }

    private void initialise() {
        try {
            context = VenueActivity.this;
            selected_date = selected_slot = "";
            parentLayout = (ScrollView)findViewById(R.id.venue_parent_scroll);
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            date_spinner = (MaterialBetterSpinner) findViewById(R.id.venue_calendar_spinner);
            time_spinner = (MaterialBetterSpinner) findViewById(R.id.venue_time_spinner);
            slotMap = new HashMap<>();
            slotList = new ArrayList<>();
            date_list = new ArrayList<>();
            timelist = new ArrayList<>();
            countList = new ArrayList<>();
            dateAdapter = new ArrayAdapter<String>(context,
                    R.layout.spinner_text, date_list);
            dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            date_spinner.setAdapter(dateAdapter);
            customSpinnerAdapter = new CustomSpinnerAdapter(VenueActivity.this, R.layout.spinner_rows, timelist, countList);
            customSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            time_spinner.setAdapter(customSpinnerAdapter);
            fetchTimeSlot();
            venue_cancel = (Button) findViewById(R.id.venue_cancel);
            venue_submit = (Button) findViewById(R.id.venue_submit);
            map_btn = (Button) findViewById(R.id.map_btn);
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
            setListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            LatLng brongo = new LatLng(13.024282, 77.63298699999996);
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(VenueActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VenueActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQUEST);
                } else {
                    map.setMyLocationEnabled(true);
                }
            } else {
                map.setMyLocationEnabled(true);
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(brongo, 14));
            map.addMarker(new MarkerOptions()
                    .title("Brongo")
                    .position(brongo)).showInfoWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == FINE_PERMISSION_REQUEST) {
            try {
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    map.setMyLocationEnabled(true);
                } else {
                    // Permission was denied. Display an error message.
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }else{
            Utils.setSnackBar(parentLayout, "Click again to back");
        }

        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    private void setListener(){
        try {
            date_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected_date = parent.getItemAtPosition(position).toString();
                        slotList.clear();
                        timelist.clear();
                        countList.clear();
                        ArrayList<SignUpModel.SlotModel> arrayList = slotMap.get(selected_date);
                        slotList.addAll(arrayList);
                        for(int i =0;i<slotList.size();i++){
                            timelist.add(slotList.get(i).getTime());
                            countList.add(slotList.get(i).getBrokersCount());
                        }
                    customSpinnerAdapter = new CustomSpinnerAdapter(VenueActivity.this,R.layout.spinner_rows,timelist,countList);
                    customSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    time_spinner.setAdapter(customSpinnerAdapter);

                }
            });
            time_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //time_spinner.setText(slotList.get(position).getTime());
                    selected_slot = slotList.get(position).getSlotName();
                }
            });
            venue_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isEligible = pref.getBoolean(AppConstants.ISELIGIBLE,false);
                    if(selected_slot != null && selected_date != null && !selected_slot.isEmpty() && !selected_date.isEmpty()) {
                        if(isEligible){
                            submitSlot();
                        }else {
                            if (!isFinishing()) {
                                onBoardDialog(context);
                            }
                        }
                    }else{
                        Utils.setSnackBar(parentLayout,"Select the slot details first");
                    }
                }
            });
            map_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri gmmIntentUri = Uri.parse("geo:13.024282,77.63298699999996?z=10&q=13.024282,77.63298699999996(brongo)");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }else{
                            Uri webpage = Uri.parse("https://www.google.co.in/maps/place/Brongo/@23.2468671,-37.7533675,3z/data=!4m8!1m2!2m1!1sbrongo!3m4!1s0x3bae1722dd0ff757:0x9e4094f5032168c!8m2!3d13.024282!4d77.632987?hl=en");
                            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }else{
                                Utils.setSnackBar(parentLayout,"No application to open");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            venue_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fetchTimeSlot(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                Call<SignUpModel.VenueModel> call = retrofitAPIs.fetchSlots(mobileNo);
                call.enqueue(new Callback<SignUpModel.VenueModel>() {
                    @Override
                    public void onResponse(Call<SignUpModel.VenueModel> call, Response<SignUpModel.VenueModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                SignUpModel.VenueModel venueModel = new SignUpModel.VenueModel();
                                venueModel = response.body();
                                int statusCode = venueModel.getStatusCode();
                                if (statusCode == 200) {
                                   ArrayList<SignUpModel.VenueObject> arrayList = venueModel.getData();
                                   if(arrayList.size() != 0){
                                      setListValue(arrayList);
                                   }
                                }
                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                Utils.setSnackBar(parentLayout,message);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpModel.VenueModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                    }
                });
            }else{
                TASK_IMCOMPLETE = 100;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        switch (TASK_IMCOMPLETE){
            case 100:
                fetchTimeSlot();
                break;
            case 200:
                submitSlot();
                break;
        }
    }
    private void setListValue(ArrayList<SignUpModel.VenueObject> arraylist){
        try {
            slotMap.clear();
            for(int i = 0;i<arraylist.size();i++){
                slotMap.put(arraylist.get(i).getDate(),arraylist.get(i).getTimeSlots());
            }
            // date_list.addAll(slotMap.keySet());
            for( String key : slotMap.keySet() ){
                date_list.add(key);
            }
            dateAdapter = new ArrayAdapter<String>(context,
                    R.layout.spinner_text, date_list);
            dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            date_spinner.setAdapter(dateAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void submitSlot(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                SignUpModel.BookedSlotModel bookedSlotModel = new SignUpModel.BookedSlotModel();
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                bookedSlotModel.setDate(selected_date);
                bookedSlotModel.setSlotName(selected_slot);
                bookedSlotModel.setMobileNo(mobileNo);
                Call<ApiModel.ResponseModel> call = retrofitAPIs.bookslotApi(bookedSlotModel);
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
                                    pref.edit().remove(AppConstants.ISELIGIBLE).commit();
                                    Utils.setSnackBar(parentLayout,message);
                                    startActivity(new Intent(context,LoginActivity.class));
                                    finish();
                                }
                            }
                        else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                Utils.setSnackBar(parentLayout,message);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                    }
                });
            }else{
                TASK_IMCOMPLETE = 200;
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void onBoardDialog(Context context){
        try {
        String onboardtext1 = "Please note a one time OnBoarding Fee of ";
        String str1 = "Rs.5,000 + 900 (18% GST) = Rs.5,900";
        String comp_name = "‘Turnip Technologies Pvt Ltd’";
        SpannableStringBuilder str = new SpannableStringBuilder(str1);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder str2 = new SpannableStringBuilder(comp_name);
        str2.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, comp_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.onboarding_dialog);
            dialog.setCanceledOnTouchOutside(false);
            TextView onBoardTextview = dialog.findViewById(R.id.onboard_textview);
            onBoardTextview.setText(onboardtext1);
            onBoardTextview.append(str);
            onBoardTextview.append(" will be applicable.\n Request you to carry a Cheque or DD in the name of ");
            onBoardTextview.append(str2);
            onBoardTextview.append(" for the same along with the originals documents.");
           Button accept_btn = dialog.findViewById(R.id.onboard_accept);
            ImageView close_btn = dialog.findViewById(R.id.onboard_close_btn);
            accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitSlot();
                    dialog.dismiss();
                }
            });
            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
