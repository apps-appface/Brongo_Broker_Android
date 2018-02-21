package in.brongo.brongo_broker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.AppConstants;

public class TermsConditionActivity extends AppCompatActivity implements OnPageChangeListener {
    private Bundle bundle;
    String activityName="";
    private LinearLayout parentLayout;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if(bundle != null){
            activityName = bundle.getString("fromActivity","");
        }
        setContentView(R.layout.activity_terms_condition);
        try {
        context = TermsConditionActivity.this;
        parentLayout = findViewById(R.id.tc_parent_linear);
        PDFView pdfView = findViewById(R.id.start_pdfView);
        ScrollBar scrollBar = findViewById(R.id.start_pdf_scrollBar);
        Button accept_btn = findViewById(R.id.tc_accept_btn);

        final SharedPreferences pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        pdfView.setScrollBar(scrollBar);
        if(activityName.equalsIgnoreCase("signup") || activityName.equalsIgnoreCase("refer")){
            accept_btn.setVisibility(View.GONE);
        }
            pdfView.fromAsset("tc_brokers.pdf")
                    .defaultPage(1)
                    .onPageChange(this)
                    .swipeVertical(true)
                    .showMinimap(false)
                    .load();


        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().putBoolean(AppConstants.IS_TERMS_ACCEPTED,true).commit();
                Intent intent = new Intent(context, WalkThroughActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from_activity", "terms");
                startActivity(intent);
                finish();
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
