package in.brongo.brongo_broker.auto_start;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import in.brongo.brongo_broker.R;

public class AutoStart {
    private static final String TAG = "AutoStart";
    public String PREF_AUTO_START = "BRONGO_AUTO_START";
    private SharedPreferences pref;
    private String manufacturer;
    private Context mContext;

    public AutoStart(Context mContext) {
        this.mContext = mContext;
        pref = mContext.getSharedPreferences(PREF_AUTO_START, 0);
        manufacturer = android.os.Build.MANUFACTURER;
    }

    public void showAutoStartDialog() {
        if (("xiaomi".equalsIgnoreCase(manufacturer)
                || "oneplus".equalsIgnoreCase(manufacturer)
                || "oppo".equalsIgnoreCase(manufacturer)
                || "vivo".equalsIgnoreCase(manufacturer)) && !pref.getBoolean("displayPopup", false)) {
            informationDialog();
        }
    }

    private void informationDialog() {
        final Dialog dialogBlock = new Dialog(mContext, R.style.DialogThemeAutoStart);
        dialogBlock.setContentView(R.layout.auto_start_dialog);
        dialogBlock.setCanceledOnTouchOutside(false);
        dialogBlock.setCancelable(false);
        dialogBlock.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Button stopShowingBtn = dialogBlock.findViewById(R.id.stopShowingBtn);
        stopShowingBtn.setVisibility(View.GONE);
        ImageView cancel = dialogBlock.findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        dialogBlock.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBlock.dismiss();
                if (!pref.getBoolean("displayStopBtn", false)) {
                    pref.edit().putBoolean("displayStopBtn", true).apply();
                }
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    xiaomi();
                } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                    onePlus();
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    oppo();
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    vivo();
                }
            }
        });

        if (pref.getBoolean("displayStopBtn", false)) {
            stopShowingBtn.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            stopShowingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBlock.dismiss();
                    pref.edit().putBoolean("displayPopup", true).apply();
                }
            });
        }

        dialogBlock.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBlock.dismiss();
            }
        });
        dialogBlock.show();
    }


    private void xiaomi() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            pref.edit().putBoolean("displayPopup", true).apply();
            Crashlytics.logException(e);
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
        } catch (Exception e) {
            pref.edit().putBoolean("displayPopup", true).apply();
            Crashlytics.logException(e);
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
        }
    }

    private void onePlus() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.oneplus.security",
                    "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            pref.edit().putBoolean("displayPopup", true).apply();
            Crashlytics.logException(e);
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
        } catch (Exception e) {
            pref.edit().putBoolean("displayPopup", true).apply();
            Crashlytics.logException(e);
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
        }
    }

    private void oppo() {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(new ComponentName("com.oppo.safe",
                    "com.oppo.safe.permission.floatwindow.FloatWindowListActivity"));
            mContext.startActivity(i);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);

            try {
                Intent intent = new Intent("action.coloros.safecenter.FloatWindowListActivity");
                intent.setComponent(new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
                mContext.startActivity(intent);
            } catch (Exception ee) {
                Crashlytics.logException(ee);
                Log.e(TAG, "Failed to launch AutoStart Screen ", ee);

                try {
                    Intent i = new Intent("com.coloros.safecenter");
                    i.setComponent(new ComponentName("com.coloros.safecenter",
                            "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"));
                    mContext.startActivity(i);
                } catch (Exception e1) {
                    Crashlytics.logException(e1);
                    Log.e(TAG, "Failed to launch AutoStart Screen ", e1);

                    try {
                        Intent i = new Intent("com.coloros.safecenter");
                        i.setComponent(new ComponentName("com.coloros.safecenter",
                                "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                        mContext.startActivity(i);
                    } catch (Exception e2) {
                        pref.edit().putBoolean("displayPopup", true).apply();
                        Crashlytics.logException(e2);
                        Log.e(TAG, "Failed to launch AutoStart Screen ", e2);
                    }
                }
            }
        }
    }


    private void vivo() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
            mContext.startActivity(intent);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e(TAG, "Failed to launch AutoStart Screen ", e);
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                mContext.startActivity(intent);
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.e(TAG, "Failed to launch AutoStart Screen ", ex);
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                    mContext.startActivity(intent);
                } catch (Exception exx) {
                    pref.edit().putBoolean("displayPopup", true).apply();
                    Crashlytics.logException(exx);
                    Log.e(TAG, "Failed to launch AutoStart Screen ", exx);
                }
            }
        }
    }

}
