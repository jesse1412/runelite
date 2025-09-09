package net.runelite.client.plugins.mafhamtoa.Akkha;

import lombok.Getter;

import java.awt.*;

public enum TombsTiles {

    //
    //
    //Four tick butterfly
    //
    //

    FOUR_SE_1(Color.white, 14676, 40,29,1,0,"1",-100),
    FOUR_SE_2(Color.white, 14676, 38,31,1,0,"",-100),
    FOUR_SE_3(Color.white, 14676, 34,29,1,0,"2",-100),
    FOUR_SE_4(Color.white, 14676, 33,27,1,0,"",-100),
    FOUR_SE_5(Color.white, 14676, 36,23,1,0,"3",-100),
    FOUR_SE_6(Color.white, 14676, 40,27,1,0,"",-100),

    FOUR_NE_1(new Color(180, 255, 255, 255), 14676, 40,34,1,0,"1",-100),
    FOUR_NE_2(new Color(180, 255, 255, 255), 14676, 38,32,1,0,"",-100),
    FOUR_NE_3(new Color(180, 255, 255, 255), 14676, 34,34,1,0,"2",-100),
    FOUR_NE_4(new Color(180, 255, 255, 255), 14676, 33,36,1,0,"",-100),
    FOUR_NE_5(new Color(180, 255, 255, 255), 14676, 36,40,1,0,"3",-100),
    FOUR_NE_6(new Color(180, 255, 255, 255), 14676, 40,36,1,0,"",-100),

    FOUR_NW_1(Color.white, 14676, 25,34,1,0,"1",-100),
    FOUR_NW_2(Color.white, 14676, 27,32,1,0,"",-100),
    FOUR_NW_3(Color.white, 14676, 31,34,1,0,"2",-100),
    FOUR_NW_4(Color.white, 14676, 32,36,1,0,"",-100),
    FOUR_NW_5(Color.white, 14676, 29,40,1,0,"3",-100),
    FOUR_NW_6(Color.white, 14676, 25,36,1,0,"",-100),

    FOUR_SW_1(new Color(180, 255, 255, 255), 14676, 25,29,1,0,"1",-100),
    FOUR_SW_2(new Color(180, 255, 255, 255), 14676, 27,31,1,0,"",-100),
    FOUR_SW_3(new Color(180, 255, 255, 255), 14676, 31,29,1,0,"2",-100),
    FOUR_SW_4(new Color(180, 255, 255, 255), 14676, 32,27,1,0,"",-100),
    FOUR_SW_5(new Color(180, 255, 255, 255), 14676, 29,23,1,0,"3",-100),
    FOUR_SW_6(new Color(180, 255, 255, 255), 14676, 25,27,1,0,"",-100),

    FOUR_SE_MARKER(new Color(125, 125, 125, 165), 14676, 37,27,1,0,"ACW",-100),
    FOUR_NE_MARKER(new Color(125, 125, 125, 165), 14676, 37,36,1,0,"CW",-100),
    FOUR_NW_MARKER(new Color(125, 125, 125, 165), 14676, 28,36,1,0,"ACW",-100),
    FOUR_SW_MARKER(new Color(125, 125, 125, 165), 14676, 28,27,1,0,"CW",-100),

    //
    //
    //Five tick butterfly
    //
    //

    FIVE_SE_1(Color.white, 14676,36,31,1,1,"",-100),
    FIVE_SE_2(Color.white, 14676,39,28,1,1,"",-100),
    FIVE_SE_3(Color.white, 14676,39,27,1,1,"",-100),
    FIVE_SE_4(Color.white, 14676,36,24,1,1,"",-100),
    FIVE_SE_5(Color.white, 14676,33,27,1,1,"",-100),
    FIVE_SE_6(Color.white, 14676,33,28,1,1,"",-100),

    FIVE_NE_1(Color.white, 14676,36,32,1,1,"",-100),
    FIVE_NE_2(Color.white, 14676,33,35,1,1,"",-100),
    FIVE_NE_3(Color.white, 14676,33,36,1,1,"",-100),
    FIVE_NE_4(Color.white, 14676,36,39,1,1,"",-100),
    FIVE_NE_5(Color.white, 14676,39,36,1,1,"",-100),
    FIVE_NE_6(Color.white, 14676,39,35,1,1,"",-100),

    FIVE_NW_1(Color.white, 14676,29,32,1,1,"",-100),
    FIVE_NW_2(Color.white, 14676,26,35,1,1,"",-100),
    FIVE_NW_3(Color.white, 14676,26,36,1,1,"",-100),
    FIVE_NW_4(Color.white, 14676,29,39,1,1,"",-100),
    FIVE_NW_5(Color.white, 14676,32,36,1,1,"",-100),
    FIVE_NW_6(Color.white, 14676,32,35,1,1,"",-100),

    FIVE_SW_1(Color.white, 14676,29,24,1,1,"",-100),
    FIVE_SW_2(Color.white, 14676,26,27,1,1,"",-100),
    FIVE_SW_3(Color.white, 14676,26,28,1,1,"",-100),
    FIVE_SW_4(Color.white, 14676,29,31,1,1,"",-100),
    FIVE_SW_5(Color.white, 14676,32,28,1,1,"",-100),
    FIVE_SW_6(Color.white, 14676,32,27,1,1,"",-100),

    //
    //
    //Memory skip follow, AC = anti-clockwise, C = clockwise
    //
    //

    NE_AC_ORANGE(Color.white, 14676, 41,33,1,2,"",16),
    NE_AC_1(Color.WHITE, 14676, 41,35,1,2,"",15),
    NE_AC_2(Color.WHITE, 14676, 39,34,1,2,"",0),
    NE_AC_3(Color.WHITE, 14676, 40,36,1,2,"",1),
    NE_AC_4(Color.WHITE, 14676, 38,35,1,2,"",2),
    NE_AC_5(Color.WHITE, 14676, 39,37,1,2,"",3),
    NE_AC_6(Color.WHITE, 14676, 37,36,1,2,"",4),
    NE_AC_7(Color.WHITE, 14676, 38,38,1,2,"",5),
    NE_AC_8(Color.WHITE, 14676, 36,37,1,2,"",6),
    NE_AC_9(Color.WHITE, 14676, 37,39,1,2,"",7),
    NE_AC_10(Color.WHITE, 14676, 35,38,1,2,"",8),
    NE_AC_11(Color.WHITE, 14676, 36,40,1,2,"",9),
    NE_AC_12(Color.WHITE, 14676, 34,39,1,2,"",10),
    NE_AC_13(Color.WHITE, 14676, 34,41,1,2,"",11),
    NE_AC_14(Color.WHITE, 14676, 34,39,1,2,"",12),

    NE_C_ORANGE(Color.white, 14676, 34,40,1,3,"",16),
    NE_C_1(Color.WHITE, 14676, 36,40,1,3,"",15),
    NE_C_2(Color.WHITE, 14676, 35,38,1,3,"",0),
    NE_C_3(Color.WHITE, 14676, 37,39,1,3,"",1),
    NE_C_4(Color.WHITE, 14676, 36,37,1,3,"",2),
    NE_C_5(Color.WHITE, 14676, 38,38,1,3,"",3),
    NE_C_6(Color.WHITE, 14676, 37,36,1,3,"",4),
    NE_C_7(Color.WHITE, 14676, 39,37,1,3,"",5),
    NE_C_8(Color.WHITE, 14676, 38,35,1,3,"",6),
    NE_C_9(Color.WHITE, 14676, 40,36,1,3,"",7),
    NE_C_10(Color.WHITE, 14676, 39,34,1,3,"",8),
    NE_C_11(Color.WHITE, 14676, 41,35,1,3,"",9),
    NE_C_12(Color.WHITE, 14676, 40,33,1,3,"",10),
    NE_C_13(Color.WHITE, 14676, 42,33,1,3,"",11),
    NE_C_14(Color.WHITE, 14676, 40,33,1,3,"",12),

    SE_C_ORANGE(Color.white, 14676, 41, 30, 1, 4, "", 16),
    SE_C_1(Color.white, 14676, 41,28,1,4,"",15),
    SE_C_2(Color.WHITE, 14676, 39, 29, 1, 4, "", 0),
    SE_C_3(Color.WHITE, 14676, 40, 27, 1, 4, "", 1),
    SE_C_4(Color.WHITE, 14676, 38, 28, 1, 4, "", 2),
    SE_C_5(Color.WHITE, 14676, 39, 26, 1, 4, "", 3),
    SE_C_6(Color.WHITE, 14676, 37, 27, 1, 4, "", 4),
    SE_C_7(Color.WHITE, 14676, 38, 25, 1, 4, "", 5),
    SE_C_8(Color.WHITE, 14676, 36, 26, 1, 4, "", 6),
    SE_C_9(Color.WHITE, 14676, 37, 24, 1, 4, "", 7),
    SE_C_10(Color.WHITE, 14676, 35, 25, 1, 4, "", 8),
    SE_C_11(Color.WHITE, 14676, 36, 23, 1, 4, "", 9),
    SE_C_12(Color.WHITE, 14676, 34, 24, 1, 4, "", 10),
    SE_C_13(Color.WHITE, 14676, 34, 22, 1, 4, "", 11),
    SE_C_14(Color.WHITE, 14676, 34, 24, 1, 4, "", 12),

    SE_AC_ORANGE(Color.white, 14676, 34, 23, 1, 5, "", 16),
    SE_AC_1(Color.white, 14676, 36,23,1,5,"",15),
    SE_AC_2(Color.WHITE, 14676, 35, 25, 1, 5, "", 0),
    SE_AC_3(Color.WHITE, 14676, 37, 24, 1, 5, "", 1),
    SE_AC_4(Color.WHITE, 14676, 36, 26, 1, 5, "", 2),
    SE_AC_5(Color.WHITE, 14676, 38, 25, 1, 5, "", 3),
    SE_AC_6(Color.WHITE, 14676, 37, 27, 1, 5, "", 4),
    SE_AC_7(Color.WHITE, 14676, 39, 26, 1, 5, "", 5),
    SE_AC_8(Color.WHITE, 14676, 38, 28, 1, 5, "", 6),
    SE_AC_9(Color.WHITE, 14676, 40, 27, 1, 5, "", 7),
    SE_AC_10(Color.WHITE, 14676, 39, 29, 1, 5, "", 8),
    SE_AC_11(Color.WHITE, 14676, 41, 28, 1, 5, "", 9),
    SE_AC_12(Color.WHITE, 14676, 40, 30, 1, 5, "", 10),
    SE_AC_13(Color.WHITE, 14676, 42, 30, 1, 5, "", 11),
    SE_AC_14(Color.WHITE, 14676, 40, 30, 1, 5, "", 12),

    SW_C_ORANGE(Color.white, 14676, 31, 23, 1, 6, "", 16),
    SW_C_1(Color.white, 14676, 29,23,1,6,"",15),
    SW_C_2(Color.WHITE, 14676, 30, 25, 1, 6, "", 0),
    SW_C_3(Color.WHITE, 14676, 28, 24, 1, 6, "", 1),
    SW_C_4(Color.WHITE, 14676, 29, 26, 1, 6, "", 2),
    SW_C_5(Color.WHITE, 14676, 27, 25, 1, 6, "", 3),
    SW_C_6(Color.WHITE, 14676, 28, 27, 1, 6, "", 4),
    SW_C_7(Color.WHITE, 14676, 26, 26, 1, 6, "", 5),
    SW_C_8(Color.WHITE, 14676, 27, 28, 1, 6, "", 6),
    SW_C_9(Color.WHITE, 14676, 25, 27, 1, 6, "", 7),
    SW_C_10(Color.WHITE, 14676, 26, 29, 1, 6, "", 8),
    SW_C_11(Color.WHITE, 14676, 24, 28, 1, 6, "", 9),
    SW_C_12(Color.WHITE, 14676, 25, 30, 1, 6, "", 10),
    SW_C_13(Color.WHITE, 14676, 23, 30, 1, 6, "", 11),
    SW_C_14(Color.WHITE, 14676, 25, 30, 1, 6, "", 12),

    SW_AC_ORANGE(Color.white, 14676, 24, 30, 1, 7, "", 16),
    SW_AC_1(Color.white, 14676, 24,28,1,7,"",15),
    SW_AC_2(Color.WHITE, 14676, 26, 29, 1, 7, "", 0),
    SW_AC_3(Color.WHITE, 14676, 25, 27, 1, 7, "", 1),
    SW_AC_4(Color.WHITE, 14676, 27, 28, 1, 7, "", 2),
    SW_AC_5(Color.WHITE, 14676, 26, 26, 1, 7, "", 3),
    SW_AC_6(Color.WHITE, 14676, 28, 27, 1, 7, "", 4),
    SW_AC_7(Color.WHITE, 14676, 27, 25, 1, 7, "", 5),
    SW_AC_8(Color.WHITE, 14676, 29, 26, 1, 7, "", 6),
    SW_AC_9(Color.WHITE, 14676, 28, 24, 1, 7, "", 7),
    SW_AC_10(Color.WHITE, 14676, 30, 25, 1, 7, "", 8),
    SW_AC_11(Color.WHITE, 14676, 29, 23, 1, 7, "", 9),
    SW_AC_12(Color.WHITE, 14676, 31, 24, 1, 7, "", 10),
    SW_AC_13(Color.WHITE, 14676, 31, 22, 1, 7, "", 11),
    SW_AC_14(Color.WHITE, 14676, 31, 24, 1, 7, "", 12),

    NW_C_ORANGE(Color.white, 14676, 24, 33, 1, 8, "", 16),
    NW_C_1(Color.white, 14676, 24,35,1,8,"",15),
    NW_C_2(Color.WHITE, 14676, 26, 34, 1, 8, "", 0),
    NW_C_3(Color.WHITE, 14676, 25, 36, 1, 8, "", 1),
    NW_C_4(Color.WHITE, 14676, 27, 35, 1, 8, "", 2),
    NW_C_5(Color.WHITE, 14676, 26, 37, 1, 8, "", 3),
    NW_C_6(Color.WHITE, 14676, 28, 36, 1, 8, "", 4),
    NW_C_7(Color.WHITE, 14676, 27, 38, 1, 8, "", 5),
    NW_C_8(Color.WHITE, 14676, 29, 37, 1, 8, "", 6),
    NW_C_9(Color.WHITE, 14676, 28, 39, 1, 8, "", 7),
    NW_C_10(Color.WHITE, 14676, 30, 38, 1, 8, "", 8),
    NW_C_11(Color.WHITE, 14676, 29, 40, 1, 8, "", 9),
    NW_C_12(Color.WHITE, 14676, 31, 39, 1, 8, "", 10),
    NW_C_13(Color.WHITE, 14676, 31, 41, 1, 8, "", 11),
    NW_C_14(Color.WHITE, 14676, 31, 39, 1, 8, "", 12),

    NW_AC_ORANGE(Color.white, 14676, 31, 40, 1, 9, "", 16),
    NW_AC_1(Color.white, 14676, 29,40,1,9,"",15),
    NW_AC_2(Color.WHITE, 14676, 30, 38, 1, 9, "", 0),
    NW_AC_3(Color.WHITE, 14676, 28, 39, 1, 9, "", 1),
    NW_AC_4(Color.WHITE, 14676, 29, 37, 1, 9, "", 2),
    NW_AC_5(Color.WHITE, 14676, 27, 38, 1, 9, "", 3),
    NW_AC_6(Color.WHITE, 14676, 28, 36, 1, 9, "", 4),
    NW_AC_7(Color.WHITE, 14676, 26, 37, 1, 9, "", 5),
    NW_AC_8(Color.WHITE, 14676, 27, 35, 1, 9, "", 6),
    NW_AC_9(Color.WHITE, 14676, 25, 36, 1, 9, "", 7),
    NW_AC_10(Color.WHITE, 14676, 26, 34, 1, 9, "", 8),
    NW_AC_11(Color.WHITE, 14676, 24, 35, 1, 9, "", 9),
    NW_AC_12(Color.WHITE, 14676, 25, 33, 1, 9, "", 10),
    NW_AC_13(Color.WHITE, 14676, 23, 33, 1, 9, "", 11),
    NW_AC_14(Color.WHITE, 14676, 25, 33, 1, 9, "", 12),

    //
    //
    //Memory skip manual "Manual North East Anti-Clockwise 1" etc.
    //
    //

    MNE_AC_1(Color.WHITE, 14676, 42,33,1,10,"",15),
    MNE_AC_2(Color.WHITE, 14676, 41,33,1,10,"",0),
    MNE_AC_3(Color.WHITE, 14676, 41,34,1,10,"",1),
    MNE_AC_4(Color.WHITE, 14676, 40,34,1,10,"",2),
    MNE_AC_5(Color.WHITE, 14676, 40,35,1,10,"",3),
    MNE_AC_6(Color.WHITE, 14676, 39,35,1,10,"",4),
    MNE_AC_7(Color.WHITE, 14676, 39,36,1,10,"",5),
    MNE_AC_8(Color.WHITE, 14676, 38,36,1,10,"",6),
    MNE_AC_9(Color.WHITE, 14676, 38,37,1,10,"",7),
    MNE_AC_10(Color.WHITE, 14676, 37,37,1,10,"",8),
    MNE_AC_11(Color.WHITE, 14676, 37,38,1,10,"",9),
    MNE_AC_12(Color.WHITE, 14676, 36,38,1,10,"",10),
    MNE_AC_13(Color.WHITE, 14676, 36,39,1,10,"",11),
    MNE_AC_14(Color.WHITE, 14676, 35,39,1,10,"",12),

    MNE_C_1(Color.WHITE, 14676, 34,41,1,12,"",15),
    MNE_C_2(Color.WHITE, 14676, 34,40,1,12,"",0),
    MNE_C_3(Color.WHITE, 14676, 35,40,1,12,"",1),
    MNE_C_4(Color.WHITE, 14676, 35,39,1,12,"",2),
    MNE_C_5(Color.WHITE, 14676, 36,39,1,12,"",3),
    MNE_C_6(Color.WHITE, 14676, 36,38,1,12,"",4),
    MNE_C_7(Color.WHITE, 14676, 37,38,1,12,"",5),
    MNE_C_8(Color.WHITE, 14676, 37,37,1,12,"",6),
    MNE_C_9(Color.WHITE, 14676, 38,37,1,12,"",7),
    MNE_C_10(Color.WHITE, 14676, 38,36,1,12,"",8),
    MNE_C_11(Color.WHITE, 14676, 39,36,1,12,"",9),
    MNE_C_12(Color.WHITE, 14676, 39,35,1,12,"",10),
    MNE_C_13(Color.WHITE, 14676, 40,35,1,12,"",11),
    MNE_C_14(Color.WHITE, 14676, 40,34,1,12,"",12),

    MSE_C_1(Color.white, 14676, 42,30,1,13,"",15),
    MSE_C_2(Color.WHITE, 14676, 41, 30, 1, 13, "", 0),
    MSE_C_3(Color.WHITE, 14676, 41, 29, 1, 13, "", 1),
    MSE_C_4(Color.WHITE, 14676, 40, 29, 1, 13, "", 2),
    MSE_C_5(Color.WHITE, 14676, 40, 28, 1, 13, "", 3),
    MSE_C_6(Color.WHITE, 14676, 39, 28, 1, 13, "", 4),
    MSE_C_7(Color.WHITE, 14676, 39, 27, 1, 13, "", 5),
    MSE_C_8(Color.WHITE, 14676, 38, 27, 1, 13, "", 6),
    MSE_C_9(Color.WHITE, 14676, 38, 26, 1, 13, "", 7),
    MSE_C_10(Color.WHITE, 14676, 37, 26, 1, 13, "", 8),
    MSE_C_11(Color.WHITE, 14676, 37, 25, 1, 13, "", 9),
    MSE_C_12(Color.WHITE, 14676, 36, 25, 1, 13, "", 10),
    MSE_C_13(Color.WHITE, 14676, 36, 24, 1, 13, "", 11),
    MSE_C_14(Color.WHITE, 14676, 35, 24, 1, 13, "", 12),

    MSE_AC_1(Color.white, 14676, 34,22,1,14,"",15),
    MSE_AC_2(Color.WHITE, 14676, 34, 23, 1, 14, "", 0),
    MSE_AC_3(Color.WHITE, 14676, 35, 23, 1, 14, "", 1),
    MSE_AC_4(Color.WHITE, 14676, 35, 24, 1, 14, "", 2),
    MSE_AC_5(Color.WHITE, 14676, 36, 24, 1, 14, "", 3),
    MSE_AC_6(Color.WHITE, 14676, 36, 25, 1, 14, "", 4),
    MSE_AC_7(Color.WHITE, 14676, 37, 25, 1, 14, "", 5),
    MSE_AC_8(Color.WHITE, 14676, 37, 26, 1, 14, "", 6),
    MSE_AC_9(Color.WHITE, 14676, 38, 26, 1, 14, "", 7),
    MSE_AC_10(Color.WHITE, 14676, 38, 27, 1, 14, "", 8),
    MSE_AC_11(Color.WHITE, 14676, 39, 27, 1, 14, "", 9),
    MSE_AC_12(Color.WHITE, 14676, 39, 28, 1, 14, "", 10),
    MSE_AC_13(Color.WHITE, 14676, 40, 28, 1, 14, "", 11),
    MSE_AC_14(Color.WHITE, 14676, 40, 29, 1, 14, "", 12),

    MSW_C_1(Color.white, 14676, 31,22,1,15,"",15),
    MSW_C_2(Color.WHITE, 14676, 31, 23, 1, 15, "", 0),
    MSW_C_3(Color.WHITE, 14676, 30, 23, 1, 15, "", 1),
    MSW_C_4(Color.WHITE, 14676, 30, 24, 1, 15, "", 2),
    MSW_C_5(Color.WHITE, 14676, 29, 24, 1, 15, "", 3),
    MSW_C_6(Color.WHITE, 14676, 29, 25, 1, 15, "", 4),
    MSW_C_7(Color.WHITE, 14676, 28, 25, 1, 15, "", 5),
    MSW_C_8(Color.WHITE, 14676, 28, 26, 1, 15, "", 6),
    MSW_C_9(Color.WHITE, 14676, 27, 26, 1, 15, "", 7),
    MSW_C_10(Color.WHITE, 14676, 27, 27, 1, 15, "", 8),
    MSW_C_11(Color.WHITE, 14676, 26, 27, 1, 15, "", 9),
    MSW_C_12(Color.WHITE, 14676, 26, 28, 1, 15, "", 10),
    MSW_C_13(Color.WHITE, 14676, 25, 28, 1, 15, "", 11),
    MSW_C_14(Color.WHITE, 14676, 25, 29, 1, 15, "", 12),

    MSW_AC_1(Color.white, 14676, 23,30,1,16,"",15),
    MSW_AC_2(Color.WHITE, 14676, 24, 30, 1, 16, "", 0),
    MSW_AC_3(Color.WHITE, 14676, 24, 29, 1, 16, "", 1),
    MSW_AC_4(Color.WHITE, 14676, 25, 29, 1, 16, "", 2),
    MSW_AC_5(Color.WHITE, 14676, 25, 28, 1, 16, "", 3),
    MSW_AC_6(Color.WHITE, 14676, 26, 28, 1, 16, "", 4),
    MSW_AC_7(Color.WHITE, 14676, 26, 27, 1, 16, "", 5),
    MSW_AC_8(Color.WHITE, 14676, 27, 27, 1, 16, "", 6),
    MSW_AC_9(Color.WHITE, 14676, 27, 26, 1, 16, "", 7),
    MSW_AC_10(Color.WHITE, 14676, 28, 26, 1, 16, "", 8),
    MSW_AC_11(Color.WHITE, 14676, 28, 25, 1, 16, "", 9),
    MSW_AC_12(Color.WHITE, 14676, 29, 25, 1, 16, "", 10),
    MSW_AC_13(Color.WHITE, 14676, 29, 24, 1, 16, "", 11),
    MSW_AC_14(Color.WHITE, 14676, 30, 24, 1, 16, "", 12),

    MNW_C_1(Color.white, 14676, 23,33,1,17,"",15),
    MNW_C_2(Color.WHITE, 14676, 24, 33, 1, 17, "", 0),
    MNW_C_3(Color.WHITE, 14676, 24, 34, 1, 17, "", 1),
    MNW_C_4(Color.WHITE, 14676, 25, 34, 1, 17, "", 2),
    MNW_C_5(Color.WHITE, 14676, 25, 35, 1, 17, "", 3),
    MNW_C_6(Color.WHITE, 14676, 26, 35, 1, 17, "", 4),
    MNW_C_7(Color.WHITE, 14676, 26, 36, 1, 17, "", 5),
    MNW_C_8(Color.WHITE, 14676, 27, 36, 1, 17, "", 6),
    MNW_C_9(Color.WHITE, 14676, 27, 37, 1, 17, "", 7),
    MNW_C_10(Color.WHITE, 14676, 28, 37, 1, 17, "", 8),
    MNW_C_11(Color.WHITE, 14676, 28, 38, 1, 17, "", 9),
    MNW_C_12(Color.WHITE, 14676, 29, 38, 1, 17, "", 10),
    MNW_C_13(Color.WHITE, 14676, 29, 39, 1, 17, "", 11),
    MNW_C_14(Color.WHITE, 14676, 30, 39, 1, 17, "", 12),

    MNW_AC_1(Color.white, 14676, 31,41,1,18,"",15),
    MNW_AC_2(Color.WHITE, 14676, 31, 40, 1, 18, "", 0),
    MNW_AC_3(Color.WHITE, 14676, 30, 40, 1, 18, "", 1),
    MNW_AC_4(Color.WHITE, 14676, 30, 39, 1, 18, "", 2),
    MNW_AC_5(Color.WHITE, 14676, 29, 39, 1, 18, "", 3),
    MNW_AC_6(Color.WHITE, 14676, 29, 38, 1, 18, "", 4),
    MNW_AC_7(Color.WHITE, 14676, 28, 38, 1, 18, "", 5),
    MNW_AC_8(Color.WHITE, 14676, 28, 37, 1, 18, "", 6),
    MNW_AC_9(Color.WHITE, 14676, 27, 37, 1, 18, "", 7),
    MNW_AC_10(Color.WHITE, 14676, 27, 36, 1, 18, "", 8),
    MNW_AC_11(Color.WHITE, 14676, 26, 36, 1, 18, "", 9),
    MNW_AC_12(Color.WHITE, 14676, 26, 35, 1, 18, "", 10),
    MNW_AC_13(Color.WHITE, 14676, 25, 35, 1, 18, "", 11),
    MNW_AC_14(Color.WHITE, 14676, 25, 34, 1, 18, "", 12),

    //
    //
    //Wardens tiles
    //
    //

    WARDENS_LEFT(Color.white, 15696, 33,36,1,19,"",-100),
    WARDENS_MIDDLE(Color.white, 15696, 32,36,1,19,"",-100),
    WARDENS_RIGHT(Color.white, 15696, 31,36,1,19,"",-100),

    ;

    @Getter
    private final Color color;
    private final int region;
    private final int x;
    private final int y;
    private final int z;
    private final int group;
    private final String label;
    private final int position;

    TombsTiles(Color color, int region, int x, int y, int z, int group, String label, int position)
    {
        this.color = color;
        this.region = region;
        this.x = x;
        this.y = y;
        this.z = z;
        this.group = group;
        this.label = label;
        this.position = position;

    }
    public int getRegion() {
        return region;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getGroup(){return group;}

    public String getLabel(){return label;}

    public int getPosition(){return position;}
}