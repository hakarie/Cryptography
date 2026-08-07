import java.io.*;
import java.net.*;

public class SDESClient {
    private Socket s = null;
    private DataInputStream in = null;
    private DataInputStream sin = null;
    private DataOutputStream out = null;

    public SDESClient(String addr, int port)
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

        String key = "1010000010"; 
        SimplifiedDES sdes = new SimplifiedDES(key);


        // Keep reading until "exit" is input
        while (true) {
            try {
                // send to server
                String m = in.readLine();
                m = sdes.encryptString(m);
                System.out.println("encrypted text: " + m);
                out.writeUTF(m);

                if(m.equals("exit")){
                    break;
                }

                // recieve form server
                String rec = sin.readUTF();
                System.out.println("cipher text: " + rec);
                rec = sdes.decryptString(rec);
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
        SDESClient c = new SDESClient("127.0.0.1", 5002);
    }
}
