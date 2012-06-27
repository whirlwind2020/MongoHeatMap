package org.jfrantz.poopers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        
        boolean isDatabaseRunning = false, isServiceRunning = false;
        int dbpid = 0, servicepid = 0;
        
        try {
			Process lister = Runtime.getRuntime().exec("/system/bin/ps");
			BufferedReader in  = new BufferedReader(new InputStreamReader(lister.getInputStream()));
			String line = null;
            while ( (line = in.readLine()) != null) {
                if(line.indexOf("/system/bin/mongod") != -1) {
                	dbpid = Integer.parseInt(line.split("[ ]+")[1]);
                	isDatabaseRunning = true;
                }
                if(line.indexOf("signalcollector") != -1) {
                	servicepid = Integer.parseInt(line.split("[ ]+")[1]);
                	isServiceRunning = true;
                }
            }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        Button startServer = (Button) findViewById(R.id.start_database);
        startServer.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		try {
        			Runtime.getRuntime().exec("/system/bin/mkdir /data/db");
        			Runtime.getRuntime().exec("/system/bin/mkdir /data/tmp");
					Runtime.getRuntime().exec("/system/bin/rm /data/db/mongod.lock");
					Runtime.getRuntime().exec("/system/bin/mongod --unixSocketPrefix=/data/tmp");
        		} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
        
        Button startMeasuring = (Button) findViewById(R.id.start_service);
        startMeasuring.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(getApplicationContext(), DataCollectorService.class);
                startService(intent);
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