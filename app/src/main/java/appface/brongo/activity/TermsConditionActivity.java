package appface.brongo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import appface.brongo.R;
import appface.brongo.util.AppConstants;

public class TermsConditionActivity extends AppCompatActivity implements OnPageChangeListener {
    private Bundle bundle;
    String activityName="";
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if(bundle != null){
            activityName = bundle.getString("fromActivity","");
        }
        setContentView(R.layout.activity_terms_condition);
        context = TermsConditionActivity.this;
        PDFView pdfView = (PDFView)findViewById(R.id.start_pdfView);
        ScrollBar scrollBar = (ScrollBar)findViewById(R.id.start_pdf_scrollBar);
        Button accept_btn = (Button)findViewById(R.id.tc_accept_btn);
        final SharedPreferences pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        pdfView.setScrollBar(scrollBar);
        if(activityName.equalsIgnoreCase("signup")){
            accept_btn.setVisibility(View.GONE);
        }
        try {
            pdfView.fromAsset("tc_brokers.pdf")
                    .defaultPage(1)
                    .onPageChange(this)
                    .swipeVertical(true)
                    .showMinimap(false)
                    .load();
        } catch (Exception e) {
        }
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().putBoolean(AppConstants.IS_TERMS_ACCEPTED,true).commit();
                Intent intent = new Intent(TermsConditionActivity.this, WalkThroughActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from_activity", "terms");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onBackPressed() {
        if(activityName.equalsIgnoreCase("signup")){
            Intent intent = new Intent(context,SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }
}
