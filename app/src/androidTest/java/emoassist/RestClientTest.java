package emoassist;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Usage:
 *
 * Download this file and unzip it on a lib folder (If it is on another folder, make sure to change it on the commands)
 * http://www.java2s.com/Code/Jar/j/Downloadjavajsonjar.htm (java-json.jar)
 * Download this file and put it on the folder
 * Compile this file with this command
 * javac -cp lib/java-json.jar JavaRestClientTest.java
 * Run the class with the command
 * java -cp lib/java-json.jar:./ JavaRestClientTest
 */
public class RestClientTest {

/*
    public void postData ()
    {
        try {
            // Construct manually a JSON object in Java, for testing purposes an object with an object
            JSONObject data = new JSONObject();
            JSONObject auth = new JSONObject();
            auth.put("login", "1234567890");
            data.put("auth", auth);

            // URL and parameters for the connection, This particulary returns the information passed
            URL url = new URL("https://dnetix.co/ping");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            // Not required
            // urlConnection.setRequestProperty("Content-Length", String.valueOf(input.getBytes().length));

            // Writes the JSON parsed as string to the connection
            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(data.toString().getBytes());
            Integer responseCode = httpConnection.getResponseCode();

            BufferedReader bufferedReader;

            // Creates a reader buffer
            if (responseCode > 199 && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }

            // To receive the response
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();

            // Prints the response
            System.out.println(content.toString());

        } catch (Exception e) {
            System.out.println("Error Message");
            System.out.println(e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }*/
}