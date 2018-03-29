package in.brongo.brongo_broker.fragment;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
    private static final int REQUEST_CALL_PERMISSIONS = 222;
    private ImageView edit_icon, delete_icon, add_icon, call_btn, email_btn, whatsapp_btn;
    private TextView menu_title, email_text, phone_text;
    private RelativeLayout call_relative, email_relative, parentLayout, whatsapp_relative;
    private Toolbar toolbar;
    private Context context;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        initialise(view);
        call_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        email_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email();
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email();
            }
        });
        whatsapp_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWhatsappClick();
            }
        });
        whatsapp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWhatsappClick();
            }
        });
        return view;
    }

    private void initialise(View view) {
        try {
            context = getActivity();
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            call_relative = view.findViewById(R.id.contact_call_relative);
            email_relative = view.findViewById(R.id.contact_email_relative);
            whatsapp_relative = view.findViewById(R.id.contact_whatsapp_relative);
            menu_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            email_text = view.findViewById(R.id.email_text);
            phone_text = view.findViewById(R.id.phone_text);
            call_btn = view.findViewById(R.id.contact_us_call_btn);
            email_btn = view.findViewById(R.id.contact_us_email_btn);
            whatsapp_btn = view.findViewById(R.id.contact_us_whatsapp_btn);
            menu_title.setText("Contact Us");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void call() {
        String mobile = phone_text.getText().toString().replaceAll("\\s", "");
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
        //callIntent.setData(Uri.parse("tel:" + mobile));
        try {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL_PERMISSIONS);
            } else {
                startActivity(callIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void email() {
        String recipient = email_text.getText().toString();
        String[] TO = {recipient};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        try {
            startActivity(emailIntent);
            //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Utils.setSnackBar(parentLayout, "No email client installed.");
        }
    }

    private void onWhatsappClick() {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        try {
            if (isWhatsappInstalled) {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("919845055841") + "@s.whatsapp.net");//phone number without "+" prefix

                startActivity(sendIntent);
            } else {
                Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                Utils.setSnackBar(parentLayout, "WhatsApp not Installed");
                startActivity(goToMarket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    call();
                } else {
                   Utils.setSnackBar(parentLayout, "Please allow permission from settings.");
                }
                return;
            }
        }
    }
}
