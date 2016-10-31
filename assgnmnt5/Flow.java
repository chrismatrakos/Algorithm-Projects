
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Flow {
    static FlowNode[] nodes;
    static Graph graph;
    static MaxFlow max;

    public static void main(String[] args){
        InitializeArraysWithInputData(args[0]);
        max = new MaxFlow(graph, 0, 54);
        for (int v = 0; v<graph.V ; v++){
            for (Edge e : graph.bag(v)){
                if (v == e.from && e.flow > 0)
                    System.out.println(e.from+"  "+e.to);
            }
        }

        System.out.println(max.total());
    }
    public static void InitializeArraysWithInputData(String source){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(source));
            String currentLine = reader.readLine();

            if (nodes == null) {
                nodes = new FlowNode[Integer.parseInt(currentLine.trim())];
                for(int i = 0; i < nodes.length; i++) {
                    currentLine = reader.readLine();
                    FlowNode n = new FlowNode(currentLine.trim(), i);
                    nodes[i] = n;
                }
            }
            if(graph == null) {
                currentLine = reader.readLine();
                int edges = Integer.parseInt(currentLine.trim());
                graph = new Graph(nodes.length);
                for(int i = 0; i < edges; i++) {
                    currentLine = reader.readLine();
                    String[] splits = currentLine.trim().split(" ");
                    Edge e = new Edge(nodes[Integer.parseInt(splits[0])],nodes[Integer.parseInt(splits[1])],Integer.parseInt(splits[2]));
                    graph.addEdge(e);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static class FlowNode {
        String name;
        public int position;
        public FlowNode(String name, int position){
            this.name = name;
            this.position = position;
        }
    }
    //Create the edge
    private static class Edge {
        public int capacity;
        public int flow;
        public int from;
        public int to;

        public Edge( FlowNode startNode, FlowNode destinationNode, int capacity){
            if(capacity==-1)
                this.capacity = Integer.MAX_VALUE; //if capacity is -1 the its infinite (or a big number)
            else
                this.capacity = capacity;
            this.flow =0;
            this.from = startNode.position;
            this.to = destinationNode.position;
        }

        public int getFrom(){return from;}
        public int getTo(){return to;}
        public int otherWay(int vertex){ //sees the edge from the end to the start
            if(vertex== from)
                return to;
           else if (vertex == to)
                return from;
            throw new IllegalArgumentException();
        }
        public int residualCapacityTo(int vertex){ // checks if there is any capacity left
            if (vertex == from)
                return flow;
            else if (vertex == to)
                return capacity - flow;
         throw new IllegalArgumentException();
        }
        public void addResidualFlowTo(int vertex, int currentFlow){ //checks if there is any flow left
            if (vertex == from)
                flow -= currentFlow;
            else if (vertex == to)
                flow += currentFlow;
            else
                throw new IllegalArgumentException();
        }
    }
    public static class Graph { //creates a graph with bags of edges connected to each node
        private final int V;
        private Bag<Edge>[] bag;
        public Graph(int V)
        {
            this.V = V;
            bag = (Bag<Edge>[]) new Bag[V];
            for (int v = 0; v < V; v++)
                bag[v] = new Bag<>();
        }
        public void addEdge(Edge e)
        {
            int v = e.getFrom();
            int w = e.getTo();
            bag[v].add(e);
            bag[w].add(e);
        }
        public Iterable<Edge> bag(int v)
        {  return bag[v]; }
    }
    public static class Bag<Item> implements Iterable<Item>{
                      // number of elements in bag
        private Node<Item> first;    // beginning of bag
        private static class Node<Item> {
            private Item item;
            private Node<Item> next;
        }
        public Bag() {
            first = null;

        }
        public void add(Item item) {
            Node<Item> oldfirst = first;
            first = new Node<>();
            first.item = item;
            first.next = oldfirst;
        }
        public Iterator<Item> iterator() {
            return new ListIterator<>(first);
        }
        private class ListIterator<Item> implements Iterator<Item> {
            private Node<Item> current;

            public ListIterator(Node<Item> first) {
                current = first;
            }
            public boolean hasNext()  { return current != null;                     }
            public void remove()      { throw new UnsupportedOperationException();  }
            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item item = current.item;
                current = current.next;
                return item;
            }
        }
    }
    private static class MaxFlow { //calculates max flow
        boolean[] marked;
        private Edge[] edgeTo;
        private int total = 0;

        public MaxFlow(Graph G, int from, int to) {
            while (hasAugmentingPath(G, from, to)) {

                int bottle = Integer.MAX_VALUE; //calculates bottleneck
                for (int v = to; v != from; v = edgeTo[v].otherWay(v))
                    bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
                for (int v = to; v != from; v = edgeTo[v].otherWay(v))
                    edgeTo[v].addResidualFlowTo(v, bottle);

                total += bottle;
            }
        }

        private boolean hasAugmentingPath(Graph G, int s, int t) { //checks if there is any path

            edgeTo = new Edge[G.V];
            marked = new boolean[G.V];
            Queue<Integer> queue = new LinkedList<>();
            queue.add(s);
            marked[s] = true;
            while (!queue.isEmpty() && !marked[t]) {
                int from = queue.remove();

                for (Edge e : G.bag(from)) {
                    int to = e.otherWay(from);

                    if (!marked[to] && (e.residualCapacityTo(to) > 0)) {
                        edgeTo[to] = e;
                        marked[to] = true;
                        queue.add(to);
                    }
                }
            }
            return marked[t];
        }

        public int total() {
            return total;
        }
    }
}

