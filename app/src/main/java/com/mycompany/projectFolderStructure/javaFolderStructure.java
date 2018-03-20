/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class creating Java projects                                |
 **************************************************************/
package com.mycompany.projectFolderStructure;
import android.content.res.*;
import java.io.*;
import android.util.*;

public class javaFolderStructure implements folderStructure
{
	private AssetManager am;

	@Override
	public void createFolderStructure(String projectType, String projectName, String projectPath, AssetManager assetManager)
	{
		am = assetManager;
		switch(projectType){
			case "Android":
				createAndroidFolderStructure(projectName, projectPath);
				break;
		}
	}

	private void createAndroidFolderStructure(String projectName, String projectPath)
	{
		File projectDirectory = new File(projectPath, projectName);
		if(!projectDirectory.exists()) {                                 
			projectDirectory.mkdirs();
		}
		copyAssetFolder(am, "myapplication", projectPath + "/" + projectName);
	}
	
	private static boolean copyAssetFolder(AssetManager assetManager,String fromAssetPath, String toPath) {
		try {
			String[] files = assetManager.list(fromAssetPath);
			new File(toPath).mkdirs();
			boolean res = true;
			for (String file : files)
				if (file.contains("."))
					res &= copyAsset(assetManager, fromAssetPath + "/" + file,toPath + "/" + file);
				else 
					res &= copyAssetFolder(assetManager,fromAssetPath + "/" + file, toPath + "/" + file);
			return res;
		} catch (Exception e) {
			Log.d("Copy Asset Error", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	private static boolean copyAsset(AssetManager assetManager,String fromAssetPath, String toPath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(fromAssetPath);
			new File(toPath).createNewFile();
			out = new FileOutputStream(toPath);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch(Exception e) {
			Log.d("Copy Asset2 Error", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}
}
