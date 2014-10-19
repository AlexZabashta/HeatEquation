public class TestSolverDiffusion implements HeatEquationSolver {

	@Override
	public double[][] solve(double[] f, int n, double k, double u, double dt, double dx) {
		int m = f.length;
		double[][] ans = new double[n][];
		ans[0] = f;
		for (int i = 1; i < n; i++) {
			double[] prevLayer = ans[i - 1], curLayer = new double[m];

			for (int j = 0; j < m; j++) {
				for (int q = -10; q <= 10; q++) {
					curLayer[j] += prevLayer[(j + q + m) % m] / 21;
				}

			}

			ans[i] = curLayer;
		}

		return ans;
	}
}
