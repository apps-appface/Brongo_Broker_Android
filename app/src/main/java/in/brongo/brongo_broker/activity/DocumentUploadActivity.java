package in.brongo.brongo_broker.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.ImageFilePath;
import in.brongo.brongo_broker.util.ImageUtils;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, NoInternetTryConnectListener {
    private final String TAG = DocumentUploadActivity.class.getName();
    private Context context;
    private boolean doubleBackToExitPressedOnce = false;
    private LinearLayout docu_linear1, docu_linear2, docu_linear3, docu_linear4, docu_linear22, docu_linear33, docu_linear44, parentLayout;
    private TextView docu_btn1, docu_btn2, docu_btn3, docu_btn4, round_text1, round_text2, round_text3, round_text4, docu_title, docu_skip;
    private EditText rera_edit, pan_edit;
    private int i, open_view1, open_view2, open_view3, open_view4, image_code2, image_code3, image_code4;
    private ImageView docu_imageview, document_back, check1, check2, check3, check4;
    private Button continue_btn;
    private TextView docu_heading;
    private RelativeLayout recapture_text, remove_text, gallery_btn, camera_btn;
    private int Gallery_CODE = 30;
    private int Camera_CODE = 50;
    private int fileNumber = -1;
    private final int STORAGE_PERMISSION_REQUEST = 100;
    private String filename, filename1, filename2, filename3;
    private Uri uri;
    private File image_file;
    private MultipartBody.Part reraCertificate, IDProof, addressProof;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String compressedImagePath;
    private Bundle bundle = new Bundle();
    private List<String> listPermissionsNeeded;
    public static final int REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }
        initialise();
        setListener();
        setTextWatcher();
        docu_btn1.performClick();
    }

    private void initialise() {
        try {
            context = this;
            parentLayout = findViewById(R.id.document_activity_linear);
            docu_title = findViewById(R.id.document_toolbar).findViewById(R.id.other_toolbar_title);
            document_back = findViewById(R.id.document_toolbar).findViewById(R.id.other_toolbar_back);
            docu_skip = findViewById(R.id.document_toolbar).findViewById(R.id.other_toolbar_skip);
            check1 = findViewById(R.id.document_check1);
            check2 = findViewById(R.id.document_check2);
            check3 = findViewById(R.id.document_check3);
            check4 = findViewById(R.id.document_check4);
            pref = getSharedPreferences(AppConstants.PREF_NAME, 0);
            docu_btn1 = findViewById(R.id.docu_btn1);
            docu_btn2 = findViewById(R.id.docu_btn2);
            docu_btn3 = findViewById(R.id.docu_btn3);
            docu_btn4 = findViewById(R.id.docu_btn4);
            continue_btn = findViewById(R.id.continue_btn);
            round_text1 = findViewById(R.id.round_text1);
            round_text2 = findViewById(R.id.round_text2);
            round_text3 = findViewById(R.id.round_text3);
            round_text4 = findViewById(R.id.round_text4);
            docu_linear1 = findViewById(R.id.docu_linear1);
            docu_linear2 = findViewById(R.id.docu_linear2);
            docu_linear3 = findViewById(R.id.docu_linear3);
            docu_linear4 = findViewById(R.id.docu_linear4);
            docu_linear22 = findViewById(R.id.docu_linear22);
            docu_linear33 = findViewById(R.id.docu_linear33);
            docu_linear44 = findViewById(R.id.docu_linear44);
            rera_edit = findViewById(R.id.rera_registration);
            pan_edit = findViewById(R.id.pan_no);
            docu_heading = findViewById(R.id.btn_no);
            docu_title.setText("Submit Documents");
            docu_skip.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextWatcher() {
        try {
            pan_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        check1.setVisibility(View.VISIBLE);
                    } else {
                        check1.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        try {
            docu_btn1.setOnClickListener(this);
            docu_btn2.setOnClickListener(this);
            docu_btn3.setOnClickListener(this);
            docu_btn4.setOnClickListener(this);
            continue_btn.setOnClickListener(this);
            document_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DocumentUploadActivity.this, SignUpActivity.class));
                    finish();
                }
            });
            docu_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DocumentUploadActivity.this, VenueActivity.class));
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == Gallery_CODE) {
                    onSelectFromGalleryResult(data);

                } else if (requestCode == Camera_CODE) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        onCaptureImageResult(uri);
                    } else {
                        uri = Uri.fromFile(image_file);
                        onCaptureImageResult(uri);
                    }
                }
                switch (i) {
                    case 2:
                        docu_linear22.setVisibility(View.VISIBLE);
                        docu_linear2.setVisibility(View.GONE);
                        image_code2 = 1;
                        break;
                    case 3:
                        docu_linear33.setVisibility(View.VISIBLE);
                        docu_linear3.setVisibility(View.GONE);
                        image_code3 = 1;
                        break;
                    case 4:
                        docu_linear44.setVisibility(View.VISIBLE);
                        docu_linear4.setVisibility(View.GONE);
                        image_code4 = 1;
                        break;
                }

            } else if (resultCode == RESULT_CANCELED) {
                Utils.setSnackBar(parentLayout, "Picture selection was canceled");
            } else {
                Utils.setSnackBar(parentLayout, "Picture was not taken");
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
                compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
                Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);
            }
            prepareFilePart(uri);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void prepareFilePart(Uri fileUri) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                compressedImagePath = fileUri.getPath();
            }
            if (fileUri != null && fileUri.getPath().length() > 0 && compressedImagePath != null && compressedImagePath.length() > 0) {
                File imageFile = new File(compressedImagePath);
                setFile(imageFile);
                // create RequestBody instance from file

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.continue_btn:
                    uploadAll();
                    break;
                case R.id.docu_btn1:
                    i = 1;
                    setView(i);
                    break;
                case R.id.docu_btn2:
                    i = 2;
                    fileNumber = 0;
                    setView(i);
                    docu_imageview = findViewById(R.id.layout22).findViewById(R.id.docu_imageview);
                    recapture_text = findViewById(R.id.layout22).findViewById(R.id.recapture_text);
                    remove_text = findViewById(R.id.layout22).findViewById(R.id.remove_text);
                    gallery_btn = findViewById(R.id.layout2).findViewById(R.id.docu_gallery);
                    camera_btn = findViewById(R.id.layout2).findViewById(R.id.docu_camera);
                    gallery_btn.setOnClickListener(this);
                    camera_btn.setOnClickListener(this);
                    remove_text.setOnClickListener(this);
                    recapture_text.setOnClickListener(this);
                    break;
                case R.id.docu_btn3:
                    i = 3;
                    fileNumber = 1;
                    setView(i);
                    docu_imageview = findViewById(R.id.layout33).findViewById(R.id.docu_imageview);
                    recapture_text = findViewById(R.id.layout33).findViewById(R.id.recapture_text);
                    remove_text = findViewById(R.id.layout33).findViewById(R.id.remove_text);
                    gallery_btn = findViewById(R.id.layout3).findViewById(R.id.docu_gallery);
                    camera_btn = findViewById(R.id.layout3).findViewById(R.id.docu_camera);
                    gallery_btn.setOnClickListener(this);
                    camera_btn.setOnClickListener(this);
                    remove_text.setOnClickListener(this);
                    recapture_text.setOnClickListener(this);
                    break;
                case R.id.docu_btn4:
                    i = 4;
                    fileNumber = 2;
                    setView(i);
                    docu_imageview = findViewById(R.id.layout44).findViewById(R.id.docu_imageview);
                    recapture_text = findViewById(R.id.layout44).findViewById(R.id.recapture_text);
                    remove_text = findViewById(R.id.layout44).findViewById(R.id.remove_text);
                    gallery_btn = findViewById(R.id.layout4).findViewById(R.id.docu_gallery);
                    camera_btn = findViewById(R.id.layout4).findViewById(R.id.docu_camera);
                    gallery_btn.setOnClickListener(this);
                    camera_btn.setOnClickListener(this);
                    remove_text.setOnClickListener(this);
                    recapture_text.setOnClickListener(this);
                    break;
                case R.id.docu_gallery:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(DocumentUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(DocumentUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                        } else {
                            openGallery();
                        }
                    } else {
                        openGallery();
                    }
                    //Toast.makeText(context,"you clicked gallery button : "+i,Toast.LENGTH_SHORT).show();
                    break;
                case R.id.docu_camera:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkCameraAndWritablePermission()) {
                            openCamera();
                        } else {
                            requestCameraAndWritablePermission();
                        }
                    } else {
                        openCamera();
                    }
                    //Toast.makeText(context,"you clicked camera button : "+i,Toast.LENGTH_SHORT).show();
                    break;
                case R.id.remove_text:
                    docu_imageview.setImageResource(android.R.color.transparent);
                    switch (i) {
                        case 2:
                            docu_linear22.setVisibility(View.GONE);
                            docu_linear2.setVisibility(View.VISIBLE);
                            check2.setVisibility(View.GONE);
                            image_code2 = 0;
                            reraCertificate = null;
                            break;
                        case 3:
                            docu_linear33.setVisibility(View.GONE);
                            docu_linear3.setVisibility(View.VISIBLE);
                            check3.setVisibility(View.GONE);
                            image_code3 = 0;
                            IDProof = null;
                            break;
                        case 4:
                            docu_linear44.setVisibility(View.GONE);
                            docu_linear4.setVisibility(View.VISIBLE);
                            check4.setVisibility(View.GONE);
                            image_code4 = 0;
                            addressProof = null;
                            break;
                    }
                    break;
                case R.id.recapture_text:
                    switch (i) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadAll() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                RequestBody mobileNo = RequestBody.create(MediaType.parse("multipart/form-data"), pref.getString(AppConstants.MOBILE_NUMBER, ""));
                    String panNo = pan_edit.getText().toString();
                    String reraRegistration = rera_edit.getText().toString();
                    if(!panNo.isEmpty() && addressProof != null && IDProof != null) {
                        RequestBody panCardNumber = RequestBody.create(MediaType.parse("multipart/form-data"), panNo);
                        RequestBody reraRegistrationNumber = RequestBody.create(MediaType.parse("multipart/form-data"), reraRegistration);
                        RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPITask(RetrofitBuilders.getBaseUrl());
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
                                                Utils.setSnackBar(parentLayout, message);
                                                //startActivity(new Intent(DocumentUploadActivity.this, VenueActivity.class));
                                                Intent intent = new Intent(DocumentUploadActivity.this,VerificationActivity.class);
                                                if(bundle != null) {
                                                    intent.putExtras(bundle);
                                                }
                                                startActivity(intent);
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
                                            Utils.setSnackBar(parentLayout, message);
                                        } catch (JSONException | IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Utils.LoaderUtils.dismissLoader();
                                Utils.showToast(context, t.getMessage().toString(), "Failure");
                            }
                        });
                    }else{
                            showErrorMessage(panNo);
                    }
            } else {
                if(!isFinishing()) {
                    Utils.internetDialog(context, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uri = getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                image_file = ImageUtils.CreateNewFileForPicture();
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", image_file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                startActivityForResult(intent, Camera_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), Gallery_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        File mediaFile = null;
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Brongo/document");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            String mImageName = "BR_" + timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

            System.out.println("Media File Name for New Post : " + mediaFile);
            return mediaFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaFile;
    }

    private void onCaptureImageResult(Uri uri) {

        try {
            Glide.with(context)
                    .load(new File(uri.getPath()))
                    .into(docu_imageview);
            compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
            Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);

        switch (i) {
            case 2:
                check2.setVisibility(View.VISIBLE);
                break;
            case 3:
                check3.setVisibility(View.VISIBLE);
                break;
            case 4:
                check4.setVisibility(View.VISIBLE);
                break;
        }

        prepareFilePart(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadGlide(Intent data) {
        try {
            if (data != null) {
                Glide.with(context)
                        .load(new File(uri.getPath()))
                        .into(docu_imageview);

                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
                    compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
                    Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);
                }
            }
        switch (i) {
            case 2:
                check2.setVisibility(View.VISIBLE);
                break;
            case 3:
                check3.setVisibility(View.VISIBLE);
                break;
            case 4:
                check4.setVisibility(View.VISIBLE);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView(int i) {
        try {
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
            switch (i) {
                case 1:
                    open_view2 = 0;
                    open_view3 = 0;
                    open_view4 = 0;
                    if (open_view1 == 0) {
                        docu_linear1.setVisibility(View.VISIBLE);
                        docu_heading.setText("Step " + i + " of 4");
                        docu_btn1.setTextColor(getResources().getColor(R.color.white));
                        docu_btn1.setBackgroundResource(R.drawable.docu_button);
                        round_text1.setTextColor(getResources().getColor(R.color.appColor));
                        round_text1.setBackgroundResource(R.drawable.text_circular_white);
                        open_view1 = 1;
                    } else if (open_view1 == 1) {
                        open_view1 = 0;
                    }
                    break;
                case 2:
                    open_view1 = 0;
                    open_view3 = 0;
                    open_view4 = 0;
                    if (open_view2 == 0) {
                        if (image_code2 == 0) {
                            docu_linear2.setVisibility(View.VISIBLE);
                        } else if (image_code2 == 1) {
                            docu_linear22.setVisibility(View.VISIBLE);
                        }
                        docu_heading.setText("Step " + i + " of 4");
                        docu_btn2.setTextColor(getResources().getColor(R.color.white));
                        docu_btn2.setBackgroundResource(R.drawable.docu_button);
                        round_text2.setTextColor(getResources().getColor(R.color.appColor));
                        round_text2.setBackgroundResource(R.drawable.text_circular_white);
                        open_view2 = 1;
                    } else if (open_view2 == 1) {
                        open_view2 = 0;
                    }
                    break;
                case 3:
                    open_view2 = 0;
                    open_view1 = 0;
                    open_view4 = 0;
                    if (open_view3 == 0) {
                        if (image_code3 == 0) {
                            docu_linear3.setVisibility(View.VISIBLE);
                        } else if (image_code3 == 1) {
                            docu_linear33.setVisibility(View.VISIBLE);
                        }
                        docu_heading.setText("Step " + i + " of 4");
                        docu_btn3.setTextColor(getResources().getColor(R.color.white));
                        docu_btn3.setBackgroundResource(R.drawable.docu_button);
                        round_text3.setTextColor(getResources().getColor(R.color.appColor));
                        round_text3.setBackgroundResource(R.drawable.text_circular_white);
                        open_view3 = 1;
                    } else if (open_view3 == 1) {
                        open_view3 = 0;
                    }
                    break;
                case 4:
                    open_view2 = 0;
                    open_view3 = 0;
                    open_view1 = 0;
                    if (open_view4 == 0) {
                        if (image_code4 == 0) {
                            docu_linear4.setVisibility(View.VISIBLE);
                        } else if (image_code4 == 1) {
                            docu_linear44.setVisibility(View.VISIBLE);
                        }
                        docu_btn4.setTextColor(getResources().getColor(R.color.white));
                        docu_heading.setText("Step " + i + " of 4");
                        docu_btn4.setBackgroundResource(R.drawable.docu_button);
                        round_text4.setTextColor(getResources().getColor(R.color.appColor));
                        round_text4.setBackgroundResource(R.drawable.text_circular_white);
                        open_view4 = 1;
                    } else if (open_view4 == 1) {
                        open_view4 = 0;
                    }
                    break;
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Utils.permissionDialog(context);
                    } else {
                        Utils.setSnackBar(parentLayout, "Permission Denied");
                    }
                }
                break;
            case REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS:
                if (permissions.length > 1) {
                    if (grantResults.length > 0) {
                        boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean WritablePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        if (CameraPermission && WritablePermission) {
                            openCamera();
                        } else {
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Utils.permissionDialog(context);
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
                                openCamera();
                            } else {
                                if (!shouldShowRequestPermissionRationale(permissions[0])) {
                                    Utils.permissionDialog(context);
                                } else {
                                    Utils.setSnackBar(parentLayout, "Permission Denied");
                                }
                            }
                        }

                    }
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        try {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            } else {
                Utils.setSnackBar(parentLayout, "click again to back");
            }

            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        uploadAll();
    }

    private boolean checkCameraAndWritablePermission() {
            int permissionCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            int permissionWritableExternal = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            listPermissionsNeeded = new ArrayList<>();
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (permissionWritableExternal != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        return listPermissionsNeeded.isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraAndWritablePermission() {
        requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS);
    }

    private void setFile(File imageFile) {
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            filename = imageFile.getName();
            // MultipartBody.Part is used to send also the actual file name
            switch (fileNumber) {
                case 0:
                    reraCertificate = MultipartBody.Part.createFormData("reraCertificate", imageFile.getName(), requestFile);
                    filename1 = filename;
                    break;
                case 1:
                    IDProof = MultipartBody.Part.createFormData("IDProof", imageFile.getName(), requestFile);
                    filename2 = filename;
                    break;
                case 2:
                    addressProof = MultipartBody.Part.createFormData("addressProof", imageFile.getName(), requestFile);
                    filename3 = filename;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showErrorMessage(String panNo){
        String message = "Data should not be empty";
        try {
            if(panNo != null && panNo.isEmpty()){
                message = "Pan Number should not be empty";
            }else if(addressProof == null || IDProof == null) {
                message = "Document should not be empty";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.setSnackBar(parentLayout,message);
    }
}