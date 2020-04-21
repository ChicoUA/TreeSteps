package pt.ua.icm.treesteps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    BroadcastReceiver broadcastReceiver;
    private TextView txtActivity;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    DatabaseReference reff;
    public boolean exists=true;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    TextView userCredits;
    TextView walkToday;
    TextView bikeToday;
    String label = "";
    TravelPoint newPoint = null;
    TravelPoint lastPoint = null;
    int currentCreds = 0;
    double dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        mAuth = FirebaseAuth.getInstance();

        getLastPoint();
        getData();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        txtActivity = findViewById(R.id.textactivity);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        startTracking();

        checkIfUserExists();

        if(!exists){
            Log.e(TAG, "Creating new user");
            saveUser();
        }

    }

    public void checkIfUserExists(){
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.e(TAG, user.toString());
                            if(mAuth.getCurrentUser().getEmail().equals(user.getEmail())){
                                exists = true;
                                Log.e(TAG, "User exists");
                                break;
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        exists = false;
                    }
                });
    }

    public void saveUser(){
        User user = new User();
        reff = FirebaseDatabase.getInstance().getReference().child("Users");
        user.setEmail(mAuth.getCurrentUser().getEmail());
        user.setCreds(1000);
        String email = mAuth.getCurrentUser().getUid();
        reff.child(email).setValue(user);
        Toast.makeText(this, "Data inserted correctly", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
                signOut();
                return true;
            case R.id.store:
                Intent intent = new Intent(MainActivity.this, StoreActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent1);
                return true;
            case R.id.my_cupons:
                Intent intent2 = new Intent(MainActivity.this, MyCuponsActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleUserActivity(int type, int confidence) {
        label = getString(R.string.activity_unknown);

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.activity_on_bicycle);
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.activity_on_foot);
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                break;
            }
            case DetectedActivity.TILTING: {
                label = getString(R.string.activity_tilting);
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                break;
            }
        }

        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence + ", " + mAuth.getCurrentUser().getEmail());

        if (confidence > Constants.CONFIDENCE) {
            if (label.equals("On Foot") || label.equals("Still") || label.equals("Running") || label.equals("walking") || label.equals("On Bycicle")) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                    Log.e(TAG, "Permission for location not granted");
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.e(TAG, "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude());
                                    TravelPoint travelPoint = new TravelPoint();
                                    travelPoint.setLatitude(location.getLatitude());
                                    travelPoint.setLongitude(location.getLongitude());
                                    travelPoint.setTravelType(label);
                                    Log.e(TAG, travelPoint.toString());

                                    if(lastPoint == null){
                                        lastPoint = travelPoint;
                                    }
                                    else{
                                        calculateDistanceAndSave(lastPoint, travelPoint);
                                    }

                                    reff = FirebaseDatabase.getInstance().getReference().child("LastPoint").child(mAuth.getCurrentUser().getUid());
                                    reff.setValue(travelPoint);
                                }
                            }
                        });

            }
        }
    }

    private void calculateDistanceAndSave(TravelPoint lastPoint, TravelPoint travelPoint) {
        walkToday = findViewById(R.id.walkToday);
        bikeToday = findViewById(R.id.bikeToday);
        userCredits = findViewById(R.id.userCredits);

        TravelData travelData = new TravelData();
        double lastLatitude = lastPoint.getLatitude();
        double lastLongitude = lastPoint.getLongitude();
        double newLatitude = travelPoint.getLatitude();
        double newLongitude = travelPoint.getLongitude();

        double theta = lastLongitude - newLongitude;
        dist = Math.sin(deg2rad(lastLatitude))
                * Math.sin(deg2rad(newLatitude))
                + Math.cos(deg2rad(lastLatitude))
                * Math.cos(deg2rad(newLatitude))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1000;

        if(travelPoint.getTravelType().equals("On Foot") ||  travelPoint.getTravelType().equals("Running") || travelPoint.getTravelType().equals("walking") && (int)dist > 10){
            travelData.setTravelType("walk");
            travelData.setDistance((int)dist);
            String date = dayOfMonth+"/"+month+"/"+year;
            travelData.setDate(date);
            reff = FirebaseDatabase.getInstance().getReference().child("TravelData").child(mAuth.getCurrentUser().getUid());
            reff.push().setValue(travelData);
            reff = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    int creds = user.getCreds();
                    int obtained_creds = (int)dist/10;
                    user.setCreds(obtained_creds+creds);
                    dataSnapshot.getRef().setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if(travelPoint.getTravelType().equals("On Bycicle") && (int)dist > 100){
            travelData.setTravelType("bike");
            travelData.setDistance((int)dist);
            String date = dayOfMonth+"/"+month+"/"+year;
            travelData.setDate(date);
            reff = FirebaseDatabase.getInstance().getReference().child("TravelData").child(mAuth.getCurrentUser().getUid());
            reff.push().setValue(travelData);
            reff = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    int creds = user.getCreds();
                    int obtained_creds = (int)dist/10;
                    user.setCreds(obtained_creds+creds);
                    dataSnapshot.getRef().setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    private void startTracking() {
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }

    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void getData(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userCredits = findViewById(R.id.userCredits);
                        userCredits.setText(""+user.getCreds());

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        exists = false;
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("TravelData").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        walkToday = findViewById(R.id.walkToday);
                        bikeToday = findViewById(R.id.bikeToday);
                        int walkDist = 0;
                        int bikeDist = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TravelData td = snapshot.getValue(TravelData.class);
                            Log.e(TAG, td.toString() + "day: "+dayOfMonth + "montth "+month+ "year "+year);
                            String[] tdDateList = td.getDate().split("/");
                            int tdDay = Integer.parseInt(tdDateList[0]);
                            int tdMonth = Integer.parseInt(tdDateList[1]);
                            int tdYear = Integer.parseInt(tdDateList[2]);

                            if(tdDay == dayOfMonth && tdMonth == month && tdYear == year){
                                if(td.getTravelType().equals("walk")){
                                    walkDist += td.getDistance();
                                }
                                else{
                                    bikeDist += td.getDistance();
                                }
                            }

                        }
                        walkToday.setText("Walking: \n"+ walkDist+"m");
                        bikeToday.setText("Bike Riding: \n" + bikeDist + "m");

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public synchronized void getLastPoint(){

        FirebaseDatabase.getInstance().getReference().child("LastPoint").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lastPoint = dataSnapshot.getValue(TravelPoint.class);
                        if(lastPoint == null) {
                            Log.e(TAG, "Last Point: null");
                        }
                        else{
                            Log.e(TAG, "Last Point: " + lastPoint.toString());
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
