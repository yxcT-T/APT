package algorithm.maximizers;

import algorithm.acquisition_functions.BaseAcquisitionFunction;

public class BaseMaximizer {

    protected BaseAcquisitionFunction _objectiveFunction;
    protected double[] _lower;
    protected double[] _upper;

    /**
     * Interface for optimizers that maximizing the
     * acquisition function.
     *
     * @param objectiveFunction acquisition function
     *                          The acquisition function which will be maximized
     * @param lower (D)
     *              Lower bounds of the input space
     * @param upper (D)
     *              Upper bounds of the input space
     */
    public BaseMaximizer(BaseAcquisitionFunction objectiveFunction, double[] lower, double[] upper){
        this._objectiveFunction = objectiveFunction;
        this._lower = lower;
        this._upper = upper;
    }

    public double[] maximize() throws Exception{
        return new double[]{0.0};
    }
}
