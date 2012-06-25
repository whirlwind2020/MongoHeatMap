package com.mongodb.mapper;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.androidnatic.maps.HeatMapOverlay;
import com.androidnatic.maps.SimpleMapView;
import com.androidnatic.maps.events.PanChangeListener;
import com.androidnatic.maps.events.ZoomChangeListener;
import com.androidnatic.maps.model.HeatPoint;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ShowMapActivity extends MapActivity implements OnClickListener, PanChangeListener, ZoomChangeListener{	
	SimpleMapView _map;
	HeatMapOverlay _heatMap;
	MapController _controller;
	
	
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
        
        //start in PA
        _controller.setCenter(new GeoPoint(0, 0));
        _controller.setZoom(14);
        
        //pointer to current location
        MyLocationOverlay mLoc = new MyLocationOverlay(this, _map);
        mLoc.enableMyLocation();
        _map.getOverlays().add(mLoc);
        
        //add the HeatMap Overlay to the map
        _heatMap = new HeatMapOverlay(20000, _map);
        _map.getOverlays().add(_heatMap);
        
        Runnable run = new Runnable() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				populateLater();
			}
        };
       Thread t = new Thread(run);
       t.start();
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
	
	

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onClick(View v) {
		populateLater();		
	}

	public void onZoom(int old, int current) {
		populateLater();		
	}

	public void onPan(GeoPoint old, GeoPoint current) {
		populateLater();
	}
}
