package me.artemdemo.moviedb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import me.artemdemo.moviedb.fragments.AppDialogFragment;
import me.artemdemo.moviedb.fragments.SelectGenreFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends Activity {
	
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
	
	/*
	 * This variable will contain selected genres
	 */
	public static ArrayList<Integer> selectedGenres = null;
	
	public int intYear = 0;

	public ApiFactory.SearchType currentSearchType;
	
	/*
	 * Popup show variables
	 */
	LinearLayout layoutOfPopup;
	PopupWindow popupMessage;
	Button popupButton;
	TextView popupText;
	
	private static final String TAG = "MovieDB-main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		progress = new ProgressDialog(this);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		
		btnSearch.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				strUrl = "";
				EditText inputYear = (EditText) findViewById(R.id.inputYear);
				String strYear = inputYear.getText().toString();
				int intCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
				
				if(strYear != null && !strYear.isEmpty()) {
					intYear = Integer.parseInt(strYear);
					if ( intYear > 1900 && intYear < intCurrentYear ) {
						currentSearchType = ApiFactory.SearchType.BY_YEAR;
						strUrl = ApiFactory.getMoviesByYearUrl(intYear, 1);
					} else {
						progress.dismiss();
						showAlert("Please use correct value for Year");
					}
				} else {
					intYear = 0;
				}
				
				EditText inputTitle = (EditText) findViewById(R.id.inputTitle);
				String strTitle = inputTitle.getText().toString();
				
				if(strTitle != null && !strTitle.isEmpty()) {
					if (intYear > 0) {
						currentSearchType = ApiFactory.SearchType.BY_YEAR_AND_TITLE;
						strUrl = ApiFactory.getMoviesByYearAndTitleUrl(intYear, strTitle, 1);
					} else {
						currentSearchType = ApiFactory.SearchType.BY_TITLE;
						strUrl = ApiFactory.getMoviesByTitleUrl(strTitle, 1);
					}
				}
				

				/*
				 * Check if we can make api call
				 */
				if ( strUrl != "" ) {
					progress.setTitle("Loading");
					progress.setMessage("Wait while loading...");
					progress.show();
					new FetchData().execute();
				}

				/*
				 * DUMMY functionality
				Intent intent = new Intent("android.intent.action.SRESULTS");
				String sr = "[{\"adult\":false,\"backdrop_path\":\"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg\",\"id\":534,\"original_title\":\"Terminator Salvation\",\"release_date\":\"2009-05-20\",\"poster_path\":\"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg\",\"popularity\":4.19532242799627,\"title\":\"Terminator Salvation\",\"video\":false,\"vote_average\":5.9,\"vote_count\":903},{\"adult\":false,\"backdrop_path\":\"/5XPPB44RQGfkBrbJxmtdndKz05n.jpg\",\"id\":19995,\"original_title\":\"Avatar\",\"release_date\":\"2009-12-18\",\"poster_path\":\"/8Ic8rRVoVrDJJlXzVzGxAesufUV.jpg\",\"popularity\":3.73434127940165,\"title\":\"Avatar\",\"video\":false,\"vote_average\":6.9,\"vote_count\":6036},{\"adult\":false,\"backdrop_path\":\"/xBKGJQsAIeweesB79KC89FpBrVr.jpg\",\"id\":278,\"original_title\":\"The Shawshank Redemption\",\"release_date\":\"1994-09-14\",\"poster_path\":\"/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg\",\"popularity\":3.51625003390461,\"title\":\"The Shawshank Redemption\",\"video\":false,\"vote_average\":8.1,\"vote_count\":3593},{\"adult\":false,\"backdrop_path\":\"/jxdSxqAFrdioKgXwgTs5Qfbazjq.jpg\",\"id\":10138,\"original_title\":\"Iron Man 2\",\"release_date\":\"2010-05-07\",\"poster_path\":\"/1LoT5WsN4Lc1aR7A18ciSA1LoMy.jpg\",\"popularity\":3.2796482936724,\"title\":\"Iron Man 2\",\"video\":false,\"vote_average\":6.6,\"vote_count\":3330},{\"adult\":false,\"backdrop_path\":\"/nMulOcoR6HAahofkcuo4mtA0o9j.jpg\",\"id\":10191,\"original_title\":\"How to Train Your Dragon\",\"release_date\":\"2010-03-05\",\"poster_path\":\"/zMAm3WYmvD40FaWFsOmpicQFabz.jpg\",\"popularity\":3.1753207014147,\"title\":\"How to Train Your Dragon\",\"video\":false,\"vote_average\":7.2,\"vote_count\":1906},{\"adult\":false,\"backdrop_path\":\"/6xKCYgH16UuwEGAyroLU6p8HLIn.jpg\",\"id\":238,\"original_title\":\"The Godfather\",\"release_date\":\"1972-03-15\",\"poster_path\":\"/d4KNaTrltq6bpkFS01pYtyXa09m.jpg\",\"popularity\":2.84774411598614,\"title\":\"The Godfather\",\"video\":false,\"vote_average\":8.1,\"vote_count\":2262},{\"adult\":false,\"backdrop_path\":\"/s2bT29y0ngXxxu2IA8AOzzXTRhd.jpg\",\"id\":27205,\"original_title\":\"Inception\",\"release_date\":\"2010-07-16\",\"poster_path\":\"/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg\",\"popularity\":2.71852258538699,\"title\":\"Inception\",\"video\":false,\"vote_average\":7.6,\"vote_count\":6307},{\"adult\":false,\"backdrop_path\":\"/rpvDBeVazJyBV5SxtnQWIgL5SIb.jpg\",\"id\":10193,\"original_title\":\"Toy Story 3\",\"release_date\":\"2010-06-17\",\"poster_path\":\"/tOwAAVeL1p3ls9dhOBo45ElodU3.jpg\",\"popularity\":2.41043273318264,\"title\":\"Toy Story 3\",\"video\":true,\"vote_average\":7.3,\"vote_count\":1796},{\"adult\":false,\"backdrop_path\":\"/39LohvXfll5dGCQIV9B9VJ16ImE.jpg\",\"id\":18785,\"original_title\":\"The Hangover\",\"release_date\":\"2009-06-05\",\"poster_path\":\"/eshEkiG7NmU4ekA8CtpIdYiYufZ.jpg\",\"popularity\":2.34621561396029,\"title\":\"The Hangover\",\"video\":false,\"vote_average\":6.9,\"vote_count\":2379},{\"adult\":false,\"backdrop_path\":\"/17Pf2aGFfehYyz9Ru2S6fzcXvEO.jpg\",\"id\":12444,\"original_title\":\"Harry Potter and the Deathly Hallows: Part 1\",\"release_date\":\"2010-11-19\",\"poster_path\":\"/maP4MTfPCeVD2FZbKTLUgriOW4R.jpg\",\"popularity\":2.31172280657409,\"title\":\"Harry Potter and the Deathly Hallows: Part 1\",\"video\":false,\"vote_average\":7.0,\"vote_count\":1907},{\"adult\":false,\"backdrop_path\":\"/qf59pVUHbY9z0Ke9Jg6HQghNJhM.jpg\",\"id\":23483,\"original_title\":\"Kick-Ass\",\"release_date\":\"2010-04-16\",\"poster_path\":\"/yYy7bJ0HuSudtHDksGODZohRQWo.jpg\",\"popularity\":2.28674931634547,\"title\":\"Kick-Ass\",\"video\":false,\"vote_average\":6.8,\"vote_count\":1737},{\"adult\":false,\"backdrop_path\":\"/AmCtBQc5KxJfJVdS2TkY4Pc9lPd.jpg\",\"id\":12155,\"original_title\":\"Alice in Wonderland\",\"release_date\":\"2010-03-04\",\"poster_path\":\"/pvEE5EN5N1yjmHmldfL4aJWm56l.jpg\",\"popularity\":2.26301099100199,\"title\":\"Alice in Wonderland\",\"video\":false,\"vote_average\":6.0,\"vote_count\":1498},{\"adult\":false,\"backdrop_path\":\"/4y5TDUZlqUmWWtjTAznWb6CFpzt.jpg\",\"id\":857,\"original_title\":\"Saving Private Ryan\",\"release_date\":\"1998-07-24\",\"poster_path\":\"/35CMz4t7PuUiQqt5h4u5nbrXZlF.jpg\",\"popularity\":2.20014781253031,\"title\":\"Saving Private Ryan\",\"video\":false,\"vote_average\":7.4,\"vote_count\":1955},{\"adult\":false,\"backdrop_path\":\"/3CbUdwyKnEcLSLkQYWJfi8H6gPO.jpg\",\"id\":41233,\"original_title\":\"Step Up 3D\",\"release_date\":\"2010-08-06\",\"poster_path\":\"/q8Pm7UpAqDdxo1Xnt29EHHAl2u2.jpg\",\"popularity\":2.19265298107209,\"title\":\"Step Up 3D\",\"video\":false,\"vote_average\":6.6,\"vote_count\":118},{\"adult\":false,\"backdrop_path\":\"/pOWkHXTtg6OTVn5Z94KxvGB8a3M.jpg\",\"id\":37799,\"original_title\":\"The Social Network\",\"release_date\":\"2010-09-30\",\"poster_path\":\"/hKTwHT6WtYhtG8U2PJQSQH2y4oD.jpg\",\"popularity\":2.1514421780243,\"title\":\"The Social Network\",\"video\":false,\"vote_average\":6.9,\"vote_count\":1015},{\"adult\":false,\"backdrop_path\":\"/ma5Ps40txofscTtWpjs39swMnwZ.jpg\",\"id\":18823,\"original_title\":\"Clash of the Titans\",\"release_date\":\"2010-04-02\",\"poster_path\":\"/n8W2Y72VzSi8Yz6IvYWwfoiMTS6.jpg\",\"popularity\":2.07785102538956,\"title\":\"Clash of the Titans\",\"video\":false,\"vote_average\":5.7,\"vote_count\":928},{\"adult\":false,\"backdrop_path\":\"/x5u73uBylbyCCnkzUGzt3uozqRp.jpg\",\"id\":27578,\"original_title\":\"The Expendables\",\"release_date\":\"2010-08-03\",\"poster_path\":\"/y2qJoYxOhzyidsA60Mqn29H38Lk.jpg\",\"popularity\":2.07174132720209,\"title\":\"The Expendables\",\"video\":false,\"vote_average\":5.9,\"vote_count\":1371},{\"adult\":false,\"backdrop_path\":\"/uFg02Gt69UM6Ouam4slMdD0s029.jpg\",\"id\":38757,\"original_title\":\"Tangled\",\"release_date\":\"2010-11-24\",\"poster_path\":\"/re6AOJbhBk9FIK3knwU6rYlbPDx.jpg\",\"popularity\":2.05549227536821,\"title\":\"Tangled\",\"video\":false,\"vote_average\":7.0,\"vote_count\":1101},{\"adult\":false,\"backdrop_path\":\"/lcRGF2RpurdvBiSQVocfzRrxV9u.jpg\",\"id\":14756,\"original_title\":\"葉問\",\"release_date\":\"2008-12-12\",\"poster_path\":\"/8knFfuqW289DJi2cpPl4RVTDkbo.jpg\",\"popularity\":2.04586763967344,\"title\":\"Ip Man\",\"video\":false,\"vote_average\":7.3,\"vote_count\":561},{\"adult\":false,\"backdrop_path\":\"/q8OEC91NiJOpghWI9hXtC27nFX0.jpg\",\"id\":10020,\"original_title\":\"Beauty and the Beast\",\"release_date\":\"1991-11-12\",\"poster_path\":\"/vGyhh8XB1AnDhBc4ssxrrz6ihdX.jpg\",\"popularity\":2.00275605165264,\"title\":\"Beauty and the Beast\",\"video\":false,\"vote_average\":6.9,\"vote_count\":926}]";
				intent.putExtra("searchResults", sr); // Passing list data to the Search Result Activity				
				startActivity(intent);*/
			}
		});
		
		/*
		 * Choose genre button
		 */
		Button btnGenre = (Button) findViewById(R.id.btnGenre);
		btnGenre.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				DialogFragment dFragment = new SelectGenreFragment();
				dFragment.show(getFragmentManager(), "theSelectGenre");
			}
		});
	}
	
	/*
	 * This method will be called from SelectGenreFragment
	 */
	public void setGenres(ArrayList<Integer> arrGenres) {
		String strGenres = "";
		
		for (int i = 0; i < arrGenres.size(); i++) {
			strGenres = strGenres + GenresList.getGenreNameByIndex(arrGenres.get(i).intValue());
			if ( i < arrGenres.size() - 1 ) {
				strGenres = strGenres + ", ";
			}
		}
		if ( strGenres == "" ) strGenres = "Select genre";
		else selectedGenres = arrGenres; // Saving genres for the future use
		
		Button btnSearch = (Button) findViewById(R.id.btnGenre);
		btnSearch.setText(strGenres);
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Creating menu
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if ( id == R.id.mBtnExit ) {
			// Exiting application
			DialogFragment dFragment = new AppDialogFragment();
			dFragment.show(getFragmentManager(), "theDialog");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/*
	 * Creating popup to show error
	 */
	public void showAlert( String strMsg ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage( strMsg )
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //ok clicked
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	};
	
	
	/*
	 * FetchData is fetching data from the server
	 * Class will use 'url' variable - be sure that it is set before you instantiating this class
	 */
	private class FetchData extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			DefaultHttpClient httpclient = new DefaultHttpClient( new BasicHttpParams() );
			HttpGet httpget = new HttpGet(strUrl);
			Log.v(TAG, strUrl);
			
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
			
			// Log.v(TAG, resultJSONObject.getString("total_pages"));
			
			Intent intent = new Intent("android.intent.action.SRESULTS");
			JSONArray searchResults;
			
			//Saving list data for the Search Result Activity
			try {
				searchResults = resultJSONObject.getJSONArray("results");
				intent.putExtra("searchResults", searchResults.toString());
				intent.putExtra("searchType", currentSearchType.toString());
				intent.putExtra("totalPages", resultJSONObject.getString("total_pages"));
				switch(currentSearchType){
					case BY_YEAR:
						intent.putExtra("intYear", intYear);
						break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			startActivity(intent);
			progress.dismiss();
		}
		
	}

}

/*
[{"adult":false,"backdrop_path":"/wUdsw5jvnsGRsuA9e4MMSP3TD2B.jpg","id":534,"original_title":"Terminator Salvation","release_date":"2009-05-20","poster_path":"/hxDfhavtxA2Ayx7O9BsQMcZRdG0.jpg","popularity":4.19532242799627,"title":"Terminator Salvation","video":false,"vote_average":5.9,"vote_count":903},{"adult":false,"backdrop_path":"/5XPPB44RQGfkBrbJxmtdndKz05n.jpg","id":19995,"original_title":"Avatar","release_date":"2009-12-18","poster_path":"/8Ic8rRVoVrDJJlXzVzGxAesufUV.jpg","popularity":3.73434127940165,"title":"Avatar","video":false,"vote_average":6.9,"vote_count":6036},{"adult":false,"backdrop_path":"/xBKGJQsAIeweesB79KC89FpBrVr.jpg","id":278,"original_title":"The Shawshank Redemption","release_date":"1994-09-14","poster_path":"/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg","popularity":3.51625003390461,"title":"The Shawshank Redemption","video":false,"vote_average":8.1,"vote_count":3593},{"adult":false,"backdrop_path":"/jxdSxqAFrdioKgXwgTs5Qfbazjq.jpg","id":10138,"original_title":"Iron Man 2","release_date":"2010-05-07","poster_path":"/1LoT5WsN4Lc1aR7A18ciSA1LoMy.jpg","popularity":3.2796482936724,"title":"Iron Man 2","video":false,"vote_average":6.6,"vote_count":3330},{"adult":false,"backdrop_path":"/nMulOcoR6HAahofkcuo4mtA0o9j.jpg","id":10191,"original_title":"How to Train Your Dragon","release_date":"2010-03-05","poster_path":"/zMAm3WYmvD40FaWFsOmpicQFabz.jpg","popularity":3.1753207014147,"title":"How to Train Your Dragon","video":false,"vote_average":7.2,"vote_count":1906},{"adult":false,"backdrop_path":"/6xKCYgH16UuwEGAyroLU6p8HLIn.jpg","id":238,"original_title":"The Godfather","release_date":"1972-03-15","poster_path":"/d4KNaTrltq6bpkFS01pYtyXa09m.jpg","popularity":2.84774411598614,"title":"The Godfather","video":false,"vote_average":8.1,"vote_count":2262},{"adult":false,"backdrop_path":"/s2bT29y0ngXxxu2IA8AOzzXTRhd.jpg","id":27205,"original_title":"Inception","release_date":"2010-07-16","poster_path":"/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg","popularity":2.71852258538699,"title":"Inception","video":false,"vote_average":7.6,"vote_count":6307},{"adult":false,"backdrop_path":"/rpvDBeVazJyBV5SxtnQWIgL5SIb.jpg","id":10193,"original_title":"Toy Story 3","release_date":"2010-06-17","poster_path":"/tOwAAVeL1p3ls9dhOBo45ElodU3.jpg","popularity":2.41043273318264,"title":"Toy Story 3","video":true,"vote_average":7.3,"vote_count":1796},{"adult":false,"backdrop_path":"/39LohvXfll5dGCQIV9B9VJ16ImE.jpg","id":18785,"original_title":"The Hangover","release_date":"2009-06-05","poster_path":"/eshEkiG7NmU4ekA8CtpIdYiYufZ.jpg","popularity":2.34621561396029,"title":"The Hangover","video":false,"vote_average":6.9,"vote_count":2379},{"adult":false,"backdrop_path":"/17Pf2aGFfehYyz9Ru2S6fzcXvEO.jpg","id":12444,"original_title":"Harry Potter and the Deathly Hallows: Part 1","release_date":"2010-11-19","poster_path":"/maP4MTfPCeVD2FZbKTLUgriOW4R.jpg","popularity":2.31172280657409,"title":"Harry Potter and the Deathly Hallows: Part 1","video":false,"vote_average":7.0,"vote_count":1907},{"adult":false,"backdrop_path":"/qf59pVUHbY9z0Ke9Jg6HQghNJhM.jpg","id":23483,"original_title":"Kick-Ass","release_date":"2010-04-16","poster_path":"/yYy7bJ0HuSudtHDksGODZohRQWo.jpg","popularity":2.28674931634547,"title":"Kick-Ass","video":false,"vote_average":6.8,"vote_count":1737},{"adult":false,"backdrop_path":"/AmCtBQc5KxJfJVdS2TkY4Pc9lPd.jpg","id":12155,"original_title":"Alice in Wonderland","release_date":"2010-03-04","poster_path":"/pvEE5EN5N1yjmHmldfL4aJWm56l.jpg","popularity":2.26301099100199,"title":"Alice in Wonderland","video":false,"vote_average":6.0,"vote_count":1498},{"adult":false,"backdrop_path":"/4y5TDUZlqUmWWtjTAznWb6CFpzt.jpg","id":857,"original_title":"Saving Private Ryan","release_date":"1998-07-24","poster_path":"/35CMz4t7PuUiQqt5h4u5nbrXZlF.jpg","popularity":2.20014781253031,"title":"Saving Private Ryan","video":false,"vote_average":7.4,"vote_count":1955},{"adult":false,"backdrop_path":"/3CbUdwyKnEcLSLkQYWJfi8H6gPO.jpg","id":41233,"original_title":"Step Up 3D","release_date":"2010-08-06","poster_path":"/q8Pm7UpAqDdxo1Xnt29EHHAl2u2.jpg","popularity":2.19265298107209,"title":"Step Up 3D","video":false,"vote_average":6.6,"vote_count":118},{"adult":false,"backdrop_path":"/pOWkHXTtg6OTVn5Z94KxvGB8a3M.jpg","id":37799,"original_title":"The Social Network","release_date":"2010-09-30","poster_path":"/hKTwHT6WtYhtG8U2PJQSQH2y4oD.jpg","popularity":2.1514421780243,"title":"The Social Network","video":false,"vote_average":6.9,"vote_count":1015},{"adult":false,"backdrop_path":"/ma5Ps40txofscTtWpjs39swMnwZ.jpg","id":18823,"original_title":"Clash of the Titans","release_date":"2010-04-02","poster_path":"/n8W2Y72VzSi8Yz6IvYWwfoiMTS6.jpg","popularity":2.07785102538956,"title":"Clash of the Titans","video":false,"vote_average":5.7,"vote_count":928},{"adult":false,"backdrop_path":"/x5u73uBylbyCCnkzUGzt3uozqRp.jpg","id":27578,"original_title":"The Expendables","release_date":"2010-08-03","poster_path":"/y2qJoYxOhzyidsA60Mqn29H38Lk.jpg","popularity":2.07174132720209,"title":"The Expendables","video":false,"vote_average":5.9,"vote_count":1371},{"adult":false,"backdrop_path":"/uFg02Gt69UM6Ouam4slMdD0s029.jpg","id":38757,"original_title":"Tangled","release_date":"2010-11-24","poster_path":"/re6AOJbhBk9FIK3knwU6rYlbPDx.jpg","popularity":2.05549227536821,"title":"Tangled","video":false,"vote_average":7.0,"vote_count":1101},{"adult":false,"backdrop_path":"/lcRGF2RpurdvBiSQVocfzRrxV9u.jpg","id":14756,"original_title":"葉問","release_date":"2008-12-12","poster_path":"/8knFfuqW289DJi2cpPl4RVTDkbo.jpg","popularity":2.04586763967344,"title":"Ip Man","video":false,"vote_average":7.3,"vote_count":561},{"adult":false,"backdrop_path":"/q8OEC91NiJOpghWI9hXtC27nFX0.jpg","id":10020,"original_title":"Beauty and the Beast","release_date":"1991-11-12","poster_path":"/vGyhh8XB1AnDhBc4ssxrrz6ihdX.jpg","popularity":2.00275605165264,"title":"Beauty and the Beast","video":false,"vote_average":6.9,"vote_count":926}] 
 */
