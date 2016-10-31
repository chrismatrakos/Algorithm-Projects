import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mbe on 13-09-2015.
 */
public class ClosestPoint {
    public static ArrayList<Point> points;

    public static void main(String[] args){
        if(args.length == 0)
        {
            System.out.println("Please give us a filename");
            System.exit(0);
        }

        File file = new File(args[0]);
        BufferedReader reader = null;


        try {
            reader = new BufferedReader(new FileReader(file));

            String text = null;

            while((text = reader.readLine()) != null){
                if(text.isEmpty()) continue;
                if(!Character.isDigit(text.trim().charAt(0))) continue;
                else{
                    text = text.trim();
                }
                if(points == null){
                    points = new ArrayList<Point>();
                }
                    String[] splits = text.split(" +");
                    Point p = new Point(Double.parseDouble(splits[1]), Double.parseDouble(splits[2]));
                    points.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Point{
    private double x,y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
