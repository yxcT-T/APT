package algorithm.acquisition_functions;

import algorithm.models.BaseModel;
import org.json.JSONObject;

public class BaseAcquisitionFunction {

    protected BaseModel _model;

    /**
     * A base class for acquisition_functions functions.
     *
     * @param model Model object
     *              Models the objective function.
     */
    public BaseAcquisitionFunction(BaseModel model){
        this._model = model;
    }

    public BaseModel get_model(){
        return _model;
    }

    /**
     * This method will be called if the model is updated. E.g.
     * Entropy search uses it to update it's approximation of P(x=x_min)
     *
     * @param model Model object
     *              Models the objective function.
     */
    public void update(BaseModel model) throws Exception{
        this._model = model;
    }

    /**
     * Computes the acquisition_functions value for a given point X. This function has
     * to be overwritten in a derived class.
     *
     * @param x np.ndarray(D,), The input point where the acquisition_functions function
     *          should be evaluate.
     * @return Expected Improvement of X
     * @throws Exception throws exception
     */
    public double compute(Double[] x) throws Exception{
        return 0.0;
    }

    /**
     * Json getter function
     * @return json object
     */
    public JSONObject get_json_data(){
        JSONObject result = new JSONObject();
        result.put("type", this.getClass().getSimpleName());
        return result;
    }
}
