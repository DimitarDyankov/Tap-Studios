/**************************************************************
| Author: Dimitar Dyankov                                     |
| This class get a string and returns a spanable that has been|
| syntatically highlighted based on the file type             |
 *************************************************************/
package com.mycompany.syntaxHighlighting;

import java.util.*;
import syntaxhighlight.*;
import prettify.*;
import android.text.*;
import android.text.style.*;
import android.graphics.*;
import android.util.*;
import android.support.v4.app.*;

public class PrettifyHighlighter extends Fragment{
    private static final Map<String, String> COLORS = buildColorsMap();

    private final Parser parser = new PrettifyParser();

	/**************************************************************
	 | This method take a string and builds a spannable veriable  |
	 |that has words and sign colored based on syntatic rules     |
	 |taken from Google's prettify liblary. The method takes a    |
	 |word at a time in a look and checks its type based on a hash|
	 |map which then passes back the color for that type.         |
	 **************************************************************/
    public Spannable highlight(String fileExtension, String sourceCode) {
        List<ParseResult> results = parser.parse(fileExtension, sourceCode);
		Spannable span = new SpannableString(sourceCode);
		for(ParseResult result : results){
            String type = result.getStyleKeys().get(0);
			span.setSpan(new ForegroundColorSpan(Color.parseColor(getColor(type))), result.getOffset(), result.getOffset() + result.getLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    private String getColor(String type){
        return COLORS.containsKey(type) ? COLORS.get(type) : COLORS.get("pln");
    }

	/****************************************************************
	 |A hash map that hold the key value pears for the syntax colors|
	 ****************************************************************/
    private static Map<String, String> buildColorsMap() {
        Map<String, String> map = new HashMap<>();
        map.put("typ", "#2196F3"); // type (veriable)
        map.put("kwd", "#0D47A1"); // Key words
        map.put("lit", "#cc3700"); 
        map.put("com", "#00E676"); // comments
        map.put("str", "#E64A19"); // Strings
        map.put("pun", "#2196F3"); // punctuation
        map.put("pln", "#000000"); // Plain Text
        return map;
    }
}
