package pl.nkg.biblospk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import pl.nkg.biblospk.R;

public class RulesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        TextView textView = (TextView) findViewById(R.id.textView);

        Spanned rules;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            rules = Html.fromHtml(getText(R.string.rules).toString(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            rules = Html.fromHtml(getText(R.string.rules).toString());
        }

        textView.setText(rules);
    }
}
