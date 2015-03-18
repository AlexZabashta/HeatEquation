public class UpstreamImplicit implements HeatEquationSolver {

	@Override
	public double[][] solve(double[] T_0, int n, double k, double u, double dt, double dx) {
		double[][] ans = new double[n][];
		ans[0] = T_0;
        double S = u * dt / dx;
        double R = k * dt / dx / dx;
		for (int layer = 1; layer < n; layer++)
            ans[layer] = Sweep.sweep(ans[layer - 1], -R - S, 1 + S + 2 * R, -R);
            /*
                T[i, n + 1] - T[i, n]   k (T[i + 1, n + 1] - 2T[i, n + 1] + T[i - 1, n + 1])   u (T[i, n + 1] - T[i - 1, n + 1])
                ————————————————————— = ———————————————————————————————————————————————————— - —————————————————————————————————
                         dt                                    dx * dx                                        dx                   

                 n             n + 1                 n + 1      n + 1
                T  = (-R - S) T      + (1 + S + 2R) T      - R T
                 i             i - 1                 i          i + 1                                                    */                   
		return ans; 
	}
}
