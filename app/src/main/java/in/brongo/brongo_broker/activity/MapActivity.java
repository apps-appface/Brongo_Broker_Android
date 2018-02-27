package in.brongo.brongo_broker.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
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
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.util.Utils;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMarkerDragListener{
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
        try {
            parentLayout = (RelativeLayout)findViewById(R.id.map_parent_relative);
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_picker));
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            autocompleteFragment.setHint("Search For Meeting Location");
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("IN")
                    .build();
            autocompleteFragment.setFilter(autocompleteFilter);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                                                                @Override
                                                                public void onPlaceSelected(Place place) {
                                                                   LatLng latLng = place.getLatLng();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            selected_lat = 12.959172;
            selected_lng =77.697419;
            LatLng latLng = new LatLng(selected_lat, selected_lng);
            marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        try {
            selected_lat = latLng.latitude;
            selected_lng = latLng.longitude;
            if(marker != null){
                marker.remove();
            }
            marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            }
        }

    private void setMap() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_REQUEST);
                } else {
                    map.setMyLocationEnabled(true);
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    List<String> providers = locationManager.getProviders(true);
                    Location bestLocation = null;
                    for (String provider : providers) {
                        Location l = locationManager.getLastKnownLocation(provider);
                        if (l == null) {
                            continue;
                        }
                        if (bestLocation == null
                                || l.getAccuracy() < bestLocation.getAccuracy()) {
                            bestLocation = l;
                        }
                    }
                    if (bestLocation != null) {
                        double latitude = bestLocation.getLatitude();
                        double longitude = bestLocation.getLongitude();
                        selected_lat = latitude;
                        selected_lng = longitude;
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (marker != null) {
                            marker.remove();
                        }
                        marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                        //markerOptions.draggable(true);
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                }
            }else{
                map.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                List<String> providers = locationManager.getProviders(true);
                Location bestLocation = null;
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null
                            || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                    }
                }
                if (bestLocation != null) {
                    double latitude = bestLocation.getLatitude();
                    double longitude = bestLocation.getLongitude();
                    selected_lat = latitude;
                    selected_lng = longitude;
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (marker != null) {
                        marker.remove();
                    }
                    marker = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    //markerOptions.draggable(true);
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
            }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        Log.i("System out", "onMarkerDrag...");
    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
        try {
            Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
            selected_lat = arg0.getPosition().latitude;
            selected_lng = arg0.getPosition().longitude;
            map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Utils.setSnackBar(parentLayout,"Click on done button to go back");
    }

}
