package com.apps.juzhihua.notes;

        import android.app.AlarmManager;
        import android.app.DatePickerDialog;
        import android.app.Dialog;
        import android.app.PendingIntent;
        import android.app.TimePickerDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.graphics.Color;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.TypedValue;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.Window;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.TimePicker;
        import android.widget.Toast;

        import com.apps.juzhihua.notes.adapter.AddListViewAdapter;
        import com.apps.juzhihua.notes.util.LabelDbHelper;
        import com.apps.juzhihua.notes.util.NoteDbHelper;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;

public class addListNote extends AppCompatActivity {
    TextView Current_Creation_Datetime;//用来获取系统当前日期和时间
    ImageView circle_white, circle_lightblue, circle_blue, circle_purple,
            circle_black, circle_yellow, circle_green, circle_corn,
            circle_crystal, circle_pink, circle_rose, circle_red;//用户可自由选择的笔记背景颜色
    ImageView text_white, text_lightblue, text_blue, text_purple,
            text_black, text_yellow, text_green, text_corn,
            text_crystal, text_pink, text_rose, text_red;//用户可自由选择的字体颜色
    RelativeLayout Parant_Layout;//这是activity_add_list_note.xml中的第一层RelativeLayout，我们需要它来改变背景颜色
    EditText Note_title, in_date, in_time;//用户从对话框中选择了日期和时间后，Note_title,in_date,in_time会被添加对应的EditText中去
    ImageView btn_date, btn_time;//当用户点击btn_date后，会显示datepicker(日期选择器)来选择日期，同理，对于btn_time也一样
    Button DateTime_ok;//用户选择日期时间后点击确定的button
    Calendar myCalendar;
    Spinner sp;//用于在对话框中选择预备创建好了的标签
    //创建字符串数组
    //String_Table[0] => Background笔记的背景 ; String_Table[1] => textcolor字体颜色 ; String_Table[2] => label标签 ;
    // String_Table[3] => Notification提醒 ; String_Table[4] => Datetime creation日期时间的创建
    private String[] String_Table = new String[5];
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    View h_line;//用来分隔元素的水平的线
    LinearLayout LL1, LL2;//LL1布局包含标签图标和标签文本，LL2包含提醒的图标和提醒的时间文本
    TextView label_text_sub, notify_text_sub;//用户添加的特别标签或从已存在的标签中选择，会被加入到label_text_sub，用户设置的日期时间会被加入到notify_text_sub
    NonScrollListView lv;//因为我们有元素在listview的下面，所以我们用NonScrollListView
    RelativeLayout add_item;//用来作为button来显示添加item的对话框
    LabelDbHelper LabelDB;//类
    NoteDbHelper NoteDB;//类
    private AddListViewAdapter listViewAdapter;
    private List<labelClass> ListOfItems;
    private Handler myHandler = new Handler();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //用来保存提醒的日期时间
    Calendar calendar = Calendar.getInstance();
    int getId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_note);
        //为Label实例化一个LabelDbHelper的对象LabelDB；为Notes实例化一个NoteDbHelper的对象NoteDB
        LabelDB = new LabelDbHelper(this);
        NoteDB = new NoteDbHelper(this);
        //used to update listview after the user click update or delete
        sharedPreferences = getSharedPreferences("ControleAction", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //FindViewByID
        initView();
        //在清单中添加新的item
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示一个用户可以在哪儿添加item的对话框
                final Dialog dialog = new Dialog(addListNote.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_items_popup);
                Button add_label_ok = (Button) dialog.findViewById(R.id.add_label_ok);
                final EditText new_label_text = (EditText) dialog.findViewById(R.id.new_label_text);
                add_label_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //方法insertItem(item,note_id)
                        LabelDB.insertItem(new_label_text.getText().toString(), 0);
                        setAdapters();//更新listview
                        dialog.dismiss();//关闭dialog
                    }
                });
                dialog.show();
            }
        });
        //获取当前的日期时间，显示在文本框Current_Creation_Datetime上
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String currentDateAndTime = sdf.format(new Date());
        Current_Creation_Datetime.setText("创建于 : " + currentDateAndTime);
        //从设置中获取默认的值
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String background = sharedPreferences.getString("Background", "#FFFFFF");
        String TextColor = sharedPreferences.getString("TextColor", "#000000");
        String FontSize = sharedPreferences.getString("FontSize", "中");
        String_Table[0] = background;
        String_Table[1] = TextColor;
        String_Table[2] = "no";
        String_Table[3] = "no";
        String_Table[4] = currentDateAndTime;
        Parant_Layout.setBackgroundColor(Color.parseColor(String_Table[0]));//改变activity_add_list_note.xml中第一层RelativeLayout的背景颜色
        Note_title.setTextColor(Color.parseColor(String_Table[1]));//可改变ListNote中标题字体颜色
        Current_Creation_Datetime.setTextColor(Color.parseColor(String_Table[1]));//可改变ListNote中显示创建时间字体的颜色，并没有做改变每个list本身字体的眼神
        if (FontSize.equals("小")) {
            Note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        if (FontSize.equals("中")) {
            Note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
        if (FontSize.equals("大")) {
            Note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        }
        setAdapters();//在listview中显示item
    }
    private void initView() {
        Current_Creation_Datetime = (TextView) findViewById(R.id.Current_Creation_Datetime);
        Parant_Layout = (RelativeLayout) findViewById(R.id.Parant_Layout);
        Note_title = (EditText) findViewById(R.id.Note_title);
        h_line = (View) findViewById(R.id.h_line);
        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL2 = (LinearLayout) findViewById(R.id.LL2);
        label_text_sub = (TextView) findViewById(R.id.label_text_sub);
        notify_text_sub = (TextView) findViewById(R.id.notify_text_sub);
        lv = (NonScrollListView) findViewById(R.id.listview);
        add_item = (RelativeLayout) findViewById(R.id.add_item);
        h_line.setVisibility(View.INVISIBLE);
        LL1.setVisibility(View.INVISIBLE);
        LL2.setVisibility(View.INVISIBLE);
    }
    private void setAdapters() {
        getListItems();
        listViewAdapter = new AddListViewAdapter(this, R.layout.add_item_list, ListOfItems);
        lv.setAdapter(listViewAdapter);
    }
    public List<labelClass> getListItems() {
        ListOfItems = new ArrayList<>();
        Cursor data = LabelDB.getItemsContents(0);//显示show all items of label_table where note_id=0
        if (data.getCount() == 0) {
            //没有找到数据
        } else {
            while (data.moveToNext()) {
                ListOfItems.add(new labelClass(data.getInt(0), data.getString(1)));
            }
        }
        return ListOfItems;
    }
    @Override
    protected void onStart() {  //Runnable 接口的Start方法(当用户点击了更新或删除item的Button时，用来更新listview)
        super.onStart();
        try {
            myHandler.postDelayed(UpdateListView, 100);
        } catch (Exception e) {
        }
    }
    @Override
    protected void onPause() { // 当用户退出这个activity时，它会往NoteDB中插入数据
        super.onPause();
        myHandler.removeCallbacks(UpdateListView);//stop the runnable
        if (!TextUtils.isEmpty(Note_title.getText().toString())) {
            //标题不为空
            insert_values();
        } else {
            //标题为空
            //删除已经插入到list表中的所有行
            LabelDB.deleteItemsFromList(0);
        }
    }
    public void insert_values() {
        boolean isInserted = NoteDB.insertData(
                Note_title.getText().toString(), // 插入笔记标题
                " ",                       // 往body中插入笔记的文本
                String_Table[0],                 // 插入Background颜色的Hex Code值
                String_Table[1],                 // 插入文本字体颜色的Hex Code值
                String_Table[3],                 // 如果提醒的日期时间存在，则插入
                "no",                      // 笔记在垃圾箱 => no
                String_Table[4],                 // 插入笔记的创建日期时间
                "no",                     // 笔记在物品的收藏 => no
                "list",                     // 笔记的种类时一个list
                String_Table[2],                 // 笔记所属的label
                "no");            //录音文件名字
        if (isInserted) {
            Toast.makeText(addListNote.this, Note_title.getText().toString() + " 笔记已创建", Toast.LENGTH_LONG).show();
            //用标题作为参数
            getId = Integer.parseInt(NoteDB.getIDFromTitle(Note_title.getText().toString()));
            //we need now to update 0 in label_table.note_id by value of getId
            LabelDB.updateNoteIDByNewID(getId);//通过新的值来更新note_id列
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            Toast.makeText(addListNote.this, "笔记未被创建 !", Toast.LENGTH_LONG).show();
        }
    }
    private Runnable UpdateListView = new Runnable() {
        public void run() {
            try {
                String UserClickDelete = sharedPreferences.getString("UserClickDelete", "no");
                String UserClickUpdate = sharedPreferences.getString("UserClickUpdate", "no");
                if (UserClickDelete.equals("yes") || UserClickUpdate.equals("yes")) {
                    setAdapters(); //更新listview
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 能将note_menu.xml中的item添加到action bar上面
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 当note_menu.xml中的item作为的action bar被点击后，在这个方法中会自动处理action bar上Button的点击事件，
        // 只要你在AndroidManifest.xml中指定一个parent activity
        int id = item.getItemId();

        if (id == R.id.color) { //更改背景颜色
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.background_color_popup);//显示一个用户能够选择颜色的对话框
            circle_white = (ImageView) dialog.findViewById(R.id.circle_white);
            circle_lightblue = (ImageView) dialog.findViewById(R.id.circle_lightblue);
            circle_blue = (ImageView) dialog.findViewById(R.id.circle_blue);
            circle_purple = (ImageView) dialog.findViewById(R.id.circle_purple);
            circle_black = (ImageView) dialog.findViewById(R.id.circle_black);
            circle_yellow = (ImageView) dialog.findViewById(R.id.circle_yellow);
            circle_green = (ImageView) dialog.findViewById(R.id.circle_green);
            circle_corn = (ImageView) dialog.findViewById(R.id.circle_corn);
            circle_crystal = (ImageView) dialog.findViewById(R.id.circle_crystal);
            circle_pink = (ImageView) dialog.findViewById(R.id.circle_pink);
            circle_rose = (ImageView) dialog.findViewById(R.id.circle_rose);
            circle_red = (ImageView) dialog.findViewById(R.id.circle_red);
            circle_white.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 用户选择白色作为背景颜色
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFFFFFF")); //更改activity_add_list_note.xml中第一层RelativeLayout区域的背景颜色
                    String_Table[0] = "#AAFFFFFF"; //插入背景颜色值到String_Table[0]中去
                    dialog.dismiss(); //关闭背景颜色选择对话框
                }
            });
            circle_lightblue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA00BCD4"));
                    String_Table[0] = "#AA00BCD4";
                    dialog.dismiss();
                }
            });
            circle_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA3F51B5"));
                    String_Table[0] = "#AA3F51B5";
                    dialog.dismiss();
                }
            });
            circle_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA9C27B0"));
                    String_Table[0] = "#AA9C27B0";
                    dialog.dismiss();
                }
            });
            circle_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA000000"));
                    String_Table[0] = "#AA000000";
                    dialog.dismiss();
                }
            });
            circle_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFF9800"));
                    String_Table[0] = "#AAFF9800";
                    dialog.dismiss();
                }
            });
            circle_green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA00CC99"));
                    String_Table[0] = "#AA00CC99";
                    dialog.dismiss();
                }
            });
            circle_corn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFBEC5D"));
                    String_Table[0] = "#AAFBEC5D";
                    dialog.dismiss();
                }
            });
            circle_crystal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAA7D8DE"));
                    String_Table[0] = "#AAA7D8DE";
                    dialog.dismiss();
                }
            });
            circle_pink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFF9899"));
                    String_Table[0] = "#AAFF9899";
                    dialog.dismiss();
                }
            });
            circle_rose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAE91E63"));
                    String_Table[0] = "#AAE91E63";
                    dialog.dismiss();
                }
            });
            circle_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AADB4437"));
                    String_Table[0] = "#AADB4437";
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        if (id == R.id.textColor) { //更改文本字体的颜色
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.text_color_popup);
            text_white = (ImageView) dialog.findViewById(R.id.text_white);
            text_lightblue = (ImageView) dialog.findViewById(R.id.text_lightblue);
            text_blue = (ImageView) dialog.findViewById(R.id.text_blue);
            text_purple = (ImageView) dialog.findViewById(R.id.text_purple);
            text_black = (ImageView) dialog.findViewById(R.id.text_black);
            text_yellow = (ImageView) dialog.findViewById(R.id.text_yellow);
            text_green = (ImageView) dialog.findViewById(R.id.text_green);
            text_corn = (ImageView) dialog.findViewById(R.id.text_corn);
            text_crystal = (ImageView) dialog.findViewById(R.id.text_crystal);
            text_pink = (ImageView) dialog.findViewById(R.id.text_pink);
            text_rose = (ImageView) dialog.findViewById(R.id.text_rose);
            text_red = (ImageView) dialog.findViewById(R.id.text_red);
            text_white.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FFFFFF"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FFFFFF"));
                    String_Table[1] = "#FFFFFF";
                    dialog.dismiss();
                }
            });
            text_lightblue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#00BCD4"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#00BCD4"));
                    String_Table[1] = "#00BCD4";
                    dialog.dismiss();
                }
            });
            text_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#3F51B5"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#3F51B5"));
                    String_Table[1] = "#3F51B5";
                    dialog.dismiss();
                }
            });
            text_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#9C27B0"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#9C27B0"));
                    String_Table[1] = "#9C27B0";
                    dialog.dismiss();
                }
            });
            text_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#000000"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#000000"));
                    String_Table[1] = "#000000";
                    dialog.dismiss();
                }
            });
            text_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FF9800"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FF9800"));
                    String_Table[1] = "#FF9800";
                    dialog.dismiss();
                }
            });
            text_green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#00CC99"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#00CC99"));
                    String_Table[1] = "#00CC99";
                    dialog.dismiss();
                }
            });
            text_corn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FBEC5D"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FBEC5D"));
                    String_Table[1] = "#FBEC5D";
                    dialog.dismiss();
                }
            });
            text_crystal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#A7D8DE"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#A7D8DE"));
                    String_Table[1] = "#A7D8DE";
                    dialog.dismiss();
                }
            });
            text_pink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FF9899"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FF9899"));
                    String_Table[1] = "#FF9899";
                    dialog.dismiss();
                }
            });
            text_rose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#E91E63"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#E91E63"));
                    String_Table[1] = "#E91E63";
                    dialog.dismiss();
                }
            });
            text_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#DB4437"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#DB4437"));
                    String_Table[1] = "#DB4437";
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if (id == R.id.label) { // 当note_menu.xml中的action bar上的标签Button被点击，弹出label_popup_box.xml对话框，添加新标签或选择标签
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.label_popup_box);
            sp = (Spinner) dialog.findViewById(R.id.spinner);
            Button label_btn_ok = (Button) dialog.findViewById(R.id.Label_ok);
            ImageView label_add_btn = (ImageView) dialog.findViewById(R.id.label_add_btn);
            final EditText label_text = (EditText) dialog.findViewById(R.id.label_text);
            FillSpinner(); //从table中获取所有的标签并显示在spinner
            label_add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //新建标签
                    if (!TextUtils.isEmpty(label_text.getText().toString())) {
                        //通过对象LabelDB回调LabelDBHelper类的方法 => insertLabel(label name)
                        LabelDB.insertLabel(label_text.getText().toString());
                        Toast.makeText(addListNote.this, label_text.getText().toString() + " 标签已创建", Toast.LENGTH_SHORT).show();
                        label_text.setText("");
                        FillSpinner(); //用户添加新的标签后更新Spinner
                    } else {
                        label_text.setError("编辑框不能为空");
                    }
                }
            });
            label_btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sp.getSelectedItem().toString() == null) {
                        LL1.setVisibility(View.INVISIBLE);
                    } else {
                        //用户从Spinner中选择了标签，并把改标签添加到了String_Table[2]（存label标签的）中去，
                        // 当用户退出时，会执行并存储数据到NoteDatabase中去
                        String_Table[2] = sp.getSelectedItem().toString();
                        Toast.makeText(addListNote.this, "已添加标签 : " + sp.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        h_line.setVisibility(View.VISIBLE);
                        LL1.setVisibility(View.VISIBLE);
                        label_text_sub.setText(sp.getSelectedItem().toString());//在activity_add_list_note.xml中id为label_text_sub的TextView上显示已选择了标签文本
                    }
                }
            });
            dialog.show();
        }
        if (id == R.id.notification) { //为笔记添加日期时间提醒
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.notification_popup_box);
            btn_date = (ImageView) dialog.findViewById(R.id.btn_date);
            btn_time = (ImageView) dialog.findViewById(R.id.btn_time);
            myCalendar = Calendar.getInstance();
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {//选择提醒的年月日
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    //给alarm manager设置提醒的日期
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "yyyy年MM月dd日"; //年月日的格式
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    in_date = (EditText) dialog.findViewById(R.id.in_date);
                    in_time = (EditText) dialog.findViewById(R.id.in_time);
                    in_date.setText(sdf.format(myCalendar.getTime()));
                    DateTime_ok = (Button) dialog.findViewById(R.id.DateTime_ok);
                    DateTime_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(in_date.getText().toString()) && !TextUtils.isEmpty(in_time.getText().toString())) {
                                String_Table[3] = in_date.getText().toString() + " " + in_time.getText().toString();
                                dialog.dismiss();
                                h_line.setVisibility(View.VISIBLE);
                                LL2.setVisibility(View.VISIBLE);
                                notify_text_sub.setText(String_Table[3]);
                                dialog.dismiss();
                            } else {
                                h_line.setVisibility(View.INVISIBLE);
                                LL2.setVisibility(View.INVISIBLE);
                                dialog.dismiss();
                            }
                        }
                    });
                }

            };
            btn_date.setOnClickListener(new View.OnClickListener() {//显示出日期选择器来给用户选择提醒的日期
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(addListNote.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            btn_time.setOnClickListener(new View.OnClickListener() {//显示出时间选择器来给用户选择提醒的具体时间
                @Override
                public void onClick(View v) {
                    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = myCalendar.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(addListNote.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            in_time.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                            //给alarm manager设置提醒的具体时间
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                        }
                    }, hour, minute, true);//使用24小时时间制
                    mTimePicker.show();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    public void FillSpinner() { //这个方法用来从table中获取所有的标签并显示在Spinner上
        list = LabelDB.getAllLabels();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }
}