package com.example.kiit.donate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShareDetails extends AppCompatActivity {
    TextView pintext,contacttext,locationtext,grouptext;
    String pin,group,location,contact;
    Button share,call;
    public static final String SHARED_PREFS = "sharedPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_details);


        Intent intent=getIntent();
        pin=intent.getStringExtra("pin");
        group=intent.getStringExtra("group");
        location = intent.getStringExtra("location");
        contact = intent.getStringExtra("contact");

        pintext = (TextView) findViewById(R.id.pincode);
        contacttext = (TextView) findViewById(R.id.contact);
        locationtext = (TextView) findViewById(R.id.location);
        grouptext = (TextView) findViewById(R.id.group);
        share = (Button) findViewById(R.id.sharebutton);
        call = (Button) findViewById(R.id.callbutton);

        pintext.setText("Area Pin Code: "+pin);
        contacttext.setText("Contact Number: "+contact);
        locationtext.setText("Location: "+location);
        grouptext.setText("Blood Group: "+group);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("shownpin");
        editor.remove("group");
        editor.remove("location");
        editor.remove("contact");
        editor.apply();


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialContactPhone("+91"+contact);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareSub = "Someone needs " + group + " blood urgently!";
                String shareBody = shareSub+"\n"+"Pin Code: "+pin+"\n"+"Location: "+location+"\n"+"Contact no: "+contact+"\n\n"+"Please Share or Contact to help!";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Share using"));
            }
        });
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        Intent i = new Intent(ShareDetails.this, MainActivity.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        return;
    }
}
