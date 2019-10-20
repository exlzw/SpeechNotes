package com.apps.juzhihua.notes;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.apps.juzhihua.notes.adapter.GridViewAdapter;
import com.apps.juzhihua.notes.adapter.ListViewAdapter;
import com.apps.juzhihua.notes.util.NoteDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 提醒事项中的
 */

public class LabelDetails extends AppCompatActivity {
    TextView display_msg;
    NoteDbHelper noteDBhelper;
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private ListViewAdapter listViewAdapter;
    private GridViewAdapter gridViewAdapter;
    private List<notes> notesList;
    private int currentViewMode = 0;
    SearchView searchView;
    String SearchValue;
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    String title;
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Bundle b = getIntent().getExtras();
        title = b.getString("title");
        setTitle(title.toString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        noteDBhelper = new NoteDbHelper(this);
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);

        display_msg = (TextView) findViewById(R.id.display_msg);
        display_msg.setVisibility(View.GONE);

        //Inflate ViewStub before get view

        stubList.inflate();
        stubGrid.inflate();

        listView = (ListView) findViewById(R.id.mylistview);
        gridView = (GridView) findViewById(R.id.mygridview);
        searchView = (SearchView) findViewById(R.id.SearchView);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search In " + title);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                // 在这里搜索
                SearchValue = text;
                Search();
                setAdapters();
                return true;
            }
        });
        getNotesList();

        //在share reference中保存视图模式
        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);//默认的视图时listview
        //Register item lick
        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);

        switchView();
    }

    private void switchView() {

        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            //显示listview
            stubList.setVisibility(View.VISIBLE);
            //隐藏gridview
            stubGrid.setVisibility(View.GONE);
        } else {
            //隐藏listview
            stubList.setVisibility(View.GONE);
            //显示gridview
            stubGrid.setVisibility(View.VISIBLE);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            listViewAdapter = new ListViewAdapter(this, R.layout.list_item, notesList);
            listView.setAdapter(listViewAdapter);
            gridView.setTextFilterEnabled(true);
        } else {
            gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, notesList);
            gridView.setAdapter(gridViewAdapter);
            gridView.setTextFilterEnabled(true);
        }
    }

    public List<notes> getNotesList() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Label_getListContents(title);
        if (data.getCount() == 0) {
            //no data founds
            display_msg.setVisibility(View.VISIBLE);
        } else {
            while (data.moveToNext()) { //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0), data.getString(1),
                        data.getString(2), data.getString(3),
                        data.getString(4), data.getString(9),
                        data.getString(11), data.getString(5),
                        data.getString(10), data.getString(12)));
            }
        }
        return notesList;
    }

    public List<notes> SortByCreationDate() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Label_SortByCreationDate(title);
        if (data.getCount() == 0) {
            //no data founds
            display_msg.setVisibility(View.VISIBLE);
        } else {
            while (data.moveToNext()) { //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0), data.getString(1),
                        data.getString(2), data.getString(3),
                        data.getString(4), data.getString(9),
                        data.getString(11), data.getString(5),
                        data.getString(10), data.getString(12)));
            }
        }
        return notesList;
    }

    public List<notes> SortByNotificationDate() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Label_SortByNotificationDate(title);
        if (data.getCount() == 0) {
            //no data founds
            display_msg.setVisibility(View.VISIBLE);
        } else {
            while (data.moveToNext()) { //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0), data.getString(1),
                        data.getString(2), data.getString(3),
                        data.getString(4), data.getString(9),
                        data.getString(11), data.getString(5),
                        data.getString(10), data.getString(12)));
            }
        }
        return notesList;
    }

    public List<notes> SortByalphabet_AZ() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Label_SortByalphabet_AZ(title);
        if (data.getCount() == 0) {
            //no data founds
            display_msg.setVisibility(View.VISIBLE);
        } else {
            while (data.moveToNext()) { //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0), data.getString(1),
                        data.getString(2), data.getString(3),
                        data.getString(4), data.getString(9),
                        data.getString(11), data.getString(5),
                        data.getString(10), data.getString(12)));
            }
        }
        return notesList;
    }

    public List<notes> SortByalphabet_ZA() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Label_SortByalphabet_ZA(title);
        if (data.getCount() == 0) {
            //no data founds
            display_msg.setVisibility(View.VISIBLE);
        } else {
            while (data.moveToNext()) { //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0), data.getString(1),
                        data.getString(2), data.getString(3),
                        data.getString(4), data.getString(9),
                        data.getString(11), data.getString(5),
                        data.getString(10), data.getString(12)));
            }
        }
        return notesList;
    }

    public List<notes> Search() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Label_Search(SearchValue, title);
        while (data.moveToNext()) { //title,body,background,color,favoris,label,notification
            notesList.add(new notes(data.getInt(0), data.getString(1),
                    data.getString(2), data.getString(3),
                    data.getString(4), data.getString(9),
                    data.getString(11), data.getString(5),
                    data.getString(10), data.getString(12)));
        }
        return notesList;
    }


    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (notesList.get(position).getType().equals("text")) {
                Intent i = new Intent(LabelDetails.this, Text_details.class);
                Bundle b = new Bundle();
                b.putInt("id", notesList.get(position).getId());
                b.putString("title", notesList.get(position).getTitle());
                b.putString("body", notesList.get(position).getbody());
                b.putString("background", notesList.get(position).getBackground());
                b.putString("color", notesList.get(position).getColor());
                b.putString("favoris", notesList.get(position).getFavoris());
                b.putString("label", notesList.get(position).getLabel());
                b.putString("notification", notesList.get(position).getNotification());
                i.putExtras(b);
                LabelDetails.this.startActivity(i);
            } else if (notesList.get(position).getType().equals("voice")) {
                Intent i = new Intent(LabelDetails.this, Voice_details.class);
                Bundle b = new Bundle();
                b.putInt("id", notesList.get(position).getId());
                b.putString("title", notesList.get(position).getTitle());
                b.putString("body", notesList.get(position).getbody());
                b.putString("background", notesList.get(position).getBackground());
                b.putString("color", notesList.get(position).getColor());
                b.putString("favoris", notesList.get(position).getFavoris());
                b.putString("label", notesList.get(position).getLabel());
                b.putString("notification", notesList.get(position).getNotification());
                b.putString("VoiceFileName", notesList.get(position).getVoiceFilePath());
                i.putExtras(b);
                LabelDetails.this.startActivity(i);
            } else if (notesList.get(position).getType().equals("list")) {
                Intent i = new Intent(LabelDetails.this, List_details.class);
                Bundle b = new Bundle();
                b.putInt("id", notesList.get(position).getId());
                b.putString("title", notesList.get(position).getTitle());
                b.putString("body", notesList.get(position).getbody());
                b.putString("background", notesList.get(position).getBackground());
                b.putString("color", notesList.get(position).getColor());
                b.putString("favoris", notesList.get(position).getFavoris());
                b.putString("label", notesList.get(position).getLabel());
                b.putString("notification", notesList.get(position).getNotification());
                i.putExtras(b);
                LabelDetails.this.startActivity(i);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 把home_menu中的item添加到 action bar上
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.home_menu, menu);
        if (currentViewMode == 0) {
            mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_grid);
        } else {
            mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_list);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 当note_menu.xml中的item作为的action bar被点击后，在这个方法中会自动处理action bar上Button的点击事件，
        // 只要你在AndroidManifest.xml中指定一个parent activity
        int id = item.getItemId();

        if (id == R.id.sort) {
            // 对话框
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.sort_popup_box);
            RadioButton sortby_creationDate = (RadioButton) dialog.findViewById(R.id.creation_date);
            RadioButton sortby_notificationDate = (RadioButton) dialog.findViewById(R.id.notification_date);
            RadioButton sortby_alphabet_AZ = (RadioButton) dialog.findViewById(R.id.alphabet_AZ);
            RadioButton sortby_alphabet_ZA = (RadioButton) dialog.findViewById(R.id.alphabet_ZA);
            sortby_creationDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SortByCreationDate();
                    setAdapters();
                    dialog.dismiss();
                }
            });
            sortby_notificationDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notesList = new ArrayList<>();
                    SortByNotificationDate();
                    setAdapters();
                    dialog.dismiss();
                }
            });
            sortby_alphabet_AZ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SortByalphabet_AZ();
                    setAdapters();
                    dialog.dismiss();
                }
            });
            sortby_alphabet_ZA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SortByalphabet_ZA();
                    setAdapters();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if (id == R.id.search) {
            if (searchView.getVisibility() == View.VISIBLE) {
                searchView.setVisibility(View.GONE);
            } else {
                searchView.setVisibility(View.VISIBLE);
            }
        }
        if (id == R.id.ViewMode) {
            if (VIEW_MODE_LISTVIEW == currentViewMode) {
                currentViewMode = VIEW_MODE_GRIDVIEW;
                mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_list);
            } else {
                currentViewMode = VIEW_MODE_LISTVIEW;
                mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_grid);
            }
            //转换视图模式
            switchView();
            //在share reference中保存视图模式
            SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("currentViewMode", currentViewMode);
            editor.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SortByCreationDate();
        setAdapters();
    }
}
