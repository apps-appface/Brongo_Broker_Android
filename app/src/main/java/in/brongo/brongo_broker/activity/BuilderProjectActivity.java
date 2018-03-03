package in.brongo.brongo_broker.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
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

public class BuilderProjectActivity extends Activity implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private ImageView project_back,three_dot_btn;
    private WebView project_webview;
    private Bundle bundle;
    private TextView project_title;
    private LinearLayout parentLayout;
    private String Url="";
    private SharedPreferences pref;
    private boolean isLoading = false;
    private Context context;
    private String title,prop_id,user_id,weburl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prop_id = user_id = title ="";
        if(getIntent().getExtras() != null) {
            bundle =new Bundle();
            bundle = getIntent().getExtras();
           title= getIntent().getExtras().getString("title");
            prop_id= getIntent().getExtras().getString("prop_id");
            user_id= getIntent().getExtras().getString("user_id");
            weburl = getIntent().getExtras().getString("url");
        }
        setContentView(R.layout.activity_builder_project);
        initialise();

    }
    private void initialise(){
        try {
            context = BuilderProjectActivity.this;
            parentLayout = findViewById(R.id.builder_activity_linear);
            pref = getSharedPreferences(AppConstants.PREF_NAME,0);
            project_title = findViewById(R.id.project_title);
            project_back = findViewById(R.id.toolbar_project_back);
            three_dot_btn = findViewById(R.id.project_three_dot);
            project_webview = findViewById(R.id.project_webview);
            project_webview.setWebViewClient(new MyBrowser());
            project_title.setText(title);
            setListener();
            setWebUrl();
            if(!isLoading){
                Utils.LoaderUtils.showLoader(context);
                isLoading = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setListener(){
        try {
            three_dot_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(BuilderProjectActivity.this, three_dot_btn);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.action_logout:
                                    emailLink();
                                    break;
                            }
                            /*Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();*/
                            return true;
                        }
                    });

                    popup.show();
                }
            });
            project_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setWebUrl(){
        try {
            project_webview.getSettings().setLoadsImagesAutomatically(true);
            project_webview.getSettings().setJavaScriptEnabled(true);
            project_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            project_webview.getSettings().setGeolocationEnabled(true);
            project_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            project_webview.getSettings().setBuiltInZoomControls(true);
            project_webview.getSettings().setDomStorageEnabled(true);
            project_webview.getSettings().setLoadWithOverviewMode(true);
            project_webview.getSettings().setUseWideViewPort(true);
            project_webview.loadUrl(weburl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        emailLink();
    }

    @Override
    public void onTryRegenerate() {
     getToken(context);
    }

    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            try {
                if (isLoading){
                    Utils.LoaderUtils.dismissLoader();
                isLoading = false;
            }
            }catch(Exception exception){
                exception.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
    private void emailLink(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                ApiModel.BuilderAcceptModel builderAcceptModel = new ApiModel.BuilderAcceptModel();
                builderAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                builderAcceptModel.setPropertyId(prop_id);
                builderAcceptModel.setUserId(user_id);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                Call<ApiModel.ResponseModel> call = retrofitAPIs.emailBuilderApi(tokenaccess, "android", deviceId, builderAcceptModel);
                call.enqueue(new Callback<ApiModel.ResponseModel>() {
                    @Override
                    public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {
                                ApiModel.ResponseModel responseModel = response.body();
                                int statusCode = responseModel.getStatusCode();
                                String message = responseModel.getMessage();
                                if (statusCode == 200 ) {
                                    Utils.setSnackBar(parentLayout,message);
                                }
                            } else {
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        if(!isFinishing()) {
                                            openTokenDialog(context);
                                        }
                                    }else{
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        Utils.showToast(context, t.getMessage().toString(),"Failure" );
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

    private void openTokenDialog(Context context){
        try {
                Utils.tokenDialog(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getToken(Context context){
        try {
            new AllUtils().getToken(context,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            emailLink();
        }else{
            Utils.LoaderUtils.dismissLoader();
            if(!isFinishing()) {
                openTokenDialog(context);
            }
        }
    }
}
