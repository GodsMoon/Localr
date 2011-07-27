package com.nightshadelabs.localr;

import android.graphics.Bitmap;


public class FlickrObject {

	public String name;
	public String imagePath;
	public Bitmap image;
	
	public FlickrObject()
	{
		
	}
	
	public FlickrObject(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return name.toString();
	}
	    	
}
