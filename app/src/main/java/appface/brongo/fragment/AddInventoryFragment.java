package appface.brongo.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import appface.brongo.R;
import appface.brongo.activity.AutoFillActivity;
import appface.brongo.activity.DocumentUploadActivity;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.MapActivity;
import appface.brongo.activity.Menu_Activity;
import appface.brongo.other.AllUtils;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AddInventoryFragment extends Fragment implements NoInternetTryConnectListener{
    private Context context;
    private static final String TAG =AddInventoryFragment.class.getName() ;
    private MaterialBetterSpinner client_spinner,prop_type_spinner,prop_status_spinner,bhk_spinner,sub_property_spinner ;
    private ArrayAdapter<String> clientAdapter,prop_typeAdpter,bhkAdapter,propStatusAdapter,subprop_typeAdpter;
    private EditText inventory_budget,inventory_client_name,inventory_mobile,inventory_email,inventory_notes;
    public static EditText inventory_location;
    private TextView inventory_addImage,inventory_add_more,imageName1,imageSize1,imageName2,imageSize2,imageName3,imageSize3,toolbar_title;
    private LinearLayout linearImage1,linearImage2,linearImage3;
    private Button relativeRemove1,relativeRemove2,relativeRemove3;
    private RelativeLayout relativeUpload;
    private final int STORAGE_PERMISSION_REQUEST = 100;
    private ArrayList<File> fileList;
    private ImageView inventory_toolbar_delete,inventory_toolbar_edit;
    private String[] inventory_clientlist = {"RENT","BUY","SELL","RENT_OUT"};
    private String[] inventory_proplist = {"Residential","Commercial"};
    private int Gallery_CODE = 30,i=0;
    private String emailPattern;
    private Button save_inventory,cancel_inventory;
    private String filename,filename1,filename2,filename3,compressedImagePath;
    private String client1,prop_type,bedroomType,prop_status,sub_prop_type,propertyId1;
   private RequestBody requestFile1,requestFile2,requestFile3;
    private SharedPreferences pref;
    private TextInputLayout invent_email_layout,invent_phone_layout;
   // private ProgressDialog pd;
   private long length1,length2,length3;
    private Uri uri;
    private String image2,image3,clientName1,clientMobileNo1,emailId1,note1,image1,edit_inventory,invent_budget;
    public static String microMarketName1,microMarketCity1,microMarketState1;
    private MultipartBody.Part propertyImage1,propertyImage2,propertyImage3;
    private String[] inventory_bhklist = {"1 BHK","2 BHK","3 BHK","4 BHK","4 BHK+"};
    private String[] subpropList = {""};
    TextInputLayout textInputLayout;
    private String[] resi_prop_type_list = {"Apartment/Flat", "Villa", "PG/Hostel", "Independent House/Pent House"};
 private String[] comm_prop_type_list = {"Office Space","Showroom/Retail space","Food & Beverage","Any"};
    private String[] inventory_propstatuslist = {"Ready to move-in(new)", "Ready to move-in(old construction)", "Under Construction", "Pre Launch"};

    public AddInventoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client1=prop_type=bedroomType=prop_status=sub_prop_type=propertyId1=image2=image3=clientName1=clientMobileNo1=emailId1=note1=image1=edit_inventory=invent_budget=microMarketCity1=microMarketState1=microMarketName1="";
        if (getArguments() != null) {
            image2 = getArguments().getString("propertyImage2","");
            image3 = getArguments().getString("propertyImage3","");
            propertyId1 = getArguments().getString("propertyId","");
            client1 = getArguments().getString("postingType","");
            microMarketName1 = getArguments().getString("microMarketName","");
            microMarketCity1 = getArguments().getString("microMarketCity","");
            microMarketState1 = getArguments().getString("microMarketState","");
            prop_type = getArguments().getString("propertyType","");
            prop_status = getArguments().getString("propertyStatus","");
            clientName1 = getArguments().getString("clientName","");
            clientMobileNo1 = getArguments().getString("clientMobileNo","");
            emailId1 = getArguments().getString("emailId","");
            note1 = getArguments().getString("note","");
            image1 = getArguments().getString("propertyImage1","");
            bedroomType = getArguments().getString("bedRoomType","");
            invent_budget = getArguments().getLong("budget")+"";
            edit_inventory = getArguments().getString("edit_inventory","");
            sub_prop_type = getArguments().getString("subPropertyType","");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_inventory, container, false);
        try {
            initialise(view);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setAdapter();
        setListener();
        return view;
    }
   /* private void getHint(){
        SpannableStringBuilder sb = new SpannableStringBuilder("Email ID (Optional)");
        CharacterStyle cs= new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        sb.setSpan(cs, 0, 8, 0);
        textInputLayout.setHint(sb);
    }*/
    private void initialise(View view) throws MalformedURLException {
        context = getActivity();
        pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
         emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        fileList = new ArrayList<>();
        invent_email_layout = (TextInputLayout)view.findViewById(R.id.input_layout_inventory_email);
        invent_phone_layout = (TextInputLayout)view.findViewById(R.id.input_layout_inventory_mobile);
        inventory_budget = (EditText)view.findViewById(R.id.inventory_budget);
        inventory_location = (EditText)view.findViewById(R.id.inventory_location);
        inventory_client_name = (EditText)view.findViewById(R.id.inventory_client_name);
        inventory_mobile = (EditText)view.findViewById(R.id.inventory_mobile);
        inventory_email = (EditText)view.findViewById(R.id.inventory_email);
    /*    textInputLayout = (TextInputLayout)view.findViewById(R.id.input_layout_inventory_email);
        getHint();*/
        inventory_notes = (EditText)view.findViewById(R.id.inventory_add_notes);
        inventory_addImage = (TextView)view.findViewById(R.id.inventory_add_image);
        relativeUpload = (RelativeLayout) view.findViewById(R.id.relative_upload);
        client_spinner = (MaterialBetterSpinner)view.findViewById(R.id.inventory_spinner_client);
        sub_property_spinner = (MaterialBetterSpinner)view.findViewById(R.id.inventory_spinner_subproptype);
        prop_type_spinner = (MaterialBetterSpinner)view.findViewById(R.id.inventory_spinner_proptype);
        bhk_spinner = (MaterialBetterSpinner)view.findViewById(R.id.inventory_spinner_bhk);
        prop_status_spinner = (MaterialBetterSpinner)view.findViewById(R.id.inventory_spinner_propstatus);
        inventory_add_more = (TextView)view.findViewById(R.id.inventory_add_more);
        linearImage1 = (LinearLayout)view.findViewById(R.id.inventory_linearimage1);
        linearImage2 = (LinearLayout)view.findViewById(R.id.inventory_linearimage2);
        linearImage3 = (LinearLayout)view.findViewById(R.id.inventory_linearimage3);
        imageName1 = (TextView)view.findViewById(R.id.inventory_image_name1);
        imageName2 = (TextView)view.findViewById(R.id.inventory_image_name2);
        imageName3 = (TextView)view.findViewById(R.id.inventory_image_name3);
        imageSize1 = (TextView)view.findViewById(R.id.inventory_image_size1);
        imageSize2 = (TextView)view.findViewById(R.id.inventory_image_size2);
        imageSize3 = (TextView)view.findViewById(R.id.inventory_image_size3);
        inventory_toolbar_delete = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        inventory_toolbar_edit = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        inventory_toolbar_delete.setVisibility(View.GONE);
        inventory_toolbar_edit.setVisibility(View.GONE);
        relativeRemove1 = (Button)view.findViewById(R.id.inventory_remove1);
        relativeRemove2 = (Button)view.findViewById(R.id.inventory_remove2);
        relativeRemove3 = (Button)view.findViewById(R.id.inventory_remove3);
        save_inventory = (Button)view.findViewById(R.id.inventory_save_btn);
        cancel_inventory = (Button)view.findViewById(R.id.inventory_cancel_btn);
      /*  pd = new ProgressDialog(context, R.style.MyDialogTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);*/
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar_title.setText("Add Inventory");
        if(edit_inventory != null) {
            if (edit_inventory.equalsIgnoreCase("edit_inventory")) {
                enterValue();
                toolbar_title.setText("Edit Inventory");
                i = 1;
            }
        }
        setTextWatcher();
    }
    private void setAdapter(){
         clientAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, inventory_clientlist);
        client_spinner.setAdapter(clientAdapter);
        prop_typeAdpter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, inventory_proplist);
        prop_type_spinner.setAdapter(prop_typeAdpter);
        subprop_typeAdpter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, subpropList);
        sub_property_spinner.setAdapter(subprop_typeAdpter);
        propStatusAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, inventory_propstatuslist);
        prop_status_spinner.setAdapter(propStatusAdapter);
        bhkAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, inventory_bhklist);
        bhk_spinner.setAdapter(bhkAdapter);
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), Gallery_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // do somthing...
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Gallery_CODE) {
                onSelectFromGalleryResult(data);
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Utils.showToast(context, " Picture selection was canceled");
        } else {
            Utils.showToast(context, " Picture was not taken ");
        }

    }
    private void onSelectFromGalleryResult(Intent data) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(context, selectedImageUri);
                Log.i("Image File Path", "" + selectedImagePath);
                uri = Uri.fromFile(new File(selectedImagePath));
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            uri = Uri.fromFile(new File(imagePath));
            cursor.close();
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
            fileList.add(imageFile);
            setFile();

            // create RequestBody instance from file
           /* RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            filename = imageFile.getName();
            long length = imageFile.length();
            length = length/1024;*/
            // MultipartBody.Part is used to send also the actual file name
        }
    }
    private void setListener(){
        save_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setInventory();
            }
        });
        cancel_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        relativeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                    } else {
                        openGallery();
                    }
                } else {
                    openGallery();
                }
            }
        });
        inventory_add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                    } else {
                        openGallery();
                    }
                } else {
                    openGallery();
                }
            }
        });
        relativeRemove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fileList.remove(0);
                setFile();
            }
        });
        relativeRemove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileList.remove(1);
                setFile();
            }
        });
        relativeRemove3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileList.remove(2);
                setFile();
            }
        });
        prop_status_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prop_status = parent.getItemAtPosition(position).toString();
            }
        });
        bhk_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bedroomType = parent.getItemAtPosition(position).toString();
            }
        });
        client_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client1 = parent.getItemAtPosition(position).toString();
            }
        });
        prop_type_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                prop_type = parent.getItemAtPosition(position).toString();
                if(prop_type.equalsIgnoreCase("Residential")){
                    subprop_typeAdpter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_dropdown_item_1line, resi_prop_type_list);
                    sub_property_spinner.setAdapter(subprop_typeAdpter);
                }else if(prop_type.equalsIgnoreCase("Commercial")){
                    subprop_typeAdpter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_dropdown_item_1line, comm_prop_type_list);
                    sub_property_spinner.setAdapter(subprop_typeAdpter);
                }
            }
        });
        sub_property_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sub_prop_type = parent.getItemAtPosition(position).toString();
            }
        });
        inventory_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoPlace();
            }
        });
    }
    private void setFile(){
        switch (fileList.size()){
            case 0:
                linearImage1.setVisibility(View.GONE);
                linearImage2.setVisibility(View.GONE);
                linearImage3.setVisibility(View.GONE);
                relativeUpload.setVisibility(View.VISIBLE);
                inventory_add_more.setVisibility(View.GONE);
                break;
            case 1:
                linearImage1.setVisibility(View.VISIBLE);
                linearImage2.setVisibility(View.GONE);
                linearImage3.setVisibility(View.GONE);
                inventory_add_more.setVisibility(View.VISIBLE);
                relativeUpload.setVisibility(View.GONE);
                requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(0));
                filename1 = fileList.get(0).getName();
                length1 = fileList.get(0).length();
                length1 = length1/1024;
                imageName1.setText(filename1);
                imageSize1.setText(length1+"KB");
                break;
            case 2:
                linearImage1.setVisibility(View.VISIBLE);
                linearImage2.setVisibility(View.VISIBLE);
                linearImage3.setVisibility(View.GONE);
                inventory_add_more.setVisibility(View.VISIBLE);
                relativeUpload.setVisibility(View.GONE);
                requestFile1= RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(0));
                filename1 = fileList.get(0).getName();
                length1 = fileList.get(0).length();
                length1 = length1/1024;
                imageName1.setText(filename1);
                imageSize1.setText(length1+"KB");
                requestFile2= RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(1));
                filename2 = fileList.get(1).getName();
                length2 = fileList.get(1).length();
                length2 = length2/1024;
                imageName2.setText(filename2);
                imageSize2.setText(length2+"KB");
                break;
            case 3:
                linearImage1.setVisibility(View.VISIBLE);
                linearImage2.setVisibility(View.VISIBLE);
                linearImage3.setVisibility(View.VISIBLE);
                inventory_add_more.setVisibility(View.GONE);
                relativeUpload.setVisibility(View.GONE);
                requestFile1= RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(0));
                filename1 = fileList.get(0).getName();
                length1 = fileList.get(0).length();
                length1 = length1/1024;
                imageName1.setText(filename1);
                imageSize1.setText(length1+"KB");
                requestFile2= RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(1));
                filename2 = fileList.get(1).getName();
                length2 = fileList.get(1).length();
                length2 = length2/1024;
                imageName2.setText(filename2);
                imageSize2.setText(length2+"KB");
                requestFile3= RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(2));
                filename3 = fileList.get(2).getName();
                length3 = fileList.get(2).length();
                length3 = length3/1024;
                imageName3.setText(filename3);
                imageSize3.setText(length3+"KB");
                break;
        }
    }
    private void setInventory(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            switch (fileList.size()) {
                case 1:
                    propertyImage1 = MultipartBody.Part.createFormData("propertyImage1", filename1, requestFile1);
                    break;
                case 2:
                    propertyImage1 = MultipartBody.Part.createFormData("propertyImage1", filename1, requestFile1);
                    propertyImage2 = MultipartBody.Part.createFormData("propertyImage2", filename2, requestFile2);
                    break;
                case 3:
                    propertyImage1 = MultipartBody.Part.createFormData("propertyImage1", filename1, requestFile1);
                    propertyImage2 = MultipartBody.Part.createFormData("propertyImage2", filename2, requestFile2);
                    propertyImage3 = MultipartBody.Part.createFormData("propertyImage3", filename3, requestFile3);
                    break;
            }
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            RequestBody brokerMobileNo = RequestBody.create(MediaType.parse("multipart/form-data"), pref.getString(AppConstants.MOBILE_NUMBER, ""));
            RequestBody client = RequestBody.create(MediaType.parse("multipart/form-data"), client1);
            RequestBody propertyId = RequestBody.create(MediaType.parse("multipart/form-data"), propertyId1);
            RequestBody propertyType = RequestBody.create(MediaType.parse("multipart/form-data"), prop_type);
            RequestBody bedRoomType = RequestBody.create(MediaType.parse("multipart/form-data"), bedroomType);
            RequestBody budget = RequestBody.create(MediaType.parse("multipart/form-data"), invent_budget);
            RequestBody propertyStatus = RequestBody.create(MediaType.parse("multipart/form-data"), prop_status);
            RequestBody clientName = RequestBody.create(MediaType.parse("multipart/form-data"), clientName1);
            RequestBody clientMobileNo = RequestBody.create(MediaType.parse("multipart/form-data"), clientMobileNo1);
            RequestBody emailId = RequestBody.create(MediaType.parse("multipart/form-data"), emailId1);
            RequestBody note = RequestBody.create(MediaType.parse("multipart/form-data"), note1);
            RequestBody microMarketName = RequestBody.create(MediaType.parse("multipart/form-data"), microMarketName1);
            RequestBody microMarketCity = RequestBody.create(MediaType.parse("multipart/form-data"), microMarketCity1);
            RequestBody microMarketState = RequestBody.create(MediaType.parse("multipart/form-data"), microMarketState1);
            RequestBody commission = RequestBody.create(MediaType.parse("multipart/form-data"), "5000");
            RequestBody subPropertyType = RequestBody.create(MediaType.parse("multipart/form-data"), sub_prop_type);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call call = retrofitAPIs.AddInventApi(tokenaccess, "android", deviceId, propertyId, brokerMobileNo, client, propertyType, bedRoomType, budget, propertyStatus, clientName, clientMobileNo, emailId, note, propertyImage1, propertyImage2, propertyImage3, microMarketName, microMarketCity, microMarketState, commission, subPropertyType);
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
                                if (statusCode == 200) {
                                    Utils.showToast(context, message);
                                    startActivity(new Intent(context, MainActivity.class));
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
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    setInventory();
                                } else {
                                    Utils.showToast(context, message);
                                }
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
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void enterValue(){
        inventory_budget.setText(invent_budget);
        inventory_location.setText(microMarketName1);
        inventory_client_name.setText(clientName1);
        inventory_email.setText(emailId1);
        inventory_mobile.setText(clientMobileNo1);
        inventory_notes.setText(note1);
        client_spinner.setText(client1);
        prop_type_spinner.setText(prop_type);
        bhk_spinner.setText(bedroomType);
        prop_status_spinner.setText(prop_status);
        if(prop_type.equalsIgnoreCase("Residential")){
            subprop_typeAdpter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, resi_prop_type_list);
            sub_property_spinner.setAdapter(subprop_typeAdpter);
        }else if(prop_type.equalsIgnoreCase("Commercial")){
            subprop_typeAdpter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, comm_prop_type_list);
            sub_property_spinner.setAdapter(subprop_typeAdpter);
        }
        if(!sub_prop_type.equalsIgnoreCase("")) {
            sub_property_spinner.setText(sub_prop_type);
        }
        if(! image1.equalsIgnoreCase("")){
            filename1 = image1.substring(image1.lastIndexOf('/') + 1);
        }
        if(! image2.equalsIgnoreCase("")){
            filename2 = image1.substring(image2.lastIndexOf('/') + 1);
        }
        if(! image3.equalsIgnoreCase("")){
            filename3 = image3.substring(image3.lastIndexOf('/') + 1);
        }
    }
    private void autoPlace(){
        Intent intent = new Intent(context,AutoFillActivity.class);
        intent.putExtra("sender_intent","AddInventory");
        startActivity(intent);
    }

    private void setTextWatcher(){
        inventory_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = inventory_email.getText().toString().trim();
                if (email.matches(emailPattern) && s.length() > 0){
                    invent_email_layout.setError("");
                    invent_email_layout.setErrorEnabled(false);
                    emailId1 = email;
                }else if(s.length()>0){
                    invent_email_layout.setErrorEnabled(true);
                    invent_email_layout.setError("Invalid email id");
                }else if(s.length() == 0){
                    invent_email_layout.setError("");
                    invent_email_layout.setErrorEnabled(false);
                }
            }
        });
        inventory_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==10){
                    Utils.hideKeyboard(context,inventory_mobile);
                    clientMobileNo1 = inventory_mobile.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = inventory_mobile.getText().toString().trim();
                if ((phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && (phone.length() == 10)){
                    invent_phone_layout.setError("");
                    invent_phone_layout.setErrorEnabled(false);
                    clientMobileNo1 = phone;
                }else if(phone.length() == 0) {
                    clientMobileNo1 = "";
                    invent_phone_layout.setError("");
                    invent_phone_layout.setErrorEnabled(false);
                }else
                {
                    invent_phone_layout.setError("Invalid Mobile number");
                }
            }
        });
        inventory_notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note1 = inventory_notes.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inventory_client_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clientName1 = inventory_client_name.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        inventory_budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                invent_budget = inventory_budget.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }else{
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Utils.permissionDialog(context);
                    }
                }
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        setInventory();
    }
}

