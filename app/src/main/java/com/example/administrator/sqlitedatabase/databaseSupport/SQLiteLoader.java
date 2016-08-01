package com.example.administrator.sqlitedatabase.databaseSupport;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Administrator on 2016/7/30.
 */

public class SQLiteLoader extends AsyncTaskLoader {

    public SQLiteLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    public void onCanceled(Object data) {
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    @Override
    public void deliverResult(Object data) {
        super.deliverResult(data);
    }

    protected void onReleaseResources(){

    }
}
