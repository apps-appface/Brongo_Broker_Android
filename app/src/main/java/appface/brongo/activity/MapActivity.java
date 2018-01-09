package appface.brongo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import appface.brongo.R;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {
    private GoogleMap map;
    private LinearLayout pop_up_address;
    private SupportMapFragment mapFragment;
    private final int FINE_PERMISSION_REQUEST = 1000;
    private final int COARSE_PERMISSION_REQUEST = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        LatLng brongo = new LatLng(13.024282, 77.63298699999996);
        if (Build.VERSION.SDK_INT >= 22){
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQUEST);
        } else {
            if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_PERMISSION_REQUEST);
            } else {
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(brongo, 14));
                map.addMarker(new MarkerOptions()
                        .title("Brongo")
                        .position(brongo)).showInfoWindow();
            }
        }
        }else{
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(brongo, 14));
            map.addMarker(new MarkerOptions()
                    .title("Brongo")
                    .position(brongo)).showInfoWindow();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 22) {
                        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_PERMISSION_REQUEST);
                        } else {
                            return;
                        }
                    }else{
                        return;
                    }
                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            case 2000:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else{
                    finish();
                    // permission denied, boo! Disable the// functionality that depends on this permission.
                }
                break;
        }
    }
    @Override
    public void onMapClick(LatLng latLng) {
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
