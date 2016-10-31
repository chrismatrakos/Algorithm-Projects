import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Gorilla {

    public static void main(String[] args) {
        //Parsing the strings from the input file.
        ArrayList<Animal> strings = getStrings(args[0]);
        for (int k = 0; k < strings.size(); k++) {
            for (int l = 0; l<strings.size(); l++) {
                if (l <= k) {
                    continue;
                }
                Animal s1 = strings.get(k);
                Animal s2 = strings.get(l);
                run(s1,s2);
            }
        }
    }
    public static void run(Animal s1, Animal s2) {
        Blosum blosumMatrix = new Blosum();
        int m = s1.characters.length();
        int n = s2.characters.length();
        int gap = -4;
        Cell[][] M = new Cell[n + 1][m + 1];
        M[0][0] = new Cell(0, 0, 0);
        //Initialize matrix with gaps etc.
        for (int j = 1; j < M.length; j++) {
            M[j][0] = new Cell(0, j, gap * j);
            M[j][0].previousCell = M[j - 1][0];
            M[j][0].position = 2;
        }
        for (int i = 1; i < M[0].length; i++) {
            M[0][i] = new Cell(i, 0, gap * i);
            M[0][i].previousCell = M[0][i - 1];
            M[0][i].position = 1;
        }
        //Compare strings and get the best score for each character
        for (int j = 1; j < M.length; j++) {
            for (int i = 1; i < M[0].length; i++) {
                M[j][i] = max(M, blosumMatrix, i, j, s1, s2, gap);
            }
        }
        //Bottom up goes through the optimal path
        Trace(M, s1, s2);
    }

    public static Cell max(Cell[][] M, Blosum blosum, int i, int j, Animal string1, Animal string2, int gap) {
        int o1, o2, o3;
        Cell currentCell = new Cell(i, j, gap);
        o1 = (blosum.getDistance(string1.characters.charAt(i - 1), string2.characters.charAt(j - 1))) + (M[j - 1][i - 1].distance);
        o2 = (M[j][i - 1].distance) + gap;
        o3 = (M[j - 1][i].distance) + gap;

        int oCheck = o1 >= o2 ? o1 : o2;
        int oFinal = oCheck >= o3 ? oCheck : o3;

        if (o1 == oFinal) {
            currentCell.distance = o1;
            int pos = 0;
            currentCell.setPosition(pos);
            currentCell.setPrevious(M[j - 1][i - 1]);
        } else if (o2 == oFinal) {
            currentCell.distance = o2;
            int pos = 1;
            currentCell.setPosition(pos);
            currentCell.setPrevious(M[j][i - 1]);

        } else if (o3 == oFinal) {
            currentCell.distance = o3;
            int pos = 2;
            currentCell.setPosition(pos);
            currentCell.setPrevious(M[j - 1][i]);
        }
        return currentCell;
    }

    public static void Trace(Cell[][] M, Animal string1, Animal string2) {
        String s1 = "";
        String s2 = "";
        Cell tracerCell = M[string2.characters.length()][string1.characters.length()];
        while (tracerCell.previousCell != null) {
            if (tracerCell.position == 0) {
                s1 += string1.characters.charAt(tracerCell.row - 1);
                s2 += string2.characters.charAt(tracerCell.col - 1);
            }
            if (tracerCell.position == 1) {
                s1 += string1.characters.charAt(tracerCell.row - 1);
                s2 += "-";
            }
            if (tracerCell.position == 2) {
                s1 += "-";
                s2 += string2.characters.charAt(tracerCell.col - 1);
            }
            tracerCell = tracerCell.previousCell;
        }
        System.out.println(string1.name + "--" + string2.name + ": " + M[string2.characters.length()][string1.characters.length()].distance);

        System.out.println(new StringBuilder(s1).reverse().toString());
        System.out.println(new StringBuilder(s2).reverse().toString());
    }

    public static ArrayList<Animal> getStrings(String source) {
        ArrayList<Animal> animals = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(source));
            String currentLine = reader.readLine();
            int i = 0;
            while (currentLine != null) {
                if (currentLine.isEmpty()) continue;
                if (currentLine.startsWith(">")) {
                    String[] splits = currentLine.split(" ");
                    currentLine = reader.readLine();
                    String totalLine = currentLine;
                    while (currentLine.length()>=70){
                        currentLine = reader.readLine();
                        totalLine += currentLine;
                    }
                    animals.add(i, new Animal(splits[0].substring(1), totalLine));
                    i++;
                    currentLine = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animals;
    }
}
class Cell {
    //Position
    // 0 : diagonal
    // 1 : left
    // 2 : up
    public Integer position;
    public Cell previousCell;
    public int distance;
    public int row;
    public int col;

    public Cell(int row, int col, int distance) {
        this.col = col;
        this.row = row;
        this.distance = distance;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setPrevious(Cell previousCell) {
        this.previousCell = previousCell;
    }
}
class Blosum {
    private static final String letters = "ARNDCQEGHILKMFPSTWYVBZX-";
    private static final int[][] matrix = {
            {4, -1, -2, -2, 0, -1, -1, 0, -2, -1, -1, -1, -1, -2, -1, 1, 0, -3, -2, 0, -2, -1, 0, -4},
            {-1, 5, 0, -2, -3, 1, 0, -2, 0, -3, -2, 2, -1, -3, -2, -1, -1, -3, -2, -3, -1, 0, -1, -4},
            {-2, 0, 6, 1, -3, 0, 0, 0, 1, -3, -3, 0, -2, -3, -2, 1, 0, -4, -2, -3, 3, 0, -1, -4},
            {-2, -2, 1, 6, -3, 0, 2, -1, -1, -3, -4, -1, -3, -3, -1, 0, -1, -4, -3, -3, 4, 1, -1, -4},
            {0, -3, -3, -3, 9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1, -3, -3, -2, -4},
            {-1, 1, 0, 0, -3, 5, 2, -2, 0, -3, -2, 1, 0, -3, -1, 0, -1, -2, -1, -2, 0, 3, -1, -4},
            {-1, 0, 0, 2, -4, 2, 5, -2, 0, -3, -3, 1, -2, -3, -1, 0, -1, -3, -2, -2, 1, 4, -1, -4},
            {0, -2, 0, -1, -3, -2, -2, 6, -2, -4, -4, -2, -3, -3, -2, 0, -2, -2, -3, -3, -1, -2, -1, -4},
            {-2, 0, 1, -1, -3, 0, 0, -2, 8, -3, -3, -1, -2, -1, -2, -1, -2, -2, 2, -3, 0, 0, -1, -4},
            {-1, -3, -3, -3, -1, -3, -3, -4, -3, 4, 2, -3, 1, 0, -3, -2, -1, -3, -1, 3, -3, -3, -1, -4},
            {-1, -2, -3, -4, -1, -2, -3, -4, -3, 2, 4, -2, 2, 0, -3, -2, -1, -2, -1, 1, -4, -3, -1, -4},
            {-1, 2, 0, -1, -3, 1, 1, -2, -1, -3, -2, 5, -1, -3, -1, 0, -1, -3, -2, -2, 0, 1, -1, -4},
            {-1, -1, -2, -3, -1, 0, -2, -3, -2, 1, 2, -1, 5, 0, -2, -1, -1, -1, -1, 1, -3, -1, -1, -4},
            {-2, -3, -3, -3, -2, -3, -3, -3, -1, 0, 0, -3, 0, 6, -4, -2, -2, 1, 3, -1, -3, -3, -1, -4},
            {-1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4, 7, -1, -1, -4, -3, -2, -2, -1, -2, -4},
            {1, -1, 1, 0, -1, 0, 0, 0, -1, -2, -2, 0, -1, -2, -1, 4, 1, -3, -2, -2, 0, 0, 0, -4},
            {0, -1, 0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1, 1, 5, -2, -2, 0, -1, -1, 0, -4},
            {-3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1, 1, -4, -3, -2, 11, 2, -3, -4, -3, -2, -4},
            {-2, -2, -2, -3, -2, -1, -2, -3, 2, -1, -1, -2, -1, 3, -3, -2, -2, 2, 7, -1, -3, -2, -1, -4},
            {0, -3, -3, -3, -1, -2, -2, -3, -3, 3, 1, -2, 1, -1, -2, -2, 0, -3, -1, 4, -3, -2, -1, -4},
            {-2, -1, 3, 4, -3, 0, 1, -1, 0, -3, -4, 0, -3, -3, -2, 0, -1, -4, -3, -3, 4, 1, -1, -4},
            {-1, 0, 0, 1, -3, 3, 4, -2, 0, -3, -3, 1, -1, -3, -1, 0, -1, -3, -2, -2, 1, 4, -1, -4},
            {0, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, -2, -1, -1, -1, -1, -1, -4},
            {-4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, 1}};

    public static int getDistance(char a, char b) {
        int index1 = letters.indexOf(a);
        int index2 = letters.indexOf(b);
        return matrix[index1][index2];
    }
}
class Animal {
    String name;
    String characters;

    public Animal(String s1, String s2) {
        this.name = s1;
        this.characters = s2;
    }
}