package appface.brongo.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import appface.brongo.R;
import appface.brongo.util.AppConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferFragmentTwo extends Fragment {
    private SharedPreferences pref;
    private TextView referral_code_text;
    private Button refer_share_btn;
    private String dynamic_link = "https://zc9nb.app.goo.gl/naxz";

    public ReferFragmentTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_refer_fragment_two, container, false);
        initialiseView(view);
        refer_share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClicked();
            }
        });
        return view;
    }
    private void initialiseView(View view){
        pref = getContext().getSharedPreferences(AppConstants.PREF_NAME,0);
        referral_code_text = (TextView)view.findViewById(R.id.referral_code);
        refer_share_btn = (Button)view.findViewById(R.id.refer_link_btn);
        referral_code_text.setText("Your Referral code is : "+ pref.getString(AppConstants.REFERRAL_ID,""));
    }
    private void onSubmitClicked(){
        String referralLink= dynamic_link  + " \n Your Referral code is : "+ pref.getString(AppConstants.REFERRAL_ID,"");
        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2 .putExtra(android.content.Intent.EXTRA_SUBJECT, "Referral link for Brongo");
        intent2.putExtra(Intent.EXTRA_TEXT, referralLink);
        startActivity(Intent.createChooser(intent2, "Share via"));
    }
}
