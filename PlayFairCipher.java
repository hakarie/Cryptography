public class PlayFairCipher {
    public char[][] matrix = new char[5][5];

    // Constructor: Generates the 5x5 matrix based on the keyword
    public PlayFairCipher(String key) {
        key = key.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        boolean[] used = new boolean[26];
        used['J' - 'A'] = true; // I and J share the same space
        
        int row = 0, col = 0;
        
        // 1. Fill the matrix with the keyword
        for (char c : key.toCharArray()) {
            if (!used[c - 'A']) {
                matrix[row][col++] = c;
                used[c - 'A'] = true;
                if (col == 5) { col = 0; row++; }
            }
        }
        
        // 2. Fill the rest of the matrix with remaining alphabet letters
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!used[c - 'A']) {
                matrix[row][col++] = c;
                used[c - 'A'] = true;
                if (col == 5) { col = 0; row++; }
            }
        }
    }

    // Formats text: handles double letters and odd lengths
    private String prepareText(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        StringBuilder sb = new StringBuilder(text);
        
        for (int i = 0; i < sb.length() - 1; i += 2) {
            if (sb.charAt(i) == sb.charAt(i + 1)) {
                sb.insert(i + 1, 'X');
            }
        }
        if (sb.length() % 2 != 0) {
            sb.append('X'); // Padding for odd length
        }
        return sb.toString();
    }

    // Finds the row and column of a character in the matrix
    private int[] getPos(char c) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) return new int[]{i, j};
            }
        }
        return null;
    }

    // Encrypts or decrypts the text
    public String process(String text, boolean encrypt) {
        text = prepareText(text);
        StringBuilder result = new StringBuilder();
        int shift = encrypt ? 1 : 4; // Adding 4 is the same as subtracting 1 modulo 5

        for (int i = 0; i < text.length(); i += 2) {
            int[] p1 = getPos(text.charAt(i));
            int[] p2 = getPos(text.charAt(i + 1));

            if (p1[0] == p2[0]) { // Same Row
                result.append(matrix[p1[0]][(p1[1] + shift) % 5]);
                result.append(matrix[p2[0]][(p2[1] + shift) % 5]);
            } else if (p1[1] == p2[1]) { // Same Column
                result.append(matrix[(p1[0] + shift) % 5][p1[1]]);
                result.append(matrix[(p2[0] + shift) % 5][p2[1]]);
            } else { // Rectangle (Swap columns)
                result.append(matrix[p1[0]][p2[1]]);
                result.append(matrix[p2[0]][p1[1]]);
            }
        }
        return result.toString();
    }

    public void printMatrix() {
        System.out.println("--- PlayFair Matrix ---");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println(); // Move to the next line after each row
        }
        System.out.println("-----------------------");
    }

    // Main method to test the code
    // public static void main(String[] args) {
    //     String keyword = "SECRET";
    //     String plaintext = "HELLO WORLD";
        
    //     PlayFairCipher cipher = new PlayFairCipher(keyword);
        
    //     System.out.println("Keyword: " + keyword);
    //     System.out.println("Original: " + plaintext);
        
    //     String encrypted = cipher.process(plaintext, true);
    //     System.out.println("Encrypted: " + encrypted);
        
    //     String decrypted = cipher.process(encrypted, false);
    //     System.out.println("Decrypted: " + decrypted);
    // }
}