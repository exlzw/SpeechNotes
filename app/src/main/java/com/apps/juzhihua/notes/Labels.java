package com.apps.juzhihua.notes;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.juzhihua.notes.adapter.LabelViewAdapter;
import com.apps.juzhihua.notes.util.LabelDbHelper;

import java.util.ArrayList;
import java.util.List;

public class Labels extends AppCompatActivity {
    private LabelViewAdapter listViewAdapter;
    private List<labelClass> labelList;
    LabelDbHelper labelDBhelper;
    ListView listView;
    TextView display_msg;
    private Handler myHandler = new Handler();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels);

        listView = (ListView) findViewById(R.id.listview);
        labelDBhelper = new LabelDbHelper(this);
        display_msg = (TextView) findViewById(R.id.display_msg);
        sharedPreferences = getSharedPreferences("ControleAction", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setAdapters();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                TextView textView = (TextView) view.findViewById(R.id.textview);
                String text = textView.getText().toString();
                // 创建一个intent来启动另一个activity
                Intent intent = new Intent(Labels.this, LabelDetails.class);
                Bundle b = new Bundle();
                b.putString("title", text);
                intent.putExtras(b);
                startActivity(intent);
                //停止runnable
                //myHandler.removeCallbacks(UpdateListView);
            }
        });
    }

    private Runnable UpdateListView = new Runnable() {
        public void run() {
            try {
                String UserClickDelete = sharedPreferences.getString("UserClickDelete", "no");
                String UserClickUpdate = sharedPreferences.getString("UserClickUpdate", "no");
                // String UserClickAdd = sharedPreferences.getString("UserClickAdd", "no");
                if (UserClickDelete.equals("yes") || UserClickUpdate.equals("yes")) {
                    setAdapters();
                    editor.putString("UserClickDelete", "no");
                    editor.commit();
                    editor.putString("UserClickUpdate", "no");
                    editor.commit();
                }
                myHandler.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setAdapters() {
        getLabelsList();
        listViewAdapter = new LabelViewAdapter(this, R.layout.label_item, labelList);
        listView.setAdapter(listViewAdapter);
    }

    public List<labelClass> getLabelsList() {
        labelList = new ArrayList<>();
        Cursor data = labelDBhelper.getLabelContents();
        if (data.getCount() == 0) {
            //没有发现数据
            display_msg.setVisibility(View.VISIBLE);
        } else {
            while (data.moveToNext()) {
                labelList.add(new labelClass(data.getInt(0), data.getString(1)));
            }
        }
        return labelList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_label, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_label) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_label_popup);
            Button add_label_ok = (Button) dialog.findViewById(R.id.add_label_ok);
            final EditText new_label_text = (EditText) dialog.findViewById(R.id.new_label_text);
            add_label_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    labelDBhelper.insertLabel(new_label_text.getText().toString());
                    setAdapters();
                    Toast.makeText(Labels.this, new_label_text.getText().toString() + " 标签已创建 ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() { //开始Runnable
        super.onStart();
        try {
            myHandler.postDelayed(UpdateListView, 100);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() { //停止Runnable
        super.onPause();
        myHandler.removeCallbacks(UpdateListView);
    }
}
