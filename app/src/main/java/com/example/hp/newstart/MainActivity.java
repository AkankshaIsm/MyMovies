package com.example.hp.newstart;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.newstart.models.MovieModel;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        ListView listView=(ListView)findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new JSONTask((MainActivity.this)).execute("https://api.myjson.com/bins/340eu");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

 class JSONTask extends AsyncTask<String,String,List<MovieModel>>
{
    Context context;
    public JSONTask(Context context){
        this.context=context;
    }
    @Override
    protected List<MovieModel> doInBackground(String... params) {
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
            List<MovieModel.Cast> castList=new ArrayList<>();
            List<MovieModel> movieModelList=new ArrayList<>();
            for(int i=0;i<parentArray.length();i++)
            {
                MovieModel movieModel=new MovieModel();
                JSONObject finalObject=parentArray.getJSONObject(i);
                movieModel.setMovie(finalObject.getString("movie"));
                movieModel.setDirector(finalObject.getString("director"));
                movieModel.setYear(finalObject.getInt("year"));
                movieModel.setDuration(finalObject.getString("duration"));
                movieModel.setImage(finalObject.getString("image"));
                movieModel.setStory(finalObject.getString("story"));

                JSONArray castArray=finalObject.getJSONArray("cast");
                for(int j=0;j<castArray.length();j++)
                {  MovieModel.Cast cast=new MovieModel.Cast();
                 JSONObject castObject=castArray.getJSONObject(j);
                    cast.setName(castObject.getString("name"));
                    castList.add(cast);
                }
                movieModel.setCastList(castList);
                //Adding castlist to each individual movie.
                movieModelList.add(movieModel);
                //Adding the final models ie movies.
            }

        return movieModelList;
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
    protected void onPostExecute(List<MovieModel> movieModelList) {
        super.onPostExecute(movieModelList);

    }
}
