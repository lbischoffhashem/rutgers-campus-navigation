package test;

import static org.junit.Assert.*;
import org.junit.*;
import rumaps.*;
import java.util.ArrayList;

/**
 * This is an optional JUnit test class for the RUMaps class.
 * You can implement the test cases below to verify the functionality of the RUMaps class. 
 */
public class RUMapsTest {

    // All tests will use the Busch.in input file since it is smaller and easier to debug
    private static final String TEST_FILE = "Busch.in"; 
     
    @Test
    public void testInitializeBlocksAndIntersections() {
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections();
        Block[] blocks = testNetwork.getAdjacencyList();
        
        //Test that intersections are properly initialized
        assertNotNull("Intersections array should not be null", intersections);
        assertTrue("Should have at least one intersection", intersections.length > 0);
        
        //Test that blocks are properly initialized
        assertNotNull("Blocks array should not be null", blocks);
        assertTrue("Should have at least one block", blocks.length > 0);
        
        //Test that intersections have valid coordinates
        for (Intersection intersection : intersections) {
            if (intersection != null) {
                assertNotNull("Intersection coordinate should not be null", intersection.getCoordinate());
                assertTrue("X coordinate should be non-negative", intersection.getCoordinate().getX() >= 0);
                assertTrue("Y coordinate should be non-negative", intersection.getCoordinate().getY() >= 0);
            }
        }
        
        //Test that blocks have valid properties
        for (Block block : blocks) {
            if (block != null) {
                assertNotNull("Block should have coordinate points", block.getCoordinatePoints());
                assertTrue("Block should have at least 2 coordinate points", block.getCoordinatePoints().size() >= 2);
                assertNotNull("Block should have a street name", block.getStreetName());
                assertTrue("Block number should be positive", block.getBlockNumber() > 0);
            }
        }
    }

    @Test
    public void testBlockLength() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();
 
        Block[] blocks = testNetwork.getAdjacencyList();
        
        //Test block length calculation for all blocks
        for (Block block : blocks) {
            if (block != null) {
                double calculatedLength = testRUMaps.blockLength(block);
                double storedLength = block.getLength();
                
                //Test that calculated length is positive
                assertTrue("Block length should be positive", calculatedLength > 0);
                
                //Test that calculated length matches stored length (within tolerance)
                assertEquals("Calculated length should match stored length", 
                           storedLength, calculatedLength, 0.001);
                
                //Test that length is calculated correctly for blocks with multiple points
                ArrayList<Coordinate> coords = block.getCoordinatePoints();
                if (coords.size() > 1) {
                    double expectedLength = 0.0;
                    for (int i = 0; i < coords.size() - 1; i++) {
                        double dx = coords.get(i).getX() - coords.get(i + 1).getX();
                        double dy = coords.get(i).getY() - coords.get(i + 1).getY();
                        expectedLength += Math.sqrt(dx * dx + dy * dy);
                    }
                    assertEquals("Length calculation should be accurate", 
                               expectedLength, calculatedLength, 0.001);
                }
            }
        }
    }

    @Test
    public void testReachableIntersections() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE);
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        
        //Test reachable intersections for each intersection
        for (Intersection intersection : intersections) {
            if (intersection != null) {
                ArrayList<Intersection> reachable = testRUMaps.reachableIntersections(intersection);
                
                //Test that result is not null
                assertNotNull("Reachable intersections should not be null", reachable);
                
                //Test that the source intersection is included in reachable intersections
                assertTrue("Source intersection should be reachable from itself", 
                          reachable.contains(intersection));
                
                //Test that all reachable intersections are valid
                for (Intersection reachableIntersection : reachable) {
                    assertNotNull("Reachable intersection should not be null", reachableIntersection);
                    assertNotNull("Reachable intersection should have valid coordinate", 
                                reachableIntersection.getCoordinate());
                }
                
                //Test that reachable intersections list is not empty (at least contains source)
                assertTrue("Should have at least one reachable intersection (source)", 
                          reachable.size() > 0);
            }
        }
        
        //Test with a specific intersection if available
        if (intersections.length > 0 && intersections[0] != null) {
            ArrayList<Intersection> reachable = testRUMaps.reachableIntersections(intersections[0]);
            assertTrue("Should have at least the source intersection", reachable.size() >= 1);
        }
    }

    @Test
    public void testMinimizeIntersections() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        
        //Test minimize intersections between different pairs of intersections
        for (int i = 0; i < intersections.length; i++) {
            for (int j = i + 1; j < intersections.length; j++) {
                if (intersections[i] != null && intersections[j] != null) {
                    ArrayList<Intersection> path = testRUMaps.minimizeIntersections(intersections[i], intersections[j]);
                    
                    //Test that result is not null
                    assertNotNull("Path should not be null", path);
                    
                    //If path exists, test its validity
                    if (path.size() > 0) {
                        //Test that path starts with source and ends with destination
                        assertEquals("Path should start with source intersection", 
                                   intersections[i], path.get(0));
                        assertEquals("Path should end with destination intersection", 
                                   intersections[j], path.get(path.size() - 1));
                        
                        //Test that all intersections in path are valid
                        for (Intersection intersection : path) {
                            assertNotNull("Path intersection should not be null", intersection);
                            assertNotNull("Path intersection should have valid coordinate", 
                                        intersection.getCoordinate());
                        }
                        
                        //Test that path has at least 2 intersections (start and end)
                        assertTrue("Path should have at least 2 intersections", path.size() >= 2);
                    }
                }
            }
        }
        
        //Test path from intersection to itself
        if (intersections.length > 0 && intersections[0] != null) {
            ArrayList<Intersection> selfPath = testRUMaps.minimizeIntersections(intersections[0], intersections[0]);
            assertNotNull("Self-path should not be null", selfPath);
            if (selfPath.size() > 0) {
                assertEquals("Self-path should contain only the source intersection", 
                           1, selfPath.size());
                assertEquals("Self-path should contain the source intersection", 
                           intersections[0], selfPath.get(0));
            }
        }
    }

    @Test
    public void testFastestPath() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        
        //Test fastest path between different pairs of intersections
        for (int i = 0; i < intersections.length; i++) {
            for (int j = i + 1; j < intersections.length; j++) {
                if (intersections[i] != null && intersections[j] != null) {
                    ArrayList<Intersection> path = testRUMaps.fastestPath(intersections[i], intersections[j]);
                    
                    //Test that result is not null
                    assertNotNull("Fastest path should not be null", path);
                    
                    //If path exists, test its validity
                    if (path.size() > 0) {
                        //Test that path starts with source and ends with destination
                        assertEquals("Fastest path should start with source intersection", 
                                   intersections[i], path.get(0));
                        assertEquals("Fastest path should end with destination intersection", 
                                   intersections[j], path.get(path.size() - 1));
                        
                        //Test that all intersections in path are valid
                        for (Intersection intersection : path) {
                            assertNotNull("Path intersection should not be null", intersection);
                            assertNotNull("Path intersection should have valid coordinate", 
                                        intersection.getCoordinate());
                        }
                        
                        //Test that path has at least 2 intersections (start and end)
                        assertTrue("Fastest path should have at least 2 intersections", path.size() >= 2);
                        
                        //Test that path is continuous (adjacent intersections should be connected)
                        for (int k = 0; k < path.size() - 1; k++) {
                            Intersection current = path.get(k);
                            Intersection next = path.get(k + 1);
                            
                            //Find if there's a block connecting these intersections
                            int currentIndex = testNetwork.findIntersection(current.getCoordinate());
                            boolean connected = false;
                            
                            if (currentIndex != -1) {
                                Block block = testNetwork.adj(currentIndex);
                                while (block != null) {
                                    if (block.other(current).equals(next)) {
                                        connected = true;
                                        break;
                                    }
                                    block = block.getNext();
                                }
                            }
                            
                            assertTrue("Adjacent intersections in path should be connected", connected);
                        }
                    }
                }
            }
        }
        
        //Test fastest path from intersection to itself
        if (intersections.length > 0 && intersections[0] != null) {
            ArrayList<Intersection> selfPath = testRUMaps.fastestPath(intersections[0], intersections[0]);
            assertNotNull("Self fastest path should not be null", selfPath);
            if (selfPath.size() > 0) {
                assertEquals("Self fastest path should contain only the source intersection", 
                           1, selfPath.size());
                assertEquals("Self fastest path should contain the source intersection", 
                           intersections[0], selfPath.get(0));
            }
        }
    }

    @Test
    public void testPathInformation() { 
        RUMaps testRUMaps = new RUMaps(TEST_FILE); 
        Network testNetwork = testRUMaps.getRutgers();

        Intersection[] intersections = testNetwork.getIntersections(); 
        
        //Test path information for various paths
        for (int i = 0; i < intersections.length; i++) {
            for (int j = i + 1; j < intersections.length; j++) {
                if (intersections[i] != null && intersections[j] != null) {
                    //Get a path between these intersections
                    ArrayList<Intersection> path = testRUMaps.minimizeIntersections(intersections[i], intersections[j]);
                    
                    if (path != null && path.size() > 1) {
                        double[] pathInfo = testRUMaps.pathInformation(path);
                        
                        //Test that path information is not null
                        assertNotNull("Path information should not be null", pathInfo);
                        
                        //Test that path information has exactly 3 elements
                        assertEquals("Path information should have 3 elements", 3, pathInfo.length);
                        
                        //Test that all values are non-negative
                        assertTrue("Total length should be non-negative", pathInfo[0] >= 0);
                        assertTrue("Average traffic factor should be non-negative", pathInfo[1] >= 0);
                        assertTrue("Total traffic should be non-negative", pathInfo[2] >= 0);
                        
                        //Test that if length is 0, traffic factor should be 0 (or NaN/Infinity)
                        if (pathInfo[0] == 0) {
                            assertTrue("If length is 0, traffic factor should be 0 or special value", 
                                     pathInfo[1] == 0 || Double.isNaN(pathInfo[1]) || Double.isInfinite(pathInfo[1]));
                        }
                        
                        //Test that total traffic equals length * average traffic factor (within tolerance)
                        if (pathInfo[0] > 0 && !Double.isNaN(pathInfo[1]) && !Double.isInfinite(pathInfo[1])) {
                            double expectedTraffic = pathInfo[0] * pathInfo[1];
                            assertEquals("Total traffic should equal length * average traffic factor", 
                                       expectedTraffic, pathInfo[2], 0.001);
                        }
                    }
                }
            }
        }
        
        //Test path information for single intersection path
        if (intersections.length > 0 && intersections[0] != null) {
            ArrayList<Intersection> singlePath = new ArrayList<>();
            singlePath.add(intersections[0]);
            
            double[] pathInfo = testRUMaps.pathInformation(singlePath);
            assertNotNull("Single intersection path info should not be null", pathInfo);
            assertEquals("Single intersection path info should have 3 elements", 3, pathInfo.length);
            
            //For single intersection, length and traffic should be 0
            assertEquals("Single intersection path length should be 0", 0.0, pathInfo[0], 0.001);
            assertEquals("Single intersection path traffic should be 0", 0.0, pathInfo[2], 0.001);
        }
        
        //Test path information for empty path
        ArrayList<Intersection> emptyPath = new ArrayList<>();
        double[] emptyPathInfo = testRUMaps.pathInformation(emptyPath);
        assertNotNull("Empty path info should not be null", emptyPathInfo);
        assertEquals("Empty path info should have 3 elements", 3, emptyPathInfo.length);
        assertEquals("Empty path length should be 0", 0.0, emptyPathInfo[0], 0.001);
        assertEquals("Empty path traffic should be 0", 0.0, emptyPathInfo[2], 0.001);
    }
}
