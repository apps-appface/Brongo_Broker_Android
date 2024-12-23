package in.brongo.brongo_broker.fragment;


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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.ReferActivity;
import in.brongo.brongo_broker.adapter.SubmenuAdapter;
import in.brongo.brongo_broker.util.RecyclerItemClickListener;
import in.brongo.brongo_broker.util.Utils;

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.ABOUTUS;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.CONTACT;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.FAQ;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.LEGAL;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.SUPPORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFaqFragment extends Fragment {
    private Context context;
    private RelativeLayout parentLayout;
    private RecyclerView help_recycle;
    private ArrayList<String> arrayList;
    private SubmenuAdapter helpAdapter;
    private TextView toolbar_title;
    private ImageView edit_icon,delete_icon,add_icon;
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
        try {
            context = getActivity();
            help_recycle = view.findViewById(R.id.help_reycleview);
            arrayList = new ArrayList<String>(Arrays.asList("ABOUT US", "CONTACT US", "FAQ's","REFER BROKER","SUPPORT","LEGAL"));
            LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
            verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
            help_recycle.setLayoutManager(verticalmanager);
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            helpAdapter = new SubmenuAdapter(context,arrayList);
            help_recycle.setAdapter(helpAdapter);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Help & FAQ's");
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void changeFragment(int position){
        try {
            switch (position){
                case 0:
                    AboutUsFragment aboutUsFragment = new AboutUsFragment();
                    Utils.replaceFragment(getFragmentManager(),aboutUsFragment,R.id.inventory_frag_container,ABOUTUS);
                    break;
                case 1:
                    ContactFragment contactFragment = new ContactFragment();
                    Utils.replaceFragment(getFragmentManager(),contactFragment,R.id.inventory_frag_container,CONTACT);
                    break;
                case 2:
                    FaqFragment faqFragment = new FaqFragment();
                    Utils.replaceFragment(getFragmentManager(),faqFragment,R.id.inventory_frag_container,FAQ);
                    break;
                case 3:
                    Intent i = new Intent(context, ReferActivity.class);
                    startActivity(i);
                    break;
                case 4:
                    SupportFragment supportFragment = new SupportFragment();
                    Utils.replaceFragment(getFragmentManager(),supportFragment,R.id.inventory_frag_container,SUPPORT);
                    break;
                case 5:
                    LegalFragment legalFragment = new LegalFragment();
                    Utils.replaceFragment(getFragmentManager(),legalFragment,R.id.inventory_frag_container,LEGAL);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
