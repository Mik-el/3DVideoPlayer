package com.estar.video.data;

import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * takee数据库直接操作类
 * @author zgl
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	private Map<String, String> sqls;

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version, Map<String, String> sqls) {
		super(context, name, factory, version);
		this.sqls = sqls;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if(sqls != null){
			for (String sql : sqls.values()) {
				db.execSQL(sql);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}