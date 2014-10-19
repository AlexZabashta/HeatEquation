public interface HeatEquationSolver {

	/**
	 * @param f - first layer
	 * @param n - number of layers
	 * @param k - thermal coefficient
	 * @param u - speed of transfer
	 * @param dt - delta t
	 * @param dx - delta x
	 * @return Numerical solution on <b>n</b> layers. 
	 */
	public double[][] solve(double[] f, int n, double k, double u, double dt, double dx);
}
