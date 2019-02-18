package com.gpslocationapp.yasaman.gpslocationapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.Manifest;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
            , LocationListener
{

    public static final String TAG = "TAG";
    private static final int REQUEST_CODE = 1000;

    private GoogleApiClient googleApiClient ;
    /*private Location location;*/
    /*private TextView txtLocation;*/

    EditText edtAddress;
    EditText edtMilesPerHour;
    EditText edtmetersPerMile;
    TextView txtDistance;
    TextView txtTime;
    Button btnGetTheData;

    private String destinationLocationAddress = "";

    private TaxiManager taxiManager;

     LocationRequest locationRequest ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtAddress = findViewById(R.id.edtAddress);
        edtMilesPerHour = findViewById(R.id.edtMilesPerHour);
        edtmetersPerMile = findViewById(R.id.edtMetersPerMile);

        txtDistance = findViewById(R.id.txtDistanceValue);
        txtTime = findViewById(R.id.txtTime);

        btnGetTheData = findViewById(R.id.btnGetTheData);

      /*  btnGetTheData.setOnClickListener(MainActivity.this);*/

        taxiManager = new TaxiManager();

      /*  txtLocation = (TextView) findViewById(R.id.txtLocation);*/

        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API).build();


        btnGetTheData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText( MainActivity.this,"You Clicked", Toast.LENGTH_SHORT).show();

                String addressValue = edtAddress.getText().toString();

                boolean isGeoCoding = true;

                /*if (!addressValue.equals(destinationLocationAddress) ){*/

                    //  addressValue = destinationLocationAddress;
                    Toast.makeText(MainActivity.this, "First if is correct", Toast.LENGTH_LONG).show();


                    destinationLocationAddress = addressValue;
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {

                        // list here is like an arrey but with more feature (it's an interface) it's not a class
                        List<Address> myAddresses =
                                geocoder.getFromLocationName(destinationLocationAddress,4 );

                        if (myAddresses != null){

                            double latitude = myAddresses.get(0).getLatitude();
                            double longitude = myAddresses.get(0).getLongitude();

                            Location locationAddress = new Location("MyDestination");
                            locationAddress.setLatitude(latitude);
                            locationAddress.setLongitude(longitude);

                            taxiManager.setDestinationLocation(locationAddress);

                        }

                    }catch (Exception e){

                        isGeoCoding = false;
                        e.printStackTrace();

                    }



                int permissionCheck = ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        ACCESS_COARSE_LOCATION);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED){

                   // Toast.makeText(MainActivity.this, "Second if is correct", Toast.LENGTH_LONG).show();

                    FusedLocationProviderApi fusedLocationProviderApi =
                            LocationServices.FusedLocationApi;

                    Location usercurrentLocation = fusedLocationProviderApi.
                            getLastLocation(googleApiClient);


                    // isGeoCoding means if isGeoCoding variable is true
                    if (usercurrentLocation != null && isGeoCoding){

                        txtDistance.setText(taxiManager.
                                returnTheMilesBetweenCurrentLocationAndDestinationLocation
                                        (usercurrentLocation,
                                                Integer.parseInt(edtmetersPerMile.getText().toString()) ));

                        txtTime.setText(taxiManager.returnTheTimeLeftToGetToDestinationLocation
                                (usercurrentLocation, Float.parseFloat(edtMilesPerHour.getText().toString()),
                                        Integer.parseInt(edtmetersPerMile.getText().toString())));

                    }


                }else {

                    txtDistance.setText("This App is not allowed to access th location");

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                }

            }
        });

    }

   /* private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }*/
    @Override
    public void onLocationChanged(Location location) {

   /*     onClick(null);*/

    }

    @Override
    protected void onPause() {
        super.onPause();

        FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
        fusedLocationProviderApi.removeLocationUpdates(googleApiClient, MainActivity.this);
    }

   /* @Override
    public void onClick(View v) {

        String addressValue = edtAddress.getText().toString();

        boolean isGeoCoding = true;

        if (!addressValue.contains(destinationLocationAddress) ){

          //  addressValue = destinationLocationAddress;

            destinationLocationAddress = addressValue;
            Geocoder geocoder = new Geocoder(getApplicationContext());

            try {

                // list here is like an arrey but with more feature (it's an interface) it's not a class
                List<Address> myAddresses =
                        geocoder.getFromLocationName(destinationLocationAddress,4 );

                if (myAddresses != null){

                    double latitude = myAddresses.get(0).getLatitude();
                    double longitude = myAddresses.get(0).getLongitude();

                    Location locationAddress = new Location("MyDestination");
                    locationAddress.setLatitude(latitude);
                    locationAddress.setLongitude(longitude);

                    taxiManager.setDestinationLocation(locationAddress);

                }

            }catch (Exception e){

                isGeoCoding = false;
                e.printStackTrace();

            }

        }

        int permissionCheck = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){

            FusedLocationProviderApi fusedLocationProviderApi =
                    LocationServices.FusedLocationApi;

            Location usercurrentLocation = fusedLocationProviderApi.
                    getLastLocation(googleApiClient);


            // isGeoCoding means if isGeoCoding variable is true
            if (usercurrentLocation != null && isGeoCoding){

                txtDistance.setText(taxiManager.
                        returnTheMilesBetweenCurrentLocationAndDestinationLocation
                                (usercurrentLocation,
                                        Integer.parseInt(edtmetersPerMile.getText().toString()) ));

                txtTime.setText(taxiManager.returnTheTimeLeftToGetToDestinationLocation
                        (usercurrentLocation, Float.parseFloat(edtMilesPerHour.getText().toString()),
                                Integer.parseInt(edtmetersPerMile.getText().toString())));

            }


        }else {

            txtDistance.setText("This App is not allowed to access th location");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        
    }*/

    //these methods below are necessary for our implements : ConnectionCallbacks and  OnConnectionFailedListener

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "We are connected to user location");
     /*   showTheUserLocation();*/

     FusedLocationProviderApi fusedLocationProviderApi =
             LocationServices.FusedLocationApi;
        locationRequest = new LocationRequest();
        // it means after 10 second location request will be updated an app request for the location again.
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
       // here after 5 meters request for the location will be sent
        locationRequest.setSmallestDisplacement(5);

        int permissionCheck = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            if (googleApiClient.isConnected()) {

                fusedLocationProviderApi.requestLocationUpdates
                        (googleApiClient, locationRequest, MainActivity.this);
            } else {
                googleApiClient.connect();
            }

        } else {

            txtDistance.setText("This App is not allowed to access th location");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "The Connection is Suspended");


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "The Connection Failed");

        if (connectionResult.hasResolution()){

            try {

                connectionResult.startResolutionForResult(MainActivity.this, ConnectionResult.RESOLUTION_REQUIRED);
            }catch (Exception e){

                Log.d(TAG, e.getStackTrace().toString());

            }

        }else {

            Toast.makeText(MainActivity.this,
                    "GooglePlayServices is not working. EXIT!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            googleApiClient.connect();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null){

            googleApiClient.connect();
        }
    }

    // Custom Methods below that i added by myself

    /*private void showTheUserLocation(){

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){


            FusedLocationProviderApi fusedLocationProviderApi =
                    LocationServices.FusedLocationApi;

            location = fusedLocationProviderApi.getLastLocation(googleApiClient);
            if (location != null){

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                txtLocation.setText(latitude + "," + longitude);

            }else {

                txtLocation.setText("This app is not able to access the location now. " +
                        "Try again later.");
            }

        }else {

            txtLocation.setText("This app is not allowed to access the location");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);

        }
    }*/
}
