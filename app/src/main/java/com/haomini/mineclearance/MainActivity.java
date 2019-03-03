package com.haomini.mineclearance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.haomini.mineclearance.widget.MineClearanceView;

public class MainActivity extends AppCompatActivity {

    private MineClearanceView mineClearanceView;

    private EditText etBombNum;

    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mineClearanceView = findViewById(R.id.mcl_view);

        etBombNum = findViewById(R.id.et_num);

        etName = findViewById(R.id.et_name);

        final boolean isFunGame = getIntent().getBooleanExtra("boolean", false);
        mineClearanceView.play(mineClearanceView.getBombNum(), isFunGame);
        findViewById(R.id.group_fun).setVisibility(isFunGame ? View.VISIBLE : View.GONE);

        findViewById(R.id.tv_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mineClearanceView.play(mineClearanceView.getBombNum(), isFunGame);
            }
        });

        findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fixme
                int count = Integer.parseInt(etBombNum.getText().toString());
                mineClearanceView.play(count, isFunGame);
            }
        });

        findViewById(R.id.bt_name_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mineClearanceView.checkWin(etName.getText().toString());
            }
        });
    }
}
