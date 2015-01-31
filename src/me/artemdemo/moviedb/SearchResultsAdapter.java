package me.artemdemo.moviedb;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchResultsAdapter extends ArrayAdapter<String> {
	
	private final Context context;
	private final List<String> movieIds;
	private final List<String> movieNames;
	private final List<String> movieYears;
	
	/**
	 * 
	 * @param context
	 * @param movieIds - List of movie ids. ListView will be based on them
	 * @param movieNames - List of movie names
	 * @param movieYears - List of movie years (Year of release)
	 */
	public SearchResultsAdapter(
			Context context,
			List<String> movieIds,
			List<String> movieNames,
			List<String> movieYears ) {
	    super(context, R.layout.rowlayout, movieIds);
	    this.context = context;
	    this.movieIds = movieIds;
	    this.movieNames = movieNames;
	    this.movieYears = movieYears;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    String[] arrMovieNames = movieNames.toArray(new String[movieNames.size()]);
	    String[] arrMovieYears = movieYears.toArray(new String[movieYears.size()]);
	    
	    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
	    TextView lblMovie = (TextView) rowView.findViewById(R.id.lblMovie);
	    TextView lblYear = (TextView) rowView.findViewById(R.id.lblYear);
	    
	    lblMovie.setText(arrMovieNames[position]);
	    lblYear.setText(arrMovieYears[position]);

	    return rowView;
	}
}
