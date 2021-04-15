import java.util.ArrayList;
import java.util.List;

class main{

    //Set total locations
    public static final int totalLocations = 50;
    //Set total ants
    public static int antCount = 100;
    public static Grid grid;

    public static void main(String[] args){
       initLocations(); 
       startIterations();
    }

    public static void initLocations(){
        grid = new Grid(totalLocations);
    }

    public static void startIterations(){
        
        List<Double> lastThreeDistances = new ArrayList<Double>();
        int iterations = 0;
        do {
            grid.initSearch(antCount);
            if(lastThreeDistances.size() == 3) lastThreeDistances.remove(0);
            lastThreeDistances.add(grid.findBestPath());
            iterations++;
        }
       //don't stop until the last three results are all equal
        while( !allEqual(lastThreeDistances));
        System.out.println("Total iterations " + iterations);
    }

    public static boolean allEqual(List<Double> numList){
        if(numList.size() < 3 ) return false;
        double lastNum = numList.get(0);
        for( Double num: numList){
            if(num != lastNum) return false;
        }
        return true;
    }


}