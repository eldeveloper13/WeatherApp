package eldeveloper13.weatherapp.weatherinfo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

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

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    CurrentWeatherModel mModel;

    @Inject
    CurrentWeatherContract.Presenter mPresenter;

    double mLatitude;
    double mLongitude;

    public static CurrentWeatherFragment newInstance(double latitude, double longitude) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(Extras.LATITUDE, latitude);
        bundle.putDouble(Extras.LONGITUDE, longitude);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        ((WeatherAppApplication) getActivity().getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this, view);
        mLatitude = getArguments().getDouble(Extras.LATITUDE);
        mLongitude = getArguments().getDouble(Extras.LONGITUDE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((WeatherAppApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        mPresenter.getWeather(mLatitude, mLongitude);
    }

    @Override
    public void onPause() {
        mPresenter.detachView();
        super.onPause();
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

    @Override
    public void showSpinner() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSpinner() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(getView(), error, Snackbar.LENGTH_LONG).show();
    }
    //endregion

    static class Extras {
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
    }
}
