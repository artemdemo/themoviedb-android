package me.artemdemo.moviedb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final ProgressDialog progress = new ProgressDialog(this);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*progress.setTitle("Loading");
				progress.setMessage("Wait while loading...");
				progress.show();
				// To dismiss the dialog
				// progress.dismiss();*/
			}
		});
	}

}
