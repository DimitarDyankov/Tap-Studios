/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class for checking the type programming language            |
 **************************************************************/
package com.mycompany.projectFolderStructure;
import android.content.res.*;
import android.util.*;

public class CreateProject
{
	public boolean createProjectFileStructure(String projectLanguage, String projectType, String projectName, String projectPath, AssetManager assetManager){
		try{
			switch(projectLanguage){
				case "HTML/CSS/JavaScript":
					htmlFolderStructure structureHtml = new htmlFolderStructure();
					structureHtml.createFolderStructure(projectType, projectName, projectPath, assetManager);
					break;
				case "PHP":
					PhpFolderStructure structurePhp = new PhpFolderStructure();
					structurePhp.createFolderStructure(projectType, projectName, projectPath, assetManager);
					break;
				case "Java":
					javaFolderStructure structureJava = new javaFolderStructure();
					structureJava.createFolderStructure(projectType, projectName, projectPath, assetManager);
					break;
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
