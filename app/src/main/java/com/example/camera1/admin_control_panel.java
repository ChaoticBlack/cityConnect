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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
//        Log.d(TAG, "onCreate:"+query_id.size());
//        for(int i=0;i<query_id.size();i++)
//        {
//            Log.d(TAG, "onCreate: "+ query_id.get(i));
//        }
//        c1=findViewById(R.id.temp);
//        c2=findViewById(R.id.temp1);
//        c3=findViewById(R.id.temp2);
//        items=new ArrayList<>();
        items1=new ArrayList<>();
//        items.add("jskhd");
//        items.add("jskhd");
//        items.add("jskhd");
//        items.add("jskhd");
//        items.add("jskhd");

//        items1=new ArrayList<>();
//        items1.add("jdfxcskhd");
//        items1.add("jskhdsdsa");
//        items1.add("jskhsacxzd");
//        items1.add("jsaskhd");
//        items1.add("jskhdsd");
//        images=new ArrayList<>();

//        images.add("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers.jpg?alt=media&token=86ccc570-11c0-4bdb-aa99-fb85a4c401f8");
//        images.add("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers1.jpg?alt=media&token=6983874c-b439-49b8-91aa-80604cf91072");
//        images.add("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers1.jpg?alt=media&token=6983874c-b439-49b8-91aa-80604cf91072");
//        images.add("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers2.jpg?alt=media&token=814d8bdd-758b-4e30-9fab-3eb910df6309");
//        images.add("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers3.jpg?alt=media&token=658e6b32-3504-4a96-a5c6-d7bc0a279358");




//        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
//            {
//                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                String text=c1.getText().toString();
//                if (isChecked)
//                {
//
//                    Toast.makeText(MainActivity.this, "C1 is checked", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "ischeck: "+text);
//                    mDatabase.child("location_info").child(text).child("status").setValue("false");
//                }
//                else
//                {
//                    mDatabase.child("location_info").child(text).child("status").setValue("true");
//                }
//            }
//        });
//        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
//            {
//                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                String text=c2.getText().toString();
//                if (isChecked)
//                {
//                    Toast.makeText(MainActivity.this, "C1 is checked", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "ischeck: "+text);
//                    mDatabase.child("location_info").child(text).child("status").setValue("false");
//                }
//                else
//                {
//                    mDatabase.child("location_info").child(text).child("status").setValue("true");
//                }
//            }
//        });
//        c3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
//            {
//                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                String text=c3.getText().toString();
//                if (isChecked)
//                {
//                    Toast.makeText(MainActivity.this, "C1 is checked", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "ischeck: "+text);
//                    mDatabase.child("location_info").child(text).child("status").setValue("false");
//                }
//                else
//                {
//                    mDatabase.child("location_info").child(text).child("status").setValue("true");
//                }
//            }
//        });

    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync((OnMapReadyCallback) admin_control_panel.this);
//        LatLngBounds curScreen = googleMap.getProjection()
//                .getVisibleRegion().latLngBounds;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        displayfirebaselocations();


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
                query_id=new ArrayList<Integer>();
                items1=new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String title=ds.getKey();
                    int key=Integer.parseInt(title);
                    Log.d(TAG, "onDataChange:"+title);
//                    query_id.add(key);
                    String currents = String.valueOf(ds.child("status").getValue());

                    if(currents.equals("true")) {


                        items1.add(title);
                        double latitude = Double.valueOf((Double) ds.child("latitude").getValue());
                        double longitude = Double.valueOf((Double) ds.child("longitude").getValue());
                        Log.d(TAG, "onDataChange:" + "latitude" + latitude + ", longitude" + longitude);
                        LatLng latlng = new LatLng(latitude, longitude);
                        if (currents.equals("true")) {
                            MarkerOptions options = new MarkerOptions()
                                    .position(latlng)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            mMap.addMarker(options);
                        } else {
                            Log.d(TAG, "onDataChange: " + title);
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
//                if (items1.isEmpty())
//                    r.setVisibility(View.VISIBLE);
//                else
//                    r.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
}