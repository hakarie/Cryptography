public class ExtendedEuclid {

    // Returns an array [gcd, x, y] such that (a * x) + (b * y) = gcd(a, b)
    public static long[] calculate(long a, long b) {
        if (b == 0) {
            return new long[]{a, 1, 0};
        }

        // Recursive call
        long[] result = calculate(b, a % b);
        long gcd = result[0];
        long x1 = result[1];
        long y1 = result[2];

        // Update x and y
        long x = y1;
        long y = x1 - (a / b) * y1;

        return new long[]{gcd, x, y};
    }

    // Uses the Extended Euclidean Algorithm to find the inverse of 'a' modulo 'm'
    public static long modInverse(long a, long m) {
        long[] result = calculate(a, m);
        long gcd = result[0];
        long x = result[1];

        if (gcd != 1) {
            throw new ArithmeticException("Inverse does not exist because GCD(" + a + ", " + m + ") is not 1.");
        }

        // Handle negative x values
        return (x % m + m) % m;
    }

    public static void main(String[] args) {
        // Test Extended Euclid
        long a = 30;
        long b = 20;
        long[] euclidResult = calculate(a, b);
        
        System.out.println("For a=" + a + ", b=" + b + ":");
        System.out.println("GCD = " + euclidResult[0]);
        System.out.println("x = " + euclidResult[1]);
        System.out.println("y = " + euclidResult[2]);
        
        System.out.println("\n-------------------\n");

        // Test Modular Inverse
        long num = 17;
        long m = 3120;
        long inverse = modInverse(num, m);
        
        System.out.println("Modular inverse of " + num + " mod " + m + " = " + inverse);
    }
}
