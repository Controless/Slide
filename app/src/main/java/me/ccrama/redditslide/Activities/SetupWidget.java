package me.ccrama.redditslide.Activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import java.util.ArrayList;

import me.ccrama.redditslide.Adapters.SubChooseAdapter;
import me.ccrama.redditslide.ColorPreferences;
import me.ccrama.redditslide.R;
import me.ccrama.redditslide.UserSubscriptions;
import me.ccrama.redditslide.Visuals.FontPreferences;
import me.ccrama.redditslide.Widget.SubredditWidgetProvider;

/**
 * Created by carlo_000 on 5/4/2016.
 */
public class SetupWidget extends BaseActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disableSwipeBackLayout();
        getTheme().applyStyle(new FontPreferences(this).getCommentFontStyle().getResId(), true);
        getTheme().applyStyle(new FontPreferences(this).getPostFontStyle().getResId(), true);
        getTheme().applyStyle(new ColorPreferences(this).getFontStyle().getBaseId(), true);

        super.onCreate(savedInstanceState);
        assignAppWidgetId();
        doShortcut();
    }

    /**
     * Widget configuration activity,always receives appwidget Id appWidget Id =
     * unique id that identifies your widget analogy : same as setting view id
     * via @+id/viewname on layout but appwidget id is assigned by the system
     * itself
     */
    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void doShortcut() {

        setContentView(R.layout.activity_setup_widget);
        setupAppBar(R.id.toolbar, "New widget", true, false);

        ListView list = (ListView)findViewById(R.id.subs);
        final ArrayList<String> sorted = UserSubscriptions.getSubscriptionsForShortcut(SetupWidget.this);
        final SubChooseAdapter adapter = new SubChooseAdapter(this, sorted, UserSubscriptions.getAllSubreddits(this));
        list.setAdapter(adapter);

        (findViewById(R.id.sort)).clearFocus();
        ((EditText)findViewById(R.id.sort)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String result = editable.toString();
                adapter.getFilter().filter(result);
            }
        });
    }

    public String name;


    /**
     * This method right now displays the widget and starts a Service to fetch
     * remote data from Server
     */
    public void startWidget() {

        // this intent is essential to show the widget
        // if this intent is not included,you can't show
        // widget on homescreen
        SubredditWidgetProvider.setSubFromid(appWidgetId, name, this);
        int theme = 0;
        switch(((RadioGroup)findViewById(R.id.theme)).getCheckedRadioButtonId()){
            case R.id.dark:
                theme = 1;
                break;
            case R.id.light:
                theme = 2;
                break;
        }
        SubredditWidgetProvider.setThemeToId(appWidgetId, theme, this);
        SubredditWidgetProvider.setLargePreviews(appWidgetId, ((SwitchCompat)findViewById(R.id.previews)).isChecked(), this);

        {
            Intent intent = new Intent();
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(Activity.RESULT_OK, intent);
        }

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, SubredditWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
        sendBroadcast(intent);

        this.finish();

    }

}