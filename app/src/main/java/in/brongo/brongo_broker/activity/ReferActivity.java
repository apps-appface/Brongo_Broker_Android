package in.brongo.brongo_broker.activity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.ReferAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferActivity extends AppCompatActivity implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private TextView refer_text,referral_plan,referral_code_text,toolbar_title;
    private EditText referee_name,referee_mobile;
    private Button refer_broker_btn,refer_share_btn;
    private LinearLayout parentLayout;
    private ImageView refer_back;
    private Toolbar toolbar;
    private int apicode=0;
    private Context context;
    public static final int REQUEST_CONTACT = 112;
    private String referee_name_text,referee_mobile_text,shorturl,refer_amount="";
    private ReferAdapter referAdapter;
    private ArrayList<String> arrayList1,arrayList2;
    private SharedPreferences pref;
    private String referralMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        referralMessage = "I'm inviting you to join Brongo. Get verified Lead @ free of cost & many other features.To know more Watch "+ "https://www.youtube.com/watch?v=NXWlgspDR_E" +" and To download the App. ";
        try {
            initialise();
            refer_broker_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isFinishing()) {
                        refer_dialog();
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initialise(){
        try {
            context = this;
            parentLayout = (LinearLayout)findViewById(R.id.refer_parent_linear);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        //createLink();
    }
    private void foo() {
        try {
            SpannableString link = makeLinkSpan("Terms & Conditions", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,TermsConditionActivity.class);
                    intent.putExtra("fromActivity","refer");
                    startActivity(intent);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onTryReconnect() {
        callReferralPlanApi();
    }

    @Override
    public void onTryRegenerate() {
       getToken(context);
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
        try {
            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                    .setCanonicalIdentifier(pref.getString(AppConstants.REFERRAL_ID,""))
                    .setTitle("Brongo Broker")
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
                        Utils.setSnackBar(parentLayout,"Try Again");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void referral_plan_dialog(){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refer_dialog(){
        try {
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
            RelativeLayout referee_phonebook_btn = (RelativeLayout)dialog.findViewById(R.id.refer_phonebook);
            RelativeLayout referee_whatsapp_btn = (RelativeLayout)dialog.findViewById(R.id.refer_whatsapp);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            if(Utils.isNetworkAvailable(context)) {
               // Utils.LoaderUtils.showLoader(context);
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
                                        openTokenDialog(context);
                                    } else {
                                        Utils.setSnackBar(parentLayout,message);
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
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                    /*if(pd.isShowing()) {
                        pd.dismiss();
                    }*/
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
    private void onSubmitClicked(){
        try {
            String msg = referralMessage.concat(shorturl);
            String phoneNo = referee_mobile.getText().toString();
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.setData(Uri.parse("sms:" + phoneNo));
            smsIntent.putExtra("sms_body", msg);
            if(phoneNo.length()>= 10) {
                startActivity(smsIntent);
            }else{
                Utils.setSnackBar(parentLayout,"Select valid Mobile Number");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onWhatsappClicked(){
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        String message = referralMessage.concat(shorturl);
        try {
            if (isWhatsappInstalled) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else {
                Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                Utils.setSnackBar(parentLayout, "WhatsApp not Installed");
                startActivity(goToMarket);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        Utils.setSnackBar(parentLayout, "Permission Denied");
                    }
                }
                break;

        }
    }
    private void openContact(){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(it, 1);*/
    }
    private void openTokenDialog(Context context){
        try {
            if(!isFinishing()) {
                Utils.tokenDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getToken(Context context){
        new AllUtils().getToken(context,this);
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            callReferralPlanApi();
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
