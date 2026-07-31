import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AffineServer {
    private Socket s = null;
    private ServerSocket ss = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // Constructor with port
    public AffineServer(int port) {
      
        // Starts server and waits for a connection
        try
        {
            ss = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            s = ss.accept();
            System.out.println("Client accepted");

            Scanner sc = new Scanner(System.in);

            // Takes input from the client socket
            in = new DataInputStream(s.getInputStream());
            // send output to client
            out = new DataOutputStream(s.getOutputStream());

            AffineCipher affine = new AffineCipher();
            int a = 7;
            int b = 5;


            while(true){

                // recive from client
                try {
                    String m = in.readUTF();
                    m = affine.decrypt(m, a, b);
                    System.out.println("Client: " + m);
                    if(m.equals("exit")){
                        break;
                    }
                } catch(IOException i) {
                    System.out.println(i);
                }
                

                // send to client
                String send = sc.nextLine();
                send = affine.encrypt(send, a, b);
                out.writeUTF(send);

                if(send.equals("exit")){
                    break;
                }

            }

            // Close connection
            s.close();
            in.close();
            sc.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        AffineServer s = new AffineServer(5000);
    }
}
