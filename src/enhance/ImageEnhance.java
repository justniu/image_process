package enhance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageEnhance {
    private static int width;
    private static int height;

    public static int getWidth() {
        return width;
    }

    public static void setWidth(int width) {
        ImageEnhance.width = width;
    }

    public static int getHeight() {
        return height;
    }

    public static void setHeight(int height) {
        ImageEnhance.height = height;
    }



    public static int[] imageRead(String path) throws IOException {
        File file = new File(path);
        BufferedImage img = ImageIO.read(file);
        int height = img.getHeight();
        int width = img.getWidth();
        ImageEnhance.setWidth(width);
        ImageEnhance.setHeight(height);
        int[] rgb=new int[width*height];
        // getRGB返回的是ARGB模型：alpha,red,green,blue
        return img.getRGB(0, 0, width, height, rgb, 0, width);
    }

    /**
     * 均值滤波器
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static int[] averageFilter(int[] img, int width, int height){
        int[] res_img = new int[width*height];
        for(int i=1; i<height-1; i++){
            for(int j=1; j<width-1; j++){
                res_img[j+i*width] = img[j+i*width];
                int alpha = (res_img[j+i*width]>>24)&0xff;
                int sum1 = 0;
                int sum2 = 0;
                int sum3 = 0;
                for(int r=-1; r<2; r++){
                    for(int c =-1; c<2;c++){
                        int temp1 = img[j + r +(c+i)*width]&0xff;
                        int temp2 = (img[j + r +(c+i)*width]>>8)&0xff;
                        int temp3 = (img[j + r +(c+i)*width]>>16)&0xff;
                        sum1 += temp1;
                        sum2 += temp2;
                        sum3 += temp3;
                    }
                }
                int res1 = (int)(sum1/9.0f);
                int res2 = (int)(sum2/9.0f);
                int res3 = (int)(sum3/9.0f);
                int res = res3 << 16 | res2 << 8 | res1;
                res_img[j+i*width] = alpha << 24 | res; //为什么这里下标写错报错在其他位置！！！！
            }
        }
        return res_img;
    }

    /**
     * 中值滤波器
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static int[] midFilter(int[] img, int width, int height){
        int[] res_img = new int[width*height];
        for(int i=1; i<height-1; i++){
            for(int j=1; j<width-1; j++){
                res_img[j+i*width] = img[j+i*width];
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for(int r=-1; r<2; r++){
                    for(int c =-1; c<2;c++){
                        temp.add(img[j + r +(c+i)*width]);
                    }
                }
                temp.sort(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }
                });
                res_img[j+i*width] = temp.get(4);
            }
        }
        return res_img;
    }

    /**
     * 直方图均衡化
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static int[] histogramEqulization(int[] img, int width, int height){
        int[] countR = new int[256];
        int[] countG = new int[256];
        int[] countB = new int[256];
        int[] res_img = new int[width*height];
        int[] alpha = new int[width*height];

        //统计直方图
        for(int i=1; i<height-1; i++){
            for(int j=1; j<width-1; j++){
                alpha[j+i*width] = (img[j+i*width]>>24)&0xff;
                int numB = img[j+i*width]&0xff;
                int numG = (img[j+i*width]>>8)&0xff;
                int numR = (img[j+i*width]>>16)&0xff;
                countR[numR]+=1;
                countG[numG]+=1;
                countB[numB]+=1;
            }
        }


        //转化为出现频率
        double freqR[] = new double[256];
        double freqG[] = new double[256];
        double freqB[] = new double[256];
        for(int i=0; i<256; i++){
            freqR[i] = 1.0*countR[i]/(width*height);
            freqG[i] = 1.0*countG[i]/(width*height);
            freqB[i] = 1.0*countB[i]/(width*height);
        }

        //累加操作
        for(int i =1; i<256; i++){
            freqR[i]=freqR[i]+freqR[i-1];
            freqG[i]=freqG[i]+freqG[i-1];
            freqB[i]=freqB[i]+freqB[i-1];
        }

        //均衡化
        for(int i = 0; i<256; i++){
            countR[i]=(int)(freqR[i]*255);
            countG[i]=(int)(freqG[i]*255);
            countB[i]=(int)(freqB[i]*255);
        }


        //转化为均衡化的值
        for(int i=1; i<height-1; i++){
            for(int j=1; j<width-1; j++){
                alpha[j+i*width] = (res_img[j+i*width]>>24)&0xff;
                int numB = img[j+i*width]&0xff;
                int numG = (img[j+i*width]>>8)&0xff;
                int numR = (img[j+i*width]>>16)&0xff;
                res_img[j+i*width]= alpha[j+i*width]<<24| countR[numR]<<16 | countG[numG]<< 8 | countB[numB];
            }
        }

        return res_img;
    }

    public static void main(String[] args) throws IOException {
        int[] img1 = imageRead("images/hat.jpg");
        for(int i=0; i<10;i++){
            img1 = midFilter(img1, ImageEnhance.getWidth(), ImageEnhance.getHeight());
        }

        BufferedImage bufferedImage1 = new BufferedImage(ImageEnhance.getWidth(), ImageEnhance.getHeight(), BufferedImage.TYPE_INT_RGB);
        bufferedImage1.setRGB(0,0,ImageEnhance.getWidth(), ImageEnhance.getHeight(), img1, 0, ImageEnhance.getWidth());
        ImageIO.write(bufferedImage1, "jpg", new File("images/resHat.jpg"));

        int[] img2 = imageRead("images/fog.jpg");
        img2 = histogramEqulization(img2, ImageEnhance.getWidth(), ImageEnhance.getHeight());

        BufferedImage bufferedImage2 = new BufferedImage(ImageEnhance.getWidth(), ImageEnhance.getHeight(), BufferedImage.TYPE_INT_RGB);
        bufferedImage2.setRGB(0,0,ImageEnhance.getWidth(), ImageEnhance.getHeight(), img2, 0, ImageEnhance.getWidth());
        ImageIO.write(bufferedImage2, "jpg", new File("images/resFog.jpg"));

    }
}
