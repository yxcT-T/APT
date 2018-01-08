package utils;

import com.sun.deploy.util.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Arrays;

public class ArrayUtils {

    public static <T> T concatenate(T a, T b) {
        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        Class<?> resCompType;
        Class<?> aCompType = a.getClass().getComponentType();
        Class<?> bCompType = b.getClass().getComponentType();

        if (aCompType.isAssignableFrom(bCompType)) {
            resCompType = aCompType;
        } else if (bCompType.isAssignableFrom(aCompType)) {
            resCompType = bCompType;
        } else {
            throw new IllegalArgumentException();
        }

        int aLen = Array.getLength(a);
        int bLen = Array.getLength(b);

        @SuppressWarnings("unchecked")
        T result = (T) Array.newInstance(resCompType, aLen + bLen);
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);

        return result;
    }

    public static int argmax(double[] elems){
        if (elems.length == 0){
            return -1;
        }
        int bestindex = 0;
        for (int i = 1; i < elems.length; i ++ ){
            if (elems[i] > elems[bestindex]){
                bestindex = i;
            }
        }
        return bestindex;
    }

    public static int argmax(List<Double> y){
        if (y == null || y.size() == 0){
            return -1;
        }
        int best_idx = 0;
        for (int i = 1; i < y.size(); i ++ ){
            if (y.get(i) > y.get(best_idx)){
                best_idx = i;
            }
        }
        return best_idx;
    }

    public static int argmin(double[] a){
        if (a.length == 0){
            return -1;
        }
        int bestindex = 0;
        for (int i = 1; i < a.length; i ++ ){
            if (a[i] < a[bestindex]){
                bestindex = i;
            }
        }
        return bestindex;
    }

    public static int argmin(List<Double> y){
        if (y == null || y.size() == 0){
            return -1;
        }
        int best_idx = 0;
        for (int i = 1; i < y.size(); i ++ ){
            if (y.get(i) < y.get(best_idx)){
                best_idx = i;
            }
        }
        return best_idx;
    }

    public static Double[] double2Double(double[] a){
        Double[] result = new Double[a.length];
        for (int i = 0; i < a.length; i ++ ){
            result[i] = a[i];
        }
        return result;
    }

    public static double[] Double2double(Double[] a){
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i ++ ){
            result[i] = a[i];
        }
        return result;
    }

    public static <T> String arrayToString(T[] a){
        List<String> a_string = new ArrayList<String>();
        for (T x : a){
            a_string.add(x.toString());
        }
        return '[' + StringUtils.join(a_string, ",") + ']';
    }

    public static List<Double[]> arrayToList(double[][] X){
        List<Double[]> result = new ArrayList<Double[]>();
        for (double[] x : X){
            result.add(double2Double(x));
        }
        return result;
    }

    public static List<Double> arrayToList(double[] y){
        List<Double> result = new ArrayList<Double>();
        for (double v : y){
            result.add(v);
        }
        return result;
    }

    public static double[][] listToArray_2d(List<Double[]> X){
        int n = X.size(), m = X.get(0).length;
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i ++ ){
            for (int j = 0; j < m; j ++ ){
                result[i][j] = X.get(i)[j];
            }
        }
        return result;
    }

    public static double[] listToArray_1d(List<Double> y){
        int n = y.size();
        double[] result = new double[n];
        for (int i = 0; i < n; i ++ ){
            result[i] = y.get(i);
        }
        return result;
    }

    public static long[] listToArray_1l(List<Long> y){
        int n = y.size();
        long[] result = new long[n];
        for (int i = 0; i < n; i ++ ){
            result[i] = y.get(i);
        }
        return result;
    }

}
