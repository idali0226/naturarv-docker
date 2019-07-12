/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal.util;

/**
 *
 * @author idali
 */
public enum MonthElement {

    Jan,
    Feb,
    Mar,
    Apr,
    May,
    Jun,
    Jul,
    Aug,
    Sep,
    Oct,
    Nov,
    Dec;
    
    public static MonthElement getName(int i) {
        switch (i) {
            case 1:
                return Jan;
            case 2:
                return Feb;
            case 3:
                return Mar;
            case 4:
                return Apr;
            case 5:
                return May;
            case 6:
                return Jun;
            case 7:
                return Jul;
            case 8:
                return Aug;
            case 9:
                return Sep;
            case 10:
                return Oct;
            case 11:
                return Nov;
            case 12:
                return Dec;
            default:
                return Jan;
        }
    }

    public int getMonthNumber() {
        switch (this) {
            case Jan:
                return 1;
            case Feb:
                return 2;
            case Mar:
                return 3;
            case Apr:
                return 4;
            case May:
                return 5;
            case Jun:
                return 6;
            case Jul:
                return 7;
            case Aug:
                return 8;
            case Sep:
                return 9;
            case Oct:
                return 10;
            case Nov:
                return 11;
            case Dec:
                return 12;
            default:
                return 1;
        }
    }

    public String getMonthNumberInString() { 
        switch (this) {
            case Jan:
                return String.valueOf(1);
            case Feb:
                return String.valueOf(2);
            case Mar:
                return String.valueOf(3);
            case Apr:
                return String.valueOf(4);
            case May:
                return String.valueOf(5);
            case Jun:
                return String.valueOf(6);
            case Jul:
                return String.valueOf(7);
            case Aug:
                return String.valueOf(8);
            case Sep:
                return String.valueOf(9);
            case Oct:
                return String.valueOf(10);
            case Nov:
                return String.valueOf(11);
            case Dec:
                return String.valueOf(12);
            default:
                return String.valueOf(1);
        }
    }
}
