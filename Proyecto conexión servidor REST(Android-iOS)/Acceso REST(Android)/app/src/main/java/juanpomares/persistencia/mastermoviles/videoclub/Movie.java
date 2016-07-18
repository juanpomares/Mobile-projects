package juanpomares.persistencia.mastermoviles.videoclub;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;

public class Movie
{
    private String title;
    private String year;
    private String director;
    private String poster;
    private boolean rented;
    private String synopsis;
    private SoftReference<Bitmap> bitmap;

    public Movie( String _title, String _year, String _director, String _poster, boolean _rented, String _synopsis )
    {
        this.title = _title;
        this.year = _year;
        this.director = _director;
        this.poster = _poster;
        this.rented = _rented;
        this.synopsis = _synopsis;
        this.bitmap = null;
    }

    public Movie(String _poster)
    {
        this.title = "";
        this.year = "";
        this.director = "";
        this.poster = _poster;
        this.rented = false;
        this.synopsis = "";
        this.bitmap = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title)  {
        this.title = title;
    }

    public String getYear()  {
        return year;
    }

    public void setYear(String year)  {
        this.year = year;
    }

    public String getDirector()  {
        return director;
    }

    public void setDirector(String director)  {
        this.director = director;
    }

    public String getPoster()  {
        return poster;
    }

    public void setPoster(String poster)  {
        this.poster = poster;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public String getSynopsis()  {
        return synopsis;
    }

    public void setSynopsis(String synopsis)  {
        this.synopsis = synopsis;
    }

    public SoftReference<Bitmap> getBitmap() { return this.bitmap; }
    public void setBitmap( SoftReference<Bitmap>  _bitmap ) { this.bitmap = _bitmap; }


}
