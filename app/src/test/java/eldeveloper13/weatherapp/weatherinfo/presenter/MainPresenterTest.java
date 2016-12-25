package eldeveloper13.weatherapp.weatherinfo.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;
import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Mock
    DarkSkyService mDarkSkyService;

    @Mock
    MainContract.View mView;

    MainContract.Presenter mSubject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mSubject = new MainPresenter(mDarkSkyService);

        mSubject.attachView(mView);
    }

    @Test
    public void getWeather_callsShowWeatherData() {
        ForecastResponse mockResponse = mock(ForecastResponse.class);
        ForecastResponse.DataPoint mockDataPoint = mock(ForecastResponse.DataPoint.class);
        when(mDarkSkyService.getForecast(anyString(), anyString(), anyString())).thenReturn(Observable.just(mockResponse));
        when(mockResponse.getCurrently()).thenReturn(mockDataPoint);
        when(mockDataPoint.getTime()).thenReturn(12345L);
        when(mockDataPoint.getTemperature()).thenReturn(12.5);
        when(mockDataPoint.getApparentTemperature()).thenReturn(8.7);
        when(mockDataPoint.getIcon()).thenReturn("partly-cloudy-day");
        when(mockDataPoint.getPrecipType()).thenReturn("rain");

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
}