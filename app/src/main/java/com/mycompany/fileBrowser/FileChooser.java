/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class creating the file/folder structure                    |
 **************************************************************/
package com.mycompany.fileBrowser;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.text.DateFormat; 
import android.os.Bundle; 
import android.app.ListActivity;
import android.view.View;
import android.widget.ListView;
import com.mycompany.tapstudio.*;
import android.widget.*;
import android.view.View.*;
import android.content.*;
import android.util.*; 

public class FileChooser extends ListActivity {

	private File currentDir;
    private FileArrayAdapter adapter;
	private Context context;
	
	private OnClickListener selectPathListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			Intent intent = new Intent();
			intent.putExtra("GetPathToProject",currentDir.toString());
			setResult(RESULT_OK, intent);
			//SharedPreferences prefs = getSharedPreferences("com.mycompany.fileBrowser", Context.MODE_PRIVATE);
			//prefs.edit().putString("GetPath", currentDir.toString()).commit();
			finish();
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/sdcard/");
        fill(currentDir); 
		this.context = this;
		Button b = new Button(this);
		b.setText("Select");
		b.setWidth(150);
		b.setHeight(50);
		b.setOnClickListener(selectPathListener);
		this.getListView().addFooterView(b);
    }
    private void fill(File f){
    	File[]dirs = f.listFiles(); 
		this.setTitle("Current Dir: "+f.getName());
		List<Item>dir = new ArrayList<Item>();
		List<Item>fls = new ArrayList<Item>();
		try{
			for(File ff: dirs){ 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){	
					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					
					//String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon")); 
				}
				else{
					fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				}
			 }
		 }catch(Exception e){ }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		 this.setListAdapter(adapter);
    }
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
	}
}
