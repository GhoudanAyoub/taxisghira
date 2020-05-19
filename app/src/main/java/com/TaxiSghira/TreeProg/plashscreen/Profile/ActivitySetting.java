package com.TaxiSghira.TreeProg.plashscreen.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatDelegate;

import com.TaxiSghira.TreeProg.plashscreen.R;

import org.jetbrains.annotations.NotNull;

public class ActivitySetting  extends PreferenceActivity {

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        Preference ratePref = findPreference("key_rate");
        Preference morePref = findPreference("key_more");
        Preference aboutPref = findPreference("key_about");
        final Preference prefTerm = findPreference(getString(R.string.pref_title_term));

        ratePref.setOnPreferenceClickListener(preference -> {
            try {
                getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
            } catch (ActivityNotFoundException e) {
                getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + getApplicationContext().getPackageName())));
            }
            return true;
        });
        morePref.setOnPreferenceClickListener(preference -> {
            try {
                getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
            } catch (ActivityNotFoundException e) {
                getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + getApplicationContext().getPackageName())));
            }
            return true;
        });

        aboutPref.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getApplicationContext().getString(R.string.pref_title_about));
            builder.setMessage(getApplicationContext().getString(R.string.Apropos));
            builder.setPositiveButton("OK", null);
            builder.show();
            return true;
        });

        prefTerm.setOnPreferenceClickListener(preference -> {
            dialogTerm(ActivitySetting.this);
            return true;
        });

    }

    @Override
    protected void onResume() {
        //  initToolbar();
        super.onResume();
    }

    public void dialogTerm(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.pref_title_term));
        builder.setMessage(activity.getString(R.string.content_term));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    //***************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @NotNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    public AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

}

