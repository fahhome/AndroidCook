package com.homecookssec35.androidcook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextUsername,editTextPassword ;
    ProgressBar progb ;
    Button  buttonsignup ;
    TextView textviewlogin ;
    private FirebaseAuth mAuth;
    DatabaseReference fahmid ;

    private  void registerUser(){
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        progb.setVisibility(View.VISIBLE);
        if(username.isEmpty()){
            editTextUsername.setError("Email is required");
            editTextUsername.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            editTextUsername.setError("Please enter valid email");
            editTextUsername.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        //Minimum length of password for firebase auth is 6 .
        if(password.length() < 6){
            editTextPassword.setError("Minimum length required is 6");
            editTextPassword.requestFocus();
            return ;
        }

        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"User Registered Successfully",Toast.LENGTH_LONG).show();


                    mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                fahmid = FirebaseDatabase.getInstance().getReference();
                                UserOrderingDetail newobj = new UserOrderingDetail();
                                newobj.setAddress("");
                                newobj.setAmount("");
                                newobj.setCurrentorder("");
                                newobj.setMobile("");
                                newobj.setName("");
                                newobj.setStatus("");
                                newobj.setEmailid(mAuth.getCurrentUser().getEmail());
                                fahmid.child(mAuth.getCurrentUser().getUid()).setValue(newobj);

                                Toast.makeText(getApplicationContext(),"User Signed in Successfully",Toast.LENGTH_LONG).show();
                                progb.setVisibility(View.GONE);
                                Intent intent4 =  new Intent(SignUpActivity.this,Profile.class);
                                startActivity(intent4);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"User sign in failed ",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else{
                    progb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext() , task.getException().toString() ,Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        editTextUsername = (EditText)findViewById(R.id.editTextEmail);
        progb = (ProgressBar)findViewById(R.id.progressbar3);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonsignup = (Button)findViewById(R.id.buttonSignUp);
        textviewlogin = (TextView)findViewById(R.id.textViewLogin);
        textviewlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent3 =  new Intent(SignUpActivity.this,LoginUserActivity.class);
                startActivity(intent3);

            }
        });
        buttonsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
}
