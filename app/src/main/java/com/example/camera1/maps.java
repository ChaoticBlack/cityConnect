package com.example.camera1;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class maps extends AppCompatActivity implements OnMapReadyCallback{
    public static class User {

        public double longitude;
        public double latitude;
        public String status,imageurl;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(double longitude, double latitiude,String status,String imageurl) {
            this.longitude=longitude;
            this.latitude=latitiude;
            this.status=status;
            this.imageurl=imageurl;
        }

    }
    private TextView mTextMessage;
    private DatabaseReference mDatabase;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 10f;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        Intent in;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    // mTextMessage.setText(R.string.title_home);
                    in=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(in);
                    return true;
                }
                case R.id.map_dashboard:
                    return true;
                case R.id.rate_us: {
                    //mTextMessage.setText(R.string.title_notifications);
                    return true;
                }
            }
            return false;
        }
    };
//    private void collectPhoneNumbers(Map<String,Object> users) {
////
////        //ArrayList<Long> phoneNumbers = new ArrayList<>();
////
////        //iterate through each user, ignoring their UID
////        for (Map.Entry<String, Object> entry : users.entrySet()){
////
////            //Get user map
////            Map singleUser = (Map) entry.getValue();
////            System.out.println(singleUser.get("imageurl"));
////            //Get phone field and append to list
////          //  phoneNumbers.add((Long) singleUser.get("phone"));
////        }
////
////        //System.out.println(phoneNumbers.toString());
////    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.map_dashboard);
        initMap();

//        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
////        mDatabase.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                User x=dataSnapshot.getValue(User.class);
////                System.out.println(x);
////                collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                System.out.println("The read failed: " + databaseError.getCode());
////            }
////        });
    }
    private void initMap() {
        Log.d("map", "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync((OnMapReadyCallback) maps.this);
//        LatLngBounds curScreen = googleMap.getProjection()
//                .getVisibleRegion().latLngBounds;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        displayfirebaselocations();


    }
    public void displayfirebaselocations()
    {
        LatLng latLng=new LatLng(18.522694,73.859105);
        Log.d("map", "displayfirebaselocations:"+"pune");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String title=ds.getKey();
                    Log.d("map", "onDataChange: "+title);
                    double latitude=Double.valueOf((Double) ds.child("latitude").getValue());
                    double longitude=Double.valueOf((Double) ds.child("longitude").getValue());
                    // double latitude = Double.parseDouble((String) ds.child("latitude").getValue());
                    //double longitude= Double.parseDouble((String) ds.child("longitude").getValue());
                    Log.d("map", "onDataChange:"+"latitude"+latitude+", longitude"+longitude);
                    String currents = String.valueOf(ds.child("status").getValue());
                    Log.d("map", "onDataChange:"+currents);
                    LatLng latlng =new LatLng(latitude,longitude);
                    if(currents.equals("true"))
                    {
                        Log.d("map", "onDataChange: "+title+" if");
                        MarkerOptions options = new MarkerOptions()
                                .position(latlng)
                                .title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(options);
                    }
                    else
                    {
                        Log.d("map", "onDataChange: "+title);
                        MarkerOptions options = new MarkerOptions()
                                .position(latlng)
                                .title(title)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(options);
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


}
