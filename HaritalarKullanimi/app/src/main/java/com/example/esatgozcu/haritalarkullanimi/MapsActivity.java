package com.example.esatgozcu.haritalarkullanimi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        // Konum tespit özelliklerini kullanabilmek için LocationManager oluşturuyoruz.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // Kullanıcının konumu değiştiği zaman konumu güncelliyoruz.
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
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
        };

        // İzin işlemlerini ayarlıyoruz.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        else // İzin verilmiş ise..
        {
            // Konumu güncelliyoruz.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Intent intent = getIntent();
            String info = intent.getStringExtra("info");

            if (info.equalsIgnoreCase("new")) {

                // Eğer yeni konum eklenecekse..
                mMap.clear();
                // Kullanıcının son konumunu haritada yaklaştırıyoruz.
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));


            } else {

                // Mevcut konum görüntüleneceği zaman..
                mMap.clear();
                int position = intent.getIntExtra("position", 0);
                // MainActivity sayfasında bulunan locations array'inden verimizi çekiyoruz.
                LatLng location = new LatLng(MainActivity.locations.get(position).latitude, MainActivity.locations.get(position).longitude);
                //Haritaya aktarıyoruz.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                // Haritada yerin belli olması için marker ekliyoruz başlığına kayıt ettiğimiz ismi yazıyoruz.
                mMap.addMarker(new MarkerOptions().position(location).title(MainActivity.names.get(position)));
            }
        }
    }

    // İzin isteme sonucunu kontol ediyoruz.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // Eğer izin verilmiş ise..

                // Konumu güncelliyoruz.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Intent intent = getIntent();
                String info = intent.getStringExtra("info");

                if (info.equalsIgnoreCase("new")) {

                    // Eğer yeni konum eklenecekse..
                    mMap.clear();
                    // Kullanıcının son konumunu haritada yaklaştırıyoruz.
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));

                } else {

                    // Mevcut konum görüntüleneceği zaman..
                    mMap.clear();
                    int position = intent.getIntExtra("position", 0);
                    // MainActivity sayfasında bulunan locations array'inden verimizi çekiyoruz.
                    LatLng location = new LatLng(MainActivity.locations.get(position).latitude, MainActivity.locations.get(position).longitude);
                    //Haritaya aktarıyoruz.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                    // Haritada yerin belli olması için marker ekliyoruz başlığına kayıt ettiğimiz ismi yazıyoruz.
                    mMap.addMarker(new MarkerOptions().position(location).title(MainActivity.names.get(position)));
                }
            }
        }
    }

    // Harita üzerine uzun basılı tutulduğunda..
    @Override
    public void onMapLongClick(LatLng latLng) {

        // Koordinat bilgisine göre konum bulmak için Geocoder oluşturuyoruz.
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {

                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        address += addressList.get(0).getSubThoroughfare();
                    }
                }

            } else {
                address = "New Place";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Dokunduğumuz yere Marker ekliyoruz başlığını Geocoder'dan gelen adresi ekliyoruz.
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        // MainActivity sınıfındaki arrayList'lere verileri ekliyoruz.
        MainActivity.names.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(),"Yeni yer oluşturuldu",Toast.LENGTH_LONG).show();

        try {

            // Veritabanına kayıt ediyoruz.
            Double l1 = latLng.latitude;
            Double l2 = latLng.longitude;

            String coord1 = l1.toString();
            String coord2 = l2.toString();

            database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, latitude VARCHAR, longitude VARCHAR)");

            String toCompile = "INSERT INTO places (name, latitude, longitude) VALUES (?, ?, ?)";

            SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);

            sqLiteStatement.bindString(1, address);
            sqLiteStatement.bindString(2, coord1);
            sqLiteStatement.bindString(3, coord2);

            sqLiteStatement.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
