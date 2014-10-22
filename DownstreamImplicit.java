public class DownstreamImplicit implements HeatEquationSolver {

    public double[] sweep(double[] d, double a, double b, double c) {
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

	@Override
	public double[][] solve(double[] T_0, int n, double k, double u, double dt, double dx) {
		double[][] ans = new double[n][];
		ans[0] = T_0;
        double S = u * dt / dx;
        double R = k * dt / dx / dx;
		for (int layer = 1; layer < n; layer++)
            ans[layer] = sweep(ans[layer - 1], R, 1 + S - 2 * R, R - S);
            /*
                T[i, n + 1] - T[i, n]   k (T[i + 1, n + 1] - 2T[i, n + 1] + T[i - 1, n + 1])   u (T[i + 1, n + 1] - T[i, n + 1])
                ————————————————————— = ———————————————————————————————————————————————————— - —————————————————————————————————
                         dt                                    dx * dx                                        dx                   

                 n      n + 1                 n + 1            n + 1
                T  = R T      + (1 + S - 2R) T      + (R - S) T
                 i      i - 1                 i                i + 1                                                    */                   
		return ans;
	}
}
