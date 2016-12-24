package eldeveloper13.weatherapp.weatherinfo.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import eldeveloper13.weatherapp.services.darksky.DarkSkyService;
import eldeveloper13.weatherapp.services.darksky.ForecastResponse;
import eldeveloper13.weatherapp.weatherinfo.MainContract;
import retrofit2.Call;

import static org.junit.Assert.*;
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
        mSubject = new MainPresenter(mDarkSkyService);

        mSubject.attachView(mView);
    }

    @Test
    public void getWeather_callsShowWeatherData() {
//        ForecastResponse mockResponse = mock(ForecastResponse.class);
//        Call<ForecastResponse> mockCall = mock(Call.class);
//        when(mDarkSkyService.getForecast(anyString(), anyString(), anyString())).thenReturn(mockCall);
//        when(mockCall
//
//
//        mSubject.getWeather();
//
//        verify(mView).showCurrentWeather();
    }
}