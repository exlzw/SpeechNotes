package com.apps.juzhihua.notes.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;
import com.apps.juzhihua.notes.R;
import com.apps.juzhihua.notes.labelClass;

import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class AddListViewAdapter extends ArrayAdapter<labelClass> {
    private List<labelClass> dataSet;
    Context mContext;
    LabelDbHelper db;
    NoteDbHelper NoteDB;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static class ViewHolder {
        TextView title;
        ImageView delete, update;
        CheckBox checkbox;
    }
    public AddListViewAdapter(Context context, int resource, List<labelClass> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.dataSet = objects;
        //instantiate classes
        db = new LabelDbHelper(mContext);
        NoteDB = new NoteDbHelper(mContext);
        //used to check ifthe user click update or delete
        sharedPreferences = mContext.getSharedPreferences("ControleAction", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final labelClass dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.add_item_list, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.textview);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete_icon);
            viewHolder.update = (ImageView) convertView.findViewById(R.id.update_icon);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.CheckBox);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.title.setText(dataModel.getTitle()); //get item text and display it in the listview
        //call getCheckFromID function from LabelDbHelper class
        if (db.getCheckFromID(dataModel.getID()).equals("yes"))//user checked the checkbox
        {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike at the middle of item text
            viewHolder.checkbox.setChecked(true);
        } else //user unchecked the checkbox
        {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));//remove strike
            viewHolder.checkbox.setChecked(false);
        }

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//add strike at the middle of item text
                    db.StrikeThrough(viewHolder.title.getText().toString(), "yes");//update checked column value
                } else {
                    viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    db.StrikeThrough(viewHolder.title.getText().toString(), "no");//update checked column value
                }
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user click delete button
                new AlertDialog.Builder(mContext)
                        .setTitle("删除确认")
                        .setMessage("确认删除这个清单 ?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                db.deleteItem(dataModel.getID());
                                //Toast.makeText(mContext, "Item Deleted Succefully", Toast.LENGTH_SHORT).show();
                                editor.putString("UserClickDelete", "yes");
                                editor.commit();
                            }
                        }).create().show();
            }
        });
        viewHolder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user click update button
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.update_listitem_popup);
                Button update_label_ok = (Button) dialog.findViewById(R.id.update_label_ok);
                final EditText update_text = (EditText) dialog.findViewById(R.id.update_text);
                update_text.setText(dataModel.getTitle());
                update_label_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.updateItem(dataModel.getID(), update_text.getText().toString());
                        //Toast.makeText(mContext, "Item Updated Succefully", Toast.LENGTH_SHORT).show();
                        editor.putString("UserClickUpdate", "yes");
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        return convertView;
    }
}