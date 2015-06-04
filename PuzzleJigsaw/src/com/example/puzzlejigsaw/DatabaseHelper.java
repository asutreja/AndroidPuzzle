package com.example.puzzlejigsaw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static DatabaseHelper sInstance;
	
	final static String TABLE_NAME = "statistics";
	final static String TIME = "Time";
	final static String _ID = "_id";
	final static String DIFFICULTY = "Difficulty";
	final static String[] columns = { _ID, TIME, DIFFICULTY};

	final private static String CREATE_CMD =

	"CREATE TABLE statistics (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TIME + " TEXT NOT NULL,"
			+ DIFFICULTY +" TEXT NOT NULL)";

	final private static String NAME = "stat_db";
	final private static Integer VERSION = 1;
	private Context mContext;
	
	public static DatabaseHelper getInstance(Context context) {

	    // Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (sInstance == null) {
	      sInstance = new DatabaseHelper(context.getApplicationContext());
	    }
	    return sInstance;
	  }


	

	private DatabaseHelper(Context context) {
		super(context, NAME, null, VERSION);
		this.mContext = context;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CMD);		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	void deleteDatabase() {
		mContext.deleteDatabase(NAME);
	}

}
