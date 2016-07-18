package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class GestionUsuarios extends AppCompatActivity
{

    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        sp=(Spinner)findViewById(R.id.spinner);

        rellenar_spinner();
    }

    public void rellenar_spinner()
    {

        UsuariosSQLiteHelper helper=UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
        String[] usuarios=helper.ObtenerNombreUsuarios();
        helper.close();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, usuarios);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp.setAdapter(spinnerArrayAdapter);
    }

    public void Cerrar(View v)
    {
        this.finish();
    }

    public void NuevoUsuario(View v)
    {
        Intent i=new Intent(this, NuevoUsuario.class);
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        rellenar_spinner();
    }

    public void Eliminar(View v)
    {
        String str=(String) sp.getSelectedItem();
        if(str!=null)
        {
            UsuariosSQLiteHelper helper = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
            helper.EliminarUsuario(str);
            helper.close();

            rellenar_spinner();
        }
    }

    public void Actualizar(View v)
    {
        Intent i=new Intent(this, ActualizarUsuario.class);

        String str=(String) sp.getSelectedItem();
        if(str!=null)
        {
            i.putExtra("NombreUsuario", str);
            startActivityForResult(i, 1234);
        }
    }

}
