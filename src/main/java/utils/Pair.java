package utils;

public class Pair<F, S>{
    private F _first;
    private S _second;

    public Pair(F first, S second){
        _first = first;
        _second = second;
    }

    public F first(){
        return _first;
    }

    public S second(){
        return _second;
    }

    public void setFirst(F firstValue){
        _first = firstValue;
    }

    public void setSecond(S secondValue){
        _second = secondValue;
    }

    @Override
    public int hashCode(){
        return _first.hashCode() ^ _second.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair)o;
        return this._first.equals(pairo.first()) &&
                this._second.equals(pairo.second());
    }
}
