/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | DropBoxSync.java handles the syncing of a project           |
 | to dropbox, it create a connection with dropbox and         |
 | then takes the path of the project to start the uplpad      |
 **************************************************************/
package com.mycompany.fileBrowser;

import com.dropbox.client2.*;
import com.dropbox.client2.android.*;
import com.dropbox.client2.session.*;
import android.content.*;
import java.io.*;
import com.dropbox.client2.exception.*;
import android.util.*;
import android.content.res.*;
import com.google.common.io.*;

public class dropBoxSync
{
	/*********************************
	|set the dropbox specific keys   |
	**********************************/
	final static private String APP_KEY = "ew1zhbbsd7kq0vt";
	final static private String APP_SECRET = "66dqg1g8wxo3psk";

	String dropBoxProjectPath = "/";
	
	private DropboxAPI<AndroidAuthSession> mDBApi;
	public DropboxAPI<AndroidAuthSession> getmDBApi(){
		return mDBApi;
	}
	
	/******************************************
	|The constructor for the class, here a    |
	|Connection to dropbox is made using the  |
	|keys and a session token is added to the |
	|dropbox object                           |
	******************************************/
	public dropBoxSync(Context c){
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		mDBApi.getSession().startOAuth2Authentication(c);
	}
	/******************************************
	|Upload a project method takes in the path|
	|of a project. The project folder name is |
	|then taken, a check to see if this folder|
	|exists in drop box is made. If yes it is |
	|deleted in order to update the project   |
	|with a new version. The copyAssetFolder  |
	|method is finally called to begin the    |
	|upload                                   |
	******************************************/
	public void uploadProject(String projectPath) throws DropboxException{
		String[] folders = projectPath.split("/",-1);
		dropBoxProjectPath = dropBoxProjectPath + folders[folders.length - 1];
		try{
			mDBApi.delete(dropBoxProjectPath);
		}catch(Exception e){Log.d("Delete Error","File not found");}
		mDBApi.createFolder(dropBoxProjectPath);
		copyAssetFolder(projectPath);
	}
	/******************************************************
	|The copy asset folder method, copies whats inside a  |
	|path to dropbox. Firstly an array of all item in  a  |
	|path is made. The looping through this path to check |
	|if the item is a folder or a file. If it is a folder |
	|the folder name is taken and created inside dropbox, |
	|copyAssetFolder is then called again with the new    |
	|path to upload. If it is a file, copyAsset method is |
	|called to upload the content of the file.            |
	******************************************************/
	private void copyAssetFolder(String path) {
		try {
			File f = new File(path);
			String[] files = f.list();
			for (String file : files){
				if (file.contains(".")){
					copyAsset(path + "/" + file, dropBoxProjectPath + path.substring(path.indexOf(dropBoxProjectPath) + dropBoxProjectPath.length()));
				}
				else{
					String folder = dropBoxProjectPath + path.substring(path.indexOf(dropBoxProjectPath) + dropBoxProjectPath.length()) + "/" + file;
					mDBApi.createFolder(folder);
					copyAssetFolder(path + "/" + file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/********************************************************
	|copyAsset takes in the location of a file, and the     |
	|location of where it needs to be uploaded. The content |
	|of the file is taken and a file with that name and     |
	|content is made on dropbox.                            |
	********************************************************/
	private void copyAsset(String fromPath, String toPath) throws DropboxException {
		File file = new File(fromPath);
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			mDBApi.putFile(toPath + "/" + file.getName(), inputStream, file.length(), null, null);
		}
		catch (FileNotFoundException e)
		{
			Log.d("File Not Found DropBox Sync",e.toString());
		}
	}
}
