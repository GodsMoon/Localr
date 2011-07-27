package com.nightshadelabs.localr;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class Util {

	public static Drawable getURLDrawable(String url)
	{
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet(url);

		try {
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpget);
			
			// Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
 
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                BitmapDrawable bd = new BitmapDrawable(instream);
                return bd;
            }
		
		} catch (ClientProtocolException e) {
			Log.e("Localr error ",e.toString());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	public static Bitmap getURLBitmap(String url)
	{
        try {
			//This one line replaces whole getURLDrawable method above. Use it instead.
			return BitmapFactory.decodeStream(new URL(url).openStream(), null,  null);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<FlickrObject> getInterestingPhotos(String url) {
		ArrayList<FlickrObject> flickrList = new ArrayList<FlickrObject>();
		
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();  
		
			Document doc = documentBuilder.parse(new URL(url).openStream());
			
			NodeList nList = doc.getElementsByTagName("photo");
			
			for(int i = 0; i<20;i++)
			{
				Element  nNode = (Element) nList.item(i);
				//url format
				//http://farm{farm-id}.static.flickr.com/{server-id}/{id}_{secret}.jpg
				String title = nNode.getAttribute("title");
				
				String id = nNode.getAttribute("id");				
				String farm = nNode.getAttribute("farm");
				String server = nNode.getAttribute("server");
				String secret = nNode.getAttribute("secret");
				
				String fullImageName = new StringBuilder("http://farm")
										.append(farm).append(".static.flickr.com/")
										.append(server).append("/")
										.append(id).append("_")
										.append(secret).append("_z.jpg")
										.toString();
				
				FlickrObject fo = new FlickrObject(title);
				fo.imagePath = fullImageName;
				fo.image = getURLBitmap(fullImageName);
				flickrList.add(fo);
			}
		}
		catch(Exception e)
		{
			Log.e("getDoc", e.toString());
		}

		
		
		return flickrList;
	}
}
