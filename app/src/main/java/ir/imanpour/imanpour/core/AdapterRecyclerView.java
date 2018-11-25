package ir.imanpour.imanpour.core;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.component.WebActivity;

import static ir.imanpour.imanpour.core.G.sqLiteDatabase;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {
  LayoutInflater layoutInflater;
  private ArrayList<RssParser.Item> arrayList = new ArrayList<>();

  public AdapterRecyclerView() {
    setNewlist();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return new ViewHolder(layoutInflater.inflate(R.layout.adapter, viewGroup, false));
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    layoutInflater = LayoutInflater.from(recyclerView.getContext());
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    final RssParser.Item item = arrayList.get(position);
    holder.txt_title.setText(item.title);
    String[] strings = item.pubDate.split(" ");
    String[] s = item.enclosure.split("/");
    final String file = G.APP_DIR + "/" + s[s.length - 1];
    holder.root.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sqLiteDatabase.execSQL("UPDATE Rss SET unread=1 where TRIM(link) = '" + item.link + "'");
        Intent intent1 = new Intent(G.context, WebActivity.class);
        intent1.putExtra(G.WEB_INTENT, item.link);
        G.context.startActivity(intent1);
      }
    });
    try {
      Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(strings[2]);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int month = calendar.get(Calendar.MONTH) + 1;
      strings[2] = "" + month;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    holder.txt_putdate.setText(strings[3] + "/" + strings[2] + "/" + strings[1]);
    try {
      if (new File(file).exists()) {
        Bitmap bitmap = BitmapFactory.decodeFile(file);
        bitmap = getCroppedBitmap(bitmap);
        holder.imageView.setImageBitmap(bitmap);
      } else {
        VolleySingletone.getInstance(G.context).addToRequestQueue(new ImageRequest(item.enclosure, new Response.Listener<Bitmap>() {
          @Override
          public void onResponse(Bitmap response) {
            try {
              response.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
              response = getCroppedBitmap(response);
              holder.imageView.setImageBitmap(response);
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            }
          }
        }, 0, 0, null, Bitmap.Config.ARGB_8888, null));
        holder.imageView.setImageResource(0);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    @SuppressLint("Recycle") final Cursor cursor = sqLiteDatabase.rawQuery("select * from Rss where  TRIM(link) = '" + item.link + "'", null);
    cursor.moveToFirst();
    final int i = cursor.getInt(cursor.getColumnIndex("like"));
    cursor.close();
    item.like = i;
    holder.chkb_like.setChecked((item.like == 1));
    holder.chkb_like.setChecked((item.like == 1));
    holder.chkb_like.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (holder.chkb_like.isChecked()) {
          holder.chkb_like.setChecked(true);
          sqLiteDatabase.execSQL("UPDATE Rss SET like=1 where TRIM(link) = '" + item.link + "'");
        } else {
          holder.chkb_like.setChecked(false);
          sqLiteDatabase.execSQL("UPDATE Rss SET like=0 where TRIM(link) = '" + item.link + "'");
        }
        @SuppressLint("Recycle") Cursor num = sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
        int n = num.getCount();
        cursor.close();
      }
    });
  }

  public Bitmap getCroppedBitmap(Bitmap bitmap) {
    float x = bitmap.getWidth();
    float y = bitmap.getHeight();
    Bitmap output = Bitmap.createBitmap((int) x, (int) y, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    final Paint paint = new Paint();
    paint.setAntiAlias(true);
    canvas.drawCircle(x / 2, y / 2, x / 2, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, 0, 0, paint);
    return output;
  }

  public void setNewlist() {
    setNewlist(0);
  }

  public void setNewlist(int mode) {
    switch (mode){
      case 0:
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from RSS", null);
        arrayList.clear();
        arrayList = DataBaseHelper.convertCoursorToArray(cursor);
        cursor.close();
        notifyDataSetChanged();
        break;
      case G.LIKE:
        @SuppressLint("Recycle")Cursor cursor1 = G.sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
        arrayList.clear();
        arrayList = DataBaseHelper.convertCoursorToArray(cursor1);
        Log.i("test","arraylist size k=like"+cursor1.getCount());
        cursor1.close();
        notifyDataSetChanged();
        break;
      case G.UNREAD:
        @SuppressLint("Recycle") Cursor cursor2 = G.sqLiteDatabase.rawQuery("select * from Rss where unread=0", null);
        arrayList.clear();
        arrayList = DataBaseHelper.convertCoursorToArray(cursor2);
        Log.i("test","arraylist size unread"+cursor2.getCount());
        cursor2.close();
        notifyDataSetChanged();
        break;
    }
  }
  public void reset(){
    arrayList.clear();
    notifyDataSetChanged();
  }
  @Override
  public int getItemCount() {
    return arrayList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView txt_title;
    // TextView txt_description;
    ImageView imageView;
    View root;
    TextView txt_putdate;
    CheckBox chkb_like;

    public ViewHolder(View view) {
      super(view);
      root = view;
      chkb_like = view.findViewById(R.id.chk_like);
      txt_putdate = view.findViewById(R.id.txt_date);
      txt_title = view.findViewById(R.id.txt_title);
      imageView = view.findViewById(R.id.image);
      // txt_description = view.findViewById(R.id.txt_descrpition);
    }
  }
}
