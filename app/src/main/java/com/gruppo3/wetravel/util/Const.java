package com.gruppo3.wetravel.util;

/**
 * This class, that contains all permissions and activities request codes and extras tags, is created to avoid hard-wiring.
 *
 * @author Giovanni Barca
 */
public class Const {
    /**
     * String used to separate marker parameters when it's set as a network resource.
     */
    public static final String PARAMETER_SEPARATOR = ",";

    /**
     * {@link Const} for {@link com.gruppo3.wetravel.activities.InstructionActivity} requesting all {@link com.gruppo3.wetravel.activities.InstructionActivity#PERMISSIONS}.
     */
    public static final int ALL_PERMISSION = 1;

    public static final String MISSION_IDENTIFIER = "mission";

    public static final String EXTRA_LATITUDE = "lat";

    public static final String EXTRA_LONGITUDE = "lng";

    public static final String EXTRA_TITLE = "title";

    public static final String EXTRA_DETAILS = "details";

    /**
     * {@link Const} for {@link com.gruppo3.wetravel.activities.MapActivity} starting {@link com.gruppo3.wetravel.activities.InstructionActivity}.
     */
    public static final int INSTRUCTION_ACTIVITY = 100;

    public static final int ADD_MARKER_ACTIVITY = 101;

    public static final int INVITE_USER_ACTIVITY = 102;
}
