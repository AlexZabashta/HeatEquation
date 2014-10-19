public class TestSolver implements HeatEquationSolver {

	@Override
	public double[][] solve(double[] f, int n, double k, double u, double dt, double dx) {
		int m = f.length;
		double[][] ans = new double[n][];
		for (int i = 1; i < n; i++) {
			double[] prevLayer = ans[i - 1], curLayer = new double[m];

			for (int j = 0; j < m; j++) {
				curLayer[j] = (prevLayer[j] + prevLayer[(j + 1) % m]) / 2;
			}

			ans[i] = curLayer;
		}

		return ans;
	}

}
