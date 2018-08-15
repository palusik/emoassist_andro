package emoassist.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import emoassist.R;

public class HelpActivity extends AppCompatActivity {

    String baseUrl = "https://palusik.github.io/emoassist_andro";  // This is the API base URL (GitHub API)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            new HelpActivity.MyTask().execute(this);

        } catch (Exception e) {
            e.printStackTrace();
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


    private static class MyTask extends AsyncTask<Object, Void, String> {

        HelpActivity activity;

        @Override
        protected String doInBackground(Object... params) {
            activity = (HelpActivity) params[0];
            try {
                StringBuilder sb = new StringBuilder();
                URL url = new URL(activity.baseUrl);

                BufferedReader in;
                in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine);

                in.close();

                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            //Do something with result string
            WebView webView = activity.findViewById(R.id.web_view);
            webView.loadData(str, "text/html; charset=UTF-8", null);
        }

    }
}
