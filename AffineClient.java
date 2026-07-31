import java.io.*;
import java.net.*;

public class AffineClient {
    // Initialize socket and input/output streams
    private Socket s = null;
    private DataInputStream in = null;
    private DataInputStream sin = null;
    private DataOutputStream out = null;

    public AffineClient(String addr, int port)
    {
        // Establish a connection
        try {
            s = new Socket(addr, port);
            System.out.println("Connected");

            // Takes input from terminal
            in = new DataInputStream(System.in);

            // recieve input from server
            sin = new DataInputStream(s.getInputStream());

            // Sends output to the socket
            out = new DataOutputStream(s.getOutputStream());
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }

        AffineCipher affine = new AffineCipher();
        int a = 7;
        int b = 5;


        // Keep reading until "exit" is input
        while (true) {
            try {
                // send to server
                String m = in.readLine();
                m = affine.encrypt(m, a, b);
                out.writeUTF(m);

                if(m.equals("exit")){
                    break;
                }

                // recieve form server
                String rec = sin.readUTF();
                rec = affine.decrypt(rec, a, b);
                System.out.println("Server: " + rec);
                if(rec.equals("exit")){
                    break;
                }
            } catch (IOException i){
                System.out.println(i);
            }

        }

        // Close the connection
        
        try {
            in.close();
            out.close();
            s.close();
        } catch (IOException i){
            System.out.println(i);
        }
        
    }

    public static void main(String[] args) {
        AffineClient c = new AffineClient("127.0.0.1", 5000);
    }
}
