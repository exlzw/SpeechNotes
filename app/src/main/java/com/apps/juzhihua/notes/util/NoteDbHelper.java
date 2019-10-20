package com.apps.juzhihua.notes.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Note_db.db";
    public static final String TABLE_NAME = "note_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "title"; // 笔记的标题文本
    public static final String COL_3 = "body";  // 笔记的具体内容文本
    public static final String COL_4 = "background"; // 背景颜色值
    public static final String COL_5 = "color"; // 文本的颜色值
    public static final String COL_6 = "notification_dt"; // 提醒的日期时间，值为{datetime or 'no'}
    public static final String COL_7 = "trash"; // 垃圾：是/否
    public static final String COL_8 = "creation_dt"; // 笔记创建的日期和具体时间
    public static final String COL_9 = "favoris"; //收藏：是/否
    public static final String COL_10 = "type"; // 种类：text/voice/list
    public static final String COL_11 = "label"; // 标签名字
    public static final String COL_12 = "voice_FileName"; // 录音文件的路径名

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建note_table
        db.execSQL("create table " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT ,body TEXT ,background TEXT,color TEXT,notification_dt TEXT,trash,TEXT,creation_dt TEXT,favoris TEXT,type TEXT,label TEXT,voice_FileName TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果存在删除表
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //insert data ==> Create Note
    public boolean insertData(String title,String body,String background,String color,String notification_dt,String trash,String creation_dt,String favoris,String type,String label,String voice_FileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,title);
        contentValues.put(COL_3,body);
        contentValues.put(COL_4,background);
        contentValues.put(COL_5,color);
        contentValues.put(COL_6,notification_dt);
        contentValues.put(COL_7,trash);
        contentValues.put(COL_8,creation_dt);
        contentValues.put(COL_9,favoris);
        contentValues.put(COL_10,type);
        contentValues.put(COL_11,label);
        contentValues.put(COL_12,voice_FileName);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }


    /******************** 所有的笔记(Show ,Count, Sort,Search ) ***********************************/
    public int CountALL() {  // Note_table的行数
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // 开始transaction.
        db.beginTransaction();
        try {
            String selectQuery = "SELECT count(*) FROM " + TABLE_NAME + " WHERE trash = 'no'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            // 结束transaction.
            db.close();
            // 关闭database
        }
        return result;
    }
    // 获取所有Notes
    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no'", null);
        return data;
    }
    //按照Creation DateTime来排序
    public Cursor SortByCreationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' ORDER BY creation_dt DESC", null);
        return data;
    }
    //按照Notification DateTime来排序
    public Cursor SortByNotificationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' ORDER BY notification_dt DESC", null);
        return data;
    }
    //按照Alphabet A-Z来排序
    public Cursor SortByalphabet_AZ() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' ORDER BY title ASC", null);
        return data;
    }
    //按照Alphabet Z-A来排序
    public Cursor SortByalphabet_ZA() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' ORDER BY title DESC", null);
        return data;
    }
    //搜索，从所有的笔记中筛选结果
    public Cursor Main_Search(String value) {  // Search Notes using value entered from SearchView
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' and title LIKE '%"+value+"%' ", null);
        return data;
    }

    /******************** 收藏(Show ,Count, Sort,Search ) ***********************************/
    //只在Favoris Activity中用到
    public int CountFavoris() {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // 开始transaction.
        db.beginTransaction();
        try {
            String selectQuery = "SELECT count(*) FROM " + TABLE_NAME + " WHERE favoris == 'yes' and trash = 'no'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            // 结束the transaction.
            db.close();
            // 关闭database
        }
        return result;// Favoris的行数
    }
    // 获取所有的Favoris笔记
    public Cursor Favoris_getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME  +" WHERE favoris = 'yes' and trash = 'no'", null);
        return data;
    }
    //按照Creation DateTime来排序
    public Cursor Favoris_SortByCreationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE favoris = 'yes' and trash = 'no' ORDER BY creation_dt DESC", null);
        return data;
    }
    //按照Notification DateTime来排序
    public Cursor Favoris_SortByNotificationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE favoris = 'yes'  ORDER BY notification_dt DESC", null);
        return data;
    }
    //按照Alphabet A-Z来排序
    public Cursor Favoris_SortByalphabet_AZ() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE favoris = 'yes' and trash = 'no'  ORDER BY title ASC", null);
        return data;
    }
    //按照Alphabet Z-A来排序
    public Cursor Favoris_SortByalphabet_ZA() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE favoris = 'yes' and trash = 'no'  ORDER BY title DESC", null);
        return data;
    }
    //搜索，从所有的收藏笔记中筛选结果
    public Cursor Favoris_Search(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE favoris = 'yes' and trash = 'no' and title LIKE '%"+value+"%' ", null);
        return data;
    }

    /******************** 提醒(Show ,Count, Sort,Search ) ***********************************/
    //只用在Notifications中
    public int CountNotification() {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // 开始transaction.
        db.beginTransaction();
        try {
            String selectQuery = "SELECT count(*) FROM " + TABLE_NAME +" WHERE notification_dt != 'no' and trash = 'no'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            // 结束transaction.
            db.close();
            // 关闭database
        }
        return result;
    }
    public Cursor Notifications_getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME  +" WHERE notification_dt != 'no' and trash = 'no'", null);
        return data;
    }
    public Cursor Notifications_SortByCreationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE notification_dt != 'no' and trash = 'no' ORDER BY creation_dt DESC", null);
        return data;
    }
    public Cursor Notifications_SortByNotificationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE notification_dt != 'no' and trash = 'no'  ORDER BY notification_dt DESC", null);
        return data;
    }
    public Cursor Notifications_SortByalphabet_AZ() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE notification_dt != 'no' and trash = 'no'  ORDER BY title ASC", null);
        return data;
    }
    public Cursor Notifications_SortByalphabet_ZA() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE notification_dt != 'no' and trash = 'no'  ORDER BY title DESC", null);
        return data;
    }
    public Cursor Notifications_Search(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' and notification_dt != 'no' and title LIKE '%"+value+"%' ", null);
        return data;
    }

    /******************** 垃圾箱(Show ,Count, Sort,Search,DeleteAllNoteInTrash ) ***********************************/
    //只在Trash中用到
    public int Counttrash() {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // 开始transaction.
        db.beginTransaction();
        try {
            String selectQuery = "SELECT count(*) FROM " + TABLE_NAME +" WHERE trash = 'yes'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            // 结束transaction.
            db.close();
            // 关闭database
        }
        return result;
    }
    public Cursor Trash_getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME  +" WHERE trash = 'yes'", null);
        return data;
    }
    public Cursor Trash_SortByCreationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'yes' ORDER BY creation_dt DESC", null);
        return data;
    }
    public Cursor Trash_SortByNotificationDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'yes'  ORDER BY notification_dt DESC", null);
        return data;
    }
    public Cursor Trash_SortByalphabet_AZ() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'yes'  ORDER BY title ASC", null);
        return data;
    }
    public Cursor Trash_SortByalphabet_ZA() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'yes'  ORDER BY title DESC", null);
        return data;
    }
    public Cursor Trash_Search(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'yes' and title LIKE '%"+value+"%' ", null);
        return data;
    }

    /******************** 详细标签(Show , Sort,Search ) ***********************************/
    //只在LabelDetails中用到
    public Cursor Label_getListContents(String key){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME  +" WHERE trash = 'no' and label='"+key+"'", null);
        return data;
    }
    public Cursor Label_SortByCreationDate(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE  trash = 'no' and label='"+key+"' ORDER BY creation_dt DESC", null);
        return data;
    }
    public Cursor Label_SortByNotificationDate(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' and label='"+key+"'  ORDER BY notification_dt DESC", null);
        return data;
    }
    public Cursor Label_SortByalphabet_AZ(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' and label='"+key+"'  ORDER BY title ASC", null);
        return data;
    }
    public Cursor Label_SortByalphabet_ZA(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' and label='"+key+"'  ORDER BY title DESC", null);
        return data;
    }
    public Cursor Label_Search(String value,String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE trash = 'no' and label='"+key+"'  and title LIKE '%"+value+"%' ", null);
        return data;
    }

    //更新标签的名字--只在Label Activity中用到
    public boolean updateLabel_Name(String name,String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11,val);
        db.update(TABLE_NAME, contentValues, "label = ?",new String[] { name });
        return true;
    }

    //恢复已删除的文件 ==> 值从 yes 变成 no
    public boolean RecoverFromTrash(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7,"no");
        db.update(TABLE_NAME, contentValues, "id = ?",new String[] { String.valueOf(id) });
        return true;
    }

    //改变背景颜色
    public boolean ChangeBackground(String title,String body,String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4,val);
        db.update(TABLE_NAME, contentValues, "title = ? and body = ?",new String[] { title,body });
        return true;
    }

    //添加笔记到我的收藏或者从我的收藏中移除
    public boolean AddToFavoris(String title,String body,String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_9,val);
        db.update(TABLE_NAME, contentValues, "title = ? and body = ?",new String[] { title,body });
        return true;
    }

    //把笔记移到垃圾箱
    public boolean AddToTrash(int id,String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7,val);
        db.update(TABLE_NAME, contentValues, "id = ?",new String[] { String.valueOf(id) });
        return true;
    }

    //为笔记添加或者移除提醒的日期时间
    public boolean AddToNotification(String title,String body,String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6,val);
        db.update(TABLE_NAME, contentValues, "title = ? and body = ?",new String[] { title,body });
        return true;
    }

    //为笔记添加或移除已有的标签
    public boolean AddToLabel(String title,String body,String val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11,val);
        db.update(TABLE_NAME, contentValues, "title = ? and body = ?",new String[] { title,body });
        return true;
    }

    //更新笔记的标题文本和内容文本
    public boolean UpdateData(String title,String body,int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,title);
        contentValues.put(COL_3,body);
        db.update(TABLE_NAME, contentValues, "id = ?",new String[] { String.valueOf(id) });
        return true;
    }

    //获取创建笔记的日期时间
    public String getCreationDateTimeFromID(int id)
    {
        String value = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT creation_dt FROM " + TABLE_NAME  +" WHERE id = '"+id+"'", null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return value;
    }

    //永久删除笔记
    public Integer deleteData (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ?",new String[] {String.valueOf(id)});

    }

    //从title中获取到ID
    public String getIDFromTitle(String title)
    {
        String value = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME  +" WHERE title = '"+title+"' and trash ='no' and type = 'list' ", null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return value;
    }
}