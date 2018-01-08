import algorithm.fmin.BayesianOptimization;
import algorithm.objective_func.Task;
import algorithm.fmin.Result;
import utils.ArrayUtils;

public class TestBayesianOptimization {

    public static void main(String[] args) throws Exception{

        class MyTask extends Task{

            @Override
            public double evaluate(double[] X) {
                double x = X[0], y = X[1];
                return Math.sin(x * 3) * 4 * (y - 1) * (x + 2);
            }

            @Override
            public double[] get_lower() {
                return new double[]{-3, -3};
            }

            @Override
            public double[] get_upper() {
                return new double[]{3, 3};
            }
        }

        Task task = new MyTask();

        String output_path = "output/result/result.txt";
        BayesianOptimization bayesianOptimization = new BayesianOptimization(task, 50, "random", "ei", "gp", 3, output_path);
        Result result = bayesianOptimization.run();

        for (int i = 0; i < result.incumbents.length; i ++ ){
            double[] x = result.incumbents[i];
            double y = result.incumbent_values[i];
            System.out.print(ArrayUtils.arrayToString(ArrayUtils.double2Double(x)) + ' ');
            System.out.println(y);
        }
    }
}
