package com.example.camera1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class admin_control_panel extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG ="MainActivity" ;
    private GoogleMap mMap ;
    private static final float DEFAULT_ZOOM = 10f;
    private CheckBox c;
    private ArrayList<Integer> query_id=new ArrayList<Integer>();
    RecyclerView r;
    com.example.camera1.adapter1 a;
    ArrayList<String> items,items1,images;
    ArrayList<LatLng> locations;
    //LatLngBounds curScreen;


    public static class User
    {

        public String status,imageurl;
        public double  longitude,latitude;
        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(double latitude, double longitude, String status,String iamageurl) {
            this.latitude =latitude;
            this.longitude=longitude;
            this.status=status;
            this.imageurl=iamageurl;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control_panel);
        r=(RecyclerView) findViewById(R.id.re);
        r.setLayoutManager(new LinearLayoutManager(this));


        initMap();

        items1=new ArrayList<>();


    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync((OnMapReadyCallback) admin_control_panel.this);
//        LatLngBounds curScreen = googleMap.getProjection()
//                .getVisibleRegion().latLngBounds;
    }
    private LatLngBounds curScreen;
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        curScreen = googleMap.getProjection()
                .getVisibleRegion().latLngBounds;
//        Log.i("sd", curScreen);
        System.out.println("hiiiiiiiiii"+curScreen);
        VisibleRegion vr = mMap.getProjection().getVisibleRegion();
        displayfirebaselocations();

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                items=new ArrayList<>();
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                curScreen = googleMap.getProjection().getVisibleRegion().latLngBounds;
//        Log.i("sd", curScreen);

                System.out.println("hiiiiiiiiii"+curScreen);
                for(int i=0;i<items1.size();i++)
                {
                    if(curScreen.contains(locations.get(i)))
                    {
                        items.add(items1.get(i));
                    }
                }
                a=new com.example.camera1.adapter1( admin_control_panel.this,items);
                r.setAdapter(a);
                a.notifyDataSetChanged();



            }
        });


    }

    private void displayfirebaselocations()
    {
        LatLng latLng=new LatLng(18.522694,73.859105);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mMap.clear();

                query_id=new ArrayList<Integer>();
                items1=new ArrayList<>();
                locations=new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String title=ds.getKey();
                    int key=Integer.parseInt(title);
                    Log.d(TAG, "onDataChange:"+title);
//                    query_id.add(key);
                    String currents = String.valueOf(ds.child("status").getValue());

                    if(currents.equals("false")) {



                        double latitude = Double.valueOf((Double) ds.child("latitude").getValue());
                        double longitude = Double.valueOf((Double) ds.child("longitude").getValue());
                        Log.d(TAG, "onDataChange:" + "latitude" + latitude + ", longitude" + longitude);
                        LatLng latlng = new LatLng(latitude, longitude);
                        if (true) {
                            items1.add(title);
                            locations.add(latlng);
                            MarkerOptions options = new MarkerOptions()
                                    .position(latlng)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            mMap.addMarker(options);
                        }
                    }
                }
                a=new com.example.camera1.adapter1( admin_control_panel.this,items1);
                r.setAdapter(a);
                a.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

    }
}