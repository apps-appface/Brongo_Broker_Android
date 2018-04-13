package in.brongo.brongo_broker.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.Utils;

public class PdfNotificationActivity extends AppCompatActivity {
    private TextView pdf_noti_text,pdf_noti_desc;
    private Button pdf_noti_btn;
    private ImageView pdf_noti_imageView;
    private RelativeLayout parentLayout;
    private Context context;
    private List<String> listPermissionsNeeded;
    private int REQUEST_READABLE_AND_WRITABLE_PERMISSIONS = 111;
    private DownloadManager downloadManager;
    private String back_image,downloadurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_notification);
        back_image = downloadurl ="";
        if(getIntent().getExtras()!= null){
            //back_image = getIntent().getExtras().getString("backImage","");
            downloadurl = getIntent().getExtras().getString("url","");
        }
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        context = PdfNotificationActivity.this;
        pdf_noti_text = findViewById(R.id.pdf_noti_title);
        pdf_noti_desc = findViewById(R.id.pdf_noti_desc);
        pdf_noti_btn = findViewById(R.id.pdf_noti_btn);
        parentLayout = findViewById(R.id.pdf_noti_rel_parent);
        pdf_noti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermission();
            }
        });
    }
    private void downloadFile(String url){
        try {
            Uri downloadUri = Uri.parse(url);
            String fileName=url.substring(url.lastIndexOf("/")+1);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setNotificationVisibility(request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDescription("Downloading a file");
            long id = downloadManager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE) .setAllowedOverRoaming(false)
                    .setTitle(fileName).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkpermission(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkReadableAndWritablePermission()) {
                    downloadFile(downloadurl);
                } else {
                    requestReadableAndWritablePermission();
                }
            } else {
                downloadFile(downloadurl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
