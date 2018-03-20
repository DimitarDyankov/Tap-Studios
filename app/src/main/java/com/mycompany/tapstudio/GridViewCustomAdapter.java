/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Contoler that handles the grid view for tutorials           |
 **************************************************************/
package com.mycompany.tapstudio;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.view.View.*;
import com.mycompany.tutorialDb.*;
import android.database.sqlite.*;

import static com.mycompany.tutorialDb.Constants.TABLE_NAME;
import static com.mycompany.tutorialDb.Constants.TutorialId; 
import static com.mycompany.tutorialDb.Constants.TITLE;
import android.database.*;
import android.util.*;
import com.mycompany.tutorials.*;

public class GridViewCustomAdapter extends ArrayAdapter
{
	Context context;
	private int gridSize;

	private createData tutorials;
	private static String[] FROM = {TutorialId,TITLE};
	
	public GridViewCustomAdapter(Context context) 
	{
		super(context, 0);
		this.context=context;
		//set the grid size 
		tutorials = new createData(context);
		SQLiteDatabase db = tutorials.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
		cursor.moveToFirst();
		try{
			gridSize = cursor.getCount() + 2;
		}catch(Exception e) { gridSize = 2;}
		cursor.close();
	}

	public int getCount() 
	{
		return gridSize;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		if (row == null) 
		{
			//get layout
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.grid_row, parent, false);
			//pick up layout views
			TextView tutorialCardTitle = (TextView) row.findViewById(R.id.textView_gridRow);
			ImageView addTutorialCard = (ImageView) row.findViewById(R.id.addTutorialView_gridRow);
			ImageView favouriteTutorialCard = (ImageView) row.findViewById(R.id.favouriteView_gridRow);
			//set layout text
			//if last one has been reached create a add button
			if((position + 1) == gridSize){
				tutorialCardTitle.setVisibility(View.GONE);
				favouriteTutorialCard.setVisibility(View.GONE);
				addTutorialCard.setImageResource(R.drawable.add_tutorial);
				addTutorialCard.setOnClickListener(addTutorialListener);
			}else if((position + 2) == gridSize){
				tutorialCardTitle.setVisibility(View.GONE);
				addTutorialCard.setVisibility(View.GONE);
				favouriteTutorialCard.setImageResource(R.drawable.favourite_tutorial);
				favouriteTutorialCard.setOnClickListener(favouriteTutorialsListener);
			}else{
				addTutorialCard.setVisibility(View.GONE);
				favouriteTutorialCard.setVisibility(View.GONE);
				Cursor cursor = getTutorials("");
				cursor.moveToPosition(position);
				String Title = cursor.getString(1);
				tutorialCardTitle.setText(Title);
				tutorialCardTitle.setOnClickListener(viewTutorialsListener);
			}
		} 
		return row;
	}
	//pick up all tutorial cards
	@SuppressWarnings("deprecation")
	private Cursor getTutorials(String where){  
		SQLiteDatabase db = tutorials.getReadableDatabase();
		Cursor cursor=db.query(TABLE_NAME,FROM,where, null,null,null,null);
		return cursor;
	}
	//listener for addTutorial button
	//opens add tutorial activity which lets you add a new tutorial
	private OnClickListener addTutorialListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			Intent intent = new Intent(context, AddTutorial.class);
			context.startActivity(intent);
		}
	};
	//listener on tutorial item click
	//opens the tutorial activity which then lists a set of tutorials
	private OnClickListener viewTutorialsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, TutorialList.class);
			TextView view = (TextView) v;
			intent.putExtra("Tutorial_Title", view.getText().toString());
			intent.putExtra("View", "normal");
			context.startActivity(intent);
		}
	};
	
	private OnClickListener favouriteTutorialsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, TutorialList.class);
			intent.putExtra("View", "favourite");
			intent.putExtra("Tutorial_Title", "Favourites");
			context.startActivity(intent);
		}
	};
}
