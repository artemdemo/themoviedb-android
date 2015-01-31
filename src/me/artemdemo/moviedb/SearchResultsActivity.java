package me.artemdemo.moviedb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultsActivity extends ListActivity {

	/*
	 * strUrl will be used by FetchData class instance
	 * This variable should contain full url for api call
	 */
	static String strUrl = "";
	
	/*
	 * resultJSONObject
	 * Contain JSON object that came from the api call
	 */
	static JSONObject resultJSONObject;
	
	/*
	 * Waiting popup
	 */
	static ProgressDialog progress;
	
	private static final String TAG = "MovieDB-main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		Bundle extras = getIntent().getExtras();
		String jsonString = extras.getString("searchResults");
		JSONArray searchResults = null;
		List<String> searchResultsMovieIds = new ArrayList<String>();
		List<String> searchResultsMovieNames = new ArrayList<String>();
		List<String> searchResultsMovieYear = new ArrayList<String>();
		
		// Converting string to JSON
		if (extras != null) {
			try {
		        searchResults = new JSONArray(jsonString);
			    Log.v(TAG, searchResults.length() + "" );
			} catch (JSONException e) {
			    e.printStackTrace();
			}
		}
		
		// Iterating over JSON list of results
		if (searchResults != null) {
			for(int i = 0; i < searchResults.length(); i++)
			{
			    try {
					JSONObject objMovie = searchResults.getJSONObject(i);
					String fullDate = objMovie.getString("release_date");
					searchResultsMovieIds.add( objMovie.getString("id") );
					searchResultsMovieNames.add( objMovie.getString("original_title") );
					searchResultsMovieYear.add( fullDate.substring(0, 4) ); // I need only year
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		
	    // Printing results
		SearchResultsAdapter adapter = new SearchResultsAdapter(this, searchResultsMovieIds, searchResultsMovieNames, searchResultsMovieYear);
	    setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String movieId = (String) getListAdapter().getItem(position);
		//Toast.makeText(this, movieId + " selected", Toast.LENGTH_LONG).show();
		
		progress = new ProgressDialog(this);
		progress.setTitle("Loading");
		progress.setMessage("Wait while loading...");
		progress.show();
		
		strUrl = ApiFactory.getMovieById(movieId);
		new FetchData().execute();
	}
	
	
	
	/**
	 * FetchData is fetching movie data from the server
	 * Class will use 'url' variable - be sure that it is set before you instantiating this class
	 *
	 */
	private class FetchData extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient( new BasicHttpParams() );
			HttpGet httpget = new HttpGet(strUrl);
			
			httpget.setHeader("Content-type", "application/json");
			InputStream inputStream = null;
			
			String result = null;
			
			try {
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder theStringBuilder = new StringBuilder();
				
				String line = null;
				while((line = reader.readLine()) != null) {
					theStringBuilder.append(line + "\n");
				}
				result = theStringBuilder.toString();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					if ( inputStream != null ) inputStream.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			try {
				resultJSONObject = new JSONObject(result);
			}
			catch(JSONException e) {
				e.printStackTrace();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			progress.dismiss();
			
			try {
				Log.v(TAG, resultJSONObject.getString("title"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Here I need to start new activity
		}
		
	}
	
}
