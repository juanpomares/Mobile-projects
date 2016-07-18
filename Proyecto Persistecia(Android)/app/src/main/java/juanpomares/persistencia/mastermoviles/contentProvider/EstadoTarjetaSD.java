package juanpomares.persistencia.mastermoviles.contentProvider;

import android.os.Environment;

/**
 * Created by mastermoviles on 16/12/15.
 */
public class EstadoTarjetaSD
{
    private boolean mExternalStorageAvailable=false, mExternalStorageWriteable=false;

    public EstadoTarjetaSD()
    {
        mExternalStorageAvailable=false;
        mExternalStorageWriteable=false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            mExternalStorageAvailable = true;
            //mExternalStorageWriteable = false;
        } else
        {
            //mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public boolean getAvaibleStorage()
    {
        return mExternalStorageAvailable;
    }

    public boolean getWritableStorage()
    {
        return mExternalStorageWriteable;
    }

}
