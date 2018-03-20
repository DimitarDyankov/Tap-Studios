/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Fragment to handle a editor opened in a tab                 |
 **************************************************************/
package com.mycompany.tapstudio;

import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import android.widget.*;
import com.mycompany.syntaxHighlighting.*;
import android.text.*;
import android.view.ViewTreeObserver.*;
import com.google.api.client.http.*;
import java.io.*;
import android.util.*;
import android.content.*;
import android.text.style.*;
import android.graphics.*;

public class EditorFragment extends Fragment {
	private TextView lineNumbers;
	private EditText editor;
	private ScrollView editorScroll;
	private PrettifyHighlighter highlighter = new PrettifyHighlighter();
	private File fileToOpen;
	private Spannable highlighted;
	String extention;
	private Context ctx;
	private TextViewUndoRedo undoRedo;
	
	public String getFilePath(){ return fileToOpen.getPath(); }
	
	public EditorFragment(){}
	
	public EditorFragment(String filePath, Context c){
		ctx = c;
		fileToOpen = new File(filePath);
		String text = "";
		try {
			FileInputStream inputStream = new FileInputStream(fileToOpen);
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString).append('\n');
				}

				inputStream.close();
				text = stringBuilder.toString();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		extention = fileToOpen.getName().substring(fileToOpen.getName().lastIndexOf(".") + 1);
		highlighted = highlighter.highlight(extention, text);
	}
	
	//method to watch the text on the editor view
	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			checkLineNumbers();
			checkSyntaxHighlight();
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			//checkLineNumbers(); //add another line number when a new line is added
		}
	};
	//method to scroll the line numbers when the editor edtiText moves
	OnScrollChangedListener editorScrollListener = new OnScrollChangedListener() { 
		@Override 
		public void onScrollChanged() {
			lineNumbers.scrollTo(0,editorScroll.getScrollY());
		}
	};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		highlighter = new PrettifyHighlighter();
        View view = inflater.inflate(R.layout.editor_content, container, false);
		setListeners(view);
		undoRedo = new TextViewUndoRedo((EditText)view.findViewById(R.id.editor));
        return view;
    }
	
	private void setListeners(View view){
		lineNumbers = (TextView)view.findViewById(R.id.lineNumber);
		editor = (EditText)view.findViewById(R.id.editor);
		editorScroll = (ScrollView)view.findViewById(R.id.editorScroll);
		editorScroll.getViewTreeObserver().addOnScrollChangedListener(editorScrollListener);
		editor.setHorizontallyScrolling(true);
		editorScroll.setHorizontalScrollBarEnabled(true);
		editor.setText(highlighted,TextView.BufferType.SPANNABLE);
		editor.addTextChangedListener(textWatcher);
		//editor.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
	}

	private void checkSyntaxHighlight(){
		int selectionEnd = editor.getSelectionEnd();
		String text = editor.getText().toString();
		if (selectionEnd >= 0) {
			// gives you the substring from start to the current cursor
			// position
			text = text.substring(0, selectionEnd);
		}
		String delimiter = " ";
		int lastDelimiterPosition = text.lastIndexOf(delimiter);
		String lastWord = lastDelimiterPosition == -1 ? text : 
			text.substring(lastDelimiterPosition + delimiter.length());

		highlighted = highlighter.highlight(extention, lastWord);
		editor.removeTextChangedListener(textWatcher);
		int start = Math.max(editor.getSelectionStart()-lastWord.length(), 0);
		editor.getText().replace(start, start+lastWord.length(),highlighted, 0, highlighted.length());
		editor.addTextChangedListener(textWatcher);
		editor.setSelection(selectionEnd);
		
	}
	
	public void highlightSearchReplaceStr(String search, String replace){
		if(!replace.equals("") || !replace.equals(null)){
			int ofe = editor.getText().toString().indexOf(search,0);   
			Spannable WordtoSpan = new SpannableString(editor.getText());
        	for(int ofs=0;ofs<editor.getText().toString().length() && ofe!=-1;ofs=ofe+1){       
				ofe = editor.getText().toString().indexOf(search,ofs);   
				if(ofe == -1) break;
				else{                       
					if(replace.equals("") || replace.equals(null)){
						WordtoSpan.setSpan(new BackgroundColorSpan(Color.GREEN), ofe, ofe+search.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						editor.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
					}else{
						Spannable RaplaceWord = new SpannableString(editor.getText().toString().replace(search,replace));
						editor.setText(highlighter.highlight(extention,RaplaceWord.toString()));
					}
				}
        	} 
		}
	}
	private void checkLineNumbers(){
		String tempLine = "";
		for(int i=1; i <= editor.getLineCount(); i++){
			if(i==1)
				tempLine = tempLine + "" + i;
			else
				tempLine = tempLine + "\n" + i;
		}
		lineNumbers.setText(tempLine);
	}
	
	public void editorUndo(){
		undoRedo.undo();
	}
	
	public void editorRedo(){
		undoRedo.redo();
	}
}

