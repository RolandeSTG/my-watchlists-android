package com.rolande.mywatchlists.ui.settings;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.rolande.mywatchlists.R;

/**
 * Fragment to manage the Settings (or Preferences) of this app, the Android way.
 * The settings properties involved are the IP address and port number of
 * the 2 services used by the app, i.e. Watchlist and Quote services.
 *
 * @author Rolande
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    final static String TAG = LOG_TAG_PREFIX + SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);

        EditTextPreference watchlistIpPref   = findPreference(getResources().getString(R.string.watchlist_api_ip_key));
        EditTextPreference watchlistPortPref = findPreference(getResources().getString(R.string.watchlist_api_port_key));
        EditTextPreference quoteIpPref       = findPreference(getResources().getString(R.string.quote_api_ip_key));
        EditTextPreference quotePortPref     = findPreference(getResources().getString(R.string.quote_api_port_key));

        // Set an Ip input validator for watchlist & quote's IP address field

        EditTextPreference.OnBindEditTextListener ipListener = getIpOnBindEditTextListener();
        watchlistIpPref.setOnBindEditTextListener(ipListener);
        quoteIpPref.setOnBindEditTextListener(ipListener);

        // Set a port input validator on watchlist & quote's port number fields

        EditTextPreference.OnBindEditTextListener portListener = getPortOnBindEditTextListener();
        watchlistPortPref.setOnBindEditTextListener(portListener);
        quotePortPref.setOnBindEditTextListener(portListener);

        // Make sure final value of an IP address is valid before being persisted (saved)...

        Preference.OnPreferenceChangeListener ipAddressPcl = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                return (isIpAddressValid((String) newValue));
            }
        };

        watchlistIpPref.setOnPreferenceChangeListener(ipAddressPcl);
        quoteIpPref.setOnPreferenceChangeListener(ipAddressPcl);

        // Make sure final value of Port number is not empty before being persisted...

        Preference.OnPreferenceChangeListener portPcl = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                return (isPortNumberValid((String) newValue));
            }
        };

        watchlistPortPref.setOnPreferenceChangeListener(portPcl);
        quotePortPref.setOnPreferenceChangeListener(portPcl);
    }

    /**
     * Define an IP EditTextListener to restrict the IP address value entered to be
     * numbers and periods...
     *
     * Note: Cannot ensure here that IP address is complete, as one could hit OK on an empty
     *       field and it would be accepted like that.  Final validation will occur after
     *       editing is completed.
     *
     * @return callback created
     */
    private EditTextPreference.OnBindEditTextListener getIpOnBindEditTextListener() {

        return new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

                // Start editing at the end of the field
                editText.setSelection(editText.getText().length());   // needs to be AFTER setInputType() for cursor to be at the end

                // Define the filters, only 1 for now...
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend) {

                        if (end == start) {      // if deleting chars...
                            return null;         // ok
                        }

                        // Tentative IP address entered...
                        String ipAddress = dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length());

                        // Pattern to match to decide if we have a good IP Address format:
                        //
                        // Pattern= [1-9]{[0-9][0-9]}.[1-9]{[0-9][0-9]}.[1-9]{[0-9][0-9]}.[1-9]{[0-9][0-9]}
                        // i.e. each portion must start with 1..9, optionally followed by 0 to 2 sequences of digits.
                        // Expression is valid if we got 4 valid digit portions, such as 999.99.9.999...

                        String ipRegEx = "^[1-9]\\d{0,2}" +
                                          "(\\.([1-9]\\d{0,2}" +
                                          "(\\.([1-9]\\d{0,2}" +
                                          "(\\.([1-9]\\d{0,2})?)?)?)?)?)?";

                        if (! ipAddress.matches(ipRegEx)) {
                            return "";                  // do not accept this entry
                        }

                        // Each section of an IP address must not exceed 255, which is the value of an unsigned 8-bit int
                        String[] digitSections = ipAddress.split("\\.");

                        for (int i = 0; i < digitSections.length; i++) {
                            if (Integer.parseInt(digitSections[i]) > 0xFF) {         // must not exceed 8 bits (i.e. 255)
                                return "";
                            }
                        }

                        return null;         // ok so far
                    }
                };

                editText.setFilters(filters);
            }
        };

    }

    /**
     * Define a TCP/IP Port Number EditTextListener to restrict the port number to
     * an unsigned 16-bit integer, which is the maximum the TCP protocol can take.
     * The port value must therefore fall in a range from 0 to 65535...
     *
     * @return callback created
     */

    private EditTextPreference.OnBindEditTextListener getPortOnBindEditTextListener() {
        return new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                // Place cursor at the end of field...for some reason, setSelection(index) needs to be
                // placed after setting InputType...if placed before, does not work...

                editText.setSelection(editText.getText().length());

                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                        try {
                            int port = Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()));

                            if (port > 0 && port <= 0xFFFF)                      // 0xFFFF = (2^16)-1 = 65535
                                return null;
                        }
                        catch (NumberFormatException e) {}

                        return "";
                    }
                };

                editText.setFilters(filters);
            }
        };

    }

    /**
     * Validates that an IP address is properly formed, after end-user finished editing the field.
     * What remains to be checked, after having applied the in-edit Input filtering, is:
     * 1) That an IP address value is not empty
     * 2) That is has 4 digits sections, separated by period
     *
     * @param ipAddress The address to validate
     * @return true if format is valid; false otherwise
     */
    private boolean isIpAddressValid(String ipAddress) {

        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            Toast.makeText(requireContext(), R.string.field_cannot_be_blank, Toast.LENGTH_SHORT).show();
            return false;
        }

        // IP address must have 4 digits sections at the end...
        String ipRegEx = "^\\d{1,3}" + "\\.\\d{1,3}" + "\\.\\d{1,3}" + "\\.\\d{1,3}";

        if (!ipAddress.matches(ipRegEx)) {
            Toast.makeText(requireContext(), R.string.improper_ip_address_format, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;   // IP address if fine
    }

    /**
     * Ensures a port number is a valid integer value (i.e. not empty)
     *
     * @param strPort Port number as a string
     * @return true if port number is not empty/null; false otherwise
     */
    private boolean isPortNumberValid(String strPort) {

        if (strPort == null || strPort.trim().isEmpty()) {
            Toast.makeText(requireContext(), R.string.field_cannot_be_blank, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;   // Port range already checked by edit filters
    }

    /**
     * Normalize an IP address by removing redundant leading zeros, if any...
     * For instance, if ipAddress provided is "192.013.099.008", normalized IP returned
     * would be "192.13.99.8"
     *
     * @param ipAddress The address to normalize
     * @return a string ip address without leading zeros...
     */
    private String getNormalizeIpAddress(String ipAddress) {

        String[] digitSections = ipAddress.split("\\.");

        // get rid of any useless 0s, if any...
        StringBuilder normalizedIp = new StringBuilder();

        for (int i = 0; i < digitSections.length; i++) {
            int section = Integer.parseInt(digitSections[i]);

            normalizedIp.append(String.valueOf(section));
            if (i < 3) normalizedIp.append(".");
        }

        return (normalizedIp.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // addPreferencesFromResource(R.xml.preferences);
    }

}