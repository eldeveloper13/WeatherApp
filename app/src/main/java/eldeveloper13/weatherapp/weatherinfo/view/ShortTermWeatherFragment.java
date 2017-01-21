package eldeveloper13.weatherapp.weatherinfo.view;

import android.support.v4.app.Fragment;

import java.util.List;

import eldeveloper13.weatherapp.weatherinfo.ShortTermWeatherContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

public class ShortTermWeatherFragment extends Fragment implements ShortTermWeatherContract.View {

    @Override
    public void showSpinner() {

    }

    @Override
    public void hideSpinner() {

    }

    @Override
    public void showWeatherData(List<CurrentWeatherModel> data) {

    }

    @Override
    public void showError(String error) {

    }
}
