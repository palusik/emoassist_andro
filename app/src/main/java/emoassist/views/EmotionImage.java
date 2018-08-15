package emoassist.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import emoassist.utils.CommonCalculations;

import vokaturi.vokaturisdk.entities.EmotionProbabilities;

import static emoassist.utils.CommonCalculations.getEmotionType;
import static emoassist.utils.CommonCalculations.getMax;
import static emoassist.utils.CommonCalculations.getMaxPos;

public class EmotionImage extends AppCompatImageView {

    public EmotionImage(Context context, AttributeSet attr) {
        super(context, attr);
    }
    public EmotionImage(Context context) {
        super(context);
    }

    public void updateImage(String appPackage, Resources res, EmotionProbabilities emotion) {
        String emojiBase = "happy";
        String emojiValue = "_0_50";

        int   maxPos     = getMaxPos( (float)emotion.happiness, (float)emotion.neutrality, (float)emotion.anger, (float)emotion.sadness, (float)emotion.fear);
        String emoType   = getEmotionType(maxPos);

        if (emoType == "H") {
            emojiBase = "happy";
            emojiValue = emotion.happiness > 0.5 ? "_50_100" : "_0_50";
        } else if (emoType == "N") {
            emojiBase = "neutral";
            emojiValue = emotion.neutrality > 0.5 ? "_50_100" : "_0_50";
        } else if (emoType == "A") {
            emojiBase = "angry";
            emojiValue = emotion.anger > 0.5 ? "_50_100" : "_0_50";
        } else if (emoType == "S") {
            emojiBase = "unhappy";
            emojiValue = emotion.sadness > 0.5 ? "_50_100" : "_0_50";
        } else if (emoType == "F") {
            emojiBase = "fear";
            emojiValue = emotion.fear > 0.5 ? "_50_100" : "_0_50";
        }

        String mDrawableName = emojiBase + emojiValue + "_low";
        int resID = res.getIdentifier(mDrawableName, "drawable", appPackage);
        Drawable drawable = res.getDrawable(resID);

        setImageDrawable(drawable);
    }
}
