package com.example.listview;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Contact> contacts;
    private ContactAdapter contactAdapter;

    private SQLiteDatabase database;
    private SQLiteOpenHelper openHelper;
    private ImageButton add;
    private ImageButton delete;
    private ImageButton search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        openHelper = new SQLiteOpenHelper(this, "contact.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {}
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        };
        database = openHelper.getWritableDatabase();
        database.execSQL("create table if not exists contact(name TEXT, phoneNbr TEXT);");

        add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(operation);

        search = (ImageButton) findViewById(R.id.search);
        search.setOnClickListener(operation);

        delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(operation);

        listView = (ListView) findViewById(R.id.listView);
        contacts = getData();
        contactAdapter = new ContactAdapter(this, 0, contacts);
        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Edit Contact");
                Contact contact = contacts.get(position);

                // Inflate the custom layout for the dialog
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.contact_form, null);
                final EditText nameEditText = dialogView.findViewById(R.id.name);
                final EditText phoneEditText = dialogView.findViewById(R.id.phoneNbr);
                nameEditText.setText(contact.getName());
                phoneEditText.setText(contact.getPhoneNbr());

                // Set the custom layout to the dialog
                dialog.setView(dialogView);

                // Set the positive button
                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString();
                        String phoneNbr = phoneEditText.getText().toString();
                        // Add the item to your data source or do something else with it
                        contact.setName(name);
                        contact.setPhoneNbr(phoneNbr);
                        contacts.set(position, contact);
                        contactAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                // Set the negative button
                dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });
    }

    private void addItem(String name, String phoneNbr) {
        ContentValues data = new ContentValues();
        data.put("name", name);
        data.put("phoneNbr", phoneNbr);
        try{
            long id = database.insert("contact", null, data);
            Contact contact = new Contact(name, phoneNbr);
            contacts.add(contact);
            contactAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
            Toast.makeText(this, "Completed create contact", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            if(database ==  null){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editItem(String name, String phoneNbr) {
        ContentValues data = new ContentValues();
        data.put("name", name);
        data.put("phoneNbr", phoneNbr);
        try{
            long id = database.insert("contact", null, data);
            Contact contact = new Contact(name, phoneNbr);
            contacts.add(contact);
            contactAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
            Toast.makeText(this, "Completed create contact", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            if(database ==  null){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchItem(String name, String phoneNbr) {
        ArrayList<Contact> dataList = new ArrayList<>();
        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor;
        if(name.isEmpty()){
            cursor = db.rawQuery("select * from contact", null);
        }else{
            cursor = db.rawQuery("select * from contact where name='" + name + "'", null);
        }
        if(cursor.getCount() > 0){
            Toast.makeText(this, "Data found: " + cursor.getCount(), Toast.LENGTH_LONG).show();
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    String searchedName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String searchedPhoneNbr = cursor.getString(cursor.getColumnIndexOrThrow("phoneNbr"));
                    dataList.add(new Contact(searchedName, searchedPhoneNbr));
                } while (cursor.moveToNext());
            }
        }else{
            Toast.makeText(this, "Data not found.", Toast.LENGTH_LONG).show();
        }

        contactAdapter.clear();
        contactAdapter.addAll(dataList);
        contactAdapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
    private void deleteItem(String name, String phoneNbr) {
        try{
            database.delete("contact", "name='" + name + "' AND phoneNbr='" + phoneNbr + "'", null);

        }catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        contactAdapter.clear();
        contactAdapter.addAll(getData());
        contactAdapter.notifyDataSetChanged();
    }


    public ArrayList<Contact> getData() {
        ArrayList<Contact> dataList = new ArrayList<>();
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM contact", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String phoneNbr = cursor.getString(cursor.getColumnIndexOrThrow("phoneNbr"));
                dataList.add(new Contact(name, phoneNbr));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dataList;
    }

    private void addData(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add Contact");
        View view = LayoutInflater.from(this).inflate(R.layout.contact_form, null);
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText phoneNbr = (EditText) view.findViewById(R.id.phoneNbr);

        dialog.setView(view);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addItem(name.getText().toString(), phoneNbr.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void editData(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Edit Contact");
        View view = LayoutInflater.from(this).inflate(R.layout.contact_form, null);
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText phoneNbr = (EditText) view.findViewById(R.id.phoneNbr);

        dialog.setView(view);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editItem(name.getText().toString(), phoneNbr.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void searchData(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Search Contact");
        View view = LayoutInflater.from(this).inflate(R.layout.contact_form, null);
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText phoneNbr = (EditText) view.findViewById(R.id.phoneNbr);

        dialog.setView(view);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchItem(name.getText().toString(), phoneNbr.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void deleteData(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete Contact");
        View view = LayoutInflater.from(this).inflate(R.layout.contact_form, null);
        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText phoneNbr = (EditText) view.findViewById(R.id.phoneNbr);

        dialog.setView(view);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(name.getText().toString(), phoneNbr.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.add){
                addData();
            }
            if (v.getId() == R.id.search){
                searchData();
            }
            if (v.getId() == R.id.delete){
                deleteData();
            }
        }
    };

}