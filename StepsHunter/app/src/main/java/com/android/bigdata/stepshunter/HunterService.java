package com.android.bigdata.stepshunter;

/**
 * Created by Natalia on 2015-11-12.
 */
public class HunterService {

    private static final long DEFAULT_FREQUENCY = 30;
    private static final long MIN_FREQUENCY = 30;
    private static final long MAX_FREQUENCY = 30;
    private static long CURRENT_FREQUENCY = DEFAULT_FREQUENCY;

    private static HunterService ourInstance = new HunterService();

    public static HunterService getInstance() {
        return ourInstance;
    }

    private HunterService() {
    }

    public static void setCurrentFrequency(long newFrequency){
        CURRENT_FREQUENCY = newFrequency;
    }

    public static long getCurrentFrequenct(){
        return CURRENT_FREQUENCY;
    }

    //powrot do domyslnej czestotliwosci
    public static void setDefaultFrequency(){
        CURRENT_FREQUENCY = DEFAULT_FREQUENCY;
    }
}
