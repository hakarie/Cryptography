import java.util.Arrays;

public class SimplifiedDES {

    // --- S-DES Constants (0-indexed for array manipulation) ---
    private static final int[] P10 = { 2, 4, 1, 6, 3, 9, 0, 8, 7, 5 };
    private static final int[] P8  = { 5, 6, 7, 8, 0, 1, 2, 3 };
    
    private static final int[] IP      = { 1, 5, 2, 0, 3, 7, 4, 6 };
    private static final int[] IP_INV  = { 3, 0, 2, 4, 6, 1, 7, 5 };
    
    private static final int[] EP = { 3, 0, 1, 2, 1, 2, 3, 0 };
    private static final int[] P4 = { 1, 3, 2, 0 };

    private static final int[][] S0 = {
        { 1, 0, 3, 2 },
        { 3, 2, 1, 0 },
        { 0, 2, 1, 3 },
        { 3, 1, 3, 2 }
    };

    private static final int[][] S1 = {
        { 0, 1, 2, 3 },
        { 2, 0, 1, 3 },
        { 3, 0, 1, 0 },
        { 2, 1, 0, 3 }
    };

    // Stored Subkeys
    public int[] K1 = new int[8];
    public int[] K2 = new int[8];

    /**
     * Constructor generates K1 and K2 immediately upon instantiation.
     */
    public SimplifiedDES(String key10Bit) {
        int[] key = stringToBits(key10Bit);
        generateKeys(key);
    }

    // --- Key Generation ---
    private void generateKeys(int[] key) {
        int[] p10Key = permute(key, P10);
        int[] leftHalf = Arrays.copyOfRange(p10Key, 0, 5);
        int[] rightHalf = Arrays.copyOfRange(p10Key, 5, 10);

        leftHalf = leftShift(leftHalf, 1);
        rightHalf = leftShift(rightHalf, 1);

        int[] combinedForK1 = combine(leftHalf, rightHalf);
        K1 = permute(combinedForK1, P8);

        leftHalf = leftShift(leftHalf, 2);
        rightHalf = leftShift(rightHalf, 2);

        int[] combinedForK2 = combine(leftHalf, rightHalf);
        K2 = permute(combinedForK2, P8);
    }

    // --- Core Encryption/Decryption (INT based) ---
    public int encrypt(int plaintext8Bit) {
        int[] pt = intToBits(plaintext8Bit);
        return bitsToInt(processBlock(pt, K1, K2));
    }

    public int decrypt(int ciphertext8Bit) {
        int[] ct = intToBits(ciphertext8Bit);
        // Decryption is the same process, but keys applied in reverse order
        return bitsToInt(processBlock(ct, K2, K1)); 
    }

    // --- String Helpers for Socket Communication ---
    
    // Encrypts an entire string character by character
    public String encryptString(String plaintext) {
        StringBuilder sb = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            sb.append((char) encrypt((int) c));
        }
        return sb.toString();
    }

    // Decrypts an entire string character by character
    public String decryptString(String ciphertext) {
        StringBuilder sb = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            sb.append((char) decrypt((int) c));
        }
        return sb.toString();
    }

    private int[] processBlock(int[] data, int[] firstKey, int[] secondKey) {
        int[] current = permute(data, IP);

        int[] left = Arrays.copyOfRange(current, 0, 4);
        int[] right = Arrays.copyOfRange(current, 4, 8);
        int[] fResult = feistel(right, firstKey);
        int[] newLeft = right; 
        int[] newRight = xor(left, fResult);

        fResult = feistel(newRight, secondKey);
        int[] finalLeft = xor(newLeft, fResult);
        int[] finalRight = newRight;

        int[] combined = combine(finalLeft, finalRight);
        return permute(combined, IP_INV);
    }

    // --- The Feistel Function (F) ---
    private int[] feistel(int[] rightHalf, int[] subkey) {
        int[] expanded = permute(rightHalf, EP);
        int[] xored = xor(expanded, subkey);
        int[] leftSBoxIn = Arrays.copyOfRange(xored, 0, 4);
        int[] rightSBoxIn = Arrays.copyOfRange(xored, 4, 8);
        int[] s0Out = applySBox(leftSBoxIn, S0);
        int[] s1Out = applySBox(rightSBoxIn, S1);
        int[] combinedSBoxOut = combine(s0Out, s1Out);
        return permute(combinedSBoxOut, P4);
    }

    private int[] applySBox(int[] in, int[][] sbox) {
        int row = (in[0] << 1) | in[3];
        int col = (in[1] << 1) | in[2];
        int val = sbox[row][col];
        return new int[]{ (val >> 1) & 1, val & 1 };
    }

    // --- Utility Methods ---
    private int[] permute(int[] input, int[] mapping) {
        int[] output = new int[mapping.length];
        for (int i = 0; i < mapping.length; i++) {
            output[i] = input[mapping[i]];
        }
        return output;
    }

    private int[] leftShift(int[] input, int shifts) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[(i + shifts) % input.length];
        }
        return output;
    }

    private int[] xor(int[] a, int[] b) {
        int[] output = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            output[i] = a[i] ^ b[i];
        }
        return output;
    }

    private int[] combine(int[] left, int[] right) {
        int[] output = new int[left.length + right.length];
        System.arraycopy(left, 0, output, 0, left.length);
        System.arraycopy(right, 0, output, left.length, right.length);
        return output;
    }

    // Parses a 10-bit binary string into an int array (Used for Keys)
    private int[] stringToBits(String s) {
        int[] bits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bits[i] = s.charAt(i) == '1' ? 1 : 0;
        }
        return bits;
    }

    // Converts an 8-bit integer into a bit array
    private int[] intToBits(int val) {
        int[] bits = new int[8];
        for (int i = 0; i < 8; i++) {
            bits[7 - i] = (val >> i) & 1;
        }
        return bits;
    }

    // Converts a bit array back into an integer
    private int bitsToInt(int[] bits) {
        int val = 0;
        for (int b : bits) {
            val = (val << 1) | b;
        }
        return val;
    }

    public String bitsToString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int b : bits) {
            sb.append(b);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // 1. Define a 10-bit key and initialize S-DES
        String key = "1010000010"; 
        SimplifiedDES sdes = new SimplifiedDES(key);

        // 2. Define the word/message you want to send
        String originalWord = "hello";
        
        System.out.println("--- S-DES String Test ---");
        System.out.println("10-Bit Key   : " + key);
        System.out.println("Original Word: " + originalWord);
        System.out.println("-------------------------");

        // 3. Encrypt the word
        String encryptedWord = sdes.encryptString(originalWord);
        System.out.println("Encrypted    : " + encryptedWord);

        // 4. Decrypt the word back to normal
        String decryptedWord = sdes.decryptString(encryptedWord);
        System.out.println("Decrypted    : " + decryptedWord);
        
        // Let's try a full sentence just to show it works on spaces too!
        System.out.println("\n--- Testing a longer phrase ---");
        String phrase = "Secret msg!";
        System.out.println("Original     : " + phrase);
        System.out.println("Encrypted    : " + sdes.encryptString(phrase));
        System.out.println("Decrypted    : " + sdes.decryptString(sdes.encryptString(phrase)));
    }
}