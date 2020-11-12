package com.lxl.essence;

import org.junit.Test;

import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        int count = 20;
        Random random =new Random();
        int[] a =new int[count] ;
        int[] b =new int[count] ;
        int[] c =new int[count] ;
        for (int i = 0; i < count; i++) {
            a[i] = 500 + random.nextInt(500);
            b[i] = 5 + random.nextInt(5);
            c[i] = a[i] * b[i];
            System.out.printf("%2d. %4d x %2d = %4d\n", i+1,a[i], b[i], c[i]);
        }

    }


    @Test
    public void muitplay() {
        int count = 10;
        Random random =new Random();
        int[] a =new int[count] ;
        int[] b =new int[count] ;
        int[] c =new int[count] ;
        for (int i = 0; i < count; i++) {
            a[i] =10 + random.nextInt(90);
            b[i] = 10 + random.nextInt(90);
            c[i] = a[i] * b[i];
            System.out.printf("%2d. %4d X %2d = %4d\n", i+1,a[i], b[i], c[i]);
        }

    }
}