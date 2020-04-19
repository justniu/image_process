package watermark;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utills {

    private static int width;
    private static  int height;
    private static int size;


    public static int[] imageRead(String path) throws IOException {
        File file = new File(path);
        BufferedImage img = ImageIO.read(file);
        int height = img.getHeight();
        int width = img.getWidth();
        WaterMark.setWidth(width);
        WaterMark.setHeight(height);
        int[] rgb=new int[width*height];
        // getRGB返回的是ARGB模型：alpha,red,green,blue
        return img.getRGB(0, 0, width, height, rgb, 0, width);
    }

    public static int[] rgb2gray(int[] img, int width, int height) throws IOException {

        int[] gray = new int[width*height];
        int j = 0;
        for(int i: img){
            int numB = i&0xff;
            int numG = (i>>8)&0xff;
            int numR = (i>>16)&0xff;
            int grayPixel = (numR*299 + numG*587 + numB*114 + 500) / 1000;
            int pixel = 255<<24 | grayPixel << 16 | grayPixel << 8 | grayPixel;
            gray[j++] = pixel;
        }

        return gray;
    }

    public static double[][] dctConvert(double[][] img, int iw, int ih, int size, int type){
        DCT dct = new DCT(size);

        int iter_num_row = iw / size;
        int iter_num_col  = ih / size;

        double[][] dct_image = new double[ih][iw];
        double[][] imgBlock = new double[size][size]; // 8x8 dct处理区域

        if (type == 1)
        {
            for (int j = 0; j < iter_num_row; j++)
            {
                for (int i = 0; i < iter_num_col; i++)
                {
                    for (int k = 0; k < size; k++)
                        for (int l = 0; l < size; l++)
                            imgBlock[k][l] = img[i * size + k][j * size + l];

                    dct.dct(imgBlock, dct.getCoefft(), dct.getCoeff());

                    for (int k = 0; k < size; k++)
                        for (int l = 0; l < size; l++)
                            dct_image[i * size + k][j * size + l] = imgBlock[k][l];
                }
            }
        }
        else
        {
            for (int j = 0; j < iter_num_row; j++)
            {
                for (int i = 0; i < iter_num_col; i++)
                {
                    for (int k = 0; k < size; k++)
                        for (int l = 0; l < size; l++)
                            imgBlock[k][l] = img[i * size + k][j * size + l];

                    dct.dct(imgBlock, dct.getCoeff(), dct.getCoefft());

                    for (int k = 0; k < size; k++)
                        for (int l = 0; l < size; l++)
                            dct_image[i * size + k][j * size + l] = imgBlock[k][l];
                }
            }
        }
        return dct_image;


    }

    public static BufferedImage getText(String str, int iw, int ih){
        BufferedImage bufferedImage = new BufferedImage(iw, ih, BufferedImage.TYPE_BYTE_BINARY);
        Graphics graphics = bufferedImage.getGraphics();
        // graphics.setFont(new Font("", Font.PLAIN, Font.BOLD));
        int length = str.length();
        for(int i=0; i<length;i++){
            char ch = str.charAt(i);
            graphics.drawString(ch+"", iw/(length+1)*(i+1), ih/2);
        }
        return bufferedImage;
    }


    public static void embedWatermark(double[][] dctImg, BufferedImage mark, int iw, int ih, int size){
        int[] pixels = new int[(iw/size)*(ih/size)];
        mark.getRGB(0, 0, iw/size, ih/size, pixels, 0, iw/size);

        int factor = 16;

        int iter_num_row = iw / size;
        int iter_num_col  = ih / size;
        for (int j = 0; j < iter_num_row; j++)
        {
            for (int i = 0; i < iter_num_col; i++)
            {
                int flag = -1;
                if(dctImg[i*size+4][j*size+4] > 0)
                    flag = flag*-1;
                int dc = (int)Math.abs(dctImg[i*size+4][j*size+4]);
                int rmd = dc % factor;
                if((pixels[i*iter_num_row+j]&1)==0){
                    if(rmd >=0 && rmd <3*factor/4)
                        dc = dc - rmd + factor/4;
                    else
                        dc = dc - rmd + 5*factor/4;
                }else {
                    if(rmd >=0 && rmd<factor/4)
                        dc = dc +rmd - factor/4;
                    else
                        dc = dc-rmd+3*factor/4;
                }

                dctImg[i*size+4][j*size+4] = dc*flag;

            }
        }

    }


    public static void etcWatermark(double[][] dctImg,int[] pixels, int iw, int ih, int size){

        int factor = 16;

        int iter_num_row = iw / size;
        int iter_num_col  = ih / size;
        for (int j = 0; j < iter_num_row; j++)
        {
            for (int i = 0; i < iter_num_col; i++)
            {

                int dc = (int)Math.abs(dctImg[i*size+4][j*size+4]);

                int rmd = dc % factor;

                if(rmd > 0 && rmd < factor/2)
                    pixels[i*iter_num_row+j] = 0;
                else
                    pixels[i*iter_num_row+j] = 1;

            }
        }
    }

    public static void one2Two(int[] a, double[][] b, int w, int h){
        for(int i = 0; i < h; i++)
            for(int j = 0; j < w; j++)
                b[i][j] = a[i*w + j];
    }

    public static void extract(String wmImg, String srcImg) throws IOException {
        int[] img = Utills.imageRead(wmImg);
        int[] src = Utills.imageRead(srcImg);


        int height = WaterMark.getHeight();
        int width = WaterMark.getWidth();

        int[] wmR = new int[width*height];
        int[] wmG = new int[width*height];
        int[] wmB = new int[width*height];
        Utills.splitRGB(img, wmR, wmG, wmB);

        int[] srcR = new int[width*height];
        int[] srcG = new int[width*height];
        int[] srcB = new int[width*height];

        Utills.splitRGB(src, srcR,srcG, srcB);

        double[] rate = new double[width*height];
        for(int i=0; i<width*height; i++){
            rate[i] = (wmR[i]*1.0/srcR[i]+wmG[i]*1.0/srcG[i]+wmB[i]*1.0/srcB[i])/3;
        }

        int[] srcGray = Utills.rgb2gray(src, width, height);
        Utills.grayConvert(srcGray, rate);

        double[][] dctGray = new double[height][width];
        Utills.one2Two(srcGray, dctGray, width, height);

        dctGray = Utills.dctConvert(dctGray, width, height, 8, 1);

        int[] pixels = new int[(width/8)*(height/8)];

        Utills.etcWatermark(dctGray, pixels, width, height, 8);
        for(int i=0; i<pixels.length;i++){
            if(pixels[i] == 1 )
                pixels[i] = -1;
            if(pixels[i] == 0)
                pixels[i] = -16777216;
        }

        BufferedImage bufferedImage = new BufferedImage(width / 8, height / 8, BufferedImage.TYPE_BYTE_BINARY);
        bufferedImage.setRGB(0, 0, width/8, height/8, pixels, 0, width/8);
        ImageIO.write(bufferedImage, "jpg", new File("images/res.jpg"));


    }

    public static void extract(int[] img, String srcImg) throws IOException {
        int[] src = Utills.imageRead(srcImg);


        int height = WaterMark.getHeight();
        int width = WaterMark.getWidth();

        int[] wmR = new int[width*height];
        Utills.splitR(img, wmR);

        int[] srcR = new int[width*height];

        Utills.splitR(src, srcR);

        double[] rate = new double[width*height];
        for(int i=0; i<width*height; i++){
            rate[i] = wmR[i]*1.0/srcR[i];
        }

        int[] srcGray = Utills.rgb2gray(src, width, height);
        Utills.grayConvert(srcGray, rate);

        double[][] dctGray = new double[height][width];
        Utills.one2Two(srcGray, dctGray, width, height);

        dctGray = Utills.dctConvert(dctGray, width, height, 8, 1);

        int[] pixels = new int[(width/8)*(height/8)];

        Utills.etcWatermark(dctGray, pixels, width, height, 8);
        for(int i=0; i<pixels.length;i++){
            if(pixels[i] == 1 )
                pixels[i] = -1;
            if(pixels[i] == 0)
                pixels[i] = -16777216;
        }

        BufferedImage bufferedImage = new BufferedImage(width / 8, height / 8, BufferedImage.TYPE_BYTE_BINARY);
        bufferedImage.setRGB(0, 0, width/8, height/8, pixels, 0, width/8);
        ImageIO.write(bufferedImage, "png", new File("images/res333.png"));
    }

    public static void embed(String srcImg) throws IOException {
        int[] sImg = Utills.imageRead(srcImg);

        int height = WaterMark.getHeight();
        int width = WaterMark.getWidth();


        int[] imgR = new int[width*height];
        int[] imgG = new int[width*height];
        int[] imgB = new int[width*height];
        int[] alpha = new int[width*height];

        for(int i=0; i<sImg.length; i++){
            int color = sImg[i];
            alpha[i] = (color>>24&0xff);
            imgR[i] = (color >>16 & 0xff );
            imgG[i] = (color >>8 & 0xff);
            imgB[i] = color & 0xff;
        }

        // 获取灰度图
        sImg = Utills.rgb2gray(sImg, width, height);
        double[][] rgbArray = new double[height][width];
        Utills.one2Two(sImg, rgbArray, width, height);

        rgbArray = Utills.dctConvert(rgbArray, width, height, 8, 1);

        BufferedImage hello = Utills.getText("hello", width / 8, height / 8);

        Utills.embedWatermark(rgbArray, hello, width, height, 8);

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
            int temp2 = sImg[i]&0xff;
            if(temp2!=0){
                double rate = temp1*1.0/temp2;
                imgR[i] = (int)Math.ceil(imgR[i]*rate);
                imgG[i] = (int)Math.ceil(imgG[i]*rate);
                imgB[i] = (int)Math.ceil(imgB[i]*rate);
            }
        }

        int[] test = new int[width*height];
        for(int i=0; i<width*height; i++){
            test[i] = alpha[i]<<24| imgR[i]<<16|imgG[i]<<8|imgB[i];
        }

        Utills.extract(test, "images/gakki-src.png");

        BufferedImage bufferedImage2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedImage2.setRGB(0, 0, width, height, test, 0, width);
        ImageIO.write(bufferedImage2, "png", new File("images/src2.png"));

    }

    public static void grayConvert(int[] src, double[] rate){
        for(int i=0; i<src.length; i++){
            int before = src[i]&0xff;
            int after = (int)Math.ceil(before*rate[i]);
            src[i] = 255<<24|after<<16|after<<8|after;
        }
    }


    public static void splitR(int[] img, int[] R){
        for(int i=0; i<img.length; i++){
            int color = img[i];
            R[i] = (color >>16 & 0xff );
        }
    }

    public static void splitRGB(int[] img, int[] R, int[] G, int[] B){
        for(int i=0; i<img.length; i++){
            int color = img[i];
            R[i] = (color >>16 & 0xff );
            G[i] = (color >>8 & 0xff);
            B[i] = color & 0xff;
        }
    }


    public static void main(String[] args) throws IOException {

        Utills.embed("images/gakki-src.png");

        Utills.extract("images/src2.png", "images/gakki-src.png");

        // int[] ints = Utills.imageRead("images/src1.png");
        //
        // int height = WaterMark.getHeight();
        // int width = WaterMark.getWidth();
        // //灰度化后竟然提取不到了？？？
        // // int[] ints1 = Utills.rgb2gray(ints, width, height);
        //
        // double[][] rgbArray = new double[height][width];
        //
        // Utills.one2Two(ints, rgbArray, width, height);
        //
        // rgbArray = Utills.dctConvert(rgbArray, width, height, 8, 1);
        //
        // int[] pixels = new int[(width/8)*(height/8)];
        //
        // Utills.etcWatermark(rgbArray, pixels, width, height, 8);
        // for(int i=0; i<pixels.length;i++){
        //     if(pixels[i] == 1 )
        //         pixels[i] = -1;
        //     if(pixels[i] == 0)
        //         pixels[i] = -16777216;
        // }
        //
        // BufferedImage bufferedImage = new BufferedImage(width / 8, height / 8, BufferedImage.TYPE_BYTE_BINARY);
        // bufferedImage.setRGB(0, 0, width/8, height/8, pixels, 0, width/8);
        // ImageIO.write(bufferedImage, "png", new File("images/resssss.png"));



        // int[] img = Utills.imageRead("images/src2.png");
        // int height = WaterMark.getHeight();
        // int width = WaterMark.getWidth();
        // img = Utills.rgb2gray(img, width, height);
        // double[][] rgbArray = new double[height][width];
        // for(int i = 0; i < height; i++)
        //     for(int j = 0; j < width; j++)
        //         rgbArray[i][j] = img[i*width + j];
        //
        //
        // int[] pixels = new int[width*height/64];
        // rgbArray = Utills.dctConvert(rgbArray, width, height, 8, 1);
        // Utills.etcWatermark(rgbArray, pixels, width, height, 8);
        // for(int i=0; i<pixels.length;i++){
        //     if(pixels[i] == 0 )
        //         pixels[i] = -1;
        //     if(pixels[i] == 1)
        //         pixels[i] = -16777216;
        // }
        //
        // BufferedImage bufferedImage = new BufferedImage(width / 8, height / 8, BufferedImage.TYPE_BYTE_BINARY);
        // bufferedImage.setRGB(0, 0, width/8, height/8, pixels, 0, width/8);
        // ImageIO.write(bufferedImage, "jpg", new File("images/output.jpg"));
    }
}
