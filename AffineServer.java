import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AffineServer {
    private Socket s = null;
    private ServerSocket ss = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public AffineServer(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            s = ss.accept();
            System.out.println("Client accepted");

            Scanner sc = new Scanner(System.in);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            AffineCipher affine = new AffineCipher();
            // CHANGED: Must match the client's valid key
            int a = 5; 
            int b = 2;

            while(true) {
                try {
                    // Receive from client
                    String m = in.readUTF();
                    System.out.println("cipher text: " + m);
                    m = affine.decrypt(m, a, b);
                    System.out.println("Client: " + m);
                    
                    if(m.equals("exit")) {
                        break;
                    }
                } catch(IOException i) {
                    System.out.println(i);
                    break;
                }
                
                // Send to client
                System.out.print("You: ");
                String send = sc.nextLine();
                
                // Break BEFORE checking encrypted text
                if(send.equals("exit")){
                    out.writeUTF(affine.encrypt(send, a, b));
                    break;
                }
                
                send = affine.encrypt(send, a, b);
                System.out.println("encrypted text: " + send);
                out.writeUTF(send);
            }

            s.close();
            in.close();
            sc.close();
            ss.close();
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        AffineServer s = new AffineServer(5003);
    }
}