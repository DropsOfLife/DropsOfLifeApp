package com.example.kiit.donate;


import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.example.kiit.donate.App.CHANNEL_1_ID;
import static com.example.kiit.donate.App.CHANNEL_2_ID;


public class request extends AppCompatActivity {

    EditText pin,location,contact;
    Button submit;
    Spinner spinner;
    String pinCode, group;
    ProgressDialog mProgress;
    private NotificationManagerCompat notificationManager;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "pin";
    public static final String SWITCH1 = "switch1";

    String savedpin;
    boolean isSaved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        pin = (EditText) findViewById(R.id.pinInput);
        location = (EditText) findViewById(R.id.location);
        contact = (EditText) findViewById(R.id.contactRequester);
        submit = (Button) findViewById(R.id.button);
        spinner = (Spinner) findViewById(R.id.spinner);

        notificationManager = NotificationManagerCompat.from(this);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinCode = pin.getText().toString().trim();
                group = spinner.getSelectedItem().toString().trim();

                if (pinCode.length() < 6) {
                    Toast.makeText(request.this, "Please enter correct pin code!", Toast.LENGTH_LONG).show();
                } else if (group.equals("None")) {
                    Toast.makeText(request.this, "Please select a blood group", Toast.LENGTH_LONG).show();
                } else {
                    YourAsyncTask task = new YourAsyncTask();
                    task.execute("http://postalpincode.in/api/pincode/" + pinCode);

                }
            }
        });


    }


    private void sendNotification(){
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    Log.d("Runninggg3",MainActivity.savedpin);
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic Y2EyM2M4NmQtMjBhNy00ZmUwLWI3NWMtZDAzMjcwZGE4NTgw");
                        con.setRequestMethod("POST");

                        String rawdata = "pin_code:"+pin.getText().toString()+"sToP!"+"location:"+location.getText().toString()+"sToP!"+"contact:"+contact.getText().toString()+"sToP!"+"blood_grp:"+spinner.getSelectedItem().toString()+"sToP!";

                        String strJsonBody = "{"
                                + "\"app_id\": \"092deffb-8b8e-4c48-a2bd-a1bb479e7da3\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"pin_code\", \"relation\": \"=\", \"value\": \"" + pin.getText().toString() + "\"}],"

                                + "\"data\": {\"rawdata\": \""+rawdata+"\"},"
                                + "\"contents\": {\"en\": \"Someone needs "+spinner.getSelectedItem().toString() + " blood in your area urgently! Tap to Share\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


    public class YourAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        public void onPreExecute() {
            mProgress = new ProgressDialog(request.this);
            mProgress.setMessage("Please wait...");
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
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
                        Toast.makeText(request.this, "Error occured!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(request.this, "1Please turn on your internet connection!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(request.this, "2Please turn on your internet connection!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Error")) {
                Toast.makeText(request.this, "Wrong Pin Code!", Toast.LENGTH_LONG).show();
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






                group = spinner.getSelectedItem().toString();
                pinCode = pin.getText().toString().trim();


                AlertDialog.Builder builder = new AlertDialog.Builder(request.this);
                builder.setMessage("Location : "+district+"\nConfirm to submit!")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendNotification();
                                Toast.makeText(request.this,"Notification sent to all users with matching pin code!",Toast.LENGTH_LONG).show();
                                Intent myintent = new Intent(request.this, FindDonor.class);
                                myintent.putExtra("pin",pinCode);
                                myintent.putExtra("group",group);
                                startActivity(myintent);
                                //getActivity().finish();
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
