package juanpomares.tfm.matermoviles.connectionwear;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by juan_ on 09/07/2016.
 */

enum ButtonName {
    Center, Up, Down, Right, Left, None
};

enum ViewType {VistaButtonHorizontal, VistaButtonVertical, VistaButtonPad};

interface ButtonListener
{
    public void onButtonPress(ButtonName pulsado);
    public void onButtonHold(ButtonName soltado);
};

public class VistaButton extends View
{
    protected ButtonListener listener;
    protected boolean centerEnabled=true;
    protected int width=0, height=0;
    protected float widthmedio=0, heightmedio=0;

    protected float MedioWidthBoton = 25, MedioHeightBoton = 25;
    protected Path BotonArriba, BotonAbajo, BotonDerecha, BotonIzquierda, Flechas;


    protected ViewType tipo;

    public ButtonName botonpulsado= ButtonName.None;
    public ButtonName getButtonPulsado(){return botonpulsado;}

    public VistaButton(Context context, ButtonListener _listener, ViewType _tipo, boolean _button) {
        super(context); listener=_listener; tipo=_tipo; centerEnabled=_button;
    }

    public VistaButton(Context context, ButtonListener _listener, ViewType _tipo)
    {
        super(context); listener=_listener; tipo=_tipo;
    }

    public void setCenterButtonEnabled(boolean x)
    {
        centerEnabled=x;
        this.invalidate();
    }

    protected boolean collidesCenterButton(float ex, float ey)
    {
        if(!centerEnabled) return false;

        if( ex >= widthmedio - MedioWidthBoton
                && ex <= widthmedio + MedioWidthBoton
                && ey >= heightmedio - MedioHeightBoton
                && ey <= heightmedio + MedioHeightBoton)
            return true;

        return false;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        width=this.getWidth();
        height=this.getHeight();


        widthmedio=width/2.0f;
        heightmedio=height/2.0f;


        inicializarPaths();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint pincel = new Paint();
        Paint pincelLineas = new Paint();

        int colorFondo= Color.LTGRAY;
        int colorLineas=Color.GRAY;
        int colorRellenoFlechas=Color.WHITE;
        int colorBotonCentralPulsado=Color.GRAY;
        int colorBotonCentralSinPulsar=Color.DKGRAY;

        canvas.drawColor(colorFondo);

        pincelLineas.setColor(colorLineas);
        pincelLineas.setStyle(Paint.Style.STROKE);
        pincelLineas.setStrokeWidth(10);

        canvas.drawColor(colorFondo);

        pincel.setColor(colorLineas);
        pincel.setStyle(Paint.Style.FILL);

        if(tipo!= ViewType.VistaButtonHorizontal)
        {
            if(botonpulsado== ButtonName.Right)
                canvas.drawPath(BotonDerecha, pincel);
            if(botonpulsado== ButtonName.Left)
                canvas.drawPath(BotonIzquierda, pincel);
        }

        if(tipo!= ViewType.VistaButtonVertical)
        {
            if(botonpulsado== ButtonName.Up)
                canvas.drawPath(BotonArriba, pincel);
            if(botonpulsado== ButtonName.Down)
                canvas.drawPath(BotonAbajo, pincel);
        }

        pincel.setColor(colorRellenoFlechas);
        canvas.drawPath(Flechas, pincel);
        canvas.drawPath(Flechas, pincelLineas);

        if (tipo== ViewType.VistaButtonHorizontal)
        {
            canvas.drawLine(0, heightmedio, width, heightmedio, pincelLineas);
        }else  if (tipo== ViewType.VistaButtonVertical)
        {
            canvas.drawLine(widthmedio, 0, widthmedio, height, pincelLineas);
        }else if (tipo== ViewType.VistaButtonPad)
        {
            canvas.drawLine(0, 0, width, height, pincelLineas);
            canvas.drawLine(width, 0, 0, height, pincelLineas);
        }

        if(centerEnabled)
        {
            pincel.setStyle(Paint.Style.FILL);
            pincel.setColor(botonpulsado == ButtonName.Center ? colorBotonCentralPulsado:colorBotonCentralSinPulsar);
            canvas.drawRoundRect(widthmedio- MedioWidthBoton, heightmedio- MedioHeightBoton, widthmedio+ MedioWidthBoton, heightmedio+ MedioHeightBoton, 20, 20, pincel);
            canvas.drawRoundRect(widthmedio- MedioWidthBoton, heightmedio- MedioHeightBoton, widthmedio+ MedioWidthBoton, heightmedio+ MedioHeightBoton, 20, 20, pincelLineas);
        }
    }

    private void inicializarPaths()
    {
        Flechas=new Path();
        if (tipo== ViewType.VistaButtonHorizontal)
        {
            MedioWidthBoton =width*0.4f;
            MedioHeightBoton =height/6;

            BotonArriba=getPathFromPoints(new float[]{0, 0, width, 0, width, heightmedio, 0, heightmedio});
            BotonAbajo=getPathFromPoints(new float[]{0, heightmedio, width, heightmedio, width, height, 0, height});

            Flechas.moveTo(widthmedio, height / 10);
            Flechas.lineTo(3 * width / 10, height / 5);
            Flechas.lineTo(7 * width / 10, height / 5);
            Flechas.lineTo(widthmedio, height / 10);

            Flechas.moveTo(widthmedio, 9 * height / 10);
            Flechas.lineTo(3 * width / 10, 4 * height / 5);
            Flechas.lineTo(7 * width / 10, 4 * height / 5);
            Flechas.lineTo(widthmedio, 9 * height / 10);
        }else if (tipo== ViewType.VistaButtonVertical)
        {
            MedioWidthBoton =width/6.f;
            MedioHeightBoton =height*0.4f;

            BotonDerecha=getPathFromPoints(new float[]{width, 0, width, height, widthmedio, height, widthmedio, 0});
            BotonIzquierda=getPathFromPoints(new float[]{0, 0, 0, height, widthmedio, height, widthmedio, 0});

            Flechas.moveTo(width/10, heightmedio);
            Flechas.lineTo(width/5, 3*height/10);
            Flechas.lineTo(width/5, 7*height/10);
            Flechas.lineTo(width/10, heightmedio);

            Flechas.moveTo(9*width/10, heightmedio);
            Flechas.lineTo(4*width/5, 3*height/10);
            Flechas.lineTo(4*width/5, 7*height/10);
            Flechas.lineTo(9*width/10, heightmedio);
        }else if (tipo== ViewType.VistaButtonPad)
        {
            MedioWidthBoton =width/6.f;
            MedioHeightBoton =height/6.f;

            BotonArriba=getPathFromPoints(new float[]{0, 0, width, 0, widthmedio, heightmedio});
            BotonAbajo=getPathFromPoints(new float[]{width, height, 0, height, widthmedio, heightmedio});
            BotonDerecha=getPathFromPoints(new float[]{width, 0, width, height, widthmedio, heightmedio});
            BotonIzquierda=getPathFromPoints(new float[]{0, 0, 0, height, widthmedio, heightmedio});

            Flechas.moveTo(widthmedio, height/10);
            Flechas.lineTo(4*width/10, height/5);
            Flechas.lineTo(4*width/10, height/5);
            Flechas.lineTo(6*width/10, height/5);
            Flechas.lineTo(widthmedio, height/10);

            Flechas.moveTo(widthmedio, 9*height/10);
            Flechas.lineTo(4*width/10, 4*height/5);
            Flechas.lineTo(6*width/10, 4*height/5);
            Flechas.lineTo(widthmedio, 9*height/10);

            Flechas.moveTo(width/10, heightmedio);
            Flechas.lineTo(width/5, 4*height/10);
            Flechas.lineTo(width/5, 6*height/10);
            Flechas.lineTo(width/10, heightmedio);

            Flechas.moveTo(9*width/10, heightmedio);
            Flechas.lineTo(4*width/5, 4*height/10);
            Flechas.lineTo(4*width/5, 6*height/10);
            Flechas.lineTo(9*width/10, heightmedio);
        }
    }

    private Path getPathFromPoints(float[] points)
    {
        Path wallpath = new Path();
        wallpath.reset(); // only needed when reusing this path for a new build

        int tam=points.length/2;
        wallpath.moveTo(points[0], points[1]);

        for(int i=1; i<tam; i++)
            wallpath.lineTo(points[i*2], points[i*2+1]);

        wallpath.close();
        return wallpath;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
        {
            setButtonPulsado(CalculateBotonpulsado(event.getX(), event.getY()));
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            setButtonPulsado(ButtonName.None);
        }

        return true;
        // return detectorGestos.onTouchEvent(event);
    }

    private void setButtonPulsado(ButtonName _button)
    {
        if(_button!=botonpulsado)
        {
            if(botonpulsado!= ButtonName.None)
                listener.onButtonHold(botonpulsado);

            botonpulsado = _button;

            if(botonpulsado!= ButtonName.None)
                listener.onButtonPress(botonpulsado);

            this.invalidate();
        }
    }

    protected ButtonName CalculateBotonpulsado(float x, float y)
    {
        if(collidesCenterButton(x,y))
            return ButtonName.Center;

        float nx=(x-widthmedio)/(widthmedio);
        float ny=-(y-heightmedio)/(heightmedio);

        if(nx>1) nx=1; else if(nx<-1) nx=-1;
        if(ny>1) ny=1; else if(ny<-1) ny=-1;

        if(tipo== ViewType.VistaButtonVertical)
        {
            if(nx>0)   return ButtonName.Right;
            else        return ButtonName.Left;
        }else if(tipo== ViewType.VistaButtonHorizontal)
        {
            if(ny>0)    return ButtonName.Up;
            else        return ButtonName.Down;
        }else if(tipo== ViewType.VistaButtonPad)
        {
            if(Math.abs(nx)>Math.abs(ny))
            {
                if(nx>0)    return ButtonName.Right;
                else        return ButtonName.Left;
            }else
            {
                if(ny>0)    return ButtonName.Up;
                else        return ButtonName.Down;
            }
        }


        return ButtonName.None;
    }
}