/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controler to handle the instruction page                    |
 **************************************************************/
package com.mycompany.tapstudio;
import android.app.*;
import android.os.*;

public class InstructionsActivity extends Activity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_main);
	}
}
