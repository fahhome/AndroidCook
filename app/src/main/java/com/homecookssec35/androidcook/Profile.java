package com.homecookssec35.androidcook;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Profile extends AppCompatActivity implements PaymentResultListener {

    EditText cateringorder ;
    EditText prevorder ;
    Button submitorderbtn;
    DatabaseReference  databaseusers , databaseusers2 , databaseusers3 ;
    DatabaseReference  userorder , userorder2 , userorder3 , userorder4, userorder5 ;
    DatabaseReference fahmid3 , fahmid4 , fahmid5 ;
    Button paybtn ;
    EditText phone,address , members , afterhours;
    TextView status , amount ;

    private static String tag = "fahmidprofileactivity";
    String cateringorderstr = " " ;

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menulogout :
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
            break;
            case R.id.menuhelp :
                startActivity(new Intent(this,Help.class));
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cateringorder = (EditText)findViewById(R.id.editorderText);
        prevorder = (EditText)findViewById(R.id.editText2);
        members = (EditText)findViewById(R.id.editText8);
        afterhours = (EditText)findViewById(R.id.editText9);
        phone = (EditText)findViewById(R.id.editText);
        address = (EditText)findViewById(R.id.editText6);
        status = (TextView) findViewById(R.id.editText5);
        cateringorderstr = cateringorder.getText().toString();
        amount = (TextView)findViewById(R.id.textView20);
        amount.setVisibility(View.INVISIBLE);
      //  Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(tag, "" + currentFirebaseUser.getUid());
       // Toast.makeText(this,"" + currentFirebaseUser.getUid(),Toast.LENGTH_LONG ).show();
        submitorderbtn = (Button)findViewById(R.id.submitorederbutton);
        paybtn = (Button)findViewById(R.id.button9);
        paybtn.setEnabled(false);
        Log.d(tag , cateringorderstr);
        Log.d(tag , " outside click ");

        userorder =  FirebaseDatabase.getInstance().getReference() ;
        userorder = userorder.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentorder");
        userorder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(tag , "inside on datachange");
                String orderstr =  dataSnapshot.getValue(String.class);
                //Toast.makeText(getApplicationContext() , "" + orderstr , Toast.LENGTH_LONG).show();
                prevorder.setText(orderstr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        fahmid3 = FirebaseDatabase.getInstance().getReference().child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mobile");
        fahmid3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String phonestr = dataSnapshot.getValue(String.class);
               phone.setText(phonestr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        fahmid4 = FirebaseDatabase.getInstance().getReference().child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("address");
        fahmid4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String phonestr = dataSnapshot.getValue(String.class);
                address.setText(phonestr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fahmid5 = FirebaseDatabase.getInstance().getReference().child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("amount");
        fahmid5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String amtstr = dataSnapshot.getValue(String.class);
                amount.setText(amtstr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


      /*  userorder5 =  FirebaseDatabase.getInstance().getReference().child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status");
        userorder5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(tag , "inside on datachange4");
                String statusstr =  dataSnapshot.getValue(String.class);
                //Toast.makeText(getApplicationContext() , "" + orderstr , Toast.LENGTH_LONG).show();
                status.setText(statusstr);
                if(statusstr.equalsIgnoreCase("accepted")){
                    paybtn.setEnabled(true);
                    amount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });   */

        paybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startpayment();
            }
        });
        submitorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag , " inside click ");
                cateringorderstr = cateringorder.getText().toString();
                if(cateringorderstr.isEmpty()){
                    Log.d(tag , " inside if ");
                }
                else {
                    databaseusers = FirebaseDatabase.getInstance().getReference();
                    //databaseusers.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Log.d(tag , " inside else ");

                    // databaseusers.push().setValue("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseusers.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentorder").setValue(cateringorderstr);
                }

              /*  databaseusers2 = FirebaseDatabase.getInstance().getReference();
                databaseusers2.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("Not Yet Accepted");

                databaseusers3 = FirebaseDatabase.getInstance().getReference();
                databaseusers3.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("amount").setValue("400"); */

                try {
                    // Construct data
                    String apiKey = "apikey=" + "F28q3+3MSJI-418puRJyj7NAAvG50kwNNuIu6WXPE5";
                    String message = "&message=" + "New Catering Order !" +  " \n" + address.getText().toString() + " " + phone.getText().toString()  + " \n" + members.getText().toString() + afterhours.getText().toString()   ;
                    String sender = "&sender=" + "TXTLCL";
                    String numbers = "&numbers=" +"9137957674";

                    // Send data
                    HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                    String data = apiKey + numbers + message + sender;
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                    conn.getOutputStream().write(data.getBytes("UTF-8"));
                    final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        //    Toast.makeText(OrderingDetails.this,line.toString(),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(Profile.this,"Your Order is placed",Toast.LENGTH_LONG).show();
                    rd.close();
                } catch (Exception e) {
                    Toast.makeText(Profile.this,e.toString(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void startpayment(){

        int totalpayamount = Integer.parseInt(amount.getText().toString());

        Checkout checkout =  new Checkout();

        final Activity activty = this ;

        try{
            JSONObject options = new JSONObject();

            options.put("description","order #123456");
            options.put("currency" , "INR");
            options.put("amount" , 300);
            checkout.open(activty,options);
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(Profile.this ,"Your Payment is successful" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(Profile.this ,"Your Payment failed" , Toast.LENGTH_SHORT).show();

    }
}
