package com.example.kiit.donate;

public class ExampleItem {
    private int mImageResource;
    private String mname,maddress,mnumber,mgroup;

    public ExampleItem(int imageResource, String name, String address, String number, String group){
        mImageResource = imageResource;
        mname = name;
        maddress = address;
        mnumber = number;
        mgroup = group;
    }

    public int getImageResource(){
        return mImageResource;
    }

    public String getName(){
        return mname;
    }

    public String getAddress(){
        return maddress;
    }

    public String getNumber(){
        return mnumber;
    }

    public String getGroup(){
        return mgroup;
    }
}
