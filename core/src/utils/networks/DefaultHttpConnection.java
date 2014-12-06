package utils.networks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultHttpConnection {

	public static String get(String serviceUrl, String params) {
		String response = "";
		HttpURLConnection connection = null;
		URL serverAddress = null;
//		InputStream input = null;
//		DataOutputStream output = null;
		try {
			serviceUrl = serviceUrl.replaceAll(" ", "%20");
			if (params != null && params.length() > 0) {
				serviceUrl += "?" + params;
			}
			serverAddress = new URL(serviceUrl);
			// set up out communications stuff
			connection = null;

			// Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(60000);
			connection.setUseCaches(false);
			connection.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
			String inputLine;
            StringBuffer res = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
            in.close();
            response = res.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	public static String post() {
		return null;
	}

}
