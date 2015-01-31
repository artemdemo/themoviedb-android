package me.artemdemo.moviedb;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


public class SingleMovieActivity extends Activity {
	
	private static final String TAG = "MovieDB-main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.single_movie);
		
		Bundle extras = getIntent().getExtras();
		JSONObject singleMovie = null;
		
		// Converting string to JSON
		if (extras != null) {
			try {
				String jsonString = extras.getString("singleMovieData");
		        singleMovie = new JSONObject(jsonString);
			} catch (JSONException e) {
			    e.printStackTrace();
			}
		}

		TextView lblMovieName = (TextView) findViewById(R.id.lblMovieName);
	    TextView lblTagline = (TextView) findViewById(R.id.lblTagline);
	    TextView lblGenres = (TextView) findViewById(R.id.lblGenres);
	    TextView lblReleaseDate = (TextView) findViewById(R.id.lblReleaseDate);
	    TextView lblRunTime = (TextView) findViewById(R.id.lblRunTime);
	    TextView lblBudget = (TextView) findViewById(R.id.lblBudget);
	    TextView lblRevenue = (TextView) findViewById(R.id.lblRevenue);
	    TextView lblVoteAverage = (TextView) findViewById(R.id.lblVoteAverage);
	    TextView lblOverview = (TextView) findViewById(R.id.lblOverview);
		
		if ( singleMovie != null ) {
			try {
				// Fetching genres from array
				JSONArray arrGenres = singleMovie.getJSONArray("genres");
				String strGenres = "Genres: ";
				for (int i = 0; i < arrGenres.length(); i++) {
					try {
						JSONObject objGenre = arrGenres.getJSONObject(i);
						strGenres = strGenres + objGenre.getString("name");
						if ( i < arrGenres.length() - 1 ) {
							strGenres = strGenres + ", ";
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				// Printing movie data
				lblMovieName.setText( singleMovie.getString("original_title") );
				lblTagline.setText( singleMovie.getString("tagline") );
				lblGenres.setText( strGenres );
				lblReleaseDate.setText( "Release Date: " + singleMovie.getString("release_date") );
				lblRunTime.setText( "Runtime: " + singleMovie.getString("runtime") + " min" );
				lblBudget.setText( "Budget: " + singleMovie.getString("budget") + " USD" );
				lblRevenue.setText( "Revenue: " + singleMovie.getString("revenue") + " USD" );
				lblVoteAverage.setText( "Vote average: " + singleMovie.getString("vote_average") );
				lblOverview.setText( singleMovie.getString("overview") );
				
				// Downloading poster
				String imgUrl = "http://image.tmdb.org/t/p/w185/" + singleMovie.getString("poster_path");
				new DownloadImageTask((ImageView) findViewById(R.id.imgMovie)).execute( imgUrl );
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Async task to fetch poster from the server
	 *
	 */
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
	
}
