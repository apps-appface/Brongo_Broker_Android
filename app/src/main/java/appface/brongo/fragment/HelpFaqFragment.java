package appface.brongo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import appface.brongo.R;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.ReferActivity;
import appface.brongo.adapter.SubmenuAdapter;
import appface.brongo.util.RecyclerItemClickListener;
import appface.brongo.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFaqFragment extends Fragment {
    private Context context;
    private RecyclerView help_recycle;
    private ArrayList<String> arrayList;
    private SubmenuAdapter helpAdapter;
    private TextView toolbar_title;
    private Toolbar toolbar;
    public HelpFaqFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_faq, container, false);
        initialise(view);
        help_recycle.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                changeFragment(position);
            }
        }));
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        help_recycle = (RecyclerView)view.findViewById(R.id.help_reycleview);
        arrayList = new ArrayList<String>(Arrays.asList("ABOUT US", "CONTACT US", "FAQ's","REFER BROKER","SUPPORT","LEGAL"));
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        help_recycle.setLayoutManager(verticalmanager);
        helpAdapter = new SubmenuAdapter(context,arrayList);
        help_recycle.setAdapter(helpAdapter);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar_title);
    }
    private void changeFragment(int position){
        switch (position){
            case 0:
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                Utils.replaceFragment(getFragmentManager(),aboutUsFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("About Brongo");
                break;
            case 1:
                ContactFragment contactFragment = new ContactFragment();
                Utils.replaceFragment(getFragmentManager(),contactFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("Contact Us");
                break;
            case 2:
                FaqFragment faqFragment = new FaqFragment();
                Utils.replaceFragment(getFragmentManager(),faqFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("FAQ's");
                break;
            case 3:
                Intent i = new Intent(context, ReferActivity.class);
                startActivity(i);
                break;
            case 4:
                SupportFragment supportFragment = new SupportFragment();
                Utils.replaceFragment(getFragmentManager(),supportFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("Support");
                break;
            case 5:
                LegalFragment legalFragment = new LegalFragment();
                Utils.replaceFragment(getFragmentManager(),legalFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("Legal");
                break;

        }
    }

}