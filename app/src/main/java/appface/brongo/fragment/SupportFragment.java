package appface.brongo.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import appface.brongo.R;
import appface.brongo.activity.WalkThroughActivity;
import appface.brongo.adapter.SubmenuAdapter;
import appface.brongo.util.RecyclerItemClickListener;
import appface.brongo.util.Utils;

import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.CONTACT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment {
    private Context context;
    private ArrayList<String> arrayList;
    private Toolbar toolbar;
    private ImageView edit_icon,delete_icon,add_icon;
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
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Support");
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
                break;
            case 1:
                ContactFragment contactFragment = new ContactFragment();
                Utils.replaceFragment(getFragmentManager(),contactFragment,R.id.inventory_frag_container,CONTACT);
                break;
        }
    }
    public static void unsubscribeDialog(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.unsubscribe_dialog);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        final ImageView cross_btn = (ImageView) dialog.findViewById(R.id.unsubscribe_dialog_close);
        final Button got_it_btn = (Button)dialog.findViewById(R.id.unsubscribe_dialog_btn);
        cross_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        got_it_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
