package in.brongo.brongo_broker.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.AppConstants;

public class TermsConditionActivity extends AppCompatActivity implements OnPageChangeListener {
    private Bundle bundle;
    String activityName="";
    private LinearLayout parentLayout;
    Context context;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if(bundle != null){
            activityName = bundle.getString("fromActivity","");
        }
        setContentView(R.layout.activity_terms_condition);
        context = TermsConditionActivity.this;
        Button accept_btn = findViewById(R.id.tc_accept_btn);
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        try {
        parentLayout = findViewById(R.id.tc_parent_linear);
        PDFView pdfView = findViewById(R.id.start_pdfView);
//        ScrollBar scrollBar = findViewById(R.id.start_pdf_scrollBar);
//        pdfView.setScrollBar(scrollBar);
        if(activityName.equalsIgnoreCase("signup") || activityName.equalsIgnoreCase("refer")){
            accept_btn.setVisibility(View.GONE);
        }
            pdfView.fromAsset("tc_brokers.pdf")
                    .defaultPage(1)
                    .onPageChange(this)
//                    .swipeVertical(true)
//                    .showMinimap(false)
                    .load();


        } catch (Exception e) {
            e.printStackTrace();
        }
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFinishing()) {
                    rentalDialog(context);
                }
            }
        });
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onBackPressed() {
        try {
            if(activityName.equalsIgnoreCase("signup")){
                Intent intent = new Intent(context,SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }else if(activityName.equalsIgnoreCase("refer")){
                Intent intent = new Intent(context,ReferActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void rentalDialog(final Context context){
        try {
            String rental_commission = " for the deal will be collected by Brongo. ";
            String commission1 = "25%";
            String rental_commission2 = " will be charged by Brongo and remaining ";
            String rental_commission3 = " will be paid to you as commission.\n ";
            String rentalCommission4 = " commission will be paid to you ";

            String commission2 = "75%";
            String deal_type = "Buy/Sell";
            String commissiontype = "Rental Commissions";
            String fromtext = "directly from the client.";
            SpannableStringBuilder str_comm_type = new SpannableStringBuilder(commissiontype);
            str_comm_type.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, commissiontype.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder str_fromtext = new SpannableStringBuilder(fromtext);
            str_fromtext.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, fromtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder str_commission1 = new SpannableStringBuilder(commission1);
            str_commission1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, commission1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder str_commission2 = new SpannableStringBuilder(commission2);
            str_commission2.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, commission2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder str_deal_type = new SpannableStringBuilder(deal_type);
            str_deal_type.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, deal_type.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.rental_commission_dialog);
            dialog.setCanceledOnTouchOutside(false);
            TextView rental_textview = dialog.findViewById(R.id.rental_dialog_textview);
            rental_textview.append(str_comm_type);
            rental_textview.append(rental_commission);
            rental_textview.append(str_commission1);
            rental_textview.append(rental_commission2);
            rental_textview.append(str_commission2);
            rental_textview.append(rental_commission3);
            rental_textview.append(str_deal_type);
            rental_textview.append(rentalCommission4);
            rental_textview.append(str_fromtext);
            Button dialog_accept_btn = dialog.findViewById(R.id.rental_dialog_accept);
            ImageView close_btn = dialog.findViewById(R.id.rental_dialog_close_btn);
            dialog_accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pref.edit().putBoolean(AppConstants.IS_TERMS_ACCEPTED,true).commit();
                    Intent intent = new Intent(context, WalkThroughActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("from_activity", "terms");
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                }
            });
            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
