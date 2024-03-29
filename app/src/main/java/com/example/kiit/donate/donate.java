package com.example.kiit.donate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.firebase.database.DatabaseReference;

public class donate extends AppCompatActivity {
    public EditText name,contact1,pinCode,address;
    public Spinner mSpinner;
    Button mButton;
    DatabaseReference reference;
    ProgressDialog mProgress;
    String group,c1,n,p,add;
    CheckBox cb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        name=(EditText) findViewById(R.id.name);
        contact1 = (EditText) findViewById(R.id.contact1);
        pinCode = (EditText) findViewById(R.id.pinInput);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mButton = (Button) findViewById(R.id.button);
        cb = (CheckBox) findViewById(R.id.checkbox);
        address = (EditText) findViewById(R.id.addresss);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group = mSpinner.getSelectedItem().toString();
                String c1 = contact1.getText().toString().trim();
                String n = name.getText().toString().trim();
                String p = pinCode.getText().toString().trim();
                String add = address.getText().toString().trim();
                if(add.length()==0){
                    Toast.makeText(donate.this,"Please enter the address",Toast.LENGTH_LONG).show();
                }else if(!cb.isChecked()){
                    Toast.makeText(donate.this,"Please enter all the details",Toast.LENGTH_LONG).show();
                }else if(group.equals("None")){
                    Toast.makeText(donate.this,"Please select a blood group",Toast.LENGTH_LONG).show();
                }else if(c1.length()<10){
                    Toast.makeText(donate.this,"Please Enter correct contact",Toast.LENGTH_LONG).show();
                }else if(n.isEmpty()){
                    Toast.makeText(donate.this,"Please Enter your name!",Toast.LENGTH_LONG).show();
                }else if(p.length()<6){
                    Toast.makeText(donate.this,"Please enter correct pin code!",Toast.LENGTH_LONG).show();
                }else{
                    //DownloadTask task = new DownloadTask();
                    YourAsyncTask task = new YourAsyncTask(donate.this);
                    task.execute("http://postalpincode.in/api/pincode/"+p);
                }

            }
        });
    }

    public class YourAsyncTask extends AsyncTask<String, String, String> {
        private Context mContext;

//private TaskCompleted mCallback;

        public YourAsyncTask(Activity activity){
            this.mContext = activity;
/*    try {
        this.mCallback = (TaskCompleted) activity;
    } catch (ClassCastException e) {
        ClassCastException tothrow = new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        tothrow.initCause(e);
        throw tothrow;
    }*/
        }

        @Override
        public void onPreExecute() {
            mProgress = new ProgressDialog(mContext);
            mProgress.setMessage("Please wait...");
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while(data!=-1){
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }
                return result;
            }

            catch (Exception e) {
                return "NULL";
            }
        }


        @Override
        protected void onPostExecute(String results) {
            //mProgress.dismiss();
            //This is where you return data back to caller
            //mCallback.onTaskComplete(results);
            onTaskComplete(results);

        }
    }




    public void onTaskComplete(String result) {
        mProgress.dismiss();
        if (result.equals("NULL")) {
            Toast.makeText(donate.this, "Please turn on your internet connection!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(donate.this, "Please turn on your internet connection!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Error")) {
                Toast.makeText(donate.this, "Wrong Pin Code!", Toast.LENGTH_LONG).show();
            } else if (status.equals("Success")) {

                try {
                    postOffice = jsonObject.getString("PostOffice");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    group = mSpinner.getSelectedItem().toString();
                    c1 = contact1.getText().toString().trim();
                    n = name.getText().toString().trim();
                    p = pinCode.getText().toString().trim();
                    add = address.getText().toString().trim();
                    JSONArray jsonArray = new JSONArray(postOffice);
                    JSONObject jsonpart = jsonArray.getJSONObject(0);
                    district = jsonpart.getString("District");


                    AlertDialog.Builder builder = new AlertDialog.Builder(donate.this);
                    builder.setMessage(n+" Your Location: "+district+"\nConfirm to submit your details!")
                            .setCancelable(false)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    reference = FirebaseDatabase.getInstance().getReference().child(p).child(group);
                                    String key=reference.push().getKey();
                                    reference = reference.child(key);
                                    reference.child("group").setValue(group+"end");
                                    reference.child("contact1").setValue(c1+"end");
                                    //reference.child("contact2").setValue(c2+"end");
                                    reference.child("name").setValue(n+"end");
                                    reference.child("pincode").setValue(p+"end");
                                    reference.child("address").setValue(add+"end");
                                    Intent myintent = new Intent(donate.this, ThankYou.class);
                                    startActivity(myintent);
                                    donate.this.finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }



}




