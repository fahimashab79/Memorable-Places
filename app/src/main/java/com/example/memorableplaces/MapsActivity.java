package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    public void zoomOnUserLocation(Location location, String title) {
        if (location != null) {


            LatLng currentposition = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentposition).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentposition, 16));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED &&grantResults.length>0)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000000000,100000000,locationListener);
                //locationManager.removeUpdates(locationListener);
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                zoomOnUserLocation(lastKnownLocation,"Here are u");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent=getIntent();

         Integer a=intent.getIntExtra("number",0);
        //Toast.makeText(this, String.valueOf(a), Toast.LENGTH_SHORT).show();

        if(a==0)
        {
            //zoom on user location
            locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    zoomOnUserLocation(location,"Here is U");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000000,10000000,locationListener);
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                zoomOnUserLocation(lastKnownLocation,"Here are u");

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }



        }
        else
        {
            Log.i("Value of a",String.valueOf(a)+String.valueOf(MainActivity.location.size()));

            Location placeLocation=new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.location.get(a).latitude);
            placeLocation.setLongitude(MainActivity.location.get(a).longitude);
            zoomOnUserLocation(placeLocation,MainActivity.places.get(a));

        }


        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";
        try {
           List<Address> addressList= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
           if(addressList!=null)
           {
              if(addressList.get(0).getThoroughfare()!=null)
              {
                  address+=addressList.get(0).getThoroughfare();
              }

           }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address=="")
        {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMDD_HHmmss");
            address+=sdf.format(new Date());
        }
        Log.i("helo","hello");
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(address)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        MainActivity.places.add(address);
        MainActivity.location.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        ArrayList<String>longitude=new ArrayList<>();
        ArrayList<String>latitude=new ArrayList<>();
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplaces",Context.MODE_PRIVATE);

        try {
            for(LatLng cord:MainActivity.location)
            {
                longitude.add(Double.toString(cord.longitude));
                latitude.add(Double.toString(cord.latitude));
            }


            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("lons",ObjectSerializer.serialize(longitude)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(latitude)).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
