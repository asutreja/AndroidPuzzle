package com.example.puzzlejigsaw;


import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Stat extends ListActivity{

	private SimpleCursorAdapter mAdapter;

	private DatabaseHelper mDbHelper;
	private static Context mContext;
	public static int difLvl;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		getListView().setBackgroundResource(R.drawable.blue_simple);

		mDbHelper = DatabaseHelper.getInstance(this);

		getListView().addHeaderView(getLayoutInflater().inflate(R.layout.list_header,null));

		ContentValues values = new ContentValues();

//		String count = "SELECT count(*) FROM "+ DatabaseHelper.TABLE_NAME;
//		Cursor mcursor = mDbHelper.getWritableDatabase().rawQuery(count, null);
//		mcursor.moveToFirst();
//		int icount = mcursor.getInt(0);
//		if(icount>0){
//			//leave 
//		}else{
//			values.put(DatabaseHelper.TIME, "99:99:999");
//			values.put(DatabaseHelper.DIFFICULTY, "lvl 1");
//			mDbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_NAME, null, values);
//		}


		// so that it doesnt crash when it doesnt have an "extra"
		if(getIntent().getExtras() != null)
		{

			Bundle b = getIntent().getExtras();
//			String difLvl = ("" + (b.getInt("LEVEL")+1));
			String timeRecord = b.getString("TIME");
			Log.d("level is: ", "" + difLvl);

			values.put(DatabaseHelper.TIME, timeRecord);
			values.put(DatabaseHelper.DIFFICULTY, "lvl " + (Play.difficultyLevel - 1));
			mDbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_NAME, null, values);
		}


		Cursor c = mDbHelper.getWritableDatabase().query(DatabaseHelper.TABLE_NAME,
				DatabaseHelper.columns, null, new String[] {}, null, null,
				DatabaseHelper.TIME,"10");



		SQLiteStatement s = mDbHelper.getWritableDatabase().compileStatement("select count(*) from " + DatabaseHelper.TABLE_NAME);

		long a = s.simpleQueryForLong();

		Log.i("DD", String.valueOf(a));

		if(a > 15){
			mDbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID +" < 5", null);
		}


		mAdapter = new MyAdapter(this, R.layout.list_layout, c,
				DatabaseHelper.columns, new int[] { R.id._id, R.id.time, R.id.difficulty },
				0);

		setListAdapter(mAdapter);

	}


	@Override
	protected void onResume() {
		super.onResume();
		MusicManager.start(this, MusicManager.MUSIC_MENU);

		mDbHelper.getWritableDatabase();

	}


	@Override
	protected void onPause() {
		super.onPause();

		MusicManager.pause();
		mDbHelper.close();

	}

	class MyAdapter extends SimpleCursorAdapter{

		private final LayoutInflater sLayoutInflater = LayoutInflater.from(mContext);

		public MyAdapter(Context context, int layout, Cursor c, String[] from,
				int[] to, int flags) {
			super(context, layout, c, from, to, flags);

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			ViewHolder holder = (ViewHolder) view.getTag();

			holder.id.setText(Integer.toString(cursor.getPosition()+1));
			holder.time.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)));
			holder.difficulty.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DIFFICULTY)));


		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			View newView;
			ViewHolder holder = new ViewHolder();

			newView = sLayoutInflater.inflate(R.layout.list_layout, parent,
					false);
			holder.id = (TextView) newView.findViewById(R.id._id);
			holder.time = (TextView) newView.findViewById(R.id.time);
			holder.difficulty = (TextView) newView.findViewById(R.id.difficulty);

			newView.setTag(holder);

			return newView;




		}




	}

	static class ViewHolder {
		TextView id;
		TextView time;
		TextView difficulty;
	}


}
