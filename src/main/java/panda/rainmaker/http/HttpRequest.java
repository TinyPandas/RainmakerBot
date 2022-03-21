package panda.rainmaker.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    public static HttpResult getResult(String strUrl, int subBegin, int subEnd) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                return new HttpResult("Response Code: " + responseCode, false);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseString = response.toString();
                return new HttpResult(responseString.substring(subBegin, responseString.length() - subEnd), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HttpResult(e.getMessage(), false);
        }
    }
}
