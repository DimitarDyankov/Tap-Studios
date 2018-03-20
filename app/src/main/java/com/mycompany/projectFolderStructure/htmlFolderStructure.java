/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class creating web projects                                 |
 **************************************************************/
package com.mycompany.projectFolderStructure;
import java.io.*;
import android.content.res.*;
import android.util.*;

public class htmlFolderStructure implements folderStructure
{
	private AssetManager am;
	
	@Override
	public void createFolderStructure(String projectType, String projectName, String projectPath, AssetManager assetManager)
	{
		am = assetManager;
		createBaseFolderStructure(projectName, projectPath);
		switch(projectType){
			case "Blank": 
				//createBlankFolderStructure(projectName, projectPath);
				break;
			case "Bootstraps":
				createBootstrapsFolderStructure(projectName, projectPath);
				break;
			case "Foundation":
				createFoundationFolderStructure(projectName, projectPath);
				break;
		}
	}
	
	private void createBaseFolderStructure(String projectName, String projectPath){
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
		File indexFile = new File(projectPath + "/" + projectName, "index.html");
		try
		{
			indexFile.createNewFile();
		}
		catch (IOException e)
		{}
	}
	
	private void createBlankFolderStructure(String projectName, String projectPath){
		
	}

	private void createBootstrapsFolderStructure(String projectName, String projectPath){
		File fontsDirectory = new File(projectPath + "/" + projectName, "fonts");
		if(!fontsDirectory.exists()) {                                 
			fontsDirectory.mkdirs();
		}
		copyAssets(projectPath + "/" + projectName + "/css", "bootstraps/css");
		copyAssets(projectPath + "/" + projectName + "/js", "bootstraps/js");
		copyAssets(projectPath + "/" + projectName + "/fonts", "bootstraps/fonts");
	}

	private void createFoundationFolderStructure(String projectName, String projectPath){
		copyAssets(projectPath + "/" + projectName + "/css", "foundation/css");
		copyAssets(projectPath + "/" + projectName + "/js", "foundation/js");
	}
	
	private void copyAssets(String filePath, String dirToCopy) {
		AssetManager assetManager = am;
		String[] files = null;
		try {
			files = assetManager.list(dirToCopy);
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		if (files != null) for (String filename : files) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(dirToCopy + "/" + filename);
					File outFile = new File(filePath, filename);
					out = new FileOutputStream(outFile);
					copyFile(in, out);
				} catch(IOException e) {
					Log.e("tag", "Failed to copy asset file: " + filename, e);
				}     
				finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							// NOOP
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// NOOP
						}
					}
				}  
			}
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}
}
