package me.artemdemo.moviedb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
	
	static ProgressDialog progress;
	
	private static final String TAG = "MovieDB-main";
	
	@Override
	/*
	 * Starting up application
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		progress = new ProgressDialog(this);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				progress.setTitle("Loading");
				progress.setMessage("Wait while loading...");
				progress.show();
				
				strUrl = ApiFactory.getMoviesByYearUrl(2011);
				
				new FetchData().execute();
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onSaveInstanceState(Bundle outState,
			PersistableBundle outPersistentState) {
		super.onSaveInstanceState(outState, outPersistentState); // This command is from API 21, I suppress it, but check what should be used in order to run it on API 16
		
	}
	
	
	
	/**
	 * FetchData is fetching data from the server
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
				Log.v(TAG, resultJSONObject.getString("total_pages"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Intent intent = new Intent("android.intent.action.SRESULTS");
			startActivity(intent);
		}
		
	}

}
