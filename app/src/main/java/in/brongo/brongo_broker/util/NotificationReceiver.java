package in.brongo.brongo_broker.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Rohit Kumar on 9/12/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1;
        Bundle data = intent.getExtras();
        String action = intent.getAction();
        if (data != null) {
            String message1 = data.getString("message");
            String noti_type = data.getString("NotiType");
            if (noti_type != null) {
                if (noti_type.equalsIgnoreCase("CLIENT_ACCEPT")) {
                    Utils.bidAcceptedDialog(message1, context);
                }
            }

        }
    }
}