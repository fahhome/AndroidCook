package com.homecookssec35.androidcook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginUserActivity extends AppCompatActivity {

    TextView textviewsignup ;
    TextView editTextEmail;
    TextView editTextPassword;
    Button buttonLogin ;
    FirebaseAuth mAuth ;
    private static String tag =  "fahmidloginactivity";
    private void signinuser(){

        String emailuser = editTextEmail.getText().toString().trim();
        String passworduser = editTextPassword.getText().toString().trim();

        if(emailuser.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailuser).matches()){
            editTextEmail.setError("Please enter valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(passworduser.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        //Minimum length of password for firebase auth is 6 .
        if(passworduser.length() < 6){
            editTextPassword.setError("Minimum length required is 6");
            editTextPassword.requestFocus();
            return ;
        }

        mAuth.signInWithEmailAndPassword(emailuser,passworduser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    Intent intent5 =  new Intent(LoginUserActivity.this,Profile.class);
                    intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent5);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        editTextEmail = (TextView)findViewById(R.id.editTextEmail);
        editTextPassword =  (TextView)findViewById(R.id.editTextPassword);
        textviewsignup = (TextView)findViewById(R.id.textViewSignup);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        mAuth = FirebaseAuth.getInstance();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinuser();
            }
        });
        Log.d(tag,"after postonexecute");
        textviewsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 =  new Intent(LoginUserActivity.this,SignUpActivity.class);
                Log.d(tag,"starting signup activity ");
                startActivity(intent4);
            }
        });
    }
}
