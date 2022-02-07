package com.astro.slidekeyboard;
import android.app.Activity;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.android.inputmethodcommon.InputMethodSettingsFragment;

public class ImePreferences extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We overwrite the title of the activity, as the default one is "Voice Search".
        //setTitle(R.string.settings_name);
    }

    public static class Settings extends InputMethodSettingsFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
            setSubtypeEnablerTitle(R.string.select_language);
            addPreferencesFromResource(R.xml.ime_preferences);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            this.onCreate(savedInstanceState);
        }
    }
}
