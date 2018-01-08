package algorithm.solver;

import org.apache.log4j.*;
import org.json.JSONObject;
import algorithm.acquisition_functions.BaseAcquisitionFunction;
import algorithm.models.BaseModel;
import algorithm.maximizers.BaseMaximizer;
import java.io.PrintWriter;
import utils.Pair;
import algorithm.objective_func.Task;

import java.util.ArrayList;
import java.util.List;

public class BaseSolver {

    private Logger logger = Logger.getLogger(BaseSolver.class);

    protected Task _objective_func;
    protected double[] _lower;
    protected double[] _upper;
    protected BaseAcquisitionFunction _acquisition_func;
    protected BaseModel _model;
    protected BaseMaximizer _maximize_func;
    protected String _output_path;
    protected PrintWriter _writer;

    protected List<Double[]> _X;
    protected List<Double> _y;

    protected long _time_start;
    protected List<Long> _time_overhead;
    protected List<Double[]> _incumbents;
    protected List<Double> _incumbent_values;
    protected List<Long> _time_func_eval;
    protected List<Long> _runtime;

    /**
     * Base class which specifies the interface for solvers. Derive from
     * this class if you implement your own solver.
     *
     * @param objective_func Function handle for the objective function
     * @param lower double[D]
     *              The lower bound of the search space
     * @param upper double[D]
     *              The upper bound of the search space
     * @param acquisition_func BaseAcquisitionFunction Object
     *                         The acquisition function which will be maximized.
     * @param model ModelObject
     *              Model (i.e. GaussianProcess, RandomForest) that models our current
     *              believe of the objective function.
     * @param maximize_func MaximizerObject
     *                      Optimization method that is used to maximize the acquisition
     *                      function
     * @param output_path String
     *                    Output path
     * @throws Exception throws exception
     */
    public BaseSolver(Task objective_func, double[] lower, double[] upper, BaseAcquisitionFunction acquisition_func,
                      BaseModel model, BaseMaximizer maximize_func, String output_path) throws Exception{
        this._objective_func = objective_func;
        this._lower = lower;
        this._upper = upper;
        this._acquisition_func = acquisition_func;
        this._model = model;
        this._maximize_func = maximize_func;
        this._output_path = output_path;

        this._time_start = System.currentTimeMillis();
        this._time_overhead = new ArrayList<Long>();
        this._time_func_eval = new ArrayList<Long>();
        this._runtime = new ArrayList<Long>();

        this._incumbents = new ArrayList<Double[]>();
        this._incumbent_values = new ArrayList<Double>();

        this._X = new ArrayList<Double[]>();
        this._y = new ArrayList<Double>();

        if (output_path != null){
            createSaveDir();
        }
    }

    /**
     * Creates the save directory to store the runs
     */
    private void createSaveDir() throws Exception{
        try{
            this._writer = new PrintWriter(this._output_path, "UTF-8");
        }
        catch (Exception e){
            logger.error("Can't create output file: " + this._output_path + " in BaseSolver.createSaveDir().");
            throw e;
        }
    }

    public Pair<List<Double[]>, List<Double>> get_observations(){
        return new Pair<List<Double[]>, List<Double>>(this._X, this._y);
    }

    public List<Double[]> get_incumbents(){
        return this._incumbents;
    }

    public List<Double> get_incumbent_values(){
        return this._incumbent_values;
    }

    public List<Long> get_runtime(){
        return this._runtime;
    }

    public List<Long> get_time_overhead(){
        return this._time_overhead;
    }

    public BaseModel get_model() throws Exception{
        if (this._model == null){
            logger.error("No model trained yet!");
            throw new Exception("No model trained yet!");
        }
        return this._model;
    }

    /**
     * The main optimization loop
     *
     * @param num_iterations The number of iterations
     * @param X double[N][D]
     *          Initial points that are already evaluated
     * @param y double[N]
     *          Function values of the already evaluated points
     * @return Incumbent, Value
     *      Incumbent: double[D]
     *              Incumbent
     *      Value: double
     *              (Estimated) function value of the incumbent
     */
    public Pair<Double[], Double> run(int num_iterations, double[][] X, double[] y) throws Exception{
        return new Pair<Double[], Double>(new Double[]{0d}, 0d);
    }

    /**
     * Suggests a new point to evaluate.
     *
     * @param X double[N][D]
     *          Initial points that are already evaluated
     * @param y double[N]
     *          Function values of the already evaluated points
     * @return double[D]
     *          Suggested point
     */
    public double[] choose_next(List<Double[]> X, List<Double> y) throws Exception{
        return new double[]{0d};
    }

    /**
     * Suggests a new point to evaluate.
     *
     * @param X double[N][D]
     *          Initial points that are already evaluated
     * @param y double[N]
     *          Function values of the already evaluated points
     * @param do_optimize bool
     *                    If true the hyperparameters of the model are
     *                    optimized before the acquisition function is
     *                    maximized.
     * @return double[D]
     *          Suggested point
     */
    public double[] choose_next(List<Double[]> X, List<Double> y, boolean do_optimize) throws Exception{
        return new double[]{0d};
    }

    /**
     * Json getter function
     *
     * @param it Iteration
     * @return json object
     */
    public JSONObject get_json_data(int it){
        JSONObject result = new JSONObject();
        result.put("optimization_overhead", this._time_overhead.get(it));
        result.put("runtime", System.currentTimeMillis() - this._time_start);
        result.put("incumbent", this._incumbents);
        result.put("incumbent_fval", this._incumbent_values);
        result.put("time_func_eval", this._time_func_eval.get(it));
        result.put("iteration", it);

        return result;
    }

    /**
     * Saves meta information of an iteration in a Json file.
     *
     * @param it Iteration
     */
    public void save_json(int it){
        JSONObject base_solver_data = this.get_json_data(it);
        JSONObject base_model_data = this._model.get_json_data();
        JSONObject base_task_data = this._objective_func.get_json_data();
        JSONObject base_acquisition_data = this._acquisition_func.get_json_data();

        JSONObject result = new JSONObject();
        result.put("Solver", base_solver_data);
        result.put("Model", base_model_data);
        result.put("Task", base_task_data);
        result.put("Acquisition", base_acquisition_data);

        this._writer.write(result.toString());
        this._writer.write('\n'); // Json more readable
    }
}
