package com.example.kiit.donate;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView bgapp, clover, donate, request,aboutus,contactus;
    LinearLayout textsplash, texthome, menus;
    Animation frombottom;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "pin";
    public static final String SWITCH1 = "switch1";

    public static String savedpin;
    private boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(connected){
            //Toast.makeText(MainActivity.this,"Internet Connection available",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,"Please Turn on Internet!",Toast.LENGTH_LONG).show();
        }

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);

        checkData();
        checkData2();


        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationHandler())
                .init();
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);


        donate = (ImageView) findViewById(R.id.donate);
        request = (ImageView) findViewById(R.id.request);
        aboutus = (ImageView) findViewById(R.id.about);
        contactus = (ImageView) findViewById(R.id.contact);
        bgapp = (ImageView) findViewById(R.id.bgapp);
        //clover = (ImageView) findViewById(R.id.clover);
        textsplash = (LinearLayout) findViewById(R.id.textsplash);
        texthome = (LinearLayout) findViewById(R.id.texthome);
        menus = (LinearLayout) findViewById(R.id.menus);

        bgapp.animate().translationY(-1900).setDuration(1000).setStartDelay(300);
        //clover.animate().alpha(0).setDuration(800).setStartDelay(600);
        textsplash.animate().translationY(140).alpha(0).setDuration(1100).setStartDelay(60);

        texthome.startAnimation(frombottom);
        menus.startAnimation(frombottom);


        ImageView img_animation = (ImageView) findViewById(R.id.clover);

        TranslateAnimation cloveranimation = new TranslateAnimation(900.0f, 0.0f,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        cloveranimation.setDuration(1000);  // animation duration
        //cloveranimation.setRepeatCount(5);  // animation repeat count
        //cloveranimation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        //animation.setFillAfter(true);

        img_animation.startAnimation(cloveranimation);  // start animation

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, donate.class));
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, request.class));
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutUs.class));
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactUs.class));
            }
        });

    }

    class NotificationHandler implements  OneSignal.NotificationOpenedHandler{


        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            JSONObject data = result.notification.payload.additionalData;

            if (data != null){

                // DO what ever you want
                String pinshow=null;
                String contactshow=null;
                String locationshow=null;
                String bloodgroupshow = null;

                String rawData = data.optString("rawdata");
                Pattern r = Pattern.compile("pin_code:(.*?)sToP!");
                Matcher m = r.matcher(rawData);

                while(m.find()){
                    pinshow =  m.group(1);
                    Log.d("Runninggg",pinshow);
                }

                r = Pattern.compile("location:(.*?)sToP!");
                m = r.matcher(rawData);

                while(m.find()){
                    locationshow =  m.group(1);
                }

                r = Pattern.compile("contact:(.*?)sToP!");
                m = r.matcher(rawData);

                while(m.find()){
                    contactshow =  m.group(1);
                }

                r = Pattern.compile("blood_grp:(.*?)sToP!");
                m = r.matcher(rawData);

                while(m.find()){
                    bloodgroupshow =  m.group(1);
                }


                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("shownpin",pinshow);
                editor.putString("group",bloodgroupshow);
                editor.putString("location",locationshow);
                editor.putString("contact",contactshow);

                editor.apply();
                checkData2();

            }



        }
    }


    public void checkData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        savedpin = sharedPreferences.getString(TEXT, "");
        isSaved = sharedPreferences.getBoolean(SWITCH1, false);

        if(savedpin.isEmpty()){
            startActivity(new Intent(MainActivity.this, pinSave.class));
        }else{
            Log.d("Runninggg2",savedpin+"<--");
            OneSignal.sendTag("pin_code",savedpin);
        }

    }

    public void checkData2() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String shownpin = sharedPreferences.getString("shownpin","0");
        if(!shownpin.equals("0")){

            Intent myintent = new Intent(getApplicationContext(), ShareDetails.class);
            myintent.putExtra("pin",shownpin);
            myintent.putExtra("group",sharedPreferences.getString("group",null));
            myintent.putExtra("location",sharedPreferences.getString("location",null));
            myintent.putExtra("contact",sharedPreferences.getString("contact",null));
            startActivity(myintent);

        }
    }
}