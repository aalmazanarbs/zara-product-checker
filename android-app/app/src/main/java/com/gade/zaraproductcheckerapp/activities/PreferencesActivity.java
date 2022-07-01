package com.gade.zaraproductcheckerapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentServiceHandler;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

import static com.gade.zaraproductcheckerapp.util.RxUtil.applyCompletableSchedulers;
import static com.gade.zaraproductcheckerapp.util.UIUtil.showShortToast;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PreferencesFragment()).commit();
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final CompositeDisposable disposables = new CompositeDisposable();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            disposables.dispose();
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (getString(R.string.check_products_in_background).equals(key)) {
                ZaraProductCheckerJobIntentServiceHandler.startOrStopPeriodicallyBackground(getActivity().getApplicationContext());
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if (getString(R.string.storage_clear_cache).equals(preference.getKey())) {
                disposables.add(clearImageDiskCache().compose(applyCompletableSchedulers()).subscribe(() -> showShortToast(getActivity(), "Done")));
            }
            return super.onPreferenceTreeClick(preference);
        }

        private Completable clearImageDiskCache() {
            return Completable.fromAction(() -> Glide.get(getActivity()).clearDiskCache());
        }
    }
}
