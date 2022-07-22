package com.example.exchange;

import static android.content.ContentValues.TAG;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText,sloganText;
    TextInputLayout username,password;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        executor = ContextCompat.getMainExecutor(this);
        mAuth = FirebaseAuth.getInstance();
        //do not delete

        //KA BA LI WW HOOKS  HA HA HA HA
        callSignUp = findViewById(R.id.signup_Screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);


        biometricPrompt = new BiometricPrompt(Login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Login success!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(Login.this, MainActivity.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use fingerprint to login.")
                .setNegativeButtonText("Use password")
                .build();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            biometricPrompt.authenticate(promptInfo);
        }

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, SignUp.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(username, "username_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });
        //onclick
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUser();
            }
        });
    }

    private void isUser() {
        final   String userEnteredUsername = username.getEditText().getText().toString().trim();
        final   String userEnteredPassword = password.getEditText().getText().toString().trim();


        if (userEnteredUsername.isEmpty()){
            username.setError("Email is required!");
            username.requestFocus();
            Toast.makeText(Login.this, "Username Is Required", Toast.LENGTH_LONG).show();
            return;
        }

        if (userEnteredPassword.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            Toast.makeText(Login.this, "Password is required", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEnteredUsername, userEnteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String uid = "";
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user != null) {
                        uid = user.getUid();
                    }
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(uid)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    } else {
                                        Log.d(TAG, "Cached get failed: ", task.getException());
                                    }
                                }
                            });
                }else{
                    Toast.makeText(Login.this, "Wrong Credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
