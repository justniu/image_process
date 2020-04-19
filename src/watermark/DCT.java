package watermark;

public class DCT {

    private int size;
    private double[][] coeff;
    private double[][] coefft;

    public double[][] getCoeff() {
        return coeff;
    }

    public double[][] getCoefft() {
        return coefft;
    }

    public DCT(){
        this(8);
    }

    public DCT(int N){
        this.size = N;
        this.coeff = new double[N][N];
        this.coefft = new double[N][N];
        coeff(coeff, coefft);
    }

    private void coeff(double[][] coeff, double[][] coefft){
        double sqrt_1 = 1.0 / Math.sqrt(2.0);

        for (int i = 0; i < size; i++){
            coeff[0][i] = sqrt_1;
            coefft[i][0] = coeff[0][i];
        }

        for (int i = 1; i < size; i++)
            for (int j = 0; j < size; j++){
                coeff[i][j] = Math.cos(i * Math.PI * (j + 0.5) / ((double)size));
                coefft[j][i] = coeff[i][j];
            }
    }

    public void dct(double[][] img, double[][] b, double[][] c)
    {
        double x;
        double[][] af = new double[size][size];

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                x = 0.0;
                for (int k = 0; k < size; k++)
                    x += img[i][k] * b[k][j];
                af[i][j] = x;
            }
        }
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                x = 0.0;
                for (int k = 0; k < size; k++)
                    x += c[i][k] * af[k][j];
                img[i][j] = 2.0 * x / ((double)size);
            }
        }
    }
}
