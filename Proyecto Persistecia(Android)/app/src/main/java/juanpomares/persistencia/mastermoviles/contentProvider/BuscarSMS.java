package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class BuscarSMS extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_sms);

        crearSMSValidacion();
    }


    public void crearSMSValidacion()
    {

    }

    public boolean LeerSMSValidacion(String tel) {


        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
        {
            Calendar cal=Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

            cursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, new String[]{Telephony.Sms.Inbox.BODY},
                    " "+Telephony.Sms.Inbox.ADDRESS+"='"+tel+"' and "+Telephony.Sms.Inbox.DATE_SENT+">="+cal.getTime().getTime(),
                    null, Telephony.Sms.Inbox.DATE_SENT+" desc");

            String delimitador="@@";
            String codigo_buscar="A3489HG";


            if (cursor.moveToFirst())
            { // must check the result to prevent exception
                do
                {
                    String contenido = cursor.getString(0);
                    String[] trozos = contenido.split(delimitador);

                    for(int i=0; i<trozos.length; i++)
                        if(trozos[i].equals(codigo_buscar))
                            return true;
                } while (cursor.moveToNext());
            } else {
                // empty box, no SMS
            }

        }
        return false;
    }


    public void Cerrar(View v)
    {
        this.finish();
    }

    public void BuscarSMS(View v)
    {
        if(LeerSMSValidacion(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.TELEFONODOSPASOS), getString(R.string.TELEFONODOSPASOSDEFAULT))))
        {
            Intent i = new Intent(this, UsuarioRegistrado.class);
            i.putExtra("Nombre", getIntent().getExtras().getString("Nombre"));
            i.putExtra("NombreCompleto", getIntent().getExtras().getString("NombreCompleto"));
            String mail = getIntent().getExtras().getString("Email", null);

            if (mail != null)
                i.putExtra("Email", mail);

            startActivity(i);
            this.finish();
        }else
        {
            Toast.makeText(BuscarSMS.this, "No se encuentra el SMS de validación", Toast.LENGTH_SHORT).show();
        }
    }
}
