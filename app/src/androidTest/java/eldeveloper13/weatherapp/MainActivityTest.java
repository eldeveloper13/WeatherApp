package eldeveloper13.weatherapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eldeveloper13.weatherapp.weatherinfo.view.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
    public void onLaunchActivity_hasThreeTabs() {
        onView(withId(R.id.action_current))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
        onView(withId(R.id.action_short_term))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));;
        onView(withId(R.id.action_long_term))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));;
    }

    @Test
    public void onClickCurrentTab_shouldShowCurrentTabWeather() {
        onView(withId(R.id.action_current))
                .perform(click());
        onView(withId(R.id.degree_textview))
                .check(matches(isDisplayed()));
        onView(withId(R.id.feels_like_textview))
                .check(matches(isDisplayed()));
        onView(withId(R.id.weather_icon))
                .check(matches(isDisplayed()));
        onView(withId(R.id.weather_icon_text))
                .check(matches(isDisplayed()));
    }
}