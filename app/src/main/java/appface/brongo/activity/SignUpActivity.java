package appface.brongo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import appface.brongo.R;
import appface.brongo.adapter.HorizontalAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.model.SignUpModel;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.services.RegistrationIntentService;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements NoInternetTryConnectListener {
    private EditText fname_edit, lname_edit, email_edit, mobile_edit, city_edit, resi_line1_edit, resi_line2_edit, office_line1_edit, office_line2_edit, referredBy, comp_type_edit;
    private ApiModel.MicroMarketModel microMarketModel1, microMarketModel2, microMarketModel3;
    private Button register_btn, real_estate_resi_btn, real_estate_commer_btn;
    public static TextView addmore_text, sign_title;
    private TextInputLayout phone_signup_layout,email_signup_layout;
    private ImageView signup_back_image;
    private Context context;
    public static ImageView micromarket_reset;
    private MaterialBetterSpinner comp_type_spinner;
    private int data_incomplete;
    private String emailPattern,comp_type = "";
    private String mobile,email="";
    public static HorizontalAdapter horizontalAdapter;
    private SharedPreferences pref;
    private RecyclerView micro_recycle;
    private ArrayList<String> comp_type_list = new ArrayList<>(Arrays.asList("Individual", "Partnership Firm/ LLP", "Pvt Ltd", "Public Ltd Company", "Others (need to specify)"));
    private ArrayList<String> real_estate_type_list;
    public static ArrayList<ApiModel.MicroMarketModel> micromarketlist;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initialise();
        setWatcher();
        setListener();
    }

    private void initialise() {
        context = SignUpActivity.this;
        mobile = "";
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        phone_signup_layout = (TextInputLayout) findViewById(R.id.input_layout_sign_phone);
        email_signup_layout = (TextInputLayout) findViewById(R.id.input_layout_email);
        signup_back_image = (ImageView) findViewById(R.id.signup_toolbar).findViewById(R.id.other_toolbar_back);
        sign_title = (TextView) findViewById(R.id.signup_toolbar).findViewById(R.id.other_toolbar_title);
        sign_title.setText("Sign Up");
        microMarketModel1 = new ApiModel.MicroMarketModel();
        microMarketModel2 = new ApiModel.MicroMarketModel();
        microMarketModel3 = new ApiModel.MicroMarketModel();
        comp_type_edit = (EditText) findViewById(R.id.comp_type_other);
        comp_type_spinner = (MaterialBetterSpinner) findViewById(R.id.comp_type_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                R.layout.spinner_text, comp_type_list);
        comp_type_spinner.setAdapter(dataAdapter);
        fname_edit = (EditText) findViewById(R.id.sign_fname);
        lname_edit = (EditText) findViewById(R.id.sign_lname);
        real_estate_type_list = new ArrayList<>();
        micromarketlist = new ArrayList<>();
        email_edit = (EditText) findViewById(R.id.sign_email);
        mobile_edit = (EditText) findViewById(R.id.sign_phone);
        micro_recycle = (RecyclerView) findViewById(R.id.hori_micromarket_recycle);
        register_btn = (Button) findViewById(R.id.signup_btn);
        real_estate_commer_btn = (Button) findViewById(R.id.real_estate_commercial);
        real_estate_resi_btn = (Button) findViewById(R.id.real_estate_resi);
        addmore_text = (TextView) findViewById(R.id.add_more_text);
        city_edit = (EditText) findViewById(R.id.sign_city);
        micromarket_reset = (ImageView) findViewById(R.id.micromarketlist_reset);
        resi_line1_edit = (EditText) findViewById(R.id.sign_residential_line1);
        resi_line2_edit = (EditText) findViewById(R.id.sign_residential_line2);
        office_line1_edit = (EditText) findViewById(R.id.sign_office_line1);
        office_line2_edit = (EditText) findViewById(R.id.sign_office_line2);
        pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
        editor = pref.edit();
        referredBy = (EditText) findViewById(R.id.sign_referredby);
        String refer_code = pref.getString(AppConstants.REFERREDBY, "");
        if (refer_code.length() > 0) {
            referredBy.setText(refer_code);
            referredBy.setEnabled(false);
        }
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        micro_recycle.setLayoutManager(horizontalLayoutManager);
        horizontalAdapter = new HorizontalAdapter(micromarketlist, context);
        micro_recycle.setAdapter(horizontalAdapter);
      /*  pd = new ProgressDialog(this, R.style.CustomProgressDialog);
        pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress_loader));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);*/
        comp_type_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != comp_type_list.size() - 1) {
                    comp_type = parent.getItemAtPosition(position).toString();
                    comp_type_edit.setVisibility(View.GONE);
                } else {
                    comp_type_edit.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setListener() {
        addmore_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (micromarketlist.size() == 3) {
                    addmore_text.setClickable(false);
                } else {
                    startActivity(new Intent(SignUpActivity.this, AutoFillActivity.class));
                }
            }
        });
        real_estate_commer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (real_estate_type_list.contains("Commercial")) {
                    real_estate_type_list.remove("Commercial");
                    real_estate_commer_btn.setBackgroundResource(R.drawable.gray_empty_btn);
                    real_estate_commer_btn.setTextColor(getResources().getColor(R.color.real_state_color));
                } else {
                    real_estate_commer_btn.setBackgroundResource(R.drawable.rounded_btn);
                    real_estate_commer_btn.setTextColor(getResources().getColor(R.color.white));
                    real_estate_type_list.add("Commercial");
                }
            }
        });
        real_estate_resi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (real_estate_type_list.contains("Residential")) {
                    real_estate_type_list.remove("Residential");
                    real_estate_resi_btn.setBackgroundResource(R.drawable.gray_empty_btn);
                    real_estate_resi_btn.setTextColor(getResources().getColor(R.color.real_state_color));
                } else {
                    real_estate_resi_btn.setBackgroundResource(R.drawable.rounded_btn);
                    real_estate_type_list.add("Residential");
                    real_estate_resi_btn.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              setValue();
            }
        });
        signup_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
        micromarket_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micromarketlist.remove(micromarketlist.size()-1);
               // micromarketlist.clear();
                horizontalAdapter.notifyDataSetChanged();
                addmore_text.setVisibility(View.VISIBLE);
                addmore_text.setText("+ADD "+(3-micromarketlist.size())+" MORE");
                if(micromarketlist.size() == 0) {
                    micromarket_reset.setVisibility(View.GONE);
                }
            }
        });
    }
    private void setValue(){
        String fname = fname_edit.getText().toString();
        String lname = lname_edit.getText().toString();
        String city = city_edit.getText().toString();
        String referral_id = referredBy.getText().toString();
        if (fname.length() == 0 || lname.length() == 0 || city.length() == 0 || mobile.length() == 0 || comp_type.length() == 0 || real_estate_type_list.size() == 0) {
            Utils.showToast(context, "Data should not be empty");
        } else {
            editor.putString(AppConstants.FIRST_NAME, fname);
            editor.putString(AppConstants.LAST_NAME, lname);
            editor.putString(AppConstants.EMAIL_ID, email);
            editor.putString(AppConstants.MOBILE_NUMBER, mobile);
            editor.commit();
            String deviceid = Utils.getDeviceId(context);
            SignUpModel signUpModel = new SignUpModel();
            signUpModel.setMobileNo(mobile);
            signUpModel.setEmailId(email);
            signUpModel.setFirstName(fname);
            signUpModel.setLastName(lname);
            signUpModel.setDeviceId(deviceid);
            signUpModel.setPlatform("android");
            signUpModel.setCity(city);
            signUpModel.setReferredBy(referral_id);
            // signUpModel.setCity("Bengaluru");
            signUpModel.setRealEstateType(real_estate_type_list);
            signUpModel.setTypeOfCompany(comp_type);
            if (micromarketlist.size() > 0) {
                microMarketModel1.setMicroMarketCity(micromarketlist.get(0).getMicroMarketCity());
                microMarketModel1.setMicroMarketName(micromarketlist.get(0).getMicroMarketName());
                microMarketModel1.setMicroMarketState(micromarketlist.get(0).getMicroMarketState());
                data_incomplete = 0;
            } else {
                Utils.showToast(context, "select atleast one micromarket");
                data_incomplete = 1;
            }
            if (micromarketlist.size() > 1) {
                microMarketModel2.setMicroMarketCity(micromarketlist.get(1).getMicroMarketCity());
                microMarketModel2.setMicroMarketName(micromarketlist.get(1).getMicroMarketName());
                microMarketModel2.setMicroMarketState(micromarketlist.get(1).getMicroMarketState());
            }

            if (micromarketlist.size() > 2) {
                microMarketModel3.setMicroMarketCity(micromarketlist.get(2).getMicroMarketCity());
                microMarketModel3.setMicroMarketName(micromarketlist.get(2).getMicroMarketName());
                microMarketModel3.setMicroMarketState(micromarketlist.get(2).getMicroMarketState());
            }

                   /* microMarketModel1.setMicroMarketCity("Bengaluru ");
                    microMarketModel1.setMicroMarketName("Marathahalli");
                    microMarketModel1.setMicroMarketState("Karnataka");
                    data_incomplete =0;*/
            signUpModel.setMicroMarket1(microMarketModel1);
            signUpModel.setMicroMarket2(microMarketModel2);
            signUpModel.setMicroMarket3(microMarketModel3);
            SignUpModel.AddressModel resi_address = new SignUpModel.AddressModel();
            resi_address.setLine1(resi_line1_edit.getText().toString());
            resi_address.setLine2(resi_line2_edit.getText().toString());
            signUpModel.setResidentialAddress(resi_address);
            SignUpModel.AddressModel office_address = new SignUpModel.AddressModel();
            office_address.setLine1(office_line1_edit.getText().toString());
            office_address.setLine2(office_line2_edit.getText().toString());
            signUpModel.setOfficeAddress(office_address);
            if (data_incomplete == 0) {
                signUp(signUpModel);
            } else {
                Utils.showToast(context, "Add atleast 1 microMarket");
            }
        }
    }

    private void setWatcher() {
        mobile_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    Utils.hideKeyboard(context, mobile_edit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = mobile_edit.getText().toString().trim();
                if ((phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && (phone.length() == 10)){
                    phone_signup_layout.setError("");
                    phone_signup_layout.setErrorEnabled(false);
                    mobile = phone;
                }else if(phone.length() == 0) {
                    mobile = "";
                    phone_signup_layout.setError("");
                    phone_signup_layout.setErrorEnabled(false);
                }else
                {
                    phone_signup_layout.setError("Invalid Mobile number");
                    phone_signup_layout.setErrorEnabled(true);
                }
            }
        });
        comp_type_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comp_type = comp_type_edit.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email1 = email_edit.getText().toString().trim();
                if (email1.matches(emailPattern) && s.length() > 0){
                    email_signup_layout.setError("");
                    email_signup_layout.setErrorEnabled(false);
                    email = email1;
                }else if(s.length()>0){
                    email_signup_layout.setErrorEnabled(true);
                    email_signup_layout.setError("Invalid email id");
                }else if(s.length() == 0){
                    email_signup_layout.setError("");
                    email_signup_layout.setErrorEnabled(false);
                }
            }
        });

    }

    private void signUp(SignUpModel signUpModel) {
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            Call<ResponseBody> call = null;
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            call = retrofitAPIs.signUpApi(signUpModel);
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
                                if (statusCode == 200) {
                                    Utils.showToast(context, message);
                                    editor.remove(AppConstants.REFERREDBY);
                                    editor.putString(AppConstants.MOBILE_NUMBER, mobile);
                                    editor.putBoolean(AppConstants.ISWALKTHROUGH, false);
                                    editor.commit();
                                    Intent serviceIntent = new Intent(context, RegistrationIntentService.class);
                                    serviceIntent.putExtra("key", 200);
                                    startService(serviceIntent);
                                    startActivity(new Intent(SignUpActivity.this, DocumentUploadActivity.class));
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (message.equalsIgnoreCase("Broker Already Exist with this Mobile Number")) {
                                    phone_signup_layout.setError("Phone Number already Exists");
                                }
                                Utils.showToast(context, message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.showToast(context, "Some Problem Occured");
                    Utils.LoaderUtils.dismissLoader();
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        setValue();
    }
}