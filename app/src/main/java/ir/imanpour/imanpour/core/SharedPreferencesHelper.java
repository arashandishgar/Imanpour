package ir.imanpour.imanpour.core;

import android.content.SharedPreferences;

public class SharedPreferencesHelper {
  public static boolean getBoolean(SharedPreferences sharedPreferences,String key,boolean defualt){
    return sharedPreferences.getBoolean(key,defualt);
  }
  public static void setBoolean(SharedPreferences sharedPreferences,String key,boolean value){
    SharedPreferences.Editor editor= sharedPreferences.edit();
    editor.putBoolean(key,value);
    editor.commit();
  }
  public static String getString(SharedPreferences sharedPreferences,String key,String  defualt){
    return sharedPreferences.getString(key,defualt);
  }
  public static void setString(SharedPreferences sharedPreferences,String key,String  value){
    SharedPreferences.Editor editor= sharedPreferences.edit();
    editor.putString(key,value);
    editor.commit();
  }
  public static Integer getInteger(SharedPreferences sharedPreferences,String key,Integer defualt){
    return sharedPreferences.getInt(key,defualt);
  }
  public static void setInteger(SharedPreferences sharedPreferences,String key,Integer  value){
    SharedPreferences.Editor editor= sharedPreferences.edit();
    editor.putInt(key,value);
    editor.commit();
  }
}
