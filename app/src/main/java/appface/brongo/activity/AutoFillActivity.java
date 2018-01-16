package appface.brongo.activity;

import android.location.Geocoder;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import appface.brongo.R;
import appface.brongo.fragment.AddInventoryFragment;
import appface.brongo.model.ApiModel;
import appface.brongo.util.Utils;

public class AutoFillActivity extends AppCompatActivity {
    private SupportMapFragment mapFragment;
    LatLng latLng;
    String sender_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null){
            sender_intent = getIntent().getStringExtra("sender_intent");
        }
        setContentView(R.layout.activity_auto_fill);
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Add More Place");
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("IN")
                .build();

        autocompleteFragment.setFilter(autocompleteFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                latLng = place.getLatLng();
                Geocoder geocoder = new Geocoder(AutoFillActivity.this );
                try {
                    String address1 = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getSubLocality();
                    String address2 = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getLocality();
                    String address3 = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAdminArea();
                    ApiModel.MicroMarketModel microMarketModel = new ApiModel.MicroMarketModel();
                    if(sender_intent != null) {
                        if (sender_intent.equalsIgnoreCase("AddInventory")) {
                            AddInventoryFragment.inventory_location.setText(address1);
                            AddInventoryFragment.microMarketName1 = address1;
                            AddInventoryFragment.microMarketCity1 = address2;
                            AddInventoryFragment.microMarketState1 = address3;
                            finish();
                        }
                    }else {
                        microMarketModel.setMicroMarketCity(address2);
                        microMarketModel.setMicroMarketState(address3);
                        microMarketModel.setMicroMarketName(address1);
                       // SignUpActivity.micromarketlist.add(microMarketModel);
                        SignUpActivity.horizontalAdapter.notifyDataSetChanged();
                        int length = 3 - SignUpActivity.micromarketlist.size();
                        SignUpActivity.addmore_text.setText("+ADD " + length + " MORE");
                        if (length == 0) {
                            SignUpActivity.addmore_text.setVisibility(View.GONE);
                        }
                        if(SignUpActivity.micromarketlist.size() > 0){
                            SignUpActivity.micromarket_reset.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (IOException e ) {
                }
                finish();
            }

            @Override
            public void onError(Status status) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
