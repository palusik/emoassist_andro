package emoassist.services;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class NotificationService {

    public static final int DEFAULT_CONNECTION_TIMEOUT = 2500;
    public static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    Date lastCapturedH;
    Date lastCapturedA;
    Date lastCapturedS;
    Date lastCapturedF;
    Date lastCapturedN;

    // delays for invoking business logic beteeen same emotional state
    Integer BUSINESS_LOGIC_DELAYS = 60;

    public final String BaseURLAlert = "https://<change this to the domain of your processing application>/api/alertWithCheck";
    public final String BaseURLBusinessLogic = "https://<change this to URL of your Logic Apps for notificaiton>";

    /**
     * The instance of this utils object
     */
    private static NotificationService instance;

    /**
     * Returns the singleton instance
     * @return The singleton instance of the Utils
     */
    public static NotificationService getInstance()
    {
        if(instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void raiseAlert (Boolean remoteMonitor, String UserId, String emotionType, Float emotionProbabilityValue, Integer currentPulseValue, String actionType, ArrayList<String> alerts)
    {

        Integer posAlert;
        String  targetValue = "";

        switch (actionType) {
            case "Activity":
                posAlert = 0;
                targetValue = alerts.get(0).toString();
                break;
            case "Music":
                posAlert = 1;
                targetValue = alerts.get(1).toString();
                break;
            case "Call":
                posAlert = 2;
                targetValue = alerts.get(2).toString();
                break;
            case "Book":
                posAlert = 3;
                targetValue = alerts.get(3).toString();
                break;
            case "Picture":
                posAlert = 4;
                targetValue = alerts.get(4).toString();
                break;
            case "None":
                posAlert = 5;
                targetValue = "None";
                break;
            default:
                targetValue = "Vibrate";
        }

        if (currentPulseValue == null)
            currentPulseValue = 0;

        if (emotionProbabilityValue == null)
            emotionProbabilityValue = Float.valueOf(0);

        String[] myPostParams = { UserId, emotionType, String.valueOf(emotionProbabilityValue), String.valueOf(currentPulseValue), actionType, targetValue };

        Date date = new Date();


        try {
            new NotificationService.storeAlert().execute(myPostParams);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //  business logic notification is disabled
        if (remoteMonitor != true)
        {

            return;
        }

        Boolean invokeBLFlow = false;

        switch (emotionType) {
            case "H":
                if (lastCapturedH == null || (date.getTime() - lastCapturedH.getTime()) > BUSINESS_LOGIC_DELAYS * 1000) {
                    lastCapturedH = date;
                    invokeBLFlow = true;
                }
                break;
            case "N":
                if (lastCapturedN == null || (date.getTime() - lastCapturedN.getTime()) > BUSINESS_LOGIC_DELAYS * 1000) {
                    lastCapturedN = date;
                    invokeBLFlow = true;
                }
                break;
            case "A":
                if (lastCapturedA == null || (date.getTime() - lastCapturedA.getTime()) > BUSINESS_LOGIC_DELAYS * 1000) {
                    lastCapturedA = date;
                    invokeBLFlow = true;
                }
                break;
            case "S":
                if (lastCapturedS == null || (date.getTime() - lastCapturedS.getTime()) > BUSINESS_LOGIC_DELAYS * 1000) {
                    lastCapturedS = date;
                    invokeBLFlow = true;
                }
                break;
            case "F":
                if (lastCapturedF == null || (date.getTime() - lastCapturedF.getTime()) > BUSINESS_LOGIC_DELAYS * 1000) {
                    lastCapturedF = date;
                    invokeBLFlow = true;
                }
                break;
        }
        // filter our updates if they are in less than 30 seconds
        if (invokeBLFlow == true)
        {
            try {
                new NotificationService.invokeBusinessLogicFlow().execute(myPostParams);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            return;
        }

    }

    // this stores alert in database
    private static class storeAlert extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                String UserId, emotionType, actionType, targetValue;
                Float emotionProbabilityValue;
                Integer currentPulseValue;

                UserId = params[0];
                emotionType = params[1];
                emotionProbabilityValue = Float.valueOf(params[2]);
                currentPulseValue = Integer.valueOf(params[3]);
                actionType = params[4];
                targetValue = params[5];

                // Construct manually a JSON object in Java, for testing purposes an object with an object
                JSONObject data = new JSONObject();
                data.put("userid", UserId);
                data.put("type", emotionType);
                data.put("probability", emotionProbabilityValue);
                data.put("hr", currentPulseValue);
                data.put("alert_type", actionType);
                data.put("target", targetValue);
                data.put("status", "new");

                // URL and parameters for the connection, This particulary returns the information passed
                URL url = new URL("https://processing/api/alertWithCheck");
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setDoOutput(true);
                //httpConnection.setFollowRedirects(true);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
                httpConnection.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);
                //httpConnection.setRequestProperty("Accept", "application/json");
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
                System.out.println("system response follows:");
                System.out.println(content.toString());

            } catch (Exception e) {
                System.out.println("Error Message");
                System.out.println(e.getClass().getSimpleName());
                System.out.println(e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            //Do something with result string
            System.out.println(str);
        }

    }

    // this contacts cloud business logic flow to handle remote alerting
    private static class invokeBusinessLogicFlow extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                String UserId, emotionType, actionType, targetValue;
                Float emotionProbabilityValue;
                Integer currentPulseValue;

                UserId = params[0];
                emotionType = params[1];
                emotionProbabilityValue = Float.valueOf(params[2]);
                currentPulseValue = Integer.valueOf(params[3]);
                actionType = params[4];
                targetValue = params[5];

                // Construct manually a JSON object in Java, for testing purposes an object with an object
                JSONObject data = new JSONObject();
                data.put("action", actionType);
                data.put("alert", targetValue);
                data.put("probability", emotionProbabilityValue);
                data.put("type", emotionType);
                data.put("hr", currentPulseValue);
                data.put("userid", UserId);

                // URL and parameters for the connection, This particulary returns the information passed
                URL url = new URL("https://notificationLogicApps");
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setDoOutput(true);
                //httpConnection.setFollowRedirects(true);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
                httpConnection.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);

                //httpConnection.setRequestProperty("Accept", "application/json");
                // Not required
                // urlConnection.setRequestProperty("Content-Length", String.valueOf(input.getBytes().length));

                System.out.println(data.toString());

                // Writes the JSON parsed as string to the connection
                DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
                wr.write(data.toString().getBytes());
                Integer responseCode = httpConnection.getResponseCode();
                System.out.println(responseCode);

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
                System.out.println("system response follows:");
                System.out.println(content.toString());

            } catch (Exception e) {
                System.out.println("Error Message");
                System.out.println(e.getClass().getSimpleName());
                System.out.println(e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            //Do something with result string
            System.out.println(str);
        }

    }

}
