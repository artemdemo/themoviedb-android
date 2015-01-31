package me.artemdemo.moviedb;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONException;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class SearchResultsActivity extends ListActivity {

	private static final String TAG = "MovieDB-main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		String jsonString = extras.getString("searchResults");
		JSONArray searchResults = null;
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
					searchResultsMovieNames.add( objMovie.getString("original_title") );
					searchResultsMovieYear.add( objMovie.getString("release_date") );
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		
	    // Printing results
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        R.layout.rowlayout, R.id.lblMovie, searchResultsMovieNames);
	    setListAdapter(adapter);
	}
	
}
