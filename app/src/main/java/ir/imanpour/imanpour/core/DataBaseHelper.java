package ir.imanpour.imanpour.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DataBaseHelper extends SQLiteOpenHelper {
  public DataBaseHelper(Context context, String name) {
    super(context, name, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE  TABLE  IF NOT EXISTS RSS (" +
      " title TEXT, description VARCHAR," +
      " link TEXT, category TEXT, pubDate TEXT, guid TEXT," +
      " enclosure TEXT, creator TEXT, like INTEGER," +
      "notify INTEGER, unread INTEGER,unique (link))");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
  public static ArrayList<RssParser.Item> convertCoursorToArray(Cursor cursor){
    ArrayList<RssParser.Item> items=new ArrayList<>();
    while (cursor.moveToNext()){
      RssParser.Item item = new RssParser.Item();
      item.description = cursor.getString(cursor.getColumnIndex("description"));
      item.link = cursor.getString(cursor.getColumnIndex("link"));
      item.category = cursor.getString(cursor.getColumnIndex("category"));
      item.pubDate = cursor.getString(cursor.getColumnIndex("pubDate"));
      item.guid = cursor.getString(cursor.getColumnIndex("guid"));
      item.enclosure = cursor.getString(cursor.getColumnIndex("enclosure"));
      item.creator = cursor.getString(cursor.getColumnIndex("creator"));
      item.title = cursor.getString(cursor.getColumnIndex("title"));
      item.like = cursor.getInt(cursor.getColumnIndex("like"));
      items.add(item);
    }
    cursor.close();
    return items;
  }
}
