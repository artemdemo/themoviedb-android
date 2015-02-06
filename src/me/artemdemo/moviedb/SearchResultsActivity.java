package me.artemdemo.moviedb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.artemdemo.moviedb.fragments.AppDialogFragment;
import me.artemdemo.moviedb.fragments.SearchResultsAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class SearchResultsActivity extends ListActivity {

	/*
	 * strUrl will be used by FetchData class instance
	 * This variable should contain full url for api call
	 */
	static String strUrl = "";
	
	/*
	 * strResult
	 * Contain string of JSON object that came from the server
	 * I don't need to convert it to JSONObject, case it should be passed as is to the next activity
	 */
	static String strResult;
	
	/*
	 * Following arrays will contain data about list of results
	 */
	private List<String> searchResultsMovieIds = new ArrayList<String>();
	private List<String> searchResultsMovieNames = new ArrayList<String>();
	private List<String> searchResultsMovieYear = new ArrayList<String>();
	private int listPositionIndex = 0; // after new content loaded I need to scroll it to the last position
	private int listPositionTop = 0;
	
	/*
	 * I will use this variable in determining whether user reached last item or not
	 */
	private int preLast;
	
	private int currentPage = 1;
	
	private enum httpAction {
		GET_MOVIE, GET_NEXT_PAGE
	};
	
	private httpAction currentAction;
	
	/*
	 * currentSearchType - used to determine what search type I'm using
	 * For different types I need to fetch different parameters from extra data
	 */
	private ApiFactory.SearchType currentSearchType;
	
	private int intYear = 0; // Will be filled with real data if needed for next search
	
	/*
	 * Waiting popup
	 */
	static ProgressDialog progress;
	
	private static final String TAG = "MovieDB-searchResults2014";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		Bundle extras = getIntent().getExtras();
		JSONArray searchResults = null;
		
		// Converting string to JSON
		if (extras != null) {
			try {
				String jsonString = extras.getString("searchResults");
				currentSearchType = ApiFactory.SearchType.valueOf(extras.getString("searchType"));
				switch(currentSearchType) {
					case BY_YEAR:
						intYear = extras.getInt("intYear");
						break;
				}
		        searchResults = new JSONArray(jsonString);
		      //Log.v(TAG, extras.getString("searchType") );
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
	    
	    // Adding scroll listener to current list view
	    ListView listViewResults = getListView();
	    listViewResults.setOnScrollListener(new OnScrollListener(){
	    	
	    	/*
	    	 * Waiting popup
	    	 */
	    	//private ProgressDialog progress;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView lv, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				switch(lv.getId()) {
			        case android.R.id.list:     
			           // Sample calculation to determine if the last 
			           // item is fully visible.
			           final int lastItem = firstVisibleItem + visibleItemCount;
			           if(lastItem == totalItemCount) {
							if(preLast != lastItem){ // avoiding multiple calls for last item
								// Save list position to scroll here after data is loaded
								listPositionIndex = lv.getFirstVisiblePosition();
								View v = lv.getChildAt(0);
								listPositionTop = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());
								
								progress = new ProgressDialog(SearchResultsActivity.this);
								progress.setTitle("Loading");
								progress.setMessage("Wait while loading...");
								progress.show();
								
								currentAction = httpAction.GET_NEXT_PAGE;
								currentPage++;
								strUrl = ApiFactory.getMoviesByYearUrl(intYear, currentPage);
								Log.v(TAG, strUrl);
								new FetchData().execute();
								
								preLast = lastItem;
							}
			           }
			    }
			}});
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String movieId = (String) getListAdapter().getItem(position);
		
		progress = new ProgressDialog(this);
		progress.setTitle("Loading");
		progress.setMessage("Wait while loading...");
		progress.show();
		
		currentAction = httpAction.GET_MOVIE;
		
		strUrl = ApiFactory.getMovieById(movieId);
		new FetchData().execute();

		/*Intent intent = new Intent("android.intent.action.SMOVIE");
		String md = "{\"adult\":false,\"backdrop_path\":\"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg\",\"belongs_to_collection\":{\"id\":528,\"name\":\"The Terminator Collection\",\"poster_path\":\"/vxiKtcxAJxHhlg2H1X8y7zcM3k6.jpg\",\"backdrop_path\":\"/tP1SCFnlYTHSMqp1yuFDVTQeLUD.jpg\"},\"budget\":200000000,\"genres\":[{\"id\":28,\"name\":\"Action\"},{\"id\":18,\"name\":\"Drama\"},{\"id\":878,\"name\":\"Science Fiction\"},{\"id\":53,\"name\":\"Thriller\"}],\"homepage\":\"http://www.terminatorsalvation.com\",\"id\":534,\"imdb_id\":\"tt0438488\",\"original_language\":\"en\",\"original_title\":\"Terminator Salvation\",\"overview\":\"All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past -- and whether he's friend or foe.\",\"popularity\":4.30919672839888,\"poster_path\":\"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg\",\"production_companies\":[{\"name\":\"The Halcyon Company\",\"id\":4021},{\"name\":\"Wonderland Sound and Vision\",\"id\":4022}],\"production_countries\":[{\"iso_3166_1\":\"DE\",\"name\":\"Germany\"},{\"iso_3166_1\":\"GB\",\"name\":\"United Kingdom\"},{\"iso_3166_1\":\"IT\",\"name\":\"Italy\"},{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"2009-05-20\",\"revenue\":280610053,\"runtime\":115,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"},{\"iso_639_1\":\"it\",\"name\":\"Italiano\"}],\"status\":\"Released\",\"tagline\":\"The End Begins.\",\"title\":\"Terminator Salvation\",\"video\":false,\"vote_average\":5.9,\"vote_count\":906}";
		intent.putExtra("singleMovieData", md); // Passing movie data to the Single Movie Activity		
		startActivity(intent);*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Creating menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if ( id == R.id.mBtnHome ) {
			Intent i = new Intent(this, MainActivity.class);
	        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(i);
			return true;
		} else if ( id == R.id.mBtnExit ) {
			// Exiting application
			DialogFragment dFragment = new AppDialogFragment();
			dFragment.show(getFragmentManager(), "theDialog");
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Add results of next page to the current page
	 */
	public void addResultsToMovieList(String strResults) {
		try {
			JSONObject resultJSONObject = new JSONObject(strResults);
			JSONArray searchResults = resultJSONObject.getJSONArray("results");
			//Log.v(TAG, resultJSONObject.getString("page"));
			for(int i = 0; i < searchResults.length(); i++) {
			    try {
					JSONObject objMovie = searchResults.getJSONObject(i);
					String fullDate = objMovie.getString("release_date");
					searchResultsMovieIds.add( objMovie.getString("id") );
					searchResultsMovieNames.add( objMovie.getString("original_title") );
					searchResultsMovieYear.add( fullDate.substring(0, 4) ); // I need only year
					
					// Printing results
					SearchResultsAdapter adapter = new SearchResultsAdapter(this, searchResultsMovieIds, searchResultsMovieNames, searchResultsMovieYear);
				    setListAdapter(adapter);
				    ListView listViewResults = getListView();
				    listViewResults.setSelectionFromTop(listPositionIndex, listPositionTop);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
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
				strResult = theStringBuilder.toString();
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
			
			return strResult;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			switch(currentAction) {
				case GET_MOVIE:
					Intent intent = new Intent("android.intent.action.SMOVIE");
					intent.putExtra("singleMovieData", strResult); // Passing movie data to the Single Movie Activity		
					startActivity(intent);
					break;
				case GET_NEXT_PAGE:
					addResultsToMovieList(result); // Adding results to current list of movies
					break;
			}
			progress.dismiss();
		}
		
	}
	
}

/*
 * {"adult":false,"backdrop_path":"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg","belongs_to_collection":{"id":528,"name":"The Terminator Collection","poster_path":"/vxiKtcxAJxHhlg2H1X8y7zcM3k6.jpg","backdrop_path":"/tP1SCFnlYTHSMqp1yuFDVTQeLUD.jpg"},"budget":200000000,"genres":[{"id":28,"name":"Action"},{"id":18,"name":"Drama"},{"id":878,"name":"Science Fiction"},{"id":53,"name":"Thriller"}],"homepage":"http://www.terminatorsalvation.com","id":534,"imdb_id":"tt0438488","original_language":"en","original_title":"Terminator Salvation","overview":"All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past -- and whether he's friend or foe.","popularity":4.30919672839888,"poster_path":"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg","production_companies":[{"name":"The Halcyon Company","id":4021},{"name":"Wonderland Sound and Vision","id":4022}],"production_countries":[{"iso_3166_1":"DE","name":"Germany"},{"iso_3166_1":"GB","name":"United Kingdom"},{"iso_3166_1":"IT","name":"Italy"},{"iso_3166_1":"US","name":"United States of America"}],"release_date":"2009-05-20","revenue":280610053,"runtime":115,"spoken_languages":[{"iso_639_1":"en","name":"English"},{"iso_639_1":"it","name":"Italiano"}],"status":"Released","tagline":"The End Begins.","title":"Terminator Salvation","video":false,"vote_average":5.9,"vote_count":906}
 * {\"adult\":false,\"backdrop_path\":\"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg\",\"belongs_to_collection\":{\"id\":528,\"name\":\"The Terminator Collection\",\"poster_path\":\"/vxiKtcxAJxHhlg2H1X8y7zcM3k6.jpg\",\"backdrop_path\":\"/tP1SCFnlYTHSMqp1yuFDVTQeLUD.jpg\"},\"budget\":200000000,\"genres\":[{\"id\":28,\"name\":\"Action\"},{\"id\":18,\"name\":\"Drama\"},{\"id\":878,\"name\":\"Science Fiction\"},{\"id\":53,\"name\":\"Thriller\"}],\"homepage\":\"http://www.terminatorsalvation.com\",\"id\":534,\"imdb_id\":\"tt0438488\",\"original_language\":\"en\",\"original_title\":\"Terminator Salvation\",\"overview\":\"All grown up in post-apocalyptic 2018, John Connor must lead the resistance of humans against the increasingly dominating militaristic robots. But when Marcus Wright appears, his existence confuses the mission as Connor tries to determine whether Wright has come from the future or the past -- and whether he's friend or foe.\",\"popularity\":4.30919672839888,\"poster_path\":\"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg\",\"production_companies\":[{\"name\":\"The Halcyon Company\",\"id\":4021},{\"name\":\"Wonderland Sound and Vision\",\"id\":4022}],\"production_countries\":[{\"iso_3166_1\":\"DE\",\"name\":\"Germany\"},{\"iso_3166_1\":\"GB\",\"name\":\"United Kingdom\"},{\"iso_3166_1\":\"IT\",\"name\":\"Italy\"},{\"iso_3166_1\":\"US\",\"name\":\"United States of America\"}],\"release_date\":\"2009-05-20\",\"revenue\":280610053,\"runtime\":115,\"spoken_languages\":[{\"iso_639_1\":\"en\",\"name\":\"English\"},{\"iso_639_1\":\"it\",\"name\":\"Italiano\"}],\"status\":\"Released\",\"tagline\":\"The End Begins.\",\"title\":\"Terminator Salvation\",\"video\":false,\"vote_average\":5.9,\"vote_count\":906}
 */