package com.sensorberg.easyipc.testapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EspressoActivity0Test {

	@Rule
	public ActivityTestRule<EspressoActivity0> mActivityTestRule = new ActivityTestRule<>(EspressoActivity0.class);

	@Test
	public void espressoActivityTest() {

		for (int i = 0; i < 10; i++) {
			Robot.waitABit();
			Robot.checkText(EspressoActivity0.lastSent);
			Robot.clickButton();
		}
	}

}
