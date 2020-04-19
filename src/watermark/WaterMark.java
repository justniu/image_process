package watermark;


import DigitalImage.process.imageTrans.WHT2;
import enhance.ImageEnhance;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class WaterMark {

    private static int width;

    private static int height;

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        WaterMark.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        WaterMark.height = height;
    }





    public static void main(String[] args) throws IOException {

        int[] img = Utills.imageRead("images/gakki-src.png");


        int height = WaterMark.getHeight();
        int width = WaterMark.getWidth();


        int[] imgR = new int[width*height];
        int[] imgG = new int[width*height];
        int[] imgB = new int[width*height];
        int[] alpha = new int[width*height];

        for(int i=0; i<img.length; i++){
            int color = img[i];
            alpha[i] = (color>>24&0xff);
            imgR[i] = (color >>16 & 0xff );
            imgG[i] = (color >>8 & 0xff);
            imgB[i] = color & 0xff;
        }


        // 获取灰度图
        img = Utills.rgb2gray(img, width, height);
        double[][] rgbArray = new double[height][width];
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                rgbArray[i][j] = img[i*width + j];


        rgbArray = Utills.dctConvert(rgbArray, width, height, 8, 1);

        BufferedImage hello = Utills.getText("hello", width / 8, height / 8);

        Utills.embedWatermark(rgbArray, hello, width, height, 8);

        rgbArray = Utills.dctConvert(rgbArray, width, height, 8, -1);

        //含水印灰度图
        rgbArray = Utills.dctConvert(rgbArray, width, height, 8, 1);


        int[] haha = new int[(width/8)*(height/8)];

        Utills.etcWatermark(rgbArray, haha, width, height, 8);

        for(int i=0; i<haha.length;i++){
            if(haha[i] == 1 )
                haha[i] = -1;
            if(haha[i] == 0)
                haha[i] = -16777216;
        }

        BufferedImage bufferedImage = new BufferedImage(width / 8, height / 8, BufferedImage.TYPE_BYTE_BINARY);
        bufferedImage.setRGB(0, 0, width/8, height/8, haha, 0, width/8);
        ImageIO.write(bufferedImage, "png", new File("images/output4.png"));




        rgbArray = Utills.dctConvert(rgbArray, width, height, 8, -1);



        //IDCT后的灰度图
        int[] data = new int[width*height];
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                data[i*width + j] =(int)rgbArray[i][j];

        BufferedImage bufferedImage1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage1.setRGB(0, 0, width, height, data, 0, width);
        ImageIO.write(bufferedImage1, "png", new File("images/src1.png"));



        for(int i=0; i<width*height; i++){
            int temp1 = data[i]&0xff;
            int temp2 = img[i]&0xff;
            if(temp2!=0){
                double rate = temp1*1.0/temp2;
                imgR[i] = (int)(imgR[i]*rate);
                imgG[i] = (int)(imgG[i]*rate);
                imgB[i] = (int)(imgB[i]*rate);
            }
        }

        int[] test = new int[width*height];
        for(int i=0; i<width*height; i++){
            test[i] = alpha[i]<<24| imgR[i]<<16|imgG[i]<<8|imgB[i];
        }

        BufferedImage bufferedImage2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage2.setRGB(0, 0, width, height, test, 0, width);
        ImageIO.write(bufferedImage2, "png", new File("images/src2.png"));
    }



}
