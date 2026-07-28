public class HillCipher {

    // Helper: Finds the modular multiplicative inverse of 'a' modulo 'm'
    // This is required to find the inverse matrix for decryption
    private static int modInverse(int a, int m) {
        a = (a % m + m) % m; // Ensure positive
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // No inverse exists
    }

    // Helper: Safely performs modulo 26, ensuring the result is always positive
    private static int mod26(int a) {
        return (a % 26 + 26) % 26;
    }

    // Converts a 4-letter string into a 2x2 integer matrix
    private static int[][] getKeyMatrix(String key) {
        key = key.toUpperCase().replaceAll("[^A-Z]", "");
        if (key.length() < 4) {
            throw new IllegalArgumentException("Key must be at least 4 letters.");
        }
        
        int[][] K = new int[2][2];
        K[0][0] = key.charAt(0) - 'A';
        K[0][1] = key.charAt(1) - 'A';
        K[1][0] = key.charAt(2) - 'A';
        K[1][1] = key.charAt(3) - 'A';
        return K;
    }

    // Calculates the inverse of the 2x2 key matrix modulo 26
    private static int[][] getInverseKeyMatrix(int[][] K) {
        // Determinant = (a*d - b*c) mod 26
        int det = mod26(K[0][0] * K[1][1] - K[0][1] * K[1][0]);
        int detInv = modInverse(det, 26);
        
        if (detInv == -1) {
            throw new IllegalArgumentException(
                "Invalid key! The determinant (" + det + ") is not coprime with 26. " +
                "Try a different 4-letter key."
            );
        }
        
        // Inverse matrix formula: detInv * [[d, -b], [-c, a]] mod 26
        int[][] invK = new int[2][2];
        invK[0][0] = mod26( K[1][1] * detInv);
        invK[0][1] = mod26(-K[0][1] * detInv);
        invK[1][0] = mod26(-K[1][0] * detInv);
        invK[1][1] = mod26( K[0][0] * detInv);
        
        return invK;
    }

    // Encrypts the plaintext using matrix multiplication
    public static String encrypt(String text, String key) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        if (text.length() % 2 != 0) {
            text += "X"; // Pad with 'X' to ensure even length for 2x2 blocks
        }
        
        int[][] K = getKeyMatrix(key);
        StringBuilder cipher = new StringBuilder();
        
        for (int i = 0; i < text.length(); i += 2) {
            int p1 = text.charAt(i) - 'A';
            int p2 = text.charAt(i + 1) - 'A';
            
            int c1 = mod26(K[0][0] * p1 + K[0][1] * p2);
            int c2 = mod26(K[1][0] * p1 + K[1][1] * p2);
            
            cipher.append((char) (c1 + 'A')).append((char) (c2 + 'A'));
        }
        return cipher.toString();
    }

    // Decrypts the ciphertext using the inverse matrix
    public static String decrypt(String cipherText, String key) {
        int[][] K = getKeyMatrix(key);
        int[][] invK = getInverseKeyMatrix(K);
        
        StringBuilder plain = new StringBuilder();
        
        for (int i = 0; i < cipherText.length(); i += 2) {
            int c1 = cipherText.charAt(i) - 'A';
            int c2 = cipherText.charAt(i + 1) - 'A';
            
            int p1 = mod26(invK[0][0] * c1 + invK[0][1] * c2);
            int p2 = mod26(invK[1][0] * c1 + invK[1][1] * c2);
            
            plain.append((char) (p1 + 'A')).append((char) (p2 + 'A'));
        }
        return plain.toString();
    }

    // Main method to test the code
    public static void main(String[] args) {
        String originalText = "ATTACK";
        
        // Key "DDCF" forms the matrix [[3, 3], [2, 5]]
        // Determinant = (3*5) - (3*2) = 9. 
        // 9 is coprime to 26, so it is a valid key.
        String keyword = "DDCF"; 
        
        System.out.println("Original:  " + originalText);
        System.out.println("Keyword:   " + keyword);
        
        String encrypted = encrypt(originalText, keyword);
        System.out.println("Encrypted: " + encrypted); 
        
        String decrypted = decrypt(encrypted, keyword);
        System.out.println("Decrypted: " + decrypted); 
    }
}
