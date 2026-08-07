import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AffineClient {
    private Socket s = null;
    private Scanner sc = null; // Changed to Scanner for console input
    private DataInputStream sin = null;
    private DataOutputStream out = null;

    public AffineClient(String addr, int port) {
        try {
            s = new Socket(addr, port);
            System.out.println("Connected");

            // Correctly use Scanner for terminal input
            sc = new Scanner(System.in);
            sin = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        AffineCipher affine = new AffineCipher();
        // CHANGED: 'a' must be coprime to 26 (e.g., 5). 13 is invalid.
        int a = 5; 
        int b = 2;

        if (gcd(a, 26) != 1) {
            System.out.println("Invalid 'a' key! Must be coprime to 26.");
            return;
        }        

        while (true) {
            try {
                // Send to server
                System.out.print("You: ");
                String m = sc.nextLine();
                
                // Break BEFORE encryption if user types exit
                if(m.equals("exit")) {
                    out.writeUTF(affine.encrypt(m, a, b)); // let server know we are exiting
                    break;
                }

                m = affine.encrypt(m, a, b);
                System.out.println("encrypted text: " + m);
                out.writeUTF(m);

                // Receive from server
                String rec = sin.readUTF();
                System.out.println("cipher text: " + rec);
                rec = affine.decrypt(rec, a, b);
                System.out.println("Server: " + rec);
                
                if(rec.equals("exit")){
                    break;
                }
            } catch (IOException i) {
                System.out.println(i);
                break;
            }
        }

        try {
            sc.close();
            sin.close();
            out.close();
            s.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    // FIXED standard Euclidean algorithm
    public int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b); 
    }

    public static void main(String[] args) {
        AffineClient c = new AffineClient("127.0.0.1", 5003);
    }
}