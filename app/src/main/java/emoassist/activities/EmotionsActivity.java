package emoassist.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.Menu;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import emoassist.R;
import emoassist.interfaces.UpdatableActivity;
import emoassist.physiology.HRConnector;
import emoassist.services.AudioListenerService;
import emoassist.services.NotificationService;
import emoassist.type.VoiceType;
import emoassist.utils.ObjectSerializer;
import emoassist.utils.Utils;
import emoassist.utils.CommonCalculations;
import emoassist.views.EmotionBarView;
import emoassist.views.EmotionImage;
import emoassist.views.GraphView;
import vokaturi.vokaturisdk.entities.EmotionProbabilities;


@SuppressWarnings("deprecation")
public class EmotionsActivity extends AppCompatActivity implements UpdatableActivity
{
    private AudioListenerService audioListenerService = AudioListenerService.getInstance();
    private static final int PERMISSIONS_REQUEST_CODE = 5;

    private static Context mContext;

    private static HRConnector connector;

    TextView statusTextView, currentHRPulse;

    public Button connectButton;

    private static EmotionImage emotionImage1;
    private static EmotionImage emotionImage2;

    private static EmotionBarView block11;
    private static EmotionBarView block12;
    private static EmotionBarView block13;
    private static EmotionBarView block14;
    private static EmotionBarView block15;

    private static EmotionBarView block21;
    private static EmotionBarView block22;
    private static EmotionBarView block23;
    private static EmotionBarView block24;
    private static EmotionBarView block25;

    private static GraphView graphView;

    private static TextView bar11;
    private static TextView bar12;
    private static TextView bar13;
    private static TextView bar14;
    private static TextView bar15;

    private static TextView bar21;
    private static TextView bar22;
    private static TextView bar23;
    private static TextView bar24;
    private static TextView bar25;

    // show the values of angry probability
    private static TextView valueView1;
    private static TextView valueView2;
    private static TextView valueView3;
    private static TextView valueView4;
    private static TextView valueView5;

    private static TextView valueView;

    private CharSequence text;

    private static Switch swMode;


    // variables for evaluating borderlines

    ArrayList<String> actionPreferences = new ArrayList<>();
    ArrayList<String> alertPreferences = new ArrayList<>();
    ArrayList<String> probabilities = new ArrayList<>();
    ArrayList<String> minHeartRates = new ArrayList<>();
    ArrayList<String> maxHeartRates = new ArrayList<>();

    private String UserId;
    private Boolean remoteMonitor;
    private Integer currentPulseValue;
    private Integer measurementCountValue;

    private Date lastAlertedH;
    private Date lastAlertedA;
    private Date lastAlertedS;
    private Date lastAlertedF;
    private Date lastAlertedN;

    // delays for invoking alerts locally
    private static final Integer SAME_ALERT_DELAYS = 5;

    // default duration of vibration
    private static final Integer VIBRATION_DURATION = 200;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        connectButton = (Button) findViewById(R.id.connectButton);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        currentHRPulse = (TextView) findViewById(R.id.currentPulseTextView);
        //Create HRConnector object
        connector = new HRConnector(this);
        mContext = EmotionsActivity.this;

        if(shouldAskForPermissionsForGrants()) {
            ActivityCompat.requestPermissions(EmotionsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_CODE);
        } else {
            initializeApp();
        }

    }

    public static boolean shouldAskForPermissionsForGrants()
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int resRecordAudio = mContext.checkCallingOrSelfPermission(Manifest.permission.RECORD_AUDIO);
            return  resRecordAudio != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeApp() {
        initializeViews();

        try {
            audioListenerService.updatableActivities.add(this);
            audioListenerService.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews()
    {
        graphView = (GraphView) findViewById(R.id.graphView);

        emotionImage1 = (EmotionImage) findViewById(R.id.emotionImage1);
        block11 = (EmotionBarView) findViewById(R.id.block11);
        block12 = (EmotionBarView) findViewById(R.id.block12);
        block13 = (EmotionBarView) findViewById(R.id.block13);
        block14 = (EmotionBarView) findViewById(R.id.block14);
        block15 = (EmotionBarView) findViewById(R.id.block15);

        emotionImage2 = (EmotionImage) findViewById(R.id.emotionImage2);
        block21 = (EmotionBarView) findViewById(R.id.block21);
        block22 = (EmotionBarView) findViewById(R.id.block22);
        block23 = (EmotionBarView) findViewById(R.id.block23);
        block24 = (EmotionBarView) findViewById(R.id.block24);
        block25 = (EmotionBarView) findViewById(R.id.block25);

        bar11 = (TextView) findViewById(R.id.textView11);
        bar12 = (TextView) findViewById(R.id.textView12);
        bar13 = (TextView) findViewById(R.id.textView13);
        bar14 = (TextView) findViewById(R.id.textView14);
        bar15 = (TextView) findViewById(R.id.textView15);

        bar11 = (TextView) findViewById(R.id.textView21);
        bar12 = (TextView) findViewById(R.id.textView22);
        bar13 = (TextView) findViewById(R.id.textView23);
        bar14 = (TextView) findViewById(R.id.textView24);
        bar15 = (TextView) findViewById(R.id.textView25);


        EmotionProbabilities emptyEmotion = new EmotionProbabilities();
        emptyEmotion.neutrality = 0.0D;
        emptyEmotion.happiness = 0.0D;
        emptyEmotion.sadness = 0.0D;
        emptyEmotion.anger = 0.0D;
        emptyEmotion.fear = 0.0D;

        emotionImage1.updateImage(getPackageName(), getResources(), emptyEmotion);
        emotionImage2.updateImage(getPackageName(), getResources(), emptyEmotion);

        setNewThresholds();

    }

    public void updateUI(EmotionProbabilities emotionProbabilities, VoiceType voiceType)
    {
        EmotionImage image = null;
        ArrayList<Float> happinessValues = null;
        switch( voiceType ) {
            case Voice1:
                image = emotionImage1;
                happinessValues = graphView.hapinessValues1;
                break;
            case Voice2:
                image = emotionImage2;
                happinessValues = graphView.hapinessValues2;
                break;
        }


        if (happinessValues.size() > GraphView.MaxNumberOfGraphPoints) {
            happinessValues.remove(0);
        }

        float hValue = Utils.getInstance().getEmotionPoint(emotionProbabilities);
        happinessValues.add( (float) hValue);

        if (graphView != null) {
            graphView.invalidate();
        }
        updateBars( emotionProbabilities );
        image.updateImage(getPackageName(), getResources(), emotionProbabilities);


    }



    void updateBars( EmotionProbabilities emotionProbabilities ) {

        Float emotionProbability;
        String emotionType;

        block11.emotionValue = (float)emotionProbabilities.happiness;
        block11.voiceType = VoiceType.Voice1;
        block11.color = getResources().getColor(R.color.vkgreen);
        block11.invalidate();

        block12.emotionValue = (float)emotionProbabilities.neutrality;
        block12.voiceType = VoiceType.Voice1;
        block12.color = getResources().getColor(R.color.vkyellow);
        block12.invalidate();

        block13.emotionValue = (float)emotionProbabilities.anger;
        block13.voiceType = VoiceType.Voice1;
        block13.color = getResources().getColor(R.color.vkred);
        block13.invalidate();

        block14.emotionValue = (float)emotionProbabilities.sadness;
        block14.voiceType = VoiceType.Voice1;
        block14.color = getResources().getColor(R.color.vkblue);
        block14.invalidate();

        block15.emotionValue = (float)emotionProbabilities.fear;
        block15.voiceType = VoiceType.Voice1;
        block15.color = getResources().getColor(R.color.vkpurple);
        block15.invalidate();

        block21.emotionValue = (float)emotionProbabilities.happiness;
        block21.voiceType = VoiceType.Voice2;
        block21.color = getResources().getColor(R.color.vkgreen);
        block21.invalidate();

        block22.emotionValue = (float)emotionProbabilities.neutrality;
        block22.voiceType = VoiceType.Voice2;
        block22.color = getResources().getColor(R.color.vkyellow);
        block22.invalidate();

        block23.emotionValue = (float)emotionProbabilities.anger;
        block23.voiceType = VoiceType.Voice2;
        block23.color = getResources().getColor(R.color.vkred);
        block23.invalidate();

        block24.emotionValue = (float)emotionProbabilities.sadness;
        block24.voiceType = VoiceType.Voice2;
        block24.color = getResources().getColor(R.color.vkblue);
        block24.invalidate();

        block25.emotionValue = (float)emotionProbabilities.fear;
        block25.voiceType = VoiceType.Voice2;
        block25.color = getResources().getColor(R.color.vkpurple);
        block25.invalidate();

        // run evaluation against setup preferences
        evaluateEmotionalState(emotionProbabilities);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE)
        {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Audio recording permissions denied.", Toast.LENGTH_SHORT).show();
            } else {
                initializeApp();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioListenerService.cleanup();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return true;
        // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent usersetup = new Intent(this, SetupActivity.class);
                usersetup.putExtra("currentPulse", currentPulseValue);
                startActivityForResult(usersetup, 1);
                return true;
            case R.id.action_preferences:
                Intent preferences = new Intent(this, PreferencesActivity.class);
                startActivityForResult(preferences, 1);
                return true;
            case R.id.action_history:
                Intent history = new Intent(this, HistoryActivity.class);
                history.putExtra("userId", UserId);
                startActivityForResult(history, 1);
                return true;
            case R.id.action_help:
                Intent help = new Intent(this, HelpActivity.class);
                startActivityForResult(help, 1);
                return true;
            case R.id.action_exit:
                this.finish();
                return true;
            default:
                Toast.makeText(getApplicationContext(), "Menu item selected " + item.toString(),
                        Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){

            setNewThresholds();

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setStatus (String s ) {
        statusTextView.setText(s);
    }

    public void setPulse ( Integer seqence, Byte pulse, Integer count) {
        currentPulseValue = Math.abs(Integer.valueOf(pulse));
        measurementCountValue = count;
        currentHRPulse.setText(String.valueOf(pulse) + "|" + String.valueOf(count));

    }

    // connect-disconnect button onClick() handler
    public void connDisconn(View view) {
        if ( connector.isConnected() ) {
            connector.disconnect(false);
        } else {
            connector.connect();
        }
    }

    // switch enabled button onClick() handler
    public void switchMode(View view) {


        if (Boolean.valueOf(swMode.isChecked()) == true)
        {
            Toast.makeText(this, "Remote monitoring is enabled. ", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Remote monitoring is turned off. ", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        sharedPreferences.edit().putBoolean( "remote", swMode.isChecked()).apply();

        remoteMonitor = swMode.isChecked();

    }

    // reset thresholds based on shared preferences from previous screens

    void setNewThresholds () {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        String newUserId         = new String();
        Boolean newRemoteMonitor  = false;


        try {

            actionPreferences = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("actions", ObjectSerializer.serialize(new ArrayList<String>())));
            alertPreferences  = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("alerts", ObjectSerializer.serialize(new ArrayList<String>())));
            probabilities     = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("probabilities", ObjectSerializer.serialize(new ArrayList<String>())));
            minHeartRates     = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("minheartrates", ObjectSerializer.serialize(new ArrayList<String>())));
            maxHeartRates     = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("maxheartrates", ObjectSerializer.serialize(new ArrayList<String>())));
            newUserId         = sharedPreferences.getString("userId", "");
            newRemoteMonitor  = sharedPreferences.getBoolean("remote", false);


        } catch (IOException e) {
            e.printStackTrace();
        }

        // set shared preferences for actions
        if (actionPreferences.isEmpty()) {

            for (int i = 0; i < 5; i++) {
                actionPreferences.add("None");
            }
        }

        // set shared preference values for probabilities
        if (probabilities.isEmpty()) {

            for (int i = 0; i < 5; i++) {
                probabilities.add("95");
            }
        }

        Integer DEFAULT_HRMIN = getResources().getInteger(R.integer.hr_default_min_neutral);
        Integer DEFAULT_HRMAX = getResources().getInteger(R.integer.hr_default_max_neutral);
        Integer ABSOLUTE_HRMAX = getResources().getInteger(R.integer.hr_absolute_max);

        Integer HAPPY_OFFSET = getResources().getInteger(R.integer.hr_offset_happy);
        Integer ANGRY_OFFSET = getResources().getInteger(R.integer.hr_offset_angry);
        Integer SAD_OFFSET = getResources().getInteger(R.integer.hr_offset_sad);
        Integer FEAR_OFFSET = getResources().getInteger(R.integer.hr_offset_fear);

        // set shared preference values for minimal heart rates
        if (minHeartRates.isEmpty()) {

            minHeartRates.add(String.valueOf(DEFAULT_HRMAX + HAPPY_OFFSET));
            minHeartRates.add(String.valueOf(DEFAULT_HRMIN));
            minHeartRates.add(String.valueOf(DEFAULT_HRMAX + ANGRY_OFFSET));
            minHeartRates.add(String.valueOf(DEFAULT_HRMAX + SAD_OFFSET));
            minHeartRates.add(String.valueOf(DEFAULT_HRMAX + FEAR_OFFSET));
        }


       // set shared preference values for probabilities
        if (maxHeartRates.isEmpty()) {

            maxHeartRates.add(String.valueOf(ABSOLUTE_HRMAX));
            maxHeartRates.add(String.valueOf(DEFAULT_HRMAX));
            maxHeartRates.add(String.valueOf(ABSOLUTE_HRMAX));
            maxHeartRates.add(String.valueOf(ABSOLUTE_HRMAX));
            maxHeartRates.add(String.valueOf(ABSOLUTE_HRMAX));
        }

        valueView = (TextView) findViewById(R.id.valueView);
        swMode    = (Switch) findViewById(R.id.swMode);

        if (!newUserId.isEmpty()) {
            UserId = newUserId;
            valueView.setText(UserId);
            swMode.setClickable(true);
        }
        else {
            valueView.setText("Guest");
            swMode.setClickable(false);
        }

        if(newRemoteMonitor == null) remoteMonitor = false;
        else {
            remoteMonitor = newRemoteMonitor;
        }

        swMode.setChecked(remoteMonitor);

    }

    // evaluate emotional state and invoke notification service

    private void evaluateEmotionalState (EmotionProbabilities emotionProbabilities) {

        Integer posEmotions = CommonCalculations.getMaxPos((float) emotionProbabilities.happiness, (float) emotionProbabilities.neutrality, (float) emotionProbabilities.anger, (float) emotionProbabilities.sadness, (float) emotionProbabilities.fear);
        Float emotionProbabilityValue = CommonCalculations.getMax((float) emotionProbabilities.happiness, (float) emotionProbabilities.neutrality, (float) emotionProbabilities.anger, (float) emotionProbabilities.sadness, (float) emotionProbabilities.fear);

        String emotionType = CommonCalculations.getEmotionType(posEmotions);

        String todoValue = "";

        Boolean reportState = false;

        if (connector.isConnected()) {
            if (Integer.valueOf(minHeartRates.get(posEmotions)) <= currentPulseValue
                    && currentPulseValue < Integer.valueOf(maxHeartRates.get(posEmotions))
                    && emotionProbabilityValue >= Float.valueOf(probabilities.get(posEmotions))) {
                reportState = true;
            }
        } else {
            if (emotionProbabilityValue >= Float.valueOf(probabilities.get(posEmotions))) {
                reportState = true;
            }

        }

        if (actionPreferences.get(posEmotions) == "None") return;

        if (reportState == true) {

           NotificationService notificationService = NotificationService.getInstance();

           notificationService.raiseAlert(remoteMonitor, UserId, emotionType, emotionProbabilityValue, currentPulseValue, actionPreferences.get(posEmotions), alertPreferences);

            Boolean invokeAlertFlow = false;

            Date date = new Date();

            switch (emotionType) {
                case "H":
                    if (lastAlertedH == null || (date.getTime() - lastAlertedH.getTime()) > SAME_ALERT_DELAYS * 1000) {
                        lastAlertedH = date;
                        invokeAlertFlow = true;
                    }
                    break;
                case "N":
                    if (lastAlertedN == null || (date.getTime() - lastAlertedN.getTime()) > SAME_ALERT_DELAYS * 1000) {
                        lastAlertedN = date;
                        invokeAlertFlow = true;
                    }
                    break;
                case "A":
                    if (lastAlertedA == null || (date.getTime() - lastAlertedA.getTime()) > SAME_ALERT_DELAYS * 1000) {
                        lastAlertedA = date;
                        invokeAlertFlow = true;
                    }
                    break;
                case "S":
                    if (lastAlertedS == null || (date.getTime() - lastAlertedS.getTime()) > SAME_ALERT_DELAYS * 1000) {
                        lastAlertedS = date;
                        invokeAlertFlow = true;
                    }
                    break;
                case "F":
                    if (lastAlertedF == null || (date.getTime() - lastAlertedF.getTime()) > SAME_ALERT_DELAYS * 1000) {
                        lastAlertedF = date;
                        invokeAlertFlow = true;
                    }
                    break;
            }

            if (invokeAlertFlow == false) return;
            // if local actions, perform the actions locally
           if (remoteMonitor == false) {
               if (actionPreferences.get(posEmotions).equals("Music")) {
                   audioPlayer(alertPreferences.get(1));
               } else if (actionPreferences.get(posEmotions).equals("Picture")) {
                   showImage(alertPreferences.get(4));
               }
               else if (actionPreferences.get(posEmotions).equals("Vibrate")) {
                 vibrate();
               }
               else if (actionPreferences.get(posEmotions).equals("Activity")) {
                   todoValue = alertPreferences.get(0).toString();
               }
               else if (actionPreferences.get(posEmotions).equals("Book")) {
                   todoValue = alertPreferences.get(3).toString();
               }
               else if (actionPreferences.get(posEmotions).equals("Call")) {
                   todoValue = alertPreferences.get(2).toString();
               }

           }

           if (todoValue != "")
               Toast.makeText(this, "Informative Notification: Emotion Type " + emotionType + " is raised. \nProbability: " + String.valueOf(emotionProbabilityValue) + ", Heart Rate: " + currentPulseValue + "\nFollowing action is required: " + actionPreferences.get(posEmotions) + "\n do: " + todoValue, Toast.LENGTH_SHORT).show();
            else
               Toast.makeText(this, "Informative Notification: Emotion Type " + emotionType + " is raised. \nProbability: " + String.valueOf(emotionProbabilityValue) + ", Heart Rate: " + currentPulseValue + "\nFollowing action is required: " + actionPreferences.get(posEmotions), Toast.LENGTH_SHORT).show();

        }

    }

    // this will play in background music selected in preferences
    public void audioPlayer(String fileName){

        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        if(mp.isPlaying()) return;

        Uri myUri1 = Uri.parse(fileName);

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mp.setDataSource(getApplicationContext(), myUri1);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "Music file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Music file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "Music file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            Toast.makeText(getApplicationContext(), "Music file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Music file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        }
        mp.start();
    }

    // this will present the selected image from preferences
    public void showImage(String fileName){

        Toast toast = new Toast(getApplicationContext());

        Uri myUri1 = Uri.parse(fileName);
        ImageView view = new ImageView(getApplicationContext());

        try {
            InputStream is = getContentResolver().openInputStream(myUri1);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            view.setImageBitmap(bitmap);
            is.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Picture file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Picture file is not configured correctly! Please, update user preferences.", Toast.LENGTH_SHORT).show();
        }


        toast.setView(view);
        toast.show();

    }



    // this will present the selected image from preferences
    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(VIBRATION_DURATION);
        }
    }
}
