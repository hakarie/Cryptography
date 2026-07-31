import java.io.*;
import java.net.*;

public class CaesarClient {
  
    // Initialize socket and input/output streams
    private Socket s = null;
    private DataInputStream in = null;
    private DataInputStream sin = null;
    private DataOutputStream out = null;

    public CaesarClient(String addr, int port)
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

        CaesarCipher caesar = new CaesarCipher();
        int key = 3;


        // Keep reading until "exit" is input
        while (true) {
            try {
                // send to server
                String m = in.readLine();
                m = caesar.encrypt(m, key);
                System.out.println(m);
                out.writeUTF(m);

                if(m.equals("exit")){
                    break;
                }

                // recieve form server
                String rec = sin.readUTF();
                rec = caesar.decrypt(rec, key);
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
        CaesarClient c = new CaesarClient("127.0.0.1", 5000);
    }
}