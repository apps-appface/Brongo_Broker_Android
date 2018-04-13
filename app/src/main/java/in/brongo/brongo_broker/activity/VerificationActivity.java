package in.brongo.brongo_broker.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.widget.ImageViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.Utils;

public class VerificationActivity extends AppCompatActivity {
    private ImageView verification_back,refresh_btn;
    private WebView verification_webview;
    private Bundle bundle;
    private SwipeRefreshLayout refresh_layout;
    private Button ok_btn;
    private TextView verification_title;
    private LinearLayout parentLayout;
    private SharedPreferences pref;
    private boolean isLoading = false;
    private Context context;
    private String title,currentUrl="";
    //private String weburl = "https://prod.brongo.in/verification/Brongo/#/step1";  // for production
    private String weburl = "http://18.221.178.146:8080/verification/Brongo/#/step1";  //for development
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getBundleExtra("onboardData");
        }
        initialise();
        verification_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerificationActivity.this,LoginActivity.class));
                finish();
            }
        });
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerificationActivity.this,LoginActivity.class));
                finish();
            }
        });
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verification_webview.reload();
                if(!isLoading){
                    Utils.LoaderUtils.showLoader(context);
                    isLoading = true;
                }
            }
        });
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_layout.setRefreshing(false);
//                VerificationActivity.this.verification_webview.loadUrl(currentUrl);
               verification_webview.reload();
                if(!isLoading){
                    Utils.LoaderUtils.showLoader(context);
                    isLoading = true;
                }
            }
        });
    }
    private void initialise(){
        try {
            context = this;
            currentUrl = weburl;
            parentLayout = findViewById(R.id.verification_activity_linear);
            pref = getSharedPreferences(AppConstants.PREF_NAME,0);
            refresh_layout = findViewById(R.id.verification_swipe);
            String broker_mobile = pref.getString(AppConstants.MOBILE_NUMBER,"");
            weburl = weburl+"?bmo="+broker_mobile;
            verification_title = findViewById(R.id.verification_title);
            verification_back = findViewById(R.id.toolbar_verification_back);
            refresh_btn = findViewById(R.id.verification_refresh_btn);
            verification_webview = findViewById(R.id.verification_webview);
            verification_webview.setWebViewClient(new MyBrowser());
            verification_title.setText("Verification");
            ok_btn = findViewById(R.id.verification_ok_btn);
            setWebUrl();
            if(!isLoading){
                Utils.LoaderUtils.showLoader(context);
                isLoading = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            enableOkButton(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            enableOkButton(request.getUrl().toString());
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            try {
//                refresh_btn.setVisibility(View.GONE);
                if (isLoading){
                    Utils.LoaderUtils.dismissLoader();
                    isLoading = false;
                }
                currentUrl = url;
                enableOkButton(url);
            }catch(Exception exception){
                exception.printStackTrace();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (isLoading){
                Utils.LoaderUtils.dismissLoader();
                isLoading = false;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            if (isLoading){
                Utils.LoaderUtils.dismissLoader();
                isLoading = false;
            }
        }
    }
    private void setWebUrl(){
        try {
            verification_webview.getSettings().setLoadsImagesAutomatically(true);
            verification_webview.getSettings().setJavaScriptEnabled(true);
            verification_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            verification_webview.getSettings().setGeolocationEnabled(true);
            verification_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            verification_webview.getSettings().setBuiltInZoomControls(true);
            verification_webview.getSettings().setDisplayZoomControls(false);
            verification_webview.getSettings().setDomStorageEnabled(true);
            verification_webview.getSettings().setLoadWithOverviewMode(true);
            verification_webview.getSettings().setUseWideViewPort(true);
            verification_webview.loadUrl(currentUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void enableOkButton(String url){
        String current_url = url;
       // String finalUrl = "https://prod.brongo.in/verification/Brongo/#/step4"; // for produc
        String finalUrl = "http://18.221.178.146:8080/verification/Brongo/#/step4";  //for dev
        if(current_url.equalsIgnoreCase(finalUrl)){
            if(bundle != null){
                String message = bundle.getString("onBoardMessage","");
                boolean isPaymentNeeded = bundle.getBoolean("isPaymentRequired",false);
                if(isPaymentNeeded){
                    onBoardingDialog(message);
                }else{
                    ok_btn.setVisibility(View.VISIBLE);
                }
            }else {
                ok_btn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
    private void onBoardingDialog(String message){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.walkthrough_back);
        dialog.setContentView(R.layout.client_broker_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        TextView dialog_title = dialog.findViewById(R.id.poc_dialog_title);
        ImageView dialog_cancel = dialog.findViewById(R.id.client_broker_dialog_close);
        Button dialog_btn = dialog.findViewById(R.id.poc_dialog_btn);
        dialog_btn.setText("OK");
        dialog_title.setText(message);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(VerificationActivity.this,LoginActivity.class));
                finish();
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
