package juanpomares.tfm.matermoviles.connectionwear;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by juan_ on 14/07/2016.
 */
public class AppSharedPreferences
{
    private static final String mIdSP="AppSP";
    private static final String mIdVar="AppOpened";

    public static boolean getAppOpen(Context context)
    {
        SharedPreferences prefs =context.getSharedPreferences(mIdSP, Context.MODE_PRIVATE);
        return prefs.getBoolean(mIdVar, false);
    }

    public static void setAppOpen(Context context, boolean open)
    {
        SharedPreferences prefs =context.getSharedPreferences(mIdSP, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(mIdVar, open);
        editor.commit();
    }
}
