package pt.ua.icm.treesteps;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "Login Activity:";
    private EditText email;
    private EditText password;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user != null){
                    Log.d(TAG, "User is signed in: "+user.getUid());
                }
                else{
                    Log.d(TAG, "User is signed out");
                }

                updateUI(user);

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createUser(String email, String password) {
        Log.d(TAG, "Create Account: " + email);
        if (!validateForm()) {
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

        private void signIn(String email, String password){
        Log.d(TAG, "Signing in: "+ email);
        if(!validateForm()){
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,  new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });

    }

    private void signOut(){
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm(){
        boolean valid = true;

        String emailValue = email.getText().toString();
        if(TextUtils.isEmpty(emailValue)){
            email.setError("Required!");
            valid = false;
        }

        else{
            email.setError(null);
        }

        String passwordValue = password.getText().toString();
        if(TextUtils.isEmpty(passwordValue)){
            password.setError("Required!");
            valid = false;
        }
        else{
            password.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user){
        if(user == null){
            Toast.makeText(LoginActivity.this, "No User", Toast.LENGTH_SHORT).show();
        }
        else{
            String emailText = user.getEmail();
            String uid = user.getUid();
            Toast.makeText(LoginActivity.this, "email: "+emailText+" - uid: "+uid, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        }

    }


    public void signIn(View view) {
        signIn(email.getText().toString(), password.getText().toString());
    }

    public void signUp(View view) {
        createUser(email.getText().toString(), password.getText().toString());
    }
}
