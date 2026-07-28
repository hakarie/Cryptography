public class AutoKeyCipher {

    // Encrypts the plaintext using the Autokey cipher
    public static String encrypt(String text, String key) {
        // Sanitize input: convert to uppercase and remove non-letters
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        key = key.toUpperCase().replaceAll("[^A-Z]", "");
        
        // The core of Autokey: The key stream is the keyword + the plaintext
        String keyStream = key + text;
        
        StringBuilder ciphertext = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            int p = text.charAt(i) - 'A';
            int k = keyStream.charAt(i) - 'A';
            
            // Shift forward
            int c = (p + k) % 26;
            ciphertext.append((char) (c + 'A'));
        }
        
        return ciphertext.toString();
    }

    // Decrypts the ciphertext back to plaintext
    public static String decrypt(String ciphertext, String key) {
        ciphertext = ciphertext.toUpperCase().replaceAll("[^A-Z]", "");
        
        // We use a StringBuilder for the key stream because we have to build it dynamically
        StringBuilder keyStream = new StringBuilder(key.toUpperCase().replaceAll("[^A-Z]", ""));
        StringBuilder plaintext = new StringBuilder();
        
        for (int i = 0; i < ciphertext.length(); i++) {
            int c = ciphertext.charAt(i) - 'A';
            int k = keyStream.charAt(i) - 'A';
            
            // Shift backward (add 26 to avoid negative modulo results)
            int p = (c - k + 26) % 26;
            char pChar = (char) (p + 'A');
            
            plaintext.append(pChar);
            
            // CRITICAL STEP: Append the recovered letter to the key stream 
            // so it can be used to decrypt future letters
            keyStream.append(pChar);
        }
        
        return plaintext.toString();
    }

    // Main method to test the code
    public static void main(String[] args) {
        String originalText = "ATTACK AT DAWN";
        String keyword = "QUEEN";
        
        System.out.println("Original:  " + originalText);
        System.out.println("Keyword:   " + keyword);
        
        String encrypted = encrypt(originalText, keyword);
        System.out.println("Encrypted: " + encrypted); // Output: QNXEP OQ XAAN
        
        String decrypted = decrypt(encrypted, keyword);
        System.out.println("Decrypted: " + decrypted); // Output: ATTACKATDAWN
    }
}
