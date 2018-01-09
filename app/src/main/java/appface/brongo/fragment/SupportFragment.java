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
import appface.brongo.activity.WalkThroughActivity;
import appface.brongo.adapter.SubmenuAdapter;
import appface.brongo.util.RecyclerItemClickListener;
import appface.brongo.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment {
    private Context context;
    private ArrayList<String> arrayList;
    private Toolbar toolbar;
    private TextView toolbar_title;

    public SupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_support, container, false);
        context = getActivity();
        arrayList = new ArrayList<String>(Arrays.asList("Take me to the app walkthrough", "Contact Brongo Customer Support", "I want to unsubscribe"));
        RecyclerView support_recycle = (RecyclerView)view.findViewById(R.id.support_recycle);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar_title);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        support_recycle.setLayoutManager(verticalmanager);
        SubmenuAdapter legalAdapter = new SubmenuAdapter(context,arrayList);
        support_recycle.setAdapter(legalAdapter);
        support_recycle.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                changePage(position);
            }
        }));
        return view;
    }
    private void changePage(int position){
        switch(position){
            case 0:
                Intent intent = new Intent(getActivity(), WalkThroughActivity.class);
                intent.putExtra("from_activity","menu");
                context.startActivity(intent);
                getActivity().finish();
                break;
            case 1:
                ContactFragment contactFragment = new ContactFragment();
                Utils.replaceFragment(getFragmentManager(),contactFragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("Contact Us");
                break;
        }
    }

}
