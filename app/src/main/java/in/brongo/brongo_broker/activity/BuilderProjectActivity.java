package in.brongo.brongo_broker.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private SharedPreferences pref;
    private boolean isLoading = false;
  private int REQUEST_READABLE_AND_WRITABLE_PERMISSIONS = 111;
    private Context context;
    private List<String> listPermissionsNeeded;
    private DownloadManager downloadManager;
    private String title,prop_id,user_id,weburl = "";
    private String downloadurl = "";


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
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
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
                            return true;
                        }
                    });

                    popup.show();
                }
            });
            project_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   onBackPressed();
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
            project_webview.getSettings().setDisplayZoomControls(false);
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
            if(url.contains(".pdf") || url.contains(".jpg") || url.contains(".png")){
                /*String googleDocs = "https://docs.google.com/viewer?url=";
                view.loadUrl(googleDocs + url);*/
            downloadurl = url;
            checkpermission();

            }else {
                view.loadUrl(url);
            }
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if(url.contains(".pdf") || url.contains(".jpg") || url.contains(".png")){
               /* String googleDocs = "https://docs.google.com/viewer?url=";
                view.loadUrl(googleDocs + url);*/
                downloadurl = url;
                checkpermission();
            }else {
                view.loadUrl(url);
            }
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
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            if(!isFinishing()) {
                                openTokenDialog(context);
                            }
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
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

    @Override
    public void onBackPressed() {
        if (project_webview.canGoBack()) {
            project_webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void downloadFile(String url){
        Uri downloadUri = Uri.parse(url);
        String fileName=url.substring(url.lastIndexOf("/")+1);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setNotificationVisibility(request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription("Downloading a file");
        long id = downloadManager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE) .setAllowedOverRoaming(false)
                .setTitle(fileName).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName));
    }
    private boolean checkReadableAndWritablePermission() {
        int permissionStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWritableExternal = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        listPermissionsNeeded = new ArrayList<>();
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWritableExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return listPermissionsNeeded.isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestReadableAndWritablePermission() {
        requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_READABLE_AND_WRITABLE_PERMISSIONS);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_READABLE_AND_WRITABLE_PERMISSIONS) {
            if (permissions.length > 1) {
                if (grantResults.length > 0) {
                    boolean ReadablePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WritablePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (ReadablePermission && WritablePermission) {
                        downloadFile(downloadurl);
                    } else {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) && !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                           if(!isFinishing()){
                               Utils.permissionDialog(context);
                           }
                        } else {
                            Utils.setSnackBar(parentLayout, "Permission Denied");
                        }
                    }
                }
            } else {
                if (permissions.length > 0) {
                    if (grantResults.length > 0) {
                        boolean permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        if (permission) {
                            downloadFile(downloadurl);
                        } else {
                            if (!shouldShowRequestPermissionRationale(permissions[0])) {
                                if(!isFinishing()){
                                    Utils.permissionDialog(context);
                                }
                            } else {
                                Utils.setSnackBar(parentLayout, "Permission Denied");
                            }
                        }
                    }

                }
            }
        }
    }
    private void checkpermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkReadableAndWritablePermission()) {
                downloadFile(downloadurl);
            } else {
                requestReadableAndWritablePermission();
            }
        } else {
            downloadFile(downloadurl);
        }
    }
    }
