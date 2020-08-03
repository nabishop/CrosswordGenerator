package logic;

import java.util.*;

public class CrosswordGenerator {
    private final char EMPTY_CHAR = '.';
    private final int DEFAULT_CHAR_WORTH = 10;

    private char[][] crossword;
    private int[][] pointsTable;
    private final Set<String> hashWords = new HashSet<>();
    private final HashMap<Character, Integer> specialCharacters = initializesSpecialCharacters();

    public char[][] getCrossword(String[] words) {
        if (words.length < 1) {
            return null;
        }

        this.hashWords.clear();
        this.hashWords.addAll(Arrays.asList(words));

        initializePointsTable(words);
        fillInCrossword(words);
        trimTable();
        return crossword;
    }

    private HashMap<Character, Integer> initializesSpecialCharacters() {
        HashMap<Character, Integer> spChars = new HashMap<>();

        int low = DEFAULT_CHAR_WORTH + 2;
        int lowMedium = DEFAULT_CHAR_WORTH + 4;
        int medium = DEFAULT_CHAR_WORTH + 6;
        int mediumHigh = DEFAULT_CHAR_WORTH + 8;
        int high = DEFAULT_CHAR_WORTH + 10;
        int extreme = DEFAULT_CHAR_WORTH + 15;

        spChars.put('c', low);
        spChars.put('f', lowMedium);
        spChars.put('g', lowMedium);
        spChars.put('j', medium);
        spChars.put('k', lowMedium);
        spChars.put('p', medium);
        spChars.put('q', extreme);
        spChars.put('u', mediumHigh);
        spChars.put('v', high);
        spChars.put('w', high);
        spChars.put('x', extreme);
        spChars.put('y', high);
        spChars.put('z', extreme);

        return spChars;
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
    public void initializePointsTable(String[] words) {
        int tableSize = getTableSize(words);
        this.pointsTable = new int[tableSize][tableSize];
        int middle = tableSize / 2;
        int pointAmount = 0;

        for (int i = 0; i < tableSize; i++) {
            for (int j = 0; j < tableSize; j++) {
                pointAmount = Math.max(1, Math.abs(middle - Math.min(i, j))) + 1;
                pointsTable[i][j] = middle - pointAmount + 2;
            }
        }
    }

    public int getTableSize(String[] words) {
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

    private void fillInCrossword(String[] words) {
        // assumes square crossword at this point
        initializeCrossword(this.pointsTable.length);

        for (String word : words) {
            int maxValue = 0;
            int[] bestPosition = {0, 0};
            boolean isI = true;

            for (int i = 0; i < this.pointsTable.length; i++) {
                for (int j = 0; j < this.pointsTable[i].length; j++) {
                    int expectedValueI = getExpectedPlacementValue(i, j, word, true);
                    int expectedValueJ = getExpectedPlacementValue(i, j, word, false);

                    int max = Math.max(maxValue, Math.max(expectedValueI, expectedValueJ));

                    if (max > maxValue) {
                        isI = max == expectedValueI;

                        bestPosition[0] = i;
                        bestPosition[1] = j;

                        maxValue = max;
                    }
                }
            }

            updateCrosswordAndPoints(word, bestPosition[0], bestPosition[1], isI);
            printTable(this.crossword);
            System.out.println();
            printTable(this.pointsTable);
            System.out.println("\n\n");

        }
    }

    private void printTable(int[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private void printTable(char[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private void initializeCrossword(int length) {
        this.crossword = new char[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                this.crossword[i][j] = EMPTY_CHAR;
            }
        }
    }

    private void updateCrosswordAndPoints(String word, int i, int j, boolean isI) {
        if (isI) {
            for (int x = 0; x < word.length(); x++) {
                char ch = word.substring(x, x + 1).toCharArray()[0];
                this.crossword[x + i][j] = ch;
                this.pointsTable[x + i][j] += getCharWorth(ch);
            }
        } else {
            for (int x = 0; x < word.length(); x++) {
                char ch = word.substring(x, x + 1).toCharArray()[0];
                this.crossword[i][x + j] = ch;
                this.pointsTable[i][x + j] += getCharWorth(ch);
            }
        }
    }


    private int getExpectedPlacementValue(int i, int j, String word, boolean isI) {
        if (isI) {
            if (i + word.length() > this.pointsTable.length) {
                return -1;
            }
        } else {
            if (j + word.length() > this.pointsTable.length) {
                return -1;
            }
        }

        int expectedValue = 0;
        for (int x = 0; x < word.length(); x++) {
            char ch = word.substring(x, x + 1).toCharArray()[0];
            if (isI) {
                if (this.crossword[x + i][j] != EMPTY_CHAR && this.crossword[x + i][j] != ch) {
                    return -1;
                } else if (this.crossword[x + i][j] == ch) {
                    expectedValue += getCharWorth(ch) * 10;
                    continue;
                }
            } else {
                if (this.crossword[i][x + j] != EMPTY_CHAR && this.crossword[i][x + j] != ch) {
                    return -1;
                } else if (this.crossword[i][x + j] == ch) {
                    expectedValue += getCharWorth(ch) * 10;
                    continue;
                }
            }

            if (!canPlaceChar(ch, isI ? i + x : i, isI ? j : j + x, word, x)) {
                return -1;
            }

            expectedValue += isI ? this.pointsTable[x + i][j] : this.pointsTable[i][x + j];
        }

        return expectedValue;
    }

    private boolean canPlaceChar(char ch, int i, int j, String word, int charPosition) {
        if (i - 1 >= 0) {
            // and placing this here would not form a new word
            if (this.crossword[i - 1][j] != EMPTY_CHAR) {
                if (!canBuildOntoWord(ch, i - 1, j, true, charPosition, word)) {
                    return false;
                }
            }
        }
        if (i + 1 < this.crossword.length) {
            if (this.crossword[i + 1][j] != EMPTY_CHAR) {
                if (!canBuildOntoWord(ch, i + 1, j, true, charPosition, word)) {
                    return false;
                }
            }
        }

        if (j - 1 >= 0) {
            if (this.crossword[i][j - 1] != EMPTY_CHAR) {
                if (!canBuildOntoWord(ch, i, j - 1, false, charPosition, word)) {
                    return false;
                }
            }
        }
        if (j + 1 < this.crossword.length) {
            if (this.crossword[i][j + 1] != EMPTY_CHAR) {
                if (!canBuildOntoWord(ch, i, j + 1, false, charPosition, word)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * See if a given word can be recreated wit the given character being placed here.
     * The word is valid if:
     * 1. The spots NESW to it are empty, so the current can potentially be placed
     * 2. For each spot NESW see if there is a character there, if there is a character,
     * backtrace in the direction of the word to find the starting character and see if this combination
     * makes a valid word included in the list.
     *
     * @param ch
     * @return
     */
    private boolean canBuildOntoWord(char ch, int i, int j, boolean isI, int charPosition, String word) {
        int wordI = i, wordJ = j;

        while (wordI >= 0 && wordJ >= 0 && this.crossword[wordI][wordJ] != EMPTY_CHAR) {
            if (isI) {
                wordI--;
            } else {
                wordJ--;
            }
        }

        if (isI) {
            wordI++;
        } else {
            wordJ++;
        }

        // could potentially be this long as a maximum
        char[] potentialCharWord = new char[this.crossword.length];
        int wordPosition = 0;
        while ((isI ? wordI <= i : wordJ <= j) && this.crossword[wordI][wordJ] != EMPTY_CHAR) {
            potentialCharWord[wordPosition] = this.crossword[wordI][wordJ];
            if (isI) {
                wordI++;
            } else {
                wordJ++;
            }
            wordPosition++;
        }
        while (charPosition < word.length() && wordPosition < this.crossword.length) {
            potentialCharWord[wordPosition] = word.charAt(charPosition);
            charPosition++;
            wordPosition++;
        }
        System.out.println("POTENTIAL WORD FOR CHARACTER [" + ch + "]: " + new String(potentialCharWord) + " EVALUATION: " + this.hashWords.contains(new String(potentialCharWord).trim()));

        return this.hashWords.contains(new String(potentialCharWord).trim());
    }

    private int getCharWorth(char ch) {
        Integer value = this.specialCharacters.get(ch);

        return value != null ? value : DEFAULT_CHAR_WORTH;
    }

    private void trimTable() {
        int minI = -1, minJ = -1, maxI = 0, maxJ = 0;

        for (int i = 0; i < this.crossword.length; i++) {
            for (int j = 0; j < this.crossword[i].length; j++) {
                if (this.crossword[i][j] != EMPTY_CHAR) {
                    if (minI == -1) {
                        minI = i;
                    }
                    if (minJ == -1 || minJ > j) {
                        minJ = j;
                    }
                    if (i > maxI) {
                        maxI = i;
                    }
                    if (j > maxJ) {
                        maxJ = j;
                    }
                }
            }
        }

        buildSmallerTable(minI, minJ, maxI, maxJ);
    }

    private void buildSmallerTable(int minI, int minJ, int maxI, int maxJ) {
        int verticalLength = maxI - minI + 1;
        int horizontalLength = maxJ - minJ + 1;
        char[][] smallerTable = new char[verticalLength][horizontalLength];

        for (int i = 0; i < verticalLength; i++) {
            for (int j = 0; j < horizontalLength; j++) {
                smallerTable[i][j] = this.crossword[minI + i][minJ + j];
            }
        }


        this.crossword = smallerTable;
    }

    public char[][] expandCrosswordPadding(int padding) {
        if (this.crossword == null || this.crossword.length == 0) {
            return null;
        }

        int verticalLength = this.crossword.length + (padding * 2);
        int horizontalLength = this.crossword[0].length + (padding * 2);

        int emptyI = 0, emptyJ = 0;

        char[][] newCrossword = new char[verticalLength][horizontalLength];
        for (int i = 0; i < verticalLength; i++) {
            boolean isEmptyI = i < padding || i - emptyI >= this.crossword.length;
            emptyI = isEmptyI ? emptyI + 1 : emptyI;
            emptyJ = 0;
            for (int j = 0; j < horizontalLength; j++) {
                if (isEmptyI) {
                    newCrossword[i][j] = EMPTY_CHAR;
                } else if (emptyJ < padding || j - emptyJ >= this.crossword[0].length) {
                    newCrossword[i][j] = EMPTY_CHAR;
                    emptyJ++;
                } else {
                    newCrossword[i][j] = this.crossword[i - emptyI][j - emptyJ];
                }
            }
        }

        this.crossword = newCrossword;
        return this.crossword;
    }

}
