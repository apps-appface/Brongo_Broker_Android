package in.brongo.brongo_broker.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.MainActivity;
import in.brongo.brongo_broker.activity.Menu_Activity;
import in.brongo.brongo_broker.activity.PushAlertActivity;
import in.brongo.brongo_broker.model.ClientDetailsModel;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.makeText;

/**
 * Created by Rohit Kumar on 7/22/2017.
 */

public class MyNotificationListener extends FirebaseMessagingService {
    public static final int MESSAGE_NOTIFICATION_ID = 1337;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    Context context = MyNotificationListener.this;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        pref = getApplication().getSharedPreferences(AppConstants.PREF_NAME, 0);
/*

        Log.i("NOTITYPE 1",remoteMessage.getNotification().getBody());

        defaultNotification(remoteMessage.getNotification().getBody());*/

        if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
            MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
            return;
        } else {
            editor = pref.edit();
            int i = pref.getInt(AppConstants.NOTIFICATION_BADGES, 0);
            i = i + 1;
            editor.putInt(AppConstants.NOTIFICATION_BADGES, i).commit();
            String noti_type = remoteMessage.getData().get("NotiType");
            Intent intent;
            if (noti_type != null) {
                Log.i("NOTITYPE",noti_type);
                switch (noti_type) {
                    case "APP":
                        Log.i("NOTITYPE","******** Getting Call **********");
                        if (pref.getBoolean(AppConstants.LOGIN_STATUS, false)) {
                            editor.putString(AppConstants.NOTI_TYPE, "call").commit();
                            callNotification(remoteMessage);
                        }
                        break;
                    case "CLIENT_ACCEPT":
                        if (!isAppIsInBackground(context)) {
                            backgroundNotification(remoteMessage);
                        }
                        foregroundNotification(remoteMessage);
                        break;
                    case "STOP_TIMER":
                        intent = new Intent();
                        intent.setAction("3_broker_done");
                        sendBroadcast(intent);
                        break;
                    case "LEADS_UPDATE":
                        if (!isAppIsInBackground(MyNotificationListener.this)) {
                            backgroundNotification(remoteMessage);
                        }
                        foregroundNotification(remoteMessage);
                        //createNotification();
                        break;
                    case "DROP_DEAL":
                        if (!isAppIsInBackground(MyNotificationListener.this)) {
                            backgroundNotification(remoteMessage);
                        }
                        foregroundNotification(remoteMessage);
                        //createNotification();
                        break;
                    case "ASSIGN_NEW_BROKER":
                        if (!isAppIsInBackground(MyNotificationListener.this)) {
                            backgroundNotification(remoteMessage);
                        }
                        foregroundNotification(remoteMessage);
                        //createNotification();
                        break;
                    case "BUILDER_POSTING":
                        if (!isAppIsInBackground(context)) {
                            backgroundNotification(remoteMessage);
                        }
                        defaultNotification(remoteMessage);
                        //createNotification();
                        break;
                    default:
                        if (!isAppIsInBackground(context)) {
                            backgroundNotification(remoteMessage);
                        }
                        defaultNotification(remoteMessage);
                }
            }
        }
    }


    private void foregroundNotification(RemoteMessage remoteMessage) {
        Uri soundUri1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String message = remoteMessage.getData().get("message");
        android.support.v4.app.NotificationCompat.Builder builder = buildNotification(message, "mainpage");
        NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder.setSound(soundUri1);
        notificationManager.notify(getRandomNotificationID(), builder.build());
    }

    private void defaultNotification(RemoteMessage remoteMessage) {
        Uri soundUri1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String message = remoteMessage.getData().get("message");
        android.support.v4.app.NotificationCompat.Builder builder = buildNotification(message, "menuPage");
        NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder.setSound(soundUri1);
        notificationManager.notify(getRandomNotificationID(), builder.build());
    }


    private void defaultNotification(String remoteMessage) {
        Uri soundUri1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String message = remoteMessage;
        android.support.v4.app.NotificationCompat.Builder builder = buildNotification(message, "menuPage");
        NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder.setSound(soundUri1);
        notificationManager.notify(getRandomNotificationID(), builder.build());
    }

    private void backgroundNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent("activity_refresh");

        //put whatever data you want to send, if any
        intent.putExtra("message", remoteMessage.getData().get("message"));
        intent.putExtra("NotiType", remoteMessage.getData().get("NotiType"));
        //send broadcast
        context.sendBroadcast(intent);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
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
        return isInBackground;
    }

    private void callNotification(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Bundle data = new Bundle();
            data.putString("microMarketName", remoteMessage.getData().get("microMarketName"));
            data.putString("clientMobileNo", remoteMessage.getData().get("clientMobileNo"));
            data.putString("bedRoomType", remoteMessage.getData().get("bedRoomType"));
            data.putString("propertyStatus", remoteMessage.getData().get("propertyStatus"));
            data.putString("propertyType", remoteMessage.getData().get("propertyType"));
            data.putString("commission", remoteMessage.getData().get("commission"));
            data.putString("propertyId", remoteMessage.getData().get("propertyId"));
            data.putString("clientName", remoteMessage.getData().get("clientName"));
            data.putString("rating", remoteMessage.getData().get("rating"));
            data.putString("image", remoteMessage.getData().get("image"));
            data.putString("postingType", remoteMessage.getData().get("postingType"));
            data.putString("budget", remoteMessage.getData().get("budget"));
            data.putString("planType", remoteMessage.getData().get("planType"));
            data.putString("subPropertyType", remoteMessage.getData().get("subPropertyType"));
            data.putString("matchedProperties", remoteMessage.getData().get("matchedProperties"));
            data.putString("impFields", remoteMessage.getData().get("impFields"));
            Intent intent1 = new Intent(MyNotificationListener.this, PushAlertActivity.class);
            intent1.putExtras(data);
              intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
    }

    protected android.support.v4.app.NotificationCompat.Builder buildNotification(String message, String page) {
        PendingIntent pIntent = PendingIntent.getActivity(
                context,
                0,
                getIntent(page),
                PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logowhite2)
                .setTicker(message)
                .setAutoCancel(true)
                .setContentIntent(pIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getComplexNotificationView(message));
        } else {
            builder = builder.setContentTitle(getTitle())
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.ic_menu_gallery);
        }
        return builder;
    }

    private Intent getIntent(String page) {
        Intent intent = null;
        try {
            if (page.equalsIgnoreCase("mainpage")) {
                intent = new Intent(context, MainActivity.class);
            } else {
                intent = new Intent(context, Menu_Activity.class);
                intent.putExtra("frgToLoad", "NotificationFragment");
            }
        } catch (Exception e) {

        }
        return intent;
    }

    private RemoteViews getComplexNotificationView(String message) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
                context.getPackageName(),
                R.layout.notification
        );

        // Locate and set the Image into customnotificationtext.xml ImageViews
        notificationView.setImageViewResource(
                R.id.noti_left_image,
                R.mipmap.ic_launcher_round);

        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.title, getTitle());
        notificationView.setTextViewText(R.id.text, message);

        return notificationView;
    }

    public CharSequence getTitle() {
        return "Brongo";
    }

    private int getRandomNotificationID() {
        return new Random().nextInt(100);
    }


}

