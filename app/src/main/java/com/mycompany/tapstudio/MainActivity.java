/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controler that handles the main activity                    |
 **************************************************************/
package com.mycompany.tapstudio;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.content.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.*;
import android.widget.AdapterView.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.support.v4.app.*;
import android.*;
import com.mycompany.fileBrowser.*;
import android.util.*;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
	private GridView gridView;
	private GridViewCustomAdapter gridViewCustomeAdapter;

	private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
	
	public void onClick(View v){
		switch(v.getId()){
			case R.id.createProjectBtn:
				createProjectIntent();
				break;
		}
	}
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		FloatingActionButton createProject = (FloatingActionButton) findViewById(R.id.createProjectBtn);
		createProject.setOnClickListener(this);

		createGridView();
		
		showInstructions();
	}
	
	@Override
	public void onResume(){ 
		super.onResume();
		createGridView();
	}
	
	private void createGridView(){
		gridView = (GridView) findViewById(R.id.gridView1);
		gridViewCustomeAdapter = new GridViewCustomAdapter(this);
		// Set the Adapter to GridView
		gridView.setAdapter(gridViewCustomeAdapter);
		
	}

	private void showInstructions(){
		SharedPreferences prefs = this.getSharedPreferences("com.mycompany.tapstudio", Context.MODE_PRIVATE);
		
		if(prefs.getInt("ISTRUCTIONS",-1) < 0){
			prefs.edit().putInt("ISTRUCTIONS",1).apply();
			
			Intent intent4 = new Intent(this, InstructionsActivity.class);
			startActivityForResult(intent4, 1);
		}
	}
	
	private void createProjectIntent(){
		Intent intent = new Intent(this, ProjectCreation.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.action_open :
				int hasWriteContactsPermission = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
				if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
					if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
						showMessageOKCancel("You need to allow access to storage, please navigate to app permissions in settings to grant access.",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ActivityCompat.requestPermissions(MainActivity.this,
																	  new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
																	  REQUEST_CODE_ASK_PERMISSIONS);
								}
							});
						break;
					}
					ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
					break;
				}
				Intent intent1 = new Intent(this, FileChooser.class);
				startActivityForResult(intent1, 1);
				return true;
			case R.id.action_create :
				createProjectIntent();
				return true;
			case R.id.action_settings :
				Intent intent2 = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent2, 1);
				return true;
			case R.id.action_about :
				Intent intent3 = new Intent(this, AboutActivity.class);
				startActivityForResult(intent3, 1);
				return true;
			case R.id.action_instruction :
				Intent intent4 = new Intent(this, InstructionsActivity.class);
				startActivityForResult(intent4, 1);
		}
		return super.onOptionsItemSelected(item);
	}
	
	//method to check that write/read permission to storage is granted
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_PERMISSIONS:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					Intent intent1 = new Intent(this, FileChooser.class);
					startActivityForResult(intent1, 0);
				} else {
					// Permission Denied
					Toast.makeText(MainActivity.this, "Write/View storage denied", Toast.LENGTH_SHORT)
                        .show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	
	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try{
			Intent intent = new Intent(this, EditorActivity.class);
			intent.putExtra("projectPath", data.getStringExtra("GetPathToProject"));
			startActivity(intent);
		}catch(Exception e){
			Log.d("Main Activity", e.toString());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
