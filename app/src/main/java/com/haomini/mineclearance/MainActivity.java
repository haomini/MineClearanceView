package com.haomini.mineclearance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.haomini.mineclearance.widget.MineClearanceView;

public class MainActivity extends AppCompatActivity {

    private MineClearanceView mineClearanceView;

    private EditText etBombNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mineClearanceView = findViewById(R.id.mcl_view);

        etBombNum = findViewById(R.id.et_num);

        mineClearanceView.play(mineClearanceView.getBombNum());

        findViewById(R.id.tv_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mineClearanceView.play(mineClearanceView.getBombNum());
            }
        });

        findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fixme
                int count = Integer.parseInt(etBombNum.getText().toString());
                mineClearanceView.play(count);
            }
        });

    }
}
