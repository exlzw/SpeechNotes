package com.apps.juzhihua.notes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apps.juzhihua.notes.util.JsonParser;
import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

public class addTexteNote extends AppCompatActivity implements View.OnClickListener {
    TextView Current_Creation_Datetime;
    ImageView circle_white, circle_lightblue, circle_blue, circle_purple,
            circle_black, circle_yellow, circle_green, circle_corn,
            circle_crystal, circle_pink, circle_rose, circle_red;
    ImageView text_white, text_lightblue, text_blue, text_purple,
            text_black, text_yellow, text_green, text_corn,
            text_crystal, text_pink, text_rose, text_red;
    RelativeLayout Parant_Layout;
    EditText Note_title, Note_body, in_date, in_time;
    String Note_body_temporary_text;
    ImageView btn_date, btn_time;
    Button DateTime_ok;
    Calendar myCalendar;
    Spinner sp;
    LabelDbHelper LabelDB;
    NoteDbHelper NoteDB;
    //String_Table[0] => Background笔记的背景 ; String_Table[1] => textcolor字体颜色 ; String_Table[2] => label标签 ; String_Table[3] => Notification提醒 ; String_Table[4] => Datetime creation日期时间的创建
    private String[] String_Table = new String[5];
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    View h_line;
    LinearLayout LL1, LL2;
    TextView label_text_sub, notify_text_sub;
    LottieAnimationView anim;
    ImageButton imgBtn_input;
    //用来保存提醒的日期时间
    Calendar calendar = Calendar.getInstance();
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a9e8e3b");
        setContentView(R.layout.activity_add_texte_note);
        //为Label实例化一个LabelDbHelper的对象LabelDB；为Notes实例化一个NoteDbHelper的对象NoteDB
        LabelDB = new LabelDbHelper(this);
        NoteDB = new NoteDbHelper(this);
        //FindViewByID
        initView();
        //设置点击事件
        imgBtn_input.setOnClickListener(this);
        //获取当前的日期时间，显示在文本框Current_Creation_Datetime上
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String currentDateAndTime = sdf.format(new Date());
        Current_Creation_Datetime.setText("创建于 : " + currentDateAndTime);
        //从设置中获取默认的值
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String background = sharedPreferences.getString("Background", "#FFFFFF");
        String TextColor = sharedPreferences.getString("TextColor", "#000000");
        String FontSize = sharedPreferences.getString("FontSize", "medium");
        String_Table[0] = background;
        String_Table[1] = TextColor;
        String_Table[2] = "no";
        String_Table[3] = "no";
        String_Table[4] = currentDateAndTime;
        Parant_Layout.setBackgroundColor(Color.parseColor(String_Table[0]));
        Note_title.setTextColor(Color.parseColor(String_Table[1]));
        Note_body.setTextColor(Color.parseColor(String_Table[1]));
        Current_Creation_Datetime.setTextColor(Color.parseColor(String_Table[1]));
        if (FontSize.equals("小")) {
            Note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Note_body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        if (FontSize.equals("中")) {
            Note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            Note_body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
        if (FontSize.equals("大")) {
            Note_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            Note_body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        }
    }
    private void initView() {
        Current_Creation_Datetime = (TextView) findViewById(R.id.Current_Creation_Datetime);
        Parant_Layout = (RelativeLayout) findViewById(R.id.Parant_Layout);
        Note_title = (EditText) findViewById(R.id.Note_title);
        Note_body = (EditText) findViewById(R.id.Note_body);
        h_line = (View) findViewById(R.id.h_line);
        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL2 = (LinearLayout) findViewById(R.id.LL2);
        label_text_sub = (TextView) findViewById(R.id.label_text_sub);
        notify_text_sub = (TextView) findViewById(R.id.notify_text_sub);
        h_line.setVisibility(View.INVISIBLE);
        LL1.setVisibility(View.INVISIBLE);
        LL2.setVisibility(View.INVISIBLE);

        imgBtn_input = (ImageButton) findViewById(R.id.imgBtn_input);
        anim = (LottieAnimationView) findViewById(R.id.anim);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBtn_input :
                imgBtn_input.setVisibility(View.INVISIBLE);
                anim.setVisibility(View.VISIBLE);
                anim.loop(true);
                anim.playAnimation();
                showDialog();
                Note_body_temporary_text = Note_body.getText().toString();
        }
    }
    //**********************************语音识别 Start****************************************************//
    public void showDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(addTexteNote.this, new MyInitListener());
        //2.设置accent language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接受语音输入
        mDialog.show();
        //获取字体所在的控件，设置为不可见,隐藏字体，
        TextView textlink = (TextView) mDialog.getWindow().getDecorView().findViewWithTag("textlink");
        textlink.setVisibility(View.GONE);
        //改变对话框的背景色为白色
        LinearLayout ll1 = (LinearLayout) mDialog.getWindow().getDecorView().findViewWithTag("container");
        ll1.setBackgroundColor(Color.parseColor("#ffffff"));

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //处理监听事件
                anim.pauseAnimation();
                anim.setVisibility(View.INVISIBLE);
                imgBtn_input.setVisibility(View.VISIBLE);
            }
        });
    }
    public class MyInitListener implements InitListener {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(null, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class MyRecognizerDialogListener implements RecognizerDialogListener {
        /**
         * @param recognizerResult
         * @param b 是否说话结束
         */
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = recognizerResult.getResultString();
            String text = JsonParser.parseIatResult(result);
            //text是解析好了的内容
            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(result);
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIatResults.put(sn, text);
            StringBuffer resultBuffer = new StringBuffer();//拼成一句
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            Note_body.setText((Note_body_temporary_text+resultBuffer.toString()));
            Note_body.setSelection(Note_body.length());
        }
        /**
         * 出错了
         * @param speechError
         */
        @Override
        public void onError(SpeechError speechError) {
            Log.e("addTexteNote", "onError: " + speechError.getErrorDescription());
        }
    }
    //**********************************语音识别 End****************************************************//
    @Override
    protected void onPause() { // 当用户退出这个activity，会把数据插入到NoteDataBase中去
        super.onPause();

        if (!TextUtils.isEmpty(Note_title.getText().toString()) && !TextUtils.isEmpty(Note_body.getText().toString())) {
            //标题和笔记(Note_body)都不为空
            insert_values();
        } else {
            if (TextUtils.isEmpty(Note_title.getText().toString()) && !TextUtils.isEmpty(Note_body.getText().toString())) {
                //标题为空，笔记(Note_body)不为空
                Note_title.setText(" ");
                insert_values();

            }
            if (!TextUtils.isEmpty(Note_title.getText().toString()) && TextUtils.isEmpty(Note_body.getText().toString())) {
                //标题不为空，笔记(Note_body)为空
                Note_body.setText(" ");
                insert_values();
            }
        }
    }
    public void insert_values() {
        boolean isInserted = NoteDB.insertData(
                Note_title.getText().toString(),  // 插入笔记标题
                Note_body.getText().toString(),   // 往Note_body中插入笔记的文本内容
                String_Table[0],                  // 插入Background颜色的Hex Code值
                String_Table[1],                  // 插入文本字体颜色的Hex Code值
                String_Table[3],                  // 如果提醒的日期时间存在，则插入
                "no",                       // 笔记在垃圾箱 => no
                String_Table[4],                  // 插入笔记的创建日期时间
                "no",                      // 笔记在物品的收藏 => no
                "text",                      // 笔记的种类时一个text
                String_Table[2],                  // 笔记所属的label
                "no");             //录音文件名字
        if (isInserted) {
            Toast.makeText(addTexteNote.this, Note_title.getText().toString() + " 笔记已创建", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            Toast.makeText(addTexteNote.this, "笔记未被创建 !", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            dialog.setContentView(R.layout.background_color_popup);
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
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFFFFFF"));//更改activity_add_list_note.xml中第一层RelativeLayout区域的背景颜色
                    String_Table[0] = "#AAFFFFFF"; //插入背景颜色值到String_Table[0]中去
                    dialog.dismiss();
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
                    Note_body.setTextColor(Color.parseColor("#FFFFFF"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FFFFFF"));
                    String_Table[1] = "#FFFFFF";
                    dialog.dismiss();
                }
            });
            text_lightblue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#00BCD4"));
                    Note_body.setTextColor(Color.parseColor("#00BCD4"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#00BCD4"));
                    String_Table[1] = "#00BCD4";
                    dialog.dismiss();
                }
            });
            text_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#3F51B5"));
                    Note_body.setTextColor(Color.parseColor("#3F51B5"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#3F51B5"));
                    String_Table[1] = "#3F51B5";
                    dialog.dismiss();
                }
            });
            text_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#9C27B0"));
                    Note_body.setTextColor(Color.parseColor("#9C27B0"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#9C27B0"));
                    String_Table[1] = "#9C27B0";
                    dialog.dismiss();
                }
            });
            text_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#000000"));
                    Note_body.setTextColor(Color.parseColor("#000000"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#000000"));
                    String_Table[1] = "#000000";
                    dialog.dismiss();
                }
            });
            text_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FF9800"));
                    Note_body.setTextColor(Color.parseColor("#FF9800"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FF9800"));
                    String_Table[1] = "#FF9800";
                    dialog.dismiss();
                }
            });
            text_green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#00CC99"));
                    Note_body.setTextColor(Color.parseColor("#00CC99"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#00CC99"));
                    String_Table[1] = "#00CC99";
                    dialog.dismiss();
                }
            });
            text_corn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FBEC5D"));
                    Note_body.setTextColor(Color.parseColor("#FBEC5D"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FBEC5D"));
                    String_Table[1] = "#FBEC5D";
                    dialog.dismiss();
                }
            });
            text_crystal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#A7D8DE"));
                    Note_body.setTextColor(Color.parseColor("#A7D8DE"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#A7D8DE"));
                    String_Table[1] = "#A7D8DE";
                    dialog.dismiss();
                }
            });
            text_pink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#FF9899"));
                    Note_body.setTextColor(Color.parseColor("#FF9899"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#FF9899"));
                    String_Table[1] = "#FF9899";
                    dialog.dismiss();
                }
            });
            text_rose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#E91E63"));
                    Note_body.setTextColor(Color.parseColor("#E91E63"));
                    Current_Creation_Datetime.setTextColor(Color.parseColor("#E91E63"));
                    String_Table[1] = "#E91E63";
                    dialog.dismiss();
                }
            });
            text_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note_title.setTextColor(Color.parseColor("#DB4437"));
                    Note_body.setTextColor(Color.parseColor("#DB4437"));
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
            FillSpinner(); //get all labels from table and display it in the spinner
            label_add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //新建标签
                    if (!TextUtils.isEmpty(label_text.getText().toString())) {
                        //通过对象LabelDB回调LabelDBHelper类的方法 => insertLabel(label name)
                        LabelDB.insertLabel(label_text.getText().toString());
                        Toast.makeText(addTexteNote.this, label_text.getText().toString() + " 标签已创建", Toast.LENGTH_SHORT).show();
                        label_text.setText("");
                        FillSpinner(); //用户添加新的标签后更新Spinner
                    } else {
                        label_text.setError("this field can't be empty");
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
                        Toast.makeText(addTexteNote.this, "已添加标签 : " + sp.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        h_line.setVisibility(View.VISIBLE);
                        LL1.setVisibility(View.VISIBLE);
                        label_text_sub.setText(sp.getSelectedItem().toString());
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
                                      int dayOfMonth) {
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
            btn_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(addTexteNote.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            btn_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = myCalendar.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(addTexteNote.this, new TimePickerDialog.OnTimeSetListener() {
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