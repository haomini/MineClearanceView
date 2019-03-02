package com.haomini.mineclearance.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author haomini
 * @since 2019/03/02
 */
public class MineClearanceView extends View {

    public static final int DEFAULT_BOMBS = 20;

    private final int RAW_BOX = 14;

    private final int COLUMN_BOX = 16;

    private final int BOMB_STATE = 9;

    private final int BOMB_OPEN_STATE = -9;

    private final int ZERO_OPEN_STATE = -12;

    private final Paint mBoxPaint = new Paint();

    private int[][] mPieces = new int[RAW_BOX][COLUMN_BOX];

    private int square;

    private int bombNum = DEFAULT_BOMBS;

    private boolean isOverGame;

    public MineClearanceView(Context context) {
        super(context);
        init();
    }

    public MineClearanceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MineClearanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPieces(bombNum);

        mBoxPaint.setColor(Color.YELLOW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        square = (int) Math.floor(getContext().getResources().getDisplayMetrics().widthPixels / (float) RAW_BOX);
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(square * COLUMN_BOX, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float dividerWidth = square / 50.0F + 1;

        for (int i = 0; i < RAW_BOX; i++) {
            for (int j = 0; j < COLUMN_BOX; j++) {

                // fixme
                RectF rectF = new RectF(
                        i * square + dividerWidth,
                        j * square + dividerWidth,
                        (i + 1) * square - dividerWidth,
                        (j + 1) * square - dividerWidth);

                if (mPieces[i][j] >= 0) {
                    // 未打开过
                    mBoxPaint.setColor(Color.YELLOW);
                    canvas.drawRect(rectF, mBoxPaint);
                } else if (mPieces[i][j] == ZERO_OPEN_STATE) {
                    // 空白打开后
                    mBoxPaint.setColor(Color.WHITE);
                    canvas.drawRect(rectF, mBoxPaint);
                } else if (mPieces[i][j] < 0 && mPieces[i][j] > BOMB_OPEN_STATE) {
                    // 普通打开后
                    mBoxPaint.setColor(Color.RED);
                    mBoxPaint.setTextAlign(Paint.Align.CENTER);
                    mBoxPaint.setTextSize(square - 2 * dividerWidth);
                    canvas.drawText(String.valueOf(Math.abs(mPieces[i][j])), rectF.left + square / 2.0F, rectF.bottom + dividerWidth, mBoxPaint);
                } else {
                    // 炸弹打开后
                    mBoxPaint.setColor(Color.RED);
                    canvas.drawRect(rectF, mBoxPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isOverGame) {
                    break;
                }
                int[] location = getPieceLocation(event.getX(), event.getY());
                if (mPieces[location[0]][location[1]] == 0) {
                    // 空白打开
                    mPieces[location[0]][location[1]] = ZERO_OPEN_STATE;
                    scanBlankConn(location[0], location[1]);
                    checkWin();
                    invalidate();
                } else if (mPieces[location[0]][location[1]] != BOMB_STATE && mPieces[location[0]][location[1]] > 0) {
                    // 普通数字打开
                    mPieces[location[0]][location[1]] = -mPieces[location[0]][location[1]];
                    checkWin();
                    invalidate();
                } else if (mPieces[location[0]][location[1]] == BOMB_STATE) {
                    // 炸弹打开
                    overGame();
                    isOverGame = true;
                }

                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    /**
     * 初始化棋子
     */
    private void initPieces(int bombNum) {

        // 重置所有状态为0
        for (int i = 0; i < RAW_BOX; i++) {
            for (int j = 0; j < COLUMN_BOX; j++) {
                mPieces[i][j] = 0;
            }
        }

        List<Integer> randomSeeds = new ArrayList<>(RAW_BOX * COLUMN_BOX);
        for (int i = 0; i < RAW_BOX * COLUMN_BOX; i++) {
            randomSeeds.add(i);
        }
        Collections.shuffle(randomSeeds);
        List<Integer> limitSeeds = new ArrayList<>(randomSeeds.subList(0, bombNum));
        Collections.sort(limitSeeds);
        for (int i = 0; i < bombNum; i++) {
            final int column = limitSeeds.get(i) / RAW_BOX;
            final int raw = limitSeeds.get(i) % RAW_BOX;
            mPieces[raw][column] = BOMB_STATE;
        }
        for (int i = 0; i < RAW_BOX; i++) {
            for (int j = 0; j < COLUMN_BOX; j++) {
                if (mPieces[i][j] != BOMB_STATE) {
                    mPieces[i][j] = calcPieceGrade(i, j);
                }
            }
        }
    }

    /**
     * 游戏结束, 显示所有 雷
     */
    private void overGame() {
        for (int i = 0; i < RAW_BOX; i++) {
            for (int j = 0; j < COLUMN_BOX; j++) {
                if (mPieces[i][j] == BOMB_STATE) {
                    mPieces[i][j] = BOMB_OPEN_STATE;
                }
            }
        }
        Toast.makeText(getContext(), "You failed!", Toast.LENGTH_SHORT).show();
        invalidate();
    }

    /**
     * 计算当前格子周围的炸弹数
     */
    private int calcPieceGrade(int raw, int column) {
        int grade = 0;
        // left_top
        if (raw != 0 && column != 0 && mPieces[raw - 1][column - 1] == BOMB_STATE) {
            grade++;
        }
        // top
        if (column != 0 && mPieces[raw][column - 1] == BOMB_STATE) {
            grade++;
        }
        // right_top
        if (raw != RAW_BOX - 1 && column != 0 && mPieces[raw + 1][column - 1] == BOMB_STATE) {
            grade++;
        }
        // left
        if (raw != 0 && mPieces[raw - 1][column] == BOMB_STATE) {
            grade++;
        }
        // right
        if (raw != RAW_BOX - 1 && mPieces[raw + 1][column] == BOMB_STATE) {
            grade++;
        }
        // bottom_left
        if (raw != 0 && column != COLUMN_BOX - 1 && mPieces[raw - 1][column + 1] == BOMB_STATE) {
            grade++;
        }
        //bottom
        if (column != COLUMN_BOX - 1 && mPieces[raw][column + 1] == BOMB_STATE) {
            grade++;
        }
        // bottom_right
        if (raw != RAW_BOX - 1 && column != COLUMN_BOX - 1 && mPieces[raw + 1][column + 1] == BOMB_STATE) {
            grade++;
        }
        return grade;
    }

    /**
     * 递归确定周围空白炸弹的格子是否靠近
     */
    private void scanBlankConn(int raw, int column) {
        // left_top
        if (raw != 0 && column != 0 && mPieces[raw - 1][column - 1] >= 0) {
            mPieces[raw - 1][column - 1] = mPieces[raw - 1][column - 1] == 0 ? ZERO_OPEN_STATE : -mPieces[raw - 1][column - 1];
            if (mPieces[raw - 1][column - 1] == ZERO_OPEN_STATE) {
                scanBlankConn(raw - 1, column - 1);
            }
        }
        // top
        if (column != 0 && mPieces[raw][column - 1] >= 0) {
            mPieces[raw][column - 1] = mPieces[raw][column - 1] == 0 ? ZERO_OPEN_STATE : -mPieces[raw][column - 1];
            if (mPieces[raw][column - 1] == ZERO_OPEN_STATE) {
                scanBlankConn(raw, column - 1);
            }
        }
        // right_top
        if (raw != RAW_BOX - 1 && column != 0 && mPieces[raw + 1][column - 1] >= 0) {
            mPieces[raw + 1][column - 1] = mPieces[raw + 1][column - 1] == 0 ? ZERO_OPEN_STATE : -mPieces[raw + 1][column - 1];
            if (mPieces[raw + 1][column - 1] == ZERO_OPEN_STATE) {
                scanBlankConn(raw + 1, column - 1);
            }
        }
        // left
        if (raw != 0 && mPieces[raw - 1][column] >= 0) {
            mPieces[raw - 1][column] = mPieces[raw - 1][column] == 0 ? ZERO_OPEN_STATE : -mPieces[raw - 1][column];
            if (mPieces[raw - 1][column] == ZERO_OPEN_STATE) {
                scanBlankConn(raw - 1, column);
            }
        }
        // right
        if (raw != RAW_BOX - 1 && mPieces[raw + 1][column] >= 0) {
            mPieces[raw + 1][column] = mPieces[raw + 1][column] == 0 ? ZERO_OPEN_STATE : -mPieces[raw + 1][column];
            if (mPieces[raw + 1][column] == ZERO_OPEN_STATE) {
                scanBlankConn(raw + 1, column);
            }
        }
        // bottom_left
        if (raw != 0 && column != COLUMN_BOX - 1 && mPieces[raw - 1][column + 1] >= 0) {
            mPieces[raw - 1][column + 1] = mPieces[raw - 1][column + 1] == 0 ? ZERO_OPEN_STATE : -mPieces[raw - 1][column + 1];
            if (mPieces[raw - 1][column + 1] == ZERO_OPEN_STATE) {
                scanBlankConn(raw - 1, column + 1);
            }
        }
        //bottom
        if (column != COLUMN_BOX - 1 && mPieces[raw][column + 1] >= 0) {
            mPieces[raw][column + 1] = mPieces[raw][column + 1] == 0 ? ZERO_OPEN_STATE : -mPieces[raw][column + 1];
            if (mPieces[raw][column + 1] == ZERO_OPEN_STATE) {
                scanBlankConn(raw, column + 1);
            }
        }
        // bottom_right
        if (raw != RAW_BOX - 1 && column != COLUMN_BOX - 1 && mPieces[raw + 1][column + 1] >= 0) {
            mPieces[raw + 1][column + 1] = mPieces[raw + 1][column + 1] == 0 ? ZERO_OPEN_STATE : -mPieces[raw + 1][column + 1];
            if (mPieces[raw + 1][column + 1] == ZERO_OPEN_STATE) {
                scanBlankConn(raw + 1, column + 1);
            }
        }
    }

    private void checkWin() {
        int count = 0;
        for (int i = 0; i < RAW_BOX; i++) {
            for (int j = 0; j < COLUMN_BOX; j++) {
                if (mPieces[i][j] > 0) {
                    count++;
                }
            }
        }
        if (count == bombNum) {
            isOverGame = true;
            Toast.makeText(getContext(), "You win!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据点击位置获取点击的格子
     */
    private int[] getPieceLocation(float x, float y) {
        int[] location = new int[2];
        location[0] = (int) Math.floor(x / square);
        location[1] = (int) Math.floor(y / square);
        return location;
    }

    /**
     * 开始游戏
     *
     * @param bombNum 设置炸弹数
     */
    public void play(int bombNum) {
        isOverGame = false;
        this.bombNum = bombNum;
        init();
        invalidate();
    }


    public int getBombNum() {
        return bombNum;
    }
}
