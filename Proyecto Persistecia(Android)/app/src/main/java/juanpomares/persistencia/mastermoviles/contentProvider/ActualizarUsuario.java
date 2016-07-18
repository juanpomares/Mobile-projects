package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActualizarUsuario extends AppCompatActivity {



    EditText nombre, nombreCompleto, pass, email;

    String nombreAnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_usuario);

        UsuariosSQLiteHelper helper=UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);

        nombreAnt=getIntent().getExtras().getString("NombreUsuario", "juanpomares");

        Usuario usu=helper.ObtenerUser(nombreAnt);
        helper.close();


        nombre=(EditText) findViewById(R.id.ETNombreUser);
        nombre.setText(usu.nombre);

        nombreCompleto=(EditText) findViewById(R.id.ETNombreCompleto);
        nombreCompleto.setText(usu.nombreCompleto);

        pass=(EditText) findViewById(R.id.ETPassword);
        pass.setText(usu.pass);

        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);

        int versionDataBaseStr=Integer.parseInt(sp.getString(getString(R.string.VERSIONBASEDATOS), getResources().getInteger(R.integer.VERSIONBASEDATOSDEFAULT) + ""));

        email=(EditText) findViewById(R.id.ETEmail);
        email.setText(usu.email);
        if(versionDataBaseStr==1)
        {
            email.setVisibility(View.INVISIBLE);
            findViewById(R.id.TVEmail).setVisibility(View.INVISIBLE);
            email = null;
        }
    }

    public void Cerrar(View v)
    {
        this.finish();
    }

    public void ActualizarUsuario(View v)
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
            helper.ActualizarUsuario(nombreAnt, new Usuario(ncomp, nuser, passw, mail, ""));
            helper.close();

            nombreAnt=nuser;

            Toast t= Toast.makeText(this, "Se ha actualizado correctamente el usuario", Toast.LENGTH_LONG);
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
