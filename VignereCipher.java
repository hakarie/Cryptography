public class Main {

    // Encrypts text using Vigenere Cipher: (letter + key) % 26
    public static String encrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        text = text.toLowerCase();
        key = key.toLowerCase();
        int keyIndex = 0;

        for (char c : text.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                int shift = key.charAt(keyIndex % key.length()) - 'a';
                char encrypted = (char) (((c - 'a' + shift) % 26) + 'a');
                result.append(encrypted);
                keyIndex++; // Only advance key for actual letters
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // Decrypts text using Vigenere Cipher: (letter - key) % 26
    public static String decrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        text = text.toLowerCase();
        key = key.toLowerCase();
        int keyIndex = 0;

        for (char c : text.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                int shift = key.charAt(keyIndex % key.length()) - 'a';
                int val = (c - 'a' - shift) % 26;
                if (val < 0) val += 26; // Fix negative remainder in Java
                
                result.append((char) (val + 'a'));
                keyIndex++; // Only advance key for actual letters
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String original = "Chennai";
        String key = "Mad";

        String encrypted = encrypt(original, key);
        String decrypted = decrypt(encrypted, key);

        System.out.println("Original : " + original);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}
