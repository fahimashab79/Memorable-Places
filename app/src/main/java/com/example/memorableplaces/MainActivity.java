package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<String>places=new ArrayList<String>();
    static ArrayList<LatLng> location=new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;
    //static int a=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String>latitude=new ArrayList<>();
        ArrayList<String >longitude=new ArrayList<>();
        latitude.clear();
        longitude.clear();
        places.clear();
        location.clear();
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);

        try {
            places=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String >())));
            latitude=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String >())));
            longitude=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lons",ObjectSerializer.serialize(new ArrayList<String >())));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(places.size()>0&&latitude.size()>0&&longitude.size()>0)
        {
            if(places.size()==latitude.size()&&places.size()==longitude.size()){
                for(int i=0;i<places.size();i++)
                {
                    location.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        }
        else {
            location.add(new LatLng(0,0));
            places.add("get user location");
        }

        listView=(ListView)findViewById(R.id.listView);

        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
              //  a=i;
                intent.putExtra("number",i);
                //Toast.makeText(MainActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }
}
