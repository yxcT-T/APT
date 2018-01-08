package algorithm.fmin;

import org.apache.log4j.*;
import smile.math.kernel.*;
import algorithm.models.*;
import algorithm.acquisition_functions.*;
import algorithm.maximizers.*;
import algorithm.objective_func.Task;
import algorithm.solver.BayesianOptimizationSolver;
import utils.Pair;
import utils.ArrayUtils;
import java.util.List;

public class BayesianOptimization {

    private Logger logger = Logger.getLogger(BayesianOptimization.class);

    private BayesianOptimizationSolver _bayesianOptimizationSolver;
    private int _num_iterations;

    /**
     * General interface for Bayesian optimization for global black box optimization problems.
     *
     * @param objectiveFunction The objective function that is minimized.
     *                          This function gets a double[D] as input and returns the function value (scalar)
     *                          objectiveFunction.get_lower() double[D]
     *                              The lower bound of the search space
     *                          objectiveFunction.get_upper() double[D]
     *                              The upper bound of the search space
     * @param numIterations     The number of iterations
     * @param maximizer         {"direct", "cmaes", "random", "scipy"}
     *                          The optimizer for the acquisition function. NOTE: "cmaes" only works in D > 1 dimensions
     * @param acquisitionFunc   {"ei", "log_ei", "lcb", "pi"}
     *                          The acquisition function
     * @param modelType         {"gp", "gp_mcmc", "rf"}
     *                          The model for the objective function.
     * @param nInit             Number of points for the initial design. Make sure that it is <= num_iterations.
     */
    public BayesianOptimization(Task objectiveFunction, int numIterations, String maximizer, String acquisitionFunc, String modelType,
                                int nInit, String output_path) throws Exception{
        double[] lower = objectiveFunction.get_lower();
        double[] upper = objectiveFunction.get_upper();

        assert upper.length == lower.length : "Dimension miss match";
        for (int i = 0; i < upper.length; i++) {
            assert upper[i] > lower[i] : "Lower bound >= Uppder bound";
        }

        MercerKernel<double[]> kernel = new GaussianKernel(1.0);
        BaseModel model = new BaseModel();
        if (modelType.equals("gp")) {
            model = new GaussianProcess(kernel, 0.0, true, true, lower, upper);
        }
        else{
            throw new Exception(modelType + "is not a valid model.");
        }

        BaseAcquisitionFunction acquisition_func = new BaseAcquisitionFunction(model);
        if (acquisitionFunc.equals("ei")){
            acquisition_func = new EI(model, 0.0);
        }
        else{
            throw new Exception(acquisitionFunc + "is not a valid acquisition function.");
        }

        BaseMaximizer max_func = new BaseMaximizer(acquisition_func, lower, upper);
        if (maximizer.equals("direct")){
            max_func = new Direct(acquisition_func, lower, upper, 400, 200, true);
        }
        else if (maximizer.equals("random")){
            max_func = new RandomSampling(acquisition_func, lower, upper, 100);
        }
        else{
            throw new Exception(maximizer + " is not a valid function to maximize the acquisition function.");
        }

        this._bayesianOptimizationSolver = new BayesianOptimizationSolver(objectiveFunction, lower, upper, acquisition_func, model,
                max_func, nInit, output_path, 1, 1);
        this._num_iterations = numIterations;
    }

    public Result run() throws Exception{
        Pair<Double[], Double> best_point = this._bayesianOptimizationSolver.run(this._num_iterations, null, null);
        Double[] X_best = best_point.first();
        Double f_min = best_point.second();

        Pair<List<Double[]>, List<Double>> observations = this._bayesianOptimizationSolver.get_observations();

        Result result = new Result();
        result.x_opt = ArrayUtils.Double2double(X_best);
        result.y_opt = f_min;
        result.incumbents = ArrayUtils.listToArray_2d(this._bayesianOptimizationSolver.get_incumbents());
        result.incumbent_values = ArrayUtils.listToArray_1d(this._bayesianOptimizationSolver.get_incumbent_values());
        result.runtime = ArrayUtils.listToArray_1l(this._bayesianOptimizationSolver.get_runtime());
        result.overhead = ArrayUtils.listToArray_1l(this._bayesianOptimizationSolver.get_time_overhead());
        result.X = ArrayUtils.listToArray_2d(observations.first());
        result.y = ArrayUtils.listToArray_1d(observations.second());

        return result;
    }
}
