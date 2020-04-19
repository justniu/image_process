package simple;

import java.io.*;

public class rgb2Gray {

    public static int bytes2Int(byte[] bytes ) {
        //如果不与0xff进行按位与操作，转换结果将出错
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }
    public static byte[] int2Byte(int num){
        byte[]bytes=new byte[4];
        bytes[3]=(byte) ((num>>24)&0xff);
        bytes[2]=(byte) ((num>>16)&0xff);
        bytes[1]=(byte) ((num>>8)&0xff);
        bytes[0]=(byte) (num&0xff);
        return bytes;
    }

    public static void main(String[] args) {
        File file = new File("images/A.bmp");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream("images/B.bmp");
            // 获取头文件
            byte[] bmpheader = new byte[54];
            fis.read(bmpheader);

            byte[] edge = new byte[4];
            // 获得图片宽
            System.arraycopy(bmpheader, 18, edge, 0, 4);
            int width = bytes2Int(edge);
            System.out.println("src image width-- "+width);
            // 获取图片高
            System.arraycopy(bmpheader, 22, edge, 0, 4);
            int height = bytes2Int(edge);
            System.out.println("src image height-- "+height);
            // 获取图片尺寸
            System.arraycopy(bmpheader, 34, edge, 0, 4);
            int bisize = bytes2Int(edge);
            System.out.println("src image size-- "+bisize);

            // 图片内容偏移量 1024+54
            System.arraycopy(int2Byte(54+4*256), 0, bmpheader, 10, 4);

            // 修改图片大小 512*512+1024
            System.arraycopy(int2Byte(263168), 0, bmpheader, 34, 4);

            // 修改比特数/每像素
            bmpheader[28]=8;


            //构造灰度图的调色版
            byte[][] rgbquad = new byte[256][4];

            for(int i=0;i<256;i++)
            {
                rgbquad[i][0]=(byte)(i);
                rgbquad[i][1]=(byte)(i);
                rgbquad[i][2]=(byte)(i);
                rgbquad[i][3]=(byte)(0);
            }
            // System.out.println(rgbquad[129][1]);// 有符号数？？？
            fos.write(bmpheader);
            fos.flush();

            for(int i=0;i<256;i++){
                fos.write(rgbquad[i]);
                fos.flush();
            }

            byte[] rgb = new byte[3];
            for(int h=0; h<height; h++){
                for(int w=0; w<width; w++){
                    fis.read(rgb);
                    // 灰度化
                    byte gray = (byte)(rgb[0]*0.299+rgb[1]*0.587+rgb[2]*0.114);
                    fos.write(gray);
                    fos.flush();
                }
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
}
