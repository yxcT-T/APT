package algorithm.objective_func;

import org.json.JSONObject;

public abstract class Task {

    public abstract double evaluate(double[] X);

    public abstract double[] get_lower();

    public abstract double[] get_upper();

    public JSONObject get_json_data(){
        JSONObject result = new JSONObject();
        result.put("Task", "Default Task");
        return result;
    }
}
