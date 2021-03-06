package org.jfrantz.poopers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class DataCollectorService extends Service implements LocationListener {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void onCreate() {
	    super.onCreate();
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100.0f, this);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	public void onLocationChanged(final Location location) {
		final Random rand = new Random();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Mongo m = new Mongo( "localhost" , 27017 );
					DB db = m.getDB( "data" );
					BasicDBObject b = new BasicDBObject();
					DBCollection coll = db.getCollection("signalPoints");
					coll.ensureIndex(new BasicDBObject("loc", "2d"));
					ArrayList<Double> loc = new ArrayList<Double>();
					loc.add(location.getLongitude());
					loc.add(location.getLatitude());
					b.put("loc", loc);
					b.put("intensity", (int)(rand.nextInt(19) * (Math.cos(location.getLatitude()) * Math.sin(location.getLongitude()) + 1)));
					coll.insert(b);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (MongoException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	

}
