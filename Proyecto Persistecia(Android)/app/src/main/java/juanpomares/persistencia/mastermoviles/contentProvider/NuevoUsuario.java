package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NuevoUsuario extends AppCompatActivity
{

    EditText nombre, nombreCompleto, pass, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        nombre=(EditText) findViewById(R.id.ETNombreUser);
        nombreCompleto=(EditText) findViewById(R.id.ETNombreCompleto);
        pass=(EditText) findViewById(R.id.ETPassword);

        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);

         int versionDataBaseStr=Integer.parseInt(sp.getString(getString(R.string.VERSIONBASEDATOS), getResources().getInteger(R.integer.VERSIONBASEDATOSDEFAULT) + ""));


        email=(EditText) findViewById(R.id.ETEmail);
        if(versionDataBaseStr==1)
        {
            email.setVisibility(View.INVISIBLE);
            findViewById(R.id.TVEmail).setVisibility(View.INVISIBLE);
            email=null;
        }

    }

    public void Cerrar(View v)
    {
        this.finish();
    }

    public void NuevoUsuario(View v)
    {
        String nuser=nombre.getText().toString();
        String ncomp=nombreCompleto.getText().toString();
        String passw=pass.getText().toString();
        String mail=null;

        if(email!=null)
            mail=email.getText().toString();


        if(nuser.length()>2 && ncomp.length()>2 && passw.length()>2 && ((mail!=null && mail.length()>2) || mail==null))
        {
            UsuariosSQLiteHelper helper=UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
            helper.NuevoUsuario(new Usuario(ncomp, nuser, passw, mail, ""));
            helper.close();

            nombre.setText(""); nombreCompleto.setText(""); pass.setText("");
            if(email!=null)email.setText("");

            Toast t= Toast.makeText(this, "Se ha añadido el usuario", Toast.LENGTH_LONG);
            t.show();
        }else
        {
            Toast t= Toast.makeText(this, "Faltan campos por rellenar", Toast.LENGTH_LONG);
            t.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(1234);
    }
}
