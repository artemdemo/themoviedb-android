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
		List<String> searchResultsMovieIds = new ArrayList<String>();
		List<String> searchResultsMovieNames = new ArrayList<String>();
		List<String> searchResultsMovieYear = new ArrayList<String>();
		JSONArray searchResults = null;
		
		// Converting string to JSON
		if (extras != null) {
			try {
				String jsonString = extras.getString("searchResults");
		        searchResults = new JSONArray(jsonString);
			    Log.v(TAG, searchResults.length() + "" );
			} catch (JSONException e) {
			    e.printStackTrace();
			}
		}
		
		// Iterating over JSON list of results
		if (searchResults != null) {
			for(int i = 0; i < searchResults.length(); i++) {
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
		
		/*progress = new ProgressDialog(this);
		progress.setTitle("Loading");
		progress.setMessage("Wait while loading...");
		progress.show();
		
		strUrl = ApiFactory.getMovieById(movieId);
		new FetchData().execute();*/

		Intent intent = new Intent("android.intent.action.SMOVIE");
		String md = "{\"adult\":false,\"backdrop_path\":\"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg\",\"belongs_to_collection\":{\"id\":528,\"name\":\"The Terminator Collection\",\"poster_path\":\"/vxiKtcxAJxHhlg2H1X8y7zcM3k6.jpg\",\"backdrop_path\":\"/tP1SCFnlYTHSMqp1yuFDVTQeLUD.jpg\"},\"budget\":200000000,\"genres\":[{\"id\":28,\"name\":\"Action\"},{\"id\":18,\"name\":\"Drama\"},{\"id\":878,\"name\":\"Science Fiction\"},{\"id\":53,\"name\":\"Thriller\"}],\"homepage\":\"http://www.terminatorsalvation.com\",\"id\":534,\"imdb_id\":\"tt0438488\",\"original_language\":\"en\",\"original_title\":\"Terminator Salvation\",\"overview\":\"All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past -- and whether he's friend or foe.\",\"popularity\":4.30919672839888,\"poster_path\":\"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg\",\"production_companies\":[{\"name\":\"The Halcyon Company\",\"id\":4021},{\"name\":\"Wonderland Sound and Vision\",\"id\":4022}],\"production_countries\":[{\"iso_3166_1\":\"DE\",\"name\":\"Germany\"},{\"iso_3166_1\":\"GB\",\"name\":\"United Kingdom\"},{\"iso_3166_1\":\"IT\",\"name\":\"Italy\"},{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"2009-05-20\",\"revenue\":280610053,\"runtime\":115,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"},{\"iso_639_1\":\"it\",\"name\":\"Italiano\"}],\"status\":\"Released\",\"tagline\":\"The End Begins.\",\"title\":\"Terminator Salvation\",\"video\":false,\"vote_average\":5.9,\"vote_count\":906}";

		intent.putExtra("singleMovieData", md); // Passing movie data to the Single Movie Activity
		
		startActivity(intent);
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

/*
 * {"adult":false,"backdrop_path":"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg","belongs_to_collection":{"id":528,"name":"The Terminator Collection","poster_path":"/vxiKtcxAJxHhlg2H1X8y7zcM3k6.jpg","backdrop_path":"/tP1SCFnlYTHSMqp1yuFDVTQeLUD.jpg"},"budget":200000000,"genres":[{"id":28,"name":"Action"},{"id":18,"name":"Drama"},{"id":878,"name":"Science Fiction"},{"id":53,"name":"Thriller"}],"homepage":"http://www.terminatorsalvation.com","id":534,"imdb_id":"tt0438488","original_language":"en","original_title":"Terminator Salvation","overview":"All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past -- and whether he's friend or foe.","popularity":4.30919672839888,"poster_path":"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg","production_companies":[{"name":"The Halcyon Company","id":4021},{"name":"Wonderland Sound and Vision","id":4022}],"production_countries":[{"iso_3166_1":"DE","name":"Germany"},{"iso_3166_1":"GB","name":"United Kingdom"},{"iso_3166_1":"IT","name":"Italy"},{"iso_3166_1":"US","name":"United States of America"}],"release_date":"2009-05-20","revenue":280610053,"runtime":115,"spoken_languages":[{"iso_639_1":"en","name":"English"},{"iso_639_1":"it","name":"Italiano"}],"status":"Released","tagline":"The End Begins.","title":"Terminator Salvation","video":false,"vote_average":5.9,"vote_count":906}
 * {\"adult\":false,\"backdrop_path\":\"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg\",\"belongs_to_collection\":{\"id\":528,\"name\":\"The Terminator Collection\",\"poster_path\":\"/vxiKtcxAJxHhlg2H1X8y7zcM3k6.jpg\",\"backdrop_path\":\"/tP1SCFnlYTHSMqp1yuFDVTQeLUD.jpg\"},\"budget\":200000000,\"genres\":[{\"id\":28,\"name\":\"Action\"},{\"id\":18,\"name\":\"Drama\"},{\"id\":878,\"name\":\"Science Fiction\"},{\"id\":53,\"name\":\"Thriller\"}],\"homepage\":\"http://www.terminatorsalvation.com\",\"id\":534,\"imdb_id\":\"tt0438488\",\"original_language\":\"en\",\"original_title\":\"Terminator Salvation\",\"overview\":\"All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past -- and whether he's friend or foe.\",\"popularity\":4.30919672839888,\"poster_path\":\"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg\",\"production_companies\":[{\"name\":\"The Halcyon Company\",\"id\":4021},{\"name\":\"Wonderland Sound and Vision\",\"id\":4022}],\"production_countries\":[{\"iso_3166_1\":\"DE\",\"name\":\"Germany\"},{\"iso_3166_1\":\"GB\",\"name\":\"United Kingdom\"},{\"iso_3166_1\":\"IT\",\"name\":\"Italy\"},{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"2009-05-20\",\"revenue\":280610053,\"runtime\":115,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"},{\"iso_639_1\":\"it\",\"name\":\"Italiano\"}],\"status\":\"Released\",\"tagline\":\"The End Begins.\",\"title\":\"Terminator Salvation\",\"video\":false,\"vote_average\":5.9,\"vote_count\":906}
 */