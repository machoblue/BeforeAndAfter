package org.macho.beforeandafter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.macho.beforeandafter.gallery.GalleryFragment2;
import org.macho.beforeandafter.preference.PreferenceFragment;
import org.macho.beforeandafter.record.RecordFragment;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class MainActivity extends AppCompatActivity {
    private ImageButton item0ImageButton;
    private ImageButton item1ImageButton;
    private ImageButton item2ImageButton;
    private ImageButton item3ImageButton;
    private TextView item0TextView;
    private TextView item1TextView;
    private TextView item2TextView;
    private TextView item3TextView;

    private ImageButton selectedImageButton;
    private TextView selectedTextView;

    private int colorSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        item0ImageButton = (ImageButton) findViewById(R.id.item_0_image);
        item1ImageButton = (ImageButton) findViewById(R.id.item_1_image);
        item2ImageButton = (ImageButton) findViewById(R.id.item_2_image);
        item3ImageButton = (ImageButton) findViewById(R.id.item_3_image);
        item0TextView = (TextView) findViewById(R.id.item_0_text);
        item1TextView = (TextView) findViewById(R.id.item_1_text);
        item2TextView = (TextView) findViewById(R.id.item_2_text);
        item3TextView = (TextView) findViewById(R.id.item_3_text);

        colorSelected = ContextCompat.getColor(this, R.color.colorBottomNaviItemPressed);

        RealmMigration migration = new RealmMigration() {
            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                RealmSchema schema = realm.getSchema();
                if (oldVersion == 0) {
                    schema.get("RecordDto").addField("memo", String.class);
                    oldVersion++;
                }
            }
        };

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1).migration(migration).build();
        Realm.setDefaultConfiguration(config);

        uncheckCurrentButtonAndCheck(item0ImageButton, item0TextView);
        getSupportFragmentManager().beginTransaction().add(R.id.content, RecordFragment.getInstance()).commit();

        AdUtil adUtil = AdUtil.getInstance();
        adUtil.requestConsentInfoUpdateIfNeed(getApplicationContext());

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        AdView adView = (AdView) findViewById(R.id.adView);
        adUtil.loadBannerAd(adView, getApplicationContext());
    }

    private void uncheckCurrentButtonAndCheck(ImageButton image, TextView text) {
        if (selectedImageButton != null) {
            selectedImageButton.clearColorFilter();
        }
        if (selectedTextView != null) {
            selectedTextView.setTextColor(Color.rgb(255, 255, 255));
        }
        selectedImageButton = image;
        selectedTextView = text;
        selectedImageButton.setColorFilter(colorSelected);
        selectedTextView.setTextColor(colorSelected);
    }

    public void onItem0Click(View view) {
        uncheckCurrentButtonAndCheck(item0ImageButton, item0TextView);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, RecordFragment.getInstance()).commit();
    }

    public void onItem1Click(View view) {
        System.out.println("*** MainActivity.onItem1Click ***");
        uncheckCurrentButtonAndCheck(item1ImageButton, item1TextView);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, GalleryFragment2.getInstance()).commit();
    }

    public void onItem2Click(View view) {
        System.out.println("*** MainActivity.onItem2Click - start");
        uncheckCurrentButtonAndCheck(item2ImageButton, item2TextView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getSupportFragmentManager().beginTransaction().replace(R.id.content, org.macho.beforeandafter.graphe2.GrapheFragment.getInstance()).commit();
        System.out.println("*** MainActivity.onItem2Click - end");
    }

    public void onItem3Click(View view) {
        uncheckCurrentButtonAndCheck(item3ImageButton, item3TextView);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, PreferenceFragment.newFragment(this)).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
