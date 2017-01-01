package eldeveloper13.weatherapp.weatherinfo.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Mock
    WeatherDataProvider mWeatherDataProvider;

    @Mock
    MainContract.View mView;

    MainContract.Presenter mSubject;

    @Before
    public void setup() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        MockitoAnnotations.initMocks(this);

        mSubject = new MainPresenter(mWeatherDataProvider);

        mSubject.attachView(mView);
    }

    @Test
    public void getWeather_callsShowWeatherData() {
        CurrentWeatherModel mockResponse = mock(CurrentWeatherModel.class);
        when(mWeatherDataProvider.getCurrentWeather(anyDouble(), anyDouble(), anyString())).thenReturn(Observable.just(mockResponse));
        when(mWeatherDataProvider.getCurrentWeather(anyDouble(), anyDouble(), anyString(), any(WeatherDataProvider.FetchStrategy.class))).thenReturn(Observable.just(mockResponse));
        when(mockResponse.getTimestamp()).thenReturn(12345L);
        when(mockResponse.getTemperature()).thenReturn(12.5);
        when(mockResponse.getFeelsLike()).thenReturn(8.7);
        when(mockResponse.getWeatherIcon()).thenReturn(CurrentWeatherModel.WeatherIcon.PartlyCloudyDay);
        when(mockResponse.getPrecipType()).thenReturn(CurrentWeatherModel.PrecipType.Rain);

        mSubject.getWeather();

        ArgumentCaptor<CurrentWeatherModel> argumentCaptor = ArgumentCaptor.forClass(CurrentWeatherModel.class);
        verify(mView).showCurrentWeather(argumentCaptor.capture());
        CurrentWeatherModel argument = argumentCaptor.getValue();

        assertThat(argument.getTimestamp()).isEqualTo(12345L);
        assertThat(argument.getTemperature()).isEqualTo(12.5);
        assertThat(argument.getFeelsLike()).isEqualTo(8.7);
        assertThat(argument.getWeatherIcon()).isEqualTo(CurrentWeatherModel.WeatherIcon.PartlyCloudyDay);
        assertThat(argument.getPrecipType()).isEqualTo(CurrentWeatherModel.PrecipType.Rain);
    }

    @Test
    public void getWeather_encountersError_shouldCallShowError() {
        TestSubscriber<CurrentWeatherModel> testSubscriber = new TestSubscriber<>();
        when(mWeatherDataProvider.getCurrentWeather(anyDouble(), anyDouble(), anyString(), any(WeatherDataProvider.FetchStrategy.class)))
                .thenReturn(Observable.<CurrentWeatherModel>error(new Exception("Test error")));
        mSubject.getWeather();

        verify(mView).showError();
    }
}