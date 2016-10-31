package pl.nkg.biblospk.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        Spanned rules;
        StringBuilder sb = new StringBuilder();
        sb.append(getText(R.string.rules));
        sb.append("<p>");
        sb.append(getText(R.string.app_name));
        sb.append(" v");
        sb.append(pInfo.versionName).append(" (build ").append(pInfo.versionCode).append(")<br/>");
        sb.append(getText(R.string.copyright));
        sb.append("<br/>");
        sb.append(getText(R.string.url));
        sb.append("</p>");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            rules = Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            rules = Html.fromHtml(sb.toString());
        }

        textView.setText(rules);
    }
}
