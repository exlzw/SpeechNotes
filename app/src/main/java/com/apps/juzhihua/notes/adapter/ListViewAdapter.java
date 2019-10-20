package com.apps.juzhihua.notes.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;
import com.apps.juzhihua.notes.R;
import com.apps.juzhihua.notes.labelClass;
import com.apps.juzhihua.notes.notes;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<notes> {
    private List<notes> dataSet;
    Context mContext;
    LabelDbHelper db;
    NoteDbHelper NoteDB;
    private List<labelClass> ListOfItems;
    private static class ViewHolder {
        TextView title;
        TextView body;
        TextView label_text_sub;
        TextView notify_text_sub;
        LinearLayout LL1;
        LinearLayout LL2;
        LinearLayout LL3;
        LinearLayout LL4;
        ImageView star;
        ImageView img;
    }

    public ListViewAdapter(Context context, int resource, List<notes> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.dataSet = objects;
        db = new LabelDbHelper(mContext);
        NoteDB = new NoteDbHelper(mContext);
        ListOfItems = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        notes dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.body = (TextView) convertView.findViewById(R.id.txtDescription);
            viewHolder.LL1 = (LinearLayout) convertView.findViewById(R.id.LL1);
            viewHolder.LL2 = (LinearLayout) convertView.findViewById(R.id.LL2);
            viewHolder.LL3 = (LinearLayout) convertView.findViewById(R.id.LL3);
            viewHolder.LL4 = (LinearLayout) convertView.findViewById(R.id.LL4);
            viewHolder.star = (ImageView) convertView.findViewById(R.id.star);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            viewHolder.label_text_sub = (TextView) convertView.findViewById(R.id.label_text_sub) ;
            viewHolder.notify_text_sub = (TextView) convertView.findViewById(R.id.notify_text_sub) ;
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
            viewHolder.body.setText("");
        }


        viewHolder.title.setText(dataModel.getTitle());
        viewHolder.title.setTextColor(Color.parseColor(dataModel.getColor()));
        convertView.setBackgroundColor(Color.parseColor(dataModel.getBackground()));

        if(dataModel.getType().equals("list"))
        {
            //get elements from list and display them here
            Cursor data = db.getItemsContents(dataModel.getId());
            if(data.getCount() == 0){
                //no data founds
            }else{
                while(data.moveToNext()){
                    ListOfItems.add(new labelClass(data.getInt(0),data.getString(1)));
                    if(viewHolder.body.getText().toString().contains(data.getString(1)))
                    {
                        //do not do anything
                    }
                    else {
                        viewHolder.body.append("\u2022 " + data.getString(1));
                        viewHolder.body.append("\n");
                    }
                }
            }
        }
        else
        {
            viewHolder.body.setText(dataModel.getbody());
            viewHolder.body.setTextColor(Color.parseColor(dataModel.getColor()));
        }


        if(dataModel.getFavoris().equals("no"))
        {
            viewHolder.star.setBackgroundResource(R.drawable.ic_star_grey);
        }
        else
        {
            viewHolder.star.setBackgroundResource(R.drawable.ic_star_yellow);
        }

        if(dataModel.getLabel().equals("no"))
        {
            viewHolder.LL1.setVisibility(View.INVISIBLE);
            viewHolder.LL3.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.LL1.setVisibility(View.VISIBLE);
            viewHolder.LL3.setVisibility(View.INVISIBLE);
            viewHolder.label_text_sub.setText(dataModel.getLabel());
        }
        if(dataModel.getNotification().equals("no"))
        {
            viewHolder.LL2.setVisibility(View.INVISIBLE);
            viewHolder.LL4.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.LL2.setVisibility(View.VISIBLE);
            viewHolder.LL4.setVisibility(View.INVISIBLE);
            viewHolder.notify_text_sub.setText(dataModel.getNotification());
        }


        if(dataModel.getType().equals("voice"))
        {
            viewHolder.img.setBackgroundResource(R.drawable.ic_mic);
        }
        else
        {
            //nothing
        }

        if(dataModel.getType().equals("list"))
        {
            viewHolder.img.setBackgroundResource(R.drawable.ic_list);
        }
        else
        {
            //nothing
        }

        return convertView;
    }
}
