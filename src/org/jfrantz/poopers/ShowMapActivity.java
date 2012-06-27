package org.jfrantz.poopers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.androidnatic.maps.HeatMapOverlay;
import com.androidnatic.maps.SimpleMapView;
import com.androidnatic.maps.events.PanChangeListener;
import com.androidnatic.maps.events.ZoomChangeListener;
import com.androidnatic.maps.model.HeatPoint;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.mapper.R;

public class ShowMapActivity extends MapActivity implements OnClickListener, PanChangeListener, ZoomChangeListener, OnTouchListener{	
	public final String COLLECTION_NAME = "signalPoints";
	public final String DATABASE_NAME = "data";
	
	SimpleMapView _map;
	HeatMapOverlay _heatMap;
	MapController _controller;
	MyLocationOverlay _mLoc;
	
	Mongo _mongo;
	DB _db;
	DBCollection _coll;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapviewlayout);
		
        _map = (SimpleMapView) findViewById(R.id.map_view_layout);
        _controller = _map.getController();
        
        _map.setBuiltInZoomControls(true);
        _map.setOnClickListener(this);
        _map.setClickable(true);
        _map.addPanChangeListener(this);
        _map.addZoomChangeListener(this);
        ((RelativeLayout) findViewById(R.id.map_root)).setOnTouchListener(this);
        
        
        //pointer to current location
        _mLoc = new MyLocationOverlay(this, _map);
        _mLoc.enableMyLocation();
        _map.getOverlays().add(_mLoc);
        
        //start at our current Location
        if (_mLoc.getMyLocation() != null) 
        	_controller.setCenter(_mLoc.getMyLocation());
        else
        	_controller.setCenter(new GeoPoint((int) (37.448965*1e6f), (int) (-122.15857*1e6f)));
        _controller.setZoom(13);
        
        //add the HeatMap Overlay to the map
        _heatMap = new HeatMapOverlay(400, _map);
        _map.getOverlays().add(_heatMap);
        
        Thread connect = new Thread(new ConnectingRunnable());
        connect.start();        
	}
	
	public void onResume() {
		super.onResume();
		_mLoc.enableMyLocation();
	}
	public void onPause() {
		super.onPause();
		_mLoc.disableMyLocation();
	}
	/*Connects to the database, which Android mandates happens in separate thread*/
	private class ConnectingRunnable implements Runnable {
		public void run() {
			try {
				_mongo = new Mongo("localhost", 27017);
		        _db = _mongo.getDB(DATABASE_NAME);
		        _coll = _db.getCollection(COLLECTION_NAME);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void populateLater() {
        //give it some fake points - for now
        HeatPoint a = new HeatPoint(0,0,100);
        //lat lon intensity
        HeatPoint b = new HeatPoint((float) 37.4419, (float) 122.1419, 100);
        
        ArrayList<HeatPoint> test = new ArrayList<HeatPoint>();
        test.add(a);
        test.add(b);
        
        _heatMap.update(test);
	}
	
	public void updatePointsToCurrent() {
		Runnable r = new Runnable() {

			public void run() {
				GeoPoint center = _map.getMapCenter();
				
				double centerLat = center.getLatitudeE6() / 1E6d;
				double centerLong = center.getLongitudeE6() / 1e6d;
				
				double latSpan = _map.getLatitudeSpan() / 1E6d;
				double longSpan = _map.getLongitudeSpan() / 1E6d;
				
				double[] bottomLeft = new double[] { centerLat - (latSpan/2) , centerLong - (longSpan/2)  };
				double[] upperRight = new double[] {centerLat + (latSpan/2) , centerLong + (longSpan/2) };
				
				/*NOW: use this box to geospatial query*/
				QueryBuilder q = new QueryBuilder();
				q.nearSphere(centerLong, centerLat, 100);

				DBObject completedQuery = q.get();
				
				double[] loc = new double[]{centerLong, centerLat};
				
				//DBCursor results = _coll.find(completedQuery);
				DBCursor results = _coll.find();
				//DBCursor results = _coll.find( new BasicDBObject( "loc" , new BasicDBObject("$near", loc)));
				
				//System.out.println(results == null);
				//System.out.println(results.length());
				
				List<HeatPoint> heatPoints = new ArrayList<HeatPoint>();
				for( DBObject obj: results)
					heatPoints.add(HeatPoint.fromDBObject(obj));
				_heatMap.update(heatPoints);
				
			}
			
		};
		Thread t = new Thread(r);
		t.start();
		
		
		/*if (results != null) {
			List<HeatPoint> heatPoints = asHeatPoints(results.toArray());
			_heatMap.update(heatPoints);
		}*/
	}
	
	public List<HeatPoint> asHeatPoints(List<DBObject> alod) {
		List<HeatPoint> toReturn = new ArrayList<HeatPoint>(alod.size());
		for (int i = 0; i < alod.size(); i++) {
			toReturn.set(i, HeatPoint.fromDBObject(alod.get(i)));
		}
		return toReturn;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onClick(View v) {
		updatePointsToCurrent();		
	}

	public void onZoom(int old, int current) {
		updatePointsToCurrent();		
	}

	public void onPan(GeoPoint old, GeoPoint current) {
		updatePointsToCurrent();
	}

	public boolean onTouch(View v, MotionEvent event) {
		updatePointsToCurrent();
		return true;
	}
}
