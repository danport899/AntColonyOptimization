


public class Range {
  
    double min, max;
    double probability;
    Destination finalDestination;
    
  
    public Range(double min, double max){
        this.min = min;
        this.max = max;
    }

    public boolean between(double num){
        if( min <= num && num < max) return true;
        else return false;
    }
}
