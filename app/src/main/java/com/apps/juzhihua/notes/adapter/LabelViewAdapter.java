package com.apps.juzhihua.notes.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;
import com.apps.juzhihua.notes.R;
import com.apps.juzhihua.notes.labelClass;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LabelViewAdapter extends ArrayAdapter<labelClass> {

    private List<labelClass> dataSet;
    Context mContext;
    LabelDbHelper db;
    NoteDbHelper NoteDB;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static class ViewHolder {
        TextView title;
        ImageView delete,update;

    }
    public LabelViewAdapter(Context context, int resource, List<labelClass> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.dataSet = objects;
        db = new LabelDbHelper(mContext);
        NoteDB = new NoteDbHelper(mContext);
        sharedPreferences = mContext.getSharedPreferences("ControleAction", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final labelClass dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.label_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.textview);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete_icon);
            viewHolder.update = (ImageView) convertView.findViewById(R.id.update_icon);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.title.setText(dataModel.getTitle());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("删除确认")
                        .setMessage("确认删除这个清单 ?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                db.deleteData(dataModel.getID());
                                NoteDB.updateLabel_Name(dataModel.getTitle(),"no");
                                Toast.makeText(mContext, "标签已成功删除", Toast.LENGTH_SHORT).show();
                                editor.putString("UserClickDelete","yes");
                                editor.commit();
                            }
                        }).create().show();
            }
        });
        viewHolder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.update_label_popup);
                Button update_label_ok = (Button) dialog.findViewById(R.id.update_label_ok);
                final EditText update_text = (EditText) dialog.findViewById(R.id.update_text);
                update_text.setText(dataModel.getTitle());
                update_label_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.updateData(dataModel.getID(),update_text.getText().toString());
                        NoteDB.updateLabel_Name(dataModel.getTitle(),update_text.getText().toString());
                        Toast.makeText(mContext, "标签已成功删除", Toast.LENGTH_SHORT).show();
                        editor.putString("UserClickUpdate","yes");
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