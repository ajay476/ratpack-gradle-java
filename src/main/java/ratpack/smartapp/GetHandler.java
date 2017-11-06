package ratpack.smartapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import ratpack.handling.Context;
import ratpack.handling.Handler;


public class GetHandler  implements Handler{

	@Override
	public void handle(Context ctx) throws Exception {
		HttpClient httpclient = null;
		String responseString = "Did not work!";
		try{
		 httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(Constants.APIEndpointURL+Constants.getSwitchesPath);
		httpGet.setHeader("Authorization", Constants.APIToken);
		HttpResponse response = httpclient.execute(httpGet);
		InputStream inputStream = response.getEntity().getContent();
		if (inputStream != null)
			responseString = convertInputStreamToString(inputStream);
		
		}catch(Exception e){
			e.getLocalizedMessage();
		}finally{
		}
		ctx.render(responseString);
	}

	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

}