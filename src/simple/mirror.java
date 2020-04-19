package simple;

import java.io.*;
import java.util.Collection;
import java.util.Collections;

public class mirror {

    // 垂直镜像
    private static byte[] reverseArray(byte[] Array) {
        byte[] new_array = new byte[Array.length];
        for (int i = 0; i < Array.length; i++) {
            new_array[i] = Array[Array.length - i - 1];
        }
        return new_array;
    }

    // 水平镜像
    private static byte[][] reverseArray2(byte[][] Array) {
        byte[][] new_array = new byte[Array.length][Array[0].length];
        for (int i = 0; i < Array.length; i++) {
            new_array[i] = Array[Array.length - i - 1];
        }
        return new_array;
    }

    public static void mirror_v(){
        File file= new File("images/B.bmp");
        FileInputStream fis=null;
        FileOutputStream fos=null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream("images/D.bmp");
            byte[] bmpheader = new byte[1078];
            byte[][] bmpdata = new byte[512][512];
            // 读取表头
            fis.read(bmpheader);
            fos.write(bmpheader);

            for(int h = 0; h < 512; h++){
                fis.read(bmpdata[h]);
            }

            for(int h=0; h<512; h++){
                fos.write(reverseArray(bmpdata[h]));
                fos.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void mirror_h(){
        File file= new File("images/B.bmp");
        FileInputStream fis=null;
        FileOutputStream fos=null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream("images/C.bmp");
            byte[] bmpheader = new byte[1078];
            byte[][] bmpdata = new byte[512][512];
            // 读取表头
            fis.read(bmpheader);
            fos.write(bmpheader);

            for(int h = 0; h < 512; h++){
                fis.read(bmpdata[h]);
            }

            for(int h=0; h<512; h++){
                fos.write(reverseArray2(bmpdata)[h]);
                fos.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        mirror_v();
        mirror_h();
    }
}
