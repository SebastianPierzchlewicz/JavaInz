package pl.inz.praca.WsbPracaInz.helper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MathHelper {

    /**
     * return percent of number
     * np. 10 percent out of 100 = 10
     */
    public static int getPercentOfNumber(int number, double percent) {
        return (int) (percent * number / 100);
    }

    public static double getPercentOfNumber(double number, double percent) {
        return percent * number / 100;
    }

    public static double getPercentOfNumberDouble(double number, double percent) {


        return percent * number / 100;
    }

    public static float getPercentOfNumberFloat(float number, float percent) {
        return percent * number / 100;
    }

    public static double getPercentFromNumber(final double value, final double maxValue) {
        return value * 100 / maxValue;
    }


    /**
     * return percent of number and adding to number
     * np. 10 percent out of 100 = 10 + 100 = 110
     */

    public static int getPercentOfNumberAndAdd(int number, double percent) {
        return number + getPercentOfNumber(number, percent);
    }

    public static double getPercentOfNumberAndAdd(double number, double percent) {
        return number + getPercentOfNumber(number, percent);
    }

    public static double getPercentOfNumberDoubleAndAdd(double number, double percent) {
        return number + getPercentOfNumberDouble(number, percent);
    }

    public static float getPercentOfNumberFloatAndAdd(float number, float percent) {
        return number + getPercentOfNumberFloat(number, percent);
    }


    /**
     * return number by percent of number
     * np. 35 percent of out 200 = 70
     */


    public static int getNumberByPercentOfNumber(int number, double percent) {
        return (int) (percent * number / 100);
    }

    public static float getNumberByPercentOfNumberFloat(float number, float percent) {
        return (percent * number / 100);
    }

    public static double getNumberByPercentOfDouble(double number, double percent) {
        return (percent * number / 100);
    }

    public static int getNumberAndAddByPercent(int number, int addPercent) {
        return number + getNumberByPercentOfNumber(number,addPercent);
    }

    public static double getNumberAndAddByPercent(double number, double addPercent) {
        return number + getNumberByPercentOfDouble(number,addPercent);
    }

    public static float getNumberAndAddByPercent(float number, float addPercent) {
        return number + getNumberByPercentOfNumberFloat(number,addPercent);
    }


    public static int getNumberAndAddRemovePercent(int number, int addPercent) {
        return number - getNumberByPercentOfNumber(number,addPercent);
    }

    public static double getNumberAndRemoveByPercent(double number, double addPercent) {
        return number - getNumberByPercentOfDouble(number,addPercent);
    }

    public static float getNumberAndRemoveByPercent(float number, float addPercent) {
        return number - getNumberByPercentOfNumberFloat(number,addPercent);
    }

    public static double round(double value, int decimals) {
        double p = Math.pow(10, decimals);
        return Math.round(value * p) / p;
    }

    public static double decimalFormat(double value,final String pattern) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat(pattern, decimalFormatSymbols);
        df.setRoundingMode(RoundingMode.DOWN);
        return Double.parseDouble(df.format(value));
    }

    public static double countBmi(double avgWeight, double avgHeight) {
        final double heightInM = avgHeight /100;
        return MathHelper.round(avgWeight/Math.pow(heightInM,2),2);
    }
}
