package pt.ua.icm.treesteps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.w3c.dom.Text;

public class ProductActivity extends AppCompatActivity {
    private TextView cupomName;
    private TextView cupomPrice;
    private TextView ownedCreds2;
    private String name;
    private String price;
    DatabaseReference reff;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        cupomName = findViewById(R.id.cupom_name);
        cupomPrice = findViewById(R.id.cupom_price);
        ownedCreds2 = findViewById(R.id.creds_owned);
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.e("OI:", user.toString());
                            ownedCreds2.setText("Wallet: "+user.getCreds()+" creds");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            name = (String) extras.get("NAME");
            price = (String) extras.get("PRICE");
        }

        cupomPrice.setText(price);
        cupomName.setText(name);
    }

    public void buyCupom(View view) {

        int ownedCreds = Integer.parseInt(ownedCreds2.getText().toString().split(" ")[1]);
        if (ownedCreds >= Integer.parseInt(cupomPrice.getText().toString().split(" ")[0])) {
            saveCupom();
            reff = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            reff.child("creds").setValue(ownedCreds - Integer.parseInt(cupomPrice.getText().toString().split(" ")[0]));
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Insufficent Credits " + ownedCreds, Toast.LENGTH_SHORT).show();
        }

    }

    public void saveCupom(){
        BoughtCupom cupom = new BoughtCupom();
        reff = FirebaseDatabase.getInstance().getReference().child("OwnedCupons");
        cupom.setPrice(Integer.parseInt(cupomPrice.getText().toString().split(" ")[0]));
        cupom.setName(cupomName.getText().toString());
        String email = mAuth.getCurrentUser().getUid();
        reff.child(email).push().setValue(cupom);
        Toast.makeText(this, "Data inserted correctly", Toast.LENGTH_SHORT).show();
    }

}
