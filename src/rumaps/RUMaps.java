package rumaps;

//import static org.junit.Assert.assertArrayEquals;

import java.util.*;

/**
 * This class represents the information that can be attained from the Rutgers University Map.
 * 
 * The RUMaps class is responsible for initializing the network, streets, blocks, and intersections in the map.
 * 
 * You will complete methods to initialize blocks and intersections, calculate block lengths, find reachable intersections,
 * minimize intersections between two points, find the fastest path between two points, and calculate a path's information.
 * 
 * Provided is a Network object that contains all the streets and intersections in the map
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class RUMaps {
    
    private Network rutgers;

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Constructor for the RUMaps class. Initializes the streets and intersections in the map.
     * For each block in every street, sets the block's length, traffic factor, and traffic value.
     * 
     * @param mapPanel The map panel to display the map
     * @param filename The name of the file containing the street information
     */
    public RUMaps(MapPanel mapPanel, String filename) {
        StdIn.setFile(filename);
        int numIntersections = StdIn.readInt();
        int numStreets = StdIn.readInt();
        StdIn.readLine();
        rutgers = new Network(numIntersections, mapPanel);
        ArrayList<Block> blocks = initializeBlocks(numStreets);
        initializeIntersections(blocks);

        for (Block block: rutgers.getAdjacencyList()) {
            Block ptr = block;
            while (ptr != null) {
                ptr.setLength(blockLength(ptr));
                ptr.setTrafficFactor(blockTrafficFactor(ptr));
                ptr.setTraffic(blockTraffic(ptr));
                ptr = ptr.getNext();
            }
        }
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     * 
     * @param filename The name of the file containing the street information
     */
    public RUMaps(String filename) {
        this(null, filename);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     */
    public RUMaps() { 
        
    }

    /**
     * Initializes all blocks, given a number of streets.
     * the file was opened by the constructor - use StdIn to continue reading the file
     * @param numStreets the number of streets
     * @return an ArrayList of blocks
     */
    public ArrayList<Block> initializeBlocks(int numStreets) {
        ArrayList<Block> blocks = new ArrayList<>(numStreets);
        for (int i = 0; i < numStreets; i++){
            String streetName = StdIn.readLine();
            int numBlocks = StdIn.readInt(); 
            for (int j = 0; j < numBlocks; j++){
                int blockNum = StdIn.readInt();
                int numPoints = StdIn.readInt();
                double roadSize = StdIn.readDouble();
                Block block = new Block(roadSize,streetName,blockNum);
                for (int p = 0; p < numPoints; p++){
                    if (p==0){
                        block.startPoint(new Coordinate(StdIn.readInt(),StdIn.readInt()));
                    }
                    else{
                        block.nextPoint(new Coordinate(StdIn.readInt(),StdIn.readInt()));
                    }
                    StdIn.readLine(); 
                }
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     * This method traverses through each block and finds
     * the block's start and end points to create intersections. 
     * 
     * It then adds intersections as vertices to the "rutgers" graph if
     * they are not already present, and adds UNDIRECTED edges to the adjacency
     * list.
     * 
     * Note that .addEdge(__) ONLY adds edges in one direction (a -> b). 
     */
    public void initializeIntersections(ArrayList<Block> blocks) {
        for (Block block : blocks){
            ArrayList<Coordinate> blockCoords = block.getCoordinatePoints();
            Coordinate startingPoint = blockCoords.get(0);
            Coordinate endingPoint = blockCoords.get(blockCoords.size()-1);
            int startIndex = rutgers.findIntersection(startingPoint);
            int endIndex = rutgers.findIntersection(endingPoint);
            if (startIndex == -1){
                Intersection intersection = new Intersection(startingPoint);
                block.setFirstEndpoint(intersection);
                rutgers.addIntersection(intersection);
                startIndex = rutgers.findIntersection(startingPoint);
            }
            else{
                Intersection[] intersections = rutgers.getIntersections();
                block.setFirstEndpoint(intersections[startIndex]);

            }
            if (endIndex == -1){
                Intersection intersection = new Intersection(endingPoint);
                block.setLastEndpoint(intersection);
                rutgers.addIntersection(intersection);
                endIndex = rutgers.findIntersection(endingPoint);
            }
            else{
                Intersection[] intersections = rutgers.getIntersections();
                block.setLastEndpoint(intersections[endIndex]);
            }
            Block a = block.copy();
            Block b = block.copy();

            Intersection temp = b.getFirstEndpoint();
            b.setFirstEndpoint(b.getLastEndpoint());
            b.setLastEndpoint(temp);

            rutgers.addEdge(startIndex, a);
            rutgers.addEdge(endIndex, b);
            
        }
     }

    /**
     * Calculates the length of a block by summing the distances between consecutive points for all points in the block.
     * 
     * @param block The block whose length is being calculated
     * @return The total length of the block
     */
    public double blockLength(Block block) {
        ArrayList<Coordinate> coords = block.getCoordinatePoints();
        double totalDistance = 0.0;
        for (int i = 0; i < coords.size()-1; i++){
            totalDistance += coordinateDistance(coords.get(i),coords.get(i+1));
        }
        return totalDistance;

    }

    /**
     * Use a DFS to traverse through blocks, and find the order of intersections
     * traversed starting from a given intersection (as source).
     * 
     * Implement this method recursively, using a helper method.
     */
    public ArrayList<Intersection> reachableIntersections(Intersection source) {  //not really sure if this is working or not
        boolean[] visited = new boolean[rutgers.getAdjacencyList().length];
        //int[] edgeTo = new int[rutgers.getAdjacencyList().length];
        ArrayList<Intersection> orderVisted = new ArrayList<>();
        int currentIndex = rutgers.findIntersection(source.getCoordinate());
        dfsHelper(currentIndex,visited,orderVisted);
        return orderVisted;
    }
    private void dfsHelper(int currentIndex, boolean[] visited, ArrayList<Intersection> orderVisted){
        if (visited[currentIndex] == true) return;
        visited[currentIndex] = true;
        orderVisted.add(rutgers.getIntersections()[currentIndex]);
        for(Block cur = rutgers.adj(currentIndex); cur != null; cur = cur.getNext()){
            int w = rutgers.findIntersection(cur.getLastEndpoint().getCoordinate());
            if (!visited[w]){
                dfsHelper(w,visited,orderVisted);
            }
        }
    }
     

    /**
     * Finds and returns the path with the least number of intersections (nodes) from the start to the end intersection.
     * 
     * - If no path exists, return an empty ArrayList.
     * - This graph is large. Find a way to eliminate searching through intersections that have already been visited.
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least number of turns, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> minimizeIntersections(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        boolean[] marked = new boolean[rutgers.getAdjacencyList().length];
        Integer[] edgeTo = new Integer[rutgers.getAdjacencyList().length];
        Queue<Intersection> queue = new Queue<Intersection>();
        bfsHelper(start, end, marked, edgeTo, queue);

        if (!marked[rutgers.findIntersection(end.getCoordinate())]) {
            return null;
        }

        ArrayList<Intersection> path = new ArrayList<>();
        Integer currentIndex =  rutgers.findIntersection(end.getCoordinate()); 
        while (currentIndex != null) {
            path.add(rutgers.getIntersections()[currentIndex]); 
            currentIndex = edgeTo[currentIndex]; 
        }


        Collections.reverse(path);
        return path;
        
    }
    private void bfsHelper(Intersection start, Intersection end, boolean[] marked, Integer[] edgeTo, Queue<Intersection> queue){
        int startIndex = rutgers.findIntersection(start.getCoordinate());
        int endIndex = rutgers.findIntersection(end.getCoordinate());
        queue.enqueue(start);
        marked[startIndex] = true; 
        while(!queue.isEmpty()){
            Intersection current = queue.dequeue();
            int currIndex = rutgers.findIntersection(current.getCoordinate());
            if (currIndex == endIndex) break;
            Block curr = rutgers.adj(currIndex);
            while(curr != null){ 
                Intersection otherIntersection = curr.other(current);
                int neighborIndex = rutgers.findIntersection(otherIntersection.getCoordinate());
                if (!marked[neighborIndex]){
                    queue.enqueue(rutgers.getIntersections()[neighborIndex]);
                    marked[neighborIndex] = true;
                    edgeTo[neighborIndex] = currIndex;
                }
                curr = curr.getNext();
            }
        }
    }

    /**
     * Finds the path with the least traffic from the start to the end intersection using a variant of Dijkstra's algorithm.
     * The traffic is calculated as the sum of traffic of the blocks along the path.
     * 
     * What is this variant of Dijkstra?
     * - We are using traffic as a cost - we extract the lowest cost intersection from the fringe.
     * - Once we add the target to the done set, we're done. 
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least traffic, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> fastestPath(Intersection start, Intersection end) {
        ArrayList<Intersection> done = new ArrayList<>();
        ArrayList<Intersection> fringe = new ArrayList<>();
        ArrayList<Double> distances = new ArrayList<>();
        ArrayList<Intersection> predecessors = new ArrayList<>();
        
        int numVertices = rutgers.getIntersections().length;
        for (int i = 0; i < numVertices; i++) {
            distances.add(Double.MAX_VALUE);
            predecessors.add(null);
        }
        
        int startIndex = rutgers.findIntersection(start.getCoordinate());
        
        distances.set(startIndex, 0.0);
        fringe.add(start);
        
        while (!fringe.isEmpty()) {
            int minIndex = 0;
            double minDist = distances.get(rutgers.findIntersection(fringe.get(0).getCoordinate()));
            for (int i = 1; i < fringe.size(); i++) {
                int currentIndex = rutgers.findIntersection(fringe.get(i).getCoordinate());
                if (distances.get(currentIndex) < minDist) {
                    minDist = distances.get(currentIndex);
                    minIndex = i;
                }
            }
            Intersection m = fringe.get(minIndex);
            fringe.remove(minIndex);
            int mIndex = rutgers.findIntersection(m.getCoordinate());
            done.add(m);

            for (Block curr = rutgers.adj(mIndex); curr != null; curr = curr.getNext()) {
                Intersection w = curr.other(m);
                int wIndex = rutgers.findIntersection(w.getCoordinate());
                if (done.contains(w)) continue;

                double weight = curr.getTraffic();
                double newDist = distances.get(mIndex) + weight;
                
                if (newDist < distances.get(wIndex)) {
                    distances.set(wIndex, newDist);
                    predecessors.set(wIndex, m);
                    if (!fringe.contains(w)) {
                        fringe.add(w);
                    }
                }
            }
        }
        ArrayList<Intersection> path = new ArrayList<>();
        Intersection curr = end;
        while (curr != null) {
            path.add(curr);
            curr = predecessors.get(rutgers.findIntersection(curr.getCoordinate()));
        }
        Collections.reverse(path);
        return path;
}
    

    /**
     * Calculates the total length, average experienced traffic factor, and total traffic for a given path of blocks.
     * 
     * You're given a list of intersections (vertices); you'll need to find the edge in between each pair.
     * 
     * Compute the average experienced traffic factor by dividing total traffic by total length.
     *  
     * @param path The list of intersections representing the path
     * @return A double array containing the total length, average experienced traffic factor, and total traffic of the path (in that order)
     */
    public double[] pathInformation(ArrayList<Intersection> path) {
        double traffic = 0.0;
        double length = 0.0;
        
    
        for (int i = 0; i < path.size()-1; i++) {
            Intersection curr = path.get(i);
            Intersection next = path.get(i + 1);
    
            int currentIndex = rutgers.findIntersection(curr.getCoordinate());
    
            Block cBlock = null;
            Block currBlock = rutgers.adj(currentIndex);
            while (currBlock != null) {
                Intersection other = currBlock.other(curr);
                if (other.equals(next)) {
                    cBlock = currBlock;
                    break;
                }
                currBlock = currBlock.getNext();
            }
            traffic += blockTraffic(cBlock);
            length += cBlock.getLength();
        }
    
        double averageTrafficFactor = traffic / length;
        return new double[] {length, averageTrafficFactor, traffic};
        
    }

    /**
     * Calculates the Euclidean distance between two coordinates.
     * PROVIDED - do not modify
     * 
     * @param a The first coordinate
     * @param b The second coordinate
     * @return The Euclidean distance between the two coordinates
     */
    private double coordinateDistance(Coordinate a, Coordinate b) {
        // PROVIDED METHOD

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Calculates and returns a randomized traffic factor for the block based on a Gaussian distribution.
     * 
     * This method generates a random traffic factor to simulate varying traffic conditions for each block:
     * - < 1 for good (faster) conditions
     * - = 1 for normal conditions
     * - > 1 for bad (slower) conditions
     * 
     * The traffic factor is generated with a Gaussian distribution centered at 1, with a standard deviation of 0.2.
     * 
     * Constraints:
     * - The traffic factor is capped between a minimum of 0.5 and a maximum of 1.5 to avoid extreme values.
     * 
     * @param block The block for which the traffic factor is calculated
     * @return A randomized traffic factor for the block
     */
    public double blockTrafficFactor(Block block) {
        double rand = StdRandom.gaussian(1, 0.2);
        rand = Math.max(rand, 0.5);
        rand = Math.min(rand, 1.5);
        return rand;
    }

    /**
     * Calculates the traffic on a block by the product of its length and its traffic factor.
     * 
     * @param block The block for which traffic is being calculated
     * @return The calculated traffic value on the block
     */
    public double blockTraffic(Block block) {
        // PROVIDED METHOD
        
        return block.getTrafficFactor() * block.getLength();
    }

    public Network getRutgers() {
        return rutgers;
    }




    
    








}
