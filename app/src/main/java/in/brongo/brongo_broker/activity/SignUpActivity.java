package in.brongo.brongo_broker.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.HorizontalAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.SignUpModel;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.services.RegistrationIntentService;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements NoInternetTryConnectListener,CompoundButton.OnCheckedChangeListener{
    private EditText fname_edit, lname_edit, email_edit, mobile_edit, city_edit, resi_line1_edit, resi_line2_edit, office_line1_edit, office_line2_edit, referredBy, comp_type_edit;
    private ApiModel.MicroMarketModel microMarketModel1, microMarketModel2, microMarketModel3;
    private String microMarket1,microMarket2,microMarket3;
    private Button register_btn, real_estate_resi_btn, real_estate_commer_btn;
    public static TextView addmore_text, sign_title,signUp_tc,docu_skip;
    private CheckBox buy_checkbox,sell_checkbox,rent_checkbox,rentout_checkbox,any_checkbox;
    private BottomSheetDialog dialog;
    private TextInputLayout phone_signup_layout,email_signup_layout;
    ArrayList<SignUpModel.MarketObject> marketlist;
    ArrayList<String> poc_list;
    private boolean isDialogOpen = false;
    private ImageView signup_back_image;
    private Context context;
    ArrayList<String> marketIdList;
    public static ImageView micromarket_reset;
    private MaterialBetterSpinner comp_type_spinner;
    private int data_incomplete=1;
    private String emailPattern,comp_type = "";
    private String mobile,email="";
    private LinearLayout parentLayout;
    public static HorizontalAdapter horizontalAdapter;
    private SharedPreferences pref;
    private ArrayAdapter<String> adapter;
    private RecyclerView micro_recycle;
    private ArrayList<String> comp_type_list = new ArrayList<>(Arrays.asList("Individual", "Partnership Firm/ LLP", "Pvt Ltd", "Public Ltd Company", "Others (need to specify)"));
    private ArrayList<String> real_estate_type_list,deal_list,paidMarketList;
    public static ArrayList<String> micromarketlist;
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
        try {
            microMarket1 = microMarket2 = microMarket3 ="";
            paidMarketList = new ArrayList<>();
            context = SignUpActivity.this;
            parentLayout = (LinearLayout)findViewById(R.id.sign_parent_linear);
            dialog = new BottomSheetDialog (context);
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            poc_list = new ArrayList<>();
            marketlist = new ArrayList<>();
            deal_list = new ArrayList<>();
            fetchMicromarket();
            marketIdList = new ArrayList<>();
            emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            phone_signup_layout = (TextInputLayout) findViewById(R.id.input_layout_sign_phone);
            email_signup_layout = (TextInputLayout) findViewById(R.id.input_layout_email);
            signup_back_image = (ImageView) findViewById(R.id.signup_toolbar).findViewById(R.id.other_toolbar_back);
            sign_title = (TextView) findViewById(R.id.signup_toolbar).findViewById(R.id.other_toolbar_title);
            docu_skip = (TextView)findViewById(R.id.signup_toolbar).findViewById(R.id.other_toolbar_skip);
            docu_skip.setVisibility(View.GONE);
            sign_title.setText("Sign Up");
            microMarketModel1 = new ApiModel.MicroMarketModel();
            microMarketModel2 = new ApiModel.MicroMarketModel();
            microMarketModel3 = new ApiModel.MicroMarketModel();
            comp_type_edit = (EditText) findViewById(R.id.comp_type_other);
            comp_type_spinner = (MaterialBetterSpinner) findViewById(R.id.comp_type_spinner);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                    R.layout.spinner_text, comp_type_list);
            comp_type_spinner.setAdapter(dataAdapter);
            buy_checkbox = findViewById(R.id.sign_check_buy);
            sell_checkbox = findViewById(R.id.sign_check_sell);
            rent_checkbox = findViewById(R.id.sign_check_rent);
            rentout_checkbox = findViewById(R.id.sign_check_rentout);
            any_checkbox = findViewById(R.id.sign_check_any);
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
            editor = pref.edit();
            signUp_tc = (TextView)findViewById(R.id.signup_tc);
            referredBy = (EditText) findViewById(R.id.sign_referredby);
            String refer_code = pref.getString(AppConstants.REFERREDBY, "");
            mobile_edit.setText(pref.getString(AppConstants.MOBILE_NUMBER,""));
            mobile = mobile_edit.getText().toString();
            if (refer_code.length() > 0) {
                referredBy.setText(refer_code);
                referredBy.setEnabled(false);
            }
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            micro_recycle.setLayoutManager(horizontalLayoutManager);
            horizontalAdapter = new HorizontalAdapter(micromarketlist, context);
            micro_recycle.setAdapter(horizontalAdapter);
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
            foo();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setListener() {
        try {
            addmore_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (micromarketlist.size() == 3) {
                        addmore_text.setClickable(false);
                    } else {
                        if(!isFinishing()) {
                            marketDialog();
                        }
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
                    switch (micromarketlist.size()){
                        case 1:
                            microMarket1="";
                            city_edit.setText("");
                            break;
                        case 2:
                            microMarket2="";
                            break;
                        case 3:
                            microMarket3="";
                            break;
                    }
                    micromarketlist.remove(micromarketlist.size()-1);
                    horizontalAdapter.notifyDataSetChanged();
                    addmore_text.setVisibility(View.VISIBLE);
                    addmore_text.setText("+ADD "+(3-micromarketlist.size())+" MORE");
                    if(micromarketlist.size() == 0) {
                        micromarket_reset.setVisibility(View.GONE);
                    }
                }
            });
            buy_checkbox.setOnCheckedChangeListener(this);
            sell_checkbox.setOnCheckedChangeListener(this);
            rent_checkbox.setOnCheckedChangeListener(this);
            rentout_checkbox.setOnCheckedChangeListener(this);
            any_checkbox.setOnCheckedChangeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setValue(){
        try {
            String fname = fname_edit.getText().toString();
            String lname = lname_edit.getText().toString();
            String city = city_edit.getText().toString();
            String referral_id = referredBy.getText().toString();
            String res_line_one = resi_line1_edit.getText().toString();
            String res_line_two = resi_line2_edit.getText().toString();
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
                data_incomplete = 0;
            }

            signUpModel.setMicroMarket1(microMarket1);
            signUpModel.setMicroMarket2(microMarket2);
            signUpModel.setMicroMarket3(microMarket3);
            SignUpModel.AddressModel resi_address = new SignUpModel.AddressModel();
            resi_address.setLine1(res_line_one);
            resi_address.setLine2(res_line_two);
            signUpModel.setResidentialAddress(resi_address);
            SignUpModel.AddressModel office_address = new SignUpModel.AddressModel();
            office_address.setLine1(office_line1_edit.getText().toString());
            office_address.setLine2(office_line2_edit.getText().toString());
            signUpModel.setOfficeAddress(office_address);
            if (data_incomplete == 0) {
                signUp(signUpModel);
            } else {
                Utils.setSnackBar(parentLayout,"Add atleast 1 microMarket");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setWatcher() {
        try {
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
                    if ((phone.startsWith("6") || phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && (phone.length() == 10)){
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void signUp(SignUpModel signUpModel) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                Call<ApiModel.SignUpResponseModel> call = null;
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                call = retrofitAPIs.signUpApi(signUpModel);
                call.enqueue(new Callback<ApiModel.SignUpResponseModel>() {
                    @Override
                    public void onResponse(Call<ApiModel.SignUpResponseModel> call, Response<ApiModel.SignUpResponseModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {
                                try {
                                    ApiModel.SignUpResponseModel signUpResponseModel = response.body();
                                    int statusCode = signUpResponseModel.getStatusCode();
                                    String message = signUpResponseModel.getMessage();
                                    if (statusCode == 200) {
                                        ArrayList<ApiModel.SignupObject> list = signUpResponseModel.getData();
                                        if(list.size()>0) {
                                            boolean isEligible = signUpResponseModel.getData().get(0).isEligible();
                                            pref.edit().putBoolean(AppConstants.ISELIGIBLE, isEligible).commit();
                                        }
                                            Utils.setSnackBar(parentLayout, message);
                                        nextPage();
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
                                    if (message.equalsIgnoreCase("Broker Already Exist with this Mobile Number")) {
                                        phone_signup_layout.setError("Phone Number already Exists");
                                    }else if(statusCode == 412) {
                                        notRegistered(message);
                                    }else {
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiModel.SignUpResponseModel> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                        Utils.LoaderUtils.dismissLoader();
                    }
                });
            }else{
                if(!isFinishing()) {
                    Utils.internetDialog(context, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
    }
    @Override
    protected void onPause() {
        if(isDialogOpen) {
            dialog.dismiss();
        }
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        setValue();
    }

    private void fetchMicromarket(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "signUp");
                Call<SignUpModel.MarketModel> call = retrofitAPIs.fetchMarketApi(mobileNo);
                call.enqueue(new Callback<SignUpModel.MarketModel>() {
                    @Override
                    public void onResponse(Call<SignUpModel.MarketModel> call, Response<SignUpModel.MarketModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                SignUpModel.MarketModel marketModel = new SignUpModel.MarketModel();
                                marketModel = response.body();
                                int statusCode = marketModel.getStatusCode();
                                if (statusCode == 200) {
                                    ArrayList<SignUpModel.MarketObject> arrayList = marketModel.getData();
                                    if(arrayList.size() != 0){
                                        marketlist.clear();
                                        poc_list.clear();
                                        marketlist.addAll(arrayList);
                                        for(int i=0;i<marketlist.size();i++){
                                                poc_list.add(marketlist.get(i).getName());
                                        }
                                    }

                                    }
                                }
                            } else {
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    int statusCode = jsonObject.optInt("statusCode");
                                    String message = jsonObject.optString("message");
                                    Utils.setSnackBar(parentLayout, message);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    @Override
                    public void onFailure(Call<SignUpModel.MarketModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                    }
                });
            }else{
                if(!isFinishing()) {
                    Utils.internetDialog(context, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void marketDialog(){
        try {
            final ArrayList<String> pocnewlist = new ArrayList<>();
            pocnewlist.addAll(poc_list);
            adapter = new ArrayAdapter<String>(this, R.layout.spinner_text,pocnewlist);
            isDialogOpen = true;
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.walkthrough_back);
            dialog.setContentView(R.layout.market_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            ListView listView = (ListView)dialog.findViewById(R.id.market_list_view);
            EditText search_market = (EditText)dialog.findViewById(R.id.inputSearch);
            Button cancel_btn = (Button)dialog.findViewById(R.id.market_dialog_cancel);
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setCancelable(true);
                    dialog.dismiss();
                }
            });
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.hideKeyboard(context, view);
                    String selectedMarket = adapter.getItem(position);
                    int position1 = poc_list.indexOf(selectedMarket);
                    if (position1 >= 0) {
                        if (microMarket1.equalsIgnoreCase("")) {
                            microMarket1 = marketlist.get(position1).getMicroMarketId();
                            micromarketlist.add(marketlist.get(position1).getName());
                            horizontalAdapter.notifyDataSetChanged();
                            addmore_text.setText("+ADD " + (3 - micromarketlist.size()) + " MORE");
                            addmore_text.setVisibility(View.VISIBLE);
                            micromarket_reset.setVisibility(View.VISIBLE);
                            city_edit.setText(marketlist.get(position1).getCity());
                        } else if (microMarket2.equalsIgnoreCase("")) {
                            microMarket2 = marketlist.get(position1).getMicroMarketId();
                            micromarketlist.add(marketlist.get(position1).getName());
                            horizontalAdapter.notifyDataSetChanged();
                            addmore_text.setText("+ADD " + (3 - micromarketlist.size()) + " MORE");
                        } else if (microMarket3.equalsIgnoreCase("")) {
                            microMarket3 = marketlist.get(position1).getMicroMarketId();
                            micromarketlist.add(marketlist.get(position1).getName());
                            horizontalAdapter.notifyDataSetChanged();
                            addmore_text.setText("+ADD " + (3 - micromarketlist.size()) + " MORE");
                        }
                        if (micromarketlist.size() == 3) {
                            addmore_text.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                    }
                }
            });
            search_market.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    adapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    private void foo() {
        try {
            SpannableString link = makeLinkSpan("Terms & Conditions", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,TermsConditionActivity.class);
                    intent.putExtra("fromActivity","signup");
                    startActivity(intent);
                }
            });
            signUp_tc.setText("By signing up, you agree to our \n ");
            signUp_tc.append(link);

            // This line makes the link clickable!
            makeLinksFocusable(signUp_tc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
 * Methods used above.
 */

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        try {
            link.setSpan(new ClickableString(listener), 0, text.length(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.edit_hint_color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return link;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return link;
    }

    private void makeLinksFocusable(TextView tv) {
        try {
            MovementMethod m = tv.getMovementMethod();
            if ((m == null) || !(m instanceof LinkMovementMethod)) {
                if (tv.getLinksClickable()) {
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
            tv.setHighlightColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        try {
            if(isChecked){
                deal_list.add(buttonView.getText().toString());
            }else{
                if(deal_list.contains(buttonView.getText().toString())){
                    deal_list.remove(buttonView.getText().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;
        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    private void nextPage(){
        try {
            editor.remove(AppConstants.REFERREDBY);
            editor.putString(AppConstants.MOBILE_NUMBER, mobile);
            editor.putBoolean(AppConstants.ISWALKTHROUGH, false);
            editor.commit();
            Intent serviceIntent = new Intent(context, RegistrationIntentService.class);
            serviceIntent.putExtra("key", 200);
            startService(serviceIntent);
            startActivity(new Intent(SignUpActivity.this, DocumentUploadActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void notRegistered(String message){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.walkthrough_back);
        dialog.setContentView(R.layout.client_broker_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        TextView dialog_title = dialog.findViewById(R.id.poc_dialog_title);
        ImageView dialog_cancel = dialog.findViewById(R.id.client_broker_dialog_close);
        Button dialog_btn = dialog.findViewById(R.id.poc_dialog_btn);
        dialog_title.setText(message);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, Menu_Activity.class);
                i.putExtra("frgToLoad", "ContactFragment");
                startActivity(i);
                finish();
                dialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });
        dialog.show();
    }
}
