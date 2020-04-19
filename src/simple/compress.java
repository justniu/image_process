package simple;

import java.io.*;

public class compress {
    public static byte[] int2Byte(int num){
        byte[]bytes=new byte[4];
        bytes[3]=(byte) ((num>>24)&0xff);
        bytes[2]=(byte) ((num>>16)&0xff);
        bytes[1]=(byte) ((num>>8)&0xff);
        bytes[0]=(byte) (num&0xff);
        return bytes;
    }


    public static void imageCompress(int proportion){
        File file= new File("images/B.bmp");
        FileInputStream fis=null;
        FileOutputStream fos=null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream("images/E.bmp");
            int new_h = 512/proportion;
            int new_w = 512/proportion;
            byte[] bmpheader = new byte[1078];
            byte[] temp = new byte[512];
            byte[] bmpline = new byte[new_h];
            // 读写表头
            fis.read(bmpheader);
            // 修改图片大小 new_h*new_w+1024
            System.arraycopy(int2Byte(66560), 0, bmpheader, 34, 4);
            // 修改图片宽
            System.arraycopy(int2Byte(new_w), 0, bmpheader, 18, 4);
            // 修改图片高
            System.arraycopy(int2Byte(new_h), 0, bmpheader, 22, 4);
            fos.write(bmpheader);
            fos.flush();

            for(int h = 0; h < 512; h++){

                if(h%proportion == 0){
                    fis.read(temp);
                    int i = 0;
                    for(int w = 0; w < 512; w++){

                        if(w%proportion == 0){
                            bmpline[i++] = temp[w];
                        }

                    }
                    fos.write(bmpline);
                    fos.flush();
                }else{
                    fis.read(temp);
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
    public static void main(String[] args) {
        imageCompress(2);
    }
}
