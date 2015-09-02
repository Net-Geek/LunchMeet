package meet.lunch.lunchmeet.ui.utils;

import android.content.Context;

import java.util.Random;

import meet.lunch.lunchmeet.R;

public class RandomColorPicker {

    public int generateRandomMaterialColor(Context context) {
        int[] randomAndroidColors = context.getResources().getIntArray(R.array.randomColorArray);
        int randomMaterialColor = randomAndroidColors[new Random().nextInt(randomAndroidColors.length)];
        return randomMaterialColor;
    }

    public int[] pickRandomMaterialColorArray(Context context, int numberOfColors) {
        int[] randomAndroidColors = context.getResources().getIntArray(R.array.randomColorArray);
        int[] randomMaterialColorArray = new int[numberOfColors];
        for (int i = 0; i < numberOfColors ; i++) {
            randomMaterialColorArray[i] = randomAndroidColors[new Random().nextInt(randomAndroidColors.length)];
        }
        return randomMaterialColorArray;
    }
}
