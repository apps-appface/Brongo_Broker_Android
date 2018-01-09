package appface.brongo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import appface.brongo.R;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.ImageFilePath;
import appface.brongo.util.ImageUtils;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener,NoInternetTryConnectListener {
    private final String TAG = DocumentUploadActivity.class.getName();
    Context context;
    private LinearLayout docu_linear1,docu_linear2,docu_linear3,docu_linear4,docu_linear22,docu_linear33,docu_linear44;
    private TextView docu_btn1,docu_btn2,docu_btn3,docu_btn4,round_text1,round_text2,round_text3,round_text4,docu_title;
    private EditText rera_edit,pan_edit;
    private int i,open_view1,open_view2,open_view3,open_view4,image_code2,image_code3,image_code4;
    private ImageView docu_imageview,document_back,check1,check2,check3,check4;
    private Button continue_btn;
    private TextView docu_heading,recapture_text,remove_text;
    private TextView gallery_btn,camera_btn;
    private int Gallery_CODE = 30;
    private int Camera_CODE = 50;
    private int fileNumber = -1;
    private final int STORAGE_PERMISSION_REQUEST = 100;
    private final int CAMERA_PERMISSION_REQUEST = 200;
    private String filename,filename1,filename2,filename3;
   private Uri uri;
   private MultipartBody.Part reraCertificate,IDProof,addressProof;
    private SharedPreferences pref ;
   private SharedPreferences.Editor editor;
    private String compressedImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        initialise();
        setListener();
        setTextWatcher();

    }

    private void initialise() {
        context = this;
        docu_title = (TextView)findViewById(R.id.document_toolbar).findViewById(R.id.other_toolbar_title);
        document_back = (ImageView) findViewById(R.id.document_toolbar).findViewById(R.id.other_toolbar_back);
        check1 = (ImageView)findViewById(R.id.document_check1);
        check2 = (ImageView)findViewById(R.id.document_check2);
        check3 = (ImageView)findViewById(R.id.document_check3);
        check4 = (ImageView)findViewById(R.id.document_check4);
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        docu_btn1 = (TextView) findViewById(R.id.docu_btn1);
        docu_btn2 = (TextView)findViewById(R.id.docu_btn2);
        docu_btn3 = (TextView)findViewById(R.id.docu_btn3);
        docu_btn4 = (TextView)findViewById(R.id.docu_btn4);
        continue_btn = (Button)findViewById(R.id.continue_btn);
        round_text1 = (TextView)findViewById(R.id.round_text1);
        round_text2 = (TextView)findViewById(R.id.round_text2);
        round_text3 = (TextView)findViewById(R.id.round_text3);
        round_text4 = (TextView)findViewById(R.id.round_text4);
        docu_linear1 = (LinearLayout)findViewById(R.id.docu_linear1);
        docu_linear2 = (LinearLayout)findViewById(R.id.docu_linear2);
        docu_linear3 = (LinearLayout)findViewById(R.id.docu_linear3);
        docu_linear4 = (LinearLayout)findViewById(R.id.docu_linear4);
        docu_linear22 = (LinearLayout)findViewById(R.id.docu_linear22);
        docu_linear33 = (LinearLayout)findViewById(R.id.docu_linear33);
        docu_linear44 = (LinearLayout)findViewById(R.id.docu_linear44);
        rera_edit = (EditText)findViewById(R.id.rera_registration);
        pan_edit = (EditText)findViewById(R.id.pan_no);
        docu_heading=(TextView)findViewById(R.id.btn_no);
        docu_title.setText("Submit Documents");
    }
    private void setTextWatcher() {
        pan_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0 && rera_edit.getText().length()>0){
                   check1.setVisibility(View.VISIBLE);
                }else{
                    check1.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rera_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0 && pan_edit.getText().length()>0){
                    check1.setVisibility(View.VISIBLE);
                }else{
                    check1.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

            }

    private void setListener() {
        docu_btn1.setOnClickListener(this);
        docu_btn2.setOnClickListener(this);
        docu_btn3.setOnClickListener(this);
        docu_btn4.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        document_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentUploadActivity.this,SignUpActivity.class));
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // do somthing...
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Gallery_CODE) {
                onSelectFromGalleryResult(data);

            } else if (requestCode == Camera_CODE) {
                onCaptureImageResult();
            }
            switch(i){
                case 2:
                    docu_linear22.setVisibility(View.VISIBLE);
                    docu_linear2.setVisibility(View.GONE);
                    image_code2=1;
                    break;
                case 3:
                    docu_linear33.setVisibility(View.VISIBLE);
                    docu_linear3.setVisibility(View.GONE);
                    image_code3=1;
                    break;
                case 4:
                    docu_linear44.setVisibility(View.VISIBLE);
                    docu_linear4.setVisibility(View.GONE);
                    image_code4=1;
                    break;
            }

        } else if (resultCode == RESULT_CANCELED) {
            Utils.showToast(context," Picture selection was canceled");
        } else {
            Utils.showToast(this, " Picture was not taken ");
        }

    }
    private void onSelectFromGalleryResult(Intent data) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(context, selectedImageUri);
                Log.i("Image File Path", "" + selectedImagePath);
                uri = Uri.fromFile(new File(selectedImagePath));
                loadGlide(data);
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            uri = Uri.fromFile(new File(imagePath));
            cursor.close();
            loadGlide(data);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
            compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
            Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);
        }
        prepareFilePart(uri);
    }
    private void prepareFilePart(Uri fileUri) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            compressedImagePath = fileUri.getPath();
        }
        if (fileUri != null && fileUri.getPath().length() > 0 && compressedImagePath != null && compressedImagePath.length() > 0) {
            File imageFile = new File(compressedImagePath);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            filename = imageFile.getName();
        // MultipartBody.Part is used to send also the actual file name
        switch(fileNumber){
            case 0:
                reraCertificate = MultipartBody.Part.createFormData("reraCertificate", imageFile.getName(), requestFile);
                filename1 = filename;
                break;
            case 1:
                IDProof = MultipartBody.Part.createFormData("IDProof",  imageFile.getName(), requestFile);
                filename2 = filename;
                break;
            case 2:
                addressProof = MultipartBody.Part.createFormData("addressProof",  imageFile.getName(), requestFile);
                filename3 = filename;
                break;
        }
    }
    }


    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.continue_btn:
                   uploadAll();
               break;
           case R.id.docu_btn1:
               i=1;
               setView(i);
               break;
           case R.id.docu_btn2:
               i=2;
               fileNumber=0;
               setView(i);
               docu_imageview = (ImageView)findViewById(R.id.layout22).findViewById(R.id.docu_imageview);
               recapture_text = (TextView)findViewById(R.id.layout22).findViewById(R.id.recapture_text);
               remove_text = (TextView)findViewById(R.id.layout22).findViewById(R.id.remove_text);
               gallery_btn= (TextView) findViewById(R.id.layout2).findViewById(R.id.docu_gallery);
               camera_btn= (TextView)findViewById(R.id.layout2).findViewById(R.id.docu_camera);
               gallery_btn.setOnClickListener(this);
               camera_btn.setOnClickListener(this);
               remove_text.setOnClickListener(this);
               recapture_text.setOnClickListener(this);
               break;
           case R.id.docu_btn3:
               i=3;
               fileNumber =1;
               setView(i);
               docu_imageview = (ImageView)findViewById(R.id.layout33).findViewById(R.id.docu_imageview);
               recapture_text = (TextView)findViewById(R.id.layout33).findViewById(R.id.recapture_text);
               remove_text = (TextView)findViewById(R.id.layout33).findViewById(R.id.remove_text);
               gallery_btn= (TextView)findViewById(R.id.layout3).findViewById(R.id.docu_gallery);
               camera_btn= (TextView)findViewById(R.id.layout3).findViewById(R.id.docu_camera);
               gallery_btn.setOnClickListener(this);
               camera_btn.setOnClickListener(this);
               remove_text.setOnClickListener(this);
               recapture_text.setOnClickListener(this);
               break;
           case R.id.docu_btn4:
               i=4;
               fileNumber =2;
               setView(i);
               docu_imageview = (ImageView)findViewById(R.id.layout44).findViewById(R.id.docu_imageview);
               recapture_text = (TextView)findViewById(R.id.layout44).findViewById(R.id.recapture_text);
               remove_text = (TextView)findViewById(R.id.layout44).findViewById(R.id.remove_text);
               gallery_btn= (TextView)findViewById(R.id.layout4).findViewById(R.id.docu_gallery);
               camera_btn= (TextView)findViewById(R.id.layout4).findViewById(R.id.docu_camera);
               gallery_btn.setOnClickListener(this);
               camera_btn.setOnClickListener(this);
               remove_text.setOnClickListener(this);
               recapture_text.setOnClickListener(this);
               break;
           case R.id.docu_gallery:
               if (Build.VERSION.SDK_INT >=23) {
                   if (ContextCompat.checkSelfPermission(DocumentUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions(DocumentUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                   } else {
                       openGallery();
                   }
               }else{
                   openGallery();
               }
               //Toast.makeText(context,"you clicked gallery button : "+i,Toast.LENGTH_SHORT).show();
               break;
           case R.id.docu_camera:
               if (Build.VERSION.SDK_INT >=23) {
                   if (ContextCompat.checkSelfPermission(DocumentUploadActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions(DocumentUploadActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                   } else {
                       openCamera();
                   }
               }else{
                   openCamera();
               }
               //Toast.makeText(context,"you clicked camera button : "+i,Toast.LENGTH_SHORT).show();
               break;
           case R.id.remove_text:
              docu_imageview.setImageResource(android.R.color.transparent);
               switch(i){
                   case 2:
                       docu_linear22.setVisibility(View.GONE);
                       docu_linear2.setVisibility(View.VISIBLE);
                       check2.setVisibility(View.GONE);
                       image_code2 = 0;
                       reraCertificate =null;
                       break;
                   case 3:
                       docu_linear33.setVisibility(View.GONE);
                       docu_linear3.setVisibility(View.VISIBLE);
                       check3.setVisibility(View.GONE);
                       image_code3 = 0;
                       IDProof=null;
                       break;
                   case 4:
                       docu_linear44.setVisibility(View.GONE);
                       docu_linear4.setVisibility(View.VISIBLE);
                       check4.setVisibility(View.GONE);
                       image_code4 = 0;
                       addressProof=null;
                       break;
               }
               break;
           case R.id.recapture_text:
               switch(i){
                   case 2:
                       docu_linear22.setVisibility(View.GONE);
                       docu_linear2.setVisibility(View.VISIBLE);
                       break;
                   case 3:
                       docu_linear33.setVisibility(View.GONE);
                       docu_linear3.setVisibility(View.VISIBLE);
                       break;
                   case 4:
                       docu_linear44.setVisibility(View.GONE);
                       docu_linear4.setVisibility(View.VISIBLE);
                       break;
               }
       }
    }
    private void uploadAll(){
        if(Utils.isNetworkAvailable(context)) {
            RequestBody mobileNo = RequestBody.create(MediaType.parse("multipart/form-data"), pref.getString(AppConstants.MOBILE_NUMBER, ""));
            if (reraCertificate != null && IDProof != null && addressProof != null) {
                String panNo = pan_edit.getText().toString();
                String reraRegistration = rera_edit.getText().toString();
                if (panNo != null && reraRegistration != null) {
                    RequestBody panCardNumber = RequestBody.create(MediaType.parse("multipart/form-data"), panNo);
                    RequestBody reraRegistrationNumber = RequestBody.create(MediaType.parse("multipart/form-data"), reraRegistration);
                    RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                    Utils.LoaderUtils.showLoader(context);
                    Call call = retrofitAPIs.uploadFile(mobileNo, panCardNumber, reraRegistrationNumber, reraCertificate, addressProof, IDProof);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Utils.LoaderUtils.dismissLoader();
                            if (response != null) {
                                if (response.isSuccessful()) {
                                    String responseString = null;
                                    try {
                                        responseString = response.body().string();
                                        JSONObject jsonObject = new JSONObject(responseString);
                                        int statusCode = jsonObject.optInt("statusCode");
                                        String message = jsonObject.optString("message");
                                        if (statusCode == 200 && message.equalsIgnoreCase("Broker Documents Successfully Uploaded")) {
                                            Utils.showToast(context, message);
                                            startActivity(new Intent(DocumentUploadActivity.this, MapActivity.class));
                                            finish();
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String responseString = null;
                                    try {
                                        responseString = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(responseString);
                                        String message = jsonObject.optString("message");
                                        int statusCode = jsonObject.optInt("statusCode");
                                        Utils.showToast(context, message);
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Utils.LoaderUtils.dismissLoader();
                        }
                    });
                } else {
                    Utils.showToast(context, "Either pan no or rera registration is empty");
                }
            } else {
                Utils.showToast(context, "select all file first");
            }
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = getOutputMediaFileUri();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, Camera_CODE);

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), Gallery_CODE);
    }
    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Brongo/document");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "BR_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        System.out.println("Media File Name for New Post : " + mediaFile);
        return mediaFile;
    }
    private void onCaptureImageResult() {
        try {
            Glide.with(context)
                    .load(new File(uri.getPath()))
                    .into(docu_imageview);
            compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
            Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (i){
            case 2:
                check2.setVisibility(View.VISIBLE);
                break;
            case 3:
                check3.setVisibility(View.VISIBLE);
                break;
            case 4:
                check4.setVisibility(View.VISIBLE);
        }
        prepareFilePart(uri);

    }
    private void loadGlide(Intent data) {
        try {
            //Bitmap bm = null;
            if (data != null) {
                //bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                Glide.with(context)
                        .load(new File(uri.getPath()))
                        .into(docu_imageview);

                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
                    compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
                    Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (i){
            case 2:
                check2.setVisibility(View.VISIBLE);
                break;
            case 3:
                check3.setVisibility(View.VISIBLE);
                break;
            case 4:
                check4.setVisibility(View.VISIBLE);
        }

    }
    private void setView(int i){
        docu_linear1.setVisibility(View.GONE);
        docu_linear2.setVisibility(View.GONE);
        docu_linear3.setVisibility(View.GONE);
        docu_linear4.setVisibility(View.GONE);
        docu_linear22.setVisibility(View.GONE);
        docu_linear33.setVisibility(View.GONE);
        docu_linear44.setVisibility(View.GONE);
        docu_btn4.setTextColor(getResources().getColor(R.color.appColor));
        docu_btn4.setBackgroundResource(R.drawable.docu_btn_white);
        docu_btn3.setTextColor(getResources().getColor(R.color.appColor));
        docu_btn3.setBackgroundResource(R.drawable.docu_btn_white);
        docu_btn2.setTextColor(getResources().getColor(R.color.appColor));
        docu_btn2.setBackgroundResource(R.drawable.docu_btn_white);
        docu_btn1.setTextColor(getResources().getColor(R.color.appColor));
        docu_btn1.setBackgroundResource(R.drawable.docu_btn_white);
        round_text1.setTextColor(getResources().getColor(R.color.white));
        round_text1.setBackgroundResource(R.drawable.text_circular);
        round_text2.setTextColor(getResources().getColor(R.color.white));
        round_text2.setBackgroundResource(R.drawable.text_circular);
        round_text3.setTextColor(getResources().getColor(R.color.white));
        round_text3.setBackgroundResource(R.drawable.text_circular);
        round_text4.setTextColor(getResources().getColor(R.color.white));
        round_text4.setBackgroundResource(R.drawable.text_circular);
        docu_heading.setText("");
        switch(i){
            case 1:
                if(open_view1 == 0) {
                    docu_linear1.setVisibility(View.VISIBLE);
                    docu_heading.setText("Step "+i+" of 4");
                    docu_btn1.setTextColor(getResources().getColor(R.color.white));
                    docu_btn1.setBackgroundResource(R.drawable.docu_button);
                    round_text1.setTextColor(getResources().getColor(R.color.appColor));
                    round_text1.setBackgroundResource(R.drawable.text_circular_white);
                    open_view1=1;
                }else if(open_view1 == 1){
                    open_view1=0;
                }
                break;
            case 2:
                if(open_view2 == 0) {
                    if(image_code2 == 0) {
                        docu_linear2.setVisibility(View.VISIBLE);
                    }else if(image_code2==1){
                        docu_linear22.setVisibility(View.VISIBLE);
                    }
                    docu_heading.setText("Step "+i+" of 4");
                    docu_btn2.setTextColor(getResources().getColor(R.color.white));
                    docu_btn2.setBackgroundResource(R.drawable.docu_button);
                    round_text2.setTextColor(getResources().getColor(R.color.appColor));
                    round_text2.setBackgroundResource(R.drawable.text_circular_white);
                    open_view2 = 1;
                }else if(open_view2 == 1){
                    open_view2=0;
                }
                break;
            case 3: if(open_view3 == 0) {
                if(image_code3 == 0) {
                    docu_linear3.setVisibility(View.VISIBLE);
                }else if(image_code3==1){
                    docu_linear33.setVisibility(View.VISIBLE);
                }
                docu_heading.setText("Step "+i+" of 4");
                docu_btn3.setTextColor(getResources().getColor(R.color.white));
                docu_btn3.setBackgroundResource(R.drawable.docu_button);
                round_text3.setTextColor(getResources().getColor(R.color.appColor));
                round_text3.setBackgroundResource(R.drawable.text_circular_white);
                open_view3=1;
            }else if(open_view3 == 1){
                open_view3=0;
            }
                break;
            case 4:
                if(open_view4 == 0) {
                    if(image_code4 == 0) {
                        docu_linear4.setVisibility(View.VISIBLE);
                    }else if(image_code4==1){
                        docu_linear44.setVisibility(View.VISIBLE);
                    }
                docu_btn4.setTextColor(getResources().getColor(R.color.white));
                    docu_heading.setText("Step "+i+" of 4");
                docu_btn4.setBackgroundResource(R.drawable.docu_button);
                round_text4.setTextColor(getResources().getColor(R.color.appColor));
                round_text4.setBackgroundResource(R.drawable.text_circular_white);
                open_view4 = 1;
        }else if(open_view4 == 1){
            open_view4=0;
        }
                break;
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }else{
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Utils.permissionDialog(context);
                        }
                    }
                }
                break;
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            Utils.permissionDialog(context);
                        }
                    }
                }
        }

        }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DocumentUploadActivity.this,SignUpActivity.class));
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        uploadAll();
    }
}