public class RailFenceCipher {

    // Encrypts the plaintext
    public static String encrypt(String text, int numRails) {
        if (numRails <= 1) return text;
        
        StringBuilder[] rails = new StringBuilder[numRails];
        for (int i = 0; i < numRails; i++) {
            rails[i] = new StringBuilder();
        }
        
        int currentRail = 0;
        boolean goingDown = false;
        
        // Distribute characters across the rails in a zigzag pattern
        for (char c : text.toCharArray()) {
            rails[currentRail].append(c);
            
            if (currentRail == 0 || currentRail == numRails - 1) {
                goingDown = !goingDown;
            }
            currentRail += goingDown ? 1 : -1;
        }
        
        // Read off the text row by row
        StringBuilder ciphertext = new StringBuilder();
        for (StringBuilder rail : rails) {
            ciphertext.append(rail);
        }
        return ciphertext.toString();
    }

    // Decrypts the ciphertext
    public static String decrypt(String cipherText, int numRails) {
        if (numRails <= 1) return cipherText;
        
        char[][] fence = new char[numRails][cipherText.length()];
        
        // 1. Mark the zigzag pattern with '*'
        int currentRail = 0;
        boolean goingDown = false;
        for (int i = 0; i < cipherText.length(); i++) {
            fence[currentRail][i] = '*';
            
            if (currentRail == 0 || currentRail == numRails - 1) {
                goingDown = !goingDown;
            }
            currentRail += goingDown ? 1 : -1;
        }
        
        // 2. Fill the '*' spots row by row using the ciphertext
        int index = 0;
        for (int r = 0; r < numRails; r++) {
            for (int c = 0; c < cipherText.length(); c++) {
                if (fence[r][c] == '*' && index < cipherText.length()) {
                    fence[r][c] = cipherText.charAt(index++);
                }
            }
        }
        
        // 3. Read the zigzag pattern to recover plaintext
        StringBuilder plaintext = new StringBuilder();
        currentRail = 0;
        goingDown = false;
        for (int i = 0; i < cipherText.length(); i++) {
            plaintext.append(fence[currentRail][i]);
            
            if (currentRail == 0 || currentRail == numRails - 1) {
                goingDown = !goingDown;
            }
            currentRail += goingDown ? 1 : -1;
        }
        
        return plaintext.toString();
    }

    public static void main(String[] args) {
        String originalText = "DEFENDTHEEASTWALL";
        int rails = 3;
        
        System.out.println("Original:  " + originalText);
        System.out.println("Rails:     " + rails);
        System.out.println("-------------------------");
        
        String encrypted = encrypt(originalText, rails);
        System.out.println("Encrypted: " + encrypted); 
        
        String decrypted = decrypt(encrypted, rails);
        System.out.println("Decrypted: " + decrypted); 
    }
}
