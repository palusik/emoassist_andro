package emoassist.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import emoassist.R;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
public class HistoryActivity extends AppCompatActivity {


    EditText editTextUser;     // UserId
    EditText editTextDate;
    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.

    private TableLayout mTableLayout;

    ProgressDialog mProgressBar;


    String baseUrl = "https://<change this to your domain of processing application>/processing/api/useralert/";  // This is the API base URL (GitHub API)
    String url;  // This will hold the full URL which will include the username entered in the etGitHubUser.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // assign currentPulse from main activity
        String userId = this.getIntent().getStringExtra("userId");

        this.editTextUser = (EditText) findViewById(R.id.editTextUser);  // Link user text box.
        this.editTextUser.setText(userId);
        this.editTextDate = (EditText) findViewById(R.id.editTextDate);  // Link date text box.

        requestQueue = Volley.newRequestQueue(this);  // This setups up a new request queue which we will need to make HTTP requests.

        mProgressBar = new ProgressDialog(this);
        mTableLayout = (TableLayout) findViewById(R.id.table_layout);
        mTableLayout.setStretchAllColumns(true);

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

    private void clearHistoryList() {
        mTableLayout.removeAllViews();
        // Create header

        TableRow tr = new TableRow(this);
        tr.setId(Integer.valueOf(0));
        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        //Note that you must use TableLayout.LayoutParams,
        //since the parent of this TableRow is a TableLayout
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);

        // created column
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(10);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setText("Created");
        tr.addView(textView);

        // type
        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(10);
        textView2.setTextColor(Color.parseColor(("#000000")));
        textView2.setText("E");
        tr.addView(textView2);

        // HR
        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(10);
        textView3.setTextColor(Color.parseColor(("#000000")));
        textView3.setText("HR");
        tr.addView(textView3);

        // probability
        TextView textView4 = new TextView(this);
        textView4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView4.setGravity(Gravity.CENTER);
        textView4.setTextSize(10);
        textView4.setTextColor(Color.parseColor(("#000000")));
        textView4.setText("Prob");
        tr.addView(textView4);

        // created HR/probability
        TextView textView5 = new TextView(this);
        textView5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView5.setGravity(Gravity.LEFT);
        textView5.setTextSize(10);
        textView5.setTextColor(Color.parseColor(("#000000")));
        textView5.setText("Alert");
        tr.addView(textView5);

        // created HR/probability
        TextView textView6 = new TextView(this);
        textView6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView6.setGravity(Gravity.LEFT);
        textView6.setTextSize(10);
        textView6.setTextColor(Color.parseColor(("#000000")));
        textView6.setText("TODO");
        tr.addView(textView6);

        // created HR/probability
        TextView textView7 = new TextView(this);
        textView7.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView7.setGravity(Gravity.CENTER);
        textView7.setTextSize(10);
        textView7.setTextColor(Color.parseColor(("#000000")));
        textView7.setText("Status");
        tr.addView(textView7);


        mTableLayout.addView(tr, params);

        mTableLayout.setStretchAllColumns(true);


        // mTableLayout.removeAllViews();
    }

    private void populateHistoryTable(String id, String created, String type, String probability, String hr, String alert_type, String target, String status) {
        // This will add a new netry to history list.
        // And then adds them followed by a new line (\n make two new lines).

        String fontColour;

        switch (type) {
            case "H":
                fontColour = "#02cc02";
                break;
            case "N":
                fontColour = "#ffc002";
                break;
            case "A":
                fontColour = "#ff0202";
                break;
            case "S":
                fontColour = "#02b1f0";
                break;
            case "F":
                fontColour = "#7d7d7d";
                break;
            default:
                fontColour = "#000000";
                break;
        }

        TableRow tr = new TableRow(this);
        tr.setId(Integer.valueOf(id));
        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        //Note that you must use TableLayout.LayoutParams,
        //since the parent of this TableRow is a TableLayout
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);

        // created column
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(10);
        textView.setTextColor(Color.parseColor(fontColour));
        textView.setText(created);
        tr.addView(textView);

        // type
        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(10);
        textView2.setTextColor(Color.parseColor(fontColour));
        textView2.setText(type);
        tr.addView(textView2);

        // HR
        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(10);
        textView3.setTextColor(Color.parseColor(fontColour));
        textView3.setText(hr);
        tr.addView(textView3);

        // probability
        TextView textView4 = new TextView(this);
        textView4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView4.setGravity(Gravity.CENTER);
        textView4.setTextSize(10);
        textView4.setTextColor(Color.parseColor(fontColour));
        textView4.setText(String.valueOf(Math.round(Float.valueOf(probability))));
        tr.addView(textView4);

        // created HR/probability
        TextView textView5 = new TextView(this);
        textView5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView5.setGravity(Gravity.LEFT);
        textView5.setTextSize(10);
        textView5.setTextColor(Color.parseColor(fontColour));
        textView5.setText(alert_type);
        tr.addView(textView5);

        // created HR/probability
        TextView textView6 = new TextView(this);
        textView6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView6.setGravity(Gravity.LEFT);
        textView6.setTextSize(10);
        textView6.setTextColor(Color.parseColor(fontColour));
        textView6.setText(target);
        tr.addView(textView6);

        // created HR/probability
        TextView textView7 = new TextView(this);
        textView7.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView7.setGravity(Gravity.LEFT);
        textView7.setTextSize(10);
        textView7.setTextColor(Color.parseColor(fontColour));
        textView7.setText(status);
        tr.addView(textView7);


        mTableLayout.addView(tr, params);

        mTableLayout.setStretchAllColumns(true);

    }




    private void setHistoryListText(String str) {
        // This is used for setting the text of our repo list box to a specific string.
        // We will use this to write a "No repos found" message if the user doens't have any.

        Toast.makeText(getApplicationContext(), str,
                Toast.LENGTH_SHORT).show();
    }

    private void getHistoryList(String username) {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).
        this.url = this.baseUrl + username;

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {
                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);

                                    String id = jsonObj.get("Id").toString();
                                    String created = jsonObj.get("created").toString();
                                    String type = jsonObj.get("type").toString();
                                    String probability = jsonObj.get("probability").toString();
                                    String hr = jsonObj.get("hr").toString();
                                    String alert_type = jsonObj.get("alert_type").toString();
                                    String target = jsonObj.get("target").toString();
                                    String status = jsonObj.get("status").toString();
                                    populateHistoryTable(id, created, type, probability, hr, alert_type, target, status);
                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            // The user didn't have any history.
                            setHistoryListText("No history found.");

                        }

                        mProgressBar.hide();


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        setHistoryListText("Error during the rest call");
                        Log.e("Volley", error.toString());
                        mProgressBar.hide();

                    }
                }


        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

    public void getHistory(View v) {
        // Clear the history list (so we have a fresh screen to add to)
        clearHistoryList();
        mProgressBar.setCancelable(false);
        mProgressBar.setMessage("Fetching Data..");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();

        // Call our getHistoryList() function that is defined above and pass in the
        // text which has been entered into the editTextUser text input field.
        getHistoryList(editTextUser.getText().toString());
    }
}
