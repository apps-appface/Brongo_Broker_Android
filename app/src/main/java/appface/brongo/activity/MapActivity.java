package appface.brongo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import appface.brongo.R;
import appface.brongo.util.Utils;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,LocationListener,GoogleMap.OnMarkerDragListener{
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Button done_btn;
    private RelativeLayout parentLayout;
    private Marker marker;
    private final int FINE_PERMISSION_REQUEST = 1000;
    private Context context;
    private double selected_lat, selected_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        context = MapActivity.this;
        parentLayout = (RelativeLayout)findViewById(R.id.map_parent_relative);
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_picker));
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Search For Place");
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("IN")
                .build();
        autocompleteFragment.setFilter(autocompleteFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                                                            @Override
                                                            public void onPlaceSelected(Place place) {
                                                               LatLng latLng = place.getLatLng();
                                                                if(marker != null){
                                                                    marker.remove();
                                                                }
                                                                marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                                                                //markerOptions.draggable(true);
                                                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                                                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                                                            }

                                                            @Override
                                                            public void onError(Status status) {

                                                            }
                                                        });

                done_btn = (Button)findViewById(R.id.map_done_btn);
        mapFragment.getMapAsync(this);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReminderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("lat_value", selected_lat);
                intent.putExtra("long_value", selected_lng);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        selected_lat = 12.959172;
        selected_lng =77.697419;
        LatLng latLng = new LatLng(selected_lat, selected_lng);
        marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //markerOptions.draggable(true);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        map.setOnMapClickListener(this);
        map.setOnMarkerDragListener(this);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQUEST);
            } else {
                setMap();
            }
        } else {
            setMap();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        if(marker != null){
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //markerOptions.draggable(true);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        selected_lat = latLng.latitude;
        selected_lng = latLng.longitude;
        if(marker != null){
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //markerOptions.draggable(true);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == FINE_PERMISSION_REQUEST) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMap();
            } else {
                   return;
                }
                // Permission was denied. Display an error message.
            }
        }

    private void setMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                selected_lat = latitude;
                selected_lng = longitude;
                LatLng latLng = new LatLng(latitude, longitude);
                if(marker != null){
                    marker.remove();
                }
                marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                //markerOptions.draggable(true);
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                onLocationChanged(location);
                locationManager.requestLocationUpdates(bestProvider, 20000, 0,this);
            }
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub
        Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        Log.i("System out", "onMarkerDrag...");
    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
        Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
        selected_lat = arg0.getPosition().latitude;
        selected_lng = arg0.getPosition().longitude;
        map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
    }

    @Override
    public void onBackPressed() {
    }
}
