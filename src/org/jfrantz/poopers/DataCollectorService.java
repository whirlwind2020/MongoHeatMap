package org.jfrantz.poopers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import com.mongodb.*;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

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
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Service.START_NOT_STICKY;
	}

	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		Log.i("Service", "Location changed to " + lat + ", " + lng);
		Random rand = new Random();
		Mongo m;
		try {
			m = new Mongo( "localhost" , 27017 );
			DB db = m.getDB( "data" );
			BasicDBObject b = new BasicDBObject();
			DBCollection coll = db.getCollection("signalPoints");
			ArrayList<BasicDBObject> loc = new ArrayList<BasicDBObject>();
			loc.add(new BasicDBObject("lon", location.getLongitude()));
			loc.add(new BasicDBObject("lat", location.getLatitude()));
			b.put("loc", loc);
			b.put("intensity", (rand.nextInt(18 + 1) * (Math.cos(location.getLatitude()) + Math.sin(location.getLongitude()) + 1)));
			coll.insert(b);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

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
