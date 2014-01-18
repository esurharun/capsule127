package com.capsule127.wordlist;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * Created by marcus on 12/01/14.
 */
public class BruteForceWordlistBuilder implements IDynamicWordlistBuilder {


    private Logger logger() {
        return Logger.getLogger("BruteForceWordlistBuilder");
    }

    public static long getTotalNumOfWords(char[] chars,int startLen,int stopLen) {

        long ret = 0;
        for (int i=startLen;i<=stopLen;i++) {

            ret += Math.pow(chars.length,i);
        }

        return ret;
    }

    public static String iterate(char[] chars,int startLen,long num) {
        int CCOUNT = chars.length;


        /*
            0           - CCOUNT                => 1 digit
            CCOUNT      - CCOUNT^2              => 2 digits
            CCOUNT^2    - CCOUNT^3              => 3 digits
            CCOUNT^3    - CCOUNT^4              => 4 digits


         */
        int digits = 0;
        long digits_s_count = 0;
        for (int i=startLen-1;;i++) {
            //Main.defaultLogger.info("num: " + num + " digits_s_count: " + digits_s_count + " i: " + i + " Math.pow(CCOUNT,i+1): " + Math.pow(CCOUNT, i + 1));
            if (num >= digits_s_count && num < (Math.pow(CCOUNT,i+1))+digits_s_count) {
                digits = i+1;
                break;
            }

            digits_s_count += Math.pow(CCOUNT,i+1);
        }

        // 16 - 5  = 11
        long to_go = num-digits_s_count;


        String ret = "";

        /*
            0  >= n <  5 //  1 digit
            5  >= n < 30 // 2 digits

            0   -   a
            1   -   b
            2   -   c
            3   -   d
            4   -   e
            5   -   aa
            6   -   ab
            7   -   ac
            8   -   ad
            9   -   ae
            10  -   ba
            11  -   bb
            12  -   bc
            13  -   bd
            14  -   be
            15  -   ca
            16  -   cb
            17  -   cc
            18  -   cd
            19  -   ce
            20  -   da
            21  -   db
            22  -   dc
            23  -   dd
            24  -   de
            25  -   ea
            26  -   eb
            27  -   ec
            28  -   ed
            29  -   ee

         */


        for (int c_digit = 0;c_digit<digits;c_digit++) {

            // c_digit = 0 => l_count 5
            // c_digit = 1 => l_count 1

            long l_count = (long) Math.pow(CCOUNT,digits-c_digit-1);


            // c_digit = 0 => remain = 1
            // c_digit = 1 => remain = 0

            long remain = to_go % l_count;

            long div = to_go / l_count;

            to_go = remain;

            ret += chars[(int)div];


        }

        return ret;

    }

    public static String iterate(char[] chars,long num) {
        return iterate(chars,1,num);
    }

    @Override
    public Vector<String> getList(Object[] params) {

        Vector<String> ret = new Vector<String>(0);

        if (params.length != 3) {

            logger().warning("Invalid params");

            return  ret;
        }


        try {

            String charlist = (String) params[0];
            Long startLen = (Long) params[1];
            Long endLen = (Long) params[2];


            char[] chArr = charlist.toCharArray();

            for (long st = startLen;st<endLen;st++) {

                ret.add(iterate(chArr,st));

            }


        } catch (Exception ex) {

            logger().warning("Exception: "+ ex.getMessage());
        }


        return ret;
    }
}
