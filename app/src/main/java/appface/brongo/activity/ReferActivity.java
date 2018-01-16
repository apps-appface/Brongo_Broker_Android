package appface.brongo.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.ReferAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferActivity extends AppCompatActivity implements NoInternetTryConnectListener{
    private TextView refer_text,referral_plan,referral_code_text,toolbar_title;
    private EditText referee_name,referee_mobile;
    private Button refer_broker_btn,refer_share_btn;
    private ImageView refer_back;
    private Toolbar toolbar;
    private Context context;
    public static final int REQUEST_CONTACT = 112;
    private String referee_name_text,referee_mobile_text,shorturl,refer_amount="";
    private ReferAdapter referAdapter;
    private ArrayList<String> arrayList1,arrayList2;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        initialise();
        refer_broker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refer_dialog();
              /*  ReferFragmentTwo referFragmentTwo = new ReferFragmentTwo();
                Utils.replaceFragment(getFragmentManager(),referFragmentTwo,R.id.inventory_frag_container,true);*/
            }
        });
        refer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
    }
    private void initialise(){
        context = this;
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        refer_text = (TextView)findViewById(R.id.refer_text);
        refer_back = (ImageView)findViewById(R.id.refer_back);
        referral_code_text = (TextView)findViewById(R.id.refer_referral_code);
        refer_broker_btn = (Button)findViewById(R.id.refer_broker_btn);
        referral_plan = (TextView)findViewById(R.id.referral_plans);
        arrayList1 = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        referAdapter = new ReferAdapter(context,arrayList1,arrayList2);
        createLink();
        callReferralPlanApi();
        referral_code_text.setText(pref.getString(AppConstants.REFERRAL_ID,""));
        //createLink();
    }
    private void foo() {
        SpannableString link = makeLinkSpan("Terms & Conditions", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(context,"Terms and Conditions");
            }
        });
        SpannableString referral_plan_link = makeLinkSpan("See Referral plans", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referral_plan_dialog();
            }
        });
        referral_plan.setText(referral_plan_link);
        // Set the TextView's text
        refer_text.setText("Refer your friends & earn "+refer_amount+" per \n successful referral.");

        // Append the link we created above using a function defined below.
        refer_text.append(link);

        // Append a period (this will not be a link).
        refer_text.append(" apply.");

        // This line makes the link clickable!
        makeLinksFocusable(refer_text);
        makeLinksFocusable(referral_plan);
    }

/*
 * Methods used above.
 */

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.refer_gray)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return link;
    }

    private void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        tv.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onTryReconnect() {
        callReferralPlanApi();
    }

/*
 * ClickableString class
 */

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
    private void createLink(){
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier(pref.getString(AppConstants.REFERRAL_ID,""))
                .setTitle("Brongo Broker")
                .setContentDescription("My Content Description")
                .setContentImageUrl("https://example.com/mycontent-12345.png")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("property1", "blue")
                .addContentMetadata("property2", "red");
        LinkProperties linkProperties = new LinkProperties()
                .setFeature("sharing");
        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("MyApp", "got my Branch link to share: " + url);
                    shorturl = url;
                }else{
                    Utils.showToast(context,"Try Again");
                }
            }
        });
    }

    private void referral_plan_dialog(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.5f);
        dialog.setContentView(R.layout.dialog_referral_plan);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        RecyclerView refer_recycle = (RecyclerView)dialog.findViewById(R.id.referee_plan_recycle);
        Button refer_plan_btn = (Button)dialog.findViewById(R.id.refer_plan_dialog_btn);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        refer_recycle.setLayoutManager(verticalmanager);
        refer_recycle.setAdapter(referAdapter);
        dialog.show();
        refer_plan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void refer_dialog(){
        final BottomSheetDialog dialog = new BottomSheetDialog (context);
        /*dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
        dialog.setContentView(R.layout.dialog_refer_broker);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);*/
        View view = View.inflate(context, R.layout.dialog_refer_broker, null);
        dialog.setContentView(view);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) view.getParent()));
        bottomSheetBehavior.setPeekHeight(1000);
        dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        referee_name = (EditText)dialog.findViewById(R.id.referee_name);
        referee_mobile = (EditText)dialog.findViewById(R.id.referee_mobile);
        Button referee_submit_btn = (Button)dialog.findViewById(R.id.refer_submit_btn);
        Button referee_phonebook_btn = (Button)dialog.findViewById(R.id.refer_phonebook);
        Button referee_whatsapp_btn = (Button)dialog.findViewById(R.id.refer_whatsapp);
        referee_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClicked();
                dialog.dismiss();
            }
        });
        referee_phonebook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isContactReadAllowed()) {
                        openContact();
                    } else {
                        requestContactPermission();
                    }
                } else {
                   openContact();
                }
            }
        });
        referee_whatsapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWhatsappClicked();
                //dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number =  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name =  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //contactName.setText(name);
                referee_mobile.setText(number);
                referee_name.setText(name);
                //contactEmail.setText(email);
            }
        }
    }
    private void callReferralPlanApi(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            Call<ApiModel.ReferPlanModel> call = retrofitAPIs.getReferralPlanApi(tokenaccess, "android", deviceId, mobileNo);
            call.enqueue(new Callback<ApiModel.ReferPlanModel>() {
                @Override
                public void onResponse(Call<ApiModel.ReferPlanModel> call, Response<ApiModel.ReferPlanModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ApiModel.ReferPlanModel referPlanModel = response.body();
                            int statusCode = referPlanModel.getStatusCode();
                            String message = referPlanModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("")) {
                                ArrayList<String> broker_no_list = referPlanModel.getData().get(0).getReferralCout();
                                ArrayList<String> referral_rate_list = referPlanModel.getData().get(0).getReferralPrice();
                                if (broker_no_list.size() != 0) {
                                    arrayList1.addAll(broker_no_list);
                                }
                                if (referral_rate_list.size() != 0) {
                                    arrayList2.addAll(referral_rate_list);
                                    refer_amount = referral_rate_list.get(0);
                                }
                                foo();
                                referAdapter.notifyDataSetChanged();
                            /*if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    callReferralPlanApi();
                                } else {
                                    Utils.showToast(context, message);
                                }
                           /* if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<ApiModel.ReferPlanModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                /*if(pd.isShowing()) {
                    pd.dismiss();
                }*/
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void onSubmitClicked(){
        String msg = shorturl;
        String phoneNo = referee_mobile.getText().toString();
       /* String referralLink= dynamic_link  + " \n Your Referral code is : "+ pref.getString(AppConstants.REFERRAL_ID,"");
        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2 .putExtra(android.content.Intent.EXTRA_SUBJECT, "Referral link for Brongo");
        intent2.putExtra(Intent.EXTRA_TEXT, referralLink);
        startActivity(Intent.createChooser(intent2, "Share via"));*/
       /* try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
           Utils.showToast(getContext(), "Message Sent");
        } catch (Exception ex) {
            Utils.showToast(getContext(),ex.getMessage().toString());
            ex.printStackTrace();
        }*/
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.setData(Uri.parse("sms:" + phoneNo));
        smsIntent.putExtra("sms_body", msg);
        startActivity(smsIntent);
    }

    private void onWhatsappClicked(){
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shorturl);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }catch (Exception e){

        }
    }
    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestContactPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
    }
    private boolean isContactReadAllowed() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openContact();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        Utils.permissionDialog(context);
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;

        }
    }
    private void openContact(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, 1);
    }

}
