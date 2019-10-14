import javax.swing.*;
import java.awt.event.MouseEvent;

/***********************
 *  chess[] index list
 *     先黑后红
 *    0 ~ 3   车
 *    4 ~ 7   马
 *    8 ~ 11  相
 *   12 ~ 15  士
 *   16 ~ 25  兵
 *   26 ~ 29  炮
 *   30 ~ 31  将
 ************************/

public class ChessmanRule {
    private static final int DISTANCE_ONESTEP = 57;
    private static final int CHESSMAN_SIZE = 55;
    private static final int RIVER_DOWN = 284;
    private static final int RIVER_UP = 341;


    /*** 卒子的移动规则 ***/
    void armsRule(int Man, JLabel play, MouseEvent me) {
        int xDistance = me.getX() - play.getX();
        int yDistance = me.getY() - play.getY();
        boolean isSameX = xDistance >= 0 & xDistance <= 55;
        boolean isSameY = Math.abs(yDistance) <= 27;
        boolean isYUpOneStep = -yDistance >= 27 & -yDistance <= 87;
        boolean isYDownOneStep = yDistance >= 27 & yDistance <= 87;
        boolean isXLeftOneStep = -xDistance >= 2 & -xDistance <= 57;
        boolean isXRightOneStep = xDistance >= 57 & xDistance <= 112;

        boolean isUP = isSameX & isYUpOneStep;
        boolean isDOWN = isSameX & isYDownOneStep;
        boolean isLEFT = isSameY & isXLeftOneStep;
        boolean isRIGHT = isSameY & isXRightOneStep;
        //黑卒向下移动
        if (Man < 21) {
            // 向下移动，棋子Label的x为图片的左边界，y为图片的中心
            // me.X 和 me.Y 就是鼠标点击的坐标点
            if (isDOWN) {
                play.setBounds(play.getX(), play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
            else if (play.getY() > RIVER_DOWN && isRIGHT) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
            else if (play.getY() > RIVER_DOWN && isLEFT) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //红卒向上
        else {
            if (isUP) {
                play.setBounds(play.getX(), play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
            else if (play.getY() <= RIVER_UP && isRIGHT) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
            else if (play.getY() <= RIVER_UP && isLEFT) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }
    }
    void armsRule(JLabel play, JLabel playTake) {
        int xDistance = playTake.getX() - play.getX();
        int yDistance = playTake.getY() - play.getY();
        boolean isEatLeft  = (yDistance == 0) & xDistance == -57;
        boolean isEatRight = (yDistance == 0) & xDistance ==  57;
        boolean isEatUp   = (xDistance == 0) & yDistance == -57;
        boolean isEatDown = (xDistance == 0) & yDistance ==  57;
        boolean isBlackPassRiver = play.getName().charAt(0) == 'B' && play.getY() > RIVER_DOWN;
        boolean isRedPassRiver = play.getName().charAt(0) == 'R' && play.getY() < RIVER_UP;

        boolean isNotSameSideChessman = play.getName().charAt(0) != playTake.getName().charAt(0);
        // 向左/右走
        if ((isEatRight || isEatLeft) && playTake.isVisible() && isNotSameSideChessman) {
            // 黑棋要过河才能右吃棋
            if (isBlackPassRiver) {
                playTake.setVisible(false);
                play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
            // 红棋要过河才左能吃棋
            else if (isRedPassRiver) {
                playTake.setVisible(false);
                play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //向上走
        else if ((isEatUp || isEatDown) && playTake.isVisible() && isNotSameSideChessman) {
            //黑棋不能向上吃棋
            if (play.getName().charAt(0) == 'B' && play.getY() < playTake.getY()) {
                playTake.setVisible(false);
                play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //红棋不能向下吃棋
            else if (play.getName().charAt(0) == 'R' && play.getY() > playTake.getY()) {
                playTake.setVisible(false);
                play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

    }

    /*** 炮、车移动规则 ***/
    void cannonRule(JLabel play, JLabel playQ[], MouseEvent me) {
        //起点和终点之间是否有棋子
        int Count = 0;

        //上、下移动
        if (play.getX() - me.getX() <= 0 && play.getX() - me.getX() >= -CHESSMAN_SIZE) {
            //指定所有模糊Y坐标
            for (int i = 56; i <= 551; i += DISTANCE_ONESTEP) {
                //移动的Y坐标是否有指定坐标相近的
                if (i - me.getY() >= -27 && i - me.getY() <= 27) {
                    //所有的棋子
                    for (int j = 0; j < 32; j++) {
                        //找出在同一条竖线的所有棋子、并不包括自己
                        if (playQ[j].getX() - play.getX() >= -27 && playQ[j].getX() - play.getX() <= 27 && playQ[j].getName() != play.getName() && playQ[j].isVisible()) {
                            //从起点到终点(从左到右)
                            for (int k = play.getY() + DISTANCE_ONESTEP; k < i; k += DISTANCE_ONESTEP) {
                                //大于起点、小于终点的坐标就可以知道中间是否有棋子
                                if (playQ[j].getY() < i && playQ[j].getY() > play.getY()) {
                                    //中间有一个棋子就不可以从这条竖线过去
                                    Count++;
                                    break;
                                }
                            }
                            //从起点到终点(从右到左)
                            for (int k = i + DISTANCE_ONESTEP; k < play.getY(); k += DISTANCE_ONESTEP) {
                                //找起点和终点的棋子
                                if (playQ[j].getY() < play.getY() && playQ[j].getY() > i) {
                                    Count++;
                                    break;
                                }
                            }
                        }
                    }
                    //起点和终点没有棋子就可以移动了
                    if (Count == 0) {
                        play.setBounds(play.getX(), i, CHESSMAN_SIZE, CHESSMAN_SIZE);
                        break;
                    }
                }
            }
        }

        //左、右移动
        else if (play.getY() - me.getY() >= -27 && play.getY() - me.getY() <= 27) {
            //指定所有模糊X坐标
            for (int i = 24; i <= 480; i += DISTANCE_ONESTEP) {
                //移动的X坐标是否有指定坐标相近的
                if (i - me.getX() >= -CHESSMAN_SIZE && i - me.getX() <= 0) {
                    //所有的棋子
                    for (int j = 0; j < 32; j++) {
                        //找出在同一条横线的所有棋子、并不包括自己
                        if (playQ[j].getY() == play.getY() && playQ[j].getName() != play.getName() && playQ[j].isVisible()) {
                            //从起点到终点(从上到下)
                            for (int k = play.getX() + DISTANCE_ONESTEP; k < i; k += DISTANCE_ONESTEP) {
                                //大于起点、小于终点的坐标就可以知道中间是否有棋子
                                if (playQ[j].getX() < i && playQ[j].getX() > play.getX()) {
                                    //中间有一个棋子就不可以从这条横线过去
                                    Count++;
                                    break;
                                }
                            }

                            //从起点到终点(从下到上)
                            for (int k = i + DISTANCE_ONESTEP; k < play.getX(); k += DISTANCE_ONESTEP) {
                                //找起点和终点的棋子
                                if (playQ[j].getX() < play.getX() && playQ[j].getX() > i) {
                                    Count++;
                                    break;
                                }
                            }
                        }
                    }

                    //起点和终点没有棋子
                    if (Count == 0) {
                        play.setBounds(i, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
                        break;
                    }
                }
            }
        }

    }
    void cannonRule(int Chess, JLabel play, JLabel playTake, JLabel playQ[], MouseEvent me) {
        //起点和终点之间是否有棋子
        int Count = 0;


        //所有的棋子
        for (int j = 0; j < 32; j++) {
            //找出在同一条竖线的所有棋子、并不包括自己
            if (playQ[j].getX()== play.getX() && playQ[j].getName() != play.getName() && playQ[j].isVisible()) {
                // 从上到下扫描中间点的个数，如果被吃的点在起点的上面，则不会进行扫描
                for (int k = play.getY() + DISTANCE_ONESTEP; k < playTake.getY(); k += DISTANCE_ONESTEP) {
                    //大于起点、小于终点的坐标就可以知道中间是否有棋子
                    if (playQ[j].getY() < playTake.getY() && playQ[j].getY() > play.getY()) {
                        //计算起点和终点的棋子个数
                        Count++;
                        break;
                    }
                }
                // 从下到上扫描，如果被吃的点在起点的下面，则不会进行扫描
                for (int k = playTake.getY(); k < play.getY(); k += DISTANCE_ONESTEP) {
                    //找起点和终点的棋子
                    if (playQ[j].getY() < play.getY() && playQ[j].getY() > playTake.getY()) {
                        Count++;
                        break;
                    }
                }
            }

            //找出在同一条横线的所有棋子、并不包括自己
            else if (playQ[j].getY() == play.getY()  && playQ[j].getName() != play.getName() && playQ[j].isVisible()) {
                //自己是起点被吃的是终点(从左到右)
                for (int k = play.getX() + 50; k < playTake.getX(); k += DISTANCE_ONESTEP) {
                    //大于起点、小于终点的坐标就可以知道中间是否有棋子
                    if (playQ[j].getX() < playTake.getX() && playQ[j].getX() > play.getX()) {
                        Count++;
                        break;
                    }
                }

                //自己是起点被吃的是终点(从右到左)
                for (int k = playTake.getX(); k < play.getX(); k += DISTANCE_ONESTEP) {
                    //找起点和终点的棋子
                    if (playQ[j].getX() < play.getX() && playQ[j].getX() > playTake.getX()) {
                        Count++;
                        break;
                    }
                }
            }
        }

        //起点和终点之间要一个棋子是炮的规则、并不能吃自己的棋子
        if (Count == 1 && Chess == 0 && playTake.getName().charAt(0) != play.getName().charAt(0)) {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

        //起点和终点之间没有棋子是车的规则、并不能吃自己的棋子
        else if (Count == 0 && Chess == 1 && playTake.getName().charAt(0) != play.getName().charAt(0)) {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

    }

    /*** 马移动规则 ***/
    /**
     *
     *        x  1    x    8  x
     *        2  x    x    x  7
     *        x  x  horse  x  x
     *        3  x    x    x  6
     *        x  4    x    5  x
     *
     * */
    void horseRule(JLabel play, JLabel playQ[], MouseEvent me) {
        //保存坐标和障碍
        int Ex = 0, Ey = 0;
        int xDistance = me.getX() - play.getX();
        int yDistance = me.getY() - play.getY();

        // x 方向移动距离
        boolean isXMoveLeftOneStep  = -xDistance >= 2  & -xDistance <= 57;
        boolean isXMoveLeftTwoStep  = -xDistance >= 59 & -xDistance <= 114;
        boolean isXMoveRightOneStep =  xDistance >= 57 &  xDistance <= 112;
        boolean isXMoveRightTwoStep =  xDistance >= 114 & xDistance <= 171;

        // y 方向移动距离
        boolean isYMoveUpOneStep   = -yDistance >= 27 & -yDistance <= 87;
        boolean isYMoveUpTwoStep   = -yDistance >= 89 & -yDistance <= 144;
        boolean isYMoveDownOneStep =  yDistance >= 27 &  yDistance <= 87;
        boolean isYMoveDownTwoStep =  yDistance >= 89 &  yDistance <= 144;

        // 判断是否蹩脚
        boolean isCanMove = false;
        boolean isCanMoveLeft = true, isCanMoveRight = true, isCanMoveUp = true, isCanMoveDown = true;
        for (int i = 0; i < 32; i++) {
            // Up
            if (playQ[i].isVisible() && play.getX() == playQ[i].getX()  && play.getY() - playQ[i].getY() == DISTANCE_ONESTEP) {
                isCanMoveUp = false;
            }
            // Down
            if (playQ[i].isVisible() && play.getX() == playQ[i].getX() && playQ[i].getY() - play.getY() == DISTANCE_ONESTEP) {
                isCanMoveDown = false;
            }
            // Left
            if (playQ[i].isVisible() && play.getY() == playQ[i].getY() && play.getX() - playQ[i].getX() == DISTANCE_ONESTEP) {
                isCanMoveLeft = false;
            }
            // Right
            if (playQ[i].isVisible() && play.getY() == playQ[i].getY() && playQ[i].getX() - play.getX() == DISTANCE_ONESTEP) {
                isCanMoveRight = false;
            }
        }

        // 上移、左边
        if (isYMoveUpTwoStep && isXMoveLeftOneStep) {
            // 正上是否有别的棋子
            if (isCanMoveUp){
                isCanMove = true;
            }
        }
        // 左移、上边
        else if (isXMoveLeftTwoStep && isYMoveUpOneStep) {
            // 正左方是否有别的棋子
            if (isCanMoveLeft){
                isCanMove = true;
            }
        }
        //下移、右边
        else if (isYMoveDownTwoStep && isXMoveRightOneStep) {
            //正下方是否有别的棋子
            if (isCanMoveDown){
                isCanMove = true;
            }
        }
        //上移、右边
        else if (isYMoveUpTwoStep && isXMoveRightOneStep) {
            //正前方是否有别的棋子
            if (isCanMoveUp){
                isCanMove = true;
            }
        }
        //下移、左边
        else if (isYMoveDownTwoStep && isXMoveLeftOneStep) {
            //正下方是否有别的棋子
            if(isCanMoveDown){
                isCanMove = true;
            }
        }
        //右移、上边
        else if (isXMoveRightTwoStep && isYMoveUpOneStep) {
            //正右方是否有别的棋子
            if(isCanMoveRight){
                isCanMove = true;
            }
        }
        //右移、下边
        else if (isXMoveRightTwoStep && isYMoveDownOneStep) {
            //正右方是否有别的棋子
            if(isCanMoveRight){
                isCanMove = true;
            }
        }
        //左移、下边
        else if (isXMoveLeftTwoStep && isYMoveDownOneStep) {
            //正左方是否有别的棋子
            if(isCanMoveLeft){
                isCanMove = true;
            }
        }
        // 合成坐标
        // 获取Y坐标
        for (int y = 56; y <= 580; y += DISTANCE_ONESTEP) {
            if (y - me.getY() >= -27 && y - me.getY() <= 27) {
                Ey = y;
                break;
            }
        }

        // 获取X坐标
        for (int x = 24; x <= 480; x += DISTANCE_ONESTEP) {
            if (me.getX() - x >= 0 && me.getX() - x <= CHESSMAN_SIZE) {
                Ex = x;
                break;
            }
        }
        if (isCanMove) {
            play.setBounds(Ex, Ey, CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

    }
    void horseRule(JLabel play, JLabel playTake, JLabel playQ[], MouseEvent me) {
        int xDistance = playTake.getX() - play.getX();
        int yDistance = playTake.getY() - play.getY();

        // x 方向移动距离
        boolean isXMoveLeftOneStep  = xDistance == -DISTANCE_ONESTEP;
        boolean isXMoveLeftTwoStep  = xDistance == -(2 * DISTANCE_ONESTEP);
        boolean isXMoveRightOneStep = xDistance == DISTANCE_ONESTEP;
        boolean isXMoveRightTwoStep = xDistance == (2 * DISTANCE_ONESTEP);

        // y 方向移动距离
        boolean isYMoveUpOneStep   = yDistance == -DISTANCE_ONESTEP;
        boolean isYMoveUpTwoStep   = yDistance == -(2 * DISTANCE_ONESTEP);
        boolean isYMoveDownOneStep = yDistance == DISTANCE_ONESTEP;
        boolean isYMoveDownTwoStep = yDistance == (2 * DISTANCE_ONESTEP);

        // 如果两个棋子是一边的直接返回
        boolean isNotSameSideChessman = play.getName().charAt(0) != playTake.getName().charAt(0);
        if (!isNotSameSideChessman){
            return;
        }
        // 如果不是一边的，继续看能不能吃
        // 判断是否蹩脚
        boolean isCanEat = false;
        boolean isCanMoveLeft = true, isCanMoveRight = true, isCanMoveUp = true, isCanMoveDown = true;
        for (int i = 0; i < 32; i++) {
            // Up
            if (playQ[i].isVisible() && play.getX() == playQ[i].getX()  && play.getY() - playQ[i].getY() == DISTANCE_ONESTEP) {
                isCanMoveUp = false;
            }
            // Down
            if (playQ[i].isVisible() && play.getX() == playQ[i].getX() && playQ[i].getY() - play.getY() == DISTANCE_ONESTEP) {
                isCanMoveDown = false;
            }
            // Left
            if (playQ[i].isVisible() && play.getY() == playQ[i].getY() && play.getX() - playQ[i].getX() == DISTANCE_ONESTEP) {
                isCanMoveLeft = false;
            }
            // Right
            if (playQ[i].isVisible() && play.getY() == playQ[i].getY() && playQ[i].getX() - play.getX() == DISTANCE_ONESTEP) {
                isCanMoveRight = false;
            }
        }
        // 上移、左吃
        if (isYMoveUpTwoStep && isXMoveLeftOneStep) {
            // 正上方是否有别的棋子
            if(isCanMoveUp){
                isCanEat = true;
            }
        }

        // 上移、右吃
        else if (isYMoveUpTwoStep && isXMoveRightOneStep) {
            // 正前方是否有别的棋子
            if(isCanMoveUp){
                isCanEat = true;
            }
        }

        // 左移、上吃
        else if (isXMoveLeftTwoStep && isYMoveUpOneStep) {
            // 正左方是否有别的棋子
            if(isCanMoveLeft){
                isCanEat = true;
            }
        }

        // 左移、下吃
        else if (isXMoveLeftTwoStep && isYMoveDownOneStep) {
            //正左方是否有别的棋子
            if(isCanMoveLeft){
                isCanEat = true;
            }
        }

        //右 移、上吃
        else if (isXMoveRightTwoStep && isYMoveUpOneStep) {
            // 正右方是否有别的棋子
            if(isCanMoveRight){
                isCanEat = true;
            }
        }

        //右移、下吃
        else if (isXMoveRightTwoStep && isYMoveDownOneStep) {
            //正右方是否有别的棋子
            if(isCanMoveRight){
                isCanEat = true;
            }
        }

        //下移、左吃
        else if (isYMoveDownTwoStep && isXMoveLeftOneStep) {
            //正下方是否有别的棋子
            if(isCanMoveDown){
                isCanEat = true;
            }
        }

        //下移、右吃
        else if (isYMoveDownTwoStep && isXMoveRightOneStep) {
            //正下方是否有别的棋子
            if(isCanMoveDown){
                isCanEat = true;
            }
        }
        //没有障碍、并可以吃棋、不能吃自己颜色
        if (isCanEat) {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }
    }

    /*** 相移动规则 ***/
    void elephantRule(int Man, JLabel play, JLabel playQ[], MouseEvent me) {
        //坐标和障碍
        int Ex = 0, Ey = 0, Move = 0;
        int xDistance = me.getX() - play.getX();
        int yDistance = me.getY() - play.getY();
        boolean isXMoveLeftTwoStep  = -xDistance >= 59 & -xDistance <= 114;
        boolean isXMoveRightTwoStep =  xDistance >= 114 & xDistance <= 171;
        boolean isYMoveUpTwoStep   = -yDistance >= 89 & -yDistance <= 144;
        boolean isYMoveDownTwoStep =  yDistance >= 89 &  yDistance <= 144;
        // 获取Y坐标
        for (int y = 56; y <= 580; y += DISTANCE_ONESTEP) {
            if (y - me.getY() >= -27 && y - me.getY() <= 27) {
                Ey = y;
                break;
            }
        }

        // 获取X坐标
        for (int x = 24; x <= 480; x += DISTANCE_ONESTEP) {
            if (me.getX() - x >= 0 && me.getX() - x <= CHESSMAN_SIZE) {
                Ex = x;
                break;
            }
        }

        // 检查周围的棋子情况
        boolean isCanMove = false;
        boolean isLeftUpHadChess    = false;
        boolean isLeftDownHadChess  = false;
        boolean isRightUpHadChess   = false;
        boolean isRightDownHadChess = false;
        for (int i = 0; i < 32; i++) {
            // 左上方
            if (playQ[i].isVisible() && play.getX() - playQ[i].getX() == DISTANCE_ONESTEP && play.getY() - playQ[i].getY() == DISTANCE_ONESTEP) {
                isLeftUpHadChess = true;
            }
            // 右上方
            if (playQ[i].isVisible() && playQ[i].getX() - play.getX() == DISTANCE_ONESTEP && play.getY() - playQ[i].getY() == DISTANCE_ONESTEP) {
                isRightUpHadChess = true;
            }
            // 左下方
            if (playQ[i].isVisible() && play.getX() - playQ[i].getX() == DISTANCE_ONESTEP && play.getY() - playQ[i].getY() == -DISTANCE_ONESTEP) {
                isLeftDownHadChess = true;
            }
            // 右下方
            if (playQ[i].isVisible() && playQ[i].getX() - play.getX() == DISTANCE_ONESTEP && playQ[i].getY() - play.getY() == DISTANCE_ONESTEP) {
                isRightDownHadChess = true;
            }

        }
        /*** 判断是否可以移动 ***/
        // 上左
        if (isYMoveUpTwoStep && isXMoveLeftTwoStep) {
            //左上方是否有棋子
            if(!isLeftUpHadChess)
                isCanMove = true;

        }
        // 上右
        else if (isYMoveUpTwoStep && isXMoveRightTwoStep) {
            //右上方是否有棋子
            if(!isRightUpHadChess)
                isCanMove = true;
        }

        // 下左
        else if (isYMoveDownTwoStep && isXMoveLeftTwoStep) {
            //下左方是否有棋子
            if(!isLeftDownHadChess)
                isCanMove = true;
        }

        //下右
        else if (isYMoveDownTwoStep && isXMoveRightTwoStep) {
            //下右方是否有棋子
            if(!isRightDownHadChess)
                isCanMove = true;
        }
        /*** 分红色黑色移动 ***/
        //红旗不能过楚河
        if (isCanMove && Ey >= RIVER_UP && Man > 9) {
            play.setBounds(Ex, Ey, CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

        //黑旗不能过汉界
        else if (isCanMove && Ey <= RIVER_DOWN && Man < 10) {
            play.setBounds(Ex, Ey, CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

    }
    void elephantRule(JLabel play, JLabel playTake, JLabel playQ[]) {
        //障碍
        int Move = 0;
        // 先检查被吃棋子是不是自己人，如果是直接返回
        if(playTake.getName().charAt(0) == play.getName().charAt(0)){
            return;
        }
        int xDistance = playTake.getX() - play.getX();
        int yDistance = playTake.getY() - play.getY();
        boolean isXMoveLeftTwoStep  = -xDistance >= 59 & -xDistance <= 114;
        boolean isXMoveRightTwoStep =  xDistance >= 114 & xDistance <= 171;
        boolean isYMoveUpTwoStep   = -yDistance >= 89 & -yDistance <= 144;
        boolean isYMoveDownTwoStep =  yDistance >= 89 &  yDistance <= 144;
        // 检查周围的棋子情况
        boolean isCanEat = false;
        boolean isLeftUpHadChess    = false;
        boolean isLeftDownHadChess  = false;
        boolean isRightUpHadChess   = false;
        boolean isRightDownHadChess = false;
        for (int i = 0; i < 32; i++) {
            // 左上方
            if (playQ[i].isVisible() && play.getX() - playQ[i].getX() == DISTANCE_ONESTEP && play.getY() - playQ[i].getY() == DISTANCE_ONESTEP) {
                isLeftUpHadChess = true;
            }
            // 右上方
            if (playQ[i].isVisible() && playQ[i].getX() - play.getX() == DISTANCE_ONESTEP && play.getY() - playQ[i].getY() == DISTANCE_ONESTEP) {
                isRightUpHadChess = true;
            }
            // 左下方
            if (playQ[i].isVisible() && play.getX() - playQ[i].getX() == DISTANCE_ONESTEP && play.getY() - playQ[i].getY() == -DISTANCE_ONESTEP) {
                isLeftDownHadChess = true;
            }
            // 右下方
            if (playQ[i].isVisible() && playQ[i].getX() - play.getX() == DISTANCE_ONESTEP && playQ[i].getY() - play.getY() == DISTANCE_ONESTEP) {
                isRightDownHadChess = true;
            }

        }
        // 上左
        if (isYMoveUpTwoStep && isXMoveLeftTwoStep) {
            //左上方是否有棋子
            if(!isLeftUpHadChess)
                isCanEat = true;

        }
        // 上右
        else if (isYMoveUpTwoStep && isXMoveRightTwoStep) {
            //右上方是否有棋子
            if(!isRightUpHadChess)
                isCanEat = true;
        }

        // 下左
        else if (isYMoveDownTwoStep && isXMoveLeftTwoStep) {
            //下左方是否有棋子
            if(!isLeftDownHadChess)
                isCanEat = true;
        }

        //下右
        else if (isYMoveDownTwoStep && isXMoveRightTwoStep) {
            //下右方是否有棋子
            if(!isRightDownHadChess)
                isCanEat = true;
        }

        //红旗不能过楚河
        if (isCanEat && playTake.getY() >= RIVER_UP && play.getName().charAt(0) == 'R') {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

        //黑旗不能过汉界
        else if (isCanEat && playTake.getY() <= RIVER_DOWN && play.getName().charAt(0) == 'B') {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

    }

    /*** 士、仕移动方法 ***/
    void chapRule(int Man, JLabel play, JLabel playQ[], MouseEvent me) {
        int xDistance = me.getX() - play.getX();
        int yDistance = me.getY() - play.getY();
        boolean isXMoveLeftOneStep  = -xDistance >= 2  & -xDistance <= 57;
        boolean isXMoveRightOneStep =  xDistance >= 57 &  xDistance <= 112;
        boolean isYMoveUpOneStep    = -yDistance >= 27 & -yDistance <= 87;
        boolean isYMoveDownOneStep  =  yDistance >= 27 &  yDistance <= 87;
        //上、右
        if (isYMoveUpOneStep && isXMoveRightOneStep) {
            //士不能超过自己的界限
            if (Man < 14 && (play.getX() + DISTANCE_ONESTEP) >= 195 && (play.getX() + DISTANCE_ONESTEP) <= 309 && (play.getY() - DISTANCE_ONESTEP) <= 170) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //仕不能超过自己的界限
            else if (Man > 13 && (play.getY() - DISTANCE_ONESTEP) >= 455 && (play.getX() + DISTANCE_ONESTEP) >= 195 && (play.getX() + DISTANCE_ONESTEP) <= 309) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //上、左
        else if (isYMoveUpOneStep && isXMoveLeftOneStep) {
            //士不能超过自己的界限
            if (Man < 14 && (play.getX() - DISTANCE_ONESTEP) >= 195 && (play.getX() - DISTANCE_ONESTEP) <= 309 && (play.getY() - DISTANCE_ONESTEP) <= 170) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //仕不能超过自己的界限
            else if (Man > 13 && (play.getY() - DISTANCE_ONESTEP) >= 455 && (play.getX() - DISTANCE_ONESTEP) >= 195 && (play.getX() - DISTANCE_ONESTEP) <= 309) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //下、左
        else if (isYMoveDownOneStep && isXMoveLeftOneStep) {
            //士不能超过自己的界限
            if (Man < 14 && (play.getX() - DISTANCE_ONESTEP) >= 195 && (play.getX() - DISTANCE_ONESTEP) <= 309 && (play.getY() + DISTANCE_ONESTEP) <= 170) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //仕不能超过自己的界限
            else if (Man > 13 && (play.getY() + DISTANCE_ONESTEP) >= 455 && (play.getX() - DISTANCE_ONESTEP) >= 195 && (play.getX() - DISTANCE_ONESTEP) <= 309) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

        }


        //下、右
        else if (isYMoveDownOneStep && isXMoveRightOneStep) {
            //士不能超过自己的界限
            if (Man < 14 && (play.getX() + DISTANCE_ONESTEP) >= 195 && (play.getX() + DISTANCE_ONESTEP) <= 309 && (play.getY() + DISTANCE_ONESTEP) <= 170) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //仕不能超过自己的界限
            else if (Man > 13 && (play.getY() + DISTANCE_ONESTEP) >= 455 && (play.getX() + DISTANCE_ONESTEP) >= 195 && (play.getX() + DISTANCE_ONESTEP) <= 309) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

    }
    void chapRule(int Man, JLabel play, JLabel playTake, JLabel playQ[]) {
        // 当前状态
        boolean isCanEat = false;
        // 检查被吃的是不是在田字内，并且是还存在的
        boolean isNotSameSideChess = playTake.getName().charAt(0) != play.getName().charAt(0);
        boolean isInRedRange   = Man > 13 & playTake.getX() >= 195 & playTake.getX() <= 309 & playTake.getY() >= 455;
        boolean isInBlackRange = Man < 14 & playTake.getX() >= 195 & playTake.getX() <= 309 & playTake.getY() <= 170;
        if(!((isInBlackRange || isInRedRange) && playTake.isVisible() && isNotSameSideChess))
            return;

        int xDistance = playTake.getX() - play.getX();
        int yDistance = playTake.getY() - play.getY();
        boolean isXMoveLeftOneStep  = xDistance == -DISTANCE_ONESTEP;
        boolean isXMoveRightOneStep = xDistance ==  DISTANCE_ONESTEP;
        boolean isYMoveUpOneStep   = yDistance == -DISTANCE_ONESTEP;
        boolean isYMoveDownOneStep = yDistance ==  DISTANCE_ONESTEP;


        // 上、右
        if (isYMoveUpOneStep && isXMoveRightOneStep) {
            isCanEat = true;
        }

        // 上、左
        else if (isYMoveUpOneStep && isXMoveLeftOneStep) {
            isCanEat = true;
        }

        // 下、左
        else if (isYMoveDownOneStep && isXMoveLeftOneStep) {
            isCanEat = true;
        }

        // 下、右
        else if (isYMoveDownOneStep && isXMoveRightOneStep) {
            isCanEat = true;
        }

        // 可移动、并不能吃自己的棋子
        if (isCanEat) {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

    }

    /*** 将移动规则 ***/
    void willRule(int Man, JLabel play, MouseEvent me) {
        int xDistance = me.getX() - play.getX();
        int yDistance = me.getY() - play.getY();
        boolean isSameX = xDistance >= 0 & xDistance <= 55;
        boolean isSameY = Math.abs(yDistance) <= 27;
        boolean isYUpOneStep = -yDistance >= 27 & -yDistance <= 87;
        boolean isYDownOneStep = yDistance >= 27 & yDistance <= 87;
        boolean isXLeftOneStep = -xDistance >= 2 & -xDistance <= 57;
        boolean isXRightOneStep = xDistance >= 57 & xDistance <= 112;

        boolean isUP = isSameX & isYUpOneStep;
        boolean isDOWN = isSameX & isYDownOneStep;
        boolean isLEFT = isSameY & isXLeftOneStep;
        boolean isRIGHT = isSameY & isXRightOneStep;
        //向上
        if (isUP) {
            //将是否超过自己的界限
            if (Man == 30 && me.getY() <= 170 && me.getY() > 55) {
                play.setBounds(play.getX(), play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //帅是否超过自己的界限
            else if (Man == 31 && me.getY() >= 455) {
                play.setBounds(play.getX(), play.getY() - DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //向左
        else if (isLEFT) {
            //将是否超过自己的界限
            if (Man == 30 && me.getX() >= 195 && me.getX() <= 359) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            //帅是否超过自己的界限
            else if (Man == 31 && me.getX() >= 195 && me.getX() <= 359) {
                play.setBounds(play.getX() - DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //向右
        else if (isRIGHT) {
            //将、帅规则
            if (Man == 30 && me.getX() >= 195 && me.getX() <= 359) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            else if (Man == 31 && me.getX() >= 195 && me.getX() <= 359) {
                play.setBounds(play.getX() + DISTANCE_ONESTEP, play.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
            }
        }

        //向下
        else if (isDOWN) {
            //将、帅规则
            if (Man == 30 && me.getY() <= 180) {
                play.setBounds(play.getX(), play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

            else if (Man == 31 && me.getY() >= 455 && me.getY() <= 590) {
                play.setBounds(play.getX(), play.getY() + DISTANCE_ONESTEP, CHESSMAN_SIZE, CHESSMAN_SIZE);
            }

        }

    }
    void willRule(int Man, JLabel play, JLabel playTake) {
        //当前状态
        boolean will = false;
        // 计算两个棋子的相对位置
        final int xDistance = play.getX() - playTake.getX();
        final int yDistance = play.getY() - playTake.getY();
        // 判断是临近的上下左右位置
        boolean isUP = yDistance == DISTANCE_ONESTEP & xDistance == 0;
        boolean isDown  = yDistance == -DISTANCE_ONESTEP & xDistance == 0;
        boolean isLEFT  = xDistance ==  DISTANCE_ONESTEP & yDistance == 0;
        boolean isRight = xDistance == -DISTANCE_ONESTEP & yDistance == 0;
        // 向上
        if (isUP && playTake.isVisible()) {
            will = true;
        }
        // 向左
        else if (isLEFT && playTake.isVisible()) {
            will = true;
        }
        // 向右
        else if (isRight && playTake.isVisible()) {
            will = true;
        }
        // 向下
        else if (isDown && playTake.isVisible()) {
            will = true;
        }
        // 不是自己的棋子，并且临近可以吃
        if (playTake.getName().charAt(0) != play.getName().charAt(0) && will) {
            playTake.setVisible(false);
            play.setBounds(playTake.getX(), playTake.getY(), CHESSMAN_SIZE, CHESSMAN_SIZE);
        }

    }


}
