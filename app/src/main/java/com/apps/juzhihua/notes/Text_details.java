package com.apps.juzhihua.notes;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Text_details extends AppCompatActivity {

    ImageView circle_white, circle_lightblue, circle_blue, circle_purple,
            circle_black, circle_yellow, circle_green, circle_corn,
            circle_crystal, circle_pink, circle_rose, circle_red;
    LinearLayout LL1, LL2;
    TextView label_text_sub, notify_text_sub, Current_Creation_Datetime;
    RelativeLayout Parant_Layout;
    View h_line;
    String title, body, label, favoris, background, color, notification;
    int id;
    Bundle b;
    private Menu mOptionsMenu;
    boolean checkFavoris = false;
    boolean checkNotification = false;
    boolean checkLabel = false;
    String FileName = "SharedPreferences";
    EditText Note_title, Note_body, in_date, in_time;
    ImageView btn_date, btn_time;
    Button DateTime_ok;
    Calendar myCalendar;
    Spinner sp;
    NoteDbHelper NoteDB;
    LabelDbHelper LabelDB;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    //用来保存提醒的日期时间
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //实例化
        NoteDB = new NoteDbHelper(this);
        LabelDB = new LabelDbHelper(this);

        //当用户点击了某个已存在的文本笔记后，获取数据
        b = getIntent().getExtras();
        id = b.getInt("id");
        title = b.getString("title");
        body = b.getString("body");
        background = b.getString("background");
        color = b.getString("color");
        favoris = b.getString("favoris");
        label = b.getString("label");
        notification = b.getString("notification");


        Parant_Layout = (RelativeLayout) findViewById(R.id.Parant_Layout);
        Note_title = (EditText) findViewById(R.id.Note_title);
        Note_body = (EditText) findViewById(R.id.Note_body);
        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL2 = (LinearLayout) findViewById(R.id.LL2);
        label_text_sub = (TextView) findViewById(R.id.label_text_sub);
        notify_text_sub = (TextView) findViewById(R.id.notify_text_sub);
        h_line = (View) findViewById(R.id.h_line);
        Current_Creation_Datetime = (TextView) findViewById(R.id.Current_Creation_Datetime);

        Note_title.setText(title);
        Note_body.setText(body);

        Current_Creation_Datetime.setText(NoteDB.getCreationDateTimeFromID(id));


        Parant_Layout.setBackgroundColor(Color.parseColor(background));
        Note_title.setTextColor(Color.parseColor(color));
        Note_body.setTextColor(Color.parseColor(color));
        Current_Creation_Datetime.setTextColor(Color.parseColor(color));

        //从Shared preferences中获取设置的详细内容
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String FontSize = sharedPreferences.getString("FontSize", "中");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.details_menu, menu);
        if (favoris.equals("yes")) { //如果笔记存在收藏的标识，则更改在MenuBar上的图标，改成黄色的星星
            mOptionsMenu.findItem(R.id.star).setIcon(R.drawable.ic_star_yellow);
            checkFavoris = true;
        } else {
            mOptionsMenu.findItem(R.id.star).setIcon(R.drawable.ic_star_border_white);
            checkFavoris = false;
        }
        if (!label.equals("no")) {  //如果笔记中存在标签，则在MenuBar上改成ic_turned_in的图标(标签图标上画了一条线)
            h_line.setVisibility(View.VISIBLE);
            LL1.setVisibility(View.VISIBLE);
            label_text_sub.setText(label);
            mOptionsMenu.findItem(R.id.label).setIcon(R.drawable.ic_turned_in);
            checkLabel = true;
        } else {
            h_line.setVisibility(View.INVISIBLE);
            LL1.setVisibility(View.INVISIBLE);
            label_text_sub.setText(label);
            mOptionsMenu.findItem(R.id.label).setIcon(R.drawable.ic_label_white);
            checkLabel = false;
        }
        if (!notification.equals("no")) { //如果笔记中存在提醒（闹铃）标识，则在MenuBar上改成ic_alarm_off的图标(闹铃图标上画了一条线)
            h_line.setVisibility(View.VISIBLE);
            LL2.setVisibility(View.VISIBLE);
            notify_text_sub.setText(notification);
            mOptionsMenu.findItem(R.id.notification).setIcon(R.drawable.ic_alarm_off);
            checkNotification = true;
        } else {
            h_line.setVisibility(View.INVISIBLE);
            LL2.setVisibility(View.INVISIBLE);
            notify_text_sub.setText(notification);
            mOptionsMenu.findItem(R.id.notification).setIcon(R.drawable.ic_set_notification_white);
            checkNotification = false;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 当details_menu.xml中的item作为的action bar被点击后，在这个方法中会自动处理action bar上Button的点击事件，
        // 只要你在AndroidManifest.xml中指定一个parent activity
        int id = item.getItemId();

        if (id == R.id.color) {  //change background color
            // 对话框
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
                    NoteDB.ChangeBackground(title, body, "#AAFFFFFF");
                    dialog.dismiss();
                }
            });
            circle_lightblue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA00BCD4"));
                    NoteDB.ChangeBackground(title, body, "#AA00BCD4");
                    dialog.dismiss();
                }
            });
            circle_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA3F51B5"));
                    NoteDB.ChangeBackground(title, body, "#AA3F51B5");
                    dialog.dismiss();
                }
            });
            circle_purple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA9C27B0"));
                    NoteDB.ChangeBackground(title, body, "#AA9C27B0");
                    dialog.dismiss();
                }
            });
            circle_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA000000"));
                    NoteDB.ChangeBackground(title, body, "#AA000000");
                    dialog.dismiss();
                }
            });
            circle_yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFF9800"));
                    NoteDB.ChangeBackground(title, body, "#AAFF9800");
                    dialog.dismiss();
                }
            });
            circle_green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AA00CC99"));
                    NoteDB.ChangeBackground(title, body, "#AA00CC99");
                    dialog.dismiss();
                }
            });
            circle_corn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFBEC5D"));
                    NoteDB.ChangeBackground(title, body, "#AAFBEC5D");
                    dialog.dismiss();
                }
            });
            circle_crystal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAA7D8DE"));
                    NoteDB.ChangeBackground(title, body, "#AAA7D8DE");
                    dialog.dismiss();
                }
            });
            circle_pink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAFF9899"));
                    NoteDB.ChangeBackground(title, body, "#AAFF9899");
                    dialog.dismiss();
                }
            });
            circle_rose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AAE91E63"));
                    NoteDB.ChangeBackground(title, body, "#AAE91E63");
                    dialog.dismiss();
                }
            });
            circle_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parant_Layout.setBackgroundColor(Color.parseColor("#AADB4437"));
                    NoteDB.ChangeBackground(title, body, "#AADB4437");
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if (id == R.id.label) {  //更改标签或者创建新的标签
            if (checkLabel == false) {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.label_popup_box);
                sp = (Spinner) dialog.findViewById(R.id.spinner);
                Button label_btn_ok = (Button) dialog.findViewById(R.id.Label_ok);
                ImageView label_add_btn = (ImageView) dialog.findViewById(R.id.label_add_btn);
                final EditText label_text = (EditText) dialog.findViewById(R.id.label_text);
                FillSpinner();
                label_add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(label_text.getText().toString())) {
                            LabelDB.insertLabel(label_text.getText().toString());
                            Toast.makeText(Text_details.this, label_text.getText().toString() + " 标签已创建", Toast.LENGTH_SHORT).show();
                            label_text.setText("");
                            FillSpinner();
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
                            NoteDB.AddToLabel(title, body, sp.getSelectedItem().toString());
                            Toast.makeText(Text_details.this, "已添加标签 : " + sp.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            h_line.setVisibility(View.VISIBLE);
                            LL1.setVisibility(View.VISIBLE);
                            label_text_sub.setText(sp.getSelectedItem().toString());
                            mOptionsMenu.findItem(R.id.label).setIcon(R.drawable.ic_turned_in);
                            checkLabel = true;
                        }
                    }
                });
                dialog.show();
            } else {
                // 进入某个文本笔记详细界面后，点击了带斜线的标签图标后，则移除标签文本并且把蓝色的标签图标改成白色的标签图标
                NoteDB.AddToLabel(title, body, "no");
                h_line.setVisibility(View.INVISIBLE);
                LL1.setVisibility(View.INVISIBLE);
                mOptionsMenu.findItem(R.id.label).setIcon(R.drawable.ic_label_white);
                checkLabel = false;
                Toast.makeText(Text_details.this, "笔记的标签已移除", Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.star) { //添加收藏或者移除收藏
            if (checkFavoris == false) {
                // 添加收藏，改变图标（变成黄色的星星）
                NoteDB.AddToFavoris(title, body, "yes");
                Toast.makeText(this, "Added To Favoris", Toast.LENGTH_SHORT).show();
                mOptionsMenu.findItem(R.id.star).setIcon(R.drawable.ic_star_yellow);
                checkFavoris = true;
            } else {
                // 移除收藏，改变图标（变成白色的星星）
                NoteDB.AddToFavoris(title, body, "no");
                Toast.makeText(this, "Note Removed From Favoris", Toast.LENGTH_SHORT).show();
                mOptionsMenu.findItem(R.id.star).setIcon(R.drawable.ic_star_border_white);
                checkFavoris = false;
            }
        }
        if (id == R.id.notification) { //添加提醒，或者移除提醒
            if (checkNotification == false) {
                // 添加提醒，并且更改图标
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
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        //***************************************************/

                        String myFormat = "yyyy年MM月dd日";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        in_date = (EditText) dialog.findViewById(R.id.in_date);
                        in_time = (EditText) dialog.findViewById(R.id.in_time);
                        in_date.setText(sdf.format(myCalendar.getTime()));
                        DateTime_ok = (Button) dialog.findViewById(R.id.DateTime_ok);
                        DateTime_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(in_date.getText().toString()) && !TextUtils.isEmpty(in_time.getText().toString())) {
                                    h_line.setVisibility(View.VISIBLE);
                                    LL2.setVisibility(View.VISIBLE);
                                    notify_text_sub.setText(in_date.getText().toString() + " " + in_time.getText().toString());
                                    NoteDB.AddToNotification(title, body, notify_text_sub.getText().toString());
                                    dialog.dismiss();
                                    mOptionsMenu.findItem(R.id.notification).setIcon(R.drawable.ic_alarm_off);
                                    Toast.makeText(Text_details.this, "提醒添加成功", Toast.LENGTH_SHORT).show();
                                    checkNotification = true;
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
                        new DatePickerDialog(Text_details.this, date, myCalendar
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
                        mTimePicker = new TimePickerDialog(Text_details.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                in_time.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

                                //给alarm manager设置提醒的日期
                                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                calendar.set(Calendar.MINUTE, selectedMinute);
                                calendar.set(Calendar.SECOND, 0);
                                //**************************************************/
                            }
                        }, hour, minute, true);
                        mTimePicker.show();
                    }
                });
                dialog.show();
            } else {
                // 移除提醒，并且改变图标
                NoteDB.AddToNotification(title, body, "no");
                h_line.setVisibility(View.INVISIBLE);
                LL2.setVisibility(View.INVISIBLE);
                mOptionsMenu.findItem(R.id.notification).setIcon(R.drawable.ic_set_notification_white);
                Toast.makeText(this, "提醒已移除", Toast.LENGTH_SHORT).show();
                checkNotification = false;

                /* Request the AlarmManager object */
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                /* Create the PendingIntent that would have launched the BroadcastReceiver */
                PendingIntent pending = PendingIntent.getBroadcast(this, 43530, new Intent(this, AlarmReceiver.class), 0);
                /* Cancel the alarm associated with that PendingIntent */
                manager.cancel(pending);
            }
        }
        if (id == R.id.delete) {  //删除这个笔记
            AlertDialog DialogBox = Confirm(); // 显示确认AlertDialog
            DialogBox.show();
        }
        if (id == R.id.share) { // 分享这个笔记
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            String title = b.getString("title");
            String body = b.getString("body");

            share.putExtra(Intent.EXTRA_TEXT, title + " \n" + body);
            startActivity(Intent.createChooser(share, "Share this Note on :"));
        }

        return super.onOptionsItemSelected(item);
    }

    public void FillSpinner() {
        //这个方法用来从table中获取所有的标签并显示在Spinner上
        list = LabelDB.getAllLabels();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    private AlertDialog Confirm() {
        AlertDialog MyDialog = new AlertDialog.Builder(this)
                .setTitle("删除确认")
                .setMessage("你确认要删除这个笔记?")
                .setIcon(R.drawable.ic_trash)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NoteDB.AddToTrash(id, "yes");
                        Toast.makeText(Text_details.this, "笔记已删除!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Text_details.this.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        return MyDialog;
    }

    @Override
    protected void onPause() {  // 当用户退出这个Activity时，它会把数据插入到NoteDBHelper中
        super.onPause();
        SaveModifications();
    }

    public void SaveModifications() {
        String NewTitleText = Note_title.getText().toString();
        String NewBodyText = Note_body.getText().toString();
        NoteDB.UpdateData(NewTitleText, NewBodyText, id);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
