package com.haomini.mineclearance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haomini.mineclearance.widget.MineClearanceListener;
import com.haomini.mineclearance.widget.MineClearanceView;

import java.util.Locale;

/**
 * @author haomini
 * @since 2019/03/02
 */
public class MainActivity extends AppCompatActivity {

    private MineClearanceView mineClearanceView;

    private EditText etBombNum;

    private TextView tvCountLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mineClearanceView = findViewById(R.id.mcl_view);

        etBombNum = findViewById(R.id.et_num);

        tvCountLabel = findViewById(R.id.tv_count_label);

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

        mineClearanceView.setMineClearanceListener(new MineClearanceListener() {
            @Override
            public void onWinGame() {
                Toast.makeText(MainActivity.this, "You win!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoseGame() {
                Toast.makeText(MainActivity.this, "You Lose!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPieceOpen(int remainedPiece, int allPiece) {
                tvCountLabel.setText(String.format(Locale.getDefault(), "还有%d个棋子等待翻开", remainedPiece));
            }
        });

    }
}
