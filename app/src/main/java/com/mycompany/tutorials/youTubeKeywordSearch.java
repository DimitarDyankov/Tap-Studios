/**************************************************************
 | Author: Dimitar Dyankov  								   | 
 | Class that does an API call to the you tube web services    |
 **************************************************************/
package com.mycompany.tutorials;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;	
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import android.util.*;
import android.os.*;

public class youTubeKeywordSearch {

	private static final String apiKey = "AIzaSyCH3EdRUM-rFVoqRdBmtEcuDfBIGqJSxFI";
	private static final long NUMBER_OF_VIDEOS_RETURNED = 50;
	private String query;
	private Iterator<SearchResult> youtubeResults;
	/**
	 * Define a global instance of a Youtube object, which will be used
	 * to make YouTube Data API requests.
	 */
	private static YouTube youtube;

	public Iterator<SearchResult> getYouTubeVideos(String queryTerm){
		query = queryTerm;
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					try {
						youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
								public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {}
							}).setApplicationName("TapStudios").build();
						// Define the API request for retrieving search results.
						YouTube.Search.List search = youtube.search().list("id,snippet");

						search.setKey(apiKey);
						search.setQ(query);
						// Restrict the search results to only include videos. See:
						search.setType("video");
						// To increase efficiency, only retrieve the fields that the
						// application uses.
						search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
						search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
						// Call the API and print results.
						SearchListResponse searchResponse = search.execute();
						Log.d("Request response", searchResponse.toString());
						List<SearchResult> searchResultList = searchResponse.getItems();
						if (searchResultList != null) {
							youtubeResults = searchResultList.iterator();
						}
					} catch (GoogleJsonResponseException e) {
						Log.d("YouTube Debugger", "There was a service error: " + e.getDetails().getCode() + " : "
							  + e.getDetails().getMessage() + ": " + e.getDetails().getErrors());
					} catch (IOException e) {
						Log.d("YouTube Debugger 2", "There was an IO error: " + e.getCause() + " : " + e.getMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			Log.d("Wait Thread", e.toString());
		}
		return youtubeResults;
	}
}
