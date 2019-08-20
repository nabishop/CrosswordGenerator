package logic;

import java.util.Arrays;

public class CrosswordGenerator {
    private static final int TABLE_EDGE_BUFFER = 2;

    public char[][] getCrossword(String[] words) {
        if (words.length < 1) {
            return null;
        }

        int[][] pointsTable = initializeTable(words);
        char[][] crossword = generateCrossword(pointsTable, words);

        return null;
    }

    /**
     * Builds the starting table based off the input string length.
     * Each spot in the table is given a certain amount of points,
     * with values being highest in the middle.
     * Assumes that words will have at least one element in it.
     *
     * @param words the words to be searched through
     * @return the points table initialized
     */
    public static int[][] initializeTable(String[] words) {
        int tableSize = getTableSize(words);
        int[][] table = new int[tableSize][tableSize];
        int middle = tableSize / 2;
        int pointAmount = 0;

        for (int i = 0; i < tableSize; i++) {
            for (int j = 0; j < tableSize; j++) {
                pointAmount = Math.max(1, Math.abs(middle - Math.min(i, j))) + 1;
                table[i][j] = middle - pointAmount + 2;
            }
        }

        return table;
    }

    public static int getTableSize(String[] words) {
        int totalSpace = 0;
        int usedSpace = 0;
        int blocksGiven = 0;
        final int FIRST_SHARING_BLOCK = 3;

        for (String word : words) {
            int space = word.length() * word.length();

            if (usedSpace + space > totalSpace) {
                totalSpace += space;
                blocksGiven++;
            }

            if (word.length() < FIRST_SHARING_BLOCK) {
                usedSpace += space;
            } else if (word.length() == FIRST_SHARING_BLOCK) {
                usedSpace += space - 1;
            } else {
                int otherCanUse = (word.length() - FIRST_SHARING_BLOCK) * 3;
                usedSpace += space - otherCanUse;
            }
        }

        return blocksGiven > 0 ? (int) Math.ceil(Math.sqrt(usedSpace)) : 0;
    }

    private char[][] generateCrossword(int[][] pointsTable, String[] words) {
        // assumes square crossword at this point
        char[][] crossword = new char[pointsTable.length][pointsTable.length];

        for (String word : words) {
            int maxValue = 0;
            int[] bestPosition = {0, 0};
            boolean isI = true;

            for (int i = 0; i < pointsTable.length && pointsTable.length - i >= word.length(); i++) {
                for (int j = 0; j < pointsTable[i].length && pointsTable[j].length - j >= word.length(); j++) {
                    int expectedValueI = getExpectedPlacementValue(pointsTable, crossword, i, j, word, true);
                    int expectedValueJ = getExpectedPlacementValue(pointsTable, crossword, i, j, word, false);

                    int max = Math.max(maxValue, Math.max(expectedValueI, expectedValueJ));

                    if (max == expectedValueI) {
                        bestPosition[0] = i;
                        bestPosition[1] = j;
                        isI = true;
                        updateValueGrid(pointsTable, i, j, word, isI);
                    } else if (max == expectedValueJ) {
                        bestPosition[0] = i;
                        bestPosition[1] = j;
                        isI = false;
                        updateValueGrid(pointsTable, i, j, word, isI);
                    }

                    maxValue = max;

                    // about to terminate, update crossword
                    if (i == pointsTable.length - 1 || pointsTable.length - (i + 1) < word.length()) {
                        updateCrossword(crossword, word, i, j, isI);
                    }
                }
            }
        }
    }

    private void updateCrossword(char[][] crossword, String word, int i, int j, boolean isI) {
        if (isI) {
            for (int x = i; x < word.length(); x++) {
                crossword[x][j] = word.substring(x, x + 1).toCharArray()[0];
            }
        } else {
            for (int x = j; x < word.length(); x++) {
                crossword[i][x] = word.substring(x, x + 1).toCharArray()[0];
            }
        }
    }


    private int getExpectedPlacementValue(int[][] pointsTable, char[][] crossword, int i, int j, String word, boolean isI) {
        return 0;
    }

    private void updateValueGrid(int[][] pointTable, int i, int j, String word, boolean isI) {

    }

    private char[][] trimTable(char[][] table) {
        return null;
    }
}
