package com.mycompany.tutorialDb;
import android.provider.BaseColumns;
public interface Constants extends BaseColumns{ 
	//table names
	public static final String TABLE_NAME = "Tutorials" ;
	public static final String TABLE_NAME2 = "FavouriteTutorials" ;
	public static final String TABLE_NAME3 = "User" ;
	
	//Columns in the tutorial database 

	public static final String UserId="UserId";
	public static final String TITLE="TutorialTitle";
	public static final String URL="TutorialURL";  
	public static final String TutorialId = "TutorialId";
	public static final String FavouriteId = "FavouriteId";
}
