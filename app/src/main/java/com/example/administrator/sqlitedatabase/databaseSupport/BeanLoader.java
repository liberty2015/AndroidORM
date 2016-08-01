package com.example.administrator.sqlitedatabase.databaseSupport;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */

public abstract class BeanLoader<T> extends AsyncTaskLoader<List<T>> {

    List<T> mData;
    public BeanLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(List<T> data) {
        if (isReset()&&data!=null){
            onReleaseResources(data);
        }
        List<T> oldData=mData;
        mData=data;
        if (isStarted()){
            super.deliverResult(data);
        }
        if (oldData!=null){
            onReleaseResources(data);
        }
    }


    @Override
    protected void onStartLoading() {
        if (mData!=null){
            deliverResult(mData);
        }
        if (takeContentChanged()||mData==null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mData!=null){
            onReleaseResources(mData);
            mData=null;
        }
    }

    @Override
    public void onCanceled(List<T> data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    protected void onReleaseResources(List<T> data){

    }
}
