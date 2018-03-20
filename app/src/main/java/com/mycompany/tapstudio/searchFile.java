/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controller to search an open file                           |
 **************************************************************/
package com.mycompany.tapstudio;

import android.app.*;
import android.os.*;
import android.view.View.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import android.util.*;

public class searchFile extends Activity implements OnClickListener
{
	public void onClick(View v){
		switch(v.getId()){
			case R.id.searchButton:
				EditText searchEt = (EditText)findViewById(R.id.searchPhraseEditText);
				EditText replaceEt = (EditText)findViewById(R.id.replaceEditText);
				Intent intent = new Intent();
				intent.putExtra("SearchQuery",searchEt.getText().toString());
				intent.putExtra("ReplaceQuery",replaceEt.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_file_main);
		//set save tutorial button listener
		Button searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
	}
}
