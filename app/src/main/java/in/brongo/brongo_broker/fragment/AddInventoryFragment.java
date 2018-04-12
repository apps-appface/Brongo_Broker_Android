package in.brongo.brongo_broker.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.MainActivity;
import in.brongo.brongo_broker.model.SignUpModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AddInventoryFragment extends Fragment implements NoInternetTryConnectListener, NoTokenTryListener, AllUtils.test {
    private Context context;
    private ArrayAdapter<String> marketAdapter,adapter;;
    private ArrayList<String> poc_list;
    private File image_file;
    private int apiCode = 0;
    private List<String> listPermissionsNeeded;
    public static final int REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS = 111;
    private static final int REQUEST_CAMERA = 200;
    private static final int SELECT_FILE = 201;
    private ArrayList<SignUpModel.MarketObject> marketlist;
    private static final String TAG = AddInventoryFragment.class.getName();
    private MaterialBetterSpinner client_spinner, prop_type_spinner, prop_status_spinner, bhk_spinner, sub_property_spinner, marketSpinner;
    private ArrayAdapter<String> clientAdapter, prop_typeAdpter, bhkAdapter, propStatusAdapter, subprop_typeAdpter;
    private EditText inventory_budget, inventory_client_name, inventory_mobile, inventory_email, inventory_notes;
    public static EditText inventory_location;
    private Toolbar toolbar;
    private TextView inventory_addImage, inventory_add_more, imageName1, imageSize1, imageName2, imageSize2, imageName3, imageSize3, toolbar_title;
    private LinearLayout linearImage1, linearImage2, linearImage3;
    private Button relativeRemove1, relativeRemove2, relativeRemove3;
    private RelativeLayout relativeUpload, image_relative, parentLayout;
    private ArrayList<File> fileList;
    private ImageView inventory_toolbar_delete, inventory_toolbar_edit, add_icon;
    private String[] inventory_clientlist = {"RENT", "BUY", "SELL", "RENT_OUT"};
    private String[] inventory_proplist = {"Residential", "Commercial"};
    private int taskCompleted, i = 0;
    private String emailPattern;
    private Button save_inventory, cancel_inventory;
    private String filename, filename1, filename2, filename3, compressedImagePath;
    private String client1, prop_type, bedroomType, prop_status, sub_prop_type, propertyId1;
    private RequestBody requestFile1, requestFile2, requestFile3;
    private SharedPreferences pref;
    private TextInputLayout invent_email_layout, invent_phone_layout;
    // private ProgressDialog pd;
    private long length1, length2, length3;
    private Uri uri;
    private String image2, image3, clientName1, clientMobileNo1, emailId1, note1, image1, edit_inventory, invent_budget;
    public static String microMarketName1, microMarketCity1, microMarketState1;
    private MultipartBody.Part propertyImage1, propertyImage2, propertyImage3;
    private String[] inventory_bhklist = {"1 BHK", "2 BHK", "3 BHK", "4 BHK", "4 BHK+"};
    private String[] subpropList = {""};
    TextInputLayout textInputLayout;
    private String[] resi_prop_type_list = {"Apartment/Flat", "Villa", "PG/Hostel", "Independent House/Pent House","Row House"};
    private String[] comm_prop_type_list = {"Office Space", "Showroom/Retail space", "Food & Beverage", "Any"};
    private String[] inventory_propstatuslist = {"Ready to move-in(new)", "Ready to move-in(old construction)", "Under Construction", "Pre Launch"};

    public AddInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client1 = prop_type = bedroomType = prop_status = sub_prop_type = propertyId1 = image2 = image3 = clientName1 = clientMobileNo1 = emailId1 = note1 = image1 = edit_inventory = invent_budget = microMarketCity1 = microMarketState1 = microMarketName1 = "";
        if (getArguments() != null) {
            image2 = getArguments().getString("propertyImage2", "");
            image3 = getArguments().getString("propertyImage3", "");
            propertyId1 = getArguments().getString("propertyId", "");
            client1 = getArguments().getString("postingType", "");
            microMarketName1 = getArguments().getString("microMarketName", "");
            microMarketCity1 = getArguments().getString("microMarketCity", "");
            microMarketState1 = getArguments().getString("microMarketState", "");
            prop_type = getArguments().getString("propertyType", "");
            prop_status = getArguments().getString("propertyStatus", "");
            clientName1 = getArguments().getString("clientName", "");
            clientMobileNo1 = getArguments().getString("clientMobileNo", "");
            emailId1 = getArguments().getString("emailId", "");
            note1 = getArguments().getString("note", "");
            image1 = getArguments().getString("propertyImage1", "");
            bedroomType = getArguments().getString("bedRoomType", "");
            invent_budget = getArguments().getLong("budget") + "";
            edit_inventory = getArguments().getString("edit_inventory", "");
            sub_prop_type = getArguments().getString("subPropertyType", "");
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
        try {
            context = getActivity();
            pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME, 0);
            poc_list = new ArrayList<>();
            marketSpinner = view.findViewById(R.id.inventory_spinner_location);
            client_spinner = view.findViewById(R.id.inventory_spinner_client);
            fetchMicromarket();
            emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            fileList = new ArrayList<>();
            marketlist = new ArrayList<>();
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            invent_email_layout = view.findViewById(R.id.input_layout_inventory_email);
            invent_phone_layout = view.findViewById(R.id.input_layout_inventory_mobile);
            inventory_budget = view.findViewById(R.id.inventory_budget);
            inventory_location = view.findViewById(R.id.inventory_location);
            inventory_client_name = view.findViewById(R.id.inventory_client_name);
            inventory_mobile = view.findViewById(R.id.inventory_mobile);
            inventory_email = view.findViewById(R.id.inventory_email);
    /*    textInputLayout = (TextInputLayout)view.findViewById(R.id.input_layout_inventory_email);
        getHint();*/
            inventory_notes = view.findViewById(R.id.inventory_add_notes);
            inventory_addImage = view.findViewById(R.id.inventory_add_image);
            relativeUpload = view.findViewById(R.id.relative_upload);
            sub_property_spinner = view.findViewById(R.id.inventory_spinner_subproptype);
            prop_type_spinner = view.findViewById(R.id.inventory_spinner_proptype);
            bhk_spinner = view.findViewById(R.id.inventory_spinner_bhk);
            prop_status_spinner = view.findViewById(R.id.inventory_spinner_propstatus);
            inventory_add_more = view.findViewById(R.id.inventory_add_more);
            linearImage1 = view.findViewById(R.id.inventory_linearimage1);
            linearImage2 = view.findViewById(R.id.inventory_linearimage2);
            linearImage3 = view.findViewById(R.id.inventory_linearimage3);
            imageName1 = view.findViewById(R.id.inventory_image_name1);
            imageName2 = view.findViewById(R.id.inventory_image_name2);
            imageName3 = view.findViewById(R.id.inventory_image_name3);
            imageSize1 = view.findViewById(R.id.inventory_image_size1);
            imageSize2 = view.findViewById(R.id.inventory_image_size2);
            imageSize3 = view.findViewById(R.id.inventory_image_size3);
            image_relative = view.findViewById(R.id.inventory_image_relative);
            inventory_toolbar_delete = (ImageView) getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            inventory_toolbar_edit = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            inventory_toolbar_delete.setVisibility(View.GONE);
            inventory_toolbar_edit.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            relativeRemove1 = view.findViewById(R.id.inventory_remove1);
            relativeRemove2 = view.findViewById(R.id.inventory_remove2);
            relativeRemove3 = view.findViewById(R.id.inventory_remove3);
            save_inventory = view.findViewById(R.id.inventory_save_btn);
            cancel_inventory = view.findViewById(R.id.inventory_cancel_btn);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Add Inventory");
            if (edit_inventory != null) {
                if (edit_inventory.equalsIgnoreCase("edit_inventory")) {
                    enterValue();
                    toolbar_title.setText("Edit Inventory");
                    i = 1;
                }
            }
            setTextWatcher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        try {
            clientAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, inventory_clientlist);
            client_spinner.setAdapter(clientAdapter);
            prop_typeAdpter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, inventory_proplist);
            prop_type_spinner.setAdapter(prop_typeAdpter);
            subprop_typeAdpter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, resi_prop_type_list);
            sub_property_spinner.setAdapter(subprop_typeAdpter);
            propStatusAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, inventory_propstatuslist);
            prop_status_spinner.setAdapter(propStatusAdapter);
            bhkAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_dropdown_item_1line, inventory_bhklist);
            bhk_spinner.setAdapter(bhkAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // do somthing...
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        onCaptureImageResult(uri);
                    } else {
                        uri = Uri.fromFile(image_file);
                        onCaptureImageResult(uri);
                    /*fileList.add(image_file);
                    setFile();*/
                    }
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
        //File file = FileUtils.getFile(context,uri);
        prepareFilePart(uri);
    }

    private void prepareFilePart(Uri fileUri) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                compressedImagePath = fileUri.getPath();
            }
            if (fileUri != null && fileUri.getPath().length() > 0 && compressedImagePath != null && compressedImagePath.length() > 0) {
                File imageFile = new File(compressedImagePath);
                fileList.add(imageFile);
            }
            //File file =  FileUtils.getFile(context, fileUri);
            setFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        try {
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
                        if (checkCameraAndWritablePermission()) {
                            selectImageAlert();
                        } else {
                            requestCameraAndWritablePermission();
                        }
                    } else {
                        selectImageAlert();
                    }
                }
            });
            inventory_add_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkCameraAndWritablePermission()) {
                            selectImageAlert();
                        } else {
                            requestCameraAndWritablePermission();
                        }
                    } else {
                        selectImageAlert();
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
                    if (client1.equalsIgnoreCase("BUY") || client1.equalsIgnoreCase("RENT")) {
                        image_relative.setVisibility(View.GONE);
                    } else {
                        image_relative.setVisibility(View.VISIBLE);
                    }
                }
            });
            prop_type_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    prop_type = parent.getItemAtPosition(position).toString();
                    if (prop_type.equalsIgnoreCase("Residential")) {
                        sub_property_spinner.setText("");
                        subprop_typeAdpter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_dropdown_item_1line, resi_prop_type_list);
                        sub_property_spinner.setAdapter(subprop_typeAdpter);
                        bhk_spinner.setVisibility(View.VISIBLE);
                    } else if (prop_type.equalsIgnoreCase("Commercial")) {
                        sub_property_spinner.setText("");
                        subprop_typeAdpter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_dropdown_item_1line, comm_prop_type_list);
                        sub_property_spinner.setAdapter(subprop_typeAdpter);
                        bhk_spinner.setVisibility(View.GONE);
                    }
                }
            });
            sub_property_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sub_prop_type = parent.getItemAtPosition(position).toString();
                }
            });
            marketSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    microMarketName1 = marketlist.get(position).getName();
                    microMarketCity1 = marketlist.get(position).getCity();
                    microMarketState1 = marketlist.get(position).getState();
                }
            });
            inventory_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    marketDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFile() {
        try {
            switch (fileList.size()) {
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
                    length1 = length1 / 1024;
                    imageName1.setText(filename1);
                    imageSize1.setText(length1 + "KB");
                    break;
                case 2:
                    linearImage1.setVisibility(View.VISIBLE);
                    linearImage2.setVisibility(View.VISIBLE);
                    linearImage3.setVisibility(View.GONE);
                    inventory_add_more.setVisibility(View.VISIBLE);
                    relativeUpload.setVisibility(View.GONE);
                    requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(0));
                    filename1 = fileList.get(0).getName();
                    length1 = fileList.get(0).length();
                    length1 = length1 / 1024;
                    imageName1.setText(filename1);
                    imageSize1.setText(length1 + "KB");
                    requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(1));
                    filename2 = fileList.get(1).getName();
                    length2 = fileList.get(1).length();
                    length2 = length2 / 1024;
                    imageName2.setText(filename2);
                    imageSize2.setText(length2 + "KB");
                    break;
                case 3:
                    linearImage1.setVisibility(View.VISIBLE);
                    linearImage2.setVisibility(View.VISIBLE);
                    linearImage3.setVisibility(View.VISIBLE);
                    inventory_add_more.setVisibility(View.GONE);
                    relativeUpload.setVisibility(View.GONE);
                    requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(0));
                    filename1 = fileList.get(0).getName();
                    length1 = fileList.get(0).length();
                    length1 = length1 / 1024;
                    imageName1.setText(filename1);
                    imageSize1.setText(length1 + "KB");
                    requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(1));
                    filename2 = fileList.get(1).getName();
                    length2 = fileList.get(1).length();
                    length2 = length2 / 1024;
                    imageName2.setText(filename2);
                    imageSize2.setText(length2 + "KB");
                    requestFile3 = RequestBody.create(MediaType.parse("multipart/form-data"), fileList.get(2));
                    filename3 = fileList.get(2).getName();
                    length3 = fileList.get(2).length();
                    length3 = length3 / 1024;
                    imageName3.setText(filename3);
                    imageSize3.setText(length3 + "KB");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInventory() {
        try {
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
            if (invent_budget.equalsIgnoreCase("")) {
                invent_budget = "0";
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
            RequestBody commission = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
            RequestBody subPropertyType = RequestBody.create(MediaType.parse("multipart/form-data"), sub_prop_type);
            if (client1.length() != 0 && prop_type.length() != 0 && prop_status.length() != 0 && clientMobileNo1.length() != 0 && sub_prop_type.length() != 0 && microMarketName1.length() != 0) {
                if (Utils.isNetworkAvailable(context)) {
                    Utils.LoaderUtils.showLoader(context);
                    RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPITask(RetrofitBuilders.getBaseUrl());
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
                                           successDialog(context,message,"Success");
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
                                            apiCode = 100;
                                            openTokenDialog(context);
                                        } else {
                                            Utils.setSnackBar(parentLayout, message);
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
                            if (t.getMessage().equals("Too many follow-up requests: 21")) {
                                apiCode = 100;
                              openTokenDialog(context);
                            }else {
                                Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                            }
                        }
                    });
                } else {
                    taskCompleted = 200;
                    Utils.internetDialog(context, this);
                }
            } else {
                Utils.setSnackBar(parentLayout, "Data can not be empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterValue() {
        try {
            inventory_budget.setText(invent_budget);
            inventory_location.setText(microMarketName1);
            inventory_client_name.setText(clientName1);
            inventory_email.setText(emailId1);
            inventory_mobile.setText(clientMobileNo1);
            inventory_notes.setText(note1);
            client_spinner.setText(client1);
            prop_type_spinner.setText(prop_type);
            if (bedroomType != null) {
                bhk_spinner.setText(bedroomType);
            }
            marketSpinner.setText(microMarketName1);
            prop_status_spinner.setText(prop_status);
            if (client1.equalsIgnoreCase("BUY") || client1.equalsIgnoreCase("RENT")) {
                image_relative.setVisibility(View.GONE);
            } else {
                image_relative.setVisibility(View.VISIBLE);
            }
            if (prop_type.equalsIgnoreCase("Residential")) {
                sub_property_spinner.setText("");
                subprop_typeAdpter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_dropdown_item_1line, resi_prop_type_list);
                sub_property_spinner.setAdapter(subprop_typeAdpter);
                bhk_spinner.setVisibility(View.VISIBLE);
            } else if (prop_type.equalsIgnoreCase("Commercial")) {
                sub_property_spinner.setText("");
                subprop_typeAdpter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_dropdown_item_1line, comm_prop_type_list);
                sub_property_spinner.setAdapter(subprop_typeAdpter);
                bhk_spinner.setVisibility(View.GONE);
            }
            if (!sub_prop_type.equalsIgnoreCase("")) {
                sub_property_spinner.setText(sub_prop_type);
            }
            if (!image1.equalsIgnoreCase("")) {
                filename1 = image1.substring(image1.lastIndexOf('/') + 1);
            }
            if (!image2.equalsIgnoreCase("")) {
                filename2 = image1.substring(image2.lastIndexOf('/') + 1);
            }
            if (!image3.equalsIgnoreCase("")) {
                filename3 = image3.substring(image3.lastIndexOf('/') + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextWatcher() {
        try {
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
                    if (email.matches(emailPattern) && s.length() > 0) {
                        invent_email_layout.setError("");
                        invent_email_layout.setErrorEnabled(false);
                        emailId1 = email;
                    } else if (s.length() > 0) {
                        invent_email_layout.setErrorEnabled(true);
                        invent_email_layout.setError("Invalid email id");
                    } else if (s.length() == 0) {
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
                    if (s.length() == 10) {
                        Utils.hideKeyboard(context, inventory_mobile);
                        clientMobileNo1 = inventory_mobile.getText().toString();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String phone = inventory_mobile.getText().toString().trim();
                    if ((phone.startsWith("6") || phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && (phone.length() == 10)) {
                        invent_phone_layout.setError("");
                        invent_phone_layout.setErrorEnabled(false);
                        clientMobileNo1 = phone;
                    } else if (phone.length() == 0) {
                        clientMobileNo1 = "";
                        invent_phone_layout.setError("");
                        invent_phone_layout.setErrorEnabled(false);
                    } else {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS:
                if (permissions.length > 1) {
                    if (grantResults.length > 0) {
                        boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean WritablePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        if (CameraPermission && WritablePermission) {
                            selectImageAlert();
                        } else {
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                                selectImageAlert();
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
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        switch (taskCompleted) {
            case 100:
                fetchMicromarket();
                break;
            case 200:
                setInventory();
        }
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }


    private void fetchMicromarket() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "signUp");
                Call<SignUpModel.MarketModel> call = retrofitAPIs.fetchMarketApi(mobileNo);
                call.enqueue(new Callback<SignUpModel.MarketModel>() {
                    @Override
                    public void onResponse(Call<SignUpModel.MarketModel> call, Response<SignUpModel.MarketModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                SignUpModel.MarketModel marketModel = new SignUpModel.MarketModel();
                                marketModel = response.body();
                                int statusCode = marketModel.getStatusCode();
                                if (statusCode == 200) {
                                    ArrayList<SignUpModel.MarketObject> arrayList = marketModel.getData();
                                    if (arrayList.size() != 0) {
                                        marketlist.addAll(arrayList);
                                        for (int i = 0; i < marketlist.size(); i++) {
                                            poc_list.add(marketlist.get(i).getName());
                                        }
                                    }
                                }
                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    apiCode = 200;
                                    openTokenDialog(context);
                                } else {
                                    Utils.setSnackBar(parentLayout, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpModel.MarketModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            apiCode = 200;
                            openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            } else {
                taskCompleted = 100;
                Utils.internetDialog(context, this);
            }
            marketAdapter = new ArrayAdapter<String>(context,
                    R.layout.spinner_text, poc_list);
            marketSpinner.setAdapter(marketAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
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

    private void onCaptureImageResult(Uri uri) {
        try {
            compressedImagePath = ImageUtils.compressImage(context, uri.getPath());
            Log.e(TAG, "onSelectFromGalleryResult: " + compressedImagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //File file = FileUtils.getFile(context,uri);
        prepareFilePart(uri);
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

    private void requestCameraAndWritablePermission() {
        try {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CAMERA_AND_WRITABLE_PERMISSIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImageAlert() {
        try {
            final CharSequence[] items = {"Camera", "Gallery",
                    "Cancel"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Select Image");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Camera")) {
                        startCameraIntent();
                    } else if (items[item].equals("Gallery")) {
                        startGalleryIntent();
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCameraIntent() {
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
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGalleryIntent() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            File file = ImageUtils.CreateNewFileForPicture();
            uri = ImageUtils.getImageUri(context, thumbnail);
            prepareFilePart(uri);
            //addToList(tempUri, thumbnail);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void onTryRegenerate() {
        getToken(context);
    }

    private void openTokenDialog(Context context) {
        try {
            if(!getActivity().isFinishing()) {
                Utils.tokenDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getToken(Context context) {
        try {
            new AllUtils().getToken(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if (isSuccess) {
            switch (apiCode) {
                case 100:
                    setInventory();
                    break;
                case 200:
                    fetchMicromarket();
                    break;
            }
        } else {
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
    private void successDialog(final Context context, final String message, String title){
        try {
          final Dialog dialog = new Dialog(context);
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
                    Utils.setSnackBar(parentLayout, message);
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void marketDialog(){
        try {
            final ArrayList<String> pocnewlist = new ArrayList<>();
            pocnewlist.addAll(poc_list);
            adapter = new ArrayAdapter<String>(context, R.layout.spinner_text,pocnewlist);
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.walkthrough_back);
            dialog.setContentView(R.layout.market_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            ListView listView = dialog.findViewById(R.id.market_list_view);
            EditText search_market = dialog.findViewById(R.id.inputSearch);
            Button cancel_btn = dialog.findViewById(R.id.market_dialog_cancel);
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setCancelable(true);
                    dialog.dismiss();
                }
            });
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utils.hideKeyboard(context, view);
                    String selectedMarket = adapter.getItem(position);
                    int position1 = poc_list.indexOf(selectedMarket);
                    if (position1 >= 0) {
                        microMarketName1 = marketlist.get(position1).getName();
                        microMarketCity1 = marketlist.get(position1).getCity();
                        microMarketState1 = marketlist.get(position1).getState();
                        inventory_location.setText(microMarketName1);
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                    }
                }
            });
            search_market.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    adapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


