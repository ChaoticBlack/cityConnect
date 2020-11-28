package com.example.camera1;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.ContextWrapper;
import android.content.Context;

import com.example.camera1.ml.Model1;
//import com.example.camera1.ui.login.adminLogin;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {
    Interpreter interpreter;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS=1235;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;
    static @NonNull List<Category> probability;
    static double userLat;
    static double userLong;
    static int n;
    //public String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

    public static class User {


        public double longitude;
        public double latitude;
        public String status,imageurl;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(double latitude, double longitude,String status,String imageurl) {
            this.latitude=latitude;
            this.longitude=longitude;
            this.status=status;
            this.imageurl=imageurl;
        }
    }
//    private void writeNewUser(String userId, String name, String email) {
//        User user = new User(name, email);
//
//        mDatabase.child("users").child(userId).setValue(user);
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent in;
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    return true;
                }
                case R.id.map_dashboard: {

                    in=new Intent(getBaseContext(),maps.class);
                    startActivity(in);
                    return true;
                }
                case R.id.rate_us:
                    Toast.makeText(getApplicationContext(),"Review",Toast.LENGTH_LONG).show();
                    return true;
            }
            return false;
        }
    };
    public String pictureFilePath;
    private static Context context;
    public Uri filePath;
    Bitmap photo;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    LatLng latLng;
// ...


    public static Context getAppContext() {
        return MainActivity.context;
    }

    static final int REQUEST_IMAGE_CAPTURE=1;
    ImageView myimageview;
    @Override
    //oncreate method
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);

        //Context context;
        //context=GetHelp.this;
//         LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
//                LOCATION_REFRESH_DISTANCE, mLocationListener);
        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_home);
        if(isServicesOK())
        {
            if(isGpsEnabled())
            {
                getLocationPermission();
            }
            else
            {
                Log.d(TAG, "AllServices:"+"request for gps");
                buildAlertMessageNoGps();
            }
        }
        else
        {
            Log.d(TAG, "AllServices:"+"google play services are not available ");
        }



        // isMapsEnabled();
        //getLocationPermission();
        //getDeviceLocation();
        Button x=(Button) findViewById(R.id.button);
        myimageview=(ImageView) findViewById(R.id.imageView);
        Log.i("adhi","adhi");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.i("adhi1","adhi1");
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }






    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public boolean isGpsEnabled()
    {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            //buildAlertMessageNoGps();
            Log.d(TAG, "isGpsEnabled:"+"not enable");
            return false;
        }
        return true;

    }
    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ENABLE_GPS:
                {
                    Log.d(TAG, "onActivityResult:"+"GPS enable ");
                    getLocationPermission();
                     //getDeviceLocation();
                }
            case 1:
            {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.getData()==null) {
                    Bundle extras = data.getExtras();

                    photo = (Bitmap) extras.get("data");
                    photo = Bitmap.createScaledBitmap(photo, 300, 300, true);

                    try {
                        Model1 model = Model1.newInstance(this);

                        // Creates inputs for reference.
                        TensorImage image = TensorImage.fromBitmap(photo);

                        // Runs model inference and gets result.
                        Model1.Outputs outputs = model.process(image);
                        probability = outputs.getProbabilityAsCategoryList();

                        Log.d(TAG, "onActivityResult:" + probability.get(0).getScore());
                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {
                        // TODO Handle the exception
                    }
                    if (probability.get(0).getScore() > 0.51) {
                        new AlertDialog.Builder(this)
                                .setTitle("Stupid Image")
                                .setMessage("Are you blind?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
                                    }
                                }).setNegativeButton("", null).show();

                    } else {
//        boolean output = doInference(photo);
//        if(output)
//            Log.i("log","z" );
//        else
//            Log.i("log", "y");

                        filePath = getImageUri(getApplicationContext(), photo);
                        if (filePath != null)
                            Log.i("hi", "bye");
                        myimageview.setImageBitmap(photo);
                        MediaStore.Images.Media.insertImage(getContentResolver(), photo, "xyzabc", "timepass");
                        Log.i("Takla", "takal");
//        ActivityCompat.requestPermissions(MainActivity.this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                1);
//        ActivityCompat.requestPermissions(MainActivity.this,
//                new String[]{Manifest.permission.INTERNET},
//                1);
                        Log.i("saved", "iamge");
                        uploadImage();
                        Random rand = new Random();
                        n = rand.nextInt(50000);
                        new AlertDialog.Builder(this)
                                .setTitle("Complaint Registered")
                                .setMessage("Thank you Citizen. Your Complaint Code is " + n)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
                                    }
                                }).setNegativeButton("", null).show();

                    }
                }


            }
        }

    }



//    private void getLocationPermission()
//    {
//        Log.d(TAG, "getLocationPermission: getting location permissions");
//        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION};
//
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//            {
//                mLocationPermissionsGranted = true;
//                Log.d(TAG, "getLocationPermission:"+"location permission Granted");
//                // initMap();
//            }
//            else
//            {
//                Log.d(TAG, "getLocationPermission:"+"request for location permission");
//                Log.d(TAG, "getLocationPermission:"+"firstelse");
//                ActivityCompat.requestPermissions(this,
//                        permissions,
//                        LOCATION_PERMISSION_REQUEST_CODE);
//                //finish();
//            }
//        }
//        else
//        {
//            Log.d(TAG, "getLocationPermission:"+"secondelse");
//            ActivityCompat.requestPermissions(this,
//                    permissions,
//                    LOCATION_PERMISSION_REQUEST_CODE);
////                    finish();}
//           // finish();
//        }
//    }

    private void getLocationPermission() {

        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionsGranted = true;
            Log.d(TAG, "getLocationPermission:"+"location permission Granted");
            getDeviceLocation();
        }
        else
        {
            Log.d(TAG, "getLocationPermission:"+"asking for location permission");
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    getDeviceLocation();
                } else {
                    mLocationPermissionsGranted = false;
                    Log.d(TAG, "onRequestPermissionsResult: permission Denied");
                    return;
                }
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < grantResults.length; i++) {
//                        Log.d(TAG, "onRequestPermissionsResult:" + grantResults[i]);
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission Denied");
//                            return;
//                        }
//
//                    }
//                    mLocationPermissionsGranted = true;
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//
//                    getDeviceLocation();
//                }
            }
        }

    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
//        mLocationPermissionsGranted = false;
//
//        switch(requestCode){
//            case LOCATION_PERMISSION_REQUEST_CODE:{
//                if(grantResults.length > 0){
//                    for(int i = 0; i < grantResults.length; i++)
//                    {
//                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
//                        {
//                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission Denied");
//                            return;
//                        }
//
//                    }
//                    mLocationPermissionsGranted = true;
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                    getDeviceLocation();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run()
//                        {
//                            getDeviceLocation();
//                        }
//                    }, 3000);
//
//
//                }
//            }
//        }
//    }
//    private void getDeviceLocation()
//    {
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        try {
//            if (mLocationPermissionsGranted)
//            {
//
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return ;
//                }
//                Log.d(TAG, "onComplete: get location");
//                Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener(){
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if(task.isSuccessful())
//                        {
//                            Log.d(TAG, "onComplete: found Location!");
//                            currentLocation = (Location) task.getResult();
//                            //Log.d(TAG, "onComplete:"+currentLocation.getLatitude());
//
//                            latLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
//                            Log.d(TAG, "onComplete:"+latLng.latitude);
//                            //moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),DEFAULT_ZOOM, "your Query");
//                            //mDatabase.child("location_info").child("1").setValue(new User(latLng.latitude, latLng.longitude,"true","imageurl"));
//                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
//                            //displayfirebaselocations();
//                        }
//                        else{
//                            Log.d(TAG, "onComplete: current location is null");
//                            Toast.makeText(getApplicationContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
//                            Intent i=new Intent(getApplicationContext(),MainActivity.class);
//                            startActivity(i);
//
//                        }
//                    }
//                });
//            }
//        }catch (SecurityException e){
//            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
//        }
//
//    }
private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    try {
        if (mLocationPermissionsGranted) {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful())
                    {
                        // Set the map's camera position to the current location of the device.
                        Log.d(TAG, "onComplete: found Location!");
                        currentLocation = (Location) task.getResult();
                        if (currentLocation != null)
                        {
                            latLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            Log.d(TAG, "onComplete:"+latLng.latitude);
                            userLat = currentLocation.getLatitude();
                            userLong = currentLocation.getLongitude();
                        }
                        else
                        {

                            Log.d(TAG, "onComplete:"+"location is null");
                            requestNewLocationData();
                        }
                    }
                    else
                    {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getApplicationContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);

                    }
                }
            });
        }
    } catch (SecurityException e)  {
        Log.e("Exception: %s", e.getMessage(), e);
    }
}
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback()
    {

        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            Location mLastLocation = locationResult.getLastLocation();
            latLng=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            userLat = mLastLocation.getLatitude();
            userLong = mLastLocation.getLongitude();
            Log.d(TAG, "onLocationResult:"+"shivam is great");
            Log.d(TAG, "onComplete:"+latLng.latitude);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void cameracapture(View view)
    {
        Intent i= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,REQUEST_IMAGE_CAPTURE);
    }
    public void adminlog(View view)
    {
        Intent i1=new Intent(getBaseContext(), adminLogin.class);
        startActivity(i1);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        FirebaseStorage storage = FirebaseStorage.getInstance();
//    if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.getData()==null) {
//        Bundle extras = data.getExtras();
//
//        photo = (Bitmap) extras.get("data");
////        boolean output = doInference(photo);
////        if(output)
////            Log.i("log","z" );
////        else
////            Log.i("log", "y");
//
//        filePath = getImageUri(getApplicationContext(), photo);
//        if(filePath!=null)
//            Log.i("hi","bye");
//        myimageview.setImageBitmap(photo);
//        MediaStore.Images.Media.insertImage(getContentResolver(), photo, "taklus", "timepass");
//        Log.i("Takla","takal");
////        ActivityCompat.requestPermissions(MainActivity.this,
////                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
////                1);
////        ActivityCompat.requestPermissions(MainActivity.this,
////                new String[]{Manifest.permission.INTERNET},
////                1);
//        Log.i("saved", "iamge");
//        uploadImage();
//        new AlertDialog.Builder(this)
//                .setTitle("Complaint Registered")
//                .setMessage("Thank you Citizen. Your Complaint Code is 00000")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
//                    }
//                }).setNegativeButton("", null).show();
//
//   }
//
//
//
//    }

public Uri getImageUri(Context inContext, Bitmap inImage) {
    Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 300, 300,true);
    String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "shiVAM", null);
    return Uri.parse(path);
}


//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            //your code here
//            currentLocation=location;
//        }
//    };



private void uploadImage() {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());
    final StorageReference riversRef = storageReference.child("images/"+ currentDateandTime + " rivers3.jpg");
    UploadTask uploadTask= riversRef.putFile(filePath);
//    riversRef.putFile(filePath)
//            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // Get a URL to the uploaded content
//                      //Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                      String x1=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                      Log.i("hhhhhhh",x1);
//                    Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
//                    //Snackbar.make(findViewById(R.id.content),"Image Uploaded",Snackbar.LENGTH_LONG).show();
//                }
//            })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    // ...
//                    Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
//                }
//            });
    Task<Uri> urlTask = uploadTask.continueWithTask(new
                                                            Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                                @Override
                                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                                    if (!task.isSuccessful()) {
                                                                        throw task.getException();
                                                                    }

                                                                    // Continue with the task to get the download URL
                                                                    return riversRef.getDownloadUrl();
                                                                }
                                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Log.i("hhhhhhh",downloadUri.toString());
                Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
                //Random rand = new Random();

// Obtain a number between [0 - 49].
                //int n = rand.nextInt(50000);
                Log.d(TAG, "onComplete:"+"jnjn");
                mDatabase.child("users").child(String.valueOf(n)).setValue(new User(latLng.latitude,latLng.longitude,"false",downloadUri.toString()));  //false= unresloved complait

            }
            else {
                // Handle failures
                // ...
                showInternetDialog();
                Log.d(TAG, "onComplete:"+"soham is great");
            }
        }
    });

    //Sunanda


}
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null;
//    }
    public void showInternetDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connect to wifi or quit")
                .setCancelable(false)
                .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Mobile-Data", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private MappedByteBuffer loadModelFile() throws IOException
    {
        //InputStream inputStream = getAssets().open("model.tflite");
        //byte[] model = new byte[inputStream.available()];
        //inputStream.read(model);
        //ByteBuffer buffer = ByteBuffer.allocateDirect(100)
        //        .order(ByteOrder.nativeOrder());
        //buffer.put(model);
        //return buffer;

        AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public boolean doInference(Bitmap photo){
        Bitmap bitmap = Bitmap.createScaledBitmap(photo, 300, 300, true);
        int batchNum = 0;
        float[][][][] input = new float[1][300][300][3];
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                int pixel = bitmap.getPixel(x, y);

                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = Color.red(pixel) ;
                input[batchNum][x][y][1] = Color.green(pixel) ;
                input[batchNum][x][y][2] = Color.blue(pixel) ;
            }
        }
        float [][] output = new float[1][2];
        interpreter.run(input, output);
        if (output[0][1] >= 0.5) {
            return true ;
        }
        else{
            return false;
        }
    }



    /**
     * Created by Ilya Gazman on 3/6/2016.
     */

}
