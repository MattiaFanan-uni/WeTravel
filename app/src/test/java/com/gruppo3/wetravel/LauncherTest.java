package com.gruppo3.wetravel;

import android.app.Activity;
import android.content.Intent;

import com.gruppo3.wetravel.activities.MapActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.android.controller.ComponentController;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({LauncherActivity.class})
public class LauncherTest {

    private ActivityController<LauncherActivity> controller;
    //private LauncherActivity spiedActivity = PowerMockito.spy(controller.get());


    @Before
    public void init() throws Exception {
        controller = Robolectric.buildActivity(LauncherActivity.class);
       //replaceComponentInActivityController(controller, spiedActivity);
    }

    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        Robolectric.buildActivity(LauncherActivity.class);
    }

    @Test
    public void notSubscribed() throws Exception {

        //when(spiedActivity, "isSubscribed", false);
        controller.create();

        Intent expectedIntent = new Intent(controller.get(), NotSubscribedActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());

    }

    @Test
    public void subscribed() throws Exception {

        //when(spiedActivity, "isSubscribed", true);
        controller.create();

        Intent expectedIntent = new Intent(controller.get(), MapActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    public static void replaceComponentInActivityController(ActivityController<?> activityController, Activity activity)
            throws NoSuchFieldException, IllegalAccessException {
        Field componentField = ComponentController.class.getDeclaredField("component");
        componentField.setAccessible(true);
        componentField.set(activityController, activity);
    }
}
