public class Sweep {
    public static double[] sweep(double[] d, double a, double b, double c) {
        int n = d.length;
        double[] cc = new double[n];
        double[] dd = new double[n];
        double[] x = new double[n];
        cc[0] = c / b;
        dd[0] = d[0] / b;
        for (int i = 1; i < n; ++i) {
            cc[i] = c / (b - a * cc[i - 1]);
            dd[i] = (d[i] - a * dd[i - 1]) / (b - a * cc[i - 1]);
        }
        x[n - 1] = dd[n - 1];
        for (int i = n - 2; i >= 0; --i)
            x[i] = dd[i] - cc[i] * x[i + 1];
        return x;
    }
}
