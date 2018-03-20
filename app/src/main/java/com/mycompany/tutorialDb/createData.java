package com.mycompany.tutorialDb;

import static com.mycompany.tutorialDb.Constants.TABLE_NAME; 
import static com.mycompany.tutorialDb.Constants.TutorialId; 
import static com.mycompany.tutorialDb.Constants.TITLE;
import static com.mycompany.tutorialDb.Constants.TABLE_NAME2; 
import static com.mycompany.tutorialDb.Constants.FavouriteId; 
import static com.mycompany.tutorialDb.Constants.URL;
import static com.mycompany.tutorialDb.Constants.TABLE_NAME3;
import static com.mycompany.tutorialDb.Constants.UserId;

import java.util.ArrayList;
import java.util.List;

import android.content.Context; 
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class createData extends SQLiteOpenHelper{
	//databse details
	private static final String DATABASE_NAME = "TapStudios.db";
	private static final int DATABASE_VERSION = 4;
	//create database
	public createData(Context ctx){ 
		super(ctx,DATABASE_NAME,null,DATABASE_VERSION); 
	}
	//create tables method
	@Override 
	public void onCreate(SQLiteDatabase db){ 
		db.execSQL("CREATE TABLE " + TABLE_NAME3 +
				   "(" + UserId +" INTEGER PRIMARY KEY AUTOINCREMENT);");
		// -------------------------------------
		db.execSQL("CREATE TABLE " + TABLE_NAME +
				"(" + TutorialId +" INTEGER PRIMARY KEY AUTOINCREMENT," +
				TITLE + " VARCHAR(255) NOT NULL," + 
				UserId + " INTEGER NOT NULL," +
				"FOREIGN KEY (" + UserId +") REFERENCES " + TABLE_NAME3 + " (" + UserId + "));");
		// -------------------------------------
		db.execSQL("CREATE TABLE " + TABLE_NAME2 +
				"(" + FavouriteId +" INTEGER PRIMARY KEY AUTOINCREMENT," +
				TITLE + " VARCHAR(510) NOT NULL,"+
				URL + " VARCHAR(510) NOT NULL," + 
				UserId + " INTEGER NOT NULL," + 
				TutorialId + " INTEGER NOT NULL," + 
				"FOREIGN KEY (" + TutorialId + ") REFERENCES " + TABLE_NAME + " (" + TutorialId + ")," +
				"FOREIGN KEY (" + UserId +") REFERENCES " + TABLE_NAME3 + " (" + UserId + "));");
		
		//--------------------------------------
		db.execSQL("INSERT INTO " + TABLE_NAME3 + " (" + UserId + ") VALUES (1);");
	}
	//update database tables when new version has been implemented
	@Override 
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ 
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); 
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2); 
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
		onCreate(db);
	}
}
