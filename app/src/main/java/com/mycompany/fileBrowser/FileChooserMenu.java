/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class creating the items for the list for file view         |
 **************************************************************/
package com.mycompany.fileBrowser;

import com.mycompany.fileBrowser.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.app.*;

public class FileChooserMenu extends ListFragment
{
	public List<Item> getMenuItems(File f){
    	File[]dirs = f.listFiles(); 
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
		}catch(Exception e){    

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if(!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		return dir;
	}
}
