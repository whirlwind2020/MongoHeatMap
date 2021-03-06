/*
 * Copyright (C) 2011 by Vinicius Carvalho (vinnie@androidnatic.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.androidnatic.maps.model;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author evincar
 *
 */
public class HeatPoint {
	public HeatPoint(float lat, float lon, int intensity) {
		this.lat = lat;
		this.lon = lon;
		this.intensity = intensity;
	}

	public float lat;
	public float lon;
	public int intensity;
	
	public HeatPoint(){
		this(0f,0f,0);
	}
	
	public HeatPoint(float lat, float lon){
		this(lat,lon,1);
	}
	
	public static HeatPoint fromDBObject(DBObject cur) {
		HeatPoint toReturn = new HeatPoint();
		toReturn.intensity = (Integer) cur.get("intensity");
		toReturn.intensity *= 100/37.0;
		toReturn.lon = ((Double)((BasicDBList)cur.get("loc")).get(1)).floatValue();
		toReturn.lat = ((Double)((BasicDBList)cur.get("loc")).get(0)).floatValue();
		return toReturn;
	}
	
	/* JSON representation of a HeatPoint*/
	public static DBObject toDBObject(HeatPoint p) {
		BasicDBObject b = new BasicDBObject();
		b.put("lat", p.lat);
		b.put("lon", p.lon);
		b.put("intensity", p.intensity);
		return b;
	}
	
	public String toString() {
		return "[" + this.lat + "," + this.lon + "], intensity " + this.intensity;
	}
	
}
