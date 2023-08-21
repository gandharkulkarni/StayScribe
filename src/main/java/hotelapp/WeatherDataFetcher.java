package hotelapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;

/**
 * Class to get weather data from API
 * by: Gandhar Kulkarni
 */
public class WeatherDataFetcher {
    /**
     * Calls weather api to fetch weather details according to latitude and longitude
     *
     * @param latitude double
     * @param longitude double
     * @return JsonObject
     */
    public JsonObject consumeWeatherApi(double latitude, double longitude){
        String urlStr = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
        PrintWriter out = null;
        BufferedReader inputBuffer = null;
        SSLSocket socket = null;
        JsonObject weatherDetails=null;
        try {
            URL weatherAPIUrl = new URL(urlStr);
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket(weatherAPIUrl.getHost(), 443);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(weatherAPIUrl.getHost(), weatherAPIUrl.getPath() + "?" + weatherAPIUrl.getQuery());

            out.println(request);
            out.flush();

            inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = inputBuffer.readLine()) != null) {
                if (line.startsWith("{")) {
                    sb.append(line);
                }
            }
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(sb.toString());

            weatherDetails = jo.getAsJsonObject("current_weather");

        } catch (IOException ioException) {
            LogHelper.getLogger().error(ioException);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
        return weatherDetails;
    }

    /**
     * Returns a get request string using host and query path
     *
     * @param host String
     * @param pathResourceQuery String
     * @return String
     */
    private String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
        return request;
    }
}
