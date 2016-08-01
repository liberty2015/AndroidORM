package com.example.administrator.sqlitedatabase;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.administrator.sqlitedatabase.beans.Account;
import com.example.administrator.sqlitedatabase.databaseSupport.AccountLoader;
import com.example.administrator.sqlitedatabase.databaseSupport.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{
    private SQLiteHelper sqLiteHelper;
    private List<Account> accountList;
    private BaseAdapter adapter;
    private int gender;
    private Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("xxxxxx",this.getDatabasePath("test").toString());
        sqLiteHelper=new SQLiteHelper<Account>(this,0);
        accountList=new ArrayList<>();
        ListView listView= (ListView) findViewById(R.id.list);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=((TextView)findViewById(R.id.name)).getText().toString();
                int age=Integer.parseInt(((TextView)findViewById(R.id.age)).getText().toString());
                Account account=new Account();
                account.setName(name);
                account.setAge(age);
                account.setGender(gender);
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
