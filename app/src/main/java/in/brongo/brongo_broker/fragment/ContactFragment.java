package in.brongo.brongo_broker.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
    private ImageView edit_icon,delete_icon,add_icon,call_btn,email_btn;
    private TextView menu_title,email_text,phone_text;
    private RelativeLayout call_relative,email_relative,parentLayout;
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

        return view;
    }
    private void initialise(View view){
        context = getActivity();
        parentLayout = (RelativeLayout)getActivity().findViewById(R.id.menu_parent_relative);
        call_relative = (RelativeLayout)view.findViewById(R.id.contact_call_relative);
        email_relative = (RelativeLayout)view.findViewById(R.id.contact_email_relative);
        menu_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        email_text = (TextView)view.findViewById(R.id.email_text);
        phone_text = (TextView)view.findViewById(R.id.phone_text);
        call_btn = (ImageView)view.findViewById(R.id.contact_us_call_btn);
        email_btn = (ImageView)view.findViewById(R.id.contact_us_email_btn);
        menu_title.setText("Contact Us");
    }
    private void call(){
        String mobile = phone_text.getText().toString().replaceAll("\\s","");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+mobile));
        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        }catch (Exception e){

        }
    }
    private void email(){
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
            Utils.setSnackBar(parentLayout,"No email client installed.");
        }
    }
}
