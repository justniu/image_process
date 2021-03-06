package watermark;

// 程式名稱：zigZag.java
// 程式功能：zigZag 類別,含two2one(二維轉一維)與one2two
// (一維轉二維)兩種zigzag scan方法

public class ZigZag {
    static int N = 3;

    void one2two(final int input[], final int output[][]) {
        int n = 0, x = 0, y = 0;
        output[x][y] = input[n];
        n++;
        while (true) {
            if (x == 0 && y <= N - 2) {
                y++;
                output[x][y] = input[n];
                n++;
                while (true) {
                    x++;
                    y--;
                    output[x][y] = input[n];
                    n++;

                    if (y == 0) {
                        break;
                    }
                }
            }

            if (y == 0 && x <= N - 2) {
                x++;
                output[x][y] = input[n];
                n++;
                while (true) {
                    x--;
                    y++;
                    output[x][y] = input[n];
                    n++;

                    if (x == 0) {
                        break;
                    }
                }
            }

            if (x == N - 1 && y < N - 2) {
                y++;
                output[x][y] = input[n];
                n++;
                while (true) {
                    x--;
                    y++;
                    output[x][y] = input[n];
                    n++;

                    if (y == N - 1) {
                        break;
                    }
                }
            }

            if (y == N - 1 && x < N - 2) {
                x++;
                output[x][y] = input[n];
                n++;
                while (true) {
                    x++;
                    y--;
                    output[x][y] = input[n];
                    n++;

                    if (x == N - 1) {
                        break;
                    }
                }
            }

            if (x == N - 1 && y == N - 2) {
                y++;
                output[x][y] = input[n];
                break;
            }

        }// while

    }// 1 Dimension --> 2 Dimension

    void two2one(final int input[][], final int output[]) {
        int n = 0, x = 0, y = 0;
        output[n] = input[x][y];
        n++;
        while (true) {

            if (x == 0 && y <= N - 2) {
                y++;
                output[n] = input[x][y];
                n++;
                while (true) {
                    x++;
                    y--;
                    output[n] = input[x][y];
                    n++;
                    if (y == 0) {
                        break;
                    }
                }
            }

            if (y == 0 && x <= N - 2) {
                x++;
                output[n] = input[x][y];
                n++;
                while (true) {
                    x--;
                    y++;
                    output[n] = input[x][y];
                    n++;
                    if (x == 0) {
                        break;
                    }
                }
            }

            if (x == N - 1 && y < N - 2) {
                y++;
                output[n] = input[x][y];
                n++;
                while (true) {
                    x--;
                    y++;
                    output[n] = input[x][y];
                    n++;
                    if (y == N - 1) {
                        break;
                    }
                }
            }

            if (y == N - 1 && x < N - 2) {
                x++;
                output[n] = input[x][y];
                n++;
                while (true) {
                    x++;
                    y--;
                    output[n] = input[x][y];
                    n++;

                    if (x == N - 1) {
                        break;
                    }
                }
            }

            if (x == N - 1 && y == N - 2) {
                y++;
                output[n] = input[x][y];
                break;
            }

        }// while
    }// 2 Dimension --> 1 Dimension

    public static void main(String[] args) {
        ZigZag zigZag = new ZigZag();
        int[][] temp ={
                {1,2,3},
                {4,5,6},
                {7,8,9},
        };

        int[] mid = new int[9];
        zigZag.two2one(temp, mid);
        for(int i: mid){
            System.out.println(i);
        }
    }
}