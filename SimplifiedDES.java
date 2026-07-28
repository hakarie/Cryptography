import java.util.Arrays;

public class SimplifiedDES {

    // --- S-DES Constants (0-indexed for array manipulation) ---
    private static final int[] P10 = { 2, 4, 1, 6, 3, 9, 0, 8, 7, 5 };
    private static final int[] P8  = { 5, 6, 7, 8, 0, 1, 2, 3 };
    
    private static final int[] IP     = { 1, 5, 2, 0, 3, 7, 4, 6 };
    private static final int[] IP_INV = { 3, 0, 2, 4, 6, 1, 7, 5 };
    
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
    private int[] K1 = new int[8];
    private int[] K2 = new int[8];

    /**
     * Constructor generates K1 and K2 immediately upon instantiation.
     */
    public SimplifiedDES(String key10Bit) {
        int[] key = stringToBits(key10Bit);
        generateKeys(key);
    }

    // --- Key Generation ---
    private void generateKeys(int[] key) {
        // 1. Apply P10 permutation
        int[] p10Key = permute(key, P10);

        // 2. Split into two 5-bit halves
        int[] leftHalf = Arrays.copyOfRange(p10Key, 0, 5);
        int[] rightHalf = Arrays.copyOfRange(p10Key, 5, 10);

        // 3. Apply LS-1 (Left Shift by 1) to both halves
        leftHalf = leftShift(leftHalf, 1);
        rightHalf = leftShift(rightHalf, 1);

        // 4. Combine and apply P8 to get K1
        int[] combinedForK1 = combine(leftHalf, rightHalf);
        K1 = permute(combinedForK1, P8);

        // 5. Apply LS-2 (Left Shift by 2) to the already shifted halves
        leftHalf = leftShift(leftHalf, 2);
        rightHalf = leftShift(rightHalf, 2);

        // 6. Combine and apply P8 to get K2
        int[] combinedForK2 = combine(leftHalf, rightHalf);
        K2 = permute(combinedForK2, P8);
    }

    // --- Core Encryption/Decryption ---
    public String encrypt(String plaintext8Bit) {
        int[] pt = stringToBits(plaintext8Bit);
        return bitsToString(processBlock(pt, K1, K2));
    }

    public String decrypt(String ciphertext8Bit) {
        int[] ct = stringToBits(ciphertext8Bit);
        // Decryption is the exact same process, but keys are applied in reverse order
        return bitsToString(processBlock(ct, K2, K1)); 
    }

    private int[] processBlock(int[] data, int[] firstKey, int[] secondKey) {
        // 1. Initial Permutation
        int[] current = permute(data, IP);

        // 2. Round 1
        int[] left = Arrays.copyOfRange(current, 0, 4);
        int[] right = Arrays.copyOfRange(current, 4, 8);
        int[] fResult = feistel(right, firstKey);
        int[] newLeft = right; 
        int[] newRight = xor(left, fResult);

        // 3. Round 2 (Notice the swap: newLeft and newRight act as Right and Left)
        fResult = feistel(newRight, secondKey);
        int[] finalLeft = xor(newLeft, fResult);
        int[] finalRight = newRight;

        // 4. Combine and Final Permutation (Inverse IP)
        int[] combined = combine(finalLeft, finalRight);
        return permute(combined, IP_INV);
    }

    // --- The Feistel Function (F) ---
    private int[] feistel(int[] rightHalf, int[] subkey) {
        // 1. Expand/Permute 4 bits to 8 bits
        int[] expanded = permute(rightHalf, EP);

        // 2. XOR with the subkey
        int[] xored = xor(expanded, subkey);

        // 3. Split for S-Boxes
        int[] leftSBoxIn = Arrays.copyOfRange(xored, 0, 4);
        int[] rightSBoxIn = Arrays.copyOfRange(xored, 4, 8);

        // 4. Apply S-Boxes
        int[] s0Out = applySBox(leftSBoxIn, S0);
        int[] s1Out = applySBox(rightSBoxIn, S1);

        // 5. Combine and apply P4
        int[] combinedSBoxOut = combine(s0Out, s1Out);
        return permute(combinedSBoxOut, P4);
    }

    private int[] applySBox(int[] in, int[][] sbox) {
        // Row is determined by the 1st and 4th bits
        int row = (in[0] << 1) | in[3];
        // Column is determined by the 2nd and 3rd bits
        int col = (in[1] << 1) | in[2];

        int val = sbox[row][col];
        // Convert the 0-3 integer value back into a 2-bit array
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

    private int[] stringToBits(String s) {
        int[] bits = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bits[i] = s.charAt(i) == '1' ? 1 : 0;
        }
        return bits;
    }

    private String bitsToString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int b : bits) {
            sb.append(b);
        }
        return sb.toString();
    }

    // --- Main Execution ---
    public static void main(String[] args) {
        String key = "1010000010"; // 10-bit key
        String plaintext = "10010111"; // 8-bit plaintext

        System.out.println("10-Bit Key: " + key);
        System.out.println("Plaintext:  " + plaintext);
        System.out.println("-------------------------");

        SimplifiedDES sdes = new SimplifiedDES(key);
        
        System.out.println("K1: " + sdes.bitsToString(sdes.K1));
        System.out.println("K2: " + sdes.bitsToString(sdes.K2));
        System.out.println("-------------------------");

        String ciphertext = sdes.encrypt(plaintext);
        System.out.println("Encrypted:  " + ciphertext);

        String decrypted = sdes.decrypt(ciphertext);
        System.out.println("Decrypted:  " + decrypted);
    }
}
