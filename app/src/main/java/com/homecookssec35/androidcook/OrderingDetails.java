package com.homecookssec35.androidcook;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderingDetails extends AppCompatActivity implements PaymentResultListener {

    public static Button checkoutbtn;
    public static Button cashondeliverybtn ;
    public static TextView displaydishname ;
    public static TextView displayunitprice;
    public static Button incrementbtn , decrementbtn ;
    public static TextView quantity , totalprice ;
    EditText phone,address,name;
    String tag="famiddetails";
    int totalpayamount ;
    DatabaseReference fahmid ;
    DatabaseReference fahmid2 ;
    DatabaseReference fahmid3 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_details);

        checkoutbtn =(Button)findViewById(R.id.button7);
        cashondeliverybtn = (Button)findViewById(R.id.button8);
        displaydishname = (TextView)findViewById(R.id.textView9);
        displayunitprice = (TextView)findViewById(R.id.textView11);
        incrementbtn = (Button)findViewById(R.id.button5);
        decrementbtn = (Button)findViewById(R.id.button6);
        quantity = (TextView)findViewById(R.id.textView13);
        totalprice = (TextView)findViewById(R.id.textView15);
        phone = (EditText)findViewById(R.id.editText3);
        address = (EditText)findViewById(R.id.editText4);
        name = (EditText)findViewById(R.id.editText7);

        Intent receiveintent2 = getIntent();
        String nameofdish = receiveintent2.getStringExtra("dishname");
        String price = String.valueOf(receiveintent2.getStringExtra("unitprice"));
        final double priceindouble = Double.parseDouble(receiveintent2.getStringExtra("unitprice"));
        displaydishname.setText(nameofdish);
        displayunitprice.setText(price);
        totalprice.setText(price);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            DatabaseReference testref = FirebaseDatabase.getInstance().getReference();
            testref = testref.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            if(testref.child("mobile") != null){
                Log.d(tag,"inside1");
                testref = testref.child("mobile");
                testref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(tag,"inside2");
                        String phonestr = dataSnapshot.getValue(String.class);
                        Log.d(tag,phonestr);
                        phone.setText(phonestr);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            fahmid2 = FirebaseDatabase.getInstance().getReference().child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("address");

            fahmid2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String addrestr = dataSnapshot.getValue(String.class);
                    address.setText(addrestr);
                    Log.d(tag,"inside3");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            fahmid3 = FirebaseDatabase.getInstance().getReference().child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");

            fahmid3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String namestr = dataSnapshot.getValue(String.class);
                    name.setText(namestr);
                    Log.d(tag,"inside4");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /*if(testref.child("name") != null){

                testref = testref.child("name");
                Log.d(tag,"inside5");
                testref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(tag,"inside6");
                        String namestr = dataSnapshot.getValue(String.class);
                        name.setText(namestr);
                        //Log.d(tag,namestr);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {



                    }
                });

            }*/

        }

        incrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String prequantity = (String)quantity.getText();
                 if(Integer.parseInt(prequantity) < 15){
                     quantity.setText(String.valueOf(Integer.parseInt(prequantity) + 1));
                     totalprice.setText(String.valueOf( Integer.parseInt((String)quantity.getText())*priceindouble ));
                 }
            }
        });
        decrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prequantity = (String)quantity.getText();
                  if(Integer.parseInt((String)quantity.getText()) > 0 ){
                      quantity.setText(String.valueOf(Integer.parseInt(prequantity) - 1));
                      totalprice.setText(String.valueOf( Integer.parseInt((String)quantity.getText())*priceindouble ));
                  }
            }
        });
        checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent3 =  new Intent(OrderingDetails.this,PaytmCheckOutActivity.class);
               // startActivity(intent3);
                // save the address and mobile
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    String mobilestr = phone.getText().toString();
                    String addressstr = address.getText().toString();
                    String namestr = name.getText().toString();
                    fahmid = FirebaseDatabase.getInstance().getReference();
                    fahmid.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mobile").setValue(mobilestr);
                    fahmid.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("address").setValue(addressstr);
                    fahmid.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(namestr);
                }
                else{

                }
                startpayment();
            }
        });
        cashondeliverybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // Construct data
                    String apiKey = "apikey=" + "F28q3+3MSJI-418puRJyj7NAAvG50kwNNuIu6WXPE5";
                    String message = "&message=" + "This is your message";
                    String sender = "&sender=" + "TXTLCL";
                    String numbers = "&numbers=" + "918638350117;919892399470";

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
                      Toast.makeText(OrderingDetails.this,line.toString(),Toast.LENGTH_LONG).show();
                    }
                    rd.close();
                } catch (Exception e) {
                    Toast.makeText(OrderingDetails.this,e.toString(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void startpayment(){

        totalpayamount = Integer.parseInt(totalprice.getText().toString());

        Checkout checkout =  new Checkout();

        final Activity activty = this ;

        try{
            JSONObject options = new JSONObject();

            options.put("description","order #123456");
            options.put("currency" , "INR");
            options.put("amount" , 500);
            checkout.open(activty,options);
        }
        catch(JSONException e){
                e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(OrderingDetails.this ,"Your Payment is successful" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(OrderingDetails.this ,"Your Payment failed" , Toast.LENGTH_SHORT).show();

    }
}
