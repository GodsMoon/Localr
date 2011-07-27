package com.nightshadelabs.localr;

import java.util.List;
import android.app.ListActivity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalrActivity extends ListActivity {
    private FlickrAdapter adapter;
	private Context context;
	private List<FlickrObject> flickrList;
	
	//get your own Flickr api key at http://www.flickr.com/services/apps/create/apply/
	private static String API_KEY = "XXX";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.main);
        
        TextView large = (TextView)findViewById(R.id.large_text);
        large.setText(R.string.hello);
        
        context = this;
        
        new DownloadImagesTask().execute();
        
        //Manually add objects to list before parsing flickr api for testing
        /*FlickrObject fo = new FlickrObject("test");
        fo.imagePath = "http://www.google.com/intl/en_com/images/srpr/logo2w.png";
        flickrList.add(fo);
        
        FlickrObject fo2 = new FlickrObject("test2");
        fo2.imagePath = "http://farm7.static.flickr.com/6137/5974355431_50c7ce64e4_m.jpg";
        flickrList.add(fo2);*/
	
    }
    /** Called right after onCreate and every time your app regains focus */
    @Override
	protected void onResume() {
    	
    	// Try moving new DownloadImagesTask().execute(); here and see the difference when you re-launch Localr
		super.onResume();
	}


    /** Download images on a separate thread asynchronously. */
	private class DownloadImagesTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);  
			criteria.setAccuracy(Criteria.ACCURACY_FINE);  
				
			Location location = lm.getLastKnownLocation(lm.getBestProvider(criteria, true));
			
			//This works on my phone, but not my tablet. Use getBestProvider for best results.
			//Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			double longitude = location.getLongitude();
			double latitude = location.getLatitude();
		        
			//Get the "Most Interesting" photos on Flickr regardless of location.
			//flickrList = Util.getInterestingPhotos("http://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key="+API_KEY+"&format=rest");
			
			flickrList = Util.getInterestingPhotos("http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="+API_KEY+"&tags=%2A&sort=interestingness-desc"+
					"&lat="+latitude+"&lon="+longitude+"&radius=5&format=rest");
	       
	        
			return null;
		}

		protected void onPostExecute(Void params) {
		        
			setProgressBarIndeterminateVisibility(false);
			
	 		adapter = new FlickrAdapter(context,R.id.image_name, flickrList);
	 		
	 		setListAdapter(adapter);
	 		
        }
    }
    /* custom ArrayAdapter. http://developer.android.com/reference/android/widget/ArrayAdapter.html */
    private static class FlickrAdapter extends ArrayAdapter<FlickrObject> { 
    	
    	private LayoutInflater mInflater;
        List<FlickrObject> flickrListItems;
        
        static class ViewHolder {
            TextView name;
            ImageView image;
        }
        
    	public FlickrAdapter(Context context, int textViewResourceId, List<FlickrObject> objects) {
			super(context, textViewResourceId, objects);

			mInflater = LayoutInflater.from(context);
			flickrListItems = objects;

		}

    	/* the ViewHolder Pattern is from: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/view/List14.html */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_image_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.image_name);
                holder.image = (ImageView) convertView.findViewById(R.id.image);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            FlickrObject listFlickrObject = flickrListItems.get(position);
            holder.name.setText(listFlickrObject.name);
            holder.image.setImageBitmap(listFlickrObject.image);
            
            //pull images in dynamically instead of loading all of them into the array beforehand
			//Drawable d = Util.getURLDrawable(listFO.imagePath);
			//holder.image.setImageDrawable(d);

            return convertView;
        }

    }
}