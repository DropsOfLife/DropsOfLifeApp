package com.example.kiit.donate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class pinSave extends AppCompatActivity {
    String pin;
    EditText pintext;
    Button submit;
    ProgressDialog mProgress;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "pin";
    public static final String SWITCH1 = "switch1";

    private String savedpin;
    private boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_save);
        pintext = (EditText) findViewById(R.id.pinSaveInput);
        submit = (Button) findViewById(R.id.pinSaveButton);


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
            Toast.makeText(pinSave.this,"Please Turn on Internet!",Toast.LENGTH_LONG).show();
        }

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String pinCode = pintext.getText().toString().trim();

                if (pinCode.length() < 6) {
                    Toast.makeText(pinSave.this, "Please enter correct pin code!", Toast.LENGTH_LONG).show();
                }  else{
                    YourAsyncTask task = new YourAsyncTask();
                    task.execute("http://postalpincode.in/api/pincode/" + pinCode);
                }
            }
        });
    }

    public class YourAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        public void onPreExecute() {
            mProgress = new ProgressDialog(pinSave.this);
            mProgress.setMessage("Please wait...");
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... urls){
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        Toast.makeText(pinSave.this, "Error occured!", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String results) {
            if(results!=null)
                onTaskComplete(results);
        }


    }

    public void onTaskComplete(String result) {
        mProgress.dismiss();
        if (result.equals("NULL")) {
            Toast.makeText(pinSave.this, "1Please turn on your internet connection!", Toast.LENGTH_LONG).show();
        } else{
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String status = null;
            String postOffice = null;
            String district = null;

            try {
                status = jsonObject.getString("Status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equals(null)) {
                Toast.makeText(pinSave.this, "2Please turn on your internet connection!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Error")) {
                Toast.makeText(pinSave.this, "Wrong Pin Code!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Success")) {

                try {
                    postOffice = jsonObject.getString("PostOffice");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray jsonArray = new JSONArray(postOffice);
                    JSONObject jsonpart = jsonArray.getJSONObject(0);
                    district = jsonpart.getString("District");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(pinSave.this);
                builder.setMessage("Location : "+district+"\nConfirm to submit!")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString(TEXT, pintext.getText().toString());
                                editor.putBoolean(SWITCH1,true);

                                editor.apply();
                                startActivity(new Intent(pinSave.this, MainActivity.class));

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
}
