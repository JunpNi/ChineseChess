import com.sun.org.apache.regexp.internal.RE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Checkerboard extends JFrame implements ActionListener, MouseListener, Runnable {
    private static final int TOOLBAR_WIDTH = 558;
    private static final int TOOLBAR_HEIGHT = 30;
    private static final int CHECKERBOARD_WIDTH = 558;
    private static final int CHECKERBOARD_HEIGHT = 620;
    private static final int CHESS_RADIUS = 55;

    private static final int END = 3;
    private static final int RED = 2;
    private static final int BLACK = 1;

    private JButton newGame;    // 新游戏按钮
    private JButton exitGame;   // 退出游戏按钮
    private JLabel promptMsg;  // 提示信息

    private JLabel[] chess = new JLabel[32]; // 棋子标签
    private JLabel checkerboardBg; // 棋盘背景图片

    private boolean bClicked;
    private int presentChessmanClick = RED; // 当前走棋的一方，如果为END游戏结束
    private int chessmanClickIndex;  // 当前点击或选中的棋子标号

    private Thread MouseThread;

    private ChessmanRule rule;

    /**
     * 初始化整个窗口
     * 包括导航窗，棋盘背景，棋子图片，以及顶层三色灯设置
     **/
    Checkerboard(String WindowName) {
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);
        /*** 构造导航栏、边框信息 ***/
        JToolBar jBar = new JToolBar();
        // 创建button和提示信息
        promptMsg = new JLabel(WindowName);
        newGame = new JButton("新游戏");
        exitGame = new JButton("退出游戏");
        // 添加到bar上
        jBar.setLayout(new GridLayout(0, 3));
        jBar.add(newGame);
        jBar.add(exitGame);
        jBar.add(promptMsg);
        jBar.setBounds(0, 0, TOOLBAR_WIDTH, TOOLBAR_HEIGHT);
        contentPane.add(jBar);
        // 按钮监听
        newGame.addActionListener(this);
        exitGame.addActionListener(this);
        // 窗口关闭按钮
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); // 关闭窗口
                System.exit(0);  // 退出程序
            }
        });
        /*** 棋盘，棋子 ***/
        // 初始化棋子
        initChess();
        for (int i = 0; i < 32; i++) {
            contentPane.add(chess[i]);
            chess[i].addMouseListener(this);
        }
        // 背景
        checkerboardBg = new JLabel(new ImageIcon("image/main.gif"));
        contentPane.add(checkerboardBg);
        checkerboardBg.setBounds(0, TOOLBAR_HEIGHT, CHECKERBOARD_WIDTH, CHECKERBOARD_HEIGHT);
        checkerboardBg.addMouseListener(this);

        // 设置初始窗口位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - CHECKERBOARD_WIDTH) / 2, (screenSize.height - CHECKERBOARD_HEIGHT) / 2);

        rule = new ChessmanRule();

        // 禁用窗口缩放
        this.setResizable(false);
        this.setTitle(WindowName);
        this.setSize(CHECKERBOARD_WIDTH, CHECKERBOARD_HEIGHT + TOOLBAR_HEIGHT);
        this.show();
    }

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
    private void initChess() {
        int i, k;
        Icon in;

        /*** 黑色棋子 ***/

        //车
        in = new ImageIcon("image/黑车.gif");
        for (i = 0, k = 24; i < 2; i++, k += 456) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 56, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("B车");
        }

        //马
        in = new ImageIcon("image/黑马.gif");
        for (i = 4, k = 81; i < 6; i++, k += 342) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 56, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("B马");
        }

        //相
        in = new ImageIcon("image/黑象.gif");
        for (i = 8, k = 138; i < 10; i++, k += 228) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 56, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("B象");
        }

        //士
        in = new ImageIcon("image/黑士.gif");
        for (i = 12, k = 195; i < 14; i++, k += 114) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 56, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("B士");
        }

        //卒
        in = new ImageIcon("image/黑卒.gif");
        for (i = 16, k = 24; i < 21; i++, k += 114) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 227, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("B卒" + i);
        }

        //炮
        in = new ImageIcon("image/黑炮.gif");
        for (i = 26, k = 81; i < 28; i++, k += 342) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 170, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("B炮" + i);
        }

        //将
        in = new ImageIcon("image/黑将.gif");
        chess[30] = new JLabel(in);
        chess[30].setBounds(252, 56, CHESS_RADIUS, CHESS_RADIUS);
        chess[30].setName("B将");

        //红色棋子
        //车
        in = new ImageIcon("image/红车.gif");
        for (i = 2, k = 24; i < 4; i++, k += 456) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 569, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("R车");
        }

        //马
        in = new ImageIcon("image/红马.gif");
        for (i = 6, k = 81; i < 8; i++, k += 342) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 569, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("R马");
        }

        //相
        in = new ImageIcon("image/红象.gif");
        for (i = 10, k = 138; i < 12; i++, k += 228) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 569, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("R象");
        }

        //士
        in = new ImageIcon("image/红士.gif");
        for (i = 14, k = 195; i < 16; i++, k += 114) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 569, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("R士");
        }

        //兵
        in = new ImageIcon("image/红卒.gif");
        for (i = 21, k = 24; i < 26; i++, k += 114) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 398, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("R卒" + i);
        }

        //炮
        in = new ImageIcon("image/红炮.gif");
        for (i = 28, k = 81; i < 30; i++, k += 342) {
            chess[i] = new JLabel(in);
            chess[i].setBounds(k, 455, CHESS_RADIUS, CHESS_RADIUS);
            chess[i].setName("R炮" + i);
        }

        //帅
        in = new ImageIcon("image/红将.gif");
        chess[31] = new JLabel(in);
        chess[31].setBounds(252, 569, CHESS_RADIUS, CHESS_RADIUS);
        chess[31].setName("R帅");
    }

    public void run() {
        while (true) {
            //单击棋子第一下开始闪烁
            if (bClicked) {
                chess[chessmanClickIndex].setVisible(false);

                //时间控制
                try {
                    Thread.sleep(200);
                } catch (Exception ignored) {
                }

                chess[chessmanClickIndex].setVisible(true);
            }

            try {
                Thread.sleep(250);
            } catch (Exception ignored) {
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        /*** New game ***/
        if (e.getSource().equals(newGame)) {
            int i, k;
            //重新排列每个棋子的位置
            //黑色棋子

            //车
            for (i = 0, k = 24; i < 2; i++, k += 456) {
                chess[i].setBounds(k, 56, 55, 55);
            }

            //马
            for (i = 4, k = 81; i < 6; i++, k += 342) {
                chess[i].setBounds(k, 56, 55, 55);
            }

            //相
            for (i = 8, k = 138; i < 10; i++, k += 228) {
                chess[i].setBounds(k, 56, 55, 55);
            }

            //士
            for (i = 12, k = 195; i < 14; i++, k += 114) {
                chess[i].setBounds(k, 56, 55, 55);
            }

            //卒
            for (i = 16, k = 24; i < 21; i++, k += 114) {
                chess[i].setBounds(k, 227, 55, 55);
            }

            //炮
            for (i = 26, k = 81; i < 28; i++, k += 342) {
                chess[i].setBounds(k, 170, 55, 55);
            }

            //将
            chess[30].setBounds(252, 56, 55, 55);

            //红色棋子
            //车
            for (i = 2, k = 24; i < 4; i++, k += 456) {
                chess[i].setBounds(k, 569, 55, 55);
            }

            //马
            for (i = 6, k = 81; i < 8; i++, k += 342) {
                chess[i].setBounds(k, 569, 55, 55);
            }

            //相
            for (i = 10, k = 138; i < 12; i++, k += 228) {
                chess[i].setBounds(k, 569, 55, 55);
            }

            //士
            for (i = 14, k = 195; i < 16; i++, k += 114) {
                chess[i].setBounds(k, 569, 55, 55);
            }

            //兵
            for (i = 21, k = 24; i < 26; i++, k += 114) {
                chess[i].setBounds(k, 398, 55, 55);
            }

            //炮
            for (i = 28, k = 81; i < 30; i++, k += 342) {
                chess[i].setBounds(k, 455, 55, 55);
            }

            //帅
            chess[31].setBounds(252, 569, 55, 55);

            presentChessmanClick = RED;
            promptMsg.setText("轮到红色走棋");

            for (i = 0; i < 32; i++) {
                chess[i].setVisible(true);
            }

        }
        //退出
        else if (e.getSource().equals(exitGame)) {
            int j = JOptionPane.showConfirmDialog(
                    this, "确定退出吗？", "退出游戏",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (j == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        //当前坐标
        int Ex = 0, Ey = 0;
        //启动线程
        if (MouseThread == null) {
            MouseThread = new Thread(this);
            MouseThread.start();
        }

        //单击棋盘(移动棋子)
        if (e.getSource().equals(checkerboardBg)) {
            // 检查是否具有移动资格
            boolean isRED = presentChessmanClick == RED & chess[chessmanClickIndex].getName().charAt(0) == 'R';
            boolean isBLACK = presentChessmanClick == BLACK & chess[chessmanClickIndex].getName().charAt(0) == 'B';
            boolean canMove = isRED | isBLACK;
            if (!canMove) {
                bClicked = false;
                return;
            }
            // 先走，最后检查是不是走棋错误
            Ex = chess[chessmanClickIndex].getX();
            Ey = chess[chessmanClickIndex].getY();
            //移动卒、兵
            if (chessmanClickIndex > 15 && chessmanClickIndex < 26) {
                rule.armsRule(chessmanClickIndex, chess[chessmanClickIndex], e);
            }

            //移动炮
            else if (chessmanClickIndex > 25 && chessmanClickIndex < 30) {
                rule.cannonRule(chess[chessmanClickIndex], chess, e);
            }

            //移动车
            else if (chessmanClickIndex >= 0 && chessmanClickIndex < 4) {
                rule.cannonRule(chess[chessmanClickIndex], chess, e);
            }

            //移动马
            else if (chessmanClickIndex > 3 && chessmanClickIndex < 8) {
                rule.horseRule(chess[chessmanClickIndex], chess, e);
            }

            //移动相、象
            else if (chessmanClickIndex > 7 && chessmanClickIndex < 12) {
                rule.elephantRule(chessmanClickIndex, chess[chessmanClickIndex], chess, e);
            }

            //移动仕、士
            else if (chessmanClickIndex > 11 && chessmanClickIndex < 16) {
                rule.chapRule(chessmanClickIndex, chess[chessmanClickIndex], chess, e);
            }

            //移动将、帅
            else if (chessmanClickIndex == 30 || chessmanClickIndex == 31) {
                rule.willRule(chessmanClickIndex, chess[chessmanClickIndex], e);
            }
            // 红棋走
            if (isRED) {
                // 原地不动，红棋继续
                // 否则换成黑棋
                if (Ex == chess[chessmanClickIndex].getX() && Ey == chess[chessmanClickIndex].getY()) {
                    presentChessmanClick = RED;
                    promptMsg.setText("轮到红棋走棋");
                } else {
                    presentChessmanClick = BLACK;
                    promptMsg.setText("轮到黑棋走棋");
                }
            }
            // 黑棋走
            else if (isBLACK) {
                // 原地不动，黑棋继续
                // 否则换成红棋
                if (Ex == chess[chessmanClickIndex].getX() && Ey == chess[chessmanClickIndex].getY()) {
                    presentChessmanClick = BLACK;
                    promptMsg.setText("轮到黑棋走棋");
                } else {
                    presentChessmanClick = RED;
                    promptMsg.setText("轮到红棋走棋");
                }
            }
            bClicked = false;
        }

        //单击棋子
        else {
            // 如果当前没有闪烁，则表示是状态机的第一状态，即第一次点击
            if (!bClicked) {
                for (int i = 0; i < 32; i++) {
                    if (e.getSource().equals(chess[i])) {
                        chessmanClickIndex = i;
                        //开始闪烁
                        bClicked = true;
                        break;
                    }
                }
            }

            //第二次单击棋子(吃棋子)
            else if (bClicked) {
                //当前没有操作(停止闪烁)
                bClicked = false;

                for (int i = 0; i < 32; i++) {
                    //找到被吃的棋子
                    if (e.getSource().equals(chess[i])) {
                        // 检查是否轮到该色走
                        boolean isRED = presentChessmanClick == RED & chess[chessmanClickIndex].getName().charAt(0) == 'R';
                        boolean isBLACK = presentChessmanClick == BLACK & chess[chessmanClickIndex].getName().charAt(0) == 'B';
                        boolean canMove = isRED | isBLACK;
                        if (!canMove) {
                            bClicked = false;
                            return;
                        }
                        Ex = chess[chessmanClickIndex].getX();
                        Ey = chess[chessmanClickIndex].getY();

                        // 卒、兵吃规则
                        if (chessmanClickIndex > 15 && chessmanClickIndex < 26) {
                            rule.armsRule(chess[chessmanClickIndex], chess[i]);
                        }

                        // 炮吃规则
                        else if (chessmanClickIndex > 25 && chessmanClickIndex < 30) {
                            rule.cannonRule(0, chess[chessmanClickIndex], chess[i], chess, e);
                        }

                        // 车吃规则
                        else if (chessmanClickIndex >= 0 && chessmanClickIndex < 4) {
                            rule.cannonRule(1, chess[chessmanClickIndex], chess[i], chess, e);
                        }

                        // 马吃规则
                        else if (chessmanClickIndex > 3 && chessmanClickIndex < 8) {
                            rule.horseRule(chess[chessmanClickIndex], chess[i], chess, e);
                        }

                        // 相、象吃规则
                        else if (chessmanClickIndex > 7 && chessmanClickIndex < 12) {
                            rule.elephantRule(chess[chessmanClickIndex], chess[i], chess);
                        }

                        // 士、仕吃棋规则
                        else if (chessmanClickIndex > 11 && chessmanClickIndex < 16) {
                            rule.chapRule(chessmanClickIndex, chess[chessmanClickIndex], chess[i], chess);
                        }

                        // 将、帅吃棋规则
                        else if (chessmanClickIndex == 30 || chessmanClickIndex == 31) {
                            rule.willRule(chessmanClickIndex, chess[chessmanClickIndex], chess[i]);
                            chess[chessmanClickIndex].setVisible(true);
                        }

                        // 该红棋吃棋的时候
                        if (isRED) {
                            //是否走棋错误(是否在原地没有动)
                            if (Ex == chess[chessmanClickIndex].getX() && Ey == chess[chessmanClickIndex].getY()) {
                                promptMsg.setText("轮到红棋走棋");
                                presentChessmanClick = RED;
                                break;
                            } else {
                                presentChessmanClick = BLACK;
                                promptMsg.setText("轮到黑棋走棋");
                                break;
                            }

                        }

                        // 该黑棋吃棋的时候
                        else if (isBLACK) {
                            //是否走棋错误(是否在原地没有动)
                            if (Ex == chess[chessmanClickIndex].getX() && Ey == chess[chessmanClickIndex].getY()) {
                                promptMsg.setText("轮到黑棋走棋");
                                presentChessmanClick = BLACK;
                                break;
                            } else {
                                presentChessmanClick = RED;
                                promptMsg.setText("轮到红棋走棋");
                                break;
                            }

                        }

                    }

                }


                //是否胜利
                if (!chess[31].isVisible()) {
                    JOptionPane.showConfirmDialog(
                            this, "黑棋胜利", "玩家一胜利",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                    //双方都不可以在走棋了
                    presentChessmanClick = END;
                    promptMsg.setText("黑棋胜利");

                } else if (!chess[30].isVisible()) {
                    JOptionPane.showConfirmDialog(
                            this, "红棋胜利", "玩家二胜利",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                    presentChessmanClick = END;
                    promptMsg.setText("  红棋胜利");
                }

            }

        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }


}
