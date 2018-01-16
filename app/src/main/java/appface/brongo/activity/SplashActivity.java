package appface.brongo.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import appface.brongo.R;
import appface.brongo.services.TokenServices;
import appface.brongo.util.AppConstants;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.Utils;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class SplashActivity extends AppCompatActivity {
    private ImageView login_image;
    private TextView appname;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static final int REQUEST_DEVICE_ID_PERMISSIONS = 112;
    private static final long ANIMATION_TIME = 1500;
    private Context context;
    private static int SPLASH_TIME_OUT = 2700;
    private TextView title, subTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;
        title = (TextView) findViewById(R.id.textview);
        subTitle = (TextView) findViewById(R.id.subtitle);
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        title.setVisibility(View.INVISIBLE);
        subTitle.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSplashScreen1();
            }
        }, SPLASH_TIME_OUT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                title.setVisibility(View.VISIBLE);
                BounceInterpolator bounceInterpolator = new BounceInterpolator();
                ObjectAnimator anim = ObjectAnimator.ofFloat(title, "translationY", 0f, 40);
                anim.setInterpolator(bounceInterpolator);
                anim.setDuration(300).start();
            }
        }, 2100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                subTitle.setVisibility(View.VISIBLE);
                BounceInterpolator bounceInterpolator = new BounceInterpolator();
                ObjectAnimator anim = ObjectAnimator.ofFloat(subTitle, "translationY", 0f, 47);
                anim.setInterpolator(bounceInterpolator);
                anim.setDuration(200).start();
            }
        }, 2400);
    }

    private void startSplashScreen1() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (isDeviceAllowed()) {
               startHomeActivity();
            } else {
               requestDeviceIdPermission();
            }
        } else{
            startHomeActivity();
        }
    }

    private void startHomeActivity() {
        Boolean login_status = pref.getBoolean(AppConstants.LOGIN_STATUS, false);
        Boolean isWalkthrough = pref.getBoolean(AppConstants.ISWALKTHROUGH,true);
            if (login_status) {
                //new RefreshTokenCall(this,0);
                startActivity(new Intent(context,MainActivity.class));
               finish();
            } else {
                if(isWalkthrough) {
                    Intent intent = new Intent(getApplicationContext(), WalkThroughActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("from_activity","splash");
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
       /*Intent intent = new Intent(SplashActivity.this,DocumentUploadActivity.class);
        intent.putExtra("frgToLoad","AddInventoryFragment");
        startActivity(intent);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_DEVICE_ID_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startHomeActivity();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                     permissionDialog();
                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        final SharedPreferences.Editor editor = pref.edit();
        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null) {
                    if(branchUniversalObject != null) {
                        String identifier = branchUniversalObject.getCanonicalIdentifier();
                        Double money = branchUniversalObject.getPrice();
                        String title = branchUniversalObject.getTitle();
                        HashMap<String,String > map = branchUniversalObject.getMetadata();
                        String property2 = map.get("property2");
                        editor.putString(AppConstants.REFERREDBY,identifier);
                        editor.commit();
                        // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                        // params will be empty if no data found
                        // ... insert custom logic here ...
                    }
                } else {
                    Log.i("MyApp", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    private void permissionDialog(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Permissions Required")
                .setMessage("You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestDeviceIdPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_DEVICE_ID_PERMISSIONS);
    }
    private boolean isDeviceAllowed() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }
}