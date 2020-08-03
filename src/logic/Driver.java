package logic;

public class Driver {
    public static void main(String[] args) {
        String[] input = {"alex", "jonah", "nick", "school", "magic", "weather", "rainbow", "car", "superfastspacecar", "arugula","commotion","retainer"};
        String[] input2 = {"METHIONYLTHREONYLTHREONYGLUTAMINYLARGINYL", "LOPADOTEMACHOSELACHOGALEOKRANIOLEIPSAN","PNEUMONOULTRAMICROSCOPICSILICOVOLCANOCONIOSIS","PSEUDOPSEUDOHYPOPARATHYROIDISM"};
        CrosswordGenerator generator = new CrosswordGenerator();

        char[][] crossword = generator.getCrossword(input);
        printTable(crossword);
        printTable(generator.expandCrosswordPadding(1));
    }

    private static void printTable(int[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private static void printTable(char[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
