

public class Link {
    double distanceDesirability;
    double distance;
    double pheromoneDensity;
    double evapCoef = 0.15;
    int antsVisited = 0;
    Destination starting, ending;

   public Link(double distance){
       this.distance = distance;
       distanceDesirability = 1/distance;
   }

   public double getDesirability(){
       if(pheromoneDensity == 0) return distanceDesirability;
       return distanceDesirability * pheromoneDensity;
   }

   public void updatePheromone(){
       pheromoneDensity += antsVisited * distanceDesirability;
       antsVisited = 0;
   }

   public void reducePheromone(){
        pheromoneDensity = (1-0.5) * pheromoneDensity;
   }
}
