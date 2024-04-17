package com.example.nrpcrud;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper Opendb;
    private EditText nrp, nama;
    private Button insert, update, delete, read;
    private SQLiteDatabase dbku;

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

        nrp = (EditText) findViewById(R.id.editTextText);
        nama = (EditText) findViewById(R.id.editTextText2);
        insert = (Button) findViewById(R.id.button);
        update = (Button) findViewById(R.id.button2);
        delete = (Button) findViewById(R.id.button4);
        read = (Button) findViewById(R.id.button5);

        insert.setOnClickListener(operasi);
        update.setOnClickListener(operasi);
        delete.setOnClickListener(operasi);
        read.setOnClickListener(operasi);

        Opendb = new SQLiteOpenHelper(this, "db.sql", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        dbku = Opendb.getWritableDatabase();
        dbku.execSQL("create table if not exists mhs(nrp TEXT, nama TEXT);");
    }

    @Override
    protected void onStop() {
        dbku.close();
        Opendb.close();
        super.onStop();
    }

    View.OnClickListener operasi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() ==R.id.button) insert();
            else if (v.getId() ==R.id.button2) update();
            else if (v.getId() ==R.id.button4) delete();
            else if (v.getId() ==R.id.button5) read();
        }
    };

    private void insert(){
        ContentValues data = new ContentValues();

        data.put("nrp", nrp.getText().toString());
        data.put("nama", nama.getText().toString());
        dbku.insert("mhs", null, data);
        Toast.makeText(this, "Data inserted.", Toast.LENGTH_LONG).show();
    }

    private void update(){
        ContentValues data = new ContentValues();

        data.put("nrp", nrp.getText().toString());
        data.put("nama", nama.getText().toString());
        dbku.update("mhs", data, "nrp='" + nrp.getText().toString() + "'", null);
        Toast.makeText(this, "Data updated.", Toast.LENGTH_LONG).show();
    }

    private void delete(){
        dbku.delete("mhs", "nrp='" + nrp.getText().toString() + "'", null);
        Toast.makeText(this, "Data deleted.", Toast.LENGTH_LONG).show();
    }
    private void read(){
        Cursor cursor = dbku.rawQuery("select * from mhs where nrp='" + nrp.getText().toString() + "'", null);
        if(cursor.getCount() > 0){
            Toast.makeText(this, "Data found: " + cursor.getCount(), Toast.LENGTH_LONG).show();
            cursor.moveToFirst();
            nama.setText(cursor.getString(cursor.getColumnIndex("nama")));
        }else{
            Toast.makeText(this, "Data not found.", Toast.LENGTH_LONG).show();
        }
    }

}