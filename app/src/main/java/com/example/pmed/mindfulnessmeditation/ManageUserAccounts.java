package com.example.pmed.mindfulnessmeditation;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ManageUserAccounts extends AppCompatActivity {

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    DatabaseHelper helper;
    Cursor cursor;
    ListDataAdapter listDataAdapter;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent i = new Intent(ManageUserAccounts.this, AdminHome.class);
            //i.putExtra("Username", str);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_accounts);
        listView = (ListView)findViewById(R.id.list_view);
        listDataAdapter = new ListDataAdapter(getApplicationContext(),R.layout.row_layout);
        listView.setAdapter(listDataAdapter);
        helper = new DatabaseHelper(getApplicationContext());
        sqLiteDatabase = helper.getReadableDatabase();
        cursor = helper.getInformation(sqLiteDatabase);

        if(cursor.moveToFirst()) { //checks if data is available on the cursor object
            do {

                Integer id;
                String uname, pass;
                //id = cursor.getString(0);
                id = cursor.getInt(0);
                uname = cursor.getString(1);
                pass = cursor.getString(2);
                Subjects s = new Subjects();
                s.setId(id);
                s.setUname(uname);
                s.setPass(pass);
                listDataAdapter.add(s); //pass each row of data into the adapter

            } while(cursor.moveToNext()); //will return true if another row is avaiable
        }
    }

    public void onClickButton(View v) {
        if(v.getId() == R.id.button_add_user) {
            Intent i = new Intent(ManageUserAccounts.this, AddUser.class);
            startActivity(i);
        }
    }

}
