package eldeveloper13.weatherapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity;

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        mActivity.runOnUiThread(wakeUpDevice);
    }

    @Test
    public void showWeatherTabsFalse_hidesWeatherTab() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().setWeatherTabsVisible(false);
            }
        });

        onView(withId(R.id.weather_tabs)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showWeatherTabsTrue_showsWeatherTabs() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().setWeatherTabsVisible(true);
            }
        });

        onView(withId(R.id.weather_tabs)).check(matches(isDisplayed()));
    }
}