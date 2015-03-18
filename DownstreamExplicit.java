import java.util.ArrayList;

public class DownstreamExplicit implements HeatEquationSolver {

	@Override
	public double[][] solve(double[] T_0, int n, double k, double u, double dt, double dx) {
		double[][] ans = new double[n][];
		int m = T_0.length;
		ans[0] = T_0;
		for (int layer = 1; layer < n; layer++) {
			double[] T        = ans[layer - 1];
            double[] curLayer = new double[m];

			for (int i = 1; i < m - 1; i++)
                curLayer[i] = T[i] + dt * (k * (T[i + 1] - 2 * T[i] + T[i - 1]) / dx / dx - u * (T[i + 1] - T[i]) / dx);/*
                                        dt k (T[i + 1, n] - 2T[i, n] + T[i - 1, n])   dt u (T[i + 1, n] - T[i, n])
                T[i, n + 1] = T[i, n] + ——————————————————————————————————————————— - ————————————————————————————
                                                         dx * dx                                   dx                   */                   
            curLayer[0    ] = T[0    ];
            curLayer[m - 1] = T[m - 1];
			ans[layer] = curLayer;
		}
		return ans;
	}
}
