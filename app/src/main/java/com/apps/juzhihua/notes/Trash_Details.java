package com.apps.juzhihua.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.juzhihua.notes.util.LabelDbHelper;
import com.apps.juzhihua.notes.util.NoteDbHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Trash_Details extends AppCompatActivity {
    EditText Note_title, Note_body, Current_Creation_Datetime;
    LinearLayout LL1, LL2;
    TextView label_text_sub, notify_text_sub;
    RelativeLayout Parant_Layout, Play_Content;
    View h_line;
    NoteDbHelper NoteDB;
    LabelDbHelper LabelDB;
    String title, body, background, color, favoris, label, notification, type, VoiceFileName;
    Bundle b;
    ImageView play_btn, pause_btn, delete_record;
    TextView current_time, Totale_time;
    android.widget.SeekBar SeekBar;

    MediaPlayer mPlayer;
    private static String mFileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    public static int oneTimeOnly = 0;
    File file;
    int id;
    private List<labelClass> ListOfItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trash_details);

        NoteDB = new NoteDbHelper(this);
        LabelDB = new LabelDbHelper(this);

        //FindViewByID
        initView();

        b = getIntent().getExtras();
        id = b.getInt("id");
        title = b.getString("title");
        body = b.getString("body");
        type = b.getString("type");
        background = b.getString("background");
        color = b.getString("color");
        favoris = b.getString("favoris");
        label = b.getString("label");
        notification = b.getString("notification");

        Note_title.setText(title);
        Note_body.setText(body);
        Parant_Layout.setBackgroundColor(Color.parseColor(background));
        Note_title.setTextColor(Color.parseColor(color));
        Note_body.setTextColor(Color.parseColor(color));
        try {
            if (type.equals("录音")) {
                VoiceFileName = b.getString("VoiceFileName").toString();
                //show player Layout
                Play_Content.setVisibility(View.VISIBLE);
                mFileName = b.getString("VoiceFileName");
                mPlayer = new MediaPlayer();
                try {
                    file = new File(mFileName);
                    if (file.exists()) {
                        mPlayer.setDataSource(mFileName);
                        mPlayer.prepare();
                    } else {
                        mFileName = "no";
                        Toast.makeText(this, "录音文件不存在", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                finalTime = mPlayer.getDuration();
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
            } else {
                Play_Content.setVisibility(View.INVISIBLE);
            }
            if (type.equals("清单")) {
                ListOfItems = new ArrayList<>();
                Cursor data = LabelDB.getItemsContents(id);
                if (data.getCount() == 0) {
                    //no data founds
                } else {
                    while (data.moveToNext()) {
                        ListOfItems.add(new labelClass(data.getInt(0), data.getString(1)));
                    }
                    String toPrint = "";
                    for (int i = 0; i < ListOfItems.size(); i++) {
                        toPrint += "\u2022 " + ListOfItems.get(i).getTitle() + " \n";
                    }
                    Note_body.setText(toPrint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!label.equals("no")) {
            h_line.setVisibility(View.VISIBLE);
            LL1.setVisibility(View.VISIBLE);
            label_text_sub.setText(label);
        }
        if (!notification.equals("no")) {
            h_line.setVisibility(View.VISIBLE);
            LL2.setVisibility(View.VISIBLE);
            notify_text_sub.setText(notification);
        }

    }

    private void initView() {
        Parant_Layout = (RelativeLayout) findViewById(R.id.Parant_Layout);
        Note_title = (EditText) findViewById(R.id.Note_title);
        Note_body = (EditText) findViewById(R.id.Note_body);
        LL1 = (LinearLayout) findViewById(R.id.LL1);
        LL2 = (LinearLayout) findViewById(R.id.LL2);
        label_text_sub = (TextView) findViewById(R.id.label_text_sub);
        notify_text_sub = (TextView) findViewById(R.id.notify_text_sub);
        h_line = (View) findViewById(R.id.h_line);
        Play_Content = (RelativeLayout) findViewById(R.id.Play_Content);
        play_btn = (ImageView) findViewById(R.id.play_btn);
        pause_btn = (ImageView) findViewById(R.id.pause_btn);
        current_time = (TextView) findViewById(R.id.current_time);
        Totale_time = (TextView) findViewById(R.id.Totale_time);
        SeekBar = (android.widget.SeekBar) findViewById(R.id.SeekBar);
    }

    private void startPlaying() {
        play_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.VISIBLE);

        try {
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    myHandler.removeCallbacks(UpdateSongTime);
                    pause_btn.setVisibility(View.INVISIBLE);
                    play_btn.setVisibility(View.VISIBLE);
                    SeekBar.setProgress(0);
                    current_time.setText("00 : 00");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        finalTime = mPlayer.getDuration();
        startTime = mPlayer.getCurrentPosition();
        SeekBar.setProgress((int) startTime);
        if (oneTimeOnly == 0) {
            SeekBar.setMax((int) finalTime);
        }
        try {
            myHandler.postDelayed(UpdateSongTime, 100);
        } catch (Exception e) {
        }
    }

    private void stopPlaying() {
        mPlayer.pause();
        pause_btn.setVisibility(View.INVISIBLE);
        play_btn.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.recover) {
            AlertDialog DialogBox = ConfirmRecover();
            DialogBox.show();
        }
        if (id == R.id.delete) {
            AlertDialog DialogBox = ConfirmDelete();
            DialogBox.show();
        }
        if (id == R.id.share) { // Share this note
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            if (type.equals("list")) {
                String toPrint = "";
                for (int i = 0; i < ListOfItems.size(); i++) {
                    toPrint += "\u2022 " + ListOfItems.get(i).getTitle().toString() + " \n";
                }
                share.putExtra(Intent.EXTRA_TEXT, title + " \n" + toPrint);
                startActivity(Intent.createChooser(share, "Share this Note on :"));
            } else {
                share.putExtra(Intent.EXTRA_TEXT, title + " \n" + body);
                startActivity(Intent.createChooser(share, "Share this Note on :"));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog ConfirmDelete() {
        AlertDialog MyDialog = new AlertDialog.Builder(this)
                .setTitle("删除确认")
                .setMessage("你确认要彻底删除这个笔记 ?")
                .setIcon(R.drawable.ic_trash)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NoteDB.deleteData(id);
                        dialog.dismiss();
                        Trash_Details.this.finish();
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

    private AlertDialog ConfirmRecover() {
        AlertDialog MyDialog = new AlertDialog.Builder(this)
                .setTitle("恢复确认")
                .setMessage("你确认要恢复这个笔记?")
                .setIcon(R.drawable.ic_recover_blue)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NoteDB.RecoverFromTrash(id);
                        dialog.dismiss();
                        Trash_Details.this.finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create();
        return MyDialog;
    }
}
