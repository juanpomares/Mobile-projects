package juanpomares.persistencia.mastermoviles.videoclub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Movie> lista;
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView lv=(ListView)findViewById(R.id.listView);

        lista=new ArrayList<Movie>();
        Movie cargando=new Movie("Cargando", "", "...", "", false, "");
        cargando.setBitmap(new SoftReference<Bitmap>(BitmapFactory.decodeResource(getResources(), R.drawable.cargando)));

        lista.add(cargando);

        movieAdapter=new MovieAdapter(this, lista);

        lv.setAdapter(movieAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent in=new Intent(MainActivity.this, MovieDetalleActivity.class);

                Movie actual=lista.get(position);

                in.putExtra("imagen", actual.getPoster());
                in.putExtra("titulo", actual.getTitle());
                in.putExtra("year", actual.getYear());
                in.putExtra("rented", actual.isRented()?"Alquilada":"Disponible");
                in.putExtra("sinopsis", actual.getSynopsis());

                startActivity(in);
            }
        });
        lv.setOnScrollListener(movieAdapter);

        //Cargar datos de la web

        new ObtenerListaPeliculas().execute("http://gbrain.dlsi.ua.es/videoclub/api/v1/catalog");
    }


    private class ObtenerListaPeliculas extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                URL url = new URL(urls[0]);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();

                if(http.getResponseCode()==200)
                {

                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader( http.getInputStream() ));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();

                }else
                    return "Error response: "+http.getResponseMessage();

            } catch (MalformedURLException e) {e.printStackTrace();
            } catch (IOException e) {e.printStackTrace(); }

            return urls[0];
        }

        @Override
        protected void onPreExecute() {}
        @Override
        protected void onCancelled() {}
        @Override
        protected void onPostExecute(String contenido)
        {

            JSONArray peliculasJSON = null;
            try
            {
                peliculasJSON = new JSONArray(contenido);

                lista.clear();
                for(int i=0;i<peliculasJSON.length();i++)
                {
                    JSONObject peliculajson = peliculasJSON.getJSONObject(i);
                    lista.add(new Movie(peliculajson.getString("title"), peliculajson.getString("year"), peliculajson.getString("director"), peliculajson.getString("poster"), peliculajson.getString("rented").equals("0")?false:true, peliculajson.getString("synopsis")));
                }
                movieAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
