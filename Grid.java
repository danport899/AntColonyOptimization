import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

class Grid{
    int totalLocations;
    int gridLength = 20;
    Destination starter;
    // Store all destinations
    Set<Destination> destinations = new HashSet<Destination>();
    // Store all possible links (number of destinations choose 2)
    List<Link> allLinks = new ArrayList<Link>();

    public Grid(int totalLocations){
        this.totalLocations = totalLocations;
        for(int i = 0; i < totalLocations; i ++){
            generateDestinations();
        }
        System.out.println("Starter coordinate: " + starter.x + "," + starter.y);
        System.out.println(destinations.size());

        System.out.println("Starter link size: " + starter.links.size());
        /*for(Destination destination: destinations){
            System.out.println(destination.x + "," + destination.y);
            System.out.println("Desirability: " + starter.links.get(destination).getDesirability());
            System.out.println();
        }*/
    }

    public void generateDestinations(){
        //Randomly generate the location of all destinations
        int xLocation = (int)(Math.random() * (gridLength +1));
        int yLocation = (int)(Math.random() * (gridLength+1));
        System.out.println(xLocation + "," + yLocation);
        
        //Create the destination class
        Destination nextDestination = new Destination(xLocation, yLocation);
        //If this destination already exists call the function again until a unique destination is created
        if(destinations.contains(nextDestination) || (starter != null && starter.equals(nextDestination))) generateDestinations();
        else {
            //Create a link between the new destination and all previously created destinations
            for( Destination destination: destinations){
                allLinks.add(nextDestination.createLink(destination));
            }
            //set the very first destination as the starting location
           if(starter == null) starter = nextDestination;
           // Otherwise add it to the destinations list and create a link with the starter
           else{
            allLinks.add(starter.createLink(nextDestination));
            destinations.add(nextDestination);
           } 
        }
    }
    
    //start the search
    public void initSearch(int antCount){
       
        Set<Link> visitedLinks = new HashSet<Link>();

        for( int i = 1; i <= antCount; i ++){
            Set<Destination> remainingDestinations = new HashSet<Destination>();
            remainingDestinations.addAll(destinations);
            visitedLinks= new HashSet<Link>();
            // Launch the ant down the decision tree
            // Store all visited links so their pheromones can be updated
            visitedLinks = launchAnt(starter,remainingDestinations, visitedLinks);
        }
       
       /* System.out.println("Before reduction");
        printLinkData();*/

        //Perform the pheromone reduction on every link
        reducePheromones();

        //System.out.println("After reduction");
        //printLinkData(); 

        // Update the pheromons of only the links that were visited by ants
        for(Link l: visitedLinks){
            l.updatePheromone();
        }
        //System.out.println("After update");
        //printLinkData();

    }


    public Set<Link> launchAnt(Destination destination, Set<Destination> remainingDestinations, Set<Link> visitedLinks){
        //Call this function recursively until the amount of remaining destinations is zero
        if(remainingDestinations.size() == 0) return visitedLinks;
        List<Range> rangeVector = new ArrayList<Range>();

        double currentMin = 0;
        /*Store the desirability of each remaining destination as a Range on a line called RangeVector, which is the size 
        of the sum of all desirabilities, the length of the desirability determines how much space it takes up on the line.*/
        for( Destination d: remainingDestinations){
            Link nextLink = destination.links.get(d);
            Range newRange = new Range(currentMin, currentMin + nextLink.getDesirability());
            currentMin += nextLink.getDesirability();
            newRange.finalDestination= d;
            rangeVector.add(newRange);
        }
        /*Get the probability that a randomly chosen number between 0 and the length of the line (sum of all desirabilities)
        will fall within a given range. More desirable links will have a better chance of getting chosen */
        rangeVector = getProbabilities(rangeVector);
        //Choose a destination based on a randomly choosen number
        Destination chosenPath = chooseDestination(rangeVector);
        //Record the fact that an ant used this link
        destination.links.get(chosenPath).antsVisited += 1;
        //Add the link to the visitedLinks set
        visitedLinks.add(destination.links.get(chosenPath));
        //Remove the chosen Destination from the remaining destinations
        remainingDestinations.remove(chosenPath);

        //System.out.println(chosenPath.x + "," + chosenPath.y + " then ");

        // Call the function recursively, using the destination where the ant is currently and the updated remaining cities
        visitedLinks = launchAnt(chosenPath, remainingDestinations, visitedLinks);
        

        return visitedLinks;
            
    }

    public Destination chooseDestination(List<Range> rangeVector){
        
        double maxNum = rangeVector.get(rangeVector.size()-1).max;

        double randomNum = Math.random() * maxNum;
        //System.out.println("Max Number: " +maxNum);
        //System.out.println("Random Number: " +randomNum);
        for( Range r: rangeVector){
            //System.out.println("Between " + r.min + " and " + r.max +  "      Probability: " + r.probability);
            if (r.between(randomNum)) return r.finalDestination;
        }
        return null;
    }

    public List<Range> getProbabilities(List<Range> rangeVector){
        double sum = rangeVector.get(rangeVector.size() -1).max;
        for(Range r: rangeVector){
            r.probability = ((r.max - r.min)/ sum) * 100;
        }
        return rangeVector;
    }


    public void reducePheromones(){
        for(Link l: allLinks){
            l.reducePheromone();
        }
    }


    //Find the best path based on desirability
    public double findBestPath(){
       
        List<Destination> remainingDestinations = new ArrayList<Destination>();
        remainingDestinations.addAll(destinations);
        List<Destination> bestPath = findBestPath(starter, remainingDestinations, new ArrayList<Destination>());
        System.out.println("Best path:");
        double distance = getDistance(bestPath);
        for( Destination d: bestPath){
            System.out.println(d.x + "," + d.y);
        }
        System.out.println("Total distance: " + distance);
        return distance;
    }

    public List<Destination> findBestPath(Destination destination, List<Destination> remainingDestinations, ArrayList<Destination> bestPath){
        if(remainingDestinations.size() == 0) return bestPath;
        Destination mostAttractive = remainingDestinations.get(0);
        for( Destination d: remainingDestinations){
            if (destination.links.get(d).getDesirability() > destination.links.get(mostAttractive).getDesirability()) mostAttractive = d;
        }
        bestPath.add(mostAttractive);
        remainingDestinations.remove(mostAttractive);
        findBestPath(mostAttractive, remainingDestinations, bestPath);
        return bestPath;
    }

    public double getDistance(List<Destination> bestPath){
        double distance = 0;
        Destination currentDestination = starter;
        System.out.println("Current destination link size: " + starter.links.size());
        for( Destination d: bestPath){
           
            distance += currentDestination.links.get(d).distance;
            currentDestination = d;
        }
        return distance;
    }

    public void printLinkData(){
        for( Link l: allLinks){
            System.out.println("From " + l.starting.x + "," + l.starting.y + " to " + l.ending.x + "," + l.ending.y + ": " );
            System.out.println("     Link desirability: " + l.distanceDesirability);
            System.out.println("     Pheromone density: " + l.pheromoneDensity);
            System.out.println("     Total desirability: " + l.getDesirability());
        }
    }

}