package appface.brongo.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import appface.brongo.R;
import appface.brongo.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
    private Button contact_call,contact_email;
    private TextView menu_title,email_text,phone_text;
    ImageView menu_toolbar_edit,menu_toolbar_delete;
    private Context context;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initialise(view);
        contact_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = phone_text.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+mobile));

                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });
        contact_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Utils.showToast(context,"There is no email client installed.");
                }
            }
        });

        return view;
    }
    private void initialise(View view){
        context = getActivity();
        contact_call = (Button)view.findViewById(R.id.contact_call_btn);
        contact_email = (Button)view.findViewById(R.id.contact_email_btn);
        menu_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        menu_toolbar_edit = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        menu_toolbar_delete = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        email_text = (TextView)view.findViewById(R.id.contact_email);
        phone_text = (TextView)view.findViewById(R.id.contact_phone);
        menu_toolbar_edit.setVisibility(View.GONE);
        menu_toolbar_delete.setVisibility(View.GONE);
        menu_title.setText("Contact Us");
    }


}
