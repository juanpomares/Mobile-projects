package juanpomares.persistencia.mastermoviles.videoclub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MovieAdapter extends BaseAdapter implements AbsListView.OnScrollListener
{
    private List<Movie> mList;
    private Context mContext;
    HashMap<Movie, MovieLoader> imagenesCargando;

    boolean mBusy=false;

    public MovieAdapter(Context context, List<Movie> objects)
    {
        mContext = context;
        mList = objects;
        imagenesCargando=new HashMap<Movie, MovieLoader>();
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.list_item, null);
        }

        TextView tvTitulo = (TextView) convertView.findViewById(R.id.textView);
        TextView tvDirector = (TextView) convertView.findViewById(R.id.textViewSubtitle);
        ImageView ivIcono = (ImageView) convertView.findViewById(R.id.imageView);

        Movie actual = mList.get(position);
        tvTitulo.setText(actual.getTitle());
        tvDirector.setText(actual.getDirector());


        if(actual.getBitmap()==null)
        {
            ivIcono.setImageResource(R.drawable.cargando); // Icono temporal
            if (imagenesCargando.get(actual) == null && !mBusy)
            {
                MovieLoader task = new MovieLoader();
                imagenesCargando.put(actual, task);
                task.execute(actual, ivIcono);
            }
        }else
        {
            ivIcono.setImageBitmap(actual.getBitmap().get());
        }
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch(scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;
                notifyDataSetChanged();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: mBusy = true; break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING: mBusy = true; break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

    }
}
