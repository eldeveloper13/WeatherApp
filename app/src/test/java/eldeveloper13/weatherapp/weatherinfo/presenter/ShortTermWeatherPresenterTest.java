package eldeveloper13.weatherapp.weatherinfo.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import eldeveloper13.weatherapp.provider.WeatherDataProvider;
import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.weatherinfo.ShortTermWeatherContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShortTermWeatherPresenterTest {

    @Mock
    WeatherDataProvider mWeatherDataProvider;

    @Mock
    ShortTermWeatherContract.View mView;

    ShortTermWeatherContract.Presenter mSubject;

    @Before
    public void setup() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        MockitoAnnotations.initMocks(this);

        mSubject = new ShortTermWeatherPresenter(mWeatherDataProvider);

        mSubject.attachView(mView);
    }

    @After
    public void teardown() {
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void loadWeather_shouldCallShowSpinner_thenCallGetShortTermWeather() {
        InOrder inOrder = Mockito.inOrder(mWeatherDataProvider, mView);
        when(mWeatherDataProvider.getShortTermWeather(anyDouble(), anyDouble(), anyString()))
                .thenReturn(Observable.<List<CurrentWeatherModel>>empty());

        mSubject.loadWeather(123.45, -123.45);

        inOrder.verify(mView).showSpinner();
        inOrder.verify(mWeatherDataProvider).getShortTermWeather(123.45, -123.45, DarkSkyService.CA);
    }

    @Test
    public void loadWeather_onDataReturned_shouldCallHideSpinner_thenCallShowWeatherData() {
        InOrder inOrder = Mockito.inOrder(mWeatherDataProvider, mView);
        List<CurrentWeatherModel> mockResponse = getMockWeatherDataProviderResponse();
        when(mWeatherDataProvider.getShortTermWeather(anyDouble(), anyDouble(), anyString()))
                .thenReturn(Observable.just(mockResponse));
        when(mWeatherDataProvider.getShortTermWeather(anyDouble(), anyDouble(), anyString(), any(WeatherDataProvider.FetchStrategy.class)))
                .thenReturn(Observable.just(mockResponse));

        mSubject.loadWeather(123.45, -123.45);

        inOrder.verify(mView).hideSpinner();
        ArgumentCaptor<List<CurrentWeatherModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        inOrder.verify(mView).showWeatherData(argumentCaptor.capture());

        List<CurrentWeatherModel> models = argumentCaptor.getValue();
        assertThat(models.size()).isEqualTo(3);

        CurrentWeatherModel model1 = models.get(0);
        assertThat(model1.getTimestamp()).isEqualTo(1);

        CurrentWeatherModel model2 = models.get(1);
        assertThat(model2.getTimestamp()).isEqualTo(2);

        CurrentWeatherModel model3 = models.get(2);
        assertThat(model3.getTimestamp()).isEqualTo(3);
    }

    @Test
    public void loadWeather_onErrorReturned_showCallHideSpinner_thenCallShowError() {
        InOrder inOrder = Mockito.inOrder(mWeatherDataProvider, mView);
        when(mWeatherDataProvider.getShortTermWeather(anyDouble(), anyDouble(), anyString()))
                .thenReturn(Observable.<List<CurrentWeatherModel>>error(new Exception("Test error message")));

        mSubject.loadWeather(123.45, -123.45);

        inOrder.verify(mView).hideSpinner();
        inOrder.verify(mView).showError("Error loading weather: Test error message");
    }

    private List<CurrentWeatherModel> getMockWeatherDataProviderResponse() {
        CurrentWeatherModel model1 = new CurrentWeatherModel(1, 1.0, 1.1, CurrentWeatherModel.PrecipType.None, CurrentWeatherModel.WeatherIcon.ClearDay);
        CurrentWeatherModel model2 = new CurrentWeatherModel(2, 2.0, 2.1, CurrentWeatherModel.PrecipType.Rain, CurrentWeatherModel.WeatherIcon.PartlyCloudyNight);
        CurrentWeatherModel model3 = new CurrentWeatherModel(3, 3.0, 3.1, CurrentWeatherModel.PrecipType.Sleet, CurrentWeatherModel.WeatherIcon.Wind);
        return Arrays.asList(model1, model2, model3);
    }
}