package com.haomini.mineclearance.widget;

/**
 * @author zhouhao
 * @since 2019/03/04
 */
public interface MineClearanceListener {

    /**
     * win game
     */
    void onWinGame();

    /**
     * lose game
     */
    void onLoseGame();

    /**
     * 当打开一个棋子后
     *
     * @param remainedPiece 还剩下的未翻开的除炸弹外的棋子
     * @param allPiece      全部棋子个数
     */
    void onPieceOpen(int remainedPiece, int allPiece);
}
