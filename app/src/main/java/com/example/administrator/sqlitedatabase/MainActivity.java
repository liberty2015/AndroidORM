package com.example.administrator.sqlitedatabase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.sqlitedatabase.beans.Account;
import com.example.administrator.sqlitedatabase.databaseSupport.AccountLoader;
import com.example.administrator.sqlitedatabase.databaseSupport.SQLiteHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{
    private SQLiteHelper sqLiteHelper;
    private List<Account> accountList;
    private BaseAdapter adapter;
    private int gender;
    private Loader loader;
    private Dialog dialog,updateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("xxxxxx",this.getDatabasePath("test").toString());
        sqLiteHelper=new SQLiteHelper<Account>(this,2);
        accountList=new ArrayList<>();
        ListView listView= (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Account account=accountList.get(position);
                final int _id=account.get_id();
                dialog=new AlertDialog.Builder(MainActivity.this)
                        .setSingleChoiceItems(new String[]{"修改","删除"}, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        dialog.dismiss();
                                        updateDialog.show();
                                        break;
                                    case 1:
                                        boolean flag=sqLiteHelper.deleteById(Account.class, _id);
                                        String message=(flag)?"delete success":"delete fail";
                                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                                        loader.onContentChanged();
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        })
                        .create();
                        dialog.show();
                View dialogView=LayoutInflater.from(MainActivity.this).inflate(R.layout.update_layout, null);
                ((EditText)dialogView.findViewById(R.id.name)).setText(account.getName());
//                ((EditText)dialogView.findViewById(R.id.age)).setText(account.getAge());
                ((RadioGroup)dialogView.findViewById(R.id.gender)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.male:
                                account.setGender(0);
                                break;
                            case R.id.female:
                                account.setGender(1);
                                break;
                        }
                    }
                });
                dialogView.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag=sqLiteHelper.update(account);
                        updateDialog.dismiss();
                        loader.onContentChanged();
                    }
                });
                updateDialog=new AlertDialog.Builder(MainActivity.this)
                        .setView(dialogView)
                        .create();
//                updateDialog.show();
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=((TextView)findViewById(R.id.name)).getText().toString();
                int age=Integer.parseInt(((TextView)findViewById(R.id.age)).getText().toString());
                Account account=new Account();
                account.setName(name);
                account.setAge(age);
                account.setGender(gender);
//                Date date=new Date();
//                account.setBirth(date);
                sqLiteHelper.insert(account);
                loader.onContentChanged();
            }
        });
        ((RadioGroup)findViewById(R.id.gender)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.male:
                        gender=0;
                        break;
                    case R.id.female:
                        gender=1;
                        break;
                }
            }
        });
        adapter=new BaseAdapter() {
            @Override
            public int getCount() {
                return accountList.size();
            }

            @Override
            public Object getItem(int position) {
                return accountList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                    convertView=LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout,parent,false);
                }
                Account account=accountList.get(position);
                ((TextView)convertView.findViewById(R.id.name_txt)).setText(account.getName());
                ((TextView)convertView.findViewById(R.id.gender_txt)).setText((account.getGender()==0)?"male":"female");
//                ((TextView)convertView.findViewById(R.id.age_num)).setText(account.getAge());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        loader=getSupportLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        AccountLoader loader=new AccountLoader(this,sqLiteHelper);
        return loader;
    }
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (data!=null){
            List<Account> oldList=accountList;
            oldList=null;
            accountList= (List<Account>) data;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
