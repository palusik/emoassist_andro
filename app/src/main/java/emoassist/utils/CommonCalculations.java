package emoassist.utils;

public class CommonCalculations {

    public static float getMax(float happy, float neutral, float angry, float sad, float fear) {
        float[] convertedValues = new float[5];

        convertedValues[0] = happy;
        convertedValues[1] = neutral;
        convertedValues[2] = angry;
        convertedValues[3] = sad;
        convertedValues[4] = fear;

        float max = convertedValues[0];

        for (int i = 1; i < convertedValues.length; i++) {
            if (convertedValues[i] > max) {
                max = convertedValues[i];
            }
        }
        return max * 100;
    }

    public static String getEmotionType(Integer pos) {

        if (pos == 0) return "H";
        else if (pos == 1) return "N";
        else if (pos == 2) return "A";
        else if (pos == 3) return "S";
        else if (pos == 4) return "F";

        return "";
    }

    public static int getMaxPos (float happy, float neutral, float angry, float sad, float fear) {
        float[] convertedValues = new float[5];

        convertedValues[0] = happy;
        convertedValues[1] = neutral;
        convertedValues[2] = angry;
        convertedValues[3] = sad;
        convertedValues[4] = fear;

        float max = convertedValues[0];
        int   pos = 0;
        for (int i = 1; i < convertedValues.length; i++) {
            if (convertedValues[i] > max) {
                max = convertedValues[i];
                pos = i;
            }
        }
        return pos;
    }


}
