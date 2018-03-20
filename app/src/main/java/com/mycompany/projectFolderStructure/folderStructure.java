/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Interface to build project creation classes                 |
 **************************************************************/
package com.mycompany.projectFolderStructure;
import android.content.res.*;

public interface folderStructure
{
	//create the folder structure for the chosen parameters
	public void createFolderStructure(String projectType, 
										String projectName,
										String projectPath,
										AssetManager assetManager);
	
}
