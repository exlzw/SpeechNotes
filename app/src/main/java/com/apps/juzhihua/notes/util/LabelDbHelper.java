package com.apps.juzhihua.notes.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LabelDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Note_db";
    public static final String TABLE_label = "label_table";
    public static final String TABLE_LIST = "list_table";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_RPO = "CREATE TABLE IF NOT EXISTS "+ TABLE_label+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NULL UNIQUE)";
    public static final String DELETE_PRO="DROP TABLE IF EXISTS " + TABLE_label;
    public static final String CREATE_LIST = "CREATE TABLE IF NOT EXISTS "+ TABLE_LIST+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, items TEXT NULL ,checked TEXT, note_id INT)";
    public static final String DELETE_LIST="DROP TABLE IF EXISTS " + TABLE_LIST;
    public LabelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(CREATE_RPO);
        db.execSQL(CREATE_LIST);
    }
    //升级数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DELETE_PRO);
        db.execSQL(DELETE_LIST);
        //再次创建表
        onCreate(db);
    }

    /***************************** 标签表*****************************/
    public void insertLabel(String pname) {
        // 打开数据库进行写操作
        SQLiteDatabase db = this.getWritableDatabase();
        // Start the transaction.
        db.beginTransaction();
        ContentValues values;
        try
        {
            values = new ContentValues();
            values.put("name", pname);
            // 插入行
            db.insert(TABLE_label, null, values);
            // 成功插入数据库
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 启动transaction（事务）
            db.endTransaction();
            // 关闭数据库
            db.close();
        }
    }

    public ArrayList<String> getAllLabels(){
        ArrayList<String> list=new ArrayList<String>();
        // 打开数据库进行读操作
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try
        {
            String selectQuery = "SELECT * FROM "+ TABLE_label;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.getCount() >0)
            {
                while (cursor.moveToNext()) {
                    String pname= cursor.getString(cursor.getColumnIndex("name"));
                    list.add(pname);
                }
            }
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // End the transaction.
            db.endTransaction();
            // 关闭数据库
            db.close();
        }
        return list;
    }
    public Cursor getLabelContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_label +"  ORDER BY name", null);
        return data;
    }
    public Integer deleteData (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_label, "id = ?",new String[]{String.valueOf(id)});
    }
    public boolean updateData(int id,String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name",title);
        db.update(TABLE_label, contentValues, "id = ?",new String[]{String.valueOf(id)});
        return true;
    }

    public int CountALL() {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // 启动transaction（事务）
        db.beginTransaction();
        try {
            String selectQuery = "SELECT count(*) FROM " + TABLE_label;
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
            db.close();
        }
        return result;
    }

    /***************************** 清单表 *****************************/
    public void insertItem(String item,int id) {
        // 打开数据库进行写操作
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;
        try
        {
            values = new ContentValues();
            values.put("items", item);
            values.put("checked", "no");
            values.put("note_id", id);
            // 插入行
            db.insert(TABLE_LIST, null, values);
            // 成功插入数据库
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            //结束transaction.
            db.close();
            // 关闭数据库
        }
    }

    public Cursor getItemsContents(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_LIST +" WHERE note_id = "+id+" ORDER BY items DESC", null);
        return data;
    }

    public Integer deleteItem (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LIST, "id = ?",new String[]{String.valueOf(id)});
    }

    public Integer deleteItemsFromList (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LIST, "note_id = ?",new String[]{String.valueOf(id)});
    }

    public boolean updateItem(int id,String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("items",item);
        db.update(TABLE_LIST, contentValues, "id = ?",new String[]{String.valueOf(id)});
        return true;
    }

    public boolean StrikeThrough(String item,String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("checked",value);
        db.update(TABLE_LIST, contentValues, "items = ?",new String[]{String.valueOf(item)});
        return true;
    }

    //获取创建的日期事件
    public String getCheckFromID(int id)
    {
        String value = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT checked FROM " + TABLE_LIST  +" WHERE id = '"+id+"'", null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do {
                value = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return value;
    }


    //update note_id where equal to 0 by the new value
    public boolean updateNoteIDByNewID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("note_id",id);
        db.update(TABLE_LIST, contentValues, "note_id = ?",new String[] {String.valueOf(0)});
        return true;
    }
}