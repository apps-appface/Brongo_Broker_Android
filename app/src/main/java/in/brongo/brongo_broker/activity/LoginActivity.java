package in.brongo.brongo_broker.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.account.user.UserLoginTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import in.brongo.brongo_broker.BuildConfig;
import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.SignInModel;
import in.brongo.brongo_broker.model.SignUpModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements NoInternetTryConnectListener {
    public EditText login_phone_edit, otp_edit1, otp_edit2, otp_edit3, otp_edit4;
    private Button login_btn, otp_verify_btn;
    private int taskcompleted = 0;
    private int SMS_PERMISSION_REQUEST = 1000;
    private int SMS_BROADCAST_REQUEST = 2000;
    private int PHONE_PERMISSION_REQUEST = 100;
    private UserLoginTask.TaskListener listener;
    public static final int REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS = 111;
    public static final int REQUEST_DEVICE_ID_PERMISSIONS = 112;
    private static final int REQUEST_CAMERA = 200;
    private List<String> listPermissionsNeeded;
    private int currentTime;
    private ImageView otp_back;
    private BroadcastReceiver broadcastReceiver;
    private Toolbar otp_toolbar;
    private LinearLayout goto_register, linear_otp, linear_resend_otp, parentLayout;
    private TextView resend_otp, register_text, otp_mobile_text, title_text, otp_timer, otp_invalid, docu_skip;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RelativeLayout relative_login;
    private Intent intent1;
    private String otpValue, phone;
    private CountDownTimer countDownTimer;
    private IntentFilter intentFilter;
    private Context context = this;
    private CheckBox remember_check;
    private boolean isOtpVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final Bundle bundle = intent.getExtras();
                try {
                    if (bundle != null) {
                        final Object[] pdusObj = (Object[]) bundle.get("pdus");
                        for (int i = 0; i < pdusObj.length; i++) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();
                            try {
                                if (message.contains("Dear User Welcome to Brongo.")) {
                                    String abcd = message.replaceAll("[^0-9]", "");
                                    fill_Otp(abcd);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        intent1 = new Intent();
        intent1.setAction("android.provider.Telephony.SMS_RECEIVED");

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_REQUEST);
            } else {
                initalise();
                setListener();
                setTextWatcher();

                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.BROADCAST_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendBroadcast(intent1);
                } else {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.BROADCAST_SMS}, SMS_BROADCAST_REQUEST);
                }
            }
        } else {
            initalise();
            setListener();
            setTextWatcher();
            sendBroadcast(intent1);
        }
    }

    private void initalise() {
        try {
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            editor = pref.edit();
            parentLayout = findViewById(R.id.login_parent_linear);
            editor.putBoolean(AppConstants.ISWALKTHROUGH, false).commit();
            remember_check = findViewById(R.id.loginCheck);
            login_phone_edit = findViewById(R.id.loginid);
            otp_toolbar = findViewById(R.id.login_toolbar);
            title_text = findViewById(R.id.login_toolbar).findViewById(R.id.other_toolbar_title);
            docu_skip = findViewById(R.id.login_toolbar).findViewById(R.id.other_toolbar_skip);
            docu_skip.setVisibility(View.GONE);
            otp_back = findViewById(R.id.login_toolbar).findViewById(R.id.other_toolbar_back);
            otp_edit1 = findViewById(R.id.otp_edit1);
            otp_mobile_text = findViewById(R.id.otp_mobile);
            otp_edit2 = findViewById(R.id.otp_edit2);
            register_text = findViewById(R.id.register_text);
            linear_resend_otp = findViewById(R.id.linear_resend_otp);
            otp_timer = findViewById(R.id.login_toolbar).findViewById(R.id.other_toolbar_otp_timer);
            otp_invalid = findViewById(R.id.invalid_otp);
            otp_edit3 = findViewById(R.id.otp_edit3);
            otp_edit4 = findViewById(R.id.otp_edit4);
            login_btn = findViewById(R.id.login_btn);
            otp_verify_btn = findViewById(R.id.otp_verify_btn);
            goto_register = findViewById(R.id.contact_new_text);
            resend_otp = findViewById(R.id.resened_otp);
            relative_login = findViewById(R.id.login_relative_layout);
            linear_otp = findViewById(R.id.otp_linear_layout);
            Utils.storeDeviceInfo(context, editor);
            String code = "+91  ";
            login_phone_edit.setSelection(login_phone_edit.getText().length());
            if (pref.getBoolean(AppConstants.LOGIN_REMEMBER, false)) {
                remember_check.setChecked(true);
                login_phone_edit.setText(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            } else {
                remember_check.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        try {
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phone = login_phone_edit.getText().toString();
                    if (phone.length() == 0) {
                        Utils.setSnackBar(parentLayout, "Mobile can not be empty");
                    } else if ((phone.startsWith("6") || phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && (phone.length() == 10)) {
                        callLogin(phone);
                    } else {
                        Utils.setSnackBar(parentLayout, "Invalid Mobile Number");
                    }
                }
            });
            goto_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                    Intent i = new Intent(LoginActivity.this, Menu_Activity.class);
                    i.putExtra("frgToLoad", "ContactFragment");
                    startActivity(i);
                }
            });
            resend_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callLogin(phone);
                    linear_resend_otp.setVisibility(View.GONE);
                }
            });
            otp_verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String otp1 = otp_edit1.getText().toString();
                    String otp2 = otp_edit2.getText().toString();
                    String otp3 = otp_edit3.getText().toString();
                    String otp4 = otp_edit4.getText().toString();
                    if (otp1.length() == 0 || otp2.length() == 0 || otp3.length() == 0 || otp4.length() == 0) {
                        Utils.setSnackBar(parentLayout, "Otp can not be empty");
                    } else {
                        otpValue = otp1 + otp2 + otp3 + otp4;
                        verifyOtp(otpValue);

                    }
                }
            });
            otp_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    otpBack();
                }
            });
            remember_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        editor.putBoolean(AppConstants.LOGIN_REMEMBER, true).commit();
                    } else {
                        editor.putBoolean(AppConstants.LOGIN_REMEMBER, false).commit();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLogin(final String phone) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                String deviceId = Utils.getDeviceId(context);
                String versionName = BuildConfig.VERSION_NAME;
                Call<SignInModel> call = null;
                editor.putString(AppConstants.DEVICE_ID, deviceId);
                editor.putString(AppConstants.MOBILE_NUMBER, phone);
                editor.commit();
                SignUpModel.LoginModel loginModel = new SignUpModel.LoginModel();
                loginModel.setUserId(phone);
                loginModel.setDeviceId(deviceId);
                loginModel.setPlatform("android");
                loginModel.setVersion(versionName);
                loginModel.setOsVersion(pref.getString(AppConstants.OS_VERSION, ""));
                loginModel.setAppVersion(pref.getString(AppConstants.APP_VERSION, ""));
                loginModel.setModelName(pref.getString(AppConstants.MODEL_NAME, ""));
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                call = retrofitAPIs.signInApi(loginModel);
                call.enqueue(new Callback<SignInModel>() {
                    @Override
                    public void onResponse(Call<SignInModel> call, Response<SignInModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                SignInModel signInModel = response.body();
                                int statusCode = signInModel.getStatusCode();
                                String message = signInModel.getMessage();
                                String otp = signInModel.getData().get(0).getOtp();
                                if (statusCode == 200 && message.equalsIgnoreCase("OTP Sent To Your Mobile Number Please Check")) {
                                    Log.w("otp",otp);
                                    relative_login.setVisibility(View.GONE);
                                    linear_otp.setVisibility(View.VISIBLE);
                                    otp_toolbar.setVisibility(View.VISIBLE);
                                    title_text.setText("Verification");
                                    otp_mobile_text.setText(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                                    isOtpVisible = true;
                                    showTimer();
                                }
                            } else {
                                try {
                                    String responseString = response.errorBody().string();
                                    String onBoardMessage="";
                                    boolean isEligible = true;
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    if(dataArray.length() > 0) {
                                        JSONObject jsonObject1 = (JSONObject) dataArray.get(0);
                                        if (jsonObject1 != null) {
                                            onBoardMessage = jsonObject1.optString("message");
                                            isEligible = jsonObject1.optBoolean("isEligible");
                                        }
                                    }
                                    if (message.equalsIgnoreCase("Broker Not Found")) {
                                        //phone_invalid.setVisibility(View.VISIBLE);
                                        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                                    } else if (statusCode == 412 && message.equalsIgnoreCase("Version Not Supported")) {
                                        AllUtils.updateDialog(context);
                                    }else if (statusCode == 412 && message.equalsIgnoreCase("Documents Need To Be Upload Before Login Into Your Account")) {
                                        Intent intent = new Intent(LoginActivity.this, DocumentUploadActivity.class);
                                        intent.putExtra("onBoardMessage",onBoardMessage);
                                        intent.putExtra("isPaymentRequired",!isEligible);
                                        startActivity(intent);
                                    }else if (statusCode == 412 && message.equalsIgnoreCase("Code Of Conduct Need To Be Signed, To Verify Your Account.")) {
                                        Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                        intent.putExtra("onBoardMessage",onBoardMessage);
                                        intent.putExtra("isPaymentRequired",!isEligible);
                                        startActivity(intent);
                                    } else if (statusCode == 412 ) {
                                        Utils.setSnackBar(parentLayout, message);
                                    } else {
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInModel> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        Utils.LoaderUtils.dismissLoader();
                    }
                });
            } else {
                taskcompleted = 100;
                if(!isFinishing()) {
                    Utils.internetDialog(context, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyOtp(String otpValue) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                Call<ResponseBody> call = null;
                SignUpModel.OtpVerificationModel otpVerificationModel = new SignUpModel.OtpVerificationModel();
                otpVerificationModel.setDeviceId(deviceId);
                otpVerificationModel.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                otpVerificationModel.setPlatform("android");
                otpVerificationModel.setOtp(Integer.parseInt(otpValue));
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                call = retrofitAPIs.verify_otpApi(otpVerificationModel);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {
                                try {
                                    responseString = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (statusCode == 200 && message.equalsIgnoreCase("OTP Verified Successfully")) {
                                        Utils.setSnackBar(parentLayout, message);
                                        editor.putBoolean(AppConstants.LOGIN_STATUS, true);
                                        editor.commit();
                                        //new RefreshTokenCall(context,0);
                                        startActivity(new Intent(context, MainActivity.class));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    if (message.equalsIgnoreCase("Invalid OTP")) {
                                        otp_invalid.setVisibility(View.VISIBLE);
                                    } else {
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        Utils.LoaderUtils.dismissLoader();
                    }
                });
            } else {
                if(!isFinishing()) {
                    taskcompleted = 200;
                    Utils.internetDialog(context, this);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setTextWatcher() {
        try {
            login_phone_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 10) {
                        Utils.hideKeyboard(getApplicationContext(), login_phone_edit);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            otp_edit1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    otp_invalid.setVisibility(View.GONE);
                    if (otp_edit1.getText().toString().length() == 1) {
                        otp_edit2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            otp_edit2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    otp_invalid.setVisibility(View.GONE);
                    if (otp_edit2.getText().toString().length() == 1) {
                        otp_edit3.requestFocus();
                    } else if (otp_edit2.getText().toString().length() == 0) {
                        otp_edit1.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            otp_edit3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    otp_invalid.setVisibility(View.GONE);
                    if (otp_edit3.getText().toString().length() == 1) {
                        otp_edit4.requestFocus();
                    } else if (otp_edit3.getText().toString().length() == 0) {
                        otp_edit2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            otp_edit4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    otp_invalid.setVisibility(View.GONE);
                    if (otp_edit4.getText().toString().length() == 0) {
                        otp_edit3.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initalise();
                    setListener();
                    setTextWatcher();
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.BROADCAST_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.BROADCAST_SMS}, SMS_BROADCAST_REQUEST);
                        } else {
                            sendBroadcast(intent1);
                        }
                    } else {
                        sendBroadcast(intent1);
                    }
                } else {
                    initalise();
                    setListener();
                    setTextWatcher();
                }
                break;
        }
    }

    private void fill_Otp(String messageText) {
        try {
            String otp_char1 = Character.toString(messageText.charAt(0));
            String otp_char2 = Character.toString(messageText.charAt(1));
            String otp_char3 = Character.toString(messageText.charAt(2));
            String otp_char4 = Character.toString(messageText.charAt(3));
            otp_edit1.setText(otp_char1);
            otp_edit2.setText(otp_char2);
            otp_edit3.setText(otp_char3);
            otp_edit4.setText(otp_char4);
            otp_verify_btn.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Utils.LoaderUtils.dismissLoader();
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver, intentFilter);
        super.onResume();
    }

    private void showTimer() {
        try {
            otp_timer.setVisibility(View.VISIBLE);
            currentTime = 1;
            countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTime++;
                    if (currentTime > 50) {
                        otp_timer.setText("00:0" + (60 - currentTime));
                    } else {
                        otp_timer.setText("00:" + (60 - currentTime));
                    }
                }

                @Override
                public void onFinish() {
                    currentTime++;
                    if (currentTime <= 60) {
                        if (currentTime > 50) {
                            otp_timer.setText("00:0" + (60 - currentTime));
                        } else {
                            otp_timer.setText("00:" + (60 - currentTime));
                        }
                    }
                    otp_timer.setVisibility(View.GONE);
                    countDownTimer.cancel();
                    linear_resend_otp.setVisibility(View.VISIBLE);
                }
            };
            countDownTimer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isOtpVisible) {
            otpBack();
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onTryReconnect() {
        switch (taskcompleted) {
            case 100:
                callLogin(phone);
                break;
            case 200:
                verifyOtp(otpValue);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

    private void otpBack() {
        try {
            otp_edit1.setText("");
            otp_edit2.setText("");
            otp_edit3.setText("");
            otp_edit4.setText("");
            relative_login.setVisibility(View.VISIBLE);
            linear_otp.setVisibility(View.GONE);
            otp_toolbar.setVisibility(View.GONE);
            isOtpVisible = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

