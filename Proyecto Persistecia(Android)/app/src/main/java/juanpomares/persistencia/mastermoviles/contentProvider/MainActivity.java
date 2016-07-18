package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity
{
    EditText usuario, pass;

    boolean datosSD;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario=(EditText) findViewById(R.id.CampoUsuario);
        pass=(EditText) findViewById(R.id.CampoPass);

        datosSD=PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.LOCALIZACIONFICHEROS), getResources().getBoolean(R.bool.LOCALIZACIONFICHEROSDEFAULT));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void Cerrar(View v)
    {
        this.finish();
    }

    public void Acceder(View v)
    {
        String nombre=usuario.getText().toString();
        String passw=pass.getText().toString();

        if(nombre.length()>0 && passw.length()>0)
        {
            UsuariosSQLiteHelper helper=UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
            Usuario us = helper.ObtenerUser(nombre, passw);
            helper.close();


            if(us!=null)
            {
                Intent i = new Intent(this, BuscarSMS.class);
                i.putExtra("Nombre", us.nombre);
                i.putExtra("NombreCompleto", us.nombreCompleto);
                if (us.email!="")
                    i.putExtra("Email", us.email);
                startActivity(i);
            }else
            {
                Toast t=Toast.makeText(this, "Usuario/Contraseña incorrecta", Toast.LENGTH_LONG);
                t.show();
            }
        }else
        {
            Toast t=Toast.makeText(this, "Faltan campos", Toast.LENGTH_LONG);
            t.show();
        }
    }

    private UsuariosSQLiteHelper ObtenerHelper()
    {
        return UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
    }


    private String LeerFicheroBackup()
    {
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        boolean sd=sp.getBoolean(getString(R.string.LOCALIZACIONFICHEROS), getResources().getBoolean(R.bool.LOCALIZACIONFICHEROSDEFAULT));
        return LeerFicheroBackup(sd);
    }


    private String LeerFicheroBackup(boolean sd)
    {

        String contenido="";
        try
        {
            FileInputStream fins=null;

            SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
            String name=sp.getString(getString(R.string.NOMBREBASEDATOS), getString(R.string.NOMBREBASEDATOSDEFAULT))+".txt";

            if(sd)
            {
                EstadoTarjetaSD estado=new EstadoTarjetaSD();
                if(estado.getAvaibleStorage())
                {
                    File ruta_sd = Environment.getExternalStorageDirectory();
                    File f = new File(ruta_sd.getAbsolutePath(), name);

                    fins = new FileInputStream(f);
                }
            }else
            {
                fins=openFileInput(name);
            }

            if(fins!=null)
            {
                BufferedReader fin = new BufferedReader(new InputStreamReader(fins));

                String texto;
                while ((texto = fin.readLine()) != null) {
                    contenido += texto;
                }
                fin.close();
                fins.close();
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }

        return contenido;
    }

    private void EscribirFicheroBackup(String texto)
    {
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        boolean sd=sp.getBoolean(getString(R.string.LOCALIZACIONFICHEROS), getResources().getBoolean(R.bool.LOCALIZACIONFICHEROSDEFAULT));
        EscribirFicheroBackup(texto, sd);
    }

    private void EscribirFicheroBackup(String texto, boolean sd)
    {
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        String name=sp.getString(getString(R.string.NOMBREBASEDATOS), getString(R.string.NOMBREBASEDATOSDEFAULT))+".txt";

        try
        {
            FileOutputStream fouts=null;
            EstadoTarjetaSD estado=new EstadoTarjetaSD();

            if(sd)
            {
                if(estado.getWritableStorage())
                {
                    File ruta_sd = Environment.getExternalStorageDirectory();
                    File f = new File(ruta_sd.getAbsolutePath(), name);
                    fouts = new FileOutputStream(f, true);
                }else
                {
                    Toast.makeText(this, "No se puede escribir en el almacenamiento externo", Toast.LENGTH_SHORT).show();
                }
            }else
            {
                fouts=openFileOutput(name, Context.MODE_PRIVATE);
            }

            if(fouts!=null)
            {
                OutputStreamWriter fout = new OutputStreamWriter(fouts);

                fout.append(texto + "\n");
                fout.close();
                fouts.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_CrearBackup:
                UsuariosSQLiteHelper helper=ObtenerHelper();
                String usuarios=helper.ObtenerUsuarios();
                helper.close();
                EscribirFicheroBackup(usuarios);
                return true;

            case R.id.action_RestaurarBackup:
                String baseactual=LeerFicheroBackup();

                UsuariosSQLiteHelper helper2=ObtenerHelper();
                if(baseactual.length()>0)
                {
                    helper2.BorrarBaseDatos();
                    helper2.AddUsuarios(baseactual);
                }else
                {
                    Toast.makeText(this, "No se ha podido conseguir el fichero de backup", Toast.LENGTH_SHORT).show();
                }
                helper2.close();
                return true;

            case R.id.action_GestionUsuarios:
                Intent i=new Intent(this, GestionUsuarios.class);
                startActivity(i);
                return true;

            case R.id.action_Configuracion:
                Intent i2=new Intent(this, PreferencesActivity.class);
                startActivityForResult(i2, 1234);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public void EliminarFichero(boolean sd)
    {
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        String name=sp.getString(getString(R.string.NOMBREBASEDATOS), getString(R.string.NOMBREBASEDATOSDEFAULT))+".txt";

        if(sd)
        {
            File ruta_sd = Environment.getExternalStorageDirectory();
            File f = new File(ruta_sd.getAbsolutePath(), name);
            if(f.exists())
                f.delete();
        }else
        {
            deleteFile(name);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        boolean datosNuevos=sp.getBoolean(getString(R.string.LOCALIZACIONFICHEROS), getResources().getBoolean(R.bool.LOCALIZACIONFICHEROSDEFAULT));

        if(datosNuevos!=datosSD)
        {
            //Toast.makeText(this, "Toca hacer el cambio del fichero jeje", Toast.LENGTH_SHORT).show();

            String baseactual=LeerFicheroBackup(datosSD);
            if(baseactual!="")
            {
                EliminarFichero(datosSD);
                EscribirFicheroBackup(baseactual, datosNuevos);
            }
            datosSD=datosNuevos;
        }
    }
}
