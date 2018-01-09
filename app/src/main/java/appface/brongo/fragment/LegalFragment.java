package appface.brongo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class LegalFragment extends Fragment {
    private ArrayList<String> arrayList;
   private SubmenuAdapter legalAdapter;
   private Context context;
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
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar_title);
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        legalrecycle.setLayoutManager(verticalmanager);
        legalAdapter = new SubmenuAdapter(context,arrayList);
        legalrecycle.setAdapter(legalAdapter);

        legalrecycle.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TandC_Fragment tandC_fragment = new TandC_Fragment();
                Bundle args = new Bundle();
                args.putInt("position", position);
                tandC_fragment.setArguments(args);
                Utils.replaceFragment(getFragmentManager(),tandC_fragment,R.id.inventory_frag_container,true);
                toolbar_title.setText("Terms & Conditions");
            }
        }));
        return view;
    }
}
