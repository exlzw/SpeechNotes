package com.apps.juzhihua.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenu;
import android.support.v4.view.MenuItemCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.apps.juzhihua.notes.adapter.GridViewAdapter;
import com.apps.juzhihua.notes.adapter.ListViewAdapter;
import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView all,favoris,notifications,trash,labels;
    RelativeLayout rl_no_note_find;
    NoteDbHelper noteDBhelper;
    LabelDbHelper labelDbHelper;
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private ListViewAdapter listViewAdapter;
    private GridViewAdapter gridViewAdapter;
    private List<notes> notesList;
    private int currentViewMode = 0;
    SearchView searchView;
    RadioButton sortby_creationDate ;
    RadioButton sortby_notificationDate ;
    RadioButton sortby_alphabet_AZ ;
    RadioButton sortby_alphabet_ZA ;
    String SearchValue ;

    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    private Menu mOptionsMenu;
    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //为主activity设置标题
        setTitle("所有笔记");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FabSpeedDial floatButton = (FabSpeedDial) findViewById(R.id.fabspeedDial);
        floatButton.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if (menuItem.getTitle().equals("文本")) {
                    Intent i = new Intent(MainActivity.this, addTexteNote.class);
                    MainActivity.this.startActivity(i);
                }
                if (menuItem.getTitle().equals("清单")) {
                    Intent i = new Intent(MainActivity.this, addListNote.class);
                    MainActivity.this.startActivity(i);
                }
                if (menuItem.getTitle().equals("录音")) {
                    Intent i = new Intent(MainActivity.this, addVoiceNote.class);
                    MainActivity.this.startActivity(i);
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        noteDBhelper = new NoteDbHelper(this);
        labelDbHelper = new LabelDbHelper(this);
        //These lines should be added in the OnCreate() of your main activity
        all =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.all));
        favoris=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.favoris));
        notifications=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.notifications));
        labels=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.label));
        trash=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.trash));

        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);

        rl_no_note_find = (RelativeLayout) findViewById(R.id.rl_no_note_find);
        rl_no_note_find.setVisibility(View.GONE);

        getNotesList();

        //Inflate ViewStub before get view

        stubList.inflate();
        stubGrid.inflate();

        listView = (ListView) findViewById(R.id.mylistview);
        gridView = (GridView) findViewById(R.id.mygridview);
        searchView = (SearchView) findViewById(R.id.SearchView);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("在这里搜索");

        //Get current view mode in share reference
        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);//Default is view listview
        //Register item lick
        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);

        switchView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                // 在這裏搜索
                SearchValue = text;
                Search();
                setAdapters();
                return true;
            }
        });

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //insert Four items in Label Listview (旅游，个人，生活，工作)
            labelDbHelper.insertLabel("旅游");
            labelDbHelper.insertLabel("个人");
            labelDbHelper.insertLabel("生活");
            labelDbHelper.insertLabel("工作");
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        }
        else
        {
            //nothing
        }
    }
    private void switchView() {

        if(VIEW_MODE_LISTVIEW == currentViewMode) {
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
        if(VIEW_MODE_LISTVIEW == currentViewMode) {
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
        Cursor data = noteDBhelper.getListContents();
        if(data.getCount() == 0){
            //没有找到数据
            rl_no_note_find.setVisibility(View.VISIBLE);
        }else{
            while(data.moveToNext()){ //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(9),data.getString(11),data.getString(5),data.getString(10),data.getString(12)));
            }
        }
        return notesList;
    }
    public List<notes> SortByCreationDate() {
        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.SortByCreationDate();
        if(data.getCount() == 0){
            //没有找到数据
            rl_no_note_find.setVisibility(View.VISIBLE);
        }else{
            while(data.moveToNext()){ //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(9),data.getString(11),data.getString(5),data.getString(10),data.getString(12)));
            }
        }
        return notesList;
    }
    public List<notes> SortByNotificationDate() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.SortByNotificationDate();
        if(data.getCount() == 0){
            //没有找到数据
            rl_no_note_find.setVisibility(View.VISIBLE);
        }else{
            while(data.moveToNext()){ //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(9),data.getString(11),data.getString(5),data.getString(10),data.getString(12)));
            }
        }
        return notesList;
    }
    public List<notes> SortByalphabet_AZ() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.SortByalphabet_AZ();
        if(data.getCount() == 0){
            //没有找到数据
            rl_no_note_find.setVisibility(View.VISIBLE);
        }else{
            while(data.moveToNext()){ //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(9),data.getString(11),data.getString(5),data.getString(10),data.getString(12)));
            }
        }
        return notesList;
    }
    public List<notes> SortByalphabet_ZA() {

        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.SortByalphabet_ZA();
        if(data.getCount() == 0){
            //没有找到数据
            rl_no_note_find.setVisibility(View.VISIBLE);
        }else{
            while(data.moveToNext()){ //title,body,background,color,favoris,label,notification
                notesList.add(new notes(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(9),data.getString(11),data.getString(5),data.getString(10),data.getString(12)));
            }
        }
        return notesList;
    }
    public List<notes> Search() {
        notesList = new ArrayList<>();
        Cursor data = noteDBhelper.Main_Search(SearchValue);
        while(data.moveToNext()){ //title,body,background,color,favoris,label,notification
            notesList.add(new notes(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getString(9),data.getString(11),data.getString(5),data.getString(10),data.getString(12)));
        }
        return notesList;
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(notesList.get(position).getType().equals("text")) {
                Intent i = new Intent(MainActivity.this, Text_details.class);
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
                MainActivity.this.startActivity(i);
            }
            else if(notesList.get(position).getType().equals("voice")) {
                Intent i = new Intent(MainActivity.this, Voice_details.class);
                Bundle b = new Bundle();
                b.putInt("id", notesList.get(position).getId());
                b.putString("title", notesList.get(position).getTitle());
                b.putString("body", notesList.get(position).getbody());
                b.putString("background", notesList.get(position).getBackground());
                b.putString("color", notesList.get(position).getColor());
                b.putString("favoris", notesList.get(position).getFavoris());
                b.putString("label", notesList.get(position).getLabel());
                b.putString("notification", notesList.get(position).getNotification());
                b.putString("VoiceFileName",notesList.get(position).getVoiceFilePath());
                i.putExtras(b);
                MainActivity.this.startActivity(i);
            }
            else if(notesList.get(position).getType().equals("list")) {
                Intent i = new Intent(MainActivity.this, List_details.class);
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
                MainActivity.this.startActivity(i);
            }
        }
    };



    private void initializeCountDrawer(){
        all.setGravity(Gravity.CENTER_VERTICAL);
        all.setTypeface(null, Typeface.BOLD);
        all.setTextColor(getResources().getColor(R.color.counters));
        int count_all = noteDBhelper.CountALL();
        all.setText(Integer.toString(count_all));
        favoris.setGravity(Gravity.CENTER_VERTICAL);
        favoris.setTypeface(null, Typeface.BOLD);
        favoris.setTextColor(getResources().getColor(R.color.counters));
        int count_favoris = noteDBhelper.CountFavoris();
        favoris.setText(Integer.toString(count_favoris));
        notifications.setGravity(Gravity.CENTER_VERTICAL);
        notifications.setTypeface(null, Typeface.BOLD);
        notifications.setTextColor(getResources().getColor(R.color.counters));
        int count_notification = noteDBhelper.CountNotification();
        notifications.setText(Integer.toString(count_notification));
        labels.setGravity(Gravity.CENTER_VERTICAL);
        labels.setTypeface(null, Typeface.BOLD);
        labels.setTextColor(getResources().getColor(R.color.counters));
        int count_labels = labelDbHelper.CountALL();
        labels.setText(Integer.toString(count_labels));
        trash.setGravity(Gravity.CENTER_VERTICAL);
        trash.setTypeface(null, Typeface.BOLD);
        trash.setTextColor(getResources().getColor(R.color.counters));
        int count_trash = noteDBhelper.Counttrash();
        trash.setText(Integer.toString(count_trash));
    }

    private Runnable UpdateCounters = new Runnable() {
        public void run() {
            try {
               initializeCountDrawer();
            }catch (Exception e){e.printStackTrace();}
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.home_menu, menu);
        if(currentViewMode == 0)
        {
            mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_grid);
        }
        else
        {
            mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_list);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort) {
            // custom dialog
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.sort_popup_box);
             sortby_creationDate = (RadioButton) dialog.findViewById(R.id.creation_date);
             sortby_notificationDate = (RadioButton) dialog.findViewById(R.id.notification_date);
             sortby_alphabet_AZ = (RadioButton) dialog.findViewById(R.id.alphabet_AZ);
             sortby_alphabet_ZA = (RadioButton) dialog.findViewById(R.id.alphabet_ZA);
            sortby_creationDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        sortby_creationDate.setChecked(true);
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
            if(searchView.getVisibility()==View.VISIBLE)
            {
                searchView.setVisibility(View.GONE);
            }
            else
            {
                searchView.setVisibility(View.VISIBLE);
            }
        }
        if(id == R.id.ViewMode) {
            if (VIEW_MODE_LISTVIEW == currentViewMode) {
                currentViewMode = VIEW_MODE_GRIDVIEW;
                mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_list);
            } else {
                currentViewMode = VIEW_MODE_LISTVIEW;
                mOptionsMenu.findItem(R.id.ViewMode).setIcon(R.drawable.ic_view_grid);
            }
            //Switch view
            switchView();
            //Save view mode in share reference
            SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("currentViewMode", currentViewMode);
            editor.commit();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 在这里处理导航栏的Item被点击后的事件
        int id = item.getItemId();
        if (id == R.id.all) {
            SortByCreationDate();
            setAdapters();
        } else if (id == R.id.favoris) {
            Intent i = new Intent(MainActivity.this, favoris.class);
            MainActivity.this.startActivity(i);
        } else if (id == R.id.notifications) {
            Intent i = new Intent(MainActivity.this, notifications.class);
            MainActivity.this.startActivity(i);
        } else if (id == R.id.label) {
            Intent i = new Intent(MainActivity.this, Labels.class);
            MainActivity.this.startActivity(i);
        } else if (id == R.id.trash) {
            Intent i = new Intent(MainActivity.this, trash.class);
            MainActivity.this.startActivity(i);
        } else if (id == R.id.settings) {
            Intent i = new Intent(MainActivity.this, settings.class);
            MainActivity.this.startActivity(i);
        }
        else if (id == R.id.share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            String title = "我的笔记";
            String body ="天天语记是一款非常简洁高效的记事本应用。它能帮助你捕捉和整理你的想法，让你快速备忘。你可以写文本笔记(配有语音识别功能)，记录音笔记，购物清单和待办事项清单，简单和直观的界面，自动保存能力让你省时省力！";
            body = body + "\n https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName();

            share.putExtra(Intent.EXTRA_SUBJECT, title);
            share.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(share, "share this app on :"));
        }
        else if (id == R.id.about) {
            AlertDialog DialogBox = About(); // show about box
            DialogBox.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private AlertDialog About() {
        AlertDialog MyDialog = new AlertDialog.Builder(this)
                .setTitle("关于!")
                .setMessage("天天语记是一款非常简洁高效的记事本应用。它能帮助你捕捉和整理你的想法，让你快速备忘。你可以写文本笔记(配有语音识别功能)，记录音笔记，购物清单和待办事项清单，简单和直观的界面，自动保存能力让你省时省力！")
                .setIcon(R.drawable.ic_about)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        return MyDialog;
    }
    @Override
    protected void onStart() {
        super.onStart();
        SortByCreationDate();
        setAdapters();
        try {
            myHandler.postDelayed(UpdateCounters, 100);
        } catch (Exception e) {}
    }
}
