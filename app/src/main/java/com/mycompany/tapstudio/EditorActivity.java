/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class that handles the editor view, It allows the user to   |
 | edit code of verious languages. User can open files, search |
 | and replace, save and syncronise to dropbox                 |
 **************************************************************/
package com.mycompany.tapstudio;

import android.support.v4.app.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v4.widget.DrawerLayout;
import android.widget.*;
import android.text.*;
import android.view.ViewTreeObserver.*;
import com.mycompany.fileBrowser.*;
import java.io.*;
import java.util.*;
import java.text.*;
import android.view.*;
import android.view.View.*;
import com.mycompany.syntaxHighlighting.*;
import android.util.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.content.*;
import android.app.Fragment;
import com.dropbox.client2.exception.*;

public class EditorActivity extends ActionBarActivity
{
	//class veriables
	private SharedPreferences prefs;
	private ListView mDrawerListLeft;
	private ListView mDrawerListRight;
	private ArrayAdapter<String> mAdapter;
	private File currentDir;
	private FileArrayAdapter adapter;
	private FileChooserMenu files;
	private Context ctx;
	
	private TabLayout mTabs;
    private ViewPager tabsviewPager;
    private ViewPagerAdapter mTabsAdapter;
	private FloatingActionButton codeShortcutBtn1;
	private FloatingActionButton codeShortcutBtn2;
	private FloatingActionButton codeShortcutBtn3;
	private FloatingActionButton codeShortcutBtn4;
	
	private dropBoxSync dropBox;
	private ArrayList<PagerItem> mPagerItems;
	/**************************************************************
	| Listener for the left drawer menu which looks for what type | 
	| of item is clicked. If the item is a directory then the new |
	| directory is populated in the left drawer menu, otherwise   |
	| a file is clicked and it's content is loaded onto the editor|
	| view                                                        |
	**************************************************************/
	private AdapterView.OnItemClickListener leftMenuOnClick = new AdapterView.OnItemClickListener() {
		//clicking an item on the file browser
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			Item o = adapter.getItem(position);
			if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
				currentDir = new File(o.getPath());
				adapter = new FileArrayAdapter(EditorActivity.this,R.layout.file_view, files.getMenuItems(currentDir));
				mDrawerListLeft.setAdapter(adapter);
			}
			else{
				onFileClick(o);
			}
		}
		//opening a file from the menu
		private void onFileClick(Item o){
			EditorFragment ef = new EditorFragment(o.getPath(), getApplicationContext());
			mPagerItems.add(new PagerItem(o.getName(), ef));
			mTabsAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mPagerItems);
			//setup viewpager to give swipe effect
			tabsviewPager.setAdapter(mTabsAdapter);
			mTabs.setupWithViewPager(tabsviewPager);
		}
	};
	
	private AdapterView.OnItemClickListener rightMenuOnClick = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			switch(position){
				case 0 :
					searchFile();
					break;
				case 1 :
					writeToFile();
					break;
				case 2:
					createFile();
					break;
				case 3:
					Toast.makeText(EditorActivity.this, "Project is getting uploaded, please be patient :)", Toast.LENGTH_LONG).show();
					Thread thread = new Thread(){
						@Override
						public void run() { try { uploadWork();} catch (DropboxException e){}}
					};
					thread.start();
					break;
				case 4:
					closeFile();
					break;
				case 5:
					undoChange();
					break;
				case 6:
					redoChange();
					break;
			}
		}
	};
	
	private Boolean shortCutIsPressed = false;
	private OnClickListener codeShortcutBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!shortCutIsPressed){
				codeShortcutBtn1.setVisibility(View.VISIBLE);
				codeShortcutBtn2.setVisibility(View.VISIBLE);
				codeShortcutBtn3.setVisibility(View.VISIBLE);
				codeShortcutBtn4.setVisibility(View.VISIBLE);
				shortCutIsPressed = true;
			}else{
				codeShortcutBtn1.setVisibility(View.GONE);
				codeShortcutBtn2.setVisibility(View.GONE);
				codeShortcutBtn3.setVisibility(View.GONE);
				codeShortcutBtn4.setVisibility(View.GONE);
				shortCutIsPressed = false;
			}
		}
	};
	
	private OnClickListener shortCodeBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
				EditText editor = (EditText)mTabsAdapter.getItem(tabsviewPager.getCurrentItem()).getView().findViewById(R.id.editor);
				int start = Math.max(editor.getSelectionStart(), 0);
				int end = Math.max(editor.getSelectionEnd(), 0);
				String textToInsert = "";
				switch(v.getId()){
					case R.id.shortCode1 :
						textToInsert = prefs.getString("CODE_SHORTCODE4", "");
						break;
					case R.id.shortCode2 :
						textToInsert = prefs.getString("CODE_SHORTCODE3", "");
						break;
					case R.id.shortCode3 :
						textToInsert = prefs.getString("CODE_SHORTCODE2", "");
						break;
					case R.id.shortCode4 :
						textToInsert = prefs.getString("CODE_SHORTCODE1", "");
						break;
				}
				editor.getText().replace(Math.min(start, end), Math.max(start, end),textToInsert, 0, textToInsert.length());
			}catch(Exception e){
				Toast.makeText(EditorActivity.this, "Please open a file first", Toast.LENGTH_LONG).show();
			}
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
		ctx = this;
		prefs = this.getSharedPreferences("com.mycompany.tapstudio", Context.MODE_PRIVATE);
		dropBox = new dropBoxSync(EditorActivity.this);
		getElements();
		setListeners();
		mPagerItems = new ArrayList();
	}
	//get elemts from the view at the start of an activity
	private void getElements(){
		mDrawerListLeft = (ListView)findViewById(R.id.navListLeft);
		mDrawerListRight = (ListView)findViewById(R.id.navListRight);
		codeShortcutBtn1 = (FloatingActionButton)findViewById(R.id.shortCode1);
		codeShortcutBtn2 = (FloatingActionButton)findViewById(R.id.shortCode2);
		codeShortcutBtn3 = (FloatingActionButton)findViewById(R.id.shortCode3);
		codeShortcutBtn4 = (FloatingActionButton)findViewById(R.id.shortCode4);
		mTabs = (TabLayout) findViewById(R.id.tabLayout);
		tabsviewPager = (ViewPager) findViewById(R.id.view_pager);
	}
	//method that sets listeners for items on the screen
	private void setListeners(){
		FloatingActionButton codeShortcut = (FloatingActionButton)findViewById(R.id.codeShortcut);
		codeShortcut.setOnClickListener(codeShortcutBtnListener);
		codeShortcutBtn1.setOnClickListener(shortCodeBtnListener);
		codeShortcutBtn2.setOnClickListener(shortCodeBtnListener);
		codeShortcutBtn3.setOnClickListener(shortCodeBtnListener);
		codeShortcutBtn4.setOnClickListener(shortCodeBtnListener);
        mTabsAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		addDrawerItemsLeft();
		addDrawerItemsRight();
	}
	//method to add items to the left drawer menu
	private void addDrawerItemsLeft() {
		files = new FileChooserMenu();
		currentDir = new File(getIntent().getStringExtra("projectPath"));
		adapter = new FileArrayAdapter(getApplicationContext(), R.layout.file_view, files.getMenuItems(currentDir));
		mDrawerListLeft.setAdapter(adapter);
		mDrawerListLeft.setOnItemClickListener(leftMenuOnClick);
	}
	
	private void setLeftDrawerItems(){
		adapter = new FileArrayAdapter(getApplicationContext(), R.layout.file_view, files.getMenuItems(currentDir));
		mDrawerListLeft.setAdapter(adapter);
	}
	//method to add items to the right drawer menu
	private void addDrawerItemsRight() {
		String[] osArray = {"Search", "Save File", "Create File/Folder", "Upload Project to DropBox", "Close File", "Undo", "Redo"};
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
		mDrawerListRight.setAdapter(mAdapter);
		mDrawerListRight.setOnItemClickListener(rightMenuOnClick);
	}
	private void searchFile(){
		Intent intent = new Intent(this, searchFile.class);
		startActivityForResult(intent,1);
	}
	//write to the file in other words save current file
	private void writeToFile() {
        try {
			EditText editor = (EditText)mTabsAdapter.getItem(tabsviewPager.getCurrentItem()).getView().findViewById(R.id.editor);
			EditorFragment editorFragment = (EditorFragment)mTabsAdapter.getItem(tabsviewPager.getCurrentItem());
        	FileWriter out = new FileWriter(new File(editorFragment.getFilePath()));
            out.write(editor.getText().toString());
            out.close();
			Toast.makeText(EditorActivity.this, "File Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
			Toast.makeText(EditorActivity.this, "File Failed to Save", Toast.LENGTH_LONG).show();
			Log.d("Writing to storage error", e.toString());	
		} 
	}
	
	private void createFile(){
		Intent intent = new Intent(this, CreateFile.class);
		intent.putExtra("projectPath",currentDir.getPath());
		this.startActivity(intent);
	}
	
	private void uploadWork() throws DropboxException{
		if (dropBox.getmDBApi().getSession().authenticationSuccessful()) {
			try {
				// Required to complete auth, sets the access token on the session
				dropBox.getmDBApi().getSession().finishAuthentication();
				String accessToken = dropBox.getmDBApi().getSession().getOAuth2AccessToken();
				dropBox.uploadProject(getIntent().getStringExtra("projectPath"));
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
	}
	
	private void closeFile(){
		try{
			mPagerItems.remove(tabsviewPager.getCurrentItem());
			mTabsAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mPagerItems);
			tabsviewPager.setAdapter(mTabsAdapter);
			mTabs.setupWithViewPager(tabsviewPager);
		}catch(Exception e){ Log.d("", e.toString());}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch(event.getKeyCode()){
			case KeyEvent.KEYCODE_TAB :
				tabPressed();
				break;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void tabPressed(){
		EditText editor = (EditText)mTabsAdapter.getItem(tabsviewPager.getCurrentItem()).getView().findViewById(R.id.editor);
		String textToInsert = "    ";
		int start = Math.max(editor.getSelectionStart(), 0);
		int end = Math.max(editor.getSelectionEnd(), 0);
		editor.getText().replace(Math.min(start, end), Math.max(start, end),textToInsert, 0, textToInsert.length());
		editor.setSelection(editor.getSelectionEnd());
	}
	
	private void undoChange(){
		try{
			EditorFragment ef = (EditorFragment) mTabsAdapter.getItem(tabsviewPager.getCurrentItem());
			ef.editorUndo();
		}catch(Exception e){
			Toast.makeText(EditorActivity.this, "Need to have a file opened", Toast.LENGTH_LONG).show();
		}
	}
	
	private void redoChange(){
		try{
			EditorFragment ef = (EditorFragment) mTabsAdapter.getItem(tabsviewPager.getCurrentItem());
			ef.editorRedo();
		}catch(Exception e){
			Toast.makeText(EditorActivity.this, "Need to have a file opened", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onResume(){ 
		super.onResume();
		setLeftDrawerItems();
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		try{
			EditorFragment ef = (EditorFragment) mTabsAdapter.getItem(tabsviewPager.getCurrentItem());
			ef.highlightSearchReplaceStr(data.getStringExtra("SearchQuery"),data.getStringExtra("ReplaceQuery"));
			Log.d("replace", data.getStringExtra("ReplaceQuery"));
			super.onActivityResult(requestCode, resultCode, data);
		}catch(Exception e){Log.d("Search", e.toString());}
	}
}
