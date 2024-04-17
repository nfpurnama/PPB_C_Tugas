package com.example.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView text;
    private EditText userinput;
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

        text = (TextView) findViewById(R.id.textView);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(buttonClick);
    }

    View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button){
                showDialog();
            }
        }
    };

    private void showDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View input = inflater.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(input);
        userinput = (EditText) input.findViewById(R.id.editTextText);

        dialog
                .setCancelable(false)
                .setPositiveButton("Enter", action)
                .setNegativeButton("Cancel", action);
        dialog.show();
    }

    DialogInterface.OnClickListener action = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case -1:
                    text.setText(userinput.getText().toString());
                    break;
                case -2:
                    break;
            }
        }
    };
}