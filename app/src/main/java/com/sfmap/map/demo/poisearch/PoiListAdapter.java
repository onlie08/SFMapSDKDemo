package com.sfmap.map.demo.poisearch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sfmap.map.demo.R;
import com.sfmap.api.services.poisearch.PoiItem;

import java.util.List;


public class PoiListAdapter extends BaseAdapter{
    private Context ctx;
    private List<PoiItem> list;

    public PoiListAdapter(Context context, List<PoiItem> poiList) {
        this.ctx = context;
        this.list = poiList;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(ctx, R.layout.item_listview, null);
            holder.poititle = (TextView) convertView
                   .findViewById(R.id.poititle);
            holder.subpois = (GridView) convertView.findViewById(R.id.listview_item_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PoiItem item = list.get(position);
        holder.poititle.setText(item.getTitle());

        return convertView;
    }
    private class ViewHolder {
        TextView poititle;
        GridView subpois;
    }

}
