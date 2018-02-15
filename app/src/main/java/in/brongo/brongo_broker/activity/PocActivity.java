package in.brongo.brongo_broker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;
import in.brongo.brongo_broker.R;

public class PocActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poc);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        float densityDpi = metrics.density;
        float scaleddensity = metrics.scaledDensity;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        TextView widthpxtext = (TextView)findViewById(R.id.screenWidthpx);
        TextView heightpxtext = (TextView)findViewById(R.id.screenHeightpx);
        TextView widthdptext = (TextView)findViewById(R.id.screenWidthDp);
        TextView heightdptext = (TextView)findViewById(R.id.screenHeightDp);
        TextView densitytext = (TextView)findViewById(R.id.screendensitydpi);
        TextView scaleddensity_text = (TextView)findViewById(R.id.scaled_density);
        widthpxtext.append(widthPixels+"px");
        heightpxtext.append(heightPixels+"px");
        widthdptext.append(xdpi+"dp");
        heightdptext.append(ydpi+"dp");
        densitytext.append(densityDpi+"");
        scaleddensity_text.append(scaleddensity+"");
    }
}
