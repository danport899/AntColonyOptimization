import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


class Destination{

    int x,y;
   
    double antCount;
    Map<Destination,Link> links = new HashMap<Destination,Link>();

    public Destination(int xLocation, int yLocation){
        x = xLocation;
        y = yLocation;
    }

    public Link createLink(Destination city){
        int xDistance = Math.abs(x-city.x);
        int yDistance = Math.abs(y-city.y);
        double shortestDistance = Math.sqrt(Math.pow(xDistance,2)+Math.pow(yDistance,2));
        Link newLink = new Link(shortestDistance);
        newLink.starting = this;
        newLink.ending = city;
        links.put(city,newLink);
        city.links.put(this, newLink);
        return newLink;
    }

    
    public void printLinkData(){
        Iterator iter = links.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            Link link = (Link) entry.getValue();
            System.out.println(link.getDesirability());
        }
    }

   

    @Override
    public int hashCode(){
        return(x *17) * (y*31);
    }

    @Override
    public boolean equals(Object c){
        Destination city = (Destination) c;
        if(city.x == x && city.y == y) return true;
        return false;
    }

}