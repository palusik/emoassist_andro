package emoassist.interfaces;

import emoassist.type.VoiceType;
import vokaturi.vokaturisdk.entities.EmotionProbabilities;

/**
 * Interface that enforces the updatable functionality. This method is triggered on them when new emotion data is available.
 */
public interface UpdatableActivity {
    public void updateUI( EmotionProbabilities emotionProbabilities, VoiceType voiceType );
}
