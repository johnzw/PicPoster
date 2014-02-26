package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import ca.ualberta.cs.picposter.model.PicPostModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ElasticSearchOperations {

	protected static final int ArrayList = 0;

	public static void pushPicPostModel(final PicPostModel model) {
		Thread thread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				super.run();
				Gson gson = new Gson();
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(
						"http://cmput301.softwareprocess.es:8080/testing/wzhong3");

				try {
					String jsonString = gson.toJson(model);
					request.setEntity(new StringEntity(jsonString));

					HttpResponse response = client.execute(request);
					Log.w("ElasticSearch", response.getStatusLine().toString());

					response.getStatusLine().toString();
					HttpEntity entity = response.getEntity();

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent()));

					String output = reader.readLine();

					while (output != null) {
						Log.w("ElasticSearch", output);
						output = reader.readLine();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		thread.start();
	}
	
	
	//copy from ESDemo by Abram Hindle and Chenlei Zhang, 2014-02-25
	public static void searchPostModel(final String search) {
		Thread thread = new Thread() {
			public void run() {

				try {

					HttpGet searchRequest = new HttpGet(
							"http://cmput301.softwareprocess.es:8080/testing/wzhong3/_search?q="
									+ java.net.URLEncoder.encode(search,
											"UTF-8"));
					searchRequest.setHeader("Accept", "application/json");

					HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(searchRequest);
					String status = response.getStatusLine().toString();
					Log.w("ElasticSearch", status);

					String json = getEntityContent(response);
					Log.w("Debug", json);
					
					Gson gson = new Gson();
					
					Type type = new TypeToken<ElasticSearchResponse<PicPostModel>>(){}.getType();
					 list = gson.fromJson(json, type);
					
					Log.w("Debug", list.get(0).getText());
					/*
					 * Type elasticSearchSearchResponseType = new
					 * TypeToken<ElasticSearchSearchResponse<PicPostModel>>() {
					 * }.getType(); ElasticSearchSearchResponse<Recipe>
					 * esResponse = gson.fromJson( json,
					 * elasticSearchSearchResponseType);
					 * System.err.println(esResponse); for
					 * (ElasticSearchResponse<Recipe> r : esResponse.getHits())
					 * { Recipe recipe = r.getSource();
					 * System.err.println(recipe); }
					 * searchRequest.releaseConnection();
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		thread.start();
	}

	/**
	 * get the http response and return json string
	 */
	//copy from ESDemo by Abram Hindle and Chenlei Zhang, 2014-02-25
	static String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));
		String output;
		System.err.println("Output from Server -> ");
		String json = "";
		while ((output = br.readLine()) != null) {
			System.err.println(output);
			json += output;
		}
		System.err.println("JSON:" + json);
		return json;
	}
	
	//copy from ESDemo by Abram Hindle and Chenlei Zhang, 2014-02-25
	class ElasticSearchResponse<T> {
	    String _index;
	    String _type;
	    String _id;
	    int _version;
	    boolean exists;
	    T _source;
	    double max_score;
	    public T getSource() {
	        return _source;
	    }
	}

}
