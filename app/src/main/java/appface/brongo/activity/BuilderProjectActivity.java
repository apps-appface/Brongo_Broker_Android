package appface.brongo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.model.BuilderModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuilderProjectActivity extends Activity implements NoInternetTryConnectListener {
    private ImageView project_back,three_dot_btn;
    private WebView project_webview;
    Bundle bundle;
    private TextView project_title;
    /*private String Url="";*/
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
   /*     Url = "https://www.tutorialspoint.com/android/android_webview_layout.htm";*/
        initialise();

    }
    private void initialise(){
        context = BuilderProjectActivity.this;
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        project_title = (TextView)findViewById(R.id.project_title);
        project_back = (ImageView)findViewById(R.id.toolbar_project_back);
        three_dot_btn = (ImageView)findViewById(R.id.project_three_dot);
        project_webview = (WebView)findViewById(R.id.project_webview);
        project_webview.setWebViewClient(new MyBrowser());
        project_title.setText(title);
        setListener();
        retrieveUrl();
        if(!isLoading){
            Utils.LoaderUtils.showLoader(context);
            isLoading = true;
        }
    }
    private void setListener(){
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
    }
    private void setWebUrl(){
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
    }

    private void retrieveUrl(){
        if(weburl != null && !weburl.isEmpty()){
            if(!weburl.contains("http")) {
                String baseImageurl = pref.getString(AppConstants.IMAGE_BASE_URL,"");
                baseImageurl =  baseImageurl.concat(weburl);
                weburl = baseImageurl;
            }
        }
        setWebUrl();
    }

    @Override
    public void onTryReconnect() {
        emailLink();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        public void onLoadResource (WebView view, String url) {
                // in standard case YourActivity.this
               /* if(!isLoading){
                    Utils.LoaderUtils.showLoader(context);
                    isLoading = true;
            }*/
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
                                Utils.showToast(context, message);
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    emailLink();
                                }else{
                                    Utils.showToast(context, message);
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
                    Utils.showToast(context, t.getMessage().toString());
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }


}
