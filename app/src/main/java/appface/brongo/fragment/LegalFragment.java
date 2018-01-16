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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import appface.brongo.R;
import appface.brongo.activity.WalkThroughActivity;
import appface.brongo.adapter.SubmenuAdapter;
import appface.brongo.util.RecyclerItemClickListener;
import appface.brongo.util.Utils;

import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.LEGAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class LegalFragment extends Fragment {
    private ArrayList<String> arrayList;
   private SubmenuAdapter legalAdapter;
    private ImageView edit_icon,delete_icon,add_icon;
   private Context context;
    private Toolbar toolbar;
   private TextView toolbar_title;
    public LegalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_legal, container, false);
        arrayList = new ArrayList<String>(Arrays.asList("Terms & Conditions", "Privacy Policy", "T & C for Buying & Selling Deals","T & C for Rental Deals"));
        context = getActivity();
        RecyclerView legalrecycle = (RecyclerView)view.findViewById(R.id.legal_recycle);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        legalrecycle.setLayoutManager(verticalmanager);
        legalAdapter = new SubmenuAdapter(context,arrayList);
        legalrecycle.setAdapter(legalAdapter);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title.setText("Legal");

        legalrecycle.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TandC_Fragment tandC_fragment = new TandC_Fragment();
                Bundle args = new Bundle();
                args.putInt("position", position);
                tandC_fragment.setArguments(args);
                Utils.replaceFragment(getFragmentManager(),tandC_fragment,R.id.inventory_frag_container,LEGAL);
            }
        }));
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
}
