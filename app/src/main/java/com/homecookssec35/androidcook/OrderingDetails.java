package com.homecookssec35.androidcook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.se.omapi.Session;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class OrderingDetails extends AppCompatActivity implements PaymentResultListener {

    public static Button checkoutbtn;
    public static Button cashondeliverybtn ;
    public static TextView displaydishname ;
    public static TextView displayunitprice;
    public static Button incrementbtn , decrementbtn ;
    public  static ProgressBar progbar2;
    public static TextView quantity , totalprice ;
    EditText phone,address,name;
    public  static String mobilenumber ;
    public  static String latitude , longitude , selectedcookname ,nameofdish ;
    public static int numboroforders ;
    String tag="famiddetails";
    double totalpayamount ;
    DatabaseReference fahmid ;
    DatabaseReference fahmid2 ;
    DatabaseReference fahmid3 ;
    DatabaseReference fahmid4,fahmid5 ,fahmid6,fahmid7;

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem logoutitem = menu.findItem(R.id.menulogout);
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            logoutitem.setEnabled(false);
        }
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

            case R.id.menuorder :
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent intent3 =  new Intent(this,LoginUserActivity.class);
                    startActivity(intent3);
                }
                else{
                    Intent intent2 =  new Intent(this,Profile.class);
                    startActivity(intent2);
                }
                break;

        }

        return true;
    }


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
        progbar2 = (ProgressBar)findViewById(R.id.progressbarorder);

        Intent receiveintent2 = getIntent();
         nameofdish = receiveintent2.getStringExtra("dishname");
        String price = String.valueOf(receiveintent2.getStringExtra("unitprice"));
        mobilenumber = receiveintent2.getStringExtra("mobilenumber");
        latitude = receiveintent2.getStringExtra("latitude");
        longitude = receiveintent2.getStringExtra("longitude");
        selectedcookname = receiveintent2.getStringExtra("cookname");
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
                      //  Log.d(tag,phonestr);
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


                fahmid4 =  FirebaseDatabase.getInstance().getReference().child("Cooks").child(selectedcookname).child("numberoforders");
                fahmid4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nom = dataSnapshot.getValue(String.class);
                        numboroforders = Integer.parseInt(nom);
                        fahmid4.setValue(String.valueOf(numboroforders + 1));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                fahmid6 =  FirebaseDatabase.getInstance().getReference().child("Cooks").child(selectedcookname).child("Products").child(nameofdish).child("numberoforders");
                fahmid6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nom = dataSnapshot.getValue(String.class);
                        int numoforders = Integer.parseInt(nom);
                        fahmid6.setValue(String.valueOf(numoforders + 1));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    String mobilestr = phone.getText().toString();
                    String addressstr = address.getText().toString();
                    String namestr = name.getText().toString();
                    fahmid = FirebaseDatabase.getInstance().getReference();
                    fahmid.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mobile").setValue(mobilestr);
                    fahmid.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("address").setValue(addressstr);
                    fahmid.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(namestr);
                }

                final String username = "testmailfahmid@gmail.com";
                final String password = "testmailpassword";

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "589");

                javax.mail.Session session = javax.mail.Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("testmailfahmid@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse("testmailfahmid@gmail.com"));
                    message.setSubject("Testing Subject");
                    message.setText("The location is  " + "http://maps.google.com/?q=" + latitude + "," + longitude + " \n" + address.getText().toString() + " " + phone.getText().toString() + name.getText().toString() +  " \n" + address.getText().toString()  + " \n" + selectedcookname);

                    Transport.send(message);

                    Log.d(tag,"done");

                } catch (MessagingException e) {
                    // new RuntimeException(e);
                }

                try {
                    // Construct data
                    String apiKey = "apikey=" + "F28q3+3MSJI-418puRJyj7NAAvG50kwNNuIu6WXPE5";
                    String message = "&message=" + "The location is  " + "http://maps.google.com/?q=" + latitude + "," + longitude + " \n" + address.getText().toString() + " " + phone.getText().toString() + name.getText().toString() +  " \n" + address.getText().toString()  + " \n" + selectedcookname ;
                    String sender = "&sender=" + "TXTLCL";
                    String numbers = "&numbers=" + mobilenumber+","+"9137957674";

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

                    Log.d(tag , "here outside");
                    while ((line = rd.readLine()) != null) {
                      //Toast.makeText(OrderingDetails.this,line.toString(),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(OrderingDetails.this,"Your order is placed ! ",Toast.LENGTH_LONG).show();
                    Log.d(tag , "here inside");
                    rd.close();
                } catch (Exception e) {
                    Toast.makeText(OrderingDetails.this,e.toString(),Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void startpayment(){

        totalpayamount = Double.parseDouble(totalprice.getText().toString());

        Checkout checkout =  new Checkout();

        final Activity activty = this ;

        try{
            JSONObject options = new JSONObject();

            options.put("description","order #123456");
            options.put("currency" , "INR");
            options.put("amount" , totalpayamount);
            checkout.open(activty,options);
        }
        catch(JSONException e){
                e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s) {

        fahmid5 =  FirebaseDatabase.getInstance().getReference().child("Cooks").child(selectedcookname).child("numberoforders"); ;
        fahmid5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nom = dataSnapshot.getValue(String.class);
                numboroforders = Integer.parseInt(nom);
                fahmid5.setValue(String.valueOf(numboroforders + 1));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fahmid7 =  FirebaseDatabase.getInstance().getReference().child("Cooks").child(selectedcookname).child("Products").child(nameofdish).child("numberoforders");
        fahmid7.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nom = dataSnapshot.getValue(String.class);
                int numoforders = Integer.parseInt(nom);
                fahmid7.setValue(String.valueOf(numoforders + 1));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(OrderingDetails.this ,"Your Payment is successful" , Toast.LENGTH_SHORT).show();

        final String username = "testmailfahmid@gmail.com";
        final String password = "testmailpassword";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        javax.mail.Session session = javax.mail.Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("testmailfahmid@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("mefahmid@gmail.com"));
            message.setSubject("Testing Subject");
            message.setText("The location is  " + "http://maps.google.com/?q=" + latitude + "," + longitude + " \n" + address.getText().toString() + " " + phone.getText().toString() + name.getText().toString() +  " \n" + address.getText().toString()  + " \n" + selectedcookname);

            Transport.send(message);

            Log.d(tag,"done");

        } catch (MessagingException e) {
           // throw new RuntimeException(e);
        }
        try {
            // Construct data
            String apiKey = "apikey=" + "F28q3+3MSJI-418puRJyj7NAAvG50kwNNuIu6WXPE5";
            String message = "&message=" + "The location is  " + "http://maps.google.com/?q=" + latitude + "," + longitude + " \n" + address.getText().toString() + " " + phone.getText().toString() + name.getText().toString() + " \n" + selectedcookname ;
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + mobilenumber+","+"9137957674";

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
            Toast.makeText(OrderingDetails.this,"Your Order is placed",Toast.LENGTH_LONG).show();
            rd.close();
        } catch (Exception e) {
            Toast.makeText(OrderingDetails.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(OrderingDetails.this ,"Your Payment failed" , Toast.LENGTH_SHORT).show();

    }
}

