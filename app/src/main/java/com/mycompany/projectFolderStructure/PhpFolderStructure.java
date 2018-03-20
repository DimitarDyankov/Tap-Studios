/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class handles the creation of PHP project types             |
 **************************************************************/
package com.mycompany.projectFolderStructure;
import java.io.*;
import android.content.res.*;
import android.util.*;
import android.os.*;

public class PhpFolderStructure implements folderStructure
{
	
	private AssetManager am;
	
	/**************************************************************
	 | This method gets the type of project and based on the type a| 
	 | differnt method is called to start the folder/file creation |
	 **************************************************************/
	@Override
	public void createFolderStructure(String projectType, String projectName, String projectPath, AssetManager assetManager)
	{
		am = assetManager;
		switch(projectType){
			case "Blank": 
				createBlankFolderStructure(projectName, projectPath);
				break;
			case "Wordpress":
				createWordpressFolderStructure(projectName, projectPath);
				break;
			case "Code Igniter":
				createCodeIgniterFolderStructure(projectName, projectPath);
				break;
		}
	}

	/**************************************************************
	 | This method create a blank folder structure based on common |
	 |PHP folder structures for website design                     |
	 **************************************************************/
	private void createBlankFolderStructure(String projectName, String projectPath){
		File projectDirectory = new File(projectPath, projectName);
		if(!projectDirectory.exists()) {                                 
			projectDirectory.mkdirs();
		}
		File cssDirectory = new File(projectPath + "/" + projectName, "css");
		if(!cssDirectory.exists()) {                                 
			cssDirectory.mkdirs();
		}
		File jsDirectory = new File(projectPath + "/" + projectName, "js");
		if(!jsDirectory.exists()) {                                 
			jsDirectory.mkdirs();
		}
		File imgDirectory = new File(projectPath + "/" + projectName, "img");
		if(!imgDirectory.exists()) {                                 
			imgDirectory.mkdirs();
		}
		File indexFile = new File(projectPath + "/" + projectName, "index.php");
		try
		{
			indexFile.createNewFile();
		}
		catch (IOException e)
		{}
	}

	/**************************************************************
	 | This method create a wordpress project by copying the assets|
	 | from the asset folder that were stored for wordpress, taken |
	 |from wordpress.com                                           |
	 **************************************************************/
	private void createWordpressFolderStructure(String projectName, String projectPath){
		File projectDirectory = new File(projectPath, projectName);
		if(!projectDirectory.exists()) {                                 
			projectDirectory.mkdirs();
		}
		copyAssetFolder(am, "wordpress", projectPath + "/" + projectName);
	}

	private void createCodeIgniterFolderStructure(String projectName, String projectPath){
		File projectDirectory = new File(projectPath, projectName);
		if(!projectDirectory.exists()) {                                 
			projectDirectory.mkdirs();
		}
		copyAssetFolder(am, "codeigniter", projectPath + "/" + projectName);
	}

	/**************************************************************
	|This method copies all folder and files from the specified   |
	|project type into a location on the device
	**************************************************************/
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
			e.printStackTrace();
			return false;
		}
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}
}
