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

		boolean isDatabaseRunning = false;
		boolean isServiceRunning = false;
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
		startServer.setText((isDatabaseRunning ? "Stop" : "Start") + "Database");
		startServer.setOnClickListener(new DatabaseButtonClickListener(isDatabaseRunning, dbpid, startServer));

		Button startMeasuring = (Button) findViewById(R.id.start_service);
		startMeasuring.setText((isServiceRunning ? "Stop" : "Start") + "Measuring");
		startMeasuring.setOnClickListener(new ServiceButtonClickListener(isServiceRunning, servicepid, startMeasuring));

		Button showMap = (Button) findViewById(R.id.view_map);
		showMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), ShowMapActivity.class);
				startActivity(i);
			}
		});

	}

	/*Toggle start/stop database*/
	private class DatabaseButtonClickListener implements OnClickListener {
		boolean isRunning;
		int PID;
		Button button;

		public DatabaseButtonClickListener(boolean isRunning, int PID, Button button) {
			this.isRunning = isRunning;
			this.PID = PID;
			this.button = button;
		}
		public void onClick(View v) {
			if (isRunning) {
				try {
					Runtime.getRuntime().exec("kill " + PID);
					isRunning = false;
					button.setText("Start Database");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Runtime.getRuntime().exec("/system/bin/mkdir /data/db");
					Runtime.getRuntime().exec("/system/bin/mkdir /data/tmp");
					Runtime.getRuntime().exec("/system/bin/rm /data/db/mongod.lock");
					Process proc = Runtime.getRuntime().exec("/system/bin/mongod --unixSocketPrefix=/data/tmp");
					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					Log.d("StartingProcess", "Starting Process");
					isRunning = true;
					button.setText("Stop Database");
					String line;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ServiceButtonClickListener implements OnClickListener {
		boolean isRunning;
		int PID;
		Button button;

		public ServiceButtonClickListener(boolean isRunning, int PID, Button button) {
			this.isRunning = isRunning;
			this.PID = PID;
			this.button = button;
		}
		public void onClick(View v) {
			if (isRunning) {
				Intent intent = new Intent(getApplicationContext(), DataCollectorService.class);
				stopService(intent);
				isRunning = false;
				button.setText("Start Measuring");
			} else {
				Intent intent = new Intent(getApplicationContext(), DataCollectorService.class);
				startService(intent);

				isRunning = true;
				button.setText("Stop Measuring");
			}
		}
	}
}