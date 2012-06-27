package org.jfrantz.poopers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mongodb.mapper.R;

public class MongoHeatMapActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 

		boolean isDatabaseRunning = false;
		boolean isServiceRunning = false;
		int dbpid = 0;

		try {
			Process lister = Runtime.getRuntime().exec("/system/bin/ps");

			BufferedReader in  = new BufferedReader(new InputStreamReader(lister.getInputStream()));
			String line = null;
			while ( (line = in.readLine()) != null) {
				if(line.indexOf("/system/bin/mongod") != -1) {
					dbpid = Integer.parseInt(line.split("[ ]+")[1]);
					Runtime.getRuntime().exec("kill " + dbpid);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Button startServer = (Button) findViewById(R.id.start_database);
		startServer.setText((isDatabaseRunning ? "Stop" : "Start") + " Database");
		startServer.setOnClickListener(new DatabaseButtonClickListener(isDatabaseRunning, startServer));

		Button startMeasuring = (Button) findViewById(R.id.start_service);
		startMeasuring.setText((isServiceRunning ? "Stop" : "Start") + " Measuring");
		startMeasuring.setOnClickListener(new ServiceButtonClickListener(isServiceRunning, startMeasuring));

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
		Button button;
		Process dbProc;

		public DatabaseButtonClickListener(boolean isRunning, Button button) {
			this.isRunning = isRunning;
			this.button = button;
		}
		public void onClick(View v) {
			if (isRunning) {
				dbProc.destroy();
				isRunning = false;
				button.setText("Start Database");
			} else {
				try {
<<<<<<< HEAD
					Log.d("StartingDatabase", "Creating db directory: " + new File("/data/db").mkdir());
					Log.d("StartingDatabase", "Creating socket directory: " + new File("/data/tmp").mkdir());
					Log.d("StartingDatabase", "Deleting Lock File: " + new File("/data/db/mongod.lock").delete());
					Process proc = Runtime.getRuntime().exec("/system/bin/mongod --dbpath=/data/db --unixSocketPrefix=/data/tmp");
					Thread.sleep(1000);
					Process ps = Runtime.getRuntime().exec("ps");
					BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
					String line;
					while ( (line = in.readLine()) != null) {
						if ( line.contains("/system/bin/mongod") ) {
							Log.d("StartingDatabase", "Process Started");
							isRunning = true;
							button.setText("Stop Database");
							dbProc = proc;
							return;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class ServiceButtonClickListener implements OnClickListener {
		boolean isRunning;
		Button button;

		public ServiceButtonClickListener(boolean isRunning, Button button) {
			this.isRunning = isRunning;
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
