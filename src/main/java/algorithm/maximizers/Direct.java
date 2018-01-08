package algorithm.maximizers;

import algorithm.acquisition_functions.BaseAcquisitionFunction;

public class Direct extends BaseMaximizer{

    private int _nFuncEvals;
    private int _nIters;
    private boolean _verbose;

    /**
     * Interface for the DIRECT algorithm by D. R. Jones, C. D. Perttunen
     * and B. E. Stuckmann
     *
     * @param objectiveFunction acquisition function
     *                          The acquisition function which will be maximized
     * @param lower (D)
     *              Lower bounds of the input space
     * @param upper (D)
     *              Upper bounds of the input space
     * @param nFuncEvals int
     *                   The maximum number of function evaluations
     * @param nIters int
     *               The maximum number of iterations
     * @param verbose boolean
     *                Suppress Direct's output.
     */
    public Direct(BaseAcquisitionFunction objectiveFunction, double[] lower, double[] upper, int nFuncEvals, int nIters, boolean verbose){
        super(objectiveFunction, lower, upper);

        this._nFuncEvals = nFuncEvals;
        this._nIters = nIters;
        this._verbose = verbose;
    }

    /**
     * Maximizes the given acquisition function.
     *
     * @return (D)
     *      Point with highest acquisition value.
     * @throws Exception throws exception
     */
    @Override
    public double[] maximize() throws Exception{
         return new double[]{0d};
    }
}
