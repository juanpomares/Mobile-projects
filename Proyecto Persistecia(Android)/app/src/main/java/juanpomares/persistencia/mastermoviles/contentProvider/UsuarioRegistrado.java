package juanpomares.persistencia.mastermoviles.contentProvider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UsuarioRegistrado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_registrado);


        Bundle extras=getIntent().getExtras();

        String nombre=extras.getString("Nombre");
        String nombrecompleto=extras.getString("NombreCompleto");
        String email=extras.getString("Email", null);

        ((TextView)findViewById(R.id.TVNombre)).setText(nombrecompleto);
        ((TextView)findViewById(R.id.TVnombreUsuario)).setText(nombre);
        if(email!=null)
        {
            ((TextView)findViewById(R.id.TVEmailUsuario)).setText(email);

        }else
        {
            ((TextView)findViewById(R.id.TVEmailUsuario)).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.TVLabelEmailUsuario)).setVisibility(View.INVISIBLE);
        }




    }

    public void Cerrar(View v)
    {
        this.finish();
    }
}
