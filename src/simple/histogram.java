package simple;

import java.io.*;

public class histogram {
    public static void save(String path){
        File file= new File("images/B.bmp");
        FileInputStream fis=null;
        BufferedWriter bw=null;
        try {
            fis = new FileInputStream(file);
            bw= new BufferedWriter(new FileWriter(path));
            byte[] bmpheader = new byte[1078];
            byte[] bmpline = new byte[512];
            int[] countGray = new int[256];
            // 读取表头
            fis.read(bmpheader);

            for(int h = 0; h < 512; h++){
                fis.read(bmpline);
                for(int i = 0; i < 512; i++){
                    if(bmpline[i]<0){
                        countGray[256+bmpline[i]]++;
                    }else{
                        countGray[bmpline[i]]++;

                    }

                }
            }

            for(int i = 0; i < 256; i++){
               bw.write(i+"\t"+countGray[i]);
               bw.newLine();
               bw.flush();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public static void main(String[] args) {
        save("F.txt");
    }
}
