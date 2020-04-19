//FFT2.java

package DigitalImage.process.imageTrans;

public class FFT2
{
    int iw, ih;
    double[] pixels;
    Complex[] td;
    Complex[] fd;

    // ����ֵ
    int w = 1;
    int h = 1;
    int wp = 0;
    int hp = 0;

    //���캯��
    public FFT2() { }

    //��������
    public void setData2(int iw, int ih, double[] pixels)
    {
        this.iw = iw;
        this.ih = ih;

        this.pixels = new double[iw * ih];
        this.pixels = pixels;

        //������и���Ҷ�任�Ŀ�Ⱥ͸߶�(2�������η�)
        while (w * 2 <= iw)
        {
            w *= 2;
            wp++;
        }
        while (h * 2 <= ih)
        {
            h *= 2;
            hp++;
        }

        td = new Complex[w * h];
        fd = new Complex[w * h];

        //��ʼ��fd,td
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
                fd[i + j * iw] = new Complex();
                td[i + j * iw] = new Complex(pixels[i + j * iw], 0);
            }
        }

        //��y�����Ͻ��п��ٸ���Ҷ�任
        for (int j = 0; j < h; j++)
        {
            //ÿһ��������Ҷ�任
            Complex[] tempW1 = new Complex[w];
            Complex[] tempW2 = new Complex[w];
            for (int i = 0; i < w; i++)
            {
                tempW1[i] = new Complex(0, 0);
                tempW2[i] = new Complex(0, 0);
            }

            for (int i = 0; i < w; i++)
                tempW1[i] = td[i + j * iw];

            setData1(tempW1, wp);

            tempW2 = getData1();

            for (int i = 0; i < w; i++)
                fd[i + j * iw] = tempW2[i];
        }

        //����任���
        for (int j = 0; j < h; j++)
            for (int i = 0; i < w; i++)
                td[i + j * iw] = fd[i + j * iw];

        //��x������и���Ҷ�任
        for (int i = 0; i < w; i++)
        {
            //ÿһ��������Ҷ�任
            Complex[] tempW1 = new Complex[h];
            Complex[] tempW2 = new Complex[h];

            for (int j = 0; j < h; j++)
            {
                tempW1[j] = new Complex(0, 0);
                tempW2[j] = new Complex(0, 0);
            }

            for (int j = 0; j < h; j++)
                tempW1[j] = td[i + j * iw];

            setData1(tempW1, hp);

            tempW2 = getData1();

            for (int j = 0; j < h; j++)
                fd[i + j * iw] = tempW2[j];
        }

        for (int j = 0; j < h; j++)
            for (int i = 0; i < w; i++)
                td[i + j * w] = fd[i + j * w];
    }

    //����FFT�任���ֵ	
    public Complex[] getFFT2()
    {
        return td;
    }

    //FFT1=====================================
    //FFT1�任����
    int count;

    //ѭ������
    int i, j, k;

    //�м����
    int bfsize, p;

    int power;

    Complex[] wc, x1, x2, x;

    Complex[] fd1;

    private void setData1(Complex[] data, int power)
    {
        this.power = power;

        //�Ƕ�
        double angle;

        //����FFT1�任�ĵ���
        count = 1 << power;

        //����ռ�
        wc = new Complex[count / 2];
        x = new Complex[count];
        x1 = new Complex[count];
        x2 = new Complex[count];
        fd1 = new Complex[count];

        //��ʼ��
        for (i = 0; i < count / 2; i++)
            wc[i] = new Complex();

        for (i = 0; i < count; i++)
        {
            x[i] = new Complex();
            x1[i] = new Complex();
            x2[i] = new Complex();
            fd1[i] = new Complex();
        }

        //�����Ȩϵ��
        for (i = 0; i < count / 2; i++)
        {
            angle = -i * Math.PI * 2 / count;
            wc[i].re = Math.cos(angle);
            wc[i].im = Math.sin(angle);
        }

        //��ʵ���д��x1
        for (i = 0; i < count; i++)
            x1[i] = data[i];
    }

    private Complex[] getData1()
    {
        //��������
        for (k = 0; k < power; k++)
        {
            for (j = 0; j < 1 << k; j++)
            {
                bfsize = 1 << (power - k);
                for (i = 0; i < bfsize / 2; i++)
                {
                    Complex temp1 = new Complex(0, 0);
                    Complex temp2 = new Complex(0, 0);

                    p = j * bfsize;
                    x2[i + p] = temp1.Add(x1[i + p], x1[i + p + bfsize / 2]);

                    temp2 = temp1.Sub(x1[i + p], x1[i + p + bfsize / 2]);

                    x2[i + p + bfsize / 2] = temp1.Mul(temp2, wc[i * (1 << k)]);
                }
            }
            x = x1;
            x1 = x2;
            x2 = x;
        }

        //��������
        for (j = 0; j < count; j++)
        {
            p = 0;
            for (i = 0; i < power; i++)
                if ((j & (1 << i)) != 0)
                    p += 1 << (power - i - 1);

            fd1[j] = x1[p];
        }
        return fd1;
    }
    
    //IFF2=================================================    
    //��������
    public void setData2i(int iw, int ih, Complex[] complex)
    {
        this.iw = iw;
        this.ih = ih;
        // ����ֵ
        w = 1;
        h = 1;
        wp = 0;
        hp = 0;

        //������и���Ҷ�任�Ŀ�Ⱥ͸߶ȣ�2�������η���
        while (w * 2 <= iw)
        {
            w *= 2; wp++;
        }
        while (h * 2 <= ih)
        {
            h *= 2; hp++;
        }

        //�����ڴ�
        td = new Complex[w * h];
        fd = new Complex[w * h];

        //��ʼ��fd,td
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
                fd[i + j * iw] = new Complex(complex[i + j * iw].re, 
                                             complex[i + j * iw].im);
                td[i + j * iw] = new Complex();
            }
        }

        for (int j = 0; j < h; j++)
            for (int i = 0; i < w; i++)
                td[i + j * w] = fd[i + j * w];

        //��x�������FFT���任
        for (int i = 0; i < w; i++)
        {
            //ÿһ����FFT���任
            Complex[] tempW1 = new Complex[h];
            Complex[] tempW2 = new Complex[h];

            for (int j = 0; j < h; j++)
            {
                tempW1[j] = new Complex(0, 0);
                tempW2[j] = new Complex(0, 0);
            }

            for (int j = 0; j < h; j++)
                tempW1[j] = td[i + j * iw];

            setData1i(tempW1, hp);

            tempW2 = getData1i();

            for (int j = 0; j < h; j++)
                fd[i + j * iw] = tempW2[j];
        }

        //����任���
        for (int j = 0; j < h; j++)
            for (int i = 0; i < w; i++)
                td[i + j * iw] = fd[i + j * iw];

        //��y�����Ͻ���FFT���任
        for (int j = 0; j < h; j++)
        {
            //ÿһ����FFT���任
            Complex[] tempW1 = new Complex[w];
            Complex[] tempW2 = new Complex[w];
            for (int i = 0; i < w; i++)
            {
                tempW1[i] = new Complex(0, 0);
                tempW2[i] = new Complex(0, 0);
            }

            for (int i = 0; i < w; i++)
                tempW1[i] = td[i + j * iw];

            setData1i(tempW1, wp);

            tempW2 = getData1i();

            for (int i = 0; i < w; i++)
                fd[i + j * iw] = tempW2[i];
        }
    }

    public Complex[] getComplex2i()
    {
        return td;
    }

    //IFFT���ݹ淶�� 	
    public int[] getPixels2i()
    {
        //����ԭ����ֵ
        int r;
        int[] pix = new int[iw * ih];
        double[] tem = new double[iw * ih];
        double max = 0, temp, re, im;
        
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
                re = fd[i + j * iw].re;
                im = fd[i + j * iw].im;

                temp = Math.sqrt(re * re + im * im);
                if (max < temp)  max = temp;
                tem[i + j * iw] = temp;                
            }
        }
        
        //�淶��
        for (int j = 0; j < h; j++)
        {
            for (int i = 0; i < w; i++)
            {
            	r = (int)(tem[i + j * iw] * 255 / max);
                pix[i + j * iw] = 255 << 24|r << 16|r << 8|r;	
            }
        }
        return pix;
    }

    //IFF1==========================================
    Complex[] fd1i;

    FFT2 fft2;

    private void setData1i(Complex[] data, int power)
    {
        this.power = power;

        //���㸵��Ҷ�任�ĵ���
        count = 1 << power;

        //����ռ�
        x = new Complex[count];
        fd1i = new Complex[count];

        for (int i = 0; i < count; i++)
        {
            x[i] = new Complex();
            fd1i[i] = new Complex();
        }

        //��Ƶ���д��x
        for (int i = 0; i < count; i++)
            x[i] = data[i];
    }

    private Complex[] getData1i()
    {
        // ����
        for (int i = 0; i < count; i++)
        {
            double im = -x[i].im;
            x[i].im = im;
        }

        fft2 = new FFT2();
        fft2.setData1(x, power);
        fd1i = fft2.getData1();

        for (int i = 0; i < count; i++)
        {
            double re = fd1i[i].re;
            double im = -fd1i[i].im;
            fd1i[i].im = im / count;
            fd1i[i].re = re / count;
        }
        return fd1i;
    }
    
    //FFT���ݿ��ӻ�
    public int[] toPix(Complex[] fftData, int iw, int ih)
    {
        int[] pix = new int[iw*ih];
	    
        int u, v, r;
        for (int j = 0; j < ih; j++)
        {
            for (int i = 0; i < iw; i++)
            {
                double tem = fftData[i + j * iw].re * fftData[i + j * iw].re
                           + fftData[i + j * iw].im * fftData[i + j * iw].im;
                r = (int)(Math.sqrt(tem) / 100);
                if (r > 255) r = 255;
                
                if (i < iw / 2) u = i + iw / 2;
                else u = i - iw / 2;
                if (j < ih / 2) v = j + ih / 2;
                else v = j - ih / 2;
                
                pix[u + v * iw] = 255 << 24|r << 16|r << 8|r;
            }
        }
        return pix; 
    }         
}