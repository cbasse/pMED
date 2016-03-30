package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jacky Sitzman on 3/29/2016.
 */
public class ListDataAdapter extends ArrayAdapter {
    List list = new ArrayList();
    //DatabaseHelper helper = new DatabaseHelper(this);
    //SQLiteDatabase db = helper.getReadableDatabase();
    //Cursor cursor = helper.getInformation(db);
    //int count = cursor.getCount();

    public ListDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class LayoutHandler {
        TextView COLUMN_ID, COLUMN_UNAME, COLUMN_PASS;
    }

    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutHandler layoutHandler;

        if(row == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout, parent, false); //get row
            layoutHandler = new LayoutHandler(); //get components from row
            layoutHandler.COLUMN_ID = (TextView)row.findViewById(R.id.text_user_id);
            layoutHandler.COLUMN_UNAME = (TextView)row.findViewById(R.id.text_user_name);
            layoutHandler.COLUMN_PASS = (TextView)row.findViewById(R.id.text_user_pass);
            row.setTag(layoutHandler);

        }
        else { layoutHandler = (LayoutHandler)row.getTag(); }


        Subjects s = (Subjects)this.getItem(position);
        layoutHandler.COLUMN_ID.setText((s.getId()).toString());
        layoutHandler.COLUMN_UNAME.setText(s.getUname());
        layoutHandler.COLUMN_PASS.setText(s.getPass());

        return row;
    }
}
