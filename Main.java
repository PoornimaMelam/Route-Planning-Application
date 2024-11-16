package com.srkr.project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

//Location class to represent each location on the map
class Location {
    String name;
    public Location(String name) {
        this.name = name;
    }
}

//Edge class to represent the connections between locations
class Edge {
    Location from;
    Location to;
    int distance;
    int time;

    public Edge(Location from, Location to, int distance,int time) {
        this.from = from;
        this.to = to;
        this.distance = distance;
        this.time=time;
    }
}

class AdjacencyListGraph {
    private static Map<Location, List<Edge>> adjacencyList;

    public AdjacencyListGraph() {
        adjacencyList = new HashMap<>();
    }

    // Method to add a new location node to the graph
    public void addLocation(Location location) {
        adjacencyList.put(location, new ArrayList<>());
    }

    // Method to add a new edge (connection) between two locations in the graph
    public void addEdge(Location from, Location to, int distance) {
        Edge edge = new Edge(from, to, distance, distance);
        adjacencyList.get(from).add(edge);
        // If the graph is undirected, you can add the reverse edge as well
        Edge reverseEdge = new Edge(to, from, distance, distance);
        adjacencyList.get(to).add(reverseEdge);
    }

    public Map<Location, List<Location>> dijkstra(Location source,boolean isTimeBased) {
        Map<Location, Integer> distances = new HashMap<>();
        Map<Location, Location> previousNodes = new HashMap<>();
        PriorityQueue<Edge> pq ;
        
        if(isTimeBased) {
        	pq=new PriorityQueue<>(Comparator.comparingInt(e -> e.time));
        	 distances.put(source, 0);
             pq.offer(new Edge(source, source, 0, 0));
        }
        else {
        	pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.distance));
        	distances.put(source, 0);
            pq.offer(new Edge(source, source, 0,0));
        	
        }
        while (!pq.isEmpty()) {
            Edge currentEdge = pq.poll();
            Location current = currentEdge.to;
            int currentDistance = currentEdge.distance;

            for (Edge edge : adjacencyList.getOrDefault(current, new ArrayList<>())) {
                int newDistance = currentDistance + edge.distance;
                Location destination = edge.to;

                if (!distances.containsKey(destination) || newDistance < distances.get(destination)) {
                    distances.put(destination, newDistance);
                    pq.offer(new Edge(current, destination, newDistance, newDistance));
                    previousNodes.put(destination, current); // Update the previous node for the shortest path
                }
            }
        }
        Map<Location, List<Location>> shortestPaths = new HashMap<>();
        for (Location destination : distances.keySet()) {
            if (!destination.equals(source)) { // Skip the source location itself
                List<Location> path = new ArrayList<>();
                Location current = destination;
                while (current != null) {
                    path.add(0, current);
                    current = previousNodes.get(current);
                }
                shortestPaths.put(destination, path);
            }
        }

        return shortestPaths;
    }

    public static List<Edge> getEdges(Location from, Location to) {
        return adjacencyList.getOrDefault(from, new ArrayList<>()).stream()
                .filter(edge -> edge.to == to)
                .collect(Collectors.toList());
    }

    public void printGraph() {
        for (Location location : adjacencyList.keySet()) {
            List<Edge> edges = adjacencyList.get(location);
            System.out.print(location.name + " -> ");
            for (Edge edge : edges) {
                System.out.print(edge.to.name + "(" + edge.distance + ") ");
            }
            System.out.println();
        }
    }
    public static int getShortestDistance(Map<Location, List<Location>> shortestPaths, Location source, Location destination) {
        if (source.equals(destination)) {
            return 0;
        }

        List<Location> shortestPath = shortestPaths.get(destination);
        if (shortestPath != null) {
            int distance = 0;
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                Location current = shortestPath.get(i);
                Location next = shortestPath.get(i + 1);
                List<Edge> edges = null;
				try {
					edges = getEdges(current, next);
				} catch (Exception e) {
					e.printStackTrace();
				} 
                if (edges != null && !edges.isEmpty()) {
                    distance += edges.get(0).distance;
                }
            }
            return distance;
        }
        return -1;
    }
    
    public Map<Location, List<List<Location>>> findAllPaths(Location source) {
        Map<Location, List<List<Location>>> allPaths = new HashMap<>();
        Set<Location> visited = new HashSet<>();
        List<Location> currentPath = new ArrayList<>();
        currentPath.add(source);
        Stack<Location> stack = new Stack<>();
        stack.push(source);

        while (!stack.isEmpty()) {
            Location current = stack.peek();
            visited.add(current);

            boolean isLeaf = true;
            for (Edge edge : adjacencyList.getOrDefault(current, new ArrayList<>())) {
                Location destination = edge.to;
                if (!visited.contains(destination)) {
                    isLeaf = false;
                    stack.push(destination);
                    currentPath.add(destination);

                    if (!allPaths.containsKey(destination)) {
                        allPaths.put(destination, new ArrayList<>());
                    }
                    allPaths.get(destination).add(new ArrayList<>(currentPath));
                    break;
                }
            }

            if (isLeaf) {
                stack.pop();
                currentPath.remove(currentPath.size() - 1);
            }
        }

        return allPaths;
    }

    
    public double findShortestTime(Location source,Location destination) {
    	Map<Location, List<Location>> shortestPaths = dijkstra(source,true);
    	int distance = getShortestDistance(shortestPaths,source,destination);
    	double time = 2*distance;
    	return time;
    }
}

public class Main {

    public static void main(String[] args) {
    	
        AdjacencyListGraph graph = new AdjacencyListGraph();

        // Create a map to store Location objects by their names
        Map<String, Location> locationMap = new HashMap<>();

        Location locA = new Location("Bhimavaram");
        Location locB = new Location("Veeravasaram");
        Location locC = new Location("Palakollu");
        Location locD = new Location("Mogalthuru");
        Location locE = new Location("Taderu");
        Location locF = new Location("Narsapuram");
        Location locG = new Location("Tanuku");
        Location locH = new Location("Tadepalligudem");
        Location locI = new Location("Rajamundry");
        Location locJ = new Location("Vijayawada");
        Location locK = new Location("Gudiwada");
        Location locL = new Location("vizag");
        Location locM = new Location("Ongole");

        // Add Location nodes to the graph and the map
        graph.addLocation(locA);
        graph.addLocation(locB);
        graph.addLocation(locC);
        graph.addLocation(locD);
        graph.addLocation(locE);
        graph.addLocation(locF);
        graph.addLocation(locG);
        graph.addLocation(locH);
        graph.addLocation(locI);
        graph.addLocation(locJ);
        graph.addLocation(locK);
        graph.addLocation(locL);
        graph.addLocation(locM);

        // Add edges (connections) between locations in the graph
        graph.addEdge(locA, locB, 12);
        graph.addEdge(locB, locC, 12);
        graph.addEdge(locC, locF, 10);
        graph.addEdge(locA, locD, 23);
        graph.addEdge(locD, locF, 17);
        graph.addEdge(locA, locE, 5);
        graph.addEdge(locE, locF, 27);
        graph.addEdge(locA, locG, 38);
        graph.addEdge(locC, locG, 34);
        graph.addEdge(locA, locH, 31);
        graph.addEdge(locA, locI, 75);
        graph.addEdge(locG, locH, 24);
        graph.addEdge(locG, locI, 41);
        graph.addEdge(locH, locI, 40);
        graph.addEdge(locA, locJ, 132);
        graph.addEdge(locK, locJ, 41);
        graph.addEdge(locA, locK, 64);
        graph.addEdge(locA, locC, 24);
        graph.addEdge(locL, locA, 268);
        graph.addEdge(locA, locM, 236);
        graph.addEdge(locB, locD, 20);
        graph.addEdge(locB, locE, 13);
        graph.addEdge(locB, locF, 19);
        graph.addEdge(locB, locG, 30);
        graph.addEdge(locB, locH, 41);
        graph.addEdge(locB, locI, 75);
        graph.addEdge(locB, locJ, 117);
        graph.addEdge(locB, locK, 76);
        graph.addEdge(locB, locL, 267);
        graph.addEdge(locB, locM, 248);
        graph.addEdge(locC, locD, 19);
        graph.addEdge(locC, locE, 26);
        graph.addEdge(locC, locH, 53);
        graph.addEdge(locC, locI, 69);
        graph.addEdge(locC, locJ, 157);
        graph.addEdge(locC, locK, 109);
        graph.addEdge(locC, locL, 259);
        graph.addEdge(locC, locM, 255);
        graph.addEdge(locD, locE, 18);
        graph.addEdge(locD, locG, 55);
        graph.addEdge(locD, locH, 54);
        graph.addEdge(locD, locI, 89);
        graph.addEdge(locD, locJ, 138);
        graph.addEdge(locD, locK, 90);
        graph.addEdge(locD, locL, 279);
        graph.addEdge(locD, locM, 235);
        graph.addEdge(locE, locG, 43);
        graph.addEdge(locE, locH, 36);
        graph.addEdge(locE, locI, 92);
        graph.addEdge(locE, locJ, 143);
        graph.addEdge(locE, locK, 96);
        graph.addEdge(locE, locL, 283);
        graph.addEdge(locE, locM, 240);
        graph.addEdge(locF, locG, 46);
        graph.addEdge(locF, locH, 63);
        graph.addEdge(locF, locI, 79);
        graph.addEdge(locF, locJ, 148);
        graph.addEdge(locF, locK, 99);
        graph.addEdge(locF, locL, 270);
        graph.addEdge(locF, locM, 245);
        graph.addEdge(locG, locJ, 131);
        graph.addEdge(locG, locK, 113);
        graph.addEdge(locG, locL, 230);
        graph.addEdge(locG, locM, 280);
        graph.addEdge(locH, locJ, 112);
        graph.addEdge(locH, locK, 94);
        graph.addEdge(locH, locL, 237);
        graph.addEdge(locH, locM, 260);
        graph.addEdge(locI, locJ, 162);
        graph.addEdge(locI, locK, 144);
        graph.addEdge(locI, locL, 191);
        graph.addEdge(locI, locM, 310);
        graph.addEdge(locJ, locK, 41);
        graph.addEdge(locJ, locL, 344);
        graph.addEdge(locJ, locM, 150);
        graph.addEdge(locK, locL, 327);
        graph.addEdge(locK, locM, 189);
        graph.addEdge(locL, locM, 492);
        

        

        // Store Location objects in the map
        locationMap.put("bhimavaram", locA);
        locationMap.put("veeravasaram", locB);
        locationMap.put("palakollu", locC);
        locationMap.put("mogalthuru", locD);
        locationMap.put("taderu", locE);
        locationMap.put("narsapuram", locF);
        locationMap.put("tanuku", locG);
        locationMap.put("tadepalligudem", locH);
        locationMap.put("rajamundry", locI);
        locationMap.put("vijayawada", locJ);
        locationMap.put("gudiwada", locK);
        locationMap.put("vizag", locL);
        locationMap.put("ongole", locM);

        System.out.println("adjacencylist matrix");
        graph.printGraph();

        Scanner scanner = new Scanner(System.in);

        int choice;
        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Show available paths from a location");
            System.out.println("2. Find the shortest path between source and destination");
            System.out.println("3. Find the shortest distance between source and destination");
            System.out.println("4. Find the shortest time between source and destination");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
            case 1:
                scanner.nextLine(); // Consume the newline left by nextInt()
                System.out.print("Enter the source location: ");
                String sourceName4 = scanner.nextLine().toLowerCase();
                Location source4 = locationMap.get(sourceName4);

                if (source4 == null) {
                    System.out.println("Source location not found!");
                    break;
                }

                Map<Location, List<List<Location>>> allPaths = graph.findAllPaths(source4);

                for (Location destination : allPaths.keySet()) {
                    List<List<Location>> paths = allPaths.get(destination);
                    System.out.println("Possible paths to " + destination.name + ":");
                    for (List<Location> path : paths) {
                        for (Location location : path) {
                            System.out.print(location.name + " -> ");
                        }
                        System.out.println();
                    }
                }
                break;
            case 2:
                scanner.nextLine(); // Consume the newline left by nextInt()
                System.out.print("Enter the source location: ");
                String sourceName2 = scanner.nextLine().toLowerCase();
                System.out.print("Enter the destination location: ");
                String destinationName2 = scanner.nextLine().toLowerCase();
                Location source2 = locationMap.get(sourceName2);
                Location destination2 = locationMap.get(destinationName2);

                if (source2 == null || destination2 == null) {
                    System.out.println("Source or Destination not found!");
                    break;
                }

                Map<Location, List<Location>> shortestPaths2 = graph.dijkstra(source2, false);
                List<Location> shortestPath2 = shortestPaths2.get(destination2);
                if (shortestPath2 != null) {
                    int distance2 = AdjacencyListGraph.getShortestDistance(shortestPaths2, source2, destination2);
                    System.out.print("Shortest path: ");
                    for (Location location : shortestPath2) {
                        System.out.print(location.name + " -> ");
                    }
                    System.out.println(" (Distance: " + distance2 + " km)");
                } else {
                    System.out.println("There is no path between " + sourceName2 + " and " + destinationName2 + ".");
                }
                break;
            case 3:
                scanner.nextLine(); // Consume the newline left by nextInt()
                System.out.print("Enter the source location: ");
                String sourceName3 = scanner.nextLine().toLowerCase();
                System.out.print("Enter the destination location: ");
                String destinationName3 = scanner.nextLine().toLowerCase();
                Location source3 = locationMap.get(sourceName3);
                Location destination3 = locationMap.get(destinationName3);

                if (source3 == null || destination3 == null) {
                    System.out.println("Source or Destination not found!");
                    break;
                }
                Map<Location, List<Location>> shortestPaths3 = graph.dijkstra(source3, false);
                int distance3 = AdjacencyListGraph.getShortestDistance(shortestPaths3, source3, destination3);

                if (distance3 != -1) {
                    System.out.println("Shortest distance from " + sourceName3 + " to " + destinationName3 + ": " + distance3 + " km");
                } else {
                    System.out.println("There is no path between " + sourceName3 + " and " + destinationName3 + ".");
                }
                break;
            case 4:
            	scanner.nextLine(); // Consume the newline left by nextInt()
                System.out.print("Enter the source location: ");
                String sourceName5 = scanner.nextLine().toLowerCase();
                System.out.print("Enter the destination location: ");
                String destinationName5 = scanner.nextLine().toLowerCase();
                Location source5 = locationMap.get(sourceName5);
                Location destination5 = locationMap.get(destinationName5);

                if (source5 == null || destination5 == null) {
                    System.out.println("Source or Destination not found!");
                    break;
                }

                double shortestTime = graph.findShortestTime(source5, destination5);
                if (shortestTime != -1) {
                    System.out.println("Shortest time from " + sourceName5 + " to " + destinationName5 + ": " + shortestTime + " minutes");
                } else {
                    System.out.println("There is no path between " + sourceName5 + " and " + destinationName5 + ".");
                }
                break;
            
            case 5:
                exit();
            default:
                System.out.println("Invalid choice! Please try again.");
                break;
            }
            
        }while(choice!=6);
}

	private static void exit() {
	        System.exit(0);
	}
}
