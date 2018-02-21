package in.brongo.brongo_broker.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.brongo.brongo_broker.BuildConfig;
import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.uiwidget.GifView;

/**
 * Created by Rohit Kumar on 7/12/2017.
 */

public class Utils {
    private static boolean isDialog = false;
   public static Dialog dialog;
    public static BroadcastReceiver br = null;

    public static String getDeviceId(Context context) {
        String deviceId="";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            }
            deviceId = telephonyManager.getDeviceId();
            System.out.println("** DEVICE_ID ** " + deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void replaceFragment(FragmentManager fm, Fragment fragment, int containerId,String tag) {
        try {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(containerId, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return isInBackground;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    public static void storeDeviceInfo(Context context, SharedPreferences.Editor editor){
        PackageInfo pInfo = null;
        try {
           pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        String appVersion =  BuildConfig.VERSION_NAME;;
        String os_version = Build.VERSION.RELEASE;
        final String manufacturer = Build.MANUFACTURER;
        final String model_no = Build.MODEL;
        String model = manufacturer + " " + model_no;
        String device_id = Utils.getDeviceId(context);
        editor.putString(AppConstants.APP_VERSION,appVersion);
        editor.putString(AppConstants.MODEL_NAME,model);
        editor.putString(AppConstants.OS_VERSION,os_version);
        editor.putString(AppConstants.DEVICE_ID,device_id);
        editor.commit();
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }
    public static void setAlphaAnimation(View view,Context context){
        try {
            Animation animation = new AlphaAnimation(1.0f,0.7f);
            animation.setDuration(100);
            animation.setBackgroundColor(context.getResources().getColor(R.color.appColor));
            view.startAnimation(animation);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void showToast(Context context, String message, String title){
        try {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.alertdialog);
            dialog.setCanceledOnTouchOutside(false);
            final TextView title_text = (TextView) dialog.findViewById(R.id.alert_title);
            final TextView message_text = (TextView) dialog.findViewById(R.id.alert_message);
            title_text.setText(title);
            message_text.setText(message);
            final Button ok_btn = (Button) dialog.findViewById(R.id.alert_ok_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }catch (Exception e){

        }
    }
    public static void showAlert(String title,String message,Context context){
        try {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title)
                    .setMessage(message)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String numToWord(int number){
        String numToWord1 = "";
        try {
            String numToWord2 = "";
            int number1 = number;
            int number2 = 0;
            int number3 = 0;
            int length = (int) Math.log10(number) + 1;
            switch(length){
                case 4:
                    number1 = number/1000;
                    number3 = number%1000;
                    number2 = number3/100;
                    if(number2 == 0){
                        numToWord1 = number1+" K";
                    }else {
                        numToWord1 = number1 + "." + number2 + " K";
                    }
                    break;
                case 5:
                    number1 = number/1000;
                    number3 = number%1000;
                    number2 = number3/100;
                    if(number2 == 0){
                        numToWord1 = number1+" K";
                    }else {
                        numToWord1 = number1 + "." + number2 + " K";
                    }
                    break;
                case 6:
                    number1 = number/100000;
                    number3 = number % 100000;
                    number2 = number3/1000;
                    if(number2 == 0){
                        numToWord1 = number1+" Lacs";
                    }else {
                        numToWord1 = number1 + "." + number2 + " Lacs";
                    }
                    break;
                case 7:
                    number1 = number/100000;
                    number3 = number % 100000;
                    number2 = number3/1000;
                    if(number2 == 0){
                        numToWord1 = number1+" Lacs";
                    }else {
                        numToWord1 = number1 + "." + number2 + " Lacs";
                    }
                    break;
                case 8:
                    number1 = number/10000000;
                    number3 = number % 10000000;
                    number2 = number3/100000;
                    if(number2 == 0){
                        numToWord1 = number1+" Cr";
                    }else {
                        numToWord1 = number1 + "." + number2 + " Cr";
                    }
                    break;
                case 9:
                    number1 = number/10000000;
                    number3 = number % 10000000;
                    number2 = number3/100000;
                    if(number2 == 0){
                        numToWord1 = number1+" Cr";
                    }else {
                        numToWord1 = number1 + "." + number2 + " Cr";
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numToWord1;
    }

    public static String stringToInt(String lead_budget){

        String word="";
        try {
            String budget1="",budget2 ="";
            if(lead_budget != null && (!lead_budget.isEmpty())) {
                if (lead_budget.contains("-")) {
                    int index = lead_budget.indexOf("-");
                    if (lead_budget.contains(".")) {
                        budget1 = lead_budget.substring(0, index - 3);
                        budget2 = lead_budget.substring(index + 2, lead_budget.length() - 2);
                    } else {
                        budget1 = lead_budget.substring(0, index - 1);
                        budget2 = lead_budget.substring(index + 2, lead_budget.length());
                    }
                    int budget3 = 0, budget4 = 0;

                    try {
                        budget3 = Integer.parseInt(budget1);
                        budget4 = Integer.parseInt(budget2);
                        if (budget3 == 0 && budget4 == 0) {
                            word = "";
                        } else {
                            if (budget3 == 0) {
                                word = numToWord(budget4);
                            } else if (budget4 == 0) {
                                word = numToWord(budget3);
                            } else {
                                word = numToWord(budget3) + "-" + numToWord(budget4);
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                } else {
                    if (budget1.contains(".")) {
                        budget1 = lead_budget.substring(0, lead_budget.length() - 2);
                    } else {
                        budget1 = lead_budget;
                    }
                    int budget3 = 0;

                    try {
                        budget3 = Integer.parseInt(budget1);
                        word = numToWord(budget3);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        System.out.println("Could not parse " + nfe);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return word;
    }
    public static String getPostingColor(String post_type){
        String back_color = "#00000000";
        try {
            try {
                if (post_type.equalsIgnoreCase("sell")) {
                    back_color = "#3664cb";
                } else if (post_type.equalsIgnoreCase("rent_out")) {
                    back_color = "#60cb36";
                } else if (post_type.equalsIgnoreCase("rent")) {
                    back_color = "#60cb36";
                } else if (post_type.equalsIgnoreCase("BUY")) {
                    back_color = "#ea8737";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return back_color;
    }
    public static SpannableStringBuilder convertToSpannableString(String str1,int initial,int length,String color){
        SpannableStringBuilder str = new SpannableStringBuilder(str1);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), initial, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(color.equalsIgnoreCase("black")) {
            str.setSpan(new ForegroundColorSpan(Color.BLACK), initial, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if(color.equalsIgnoreCase("green")){
            str.setSpan(new ForegroundColorSpan(Color.GREEN), initial, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            str.setSpan(new ForegroundColorSpan(Color.rgb(157,157,157)), initial, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return str;
    }

    public static void internetDialog(final Context context,final NoInternetTryConnectListener internetTryConnectListener){
       // final Activity  activity = (Activity)context;
        try {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            final Button try_again_btn = (Button)dialog.findViewById(R.id.internet_dialog_btn);
            try_again_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable(context)) {
                        dialog.dismiss();
                        if (internetTryConnectListener != null)
                            internetTryConnectListener.onTryReconnect();
                    }
                }
            });
                dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bidAcceptedDialog(String message,Context context){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.bid_accepted_dialog);
            TextView message_text = (TextView)dialog.findViewById(R.id.bid_accepted_text);
            //dialog.setCanceledOnTouchOutside(false);
            // dialog.setCancelable(false);
            ImageView cross_btn = (ImageView) dialog.findViewById(R.id.dialog_close_btn);
            Button see_details_btn = (Button)dialog.findViewById(R.id.dialog_see_details);
            message_text.setText(message);
            cross_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            see_details_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clientAcceptDialog(Context context){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.noti_accept_dialog);
            //dialog.setCanceledOnTouchOutside(false);
            // dialog.setCancelable(false);
            final ImageView cross_btn = (ImageView) dialog.findViewById(R.id.client_accept_dialog_close);
            final Button got_it_btn = (Button)dialog.findViewById(R.id.client_accept_dialog_btn);
            cross_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            got_it_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void permissionDialog(final Context context){
        try {
            final Activity activity = (Activity)context;
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Permissions Required")
                    .setMessage("You have forcefully denied some of the required permissions " +
                            "for this action. Please open settings, go to permissions and allow them.")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean match_deal(String posting1,String posting2, String prop1, String prop2){
        boolean isMatched = false;
        try {
            if(prop1.equalsIgnoreCase(prop2)){
                if(posting1.equalsIgnoreCase("rent") && posting2.equalsIgnoreCase("rent_out")){
                    isMatched = true;
                }else if(posting1.equalsIgnoreCase("rent_out") && posting2.equalsIgnoreCase("rent")){
                    isMatched = true;
                }else if(posting1.equalsIgnoreCase("buy") && posting2.equalsIgnoreCase("sell")){
                    isMatched = true;
                }else if(posting1.equalsIgnoreCase("sell") && posting2.equalsIgnoreCase("buy")){
                    isMatched = true;
                }
            }else{
                return isMatched;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMatched;
    }
    public static String changeTimeFormat(String DateString){
        String formattedDate = DateString != null ? DateString : "";
        if (formattedDate != null && !formattedDate.isEmpty()) {
            try {
                Date apiFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(formattedDate);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE dd/MM/yyyy hh:mm a");
                formattedDate = sdf.format(apiFormat);
                String array[] = formattedDate.split("\\s+");
                formattedDate = array[0].toUpperCase() + " " + array[1] + " at " + array[2].toLowerCase() + " " + array[3].toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return formattedDate;
    }
    public static class LoaderUtils {
        public static void showLoader(Context context) {
            try {
                dialog = new Dialog(context, R.style.CustomProgressDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.progress_layout);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                    dialog.show();
                GifView check_mark_GV = (GifView) dialog.findViewById(R.id.check_mark_GV);
                check_mark_GV.setGifResource(R.drawable.loader);
                check_mark_GV.play();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public static void dismissLoader() {
            try {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static String getImageUrl(String stringurl,SharedPreferences pref){
        String url = "";
        if (!stringurl.contains("http")) {
            String baseurl = pref.getString(AppConstants.IMAGE_BASE_URL, "");
            stringurl = baseurl.concat(stringurl);
        }
        return url;
    }
    public static void setSnackBar(View coordinatorLayout, String snackTitle) {
        try {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_SHORT);
            snackbar.show();
            View view = snackbar.getView();
            TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void tokenDialog(final Context context,final NoTokenTryListener tokenTryListener) {
        // final Activity  activity = (Activity)context;
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_internet);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            final Button try_again_btn = (Button) dialog.findViewById(R.id.internet_dialog_btn);
            try_again_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable(context)) {
                        dialog.dismiss();
                        if (tokenTryListener != null)
                            tokenTryListener.onTryRegenerate();
                    }
                }
            });
                dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


