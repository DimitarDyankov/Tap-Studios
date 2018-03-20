/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controler for creating file                                 |
 **************************************************************/
package com.mycompany.tapstudio;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.content.*;
import java.io.*;

public class CreateFile extends Activity implements OnClickListener
{
	private String currentPath;
	
	public void onClick(View v){
		switch(v.getId()){
			case R.id.creteFileButton:
				EditText fileName = (EditText)findViewById(R.id.fileNameEditText);
				createFile(fileName.getText().toString());
				this.finish();
				break;
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_file_main);
		//set save tutorial button listener
		Button createButton = (Button) findViewById(R.id.creteFileButton);
		createButton.setOnClickListener(this);
		currentPath = getIntent().getStringExtra("projectPath");
		Toast.makeText(CreateFile.this, currentPath, Toast.LENGTH_SHORT).show();
	}
	
	private void createFile(String name){
		File newFile = new File(currentPath,name);
		try{
			if(name.contains(".")) newFile.createNewFile();
			else newFile.mkdir();
		}
		catch (IOException e){}
	}
}
