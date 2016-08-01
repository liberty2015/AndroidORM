package com.example.administrator.sqlitedatabase.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.administrator.sqlitedatabase.MyApplication;
import com.example.administrator.sqlitedatabase.databaseSupport.SQLiteHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/7/29.
 */

public class BaseDao<T> {
    private String TAG="BaseDao";
    private SQLiteHelper sqLiteHelper;
    public BaseDao(Context context){
        sqLiteHelper=new SQLiteHelper(context,0);
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class aClass, Object o) {

                if (o==null){
                    return null;
                }
                SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd");
                Date date=null;
                try {
                    date=format.parse((String) o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date;
            }
        },Date.class);
    }
    public boolean insert(T entity){
//        try {
//            StringBuffer sql=new StringBuffer();
//            List<String> names=new ArrayList<>();
////            List<Object> params=new ArrayList<>();
//            sql.append("insert into ");
//            Class<T> entityClass= (Class<T>) entity.getClass();
//            String tableName=entityClass.getSimpleName();
//            sql.append(tableName);
//            sql.append("(");
//            Field[] fields=entityClass.getDeclaredFields();
//            for (Field field:fields){
//                field.setAccessible(true);
//                String fieldName=field.getName();
//                Object value=field.get(entity);
//                if (value!=null){
////                    params.add(value);
//                    names.add(fieldName);
//                    sql.append(",");
//                    sql.append(fieldName);
//                }
//            }
//            sql.append(") ");
//            sql.append("values(");
//            for (int i=0;i<names.size();i++){
//                String value=BeanUtils.getProperty(entity,names.get(i));
//                if (i==0){
//                    sql.append(value);
//                }else {
//                    sql.append(",");
//                    sql.append(value);
//                }
//            }
//            sql.append(")");
//            sqLiteHelper.execSQL(sql.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            Class<T> entityClass= (Class<T>) entity.getClass();
            String tableName=entityClass.getSimpleName();
            Field[] fields=entityClass.getDeclaredFields();
            ContentValues values=new ContentValues();
            for (Field field:fields){
                field.setAccessible(true);
                String fieldName=field.getName();
                String value=BeanUtils.getProperty(entity,fieldName);
                if (value!=null){
                    values.put(fieldName,value);
                }
            }
            return sqLiteHelper.insert(tableName,null,values);
        }catch (Exception e){
            Log.d(TAG,e.toString());
        }
        return false;
    }


    public boolean update(T entity){
//        try {
//            StringBuffer sql=new StringBuffer();
//            List<String> names=new ArrayList<>();
//            Class<T> entityClass= (Class<T>) entity.getClass();
//            String tableName=entityClass.getSimpleName();
//            String where=" where _id="+BeanUtils.getProperty(entity,"_id");
//            sql.append("update ");
//            sql.append(tableName);
//            sql.append(" set ");
//            Field[] fields=entityClass.getDeclaredFields();
//            for (int i=0;i<fields.length;i++){
//                String fieldName=fields[i].getName();
//                String value=BeanUtils.getProperty(entity,fieldName);
//                if (value!=null){
//                    if (i==0){
//                        sql.append(fieldName+"="+value);
//                    }else {
//                        sql.append(",");
//                        sql.append(fieldName+"="+value);
//                    }
//                }
//            }
//            sql.append(where);
//            sqLiteHelper.execSQL(sql.toString());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        try {
            Class<T> entityClass= (Class<T>) entity.getClass();
            String tableName=entityClass.getSimpleName();
            Field[] fields=entityClass.getDeclaredFields();
            String where=" where _id= ? ";
            String _id=BeanUtils.getProperty(entity,"_id");
            String[] whereArgs=new String[]{_id};
            ContentValues values=new ContentValues();
            for (Field field:fields){
                field.setAccessible(true);
                String fieldName=field.getName();
                String value=BeanUtils.getProperty(entity,fieldName);
                if (value!=null){
                    values.put(fieldName,value);
                }
            }
            return sqLiteHelper.update(tableName,values,where,whereArgs);
        }catch (Exception e){
            Log.d(TAG,e.toString());
        }
        return false;
    }

    public boolean deleteById(Class clazz,String id){
        String whereCause="where _id=? ";
        return delete(clazz,whereCause,id);
    }

    public boolean deleteById(Class clazz,int id){
        String whereCause="where _id=? ";
        String _id=Integer.toString(id);
        return delete(clazz,whereCause,_id);
    }

    public boolean delete(Class clazz,String whereCause,String...args){
        String tableName=clazz.getSimpleName();
        return sqLiteHelper.delete(tableName,whereCause,args);
    }

    public void executeBySQL(String sql){
        Log.d(TAG,sql);
        sqLiteHelper.execSQL(sql);
    }



}
