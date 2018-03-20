/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controler to handle the project creation                    |
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
import android.util.*;
import android.content.pm.*;
import android.*;
import android.support.v4.content.*;
import android.support.v4.app.*;
import com.mycompany.fileBrowser.*;
import com.mycompany.projectFolderStructure.*;
import com.mycompany.projectFolderStructure.CreateProject;

public class ProjectCreation extends AppCompatActivity implements OnItemSelectedListener, OnClickListener
{

	private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
	private Context context;
	private CreateProject createProject = new CreateProject();
	SharedPreferences prefs;
	
	//button on click method
	public void onClick(View v){
		switch(v.getId()){
			case R.id.browseFiles_btn:
				int hasWriteContactsPermission = ContextCompat.checkSelfPermission(ProjectCreation.this,Manifest.permission.READ_EXTERNAL_STORAGE);
				if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
					if (!ActivityCompat.shouldShowRequestPermissionRationale(ProjectCreation.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
						showMessageOKCancel("You need to allow access to storage, please navigate to app permissions in settings to grant access.",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ActivityCompat.requestPermissions(ProjectCreation.this,
																	  new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
																	  REQUEST_CODE_ASK_PERMISSIONS);
								}
							});
						break;
					}
					ActivityCompat.requestPermissions(ProjectCreation.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
					break;
				}
				Intent intent1 = new Intent(this, FileChooser.class);
				startActivityForResult(intent1, 1);
				break;
		}
	}
	
	//listeners --------------------------------------------
	//listener on create project button
	//if all fields are filled in then create project structure and open up project
	//open Editor activity if everything is sucsesfull
	private OnClickListener createProjectBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Spinner languageSpinner = (Spinner) findViewById(R.id.language_spinner);
			Spinner projectSpinner = (Spinner) findViewById(R.id.projectType_spinner);
			EditText projectTitle = (EditText) findViewById(R.id.projectName_etext);
			TextView projectPath = (TextView) findViewById(R.id.projectPath_text);
			
			if((languageSpinner.getSelectedItemPosition() > 0) && (projectSpinner.getSelectedItemPosition() > 0) 
			   && (projectTitle.getText().toString().trim().length() > 0) 
			   && (projectPath.getText().toString().trim().length() > 0) && !projectPath.getText().toString().equals("Project Path")){
				if(createProject.createProjectFileStructure(languageSpinner.getSelectedItem().toString(),projectSpinner.getSelectedItem().toString(), projectTitle.getText().toString().trim(), projectPath.getText().toString(), getAssets()) == true){
					Intent intent = new Intent(context, EditorActivity.class);
					intent.putExtra("projectPath", projectPath.getText().toString() + "/" + projectTitle.getText().toString());
					finish();
					startActivity(intent);
				}
			}else{
				Toast.makeText(ProjectCreation.this, "Not all items have been correctly filled in!", Toast.LENGTH_SHORT)
					.show();
			}
		}
	};
	//drop down item selected listener
	//populae the project type drop down based on programming language selection
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		Spinner projectSpinner = (Spinner) findViewById(R.id.projectType_spinner);
		switch(pos){
			case 0:
				projectSpinner.setAdapter(null);
				break;
			case 1:
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.webProject_array, android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				projectSpinner.setAdapter(adapter);
				break;
			case 2:
				ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.javaProject_array, android.R.layout.simple_spinner_item);
				adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				projectSpinner.setAdapter(adapter2);
				break;
			case 3:
				ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.phpProject_array, android.R.layout.simple_spinner_item);
				adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				projectSpinner.setAdapter(adapter3);
				break;
		}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_creation_view);
		prefs = getSharedPreferences("com.mycompany.fileBrowser", Context.MODE_PRIVATE);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		context = this;
		
		setListeners();
	}
	//method to check that write/read permission to storage is granted
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_PERMISSIONS:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					Intent intent1 = new Intent(this, FileChooser.class);
					startActivityForResult(intent1, 1);
				} else {
					// Permission Denied
					Toast.makeText(ProjectCreation.this, "Write/View storage denied", Toast.LENGTH_SHORT)
                        .show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	//method to set listeners for views in the activity
	private void setListeners(){
		Spinner spinner = (Spinner) findViewById(R.id.language_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.language_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		FloatingActionButton starProject = (FloatingActionButton) findViewById(R.id.startProject_Btn);
		starProject.setOnClickListener(createProjectBtnListener);
		
		Button browseFiles = (Button) findViewById(R.id.browseFiles_btn);
		browseFiles.setOnClickListener(this);
	}
	
	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new android.support.v7.app.AlertDialog.Builder(ProjectCreation.this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show();
	}
	//when activity is resumed it would take the path selected from the file brawser activity
	//prevous selected path will be placed in there if once selected before
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try{
			TextView path = (TextView) this.findViewById(R.id.projectPath_text);
			path.setText(data.getStringExtra("GetPathToProject"));
			super.onActivityResult(requestCode, resultCode, data);
		}catch(Exception e){
			Log.d("Project Creation", e.toString());
		}
	}
}
