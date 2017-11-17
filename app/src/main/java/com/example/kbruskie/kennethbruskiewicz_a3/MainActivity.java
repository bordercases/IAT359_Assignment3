package com.example.kbruskie.kennethbruskiewicz_a3;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, Button.OnClickListener {

    TextView number0;
    TextView number1;
    TextView number2;
    TextView number3;
    TextView[] numberViews = new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkConnection();

        number0 = findViewById(R.id.number0);
        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        number3 = findViewById(R.id.number3);

        numberViews[0] = number0;
        numberViews[1] = number1;
        numberViews[2] = number2;
        numberViews[3] = number3;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            //the user has touched the View to drag it
            //prepare the drag
            ClipData data = ClipData.newPlainText("","");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            //start dragging the item touched
            view.startDrag(data, shadowBuilder, view, 0);
            Toast.makeText(this, "here0", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                //no action necessary
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                //no action necessary
                break;
            case DragEvent.ACTION_DROP:

                Toast.makeText(this,  "here1", Toast.LENGTH_SHORT).show();

                //handle the dragged view being dropped over a target view
                View view = (View) dragEvent.getLocalState();

                //stop displaying the view where it was before it was dragged
                view.setVisibility(View.INVISIBLE);

                //view dragged item is being dropped on
                TextView dropTarget = (TextView) v;

                //view being dragged and dropped
                TextView dropped = (TextView) view;

                //update the text in the target view to reflect the data being dropped
                dropTarget.setText(""+ dropTarget.getText()+" = " +dropped.getText());

                //make it bold to highlight the fact that an item has been dropped
                dropTarget.setTypeface(Typeface.DEFAULT_BOLD);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                //no action necessary
                break;
            default:
                break;
        }
        return true;
    }


    public void checkConnection(){
        ConnectivityManager connectMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            //fetch data

            String networkType = networkInfo.getTypeName().toString();
            Toast.makeText(this, "connected to " + networkType, Toast.LENGTH_LONG).show();
        }
        else {
            //display error
            Toast.makeText(this, "no network connection", Toast.LENGTH_LONG).show();
        }
    }


    private String readJSONData(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 2500;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("tag", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
                conn.disconnect();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class ReadNumbersJSONDataTask extends AsyncTask<String, Void, String> {

        Exception exception = null;

        protected String doInBackground(String... urls) {
            try{
                return readJSONData(urls[0]);
            }catch(IOException e){
                exception = e;
            }
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray numberData;
                numberData = jsonObject.getJSONArray("data");

                assert (numberData.length() == numberViews.length);
                for (int i = 0; i < numberData.length(); i++) {
                    Integer n = (Integer) numberData.get(i);
                    numberViews[i].setText(String.valueOf(n));
                }

            } catch (Exception e) {
                Log.d("ReadNumbersJSONTask", e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

}
