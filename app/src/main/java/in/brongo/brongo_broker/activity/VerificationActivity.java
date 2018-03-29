package in.brongo.brongo_broker.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private ImageView verification_back;
    private WebView verification_webview;
    private Bundle bundle;
    private Button ok_btn;
    private TextView verification_title;
    private LinearLayout parentLayout;
    private SharedPreferences pref;
    private boolean isLoading = false;
    private Context context;
    private String title;
    //private String weburl = "https://prod.brongo.in/verification/Brongo/#/step1";  // for production
    private String weburl = "http://18.221.178.146:8080/verification/Brongo/#/step1";  //for development
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
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
    }
    private void initialise(){
        try {
            context = this;
            parentLayout = findViewById(R.id.verification_activity_linear);
            pref = getSharedPreferences(AppConstants.PREF_NAME,0);
            String broker_mobile = pref.getString(AppConstants.MOBILE_NUMBER,"");
            weburl = weburl+"?bmo="+broker_mobile;
            verification_title = findViewById(R.id.verification_title);
            verification_back = findViewById(R.id.toolbar_verification_back);
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
        public void onPageFinished(WebView view, String url) {
            try {
                if (isLoading){
                    Utils.LoaderUtils.dismissLoader();
                    isLoading = false;
                }
                enableOkButton(url);
            }catch(Exception exception){
                exception.printStackTrace();
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
            verification_webview.loadUrl(weburl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void enableOkButton(String url){
        String current_url = url;
       // String finalUrl = "https://prod.brongo.in/verification/Brongo/#/step4"; // for produc
        String finalUrl = "http://18.221.178.146:8080/verification/Brongo/#/step4";  //for dev
        if(current_url.equalsIgnoreCase(finalUrl)){
            ok_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
