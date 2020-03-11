package com.gruppo3.wetravel.util;

/**
 * This class, that contains all permissions and activities request codes, is created to avoid hard-wiring.
 *
 * @author Giovanni Barca
 */
public class RequestCode {
    /**
     * {@link RequestCode} for {@link com.gruppo3.wetravel.activities.InstructionActivity} requesting all {@link com.gruppo3.wetravel.activities.InstructionActivity#PERMISSIONS}.
     */
    public static final int ALL_PERMISSION = 1;

    /**
     * {@link RequestCode} for {@link com.gruppo3.wetravel.activities.MapActivity} starting {@link com.gruppo3.wetravel.activities.InstructionActivity}.
     */
    public static final int INSTRUCTION_ACTIVITY = 100;
}
