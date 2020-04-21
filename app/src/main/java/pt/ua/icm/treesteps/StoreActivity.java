package pt.ua.icm.treesteps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoreActivity extends AppCompatActivity {
    DatabaseReference reff;
    Cupom cupom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        LinearLayout mainLayout = findViewById(R.id.parent);
        LayoutInflater inflater = getLayoutInflater();

        FirebaseDatabase.getInstance().getReference().child("Products")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Cupom cupom = snapshot.getValue(Cupom.class);
                            Log.e("StoreActivity: ", cupom.toString());
                            View myLayout = inflater.inflate(R.layout.list_elements, mainLayout, false);
                            TextView name = myLayout.findViewById(R.id.product_name);
                            TextView price = myLayout.findViewById(R.id.product_price);

                            name.setText(cupom.getName());
                            price.setText(""+cupom.getPrice()+" Creds");

                            mainLayout.addView(myLayout);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    public void goToProduct(View view){
        Intent intent = new Intent(this, ProductActivity.class);
        RelativeLayout relativeLayout = (RelativeLayout) view;
        TextView name = relativeLayout.findViewById(R.id.product_name);
        TextView price = relativeLayout.findViewById(R.id.product_price);
        Log.e("StoreActivity: ", "get textview: "+name.getText().toString());
        intent.putExtra("NAME", name.getText().toString());
        intent.putExtra("PRICE", price.getText().toString());
        startActivity(intent);
    }
}
