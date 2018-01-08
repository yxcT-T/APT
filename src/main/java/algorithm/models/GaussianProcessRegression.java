package algorithm.models;

import smile.math.kernel.MercerKernel;
import smile.math.matrix.Matrix;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Cholesky;

public class GaussianProcessRegression <T> {
    private static final long serialVersionUID = 1L;

    /**
     * The control points in the regression.
     */
    private T[] knots;
    /**
     * The linear weights.
     */
    private double[] w;
    /**
     * The distance functor.
     */
    private MercerKernel<T> kernel;
    /**
     * The shrinkage/regularization parameter.
     */
    private double lambda;

    private Cholesky cholesky;

    /**
     * Constructor. Fitting a regular Gaussian process model.
     * @param x the training dataset.
     * @param y the response variable.
     * @param kernel the Mercer kernel.
     * @param lambda the shrinkage/regularization parameter.
     */
    public GaussianProcessRegression(T[] x, double[] y, MercerKernel<T> kernel, double lambda) {
        if (x.length != y.length) {
            throw new IllegalArgumentException(String.format("The sizes of X and Y don't match: %d != %d", x.length, y.length));
        }

        if (lambda < 0.0) {
            throw new IllegalArgumentException("Invalid regularization parameter lambda = " + lambda);
        }

        this.kernel = kernel;
        this.lambda = lambda;
        this.knots = x;

        int n = x.length;

        DenseMatrix K = Matrix.zeros(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                double k = kernel.k(x[i], x[j]);
                K.set(i, j, k);
                K.set(j, i, k);
            }

            K.add(i, i, lambda);
        }

        this.cholesky = K.cholesky();
        w = y.clone();
        this.cholesky.solve(w);
    }

    /**
     * Returns the coefficients.
     */
    public double[] coefficients() {
        return w;
    }

    /**
     * Returns the shrinkage parameter.
     */
    public double shrinkage() {
        return lambda;
    }

    public double predict_mean(T x) {
        double f = 0.0;

        for (int i = 0; i < knots.length; i++) {
            f += w[i] * kernel.k(x, knots[i]);
        }

        return f;
    }

    public double predict_variance(T x){
        int n = knots.length;
        double[] k = new double[n];
        for (int i = 0; i < n; i ++ )
            k[i] = kernel.k(x, knots[i]);

        double[] wk = k.clone();
        cholesky.solve(wk);

        double f = kernel.k(x, x);
        for (int i = 0; i < n; i ++ ){
            f -= k[i] * wk[i];
        }
        return f;
    }
}