package com.example.hp.newstart;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.button);
       // Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);

        textView = (TextView) findViewById(R.id.tv);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask((MainActivity.this)).execute("https://api.myjson.com/bins/2whku");
            }

        });
//        spinner1.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
//        List<String> categories = new ArrayList<String>();
//        categories.add("Celsius");
//        categories.add("Kelvin");
//        categories.add("Fahrenheit");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner1.setAdapter(dataAdapter);


    }

//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//           /* String s=e.getText().toString();
//            double temp=Double.parseDouble(s);
//            double ntemp;*/
//           String item=parent.getItemAtPosition(position).toString();
//            Toast.makeText(this,item,Toast.LENGTH_SHORT).show();
//           /* switch(position)
//            {
//                case 0:Toast.makeText(this,s+" Celsius",Toast.LENGTH_SHORT);break;
//                case 1:ntemp=temp+273;
//                       String ns=Double.toString(ntemp);
//                       Toast.makeText(this,ns+" Kelvin",Toast.LENGTH_SHORT);break;
//                case 2:ntemp=temp*1.8+32;
//                        ns=Double.toString(ntemp);
//                    Toast.makeText(this,ns+" Kelvin",Toast.LENGTH_SHORT);break;
//            }*/
//
//        }
//        public void onNothingSelected(AdapterView<?> arg0) {
//            // TODO Auto-generated method stub
//        }
//
//
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}

 class JSONTask extends AsyncTask<String,String,String>
{
    Context context;
    public JSONTask(Context context){
        this.context=context;
    }
    @Override
    protected String doInBackground(String... params) {
        BufferedReader reader=null;
        HttpURLConnection connection=null;
        try {
            URL url = new URL(params[0]);
            connection=(HttpURLConnection)url.openConnection();
            connection.connect();
            InputStream stream=connection.getInputStream();
            reader=new BufferedReader(new InputStreamReader(stream));
            String line ="";
            StringBuffer buffer=new StringBuffer();
            while((line = reader.readLine())!=null)
            {
                buffer.append(line);
            }
            String finalJSON=buffer.toString();

            JSONObject parentObject=new JSONObject(finalJSON);
            JSONArray parentArray = parentObject.getJSONArray("movies");
            StringBuffer finalBufferedData=new StringBuffer();
            for(int i=0;i<parentArray.length();i++)
            {
                JSONObject finalObject=parentArray.getJSONObject(i);
                String movieName=finalObject.getString("movie");
                int year=finalObject.getInt("year");
                finalBufferedData.append(movieName+"-"+year+"\n");
            }
            return finalBufferedData.toString();

        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(connection!=null)
                connection.disconnect();

            try {
                if(reader!=null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        TextView textView=(TextView)((MainActivity)context).findViewById(R.id.tv);
        textView.setText(result);
    }
}
