package org.jfrantz.poopers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

import com.mongodb.mapper.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MongoHeatMapActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        Button startServer = (Button) findViewById(R.id.start_database);
        startServer.setText((isServerRunning ? "Stop" : "Start") + "Database");
        startServer.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (isServerRunning) {
        			Runtime.getRuntime().exec("kill " + databasePID);
        			isServerRunning = false;
        			startServer.setText("Start Database");
        			return;
        		}
        		try {
        			Runtime.getRuntime().exec("/system/bin/mkdir /data/db");
        			Runtime.getRuntime().exec("/system/bin/mkdir /data/tmp");
					Runtime.getRuntime().exec("/system/bin/rm /data/db/mongod.lock");
					Process proc = Runtime.getRuntime().exec("/system/bin/mongod --unixSocketPrefix=/data/tmp");
					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					Log.d("StartingProcess", "Starting Process");
					isDatabaseRunning = true;
					startServer.setText("Stop Database");
					String line;
					//while((line = in.readLine()) != null) {
                    //     Log.d("StartingProcess",line);
					//}
        		} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
        
        Button startMeasuring = (Button) findViewById(R.id.start_service);
        startMeasuring.setText((isServiceRunning ? "Stop" : "Start") + "Measuring");
        startMeasuring.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (isServiceRunning) {
        			//kill it
        			Runtime.getRuntime().exec("kill " + servicePID);
        			isServiceRunning = false;
        			startMeasuring.setText("Start Measuring");
        			return;
        		}
        		Intent intent = new Intent(getApplicationContext(), DataCollectorService.class);
                startService(intent);
                
                isServiceRunning = true;
                startMeasuring.setText("Stop Measuring");

        	}
        });
        
        Button showMap = (Button) findViewById(R.id.view_map);
        showMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ShowMapActivity.class);
				startActivity(i);
			}
        });
        
    }
}