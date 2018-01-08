package algorithm.acquisition_functions;

import algorithm.models.BaseModel;
import utils.Pair;
import utils.Norm;
import java.util.*;

import java.util.ArrayList;

public class EI extends BaseAcquisitionFunction {

    private double _par;

    /**
     * Computes for a given x the expected improvement as
     * acquisition_functions value.
     * @param model Model object
     *          A model that implements at least
     *              - predict(X)
     *              - getCurrentBestX().
     *          If you want to calculate derivatives than it should also support
     *              - predictive_gradients(X)
     * @param par float
     *          Controls the balance between exploration
     *          and exploitation of the acquisition_functions function. Default is 0.0
     */
    public EI(BaseModel model,double par){
        super(model);
        this._par = par;
    }

    /**
     * Computes the EI value
     *
     * @param X np.ndarray(1, D), The input point where the acquisition_functions function
     *          should be evaluate. The dimensionality of X is (N, D), with N as
     *          the number of points to evaluate at and D is the number of
     *          dimensions of one X.
     * @return Expected Improvement of X
     *@throws Exception throws exception
     */
    @Override
    public double compute(Double[] X) throws Exception{
        List<Double[]> X_list = new ArrayList<Double[]>();
        X_list.add(X);
        Pair<Double, Double> pred = this._model.predict(X_list).get(0);
        double m = pred.first();
        double v = pred.second();

        Pair<Double[], Double> incumbent = this._model.get_incumbent();
        double eta = incumbent.second();
        double s = Math.sqrt(v);

        double result = 0.0;
        if (s > 0){
            double z = (eta - m - this._par) / s;
            result = s * (z * Norm.cdf(z) + Norm.pdf(z));
        }
        return result;
    }
}
