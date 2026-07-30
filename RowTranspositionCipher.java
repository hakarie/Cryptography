import java.util.Arrays;

public class RowTranspositionCipher {

    // Helper: Gets the alphabetical order of the key's characters
    // Example: "HACK" -> 'A', 'C', 'H', 'K' -> Original column indices [1, 2, 0, 3]
    private static int[] getKeyOrder(String key) {
        int[] order = new int[key.length()];
        boolean[] used = new boolean[key.length()];
        
        char[] keyChars = key.toCharArray();
        char[] sortedChars = key.toCharArray();
        Arrays.sort(sortedChars);

        for (int i = 0; i < key.length(); i++) {
            for (int j = 0; j < key.length(); j++) {
                if (sortedChars[i] == keyChars[j] && !used[j]) {
                    order[i] = j;
                    used[j] = true;
                    break;
                }
            }
        }
        return order;
    }

    // Encrypts the plaintext
    public static String encrypt(String text, String key) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        key = key.toUpperCase().replaceAll("[^A-Z]", "");
        
        int cols = key.length();
        int rows = (int) Math.ceil((double) text.length() / cols);
        
        // Pad with 'X' to complete the rectangle
        StringBuilder paddedText = new StringBuilder(text);
        while (paddedText.length() < rows * cols) {
            paddedText.append('X');
        }
        
        char[][] grid = new char[rows][cols];
        int index = 0;
        
        // 1. Write row by row
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = paddedText.charAt(index++);
            }
        }
        
        // 2. Read column by column based on the key's alphabetical order
        StringBuilder ciphertext = new StringBuilder();
        int[] order = getKeyOrder(key);
        
        for (int i = 0; i < cols; i++) {
            int targetCol = order[i];
            for (int r = 0; r < rows; r++) {
                ciphertext.append(grid[r][targetCol]);
            }
        }
        
        return ciphertext.toString();
    }

    // Decrypts the ciphertext
    public static String decrypt(String ciphertext, String key) {
        ciphertext = ciphertext.toUpperCase().replaceAll("[^A-Z]", "");
        key = key.toUpperCase().replaceAll("[^A-Z]", "");
        
        int cols = key.length();
        int rows = ciphertext.length() / cols;
        
        char[][] grid = new char[rows][cols];
        int[] order = getKeyOrder(key);
        
        // 1. Write column by column based on the key's alphabetical order
        int index = 0;
        for (int i = 0; i < cols; i++) {
            int targetCol = order[i];
            for (int r = 0; r < rows; r++) {
                grid[r][targetCol] = ciphertext.charAt(index++);
            }
        }
        
        // 2. Read row by row to recover the text
        StringBuilder plaintext = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                plaintext.append(grid[r][c]);
            }
        }
        
        return plaintext.toString();
    }

    public static void main(String[] args) {
        String originalText = "DEFENDTHEEASTWALL";
        String keyword = "HACK"; 
        
        System.out.println("Original:  " + originalText);
        System.out.println("Keyword:   " + keyword);
        System.out.println("-------------------------");
        
        String encrypted = encrypt(originalText, keyword);
        System.out.println("Encrypted: " + encrypted); 
        
        String decrypted = decrypt(encrypted, keyword);
        // The decrypted text will include the 'X' padding at the end
        System.out.println("Decrypted: " + decrypted); 
    }
}
