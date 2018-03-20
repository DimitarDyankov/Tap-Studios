/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controler to add tutorials                                  |
 **************************************************************/
package com.mycompany.tapstudio;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.content.*;
import android.database.sqlite.*;

import static com.mycompany.tutorialDb.Constants.TABLE_NAME; 
import static com.mycompany.tutorialDb.Constants.TutorialId; 
import static com.mycompany.tutorialDb.Constants.TITLE; 
import static com.mycompany.tutorialDb.Constants.UserId;
import com.mycompany.tutorialDb.*;

public class AddTutorial extends Activity implements OnClickListener
{
	private createData tutorials;
	private Context context;
	
	public void onClick(View v){
		switch(v.getId()){
			case R.id.add_tutorial_saveButton:
				EditText tutorialTitle = (EditText)findViewById(R.id.add_tutorial_titleEditText);
				addTutorial(tutorialTitle.getText().toString());
				this.finish();
				break;
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tutorial_main);
		//set save tutorial button listener
		Button saveButton = (Button) findViewById(R.id.add_tutorial_saveButton);
		saveButton.setOnClickListener(this);
	}
	//add tutorial to database
	private void addTutorial(String title){
		tutorials = new createData(this);
		SQLiteDatabase db = tutorials.getWritableDatabase(); 
		ContentValues values = new ContentValues();
		values.put(TITLE,title);
		values.put(UserId,1);
		db.insertOrThrow(TABLE_NAME,null,values);
	}
}
