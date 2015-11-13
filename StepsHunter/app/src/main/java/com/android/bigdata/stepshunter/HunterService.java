package com.android.bigdata.stepshunter;

/**
 * Created by Natalia on 2015-11-12.
 */
public class HunterService {

    private final long DEFAULT_FREQUENCY = 30;
    private final long MIN_FREQUENCY = 30;
    private final long MAX_FREQUENCY = 30;
    private long CURRENT_FREQUENCY = DEFAULT_FREQUENCY;

    private static HunterService ourInstance = new HunterService();

    public static HunterService getInstance() {
        return ourInstance;
    }

    private HunterService() {
    }
}
