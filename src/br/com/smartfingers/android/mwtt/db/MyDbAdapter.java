package br.com.smartfingers.android.mwtt.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.smartfingers.android.mwtt.entity.TimeTrack;

public class MyDbAdapter {

    private static final String DATABASE_TABLE = "time_tracks";
    public static final String KEY_ROWID = "_id";
	public static final String KEY_ENTRY_DATE = "entry_date";
	public static final String KEY_HOUR_IN = "hour_in";
	public static final String KEY_MINUTE_IN = "minute_in";
	public static final String KEY_HOUR_OUT = "hour_out";
	public static final String KEY_MINUTE_OUT = "minute_out";
	public static final String KEY_HOUR_LUNCH = "hour_lunch";
	public static final String KEY_MINUTE_LUNCH = "minute_lunch";
	
	public static final String[] columns = new String[] {
			KEY_ROWID, "date(" + KEY_ENTRY_DATE + ") as " + KEY_ENTRY_DATE, 
    		KEY_HOUR_IN, KEY_MINUTE_IN,
            KEY_HOUR_OUT, KEY_MINUTE_OUT, 
            KEY_HOUR_LUNCH, KEY_MINUTE_LUNCH}; 

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    private static final String TAG = "MyDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    public MyDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    public MyDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public long createTimeTrack(TimeTrack tt) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ENTRY_DATE, sdf.format(tt.date));
        initialValues.put(KEY_HOUR_IN, tt.hourIn);
        initialValues.put(KEY_MINUTE_IN, tt.minuteIn);
        initialValues.put(KEY_HOUR_OUT, tt.hourOut);
        initialValues.put(KEY_MINUTE_OUT, tt.minuteOut);
        initialValues.put(KEY_HOUR_LUNCH, tt.hourLunch);
        initialValues.put(KEY_MINUTE_LUNCH, tt.minuteLunch);

        int id = (int) mDb.insert(DATABASE_TABLE, null, initialValues);
        tt.id = id;
        
		return id;
    }
    
    public boolean deleteAll() {
        return mDb.delete(DATABASE_TABLE, null, null) > 0;
    }
    
    public boolean deleteTimeTrack(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + " = " + rowId, null) > 0;
    }
    
    public boolean deleteTodayTimeTrack() {
        return mDb.delete(DATABASE_TABLE, KEY_ENTRY_DATE + " = ?", new String[]{getTodayDateFormated()}) > 0;
    }
    
    public Cursor fetchAllTimeTracks() {
        return mDb.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ENTRY_DATE + " desc");
    }
    
    public Cursor fetchLimitedTimeTracks(int limit) {
    	return mDb.query(DATABASE_TABLE, columns, null, null, null, null, KEY_ENTRY_DATE + " desc", Integer.toString(limit));
    }
    
    public Cursor fetchTimeTrack(long rowId) throws SQLException {
        String selection = KEY_ROWID + "=" + rowId;
		Cursor mCursor = mDb.query(DATABASE_TABLE, columns, selection, null, 
				null, null, KEY_ENTRY_DATE + " desc");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;
    }
    
    public TimeTrack fetchTimeTrack(Date date) throws SQLException {
        String selection = KEY_ENTRY_DATE + "= ?";
		Cursor mCursor = mDb.query(DATABASE_TABLE, columns, selection, new String[]{sdf.format(date)}, 
				null, null, KEY_ENTRY_DATE + " desc");
		TimeTrack tt = null;
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            tt = populateTimeTrack(mCursor);
        }
        mCursor.close();
        
        return tt;
    }
    
    public TimeTrack fetchTodayTimeTrack() throws SQLException {
        String selection = KEY_ENTRY_DATE + " = ?";
		Cursor mCursor = mDb.query(DATABASE_TABLE, columns, selection, new String[]{getTodayDateFormated()}, 
				null, null, KEY_ENTRY_DATE + " desc");
		
		TimeTrack tt = null;
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            tt = populateTimeTrack(mCursor);
        }
        mCursor.close();
        
        return tt;
    }
    
    public boolean updateTimeTrackLunch(TimeTrack tt) {
    	TimeTrack temp = fetchTodayTimeTrack();
    	
    	if(temp != null){
	        ContentValues args = new ContentValues();
	        args.put(KEY_HOUR_LUNCH, tt.hourLunch);
	        args.put(KEY_MINUTE_LUNCH, tt.minuteLunch);

	        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + temp.id, null) > 0;
    	}
        
        return false;
    }
    
    public boolean updateTimeTrack(TimeTrack tt) {
        ContentValues args = new ContentValues();
        args.put(KEY_HOUR_IN, tt.hourIn);
        args.put(KEY_MINUTE_IN, tt.minuteIn);
        args.put(KEY_HOUR_OUT, tt.hourOut);
        args.put(KEY_MINUTE_OUT, tt.minuteOut);
        args.put(KEY_HOUR_LUNCH, tt.hourLunch);
        args.put(KEY_MINUTE_LUNCH, tt.minuteLunch);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + tt.id, null) > 0;
    }
    
    public static TimeTrack populateTimeTrack(Cursor cursor){
    	TimeTrack tt = new TimeTrack();
    	tt.id = cursor.getInt(0);
    	
    	try {
			tt.date = sdf.parse(cursor.getString(1));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		tt.hourIn = cursor.getInt(2);
		tt.minuteIn = cursor.getInt(3);
		tt.hourOut = cursor.getInt(4);
		tt.minuteOut = cursor.getInt(5);
		tt.hourLunch = cursor.getInt(6);
		tt.minuteLunch = cursor.getInt(7);
    	
    	return tt;
    }
    
    public static String getTodayDateFormated(){
    	Calendar now = new GregorianCalendar();
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		return sdf.format(now.getTime());
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
        private static final String DATABASE_NAME = "mwtt.db";
        private static final int DATABASE_VERSION = 1;
        
    	private static final String DATABASE_CREATE =
            "create table "+ DATABASE_TABLE +" (_id integer primary key autoincrement, " + 
            KEY_ENTRY_DATE + " text not null," +
            KEY_HOUR_IN + " integer, " + KEY_MINUTE_IN + " integer, " +
            KEY_HOUR_OUT + " integer, " + KEY_MINUTE_OUT + " integer, " +
            KEY_HOUR_LUNCH + " integer, " + KEY_MINUTE_LUNCH + " integer);";
    	
    	DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
//                    + newVersion + ", which will destroy all old data");
//            
//            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
//            onCreate(db);
        }
    }
}
