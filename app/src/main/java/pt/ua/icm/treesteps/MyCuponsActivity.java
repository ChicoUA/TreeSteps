package pt.ua.icm.treesteps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyCuponsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        mAuth = FirebaseAuth.getInstance();

        LinearLayout mainLayout = findViewById(R.id.parent);
        LayoutInflater inflater = getLayoutInflater();

        FirebaseDatabase.getInstance().getReference().child("OwnedCupons").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            BoughtCupom boughtCupom = snapshot.getValue(BoughtCupom.class);
                            Log.e("MyCuponsActivity: ", boughtCupom.toString());

                            if(!boughtCupom.getUsed()) {
                                View myLayout = inflater.inflate(R.layout.list_elements_with_eliminate, mainLayout, false);
                                TextView name = myLayout.findViewById(R.id.product_name);
                                TextView price = myLayout.findViewById(R.id.product_price);

                                name.setText(boughtCupom.getName());
                                price.setText("" + boughtCupom.getPrice() + " Creds");
                                mainLayout.addView(myLayout);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


    public void goToCode(View view){
        Intent intent = new Intent(this, QRCodeActivity.class);
        startActivity(intent);
    }
}
