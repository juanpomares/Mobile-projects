package juanpomares.persistencia.mastermoviles.contentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;


import java.util.Vector;

/**
 * Created by Usuario on 14/12/2015.
 */
public class UsuariosSQLiteHelper extends SQLiteOpenHelper
{
    public static final String NombreTabla="Usuarios";
    public static final String CampoID="ID";
    public static final String CampoNombreUsuario="nombre_usuario";
    public static final String CampoPass="password";
    public static final String CampoNombreCompleto="nombre_completo";
    public static final String CampoMail="mail";
    public static final String CampoMailDefault="usuario@usuario.com";

    private static final String Delimitador="!#!#!";
    private static final String DelimitadorCambioUser="¡¡¡";

    public static int version_deseada;

    public Usuario ObtenerUser(String name)
    {
        SQLiteDatabase db=this.getReadableDatabase();

        String args[]={name};
        Usuario us=null;
        Cursor c=db.rawQuery("select * from " + NombreTabla + " where " + CampoNombreUsuario + "=?", args);

        if(c.moveToFirst())
        {
            us=new Usuario(c.getString(3), c.getString(1), c.getString(2), (c.getColumnCount()<5)?"":c.getString(4), c.getString(0));
        }

        c.close();
        db.close();
        return us;
    }

    public Usuario ObtenerUser(String name, String pass)
    {
        SQLiteDatabase db=this.getReadableDatabase();

        String args[]={name, pass};
        Usuario us=null;
        Cursor c=db.rawQuery("select * from " + NombreTabla + " where " + CampoNombreUsuario + "=? and " + CampoPass + "=?", args);

        if(c.moveToFirst())
        {
            us=new Usuario(c.getString(3), c.getString(1), c.getString(2), (c.getColumnCount()<5)?"":c.getString(4), c.getString(0));
        }

        c.close();
        db.close();
        return us;
    }

    public void NuevoUsuario(Usuario us)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String sql;
        if(db.getVersion()==2 && us.email!=null)
        {
            sql="INSERT INTO "+NombreTabla+" ("+CampoNombreUsuario+", "+CampoNombreCompleto+", "+CampoPass+", "+CampoMail+") VALUES "+
                    "('"+us.nombre+"', '"+us.nombreCompleto+"', '"+us.pass+"', '"+us.email+"')";
        }else
        {
            sql="INSERT INTO "+NombreTabla+" ("+CampoNombreUsuario+", "+CampoNombreCompleto+", "+CampoPass+") VALUES "+
                    "('"+us.nombre+"', '"+us.nombreCompleto+"', '"+us.pass+"')";
       }

       /* ContentValues valores = new ContentValues();

        valores.put(CampoNombreCompleto, us.nombreCompleto);
        valores.put(CampoNombreUsuario, us.nombre);
        valores.put(CampoPass, us.pass);
        if(us.email!=null)
            valores.put(CampoMail, us.email);

        db.insert(NombreTabla, null, valores);*/
        db.execSQL(sql);

        db.close();
    }

    public void ActualizarUsuario(String nombre_ant, Usuario us)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(CampoNombreCompleto, us.nombreCompleto);
        valores.put(CampoNombreUsuario, us.nombre);
        valores.put(CampoPass, us.pass);
        if(db.getVersion()==2 && us.email!=null)
            valores.put(CampoMail, us.email);

        db.update(NombreTabla, valores, CampoNombreUsuario + "='" + nombre_ant + "'", null);
        db.close();
    }

    public void EliminarUsuario(String nombre)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String args[]={nombre};
        db.delete(NombreTabla, CampoNombreUsuario + "='" + nombre + "'", null);
        //db.execSQL("delete from " + NombreTabla + " where " + CampoNombreUsuario + "=?", args);
        db.close();
    }

    public UsuariosSQLiteHelper(Context context, int versionDataBase, String nomBaseDatos)
    {
        super(context, nomBaseDatos, null, versionDataBase);
    }

    public static UsuariosSQLiteHelper CreateUsuarioSQLiteHelper(Context context)
    {
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);

        String nomBaseDatos=sp.getString(context.getString(R.string.NOMBREBASEDATOS), context.getString(R.string.NOMBREBASEDATOSDEFAULT));
        String versionDataBaseStr=sp.getString(context.getString(R.string.VERSIONBASEDATOS), context.getResources().getInteger(R.integer.VERSIONBASEDATOSDEFAULT)+"");
        int versionDataBase=Integer.parseInt(versionDataBaseStr);

        version_deseada=versionDataBase;

        return new UsuariosSQLiteHelper(context, versionDataBase, nomBaseDatos);
    }

    public String ObtenerUsuarios()
    {
        SQLiteDatabase db=this.getReadableDatabase();

        Usuario us=null;
        Cursor c=db.rawQuery("select * from " + NombreTabla, null);

        String texto_final="";

        boolean primerusuario=true;

        if(c.moveToFirst())
        {
            do
            {
                if(primerusuario)
                    primerusuario=false;
                else
                    texto_final+=DelimitadorCambioUser;

                texto_final+=c.getString(3);
                texto_final+=Delimitador;
                texto_final+=c.getString(1);
                texto_final+=Delimitador;
                texto_final+=c.getString(2);
                texto_final+=Delimitador;
                texto_final+=c.getString(0);

                if(!(c.getColumnCount()<5))
                {
                    texto_final += Delimitador;
                    texto_final += c.getString(4);
                }
            }while(c.moveToNext());

        }

        c.close();
        db.close();
        return texto_final;
    }

    public int ObtenerVersionBD()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        int v=db.getVersion();
        db.close();

        return v;
    }

    public void BorrarBaseDatos()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE from "+NombreTabla);
        db.close();
    }

    public void AddUsuarios(String x)
    {
        if(x.length()>0)
        {
            String[] usuarios=x.split(DelimitadorCambioUser);

            int length=usuarios.length;

            int versionBD=ObtenerVersionBD();

            for(int i=0; i<length; i++)
            {
                String[] partes=usuarios[i].split(Delimitador);
                if(partes.length>3)
                    NuevoUsuario(new Usuario(partes[0], partes[1], partes[2], partes.length==5?partes[4]:null, partes[3]));
            }
        }
    }

    public String[] ObtenerNombreUsuarios()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Vector<String> usuarios=new Vector<String>();

        Cursor c=db.rawQuery("select "+CampoNombreUsuario+" from "+NombreTabla, null);

        if(c.moveToFirst())
        {
            do
            {
                usuarios.add(c.getString(0));
            }while(c.moveToNext());
        }
        c.close();
        db.close();

        return usuarios.toArray(new String[usuarios.size()]);
    }

    private String CreateDatabase(String nombreDataBase)
    {
        return "CREATE TABLE " + nombreDataBase + " ("+CampoID+" integer primary key autoincrement, "
                +CampoNombreUsuario +" TEXT, "+CampoPass+" TEXT, "
                +CampoNombreCompleto+" TEXT);";
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.d("UsuariosSQLLiteHelper", "OnCreate");
        database.execSQL(CreateDatabase(NombreTabla));

        Log.d("UsuariosSQLLiteHelper", "Version: "+database.getVersion());
       /* database.execSQL("INSERT INTO "+NombreTabla+" ("+CampoNombreUsuario+", "+CampoNombreCompleto+", "+CampoPass+") VALUES "+
            "('juanpomares', 'Juan Pomares Bernabeu', '1234')");*/

        if(version_deseada==2)
            onUpgrade(database, 0, version_deseada);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d("UsuariosSQLIteHelper", "onUpgrade");
        if(newVersion==2)
            db.execSQL("ALTER TABLE "+NombreTabla+" ADD COLUMN "+CampoMail+" TEXT DEFAULT '"+CampoMailDefault+"'");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //super.onDowngrade(db, oldVersion, newVersion);
        if(newVersion==1)
        {

            Log.d("UsuariosSQLIteHelper", "onDowngrade");

            db.execSQL(CreateDatabase(NombreTabla + "Temp"));

            //Copiar datos a la temporal
            db.execSQL("INSERT INTO " + NombreTabla + "Temp (" + CampoID + ", " + CampoNombreUsuario + ", " + CampoNombreCompleto + ", " + CampoPass + ")" +
                    " SELECT " + CampoID + ", " + CampoNombreUsuario + ", " + CampoNombreCompleto + ", " + CampoPass +
                    " FROM " + NombreTabla);

            //Eliminar y volver a crear tabla original sin el campo email
            db.execSQL("DROP TABLE IF EXISTS " + NombreTabla);
            db.execSQL(CreateDatabase(NombreTabla));
            db.execSQL("INSERT INTO " + NombreTabla + " (" + CampoID + ", " + CampoNombreUsuario + ", " + CampoNombreCompleto + ", " + CampoPass + ")" +
                    " SELECT " + CampoID + ", " + CampoNombreUsuario + ", " + CampoNombreCompleto + ", " + CampoPass +
                    " FROM " + NombreTabla + "Temp");

            //Copiar datos de la temporal a la original

            //Eliminar Temporal
            db.execSQL("DROP TABLE IF EXISTS " + NombreTabla + "Temp");
        }
    }
}
