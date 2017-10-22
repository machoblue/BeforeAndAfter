package org.macho.beforeandafter.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.macho.beforeandafter.R;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class EditGoalFragment extends AppCompatActivity {
    private EditText goalWeight;
    private EditText goalRate;
    private Button save;
    private Button cancel;
    private SharedPreferences preferences;
    private View.OnClickListener onSaveButtonClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            preferences.edit().putFloat("GOAL_WEIGHT", Float.parseFloat(goalWeight.getText().toString())).commit();
            preferences.edit().putFloat("GOAL_RATE", Float.parseFloat(goalRate.getText().toString())).commit();
            finish();
        }
    };
    private View.OnClickListener onCancelButtonClickListener = new View.OnClickListener() {
        public void onClick(View view) {

            finish();
        }
    };

//    public static Fragment newFragment(final Activity activity) {
//        final EditGoalFragment fragment = new EditGoalFragment();
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_goal);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        goalWeight = (EditText) findViewById(R.id.goal_weight);
        goalRate = (EditText) findViewById(R.id.goal_rate);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        goalWeight.setText(String.valueOf(preferences.getFloat("GOAL_WEIGHT", 50)));
        goalRate.setText(String.valueOf(preferences.getFloat("GOAL_RATE", 20)));
        save.setOnClickListener(onSaveButtonClickListener);
        cancel.setOnClickListener(onCancelButtonClickListener);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
}
