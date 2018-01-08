package algorithm.models;

import java.util.*;
import utils.Pair;
import org.json.JSONObject;
import utils.ArrayUtils;

public class BaseModel {
    protected List<Double[]> _X;
    protected List<Double> _y;

    public BaseModel(){
    }

    /**
     * Trains the model on the provided data.
     *
     * @param X (N, D)
     *          Input data points. The dimensionality of X is (N, D),
     *          with N as the number of points and D is the number of input dimensions.
     * @param y (N)
     *          The corresponding target values of the input data points.
     */
    public void train(List<Double[]> X, List<Double> y) throws Exception{
    }

    /**
     * Update the model with the new additional data. Override this function if your
     * model allows to do something smarter than simple retraining
     *
     * @param X (N, D)
     *          Input data points. The dimensionality of X is (N, D),
     *          with N as the number of points and D is the number of input dimensions.
     * @param y (N)
     *          The corresponding target values of the input data points.
     */
    public void update(List<Double[]> X, List<Double> y) throws Exception{
        _X.addAll(X);
        _y.addAll(y);
        train(_X, _y);
    }

    /**
     * Predicts for a given set of test data points the mean and variance of its target values
     *
     * @param X (N, D)
     *          N Test data points with input dimensions D
     * @return (mean, val)
     *          mean: (N), Predictive mean of the test data points
     *          var: (N), Predictive variance of the test data points
     */
    public List<Pair<Double, Double>> predict(List<Double[]> X) throws Exception{
        return new ArrayList<Pair<Double, Double>>();
    }

    /**
     * Json getter function
     *
     * @return json object
     */
    public JSONObject get_json_data(){
        JSONObject result = new JSONObject();
        result.put("X", this._X);
        result.put("y", this._y);
        return result;
    }

    public Pair<Double[], Double> get_incumbent() throws Exception{
        int best_index = 0;
        for (int i = 0; i < _y.size(); i ++ ) {
            if (_y.get(best_index) > _y.get(i)){
                best_index = i;
            }
        }
        return  new Pair<Double[], Double>(_X.get(best_index), _y.get(best_index));
    }
}
