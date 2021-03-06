package emoassist.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class PreferencesActivity extends AppCompatActivity {

    final int ACTIVITY_CHOOSE_AUDIO = 1;
    final int ACTIVITY_CHOOSE_VIDEO = 2;
    final int ACTIVITY_CHOOSE_CONTACT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> newActionPreferences = new ArrayList<>();
        ArrayList<String> newAlertPreferences = new ArrayList<>();

        try {

            newActionPreferences = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("actions", ObjectSerializer.serialize(new ArrayList<String>())));
            newAlertPreferences  = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("alerts", ObjectSerializer.serialize(new ArrayList<String>())));


        } catch (IOException e) {
            e.printStackTrace();
        }

        // add default selection options
        Spinner spinner11 = (Spinner) findViewById(R.id.spinner11);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner12);
        Spinner spinner13 = (Spinner) findViewById(R.id.spinner13);
        Spinner spinner14 = (Spinner) findViewById(R.id.spinner14);
        Spinner spinner15 = (Spinner) findViewById(R.id.spinner15);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.preferences_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner11.setAdapter(adapter);
        spinner12.setAdapter(adapter);
        spinner13.setAdapter(adapter);
        spinner14.setAdapter(adapter);
        spinner15.setAdapter(adapter);

        // set shared preferences for actions
        if (!newActionPreferences.isEmpty()) {

            spinner11.setSelection(adapter.getPosition(newActionPreferences.get(0)));
            spinner12.setSelection(adapter.getPosition(newActionPreferences.get(1)));
            spinner13.setSelection(adapter.getPosition(newActionPreferences.get(2)));
            spinner14.setSelection(adapter.getPosition(newActionPreferences.get(3)));
            spinner15.setSelection(adapter.getPosition(newActionPreferences.get(4)));
            //spinner11.setSelection(spinnerPosition);
        }

        // set shared preference values for alerts
        if (!newAlertPreferences.isEmpty()) {

            EditText editText1 = (EditText) findViewById(R.id.editTextAlert1);
            EditText editText2 = (EditText) findViewById(R.id.editTextAlert2);
            EditText editText3 = (EditText) findViewById(R.id.editTextAlert3);
            EditText editText4 = (EditText) findViewById(R.id.editTextAlert4);
            EditText editText5 = (EditText) findViewById(R.id.editTextAlert5);

            editText1.setText(newAlertPreferences.get(0));
            editText2.setText(newAlertPreferences.get(1));
            editText3.setText(newAlertPreferences.get(2));
            editText4.setText(newAlertPreferences.get(3));
            editText5.setText(newAlertPreferences.get(4));

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
    public void preferencesSave (View view) {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> actions = new ArrayList<>();

        Spinner spinner11 = (Spinner) findViewById(R.id.spinner11);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner12);
        Spinner spinner13 = (Spinner) findViewById(R.id.spinner13);
        Spinner spinner14 = (Spinner) findViewById(R.id.spinner14);
        Spinner spinner15 = (Spinner) findViewById(R.id.spinner15);

        actions.add(spinner11.getSelectedItem().toString());
        actions.add(spinner12.getSelectedItem().toString());
        actions.add(spinner13.getSelectedItem().toString());
        actions.add(spinner14.getSelectedItem().toString());
        actions.add(spinner15.getSelectedItem().toString());

        ArrayList<String> alerts = new ArrayList<>();

        EditText editText1 = (EditText) findViewById(R.id.editTextAlert1);
        EditText editText2 = (EditText) findViewById(R.id.editTextAlert2);
        EditText editText3 = (EditText) findViewById(R.id.editTextAlert3);
        EditText editText4 = (EditText) findViewById(R.id.editTextAlert4);
        EditText editText5 = (EditText) findViewById(R.id.editTextAlert5);

        alerts.add(editText1.getText().toString());
        alerts.add(editText2.getText().toString());
        alerts.add(editText3.getText().toString());
        alerts.add(editText4.getText().toString());
        alerts.add(editText5.getText().toString());


        try {

            sharedPreferences.edit().putString("actions", ObjectSerializer.serialize(actions)).apply();
            sharedPreferences.edit().putString("alerts", ObjectSerializer.serialize(alerts)).apply();


        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    // reset preferences to default values
    public void preferencesReset (View view) {

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.emoassist", Context.MODE_PRIVATE);

        ArrayList<String> actions = new ArrayList<>();

        Spinner spinner11 = (Spinner) findViewById(R.id.spinner11);
        Spinner spinner12 = (Spinner) findViewById(R.id.spinner12);
        Spinner spinner13 = (Spinner) findViewById(R.id.spinner13);
        Spinner spinner14 = (Spinner) findViewById(R.id.spinner14);
        Spinner spinner15 = (Spinner) findViewById(R.id.spinner15);

        spinner11.setSelection(0);
        spinner12.setSelection(0);
        spinner13.setSelection(0);
        spinner14.setSelection(0);
        spinner15.setSelection(0);

        ArrayList<String> alerts = new ArrayList<>();

        EditText editText1 = (EditText) findViewById(R.id.editTextAlert1);
        EditText editText2 = (EditText) findViewById(R.id.editTextAlert2);
        EditText editText3 = (EditText) findViewById(R.id.editTextAlert3);
        EditText editText4 = (EditText) findViewById(R.id.editTextAlert4);
        EditText editText5 = (EditText) findViewById(R.id.editTextAlert5);

        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        editText4.setText("");
        editText5.setText("");


        Toast.makeText(getApplicationContext(), "Click on Save preferences to keep default values.",
                Toast.LENGTH_SHORT).show();

        try {

            sharedPreferences.edit().putString("actions", ObjectSerializer.serialize(actions)).apply();
            sharedPreferences.edit().putString("alerts", ObjectSerializer.serialize(alerts)).apply();


        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    //this when button click
    public void audioLookup(View view) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("audio/*");
        intent = Intent.createChooser(chooseFile, "Choose a audio file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_AUDIO);
    }
    //this when button click
    public void videoLookup(View view) {
        Intent getIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, ACTIVITY_CHOOSE_VIDEO);
    }
    //this when button click
    public void contactLookup(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(intent, ACTIVITY_CHOOSE_CONTACT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        if(requestCode == ACTIVITY_CHOOSE_AUDIO && data != null)
        {
            Uri uri = data.getData();
            EditText editText2 = (EditText) findViewById(R.id.editTextAlert2);
            editText2.setText(uri.toString());
        }
        else if(requestCode == ACTIVITY_CHOOSE_VIDEO && data != null)
        {
            Uri selectedImage = data.getData();

            EditText editText5 = (EditText) findViewById(R.id.editTextAlert5);
            editText5.setText(selectedImage.toString());
        }
        else if(requestCode == ACTIVITY_CHOOSE_CONTACT)
        {
            Uri contactUri = data.getData();
            // We only need the NUMBER column, because there will be only one row in the result
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

            // Perform the query on the contact to get the NUMBER column
            // We don't need a selection or sort order (there's only one result for the given URI)
            // CAUTION: The query() method should be called from a separate thread to avoid blocking
            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
            // Consider using CursorLoader to perform the query.
            Cursor cursor = getContentResolver()
                    .query(contactUri, projection, null, null, null);
            cursor.moveToFirst();

            // Retrieve the phone number from the NUMBER column
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(column);

            System.out.print("number  = " + number);
            EditText editText3 = (EditText) findViewById(R.id.editTextAlert3);
            editText3.setText(number);
        }

    }

}
