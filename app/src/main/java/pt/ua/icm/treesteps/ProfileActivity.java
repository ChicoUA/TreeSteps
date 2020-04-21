package pt.ua.icm.treesteps;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    private FirebaseAuth mAuth;
    private TextView startDate;
    private TextView endDate;
    DatabaseReference reff;
    TextView walkD, bikeD;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        /*
        TravelData td = new TravelData();
        reff = FirebaseDatabase.getInstance().getReference().child("TravelData");
        td.setDate("21/04/2020");
        td.setDistance(100);
        td.setTravelType("bike");
        String email = mAuth.getCurrentUser().getUid();
        reff.child(email).push().setValue(td);
        Toast.makeText(this, "Data inserted correctly", Toast.LENGTH_SHORT).show();
        */


        Button selectStartDate = findViewById(R.id.startButton);
        Button selectEndDate = findViewById(R.id.endButton);

        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        selectStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                startDate.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });


        selectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                endDate.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });
    }


    public void searchForData(View view) {
        String sDate = startDate.getText().toString();
        String eDate = endDate.getText().toString();

        if(sDate.equals("Start Date") || eDate.equals("End Date")){
            Toast.makeText(this, "Select both dates", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("TravelData").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int totalDistance = 0;
                        int walkDistance = 0;
                        int bikeDistance = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TravelData td = snapshot.getValue(TravelData.class);
                            Log.e("ProfileActivity: ", td.toString() + " " + sDate + " " + eDate);
                            String[] date = td.getDate().split("/");
                            String[] sDatelist = sDate.split("/");
                            String[] eDateList = eDate.split("/");

                            int startDay = Integer.parseInt(sDatelist[0]);
                            int startMonth = Integer.parseInt(sDatelist[1]);
                            int startYear = Integer.parseInt(sDatelist[2]);

                            int endDay = Integer.parseInt(eDateList[0]);
                            int endMonth = Integer.parseInt(eDateList[1]);
                            int endYear = Integer.parseInt(eDateList[2]);

                            int dataDay = Integer.parseInt(date[0]);
                            int dataMonth = Integer.parseInt(date[1]);
                            int dataYear = Integer.parseInt(date[2]);

                            if(dataDay >= startDay && dataMonth >= startMonth && dataYear >= startYear && dataDay <= endDay && dataMonth <= endMonth && dataYear <= endYear){
                                if(td.getTravelType().equals("walk")){
                                    totalDistance += td.getDistance();
                                    walkDistance += td.getDistance();
                                }
                                else{
                                    totalDistance += td.getDistance();
                                    bikeDistance += td.getDistance();
                                }
                            }




                        }
                        Log.e("ProfileActivity: ", "total: "+totalDistance+" walk: "+walkDistance+" bike: "+bikeDistance);
                        walkD = findViewById(R.id.walkD);
                        bikeD = findViewById(R.id.bikeD);

                        float percentagewalk = (float)walkDistance/(float)totalDistance*100;
                        float percentagebike = (float)bikeDistance/(float)totalDistance*100;

                        walkD.setText("Walk: "+ walkDistance + " - " + percentagewalk + "%");
                        bikeD.setText("Bike: "+ bikeDistance + " - " + percentagebike + "%");


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }
}
