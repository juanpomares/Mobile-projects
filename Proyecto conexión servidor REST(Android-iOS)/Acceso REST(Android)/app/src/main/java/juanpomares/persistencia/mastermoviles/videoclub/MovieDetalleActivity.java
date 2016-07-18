package juanpomares.persistencia.mastermoviles.videoclub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Usuario on 18/01/2016.
 */
public class MovieDetalleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelicula_detalle);

        String img = getIntent().getExtras().getString("imagen");
        String title = getIntent().getExtras().getString("titulo");
        String year = getIntent().getExtras().getString("year");
        String rented = getIntent().getExtras().getString("rented");
        String sinopsis = getIntent().getExtras().getString("sinopsis");

        setTitle(title);
        ((TextView) findViewById(R.id.titleMovie)).setText(title);
        ((TextView) findViewById(R.id.yearMovie)).setText(year);
        ((TextView) findViewById(R.id.rentedMovie)).setText(rented);
        ((TextView) findViewById(R.id.sinopsisMovie)).setText(sinopsis);

        new MovieLoader().execute(new Movie(img), (ImageView) findViewById(R.id.imagenPelicula));
    }
}
