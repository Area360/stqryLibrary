package nz.co.stqry.library.android.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Marc on 1/19/2015.
 */
public class EditTextSummaryPreference extends EditTextPreference {
    public EditTextSummaryPreference(Context context) {
        super(context);
    }

    public EditTextSummaryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        setSummary(this.getText());
        return super.onCreateView(parent);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            this.setSummary(getText());
        }
    }
}
