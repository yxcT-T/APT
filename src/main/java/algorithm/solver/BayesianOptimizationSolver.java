package algorithm.solver;

import org.apache.log4j.*;
import algorithm.acquisition_functions.BaseAcquisitionFunction;
import algorithm.models.BaseModel;
import algorithm.maximizers.BaseMaximizer;
import org.json.JSONObject;
import utils.Pair;
import utils.ArrayUtils;

import java.util.*;
import algorithm.initial_design.InitRandomUniform;
import utils.ArrayUtils;
import algorithm.objective_func.Task;

public class BayesianOptimizationSolver extends BaseSolver{

    private Logger logger = Logger.getLogger(BayesianOptimizationSolver.class);

    private int _initial_points;
    private int _train_interval;
    private int _n_restarts;


    /**
     * Implementation of the standard Bayesian optimization loop that uses
     * an acquisition function and a model to optimize a given objective_func.
     * This module keeps track of additional information such as runtime,
     * optimization overhead, evaluated points and saves the output
     * in a json file.
     *
     * @param objective_func Function handle for the objective function
     * @param lower double[D]
     *              The lower bound of the search space
     * @param upper double[D]
     *              The upper bound of the search space
     * @param acquisition_func BaseAcquisitionFunctionObject
     *                         The acquisition function which will be maximized.
     * @param model ModelObject
     *              Model (i.e. GaussianProcess, RandomForest) that models our current
     *              believe of the objective function.
     * @param maximize_func Optimization method that is used to maximize the acquisition
     *                      function
     * @param initial_points number of initial points
     * @param output_path Specifies the path where the intermediate output after each iteration will be saved.
     *                    If None no output will be saved to disk.
     * @param train_interval Specifies after how many iterations the model is retrained.
     * @param n_restarts How often the incumbent estimation is repeated.
     * @throws Exception throw exception
     */
    public BayesianOptimizationSolver(Task objective_func, double[] lower, double[] upper, BaseAcquisitionFunction acquisition_func,
                                      BaseModel model, BaseMaximizer maximize_func, int initial_points, String output_path,
                                      int train_interval, int n_restarts) throws Exception{
        super(objective_func, lower, upper, acquisition_func, model, maximize_func, output_path);

        this._initial_points = initial_points;
        this._train_interval = train_interval;
        this._n_restarts = n_restarts;

        this._incumbents = new ArrayList<Double[]>();
        this._incumbent_values = new ArrayList<Double>();
    }

    /**
     * The main Bayesian optimization loop
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
        this._time_start = System.currentTimeMillis();

        if (X == null && y == null){
            long start_time_overhead = System.currentTimeMillis();
            double[][] init = InitRandomUniform.get(this._lower, this._upper, this._initial_points);

            X = new double[init.length][this._lower.length];
            y = new double[init.length];

            long time_overhead = (System.currentTimeMillis() - start_time_overhead) / this._initial_points;
            for (int i = 0; i < init.length; i ++ ){
                double[] x = init[i];
                logger.info("Evaluate: " + ArrayUtils.arrayToString(ArrayUtils.double2Double(x)));

                long start_time = System.currentTimeMillis();
                double new_y = this._objective_func.evaluate(x);

                X[i] = x;
                y[i] = new_y;
                this._time_func_eval.add(System.currentTimeMillis() - start_time);
                this._time_overhead.add(time_overhead);

                logger.info(String.format("Configuration achieved a performance of %f in %d seconds", y[i], this._time_func_eval.get(i)));

                int best_idx = ArrayUtils.argmin(y);
                double[] incumbent = X[best_idx];
                double incumbent_value = y[best_idx];

                this._incumbents.add(ArrayUtils.double2Double(incumbent));
                this._incumbent_values.add(incumbent_value);

                this._runtime.add(System.currentTimeMillis() - this._time_start);

                if (this._output_path != null){
                    save_output(i);
                }
            }
        }
        else if (X == null || y == null){
            throw new Exception("X or y is null in BayesianOptimizationSolver.run().");
        }

        this._X = ArrayUtils.arrayToList(X);
        this._y = ArrayUtils.arrayToList(y);

        // Main Bayesian optimization loop
        for (int it = this._initial_points; it < num_iterations; it ++ ){
            logger.info(String.format("Start iteration %d ...", it));

            long start_time = System.currentTimeMillis();

            boolean do_optimize = false;
            if (it % this._train_interval == 0){
                do_optimize = true;
            }

            // Choose next point to evaluate
            double[] new_x = choose_next(this._X, this._y, do_optimize);

            long time_overhead = System.currentTimeMillis() - start_time;
            this._time_overhead.add(time_overhead);
            logger.info(String.format("Optimization overhead was %d seconds", time_overhead));
            logger.info(String.format("Next candidate %s", ArrayUtils.arrayToString(ArrayUtils.double2Double(new_x))));

            // Evaluate
            start_time = System.currentTimeMillis();
            double new_y = this._objective_func.evaluate(new_x);
            long time_func_eval = System.currentTimeMillis() - start_time;
            this._time_func_eval.add(time_func_eval);

            logger.info(String.format("Configuration achieved a performance of %f", new_y));
            logger.info(String.format("Evaluation of this configuration took %d seconds", time_func_eval));

            // Extend the data
            this._X.add(ArrayUtils.double2Double(new_x));
            this._y.add(new_y);

            // Estimate incumbent
            int best_idx = ArrayUtils.argmin(this._y);
            Double[] incumbent = this._X.get(best_idx);
            Double incumbent_value = this._y.get(best_idx);

            this._incumbents.add(incumbent);
            this._incumbent_values.add(incumbent_value);
            logger.info(String.format("Current incumbent %s with estimated performance %f", ArrayUtils.arrayToString(incumbent), incumbent_value));

            this._runtime.add(System.currentTimeMillis() - this._time_start);

            if (this._output_path != null){
                save_output(it);
            }
        }

        Double[] incumbent = this._incumbents.get(this._incumbents.size() - 1);
        Double incumbent_value = this._incumbent_values.get(this._incumbent_values.size() - 1);
        logger.info(String.format("Return %s as incumbent with error %f ", ArrayUtils.arrayToString(incumbent), incumbent_value));

        return new Pair<Double[], Double>(incumbent, incumbent_value);
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
        double[] x = new double[this._lower.length];

        if (X == null && y == null){
            x = InitRandomUniform.get(this._lower, this._upper, 1)[0];
        }
        else if (X == null || y == null){
            throw new Exception("(X, y) is invalid in BayesianOptimizationSolver.choose_next().");
        }
        else if (X.size() == 1){
            x = InitRandomUniform.get(this._lower, this._upper, 1)[0];
        }
        else{
            try{
                logger.info("Train model ...");
                long t = System.currentTimeMillis();
                this._model.train(X, y);
                logger.info(String.format("Time to train the model: %d", System.currentTimeMillis() - t));
            }
            catch (Exception e){
                logger.error("Model could not be trained!");
                /*System.out.print("double[][] x = {");
                for (Double[] v : X) {
                    System.out.print('{' + v[0].toString() + "},");
                }
                System.out.println("};");
                System.out.print("double[] y = {");
                for (Double v : y){
                    System.out.print(v.toString() + ',');
                }
                System.out.println("};");
                System.out.println(e.toString());*/
                throw e;
            }

            this._acquisition_func.update(this._model);

            logger.info("Maximize acquisition function...");
            long t = System.currentTimeMillis();
            x = this._maximize_func.maximize();

            logger.info(String.format("Time to maximize the acquisition function: %d", System.currentTimeMillis() - t));
        }

        return x;
    }

    private void save_output(int it){
        JSONObject data = new JSONObject();
        data.put("optimization_overhead", this._time_overhead.get(it));
        data.put("runtime", this._runtime.get(it));
        data.put("incumbent", this._incumbents.get(it));
        data.put("incumbents_value", this._incumbent_values.get(it));
        data.put("time_func_eval", this._time_func_eval.get(it));
        data.put("iteration", it);

        this._writer.write(data.toString());
        this._writer.write('\n'); //Json more readable
    }
}
