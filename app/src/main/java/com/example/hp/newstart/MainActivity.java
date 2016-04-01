package com.example.hp.newstart;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.newstart.models.MovieModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //starts the json task.
        new JSONTask().execute("https://api.myjson.com/bins/3cxsk");  //my json file is stored at this address.
        listView = (ListView) findViewById(R.id.listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
    }


    class JSONTask extends AsyncTask<String,String,List<MovieModel>>
    {
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
                //create a list of Movie Models
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
                    List<MovieModel.Cast> castList=new ArrayList<>();
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
        protected void onPostExecute(List<MovieModel> result) {
            super.onPostExecute(result);

            if(result != null) {
                MovieAdapter adapter = new MovieAdapter(MainActivity.this, R.layout.row, result);
                listView.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data, please check your connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

public class MovieAdapter extends ArrayAdapter {
    private List<MovieModel> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView=inflater.inflate(resource,null);
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.image);
        TextView movie;
        movie=(TextView)convertView.findViewById(R.id.movie);
        TextView director;
        director=(TextView)convertView.findViewById(R.id.director);
        TextView year;
        year=(TextView)convertView.findViewById(R.id.year);
        TextView duration;
        duration=(TextView)convertView.findViewById(R.id.duration);
        TextView cast;
        cast=(TextView)convertView.findViewById(R.id.cast);
        TextView story;
        story=(TextView)convertView.findViewById(R.id.story);
        RatingBar ratingBar;
        ratingBar=(RatingBar)convertView.findViewById(R.id.ratingBar);
        ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), imageView); //first parameter is image url

            movie.setText(movieModelList.get(position).getMovie());

            director.setText(movieModelList.get(position).getDirector());

            duration.setText(movieModelList.get(position).getDuration());

            year.setText("year  :" + movieModelList.get(position).getYear());

            story.setText(movieModelList.get(position).getStory());

            ratingBar.setRating((int)movieModelList.get(position).getRating()/2);

        StringBuffer stringBuffer=new StringBuffer();
        for(MovieModel.Cast castl:movieModelList.get(position).getCastList())
        {
            stringBuffer.append(castl.getName()+" ,");
        }

            cast.setText(stringBuffer);

        return convertView;
    }
}



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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


