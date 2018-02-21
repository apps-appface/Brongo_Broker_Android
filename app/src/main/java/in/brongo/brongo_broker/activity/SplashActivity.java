package in.brongo.brongo_broker.activity;

import android.Manifest;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.HashMap;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.uiwidget.GifSplashView;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.Utils;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static final int REQUEST_DEVICE_ID_PERMISSIONS = 112;
    private Context context;
    private LinearLayout parentLayout;
    private static int SPLASH_TIME_OUT = 5100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            context = SplashActivity.this;
            parentLayout = findViewById(R.id.splash_parent_linear);
            final GifSplashView check_mark_GV = findViewById(R.id.splash_gif);
            check_mark_GV.setGifResource(R.drawable.splash_gif);
            check_mark_GV.play();
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        check_mark_GV.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startSplashScreen1();
                }
            }, SPLASH_TIME_OUT);

            LogDeviceDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LogDeviceDetails() {
        try {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            Log.w(TAG, "=================LogDeviceDetails=================");
            Log.w(TAG, "screenSWHeight-> " + metrics.heightPixels/metrics.density);
            Log.w(TAG, "ScreenSWWidth-> " + metrics.widthPixels/metrics.density);
            Log.w(TAG, "densityDpi-> " + metrics.density);
            Log.w(TAG, "scaledDensity-> " + metrics.scaledDensity);
            Log.w(TAG, "xdpi-> " + metrics.xdpi);
            Log.w(TAG, "ydpi-> " + metrics.ydpi);
            Log.w(TAG, "=================LogDeviceDetails=================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSplashScreen1() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (isDeviceIdPermissionAllowed()) {
                    StartHomeActivity();
                } else {
                    requestDeviceIdPermission();
                }
            } else {
                StartHomeActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void StartHomeActivity() {
        try {
            Boolean login_status = pref.getBoolean(AppConstants.LOGIN_STATUS, false);
            Boolean isWalkthrough = pref.getBoolean(AppConstants.ISWALKTHROUGH, true);
            Boolean isTermAccepted = pref.getBoolean(AppConstants.IS_TERMS_ACCEPTED, false);
            if (isTermAccepted) {
                if (login_status) {
                    //new RefreshTokenCall(this,0);
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else {
                    if (isWalkthrough) {
                        Intent intent = new Intent(context, WalkThroughActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("from_activity", "splash");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                Intent intent = new Intent(context, TermsConditionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_DEVICE_ID_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartHomeActivity();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                        permissionDialog();
                    } else {
                        Utils.setSnackBar(parentLayout, "Permission Denied");
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Branch branch = Branch.getInstance();
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            final SharedPreferences.Editor editor = pref.edit();
            branch.initSession(new Branch.BranchUniversalReferralInitListener() {
                @Override
                public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                    if (error == null) {
                        if (branchUniversalObject != null) {
                            String identifier = branchUniversalObject.getCanonicalIdentifier();
                            Double money = branchUniversalObject.getPrice();
                            String title = branchUniversalObject.getTitle();
                            HashMap<String, String> map = branchUniversalObject.getMetadata();
                            String property2 = map.get("property2");
                            editor.putString(AppConstants.REFERREDBY, identifier);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void permissionDialog() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestDeviceIdPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_DEVICE_ID_PERMISSIONS);
    }

    private boolean isDeviceIdPermissionAllowed() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }
}