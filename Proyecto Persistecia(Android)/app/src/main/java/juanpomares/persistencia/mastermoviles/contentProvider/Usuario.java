package juanpomares.persistencia.mastermoviles.contentProvider;

/**
 * Created by Usuario on 14/12/2015.
 */
public class Usuario
{
    public String nombre, nombreCompleto, email, pass, id;

    Usuario()
    {
        nombre=""; nombreCompleto=""; email=""; pass=""; id="";
    }

    Usuario(String CompleteName, String Name, String Pass, String Mail, String ID)
    {
        nombre=Name; nombreCompleto=CompleteName; email=Mail; pass=Pass; id=ID;
    }
}
