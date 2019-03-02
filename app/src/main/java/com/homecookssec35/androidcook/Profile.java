package com.homecookssec35.androidcook;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Profile extends AppCompatActivity {

    EditText cateringorder ;
    EditText prevorder ;
    Button submitorderbtn;
    DatabaseReference  databaseusers ;
    DatabaseReference  userorder ;
    Button paybtn ;

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
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        cateringorder = (EditText)findViewById(R.id.editorderText);
        prevorder = (EditText)findViewById(R.id.editText2);
        cateringorderstr = cateringorder.getText().toString();
      //  Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(tag, "" + currentFirebaseUser.getUid());
        Toast.makeText(this,"" + currentFirebaseUser.getUid(),Toast.LENGTH_LONG ).show();
        submitorderbtn = (Button)findViewById(R.id.submitorederbutton);
        paybtn = (Button)findViewById(R.id.button9);
        Log.d(tag , cateringorderstr);
        Log.d(tag , " outside click ");
        paybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 userorder =  FirebaseDatabase.getInstance().getReference() ;
                 userorder = userorder.child("" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentorder");
                 userorder.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {

                         Log.d(tag , "inside on datachange");
                         String orderstr =  dataSnapshot.getValue(String.class);
                         Toast.makeText(getApplicationContext() , "" + orderstr , Toast.LENGTH_LONG).show();
                         prevorder.setText(orderstr);
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });
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
            }
        });
    }
}
