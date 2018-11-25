package ir.imanpour.imanpour.widjet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.DataBaseHelper;
import ir.imanpour.imanpour.core.G;
import ir.imanpour.imanpour.core.RssParser;

import static ir.imanpour.imanpour.core.G.sqLiteDatabase;


public class FeedAapter extends RecyclerView.Adapter<FeedAapter.ViewHolder> {
  private ArrayList<RssParser.Item> arrayList = new ArrayList<>();
  private LayoutInflater layoutInflater;
  private Context context;

  public FeedAapter() {
    setNewlist();
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    context = recyclerView.getContext();
    layoutInflater = LayoutInflater.from(context);
  }

  public FeedAapter(ArrayList arrayList) {
    this.arrayList = arrayList;
  }
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(layoutInflater.inflate(R.layout.adapter, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    final RssParser.Item item = arrayList.get(position);
    String[] s = item.enclosure.split("/");
    final String file = G.APP_DIR + "/" + s[s.length - 1];
    @SuppressLint("Recycle") final Cursor cursor = sqLiteDatabase.rawQuery("select * from Rss where  TRIM(link) = '" + item.link + "'", null);
    cursor.moveToFirst();
    final int i = cursor.getInt(cursor.getColumnIndex("like"));
    cursor.close();
    item.like = i;
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
    holder.txt_title.setText(item.title);
    try {
      if (new File(file).exists()) {
        Bitmap bitmap = BitmapFactory.decodeFile(file);
        holder.imageView.setImageBitmap(bitmap);
      } else {
        holder.imageView.setImageResource(0);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    holder.txt_putdate.setText(item.pubDate);
    String[] strings = item.pubDate.split(" ");
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
    sqLiteDatabase.execSQL("UPDATE Rss SET unread=1 where TRIM(link) = '" + item.link + "'");
    Cursor cursor1 = sqLiteDatabase.rawQuery("select * from Rss  where unread=0", null);
    final int count = cursor1.getCount();
    cursor1.close();

    /*if (unread != null) {
      unread.read(count);
    }*/
  }
  private Uri uri(String  path){
    Bitmap bitmap=BitmapFactory.decodeFile(path);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String s = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
    return Uri.parse(s);
  }
  @SuppressWarnings("deprecation")
  public static Spanned fromHtml(String html) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH);
    } else {
      return Html.fromHtml(html);
    }
  }

  @Override
  public int getItemCount() {
    return arrayList.size();
  }

  public void setNewlist() {
    @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from RSS", null);
    arrayList.clear();
    arrayList = DataBaseHelper.convertCoursorToArray(cursor);
    notifyDataSetChanged();
  }


  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView txt_title;
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
    }
  }
}
