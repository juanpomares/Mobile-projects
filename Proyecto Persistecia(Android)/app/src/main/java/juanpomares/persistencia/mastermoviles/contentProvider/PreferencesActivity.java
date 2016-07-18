package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Usuario on 14/12/2015.
 */
public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy()
    {
        setResult(1234);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String key)
    {
        if(key.equals(getString(R.string.NOMBREBASEDATOS)))
        {
            String value = sp.getString(key, "");
            if(value.length()<3)
            {
                String def=getString(R.string.NOMBREBASEDATOSDEFAULT);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, def);
                editor.apply();

                EditTextPreference editt1 = (EditTextPreference) findPreference(key);
                editt1.setText(def);
            }
        }else if(key.equals(getString(R.string.VERSIONBASEDATOS)))
        {
            int def=getResources().getInteger(R.integer.VERSIONBASEDATOSDEFAULT);
            String value = sp.getString(key, def+"");
            int VersionBDAnt = Integer.parseInt(value);
            int VersionBD = VersionBDAnt;
            if (VersionBD < 1) VersionBD = 1;
            else if (VersionBD > 2) VersionBD = 2;

            if (VersionBD != VersionBDAnt)
            {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, ""+VersionBD);
                editor.apply();

                EditTextPreference editt1 = (EditTextPreference) findPreference(key);
                editt1.setText(VersionBD + "");
                //Reiniciar();
            }
        }else if(key.equals(getString(R.string.TELEFONODOSPASOS)))
        {
            String value = sp.getString(key, "");
            if(value.length()<3)
            {
                String def=getString(R.string.TELEFONODOSPASOSDEFAULT);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, def);
                editor.apply();

                EditTextPreference editt1 = (EditTextPreference) findPreference(key);
                editt1.setText(def);
            }
        }else if(key.equals(getString(R.string.LOCALIZACIONFICHEROS)))
        {
            boolean value = sp.getBoolean(getString(R.string.LOCALIZACIONFICHEROS), getResources().getBoolean(R.bool.LOCALIZACIONFICHEROSDEFAULT));


            if(value)
            {
                EstadoTarjetaSD estado=new EstadoTarjetaSD();

                if(!estado.getWritableStorage())
                {
                    Toast.makeText(this, "La memoria extraíble no se encuentra disponible W:"+estado.getWritableStorage()+" R:"+estado.getAvaibleStorage(), Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(key, false);
                    editor.apply();

                    CheckBoxPreference cbox = (CheckBoxPreference) findPreference(key);
                    cbox.setChecked(false);
                }
            }
        }
    }

}

