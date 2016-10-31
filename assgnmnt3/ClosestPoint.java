/*
 Final Version.
 */
package assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;

/**
 *
 * @author chm
 */
public class ClosestPoint{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please give us a filename");
            System.exit(0);
        }
        File file = new File(args[0]);
        if (file.isDirectory()) {
            Files.walk(Paths.get(args[0])).forEach(filePath -> {
                if (filePath.getFileName().toString().contains("tsp")) {
                    try {
                        run(filePath.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            run(file);
        }
    }
    public static ArrayList<Point> points;  //list
    public static ArrayList<Point> pointsX; //list sorted by X
    public static ArrayList<Point> pointsY; //list sorted by Y

    private static void run(File file) throws IOException {
        parse(file);
        double distance;

//sorting list by X coordinate
        pointsX = new ArrayList<Point>(points);
        pointsX.sort((p1, p2) -> Double.valueOf(p1.x).compareTo(p2.x));

//sorting List by Y coordinate
        pointsY = new ArrayList<Point>(points);
        pointsX.sort((p1, p2) -> Double.valueOf(p1.y).compareTo(p2.y));

        distance = SplitAndCompute(pointsX, pointsY);
        System.out.println(file.getName() + ": " + points.size() + " " + distance);

    }

    private static void parse(File file) {
        points = new ArrayList<>();
        boolean lastLineOfText = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            while ((text = reader.readLine()) != null) {
                if (text.isEmpty()) {
                    continue;
                }
                if (text.trim().equals("NODE_COORD_SECTION")) {
                    lastLineOfText = true;
                    continue;
                }
                if (!lastLineOfText) {
                    continue;
                }
                if (!Character.isDigit(text.trim().charAt(0))) {
                    continue;
                } else {
                    text = text.trim();
                }
                if (points == null) {
                    points = new ArrayList<>();
                }
                String[] splits = text.split(" +");
                Point p = new Point(Double.parseDouble(splits[1]), Double.parseDouble(splits[2]));
                points.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        };
    }

    public static class Point {

        public final double x;
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static class Pair {

        public Point point1 = null;
        public Point point2 = null;
        public double distance = 0.0;

        public Pair() {
        }

        public Pair(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
            calcDistance();
        }

        public void update(Point point1, Point point2, double distance) {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance;
        }

        public void calcDistance() {
            this.distance = distance(point1, point2);
        }

        public String toString() {
            return point1 + "-" + point2 + " : " + distance;
        }
    }

    public static double distance(Point p1, Point p2) {
        double xdist = p2.x - p1.x;
        double ydist = p2.y - p1.y;
        return Math.hypot(xdist, ydist);
    }

    public static double ComputeDistance(List<Point> points) {
        double distance = 0;
        int length = points.size();
        if (length < 2) {
            return 0;
        }
        Pair pair = new Pair(points.get(0), points.get(1));
        if (length > 2) {
            for (int i = 0; i < length - 1; i++) {
                Point point1 = points.get(i);
                for (int j = i + 1; j < length; j++) {
                    Point point2 = points.get(j);
                    distance = distance(point1, point2);
                    if (distance < pair.distance) {
                        pair.update(point1, point2, distance);

                    }
                }
            }
        }
        return pair.distance;
    }

    private static double SplitAndCompute(List<Point> pointsSortedByX, List<Point> pointsSortedByY) {
        int height = pointsSortedByX.size();
        if (height <= 3) {
            return ComputeDistance(pointsSortedByX);
        }

        int middle = height / 2;
        List<Point> left = pointsSortedByX.subList(0, middle);
        List<Point> right = pointsSortedByX.subList(middle, height);

        List<Point> tempList = new ArrayList<Point>(left);
        tempList.sort((p1, p2) -> Double.valueOf(p1.x).compareTo(p2.x));
        double closestPair = SplitAndCompute(left, tempList);

        tempList.clear();
        tempList.addAll(right);
        tempList.sort((p1, p2) -> Double.valueOf(p1.y).compareTo(p2.y));
        double closestPairRight = SplitAndCompute(right, tempList);

        if (closestPairRight < closestPair) {
            closestPair = closestPairRight;
        }

        tempList.clear();
        double minDistance = closestPair;
        double center = right.get(0).x;
        for (Point point : pointsSortedByY) {
            if (Math.abs(center - point.x) < minDistance) {
                tempList.add(point);
            }
        }

        for (int i = 0; i < tempList.size() - 1; i++) {
            Point point1 = tempList.get(i);
                Point point2 = tempList.get(i+1);
                if ((point2.y - point1.y) >= minDistance) {
                    break;
                }
                double distance = distance(point1, point2);
                if (distance < closestPair) {
                    closestPair = distance;
                    minDistance = distance;
                }
            }
        
        return minDistance;
    }

}
