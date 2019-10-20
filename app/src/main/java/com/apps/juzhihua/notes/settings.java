package com.apps.juzhihua.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class settings extends AppCompatActivity {
    RelativeLayout Background_color, Text_color, Notification,
            Background_color_square, Text_color_square;
    ImageView circle_white, circle_lightblue, circle_blue, circle_purple,
            circle_black, circle_yellow, circle_green, circle_corn,
            circle_crystal, circle_pink, circle_rose, circle_red;
    ImageView text_white, text_lightblue, text_blue, text_purple,
            text_black, text_yellow, text_green, text_corn,
            text_crystal, text_pink, text_rose, text_red;
    private String[] String_Table = new String[4]; // 0 => background , 1 => Color , 2 => status Bar , 3 => Notification
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Spinner spin;

    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Background_color = (RelativeLayout) findViewById(R.id.Background_color);
        Text_color = (RelativeLayout) findViewById(R.id.Text_color);
        Notification = (RelativeLayout) findViewById(R.id.Notification);
        Background_color_square = (RelativeLayout) findViewById(R.id.Background_color_square);
        Text_color_square = (RelativeLayout) findViewById(R.id.Text_color_square);
        spin = (Spinner) findViewById((R.id.Spinner));
        text = (TextView) findViewById(R.id.text);
        //Save view mode in share reference
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        String background = sharedPreferences.getString("Background", "#FFFFFF");
        String Textcolor = sharedPreferences.getString("TextColor", "#000000");
        String FontSize = sharedPreferences.getString("FontSize", "medium");

        if (FontSize.equals("小")) {
            spin.setSelection(0);
        }
        if (FontSize.equals("中")) {
            spin.setSelection(1);
        }
        if (FontSize.equals("大")) {
            spin.setSelection(2);
        }


        Background_color_square.setBackgroundColor(Color.parseColor(background));
        Text_color_square.setBackgroundColor(Color.parseColor(Textcolor));


        Background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(settings.this);
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
                        Background_color_square.setBackgroundColor(Color.parseColor("#AAFFFFFF"));
                        String_Table[0] = "#AAFFFFFF";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_lightblue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AA00BCD4"));
                        String_Table[0] = "#AA00BCD4";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_blue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AA3F51B5"));
                        String_Table[0] = "#AA3F51B5";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_purple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AA9C27B0"));
                        String_Table[0] = "#AA9C27B0";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_black.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AA000000"));
                        String_Table[0] = "#AA000000";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_yellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AAFF9800"));
                        String_Table[0] = "#AAFF9800";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_green.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AA00CC99"));
                        String_Table[0] = "#AA00CC99";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_corn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AAFBEC5D"));
                        String_Table[0] = "#AAFBEC5D";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_crystal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AAA7D8DE"));
                        String_Table[0] = "#AAA7D8DE";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_pink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AAFF9899"));
                        String_Table[0] = "#AAFF9899";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_rose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AAE91E63"));
                        String_Table[0] = "#AAE91E63";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                circle_red.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Background_color_square.setBackgroundColor(Color.parseColor("#AADB4437"));
                        String_Table[0] = "#AADB4437";
                        editor.putString("Background", String_Table[0]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        Text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(settings.this);
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
                        Text_color_square.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        String_Table[1] = "#FFFFFF";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_lightblue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#00BCD4"));
                        String_Table[1] = "#00BCD4";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_blue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#3F51B5"));
                        String_Table[1] = "#3F51B5";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_purple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#9C27B0"));
                        String_Table[1] = "#9C27B0";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_black.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#000000"));
                        String_Table[1] = "#000000";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_yellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#FF9800"));
                        String_Table[1] = "#FF9800";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_green.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#00CC99"));
                        String_Table[1] = "#00CC99";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_corn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#FBEC5D"));
                        String_Table[1] = "#FBEC5D";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_crystal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#A7D8DE"));
                        String_Table[1] = "#A7D8DE";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_pink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#FF9899"));
                        String_Table[1] = "#FF9899";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_rose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#E91E63"));
                        String_Table[1] = "#E91E63";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                text_red.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Text_color_square.setBackgroundColor(Color.parseColor("#DB4437"));
                        String_Table[1] = "#DB4437";
                        editor.putString("TextColor", String_Table[1]);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String_Table[2] = "小";
                        editor.putString("FontSize", String_Table[2]);
                        editor.commit();
                        break;
                    case 1:
                        String_Table[2] = "中";
                        editor.putString("FontSize", String_Table[2]);
                        editor.commit();
                        break;
                    case 2:
                        String_Table[2] = "大";
                        editor.putString("FontSize", String_Table[2]);
                        editor.commit();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "保存更改中 ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                Toast.makeText(this, "" + uri.toString(), Toast.LENGTH_SHORT).show();
                String_Table[3] = uri.toString();
                editor.putString("NotificationSound", String_Table[3]);
                editor.commit();
            }
        }
    }

    /*
    @Override
    protected void onPause()
    {
        super.onPause();
        Toast.makeText(this, "Background : "+String_Table[0]+" texte color : "+String_Table[1] + " StatusBar : "+String_Table[2] + " Notification : "+String_Table[4], Toast.LENGTH_SHORT).show();
    }
    */
}
