import utils.Norm;

public class TestNorm {

    public static void main(String[] args) throws Exception{
        double x = 0.5;
        double pdf = Norm.pdf(x);
        double cdf = Norm.cdf(pdf);
        System.out.println(pdf);
        System.out.println(cdf);
    }
}
