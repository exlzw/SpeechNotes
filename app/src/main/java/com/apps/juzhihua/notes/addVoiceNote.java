package com.apps.juzhihua.notes;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
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
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class addVoiceNote extends AppCompatActivity {
    //变量声明
    TextView Current_Creation_Datetime;
    ImageView circle_white, circle_lightblue, circle_blue, circle_purple,
            circle_black, circle_yellow, circle_green, circle_corn,
            circle_crystal, circle_pink, circle_rose, circle_red;
    ImageView text_white, text_lightblue, text_blue, text_purple,
            text_black, text_yellow, text_green, text_corn,
            text_crystal, text_pink, text_rose, text_red;
    RelativeLayout Parant_Layout;
    EditText Note_title, Note_body, in_date, in_time;
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
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;
    private static String mFileName = "no";
    private static final String LOG_TAG = "AudioRecordTest";
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;
    RelativeLayout Voice_Content, Play_Content;
    ImageView record_btn, record_stop, play_btn, pause_btn, delete_record;
    TextView clickToStart, current_time, Totale_time;
    SeekBar SeekBar;
    Chronometer recording_timer;

    //用来保存提醒的日期时间
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voice_note);

        //instantiate Classes labelDBhelper for LAbels and NoteDB for Notes
        LabelDB = new LabelDbHelper(this);
        NoteDB = new NoteDbHelper(this);

        //FindViewByID
        initView();

        //为Label实例化一个LabelDbHelper的对象LabelDB；为Notes实例化一个NoteDbHelper的对象NoteDB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String currentDateandTime = sdf.format(new Date());
        Current_Creation_Datetime.setText("创建于 : " + currentDateandTime);


        //从设置中获取默认的值
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String background = sharedPreferences.getString("Background", "#FFFFFF");
        String TextColor = sharedPreferences.getString("TextColor", "#000000");
        String FontSize = sharedPreferences.getString("FontSize", "medium");
        String_Table[0] = background;
        String_Table[1] = TextColor;
        String_Table[2] = "no";
        String_Table[3] = "no";
        String_Table[4] = currentDateandTime;
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


        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        record_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });
        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });

        delete_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(addVoiceNote.this)
                        .setTitle("你确定吗?")
                        .setMessage("删除这条录音吗?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                File fdelete = new File(mFileName);
                                if (fdelete.exists()) {
                                    stopPlaying();
                                    if (fdelete.delete()) {
                                        Play_Content.setVisibility(View.INVISIBLE);
                                        Voice_Content.setVisibility(View.VISIBLE);
                                        record_stop.setVisibility(View.INVISIBLE);
                                        record_btn.setVisibility(View.VISIBLE);
                                        recording_timer.setVisibility(View.INVISIBLE);
                                        clickToStart.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(addVoiceNote.this, "不能删除这个文件 !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).create().show();
            }
        });
    }

    private void initView() {
        Current_Creation_Datetime = (TextView) findViewById(R.id.Current_Creation_Datetime);
        Parant_Layout = (RelativeLayout) findViewById(R.id.Parant_Layout);
        Note_title = (EditText) findViewById(R.id.Note_title);
        Note_body = (EditText) findViewById(R.id.Note_body);
        Voice_Content = (RelativeLayout) findViewById(R.id.Voice_Content);
        Play_Content = (RelativeLayout) findViewById(R.id.Play_Content);
        record_btn = (ImageView) findViewById(R.id.record_btn);
        record_stop = (ImageView) findViewById(R.id.record_stop);
        play_btn = (ImageView) findViewById(R.id.play_btn);
        pause_btn = (ImageView) findViewById(R.id.pause_btn);
        delete_record = (ImageView) findViewById(R.id.delete_record);
        recording_timer = (Chronometer) findViewById(R.id.recording_timer);
        current_time = (TextView) findViewById(R.id.current_time);
        Totale_time = (TextView) findViewById(R.id.Totale_time);
        clickToStart = (TextView) findViewById(R.id.clickToStart);
        SeekBar = (SeekBar) findViewById(R.id.SeekBar);
        h_line = (View) findViewById(R.id.h_line);
        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL2 = (LinearLayout) findViewById(R.id.LL2);
        label_text_sub = (TextView) findViewById(R.id.label_text_sub);
        notify_text_sub = (TextView) findViewById(R.id.notify_text_sub);
        h_line.setVisibility(View.INVISIBLE);
        LL1.setVisibility(View.INVISIBLE);
        LL2.setVisibility(View.INVISIBLE);
    }

    private void startRecording() {
        record_btn.setVisibility(View.INVISIBLE);
        record_stop.setVisibility(View.VISIBLE);
        // 记录到可见的外部缓存目录
        mFileName = getExternalCacheDir().getAbsolutePath();
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        mFileName += "/" + date.format(new Date()) + ".3gp";
        clickToStart.setVisibility(View.INVISIBLE);
        recording_timer.setVisibility(View.VISIBLE);
        recording_timer.setBase(SystemClock.elapsedRealtime());
        recording_timer.start();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    private void stopRecording() {
        Voice_Content.setVisibility(View.INVISIBLE);
        Play_Content.setVisibility(View.VISIBLE);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recording_timer.stop();
    }

    private void startPlaying() {
        play_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                    pause_btn.setVisibility(View.INVISIBLE);
                    play_btn.setVisibility(View.VISIBLE);
                }

            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        finalTime = mPlayer.getDuration();
        startTime = mPlayer.getCurrentPosition();
        SeekBar.setProgress((int) startTime);
        if (oneTimeOnly == 0) {
            SeekBar.setMax((int) finalTime);
        }
        SeekBar.setProgress((int) startTime);
        long TimeSec = TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime));
        long TimeMin = TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
        if (TimeSec < 10) {
            if (TimeMin < 10) {
                Totale_time.setText("0" + TimeMin + " : 0" + TimeSec);
            } else {
                Totale_time.setText(TimeMin + " : 0" + TimeSec);
            }
        } else {
            if (TimeMin < 10) {
                Totale_time.setText("0" + TimeMin + " : " + TimeSec);
            } else {
                Totale_time.setText(TimeMin + " : " + TimeSec);
            }
        }
        try {
            myHandler.postDelayed(UpdateSongTime, 100);
        } catch (Exception e) {
        }
    }

    private void stopPlaying() {
        try {
            myHandler.removeCallbacks(UpdateSongTime);
            mPlayer.release();
            mPlayer = null;
            pause_btn.setVisibility(View.INVISIBLE);
            play_btn.setVisibility(View.VISIBLE);
            SeekBar.setProgress(0);
            current_time.setText("00 : 00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mPlayer.getCurrentPosition();
            long TimeSec = TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));
            long TimeMin = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
            if (TimeSec < 10) {
                if (TimeMin < 10) {
                    current_time.setText("0" + TimeMin + " : 0" + TimeSec);
                } else {
                    current_time.setText(TimeMin + " : 0" + TimeSec);
                }
            } else {
                if (TimeMin < 10) {
                    current_time.setText("0" + TimeMin + " : " + TimeSec);
                } else {
                    current_time.setText(TimeMin + " : " + TimeSec);
                }
            }
            SeekBar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onPause() {   // 当用户退出这个activity时，它会往NoteDB中插入数据
        super.onPause();
        stopPlaying();
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
                Note_title.getText().toString(), // 插入笔记标题
                Note_body.getText().toString(), // 往Note_body中插入笔记的文本内容
                String_Table[0],                // 插入Background颜色的Hex Code值
                String_Table[1],                // 插入文本字体颜色的Hex Code值
                String_Table[3],                // 如果提醒的日期时间存在，则插入
                "no",                      // 笔记在垃圾箱 => no
                String_Table[4],                // 插入笔记的创建日期时间
                "no",                    // 笔记在物品的收藏 => no
                "voice",                   // 笔记的种类时一个voice
                String_Table[2],                // 笔记所属的label
                mFileName);                     //在这里插入录音文件名
        if (isInserted) {
            Toast.makeText(addVoiceNote.this, Note_title.getText().toString() + " 笔记已创建", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            Toast.makeText(addVoiceNote.this, "笔记未创建 !", Toast.LENGTH_LONG).show();
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
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFFFFFF"));
                    String_Table[0] = "#AAFFFFFF";
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
                        LabelDB.insertLabel(label_text.getText().toString());
                        Toast.makeText(addVoiceNote.this, label_text.getText().toString() + " 标签已创建", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(addVoiceNote.this, "已添加标签 : " + sp.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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
                    //***************************************************/

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
                    new DatePickerDialog(addVoiceNote.this, date, myCalendar
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
                    mTimePicker = new TimePickerDialog(addVoiceNote.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            in_time.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

                            //used for alarm manager---------------------------
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            /**************************************************/
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
