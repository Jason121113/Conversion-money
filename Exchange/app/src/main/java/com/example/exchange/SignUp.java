package com.example.exchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    TextInputLayout regName, regUsername, regEmail, regPhoneNo, regPassword;
    Button regBtn, regToLoginBtn;
    private String id;
    private String Code;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        String url;
        OkHttpClient client = new OkHttpClient();

        mAuth = FirebaseAuth.getInstance();

        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_To_Login_Btn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateName() | !validateUsername() | !validateEmail() | !validatePhoneNo() | !validatePassword()){
                    return;
                }

                new HTTPReqTask().execute();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle("SMS Verification");

                id = UUID.randomUUID().toString();
                id = id.replaceAll("[^\\d.]", "");
                id = id.replaceAll("0", "");
                id = id.substring(0,8);

                // Set up the input
                final EditText input = new EditText(SignUp.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("VERIFY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (m_Text != Code){

                            id = UUID.randomUUID().toString();
                            id = id.replaceAll("[^\\d.]", "");
                            id = id.replaceAll("0", "");
                            id = id.substring(0,6);

                            String DateText = new java.sql.Date(System.currentTimeMillis()).toString();

                            //getting the values
                            String name = regName.getEditText().getText().toString();
                            String username = regUsername.getEditText().getText().toString();
                            String email = regEmail.getEditText().getText().toString();
                            String phoneNo = regPhoneNo.getEditText().getText().toString();
                            String password = regPassword.getEditText().getText().toString();

                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        User user = new User(name,username,email,password,phoneNo,id,DateText, "default", "0", "0", "0", "0");
                                        // Write a message to the database
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(SignUp.this, "You have been registered!", Toast.LENGTH_LONG).show();
                                                        FirebaseAuth.getInstance().signOut();
                                                        startActivity(new Intent(SignUp.this, Login.class));
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignUp.this, "Database Error", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }else{
                                        Toast.makeText(SignUp.this, "Database Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(SignUp.this, "Wrong OTP Code", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });// register Button method end
        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Login.class));

            }
        });

    }// onCreate method end

    //ValidateUsers Here
    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()) {
            regName.setError("Field cannot be empty");
            return false;
        } else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            regUsername.setError("Field cannot be empty");
            return false;
        }else if (val.length()>=15){
            regUsername.setError("Username too long");
            return false;
        }
        else if (!val.matches(noWhiteSpace)){
            regUsername.setError("White Spaces are not allowed");
            return false;
        }
        else {
            regUsername.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)){
            regEmail.setError("Invalid email address");
            return false;
        }
        else {
            regEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = regPhoneNo.getEditText().getText().toString();
        if (val.isEmpty()) {
            regPhoneNo.setError("Field cannot be empty");
            return false;
        } else {
            regPhoneNo.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        // String passwordVal="^"+
        //   "(?=.*[0-9])"+            // at least1digit
        //  "(?=.*[a-z])"+            // at least1lower case letter
        // "(?=.*[A-Z])"+            // at least1upper case letter
        //  "(?=.*[a-zA-Z])"+           // any letter
        // "(?=.*[@#$%^&+=])"+         // at least1special character
        // "\\A\\w{4,20}\\z"+          // no white spaces
        // ".{4,}"+                    // at least4characters
        // "S";

        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        }  //else if (!val.matches(passwordVal)){
        // regPassword.setError("Password is to weak");
        // return false;
        //  }
        else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    public class HTTPReqTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            Code = new DecimalFormat("000000").format(new Random().nextInt(999999));

            try {
                URL url = new URL("http://122.54.191.90:8085/goip_send_sms.html?username=root&password=root&port=2&recipients="+regPhoneNo.getEditText().getText().toString()+"&sms="+ URLEncoder.encode("Hello ka exchange "+regName.getEditText().getText().toString()+"! This is your OTP code for Registration: "+Code));
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }

}
