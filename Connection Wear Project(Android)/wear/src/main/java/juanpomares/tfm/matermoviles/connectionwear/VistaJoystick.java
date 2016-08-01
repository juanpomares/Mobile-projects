package juanpomares.tfm.matermoviles.connectionwear;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

interface JoystickListener
{
    public void onPositionChange(float newX, float newY);
};

public class VistaJoystick extends View
{
    private float RADIO = 25;
    private float MEDIO_RADIO = 25;


    private JoystickListener listener;
    private float DivisorRadio=4;

    private int width=0, height=0;
    private float widthmedio=0, heightmedio=0;

    private float x, y;
    public float nx, ny;//Normalizadas

    private boolean tocando=false;

    public VistaJoystick(Context context, JoystickListener _list) {
        super(context); listener=_list;
    }



    private boolean collidesCircle(float ex, float ey)
    {
        setNormalizedCoordinatesWithoutListener(ex, ey);

        if( ex >= widthmedio - MEDIO_RADIO
                && ex <= widthmedio + MEDIO_RADIO
                && ey >= heightmedio - MEDIO_RADIO
                && ey <= heightmedio + MEDIO_RADIO)
            tocando=true;


        if(!tocando)
        {
            setNormalizedCoordinates(widthmedio, heightmedio);
        }else
            listener.onPositionChange(nx, ny);

        return tocando;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        width=this.getWidth();
        height=this.getHeight();


        widthmedio=width/2.0f;
        heightmedio=height/2.0f;


        RADIO=width/DivisorRadio;
        MEDIO_RADIO=3*RADIO/4;
        x = widthmedio;
        y = heightmedio;
        nx=0; ny=0;
        tocando=false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        Paint paint2=new Paint();

        if(tocando)
            paint.setColor(Color.GRAY);
        else
            paint.setColor(Color.BLACK);

        paint2.setColor(Color.WHITE);
        paint2.setAlpha(128);


        paint2.setStyle(Style.FILL);
        paint.setStyle(Style.FILL);


        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(x, y, RADIO, paint);
        canvas.drawCircle(x, y, RADIO/1.5f, paint2);


        paint.setColor(Color.RED);
        canvas.drawLine(0, heightmedio, width, heightmedio, paint);
        canvas.drawLine(widthmedio, 0, widthmedio, height, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float ex = event.getX();
            float ey = event.getY();

            if(collidesCircle(ex, ey))
            {
                x=ex; y=ey;
                this.invalidate();
                return true;
            }else
                return false;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            x = event.getX();
            y = event.getY();

            setNormalizedCoordinates(x, y);
            this.invalidate();


            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP)
        {

            x=widthmedio; y=heightmedio;
            setNormalizedCoordinates(x, y);
            tocando=false;

            this.invalidate();
        }

        return true;
        // return detectorGestos.onTouchEvent(event);
    }

    private void setNormalizedCoordinates(float x, float y)
    {
        float antnx=nx, antny=ny;

        nx=(x-widthmedio)/(widthmedio);
        ny=-(y-heightmedio)/(heightmedio);

        if(nx>1) nx=1; else if(nx<-1) nx=-1;
        if(ny>1) ny=1; else if(ny<-1) ny=-1;

        if(!(nx==antnx && ny==antny))
            listener.onPositionChange(nx, ny);
    }

    private void setNormalizedCoordinatesWithoutListener(float x, float y)
    {
        float antnx=nx, antny=ny;

        nx=(x-widthmedio)/(widthmedio);
        ny=-(y-heightmedio)/(heightmedio);

        if(nx>1) nx=1; else if(nx<-1) nx=-1;
        if(ny>1) ny=1; else if(ny<-1) ny=-1;
    }
}
