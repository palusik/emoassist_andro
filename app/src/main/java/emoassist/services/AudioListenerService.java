package emoassist.services;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import emoassist.interfaces.UpdatableActivity;
import emoassist.type.VoiceType;
import vokaturi.vokaturisdk.entities.EmotionProbabilities;
import vokaturi.vokaturisdk.entities.Voice;

/**
 * Class object used to hold microphone separated voice data
 */
class StereoData {

    /**
     * Microphone voice data
     */
    public float[] voiceData1;
    /**
     * Microphone voice data
     */
    public float[] voiceData2;

    /**
     * Separates microphone voice data
     * @param audioData Audio data from the microphone
     */
    public void getSeparatedData( float[] audioData ) {
        int voiceSize = (int)(audioData.length / 2);
        voiceData1 = new float[voiceSize];
        voiceData2 = new float[voiceSize];
        for( int index = 0; index < audioData.length; index++ ) {
            int scopeVoiceIndex = (int)index / 2;
            voiceData1[scopeVoiceIndex] =  audioData[index];
            int secondVoiceIndex = index + 1;
            if( secondVoiceIndex < audioData.length ) {
                voiceData2[scopeVoiceIndex] = audioData[secondVoiceIndex];
            }
        }
    }

}

/**
 * Singleton service used to retrieve microphone data and update existing activities accordingly
 */
public class AudioListenerService
{
    /**
     * Array list of existing updatable activities - activities that will receive emotion data
     */
    public ArrayList<UpdatableActivity> updatableActivities = new ArrayList<UpdatableActivity>();
    /**
     * Vokaturi voice data object
     */
    private Voice voice1 = new Voice(44100.f, 441000);
    /**
     * Vokaturi voice data object
     */
    private Voice voice2 = new Voice(44100.f, 441000);
    /**
     * The audio sample rate. We have an array because some devices might not be able to sample 44100
     */
    private final static int[] sampleRates = {44100};
    /**
     * The audio recorder object retrieveing the audio data
     */
    private AudioRecord audioRecorder = null;
    /**
     * Audio buffer collected from the microphone
     */
    private float[] buffer;
    /**
     * The sample rate of the audio recording
     */
    public int sRate;
    /**
     * The size of the audio buffer sampled
     */
    private int bufferSize;
    /**
     * Number of sample to be analized at once in a voice
     */
    private int numOfSamples = 3;

    /**
     * Current number of samples added
     */
    private int currentNumOfSamples = 0;

    /**
     * Updates the activities with the available emotion data
     * @param emotionProbabilities Object containing emotion data
     * @param voiceType The id of the voice used
     */
    public void updateActivities( EmotionProbabilities emotionProbabilities, VoiceType voiceType ) {

        for( int i = 0; i < updatableActivities.size(); i++ ) {

            UpdatableActivity activity = updatableActivities.get(i);
            activity.updateUI( emotionProbabilities, voiceType );
        }
    }

    /**
     * Returns the singleton instance of this audio listener service
     * @return Singleton instance of AudioListenerService
     */
    public static AudioListenerService getInstance()
    {
        AudioListenerService result = null;

        int i=0;
        do
        {
            result = new AudioListenerService(true,
                    MediaRecorder.AudioSource.MIC,
                    sampleRates[i],
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_FLOAT);

        } while(++i<sampleRates.length);

        return result;
    }

    /**
     * Triggered when audio data is available from the microphone
     */
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener()
    {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onPeriodicNotification(AudioRecord recorder)
        {
            audioRecorder.read(buffer, 0, buffer.length, AudioRecord.READ_BLOCKING);

            StereoData stereoData = new StereoData();
            stereoData.getSeparatedData(buffer);

            try {
                voice1.fill(stereoData.voiceData1);
                voice2.fill(stereoData.voiceData2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentNumOfSamples++;

            if (currentNumOfSamples % numOfSamples == 0) {
                try {
                    new ProcessAudioDataTask(voice1, VoiceType.Voice1).execute();
                    new ProcessAudioDataTask(voice2, VoiceType.Voice2).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentNumOfSamples = 0;
            }

        }

        public void onMarkerReached(AudioRecord recorder)
        {
            // NOT USED
        }
    };

    /**
     * Initialier method of the audio service
     * @param uncompressed Wether the audio data is compressed or uncomressed
     * @param audioSource The audio source (microphone identifier)
     * @param sampleRate The audio sample rate required
     * @param channelConfig The configuration of the audio channels
     * @param audioFormat The audio format of the samples
     */
    public AudioListenerService(boolean uncompressed, int audioSource, int sampleRate, int channelConfig, int audioFormat)
    {
        try
        {
            sRate = sampleRate;
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            buffer = new float[bufferSize];

            audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);

            if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED)
                throw new Exception("AudioRecord initialization failed");
            audioRecorder.setRecordPositionUpdateListener(updateListener);
            audioRecorder.setPositionNotificationPeriod(bufferSize);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Starts recording the audio data
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public synchronized void start()
    {
        audioRecorder.startRecording();
        audioRecorder.read(buffer, 0, buffer.length, AudioRecord.READ_BLOCKING);
    }

    /**
     * Asynchronous task for processing the audio data via the sdk. We do this to avoid prcessing on the main thread and freezing the application
     */
    public class ProcessAudioDataTask extends AsyncTask<EmotionProbabilities, Void, EmotionProbabilities> {

        private Voice processedVoice;
        private VoiceType processedVoiceType;

        /**
         * Task initializer
         * @param voice The voice object that processes the data
         * @param voiceType The identifier of the voice used
         */
        public ProcessAudioDataTask( Voice voice, VoiceType voiceType ) {
            processedVoice = voice;
            processedVoiceType = voiceType;
        }

        /**
         * Executes the background task
         * @param params The task parameter. This is not really needed but the signature enforces it
         * @return null if there's a failure. Emotion data if successfull
         */
        @Override
        protected EmotionProbabilities doInBackground(EmotionProbabilities... params) {
            try {
                return processedVoice.extract();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Triggered when the probabilities data is ready to be sent to the activities
         * @param probabilities The emotion probability object
         */
        @Override
        protected void onPostExecute(EmotionProbabilities probabilities) {
            if(probabilities.isValid) {
                updateActivities( probabilities, processedVoiceType );
            }
        }

        /**
         * Triggered right before execution
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * Triggered when there's a progress update. We're not using this
         * @param values Not used
         */
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /**
     * Destroys the voice objets for cleanup
     */
    public void cleanup() {
        voice1.destroy();
        voice2.destroy();
    }
}