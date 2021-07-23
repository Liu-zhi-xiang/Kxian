package com.gjmetal.star.cache.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gjmetal.star.cache.db.anotation.Column;
import com.gjmetal.star.cache.db.anotation.Ignore;
import com.gjmetal.star.cache.db.anotation.MergeTable;
import com.gjmetal.star.cache.db.anotation.PrimaryKey;
import com.gjmetal.star.cache.db.anotation.Table;
import com.gjmetal.star.cache.db.anotation.Unique;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description：基础数据库封装
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2019-10-14 17:32
 */

public abstract class BaseSQLite extends SQLiteOpenHelper {
	public boolean isTransaction() {
		return isTransaction;
	}
	private ArrayList<HashMap<String, String[]>> args=new ArrayList<HashMap<String, String[]>>();

	private boolean isTransaction=false;
	public BaseSQLite(Context context, String DATABASE_NAME, int DATABASE_VERSION) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	private SQLiteDatabase sqldb;

	@Override
	public synchronized void onCreate(SQLiteDatabase arg0) {this.sqldb=arg0;onCreate(this);}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {this.sqldb=arg0;onUpgrade(this, arg1, arg2);}
	

	public abstract void onCreate(BaseSQLite database);

	public abstract void onUpgrade(BaseSQLite database, int oldVersion, int newVersion);
	
	public synchronized void createTable(Class<?> table){
		Table ltable = table.getAnnotation(Table.class);
		String tableName = null ;
		if(ltable!=null){
			tableName = ltable.tableName();
		}else{
			tableName = table.getSimpleName();
		}
		StringBuilder column = new StringBuilder();
		Field[] fields = table.getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field=fields[i];
			field.setAccessible(true);
			Ignore ignore = field.getAnnotation(Ignore.class);
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			if(ignore!=null&&ignore.value()){
				continue;
			}
			if (field.getName().equals("serialVersionUID")) {
				continue;
			}
			String columnName = field.getName();
			Column lColumn = field.getAnnotation(Column.class);
			if(lColumn!=null)
				columnName = lColumn.columnName();
			Class<?> clz = field.getType();
			column.append(columnName);
			Unique unique = field.getAnnotation(Unique.class);
			PrimaryKey primarykey = field.getAnnotation(PrimaryKey.class);
			
			if(clz.isPrimitive()|| String.class.isAssignableFrom(clz)|| Enum.class.isAssignableFrom(clz)||byte[].class.isAssignableFrom(clz)){
				if(byte[].class.isAssignableFrom(clz)){
					column.append(" ".concat("BLOB"));
				}else{
					column.append(" ".concat(int.class.isAssignableFrom(clz)?"INTEGER":"TEXT"));
				}

				if(primarykey!=null){
					column.append(" PRIMARY KEY");
					if(primarykey.autoincrement())
					column.append("  AUTOINCREMENT");
				}
				if(unique!=null&&unique.value())
					column.append(" unique");
			}else if(mergeTable!=null&&mergeTable.value()){
				String str = buildColumn(clz);
				column.append(str);
			}
			
			if(i!=fields.length-1){
				column.append(","); 
			}
		}
		createTable(tableName, column.toString());
	}
	
	private void buildValues(ContentValues cv, Class<?> table, Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Field[] fields = table.getFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			if(column!=null)fieldName = column.columnName();
			Class<?> clz = field.getType();
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			Object value = field.get(obj);
			if(mergeTable!=null&&mergeTable.value()){
				buildValues(cv, clz, value);
			}else{
				cv.put(fieldName, value != null ? value.toString() : "");
			}
		}
	}
	
	private String buildColumn(Class<?> table){
		StringBuilder column = new StringBuilder();
		Field[] fields = table.getFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			field.setAccessible(true);
			if (field.getName().equals("serialVersionUID")) {
				continue;
			}
			if(i!=0){
				column.append(",");
			}
			String columnName = field.getName();
			Column lColumn = field.getAnnotation(Column.class);
			if(lColumn!=null)
				columnName = lColumn.columnName();
			Class<?> clz = field.getType();
			column.append(columnName);
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			if(clz.isPrimitive()|| String.class.isAssignableFrom(clz)|| Enum.class.isAssignableFrom(clz)||byte[].class.isAssignableFrom(clz)){
				if(byte[].class.isAssignableFrom(clz)){
					column.append(" ".concat("BLOB"));
				}else{
					column.append(" ".concat(int.class.isAssignableFrom(clz)?"INTEGER":"TEXT"));
				}
				Unique unique = field.getAnnotation(Unique.class);
				if(unique!=null&&unique.value())
					column.append(" unique");
			}else if(mergeTable!=null&&mergeTable.value()){
				String str = buildColumn(clz);
				column.append(str);
			}
		}
		return column.toString();
	}
	
	private void createTable(String tableName, String column){
		Log.e("CREATE TABLE", String.format("CREATE TABLE IF NOT EXISTS %s (%s)", tableName,column));
		Log.d("createTable", "表名:" + tableName + "_列名:" + column);
		sqldb.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s)", tableName,column));
	}
	
	public Cursor queryObject(String sql, String[] selectionArgs) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery(sql, selectionArgs);
	}
	
	public String rawQueryOfColumn(String sql , String column, String...args){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, args);
		try{
			if(cursor.moveToFirst()){
				String value = cursor.getString(cursor.getColumnIndex(column));
				return value;
			}else{
				return null;
			}
		}finally{
			db.close();
			cursor.close();
		}
	}
	
	public Cursor rawQuery(String sql, String...args){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, args);
		return cursor;
	}

	public synchronized void execTransaction(ArrayList<HashMap<String, String[]>> args) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();// �??�??
		try {
			for (HashMap<String, String[]> arg : args) {
				Iterator<String> it = arg.keySet().iterator();
				for (; it.hasNext();) {
					String sql = it.next();
					db.execSQL(sql, arg.get(sql));
				}
			}
			db.setTransactionSuccessful();
		}catch(Exception e){
		} finally {
			isTransaction=false;
			db.endTransaction();
		}
		db.close();
	}
	public synchronized void startTransaction(){
		isTransaction=true;
	}

	public long getCount(String sql, String[] selectionArgs) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(sql, selectionArgs);
		long count = c.getCount();
		c.close();
		db.close();
		return count;
	}
	
	public synchronized void dropTable(String tableName) {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "DROP TABLE IF EXISTS " + tableName;
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.close();
		}
	}
	
	public synchronized void execSql(String sql, String...args){
		if(!isTransaction()){
			SQLiteDatabase db = this.getWritableDatabase();

			try {
				db.execSQL(sql,args);
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				db.close();	
			}
		
		}else{
			HashMap<String, String[]> sqls=new HashMap<String, String[]>();
			sqls.put(sql, args);
			this.args.add(sqls);
		}
	}
	
	public synchronized void save(List<?> list){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
		db.beginTransaction();
		for(Object o:list){
			save(db,o);
		}
		db.setTransactionSuccessful();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			db.close();
		}
	}
	
	public synchronized void saveOrIgnore(List<?> list){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
		db.beginTransaction();
		for(Object o:list){
			saveOrIgnore(db,o);
		}
		db.setTransactionSuccessful();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			db.close();
		}
	}
	public synchronized void replace(List<?> list){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
		db.beginTransaction();
		for(Object o:list){
			replace(db,o);
		}
		db.setTransactionSuccessful();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally{
			db.endTransaction();
			db.close();
		}
	}
	public synchronized void save(Object obj){
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			save(db, obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		db.close();
	}
	
	private synchronized void save(SQLiteDatabase db, Object obj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Table table = obj.getClass().getAnnotation(Table.class);
		String tableName = obj.getClass().getSimpleName();
		if(table!=null)
			tableName = table.tableName();
		Field[] fields = obj.getClass().getDeclaredFields();
		ContentValues values = new ContentValues();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			PrimaryKey primaryKey =field.getAnnotation(PrimaryKey.class);
			Ignore ignore =field.getAnnotation(Ignore.class);
			if(ignore!=null)continue;
			if(primaryKey!=null&&primaryKey.autoincrement()&&int.class.isAssignableFrom(field.getType()))
				continue;
			Column column = field.getAnnotation(Column.class);
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			if(column!=null)fieldName = column.columnName();
			Class<?> clz = field.getType();
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			Object value = field.get(obj);
			if(mergeTable!=null&&mergeTable.value()){
				buildValues(values, clz,value);
			}else{
				values.put(fieldName, value != null ? value.toString() : "");
			}
		}
		db.insert(tableName, null, values);
	}
	
	public synchronized void saveOrIgnore(Object obj){
		SQLiteDatabase database=getWritableDatabase();
		try {
			saveOrIgnore(database, obj);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		database.close();
	}
	private synchronized void saveOrIgnore(SQLiteDatabase db, Object obj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Field[] fields = obj.getClass().getDeclaredFields();
		ContentValues values = new ContentValues();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			PrimaryKey primaryKey =field.getAnnotation(PrimaryKey.class);
			if(primaryKey!=null&&primaryKey.autoincrement()&&int.class.isAssignableFrom(field.getType()))
				continue;
			Column column = field.getAnnotation(Column.class);
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			if(column!=null)fieldName = column.columnName();
			
			Class<?> clz = field.getType();
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			Object value = field.get(obj);
				
			if(mergeTable!=null&&mergeTable.value()){
				buildValues(values, clz,value);
			}else{
				values.put(fieldName, value != null ? value.toString() : "");
			}
		}
		String tableName = getTableName(obj.getClass());
		StringBuilder column = new StringBuilder();
		StringBuilder value = new StringBuilder();
		 Set<Map.Entry<String, Object>> entrySet = null;
	        if (values != null && values.size() > 0) {
	            entrySet = values.valueSet();
	            Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();

	            boolean needSeparator = false;
	            while (entriesIter.hasNext()) {
	                if (needSeparator) {
	                	column.append(", ");
	                	value.append(", ");
	                }
	                needSeparator = true;
	                Map.Entry<String, Object> entry = entriesIter.next();
	                column.append(entry.getKey());
	                value.append(String.format("'%s'", entry.getValue()));
	            }
	        }
		String sql = String.format("insert or ignore into %s (%s) values (%s)", tableName,column.toString(),value.toString());
		db.execSQL(sql);
	}

	public synchronized void update(Object obj) {
		SQLiteDatabase db = getWritableDatabase();
		Table table = obj.getClass().getAnnotation(Table.class);
		String tableName = obj.getClass().getSimpleName();
		if(table!=null)
			tableName = table.tableName();
		Field[] fields = obj.getClass().getDeclaredFields();
		ContentValues values = new ContentValues();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			if(column!=null)fieldName = column.columnName();
			Class<?> clz = field.getType();
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			try {
				Object value = field.get(obj);
				if(mergeTable!=null&&mergeTable.value()){
					buildValues(values, clz,value);
				}else{
					values.put(fieldName, value != null ? value.toString() : "");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
				
		}
		db.update(tableName, values, "", new String[]{});
		db.close();
	}

	public synchronized void replace(Object obj){
		SQLiteDatabase db = getWritableDatabase();
		replace(db, obj); 
		db.close();
	}
	
	public synchronized void replace(SQLiteDatabase db, Object obj) {
		Table table = obj.getClass().getAnnotation(Table.class);
		String tableName = obj.getClass().getSimpleName();
		if(table!=null)
			tableName = table.tableName();
		Field[] fields = obj.getClass().getDeclaredFields();
		ContentValues values = new ContentValues();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			Ignore ignore = field.getAnnotation(Ignore.class);
			if(ignore!=null)continue;
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			if(column!=null)fieldName = column.columnName();
			Class<?> clz = field.getType();
			MergeTable mergeTable = field.getAnnotation(MergeTable.class);
			try {
				Object value = field.get(obj);
				
				if(mergeTable!=null&&mergeTable.value()){
					buildValues(values, clz,value);
				}else{
					values.put(fieldName, value != null ? value.toString() : "");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		db.replace(tableName, null, values);
	}
	
	public synchronized void execTransaction(){
		if(isTransaction()){
			execTransaction(args);
			args.clear();
			isTransaction=false;
		}
	}
	
	public synchronized void delete(Class<?> class1, String where, String...args){
		String tableName = getTableName(class1) ;
		execSql(String.format("DELETE FROM %s %s %s",tableName,where!=null&&where.length()>0?"where":""  , where), args);
	}
	
	public synchronized void clearTable(Class<?> class1){
		String tableName = getTableName(class1) ;
		execSql(String.format("DELETE FROM %s",tableName));
	}

	private HashMap<String, Field> getMethod(Class<?> clz){
		HashMap<String, Field> map=new HashMap<String, Field>();
		Field[] fields=clz.getDeclaredFields();
		for(Field field:fields){
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			String fieldName = field.getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}
			if(column!=null)fieldName = column.columnName();
			map.put(fieldName, field);
		}
		return map;
	}
	
	public Cursor findAllCursor(Class<?> cls, String where, String[] selectionArgs){
		String sql = String.format("SELECT *FROM %s %s %s", getTableName(cls),where!=null?"where":"",where);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}
	
	public <T> List<?> findAll(String sql, String[] selectionArgs, Class<?> cls){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<?> list =findAll(cursor, cls);
		cursor.close();
		db.close();
		return list;
	}
	
	public <T>T find(Cursor cursor, Class<?> cls){
		HashMap<String, Field> fields=getMethod(cls);
		Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		for(int i=0;i<cursor.getColumnCount();i++){
			String columnName = cursor.getColumnName(i);
			if(fields.containsKey(columnName)){
				Field field = fields.get(columnName);
				try {
					invokeBaseValue(field, obj, cursor.getString(cursor.getColumnIndex(columnName)));
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				Log.w("DB", columnName.concat(" isn't contains"));
			}
		}
		return (T) obj;	
	}
	public List<?> findAll(Cursor cursor, Class<?> cls){
		HashMap<String, Field> fields=getMethod(cls);
		List<Object> list=new ArrayList<Object>();
		while(cursor.moveToNext()){
			Object obj = null;
			try {
				obj = cls.newInstance();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			for(int i=0;i<cursor.getColumnCount();i++){
				String columnName = cursor.getColumnName(i);
				if(fields.containsKey(columnName)){
					Field field = fields.get(columnName);
					try {
						invokeBaseValue(field, obj, cursor.getString(cursor.getColumnIndex(columnName)));
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					Log.w("DB", columnName.concat(" isn't contains"));
				}
			}
			list.add(obj);
		}
		return list;
	}
	

	public List<?> findAll(Class<?> cls){
		return findAll(String.format("SELECT *FROM %s", getTableName(cls)),new String[]{},cls);
	}
	
	public List<?> findAll(Class<?> cls, String where, String...args){
		return findAll(String.format("SELECT *FROM %s %s %s", getTableName(cls),where!=null?"where":"",where),args,cls);
	}
	
	public <T> List<?> findAll(String tableName, Class<?> cls){
		return findAll(String.format("SELECT *FROM %s", tableName),new String[]{},cls);
	}
	
	public String[] findStringArrOfColumn(Class<?> cls, String columnName, String where, String...selectionArgs){
		SQLiteDatabase db = getReadableDatabase();
		String tableName = getTableName(cls);
		Cursor cursor = db.rawQuery(String.format("SELECT %s FROM %s ", columnName,tableName).concat(where!=null&&where.length()>0?"where "+where:""), selectionArgs);
		String[] str = new String[cursor.getCount()];
		int i=0;
		while(cursor.moveToNext()){
			String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
			str[i] = columnValue;
			i++;
		}
		cursor.close();
		db.close();
		return str;
	}
	
	public <T> T findLast(String sql, String[] selectionArgs, Class<?> cls) throws Exception {
		SQLiteDatabase db = this.getReadableDatabase();
		HashMap<String, Field> fields=getMethod(cls);
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		Object obj = cls.newInstance();
		if(cursor.moveToLast()){
			for(int i=0;i<cursor.getColumnCount();i++){
				String columnName = cursor.getColumnName(i);
				if(fields.containsKey(columnName)){
					invokeBaseValue(fields.get(columnName), obj, cursor.getString(cursor.getColumnIndex(columnName)));
				}
			}
		}
		cursor.close();
		db.close();
		return (T) obj;
	}
	
	public String getTableName(Class<?> clz){
		Table table = clz.getAnnotation(Table.class);
		String tableName = null ;
		if(table!=null)
			tableName = table.tableName();
		else
			tableName = clz.getSimpleName();
		return tableName;
	}
	
	public <T> T findFrist(String sql, String[] selectionArgs, Class<?> cls) throws Exception {
		SQLiteDatabase db = this.getReadableDatabase();
		HashMap<String, Field> fields=getMethod(cls);
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		Object obj = cls.newInstance();
		if(cursor.moveToFirst()){
			for(int i=0;i<cursor.getColumnCount();i++){
				String columnName = cursor.getColumnName(i);
				if(fields.containsKey(columnName)){
					invokeBaseValue(fields.get(columnName), obj, cursor.getString(cursor.getColumnIndex(columnName)));
				}
			}
		}
		cursor.close();
		db.close();
		return (T) obj;
	}

	private void invokeBaseValue(Field childField, Object object, Object value) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, ParseException {
		if(value!=null && !value.toString().trim().equals("")){
			childField.set(object, castType(value,childField.getType()));	
		}
	}

	private <T> Object castType(Object value, Class<?> class1) throws IllegalAccessException, InstantiationException, ParseException {
		if(class1.equals(int.class)){
			return Integer.parseInt(value.toString());
		}else if(class1.equals(long.class)){
			return Long.parseLong(value.toString());
		}else if(class1.equals(double.class)){
			return Double.parseDouble(value.toString());
		}else if(class1.equals(float.class)){
			return Float.parseFloat(value.toString());
		}else if(class1.equals(boolean.class)){
			return Boolean.parseBoolean(value.toString());
		}else if(Timestamp.class.equals(class1)){
			return new Timestamp(Long.parseLong(value.toString()));
		}
		return value.toString();
	}
}