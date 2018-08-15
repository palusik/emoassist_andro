package emoassist.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import emoassist.R;
import emoassist.utils.ObjectSerializer;

public class SetupActivity extends AppCompatActivity {

    private Integer DEFAULT_HRMIN;
    private Integer DEFAULT_HRMAX;
    private Integer ABSOLUTE_HRMAX;

    private Integer HAPPY_OFFSET;
    private Integer ANGRY_OFFSET;
    private Integer SAD_OFFSET;
    private Integer FEAR_OFFSET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DEFAULT_HRMIN = getResources().getInteger(R.integer.hr_default_min_neutral);
        DEFAULT_HRMAX = getResources().getInteger(R.integer.hr_default_max_neutral);
        ABSOLUTE_HRMAX = getResources().getInteger(R.integer.hr_absolute_max);

        HAPPY_OFFSET = getResources().getInteger(R.integer.hr_offset_happy);
        ANGRY_OFFSET = getResources().getInteger(R.integer.hr_offset_angry);
        SAD_OFFSET = getResources().getInteger(R.integer.hr_offset_sad);
        FEAR_OFFSET = getResources().getInteger(R.integer.hr_offset_fear);

        // assign currentPulse from main activity
        Integer currentPulse = this.getIntent().getIntExtra("currentPulse", 0);

        if (currentPulse == null) {
            currentPulse = 0;
        }


        EditText editTextCurrentPulse = (EditText) findViewById(R.id.editTextCurrentPulse);

        editTextCurrentPulse.setText(String.valueOf(currentPulse));


        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> newProbabilities = new ArrayList<>();
        ArrayList<String> newMinHeartRates = new ArrayList<>();
        ArrayList<String> newMaxHeartRates = new ArrayList<>();

        String newUserId = new String();

        try {

            newProbabilities = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("probabilities", ObjectSerializer.serialize(new ArrayList<String>())));
            newMinHeartRates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("minheartrates", ObjectSerializer.serialize(new ArrayList<String>())));
            newMaxHeartRates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("maxheartrates", ObjectSerializer.serialize(new ArrayList<String>())));
            newUserId        = sharedPreferences.getString("userId", "");

        } catch (IOException e) {
            e.printStackTrace();
        }

        EditText editTextUser = (EditText) findViewById(R.id.editTextUser);

        // add default selection options
        Spinner spinner11 = (Spinner) findViewById(R.id.spinner11);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner12);
        Spinner spinner13 = (Spinner) findViewById(R.id.spinner13);
        Spinner spinner14 = (Spinner) findViewById(R.id.spinner14);
        Spinner spinner15 = (Spinner) findViewById(R.id.spinner15);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.probabilities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner11.setAdapter(adapter);
        spinner12.setAdapter(adapter);
        spinner13.setAdapter(adapter);
        spinner14.setAdapter(adapter);
        spinner15.setAdapter(adapter);


        Log.i("GOTHERE", "blabla");

        // add default selection options
        EditText editText21 = (EditText) findViewById(R.id.editText21);
        EditText editText22 = (EditText) findViewById(R.id.editText22);
        EditText editText23 = (EditText) findViewById(R.id.editText23);
        EditText editText24 = (EditText) findViewById(R.id.editText24);
        EditText editText25 = (EditText) findViewById(R.id.editText25);

        // add default selection options
        EditText editText31 = (EditText) findViewById(R.id.editText31);
        EditText editText32 = (EditText) findViewById(R.id.editText32);
        EditText editText33 = (EditText) findViewById(R.id.editText33);
        EditText editText34 = (EditText) findViewById(R.id.editText34);
        EditText editText35 = (EditText) findViewById(R.id.editText35);

        // set shared preferences for actions
        if (!newProbabilities.isEmpty()) {

            spinner11.setSelection(adapter.getPosition(newProbabilities.get(0)), true);
            spinner12.setSelection(adapter.getPosition(newProbabilities.get(1)), true);
            spinner13.setSelection(adapter.getPosition(newProbabilities.get(2)), true);
            spinner14.setSelection(adapter.getPosition(newProbabilities.get(3)), true);
            spinner15.setSelection(adapter.getPosition(newProbabilities.get(4)), true);
        }
        else {
            spinner11.setSelection(7, true);
            spinner12.setSelection(7, true);
            spinner13.setSelection(7, true);
            spinner14.setSelection(7, true);
            spinner15.setSelection(7, true);

        }

        // set shared preference values for alerts
        if (!newMinHeartRates.isEmpty()) {

            editText21.setText(newMinHeartRates.get(0));
            editText22.setText(newMinHeartRates.get(1));
            editText23.setText(newMinHeartRates.get(2));
            editText24.setText(newMinHeartRates.get(3));
            editText25.setText(newMinHeartRates.get(4));

        }
        else {
            editText21.setText(String.valueOf(DEFAULT_HRMAX + HAPPY_OFFSET));
            editText22.setText(String.valueOf(DEFAULT_HRMIN));
            editText23.setText(String.valueOf(DEFAULT_HRMAX + ANGRY_OFFSET));
            editText24.setText(String.valueOf(DEFAULT_HRMAX + SAD_OFFSET));
            editText25.setText(String.valueOf(DEFAULT_HRMAX + FEAR_OFFSET));
        }

        // set shared preference values for alerts
        if (!newMaxHeartRates.isEmpty()) {

            editText31.setText(newMaxHeartRates.get(0));
            editText32.setText(newMaxHeartRates.get(1));
            editText33.setText(newMaxHeartRates.get(2));
            editText34.setText(newMaxHeartRates.get(3));
            editText35.setText(newMaxHeartRates.get(4));
       }
        else {
            editText31.setText(String.valueOf(ABSOLUTE_HRMAX));
            editText32.setText(String.valueOf(DEFAULT_HRMAX));
            editText33.setText(String.valueOf(ABSOLUTE_HRMAX));
            editText34.setText(String.valueOf(ABSOLUTE_HRMAX));
            editText35.setText(String.valueOf(ABSOLUTE_HRMAX));

        }

        Log.i("BEFORE USER", "blabla");

        if (!newUserId.isEmpty()) {
           editTextUser.setText(newUserId);
        }

    }

    // set results ok when returning back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_OK);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Save preferences action by button
    public void setupSave (View view) {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> probabilities = new ArrayList<>();
        ArrayList<String> minheartrates = new ArrayList<>();
        ArrayList<String> maxheartrates = new ArrayList<>();

        Spinner spinner11 = (Spinner) findViewById(R.id.spinner11);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner12);
        Spinner spinner13 = (Spinner) findViewById(R.id.spinner13);
        Spinner spinner14 = (Spinner) findViewById(R.id.spinner14);
        Spinner spinner15 = (Spinner) findViewById(R.id.spinner15);

        EditText editText21 = (EditText) findViewById(R.id.editText21);
        EditText editText22 = (EditText) findViewById(R.id.editText22);
        EditText editText23 = (EditText) findViewById(R.id.editText23);
        EditText editText24 = (EditText) findViewById(R.id.editText24);
        EditText editText25 = (EditText) findViewById(R.id.editText25);

        EditText editText31 = (EditText) findViewById(R.id.editText31);
        EditText editText32 = (EditText) findViewById(R.id.editText32);
        EditText editText33 = (EditText) findViewById(R.id.editText33);
        EditText editText34 = (EditText) findViewById(R.id.editText34);
        EditText editText35 = (EditText) findViewById(R.id.editText35);

        probabilities.add(spinner11.getSelectedItem().toString());
        probabilities.add(spinner12.getSelectedItem().toString());
        probabilities.add(spinner13.getSelectedItem().toString());
        probabilities.add(spinner14.getSelectedItem().toString());
        probabilities.add(spinner15.getSelectedItem().toString());

        minheartrates.add(editText21.getText().toString());
        minheartrates.add(editText22.getText().toString());
        minheartrates.add(editText23.getText().toString());
        minheartrates.add(editText24.getText().toString());
        minheartrates.add(editText25.getText().toString());

        maxheartrates.add(editText31.getText().toString());
        maxheartrates.add(editText32.getText().toString());
        maxheartrates.add(editText33.getText().toString());
        maxheartrates.add(editText34.getText().toString());
        maxheartrates.add(editText35.getText().toString());

        EditText editTextUser = (EditText) findViewById(R.id.editTextUser);

        try {

            sharedPreferences.edit().putString("probabilities", ObjectSerializer.serialize(probabilities)).apply();
            sharedPreferences.edit().putString("minheartrates", ObjectSerializer.serialize(minheartrates)).apply();
            sharedPreferences.edit().putString("maxheartrates", ObjectSerializer.serialize(maxheartrates)).apply();
            sharedPreferences.edit().putString( "userId", editTextUser.getText().toString()).apply();

            // for blank user reset remote monitoring
            if (editTextUser.getText().toString() == "") {
                sharedPreferences.edit().putBoolean( "remote", false).apply();
            }


        } catch (IOException e) {

            e.printStackTrace();

        }

        // set remote monitoring to false if userId is empty

    }

    // connect-disconnect button onClick() handler
    public void setupReset (View view) {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> probabilities = new ArrayList<>();

        Spinner spinner11 = (Spinner) findViewById(R.id.spinner11);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner12);
        Spinner spinner13 = (Spinner) findViewById(R.id.spinner13);
        Spinner spinner14 = (Spinner) findViewById(R.id.spinner14);
        Spinner spinner15 = (Spinner) findViewById(R.id.spinner15);

        EditText editText21 = (EditText) findViewById(R.id.editText21);
        EditText editText22 = (EditText) findViewById(R.id.editText22);
        EditText editText23 = (EditText) findViewById(R.id.editText23);
        EditText editText24 = (EditText) findViewById(R.id.editText24);
        EditText editText25 = (EditText) findViewById(R.id.editText25);

        EditText editText31 = (EditText) findViewById(R.id.editText31);
        EditText editText32 = (EditText) findViewById(R.id.editText32);
        EditText editText33 = (EditText) findViewById(R.id.editText33);
        EditText editText34 = (EditText) findViewById(R.id.editText34);
        EditText editText35 = (EditText) findViewById(R.id.editText35);

        spinner11.setSelection(7, true);
        spinner12.setSelection(7, true);
        spinner13.setSelection(7, true);
        spinner14.setSelection(7, true);
        spinner15.setSelection(7, true);

        editText21.setText(String.valueOf(DEFAULT_HRMAX + HAPPY_OFFSET));
        editText22.setText(String.valueOf(DEFAULT_HRMIN));
        editText23.setText(String.valueOf(DEFAULT_HRMAX + ANGRY_OFFSET));
        editText24.setText(String.valueOf(DEFAULT_HRMAX + SAD_OFFSET));
        editText25.setText(String.valueOf(DEFAULT_HRMAX + FEAR_OFFSET));


        // set shared preference values for alerts
        editText31.setText(String.valueOf(ABSOLUTE_HRMAX));
        editText32.setText(String.valueOf(DEFAULT_HRMAX));
        editText33.setText(String.valueOf(ABSOLUTE_HRMAX));
        editText34.setText(String.valueOf(ABSOLUTE_HRMAX));
        editText35.setText(String.valueOf(ABSOLUTE_HRMAX));


        try {

            sharedPreferences.edit().putString("probabilities", ObjectSerializer.serialize(probabilities)).apply();
            sharedPreferences.edit().putString("minheartrates", ObjectSerializer.serialize(probabilities)).apply();
            sharedPreferences.edit().putString("maxheartrates", ObjectSerializer.serialize(probabilities)).apply();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    // connect-disconnect button onClick() handler
    public void setupBaseline (View view) {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> probabilities = new ArrayList<>();

        EditText editTextCurrentPulse = (EditText) findViewById(R.id.editTextCurrentPulse);

        Integer currentPulse = Integer.valueOf(editTextCurrentPulse.getText().toString());

        if (currentPulse <= 30) {
            Toast.makeText(getApplicationContext(), "Current pulse is below normal values, reset is not allowed. ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        EditText editText21 = (EditText) findViewById(R.id.editText21);
        EditText editText22 = (EditText) findViewById(R.id.editText22);
        EditText editText23 = (EditText) findViewById(R.id.editText23);
        EditText editText24 = (EditText) findViewById(R.id.editText24);
        EditText editText25 = (EditText) findViewById(R.id.editText25);

        EditText editText31 = (EditText) findViewById(R.id.editText31);
        EditText editText32 = (EditText) findViewById(R.id.editText32);
        EditText editText33 = (EditText) findViewById(R.id.editText33);
        EditText editText34 = (EditText) findViewById(R.id.editText34);
        EditText editText35 = (EditText) findViewById(R.id.editText35);

        editText21.setText(String.valueOf(currentPulse + HAPPY_OFFSET));
        editText22.setText(String.valueOf(currentPulse - 10));
        editText23.setText(String.valueOf(currentPulse + ANGRY_OFFSET));
        editText24.setText(String.valueOf(currentPulse + SAD_OFFSET));
        editText25.setText(String.valueOf(currentPulse + FEAR_OFFSET));


        // set shared preference values for alerts
        editText31.setText(String.valueOf(ABSOLUTE_HRMAX));
        editText32.setText(String.valueOf(currentPulse));
        editText33.setText(String.valueOf(ABSOLUTE_HRMAX));
        editText34.setText(String.valueOf(ABSOLUTE_HRMAX));
        editText35.setText(String.valueOf(ABSOLUTE_HRMAX));

    }

}
