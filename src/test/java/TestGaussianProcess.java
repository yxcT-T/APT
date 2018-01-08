import algorithm.models.GaussianProcessRegression;
import smile.math.kernel.GaussianKernel;

public class TestGaussianProcess {
    public static void main(String[] args) throws Exception{
        double[][] x = {{4.386882584218506},{5.23129826529196},{2.3311506699671813},{0.04517196539697266},{5.996213783028994},{6.0},{3.088130817755673},{0.666919785038967},{5.995626798159346},};
        double[] y = {48.447042048774314,1.7217992445056363,15.037030349916506,-1.0552980601949857,-121.20095109289207,-120.15795948346819,6.786995429386998,-3.2297853738328812,-121.360997428333,};
        try{
            for (double[] v : x){
                v[0] /= 6;
            }
            GaussianProcessRegression<double[]> gpr = new GaussianProcessRegression<double[]>(x, y, new GaussianKernel(1.0), 0.1);
            double[] x_test = {5.794};
            System.out.println(gpr.predict_mean(x_test));
            System.out.println(gpr.predict_variance(x_test));
        }
        catch (Exception e){
            System.out.println(e.toString());
            throw e;
        }
    }
}
