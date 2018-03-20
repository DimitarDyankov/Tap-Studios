/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controller to handle the tutorial list page                 |
 **************************************************************/
package com.mycompany.tapstudio;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.support.v7.app.*;
import java.util.*;
import com.google.api.services.youtube.model.*;
import android.util.*;
import android.view.ViewGroup.*;
import com.mycompany.tutorials.*;
import android.content.*;
import android.widget.AdapterView.*;
import android.database.sqlite.*;
import com.mycompany.tutorialDb.*;

import static com.mycompany.tutorialDb.Constants.TABLE_NAME2; 
import static com.mycompany.tutorialDb.Constants.TutorialId; 
import static com.mycompany.tutorialDb.Constants.TITLE; 
import static com.mycompany.tutorialDb.Constants.UserId;
import static com.mycompany.tutorialDb.Constants.URL;
import static com.mycompany.tutorialDb.Constants.FavouriteId;
import android.database.*;

public class TutorialList extends ActionBarActivity
{
	private String searchTitle = "";
	private Context context;
	
	private createData tutorials;
	private static String[] FROM = {FavouriteId,TITLE, URL};
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_list);
		//get intent extras passed from the main activity
		//set the search title that will be used in the API calls
		Bundle extras = getIntent().getExtras();
		searchTitle = extras.getString("Tutorial_Title", "None") + "Tutorials";
		// set the title of the activity
		TextView Title = (TextView) findViewById(R.id.tutorial_listTitleTextView);
		Title.setText(Title.getText() + extras.getString("Tutorial_Title", "None"));
		context = this;
		if(extras.getString("View", "None").equals("favourite")){
			setFavouritesListView();
		}else{
			setTutorialList();
		}
	}
	
	private void setTutorialList(){
		youTubeKeywordSearch youtube = new youTubeKeywordSearch();
		setTutorialListViews(youtube.getYouTubeVideos(searchTitle));
	}
	
	private void setTutorialListViews(Iterator<SearchResult> iteratorSearchResults){
		try{
			if (!iteratorSearchResults.hasNext()) {
				Log.d("YouTube Debugger 4", "There aren't any results for your query.");
			}
			while (iteratorSearchResults.hasNext()) {
				SearchResult singleVideo = iteratorSearchResults.next();
				ResourceId rId = singleVideo.getId();
				// Confirm that the result represents a video. Otherwise, the
				// item will not contain a video ID.
				if (rId.getKind().equals("youtube#video")) {
					//Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

					TextView t = new TextView(this);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 200);
					layoutParams.setMargins(20,0,20,30);
					t.setLayoutParams(layoutParams);
					t.setText(singleVideo.getSnippet().getTitle() + " \n \n URL: http://www.youtube.com/watch?v=" + rId.getVideoId());
					t.setBackgroundResource(R.drawable.bg_card);
					t.setOnClickListener(viewTutorialListener);
					t.setOnLongClickListener(favouriteTutorialListener);
					LinearLayout l = (LinearLayout)findViewById(R.id.tutorial_listNestedLinearLayout);
					l.addView(t);
				}
			}
		}catch(Exception e){Log.d("Tutorial Error", e.toString());}
	}
	
	private void setFavouritesListView(){
		LinearLayout l = (LinearLayout)findViewById(R.id.tutorial_listNestedLinearLayout);
		l.removeAllViews();
		tutorials = new createData(context);
		SQLiteDatabase db = tutorials.getReadableDatabase();
		Cursor cursor=db.query(TABLE_NAME2,FROM,"", null,null,null,null);
		while(cursor.moveToNext()){ 
			TextView t = new TextView(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 200);
			layoutParams.setMargins(20,0,20,30);
			t.setLayoutParams(layoutParams);
			t.setText("No: " + cursor.getInt(0) + " Title: " + cursor.getString(1) + "\n \n URL: " + cursor.getString(2));
			t.setBackgroundResource(R.drawable.bg_card);
			t.setOnClickListener(viewTutorialListener);
			t.setOnLongClickListener(unFavouriteTutorialListener);
			l.addView(t);
		}
	}

	//listener on tutorial item click
	//opens the single tutorial activty which opens the link to the youtube video
	private OnClickListener viewTutorialListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, SingleTutorial.class);
			TextView view = (TextView) v;
			String url = view.getText().toString();
			url = url.substring(url.indexOf("URL: ") + 5);
			Log.d("URL Debugger", url);
			intent.putExtra("Tutorial_Id", url);
			context.startActivity(intent);
		}
	};
	
	private OnLongClickListener favouriteTutorialListener = new OnLongClickListener(){
		@Override
		public boolean onLongClick(View v) {
			TextView tv = (TextView)v;
			String viewStr = tv.getText().toString();
			createData tutorials = new createData(context);
			SQLiteDatabase db = tutorials.getWritableDatabase(); 
			ContentValues values = new ContentValues();
			values.put(TITLE,viewStr.substring(viewStr.indexOf("Title: ") + 7, viewStr.indexOf("URL: ")));
			values.put(URL, viewStr.substring(viewStr.indexOf("URL: ") + 5));
			values.put(TutorialId, 1);
			values.put(UserId,1);
			db.insertOrThrow(TABLE_NAME2,null,values);
			Toast.makeText(context, "Tutorial Added to Favourites " + viewStr.substring(0, viewStr.indexOf("URL: ")), Toast.LENGTH_SHORT).show();
			return true;
		}
	};

	private OnLongClickListener unFavouriteTutorialListener = new OnLongClickListener(){
		@Override
		public boolean onLongClick(View v) {
			TextView tv = (TextView)v;
			String viewStr = tv.getText().toString();
			createData tutorials = new createData(context);
			SQLiteDatabase db = tutorials.getWritableDatabase();
			db.execSQL("delete from "+TABLE_NAME2+" where FavouriteId='"+viewStr.substring(viewStr.indexOf("No: ") + 4, viewStr.indexOf("Title: ")-1)+"'");
			Toast.makeText(context, "Tutorial Deleted: " + viewStr.substring(0, viewStr.indexOf("URL: ")), Toast.LENGTH_SHORT).show();
			setFavouritesListView();
			return true;
		}
	};
}
