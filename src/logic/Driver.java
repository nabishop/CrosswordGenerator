package logic;

public class Driver {
    public static void main(String[] args) {
        String[] input = {"firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse", "firehouse"};
        System.out.println(CrosswordGenerator.getTableSize(input));

        int[][] table = CrosswordGenerator.initializeTable(input);
        printIntTable(table);
    }

    private static void printIntTable(int[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
