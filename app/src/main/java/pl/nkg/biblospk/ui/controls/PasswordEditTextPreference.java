package pl.nkg.biblospk.ui.controls;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.EditText;

public class PasswordEditTextPreference extends EditTextPreference {
    public PasswordEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PasswordEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PasswordEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordEditTextPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ((EditText)holder.findViewById(android.R.id.edit)).setBackgroundColor(Color.BLUE);
    }
}
