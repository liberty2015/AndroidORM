package com.example.administrator.sqlitedatabase.databaseSupport;

import android.content.Context;

import com.example.administrator.sqlitedatabase.beans.Account;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */

public class AccountLoader extends BeanLoader<Account> {
    private SQLiteHelper<Account> sqLiteHelper;
    public AccountLoader(Context context,SQLiteHelper sqLiteHelper) {
        super(context);
        this.sqLiteHelper=sqLiteHelper;
    }

    @Override
    public List<Account> loadInBackground() {
        List<Account> list=sqLiteHelper.findList("select * from account",Account.class);
        return list;
    }
}
