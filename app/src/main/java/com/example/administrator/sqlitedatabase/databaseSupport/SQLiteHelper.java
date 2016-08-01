package com.example.administrator.sqlitedatabase.databaseSupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

import com.example.administrator.sqlitedatabase.R;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/29.
 */

public class SQLiteHelper <M> {
    private String TAG="SQLiteHelper";
    private static final String DATABASE_NAME="test.db";
    private static String DATABASE_PATH;
    private static String outfile;
    private Context mContext;
    private SQLiteDatabase database;
    private int mVersion;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

    public SQLiteHelper(Context context,int version){
        mContext=context;
        mVersion=version;
        DATABASE_PATH=mContext.getFilesDir().getAbsolutePath()+ File.separator+"databases";
        outfile=DATABASE_PATH+File.separator+DATABASE_NAME;
        File file=new File(outfile);
        if (!file.exists()){
            try {
                buildDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            database=SQLiteDatabase.openOrCreateDatabase(outfile,null);
        }
        if (database!=null&&database.needUpgrade(mVersion)){
            try {
                buildDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class aClass, Object o) {

                if (o==null){
                    return null;
                }
                Date date=null;
                try {
                    date=dateFormat.parse((String) o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date;
            }
        },Date.class);
    }

    private void buildDatabase() throws Exception {
        InputStream inputStream=mContext.getResources().openRawResource(R.raw.test);
        File file=new File(outfile);
        File dir=new File(DATABASE_PATH);
        if (!dir.exists()){
            dir.mkdir();
        }
        if (!file.exists()){
            FileOutputStream fos=new FileOutputStream(outfile);
            byte[] buffer=new byte[1024];
            int length;
            while ((length=inputStream.read(buffer))>0){
                fos.write(buffer,0,length);
            }
            fos.close();
            inputStream.close();
        }
        database=SQLiteDatabase.openOrCreateDatabase(outfile,null);
    }

    public void execSQL(String sql){
        database.execSQL(sql);
    }

    public void execSQL(String sql,String ...args){
        database.execSQL(sql,args);
    }

    public Cursor rawSQL(String sql){
        return database.rawQuery(sql,null);
    }

    public Cursor rawSQL(String sql,String ...args){
        return database.rawQuery(sql,args);
    }

    public boolean insert(M entity){
        String tableName=entity.getClass().getSimpleName().toLowerCase();
        ContentValues values=new ContentValues();
        Field[] fields=entity.getClass().getDeclaredFields();
        for (Field field:fields){
            field.setAccessible(true);
            String fieldName=field.getName().toLowerCase();
            String type=field.getGenericType().toString();
            try {
                Object value=field.get(entity);
                switch (type){
                    case "int":
                        values.put(fieldName,(Integer) value);
                        break;
                    case "double":
                        values.put(fieldName,(Double)value);
                        break;
                    case "float":
                        values.put(fieldName,(Float)value);
                        break;
                    case "class java.lang.String":
                        values.put(fieldName,(String) value);
                        break;
                    case "boolean":
                        int flag=((Boolean)value==true)?1:0;
                        values.put(fieldName,(Integer)flag);
                        break;
                    case "short":
                        values.put(fieldName,(Short)value);
                        break;
                    case "long":
                        values.put(fieldName,(Long) value);
                        break;
                    case "class java.util.Date":
                        Date date= (Date) value;
                        String dateStr=dateFormat.format(date);
                        values.put(fieldName,dateStr);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return insert(tableName,null,values);
    }

    public boolean insert(String table, String nullColumnHack, ContentValues values){
        long result=database.insert(table,nullColumnHack,values);
        return (result==-1)?false:true;
    }

    public boolean update(String table,ContentValues values,String whereClause,String[] whereArgs){
        int result=database.update(table,values,whereClause,whereArgs);
        return (result==0)?false:true;
    }

    public boolean delete(String table,String whereCause,String ...args){
        int result=database.delete(table,whereCause,args);
        return (result==0)?false:true;
    }

    public Cursor find(String sql){
        return find(sql,null);
    }

    public Cursor find(String sql,String ...args){
        Log.d(TAG,sql+"  args="+args);
        return database.rawQuery(sql,args);
    }

    private M getEntity(Cursor cursor,Class<M> entityClass){
        M entity=null;
            try {
                entity=entityClass.newInstance();
                Field[] fields=entityClass.getDeclaredFields();
                for (int i=0;i<fields.length;i++){
                    fields[i].setAccessible(true);
                    Type type=fields[i].getGenericType();
                    String typeName=type.toString();
                    String fieldName=fields[i].getName();
                    switch (typeName){
                        case "int":
                            fields[i].set(entity,cursor.getInt(cursor.getColumnIndex(fieldName)));
//                            BeanUtils.setProperty(entity,fieldName,cursor.getInt(cursor.getColumnIndex(fieldName)));
                            break;
                        case "class java.lang.String":
                            fields[i].set(entity,cursor.getString(cursor.getColumnIndex(fieldName)));
//                            BeanUtils.setProperty(entity,fieldName,cursor.getString(cursor.getColumnIndex(fieldName)));
                            break;
                        case "double":
                            fields[i].set(entity,cursor.getDouble(cursor.getColumnIndex(fieldName)));
//                            BeanUtils.setProperty(entity,fieldName,cursor.getDouble(cursor.getColumnIndex(fieldName)));
                            break;
                        case "float":
                            fields[i].set(entity,cursor.getFloat(cursor.getColumnIndex(fieldName)));
//                            BeanUtils.setProperty(entity,fieldName,cursor.getDouble(cursor.getColumnIndex(fieldName)));
                            break;
                        case "boolean":
                            int flag=cursor.getInt(cursor.getColumnIndex(fieldName));
                            fields[i].set(entity,(flag!=0)?true:false);
//                            BeanUtils.setProperty(entity,fieldName,cursor.getInt(cursor.getColumnIndex(fieldName)));
                            break;
                        case "long":
                            fields[i].set(entity,cursor.getLong(cursor.getColumnIndex(fieldName)));
//                            BeanUtils.setProperty(entity,fieldName,cursor.getLong(cursor.getColumnIndex(fieldName)));
                            break;
                        case "short":
                            fields[i].set(entity,cursor.getShort(cursor.getColumnIndex(fieldName)));
//                            BeanUtils.setProperty(entity,fieldName,cursor.getShort(cursor.getColumnIndex(fieldName)));
                            break;
                        case "class java.util.Date":
                            String dateTime=cursor.getString(cursor.getColumnIndex(fieldName));
                            Date date=null;
                            date=dateFormat.parse(dateTime);
                            fields[i].set(entity,date);
//                            BeanUtils.setProperty(entity,fieldName,date);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return entity;
    }

    public M findOne(String sql,Class<M> entityClass){
        Cursor cursor=find(sql,null);
        if (cursor!=null&&cursor.moveToFirst()) {
            return getEntity(cursor,entityClass);
        }
        return null;
    }

    public Map<String ,Object> findMap(String sql){
        Map<String,Object> dataMap=new HashMap<>();
        Cursor cursor=find(sql,null);
        if (cursor!=null&&cursor.moveToFirst()){
            int count=cursor.getColumnCount();
            for (int i=0;i<count;i++){
                int type=cursor.getType(i);
                switch (type){
                    case Cursor.FIELD_TYPE_INTEGER:
                        dataMap.put(cursor.getColumnName(i),cursor.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        dataMap.put(cursor.getColumnName(i),cursor.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        dataMap.put(cursor.getColumnName(i),cursor.getBlob(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        dataMap.put(cursor.getColumnName(i),cursor.getString(i));
                        break;
                    default:

                        break;
                }
            }
        }
        return dataMap;
    }

    public List<M> findList(String sql,Class<M> entityClass){
        Cursor cursor=find(sql,null);
        List<M> entityList=new ArrayList<M>();
        if (cursor!=null&&cursor.moveToFirst()){
            int count=cursor.getCount();
            do{
                M entity=getEntity(cursor,entityClass);
                if (entity!=null){
                    entityList.add(entity);
                }
//                cursor.moveToNext();
            }while (cursor.moveToNext());
        }
        return entityList;
    }

}
