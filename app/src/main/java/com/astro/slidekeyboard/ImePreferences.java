package com.astro.slidekeyboard;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.android.inputmethodcommon.InputMethodSettingsFragment;

public class ImePreferences extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings_name);

        Settings settingsFragment = new Settings();
        getSupportFragmentManager().beginTransaction().replace(R.id.keyboard, settingsFragment).commit();

    }

    public static class Settings extends InputMethodSettingsFragment {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
            setSubtypeEnablerTitle(R.string.select_language);
            addPreferencesFromResource(R.xml.ime_preferences);
        }
    }
}
