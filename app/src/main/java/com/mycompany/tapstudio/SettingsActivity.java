/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Controller to handlle the setting page                      |
 **************************************************************/
package com.mycompany.tapstudio;
import android.os.*;
import android.support.v7.app.*;
import android.content.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;

public class SettingsActivity extends AppCompatActivity
{
	private SharedPreferences prefs;
	private EditText shortCodeEditText1;
	private EditText shortCodeEditText2;
	private EditText shortCodeEditText3;
	private EditText shortCodeEditText4;
	private Button shortCodeSaveBtn;
	
	private OnClickListener saveBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			prefs.edit().putString("CODE_SHORTCODE1", shortCodeEditText1.getText().toString()).apply();
			prefs.edit().putString("CODE_SHORTCODE2", shortCodeEditText2.getText().toString()).apply();
			prefs.edit().putString("CODE_SHORTCODE3", shortCodeEditText3.getText().toString()).apply();
			prefs.edit().putString("CODE_SHORTCODE4", shortCodeEditText4.getText().toString()).apply();
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
		prefs = this.getSharedPreferences("com.mycompany.tapstudio", Context.MODE_PRIVATE);
		setListeners();
		shortCodeEditText1.setText(prefs.getString("CODE_SHORTCODE1", ""));
		shortCodeEditText2.setText(prefs.getString("CODE_SHORTCODE2", ""));
		shortCodeEditText3.setText(prefs.getString("CODE_SHORTCODE3", ""));
		shortCodeEditText4.setText(prefs.getString("CODE_SHORTCODE4", ""));
	}
	
	private void setListeners(){
		shortCodeEditText1 = (EditText)findViewById(R.id.ShortCodeEditText1);
		shortCodeEditText2 = (EditText)findViewById(R.id.ShortCodeEditText2);
		shortCodeEditText3 = (EditText)findViewById(R.id.ShortCodeEditText3);
		shortCodeEditText4 = (EditText)findViewById(R.id.ShortCodeEditText4);
		shortCodeSaveBtn = (Button)findViewById(R.id.ShortCodeButton);
		shortCodeSaveBtn.setOnClickListener(saveBtnListener);
	}
}
