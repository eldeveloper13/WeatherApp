package eldeveloper13.weatherapp.weatherinfo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eldeveloper13.weatherapp.R;
import eldeveloper13.weatherapp.WeatherAppApplication;
import eldeveloper13.weatherapp.utils.DateTimeUtil;
import eldeveloper13.weatherapp.weatherinfo.CurrentWeatherContract;
import eldeveloper13.weatherapp.weatherinfo.model.CurrentWeatherModel;

public class CurrentWeatherFragment extends Fragment implements CurrentWeatherContract.View {

    @BindView(R.id.degree_textview)
    TextView mDegreeTextView;

    @BindView(R.id.feels_like_textview)
    TextView mFeelsLikeTextView;

    @BindView(R.id.weather_icon)
    ImageView mWeatherIconImageView;

    @BindView(R.id.weather_icon_text)
    TextView mWeatherIconTextView;

    @BindView(R.id.last_updated)
    TextView mLastUpdateTextView;

    CurrentWeatherModel mModel;

    public static CurrentWeatherFragment newInstance() {
        return new CurrentWeatherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((WeatherAppApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    //region CurrentWeatherContractor.View Methods
    @Override
    public void showCurrentWeather(CurrentWeatherModel model) {
        mDegreeTextView.setText(String.format("Temp: %.1fC", model.getTemperature()));
        if (model.getFeelsLike() != null) {
            mFeelsLikeTextView.setText(String.format("Feels like: %.1fC", model.getFeelsLike()));
            mFeelsLikeTextView.setVisibility(View.VISIBLE);
        } else {
            mFeelsLikeTextView.setVisibility(View.GONE);
        }
        mWeatherIconImageView.setImageResource(model.getWeatherIcon().getIconRes());
        mWeatherIconTextView.setText(model.getWeatherIcon().name());
        mLastUpdateTextView.setText(String.format("Last Updated: %s", DateTimeUtil.getDate(model.getTimestamp() * 1000)));
    }
    //endregion

    static class Extra {
        private static final String MODEL = "model";
    }
}
