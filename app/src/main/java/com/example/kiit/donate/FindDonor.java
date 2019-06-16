package com.example.kiit.donate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.v4.content.ContextCompat.startActivity;

public class FindDonor extends AppCompatActivity {
    String namee,c1,groupp,result,add;
    String pin,group;
    FirebaseDatabase database;
    RecyclerView mRecyclerView;
    ExampleAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ExampleItem> exampleList = new ArrayList<>();
    TextView mTextView;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_donor);

       // listItems.add(new ExampleItem((R.id.call)));
        Intent intent=getIntent();
        pin=intent.getStringExtra("pin");
        group=intent.getStringExtra("group");
        mProgressDialog = new ProgressDialog(FindDonor.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(exampleList);
        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onCallClick(int position) {
                String num = '0'+exampleList.get(position).getNumber();
                dialContactPhone(num);
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(pin+"/"+group);


        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                if (dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        String rawData = snapshot.getValue().toString();

                        Pattern r = Pattern.compile("name=(.*?)end");
                        Matcher m = r.matcher(rawData);

                        while(m.find()){
                            namee =  m.group(1);
                        }

                        r = Pattern.compile("contact1=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            c1 =  m.group(1);
                        }

                        r = Pattern.compile(", group=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            groupp =  m.group(1);
                        }

                        r = Pattern.compile("address=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            add =  m.group(1);
                        }

                        r = Pattern.compile("pincode=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            pin =  m.group(1);
                        }

                        //result="Name:"+namee+"\n"+"Blood Group: "+groupp+"\n"+"Contact: "+c1;

                        exampleList.add(new ExampleItem(R.drawable.sym_action_call,namee,add+", "+pin,c1,groupp));
                        mAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    exampleList.add(new ExampleItem(0,"Sorry No Donors Found!","","",""));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FindDonor.this,"Sorry No Donors Available",Toast.LENGTH_LONG).show();
                Log.d("checking","klo--"+databaseError.getCode());
            }
        });

    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
