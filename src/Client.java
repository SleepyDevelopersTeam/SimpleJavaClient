import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] ar) {
        int serverPort = 9090;
        String address = "127.0.0.1";

        try {
            InetAddress ipAddress = InetAddress.getByName(address); // ip adress
            Socket socket = new Socket(ipAddress, serverPort); // socket

            //streams input and output
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            // string stram
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            // keyboard stream
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            System.out.println("Type text");
            System.out.println();

            while (true) {
                line = keyboard.readLine(); // reading
                out.writeUTF(line); //write in socket
                out.flush();
                line = in.readUTF(); // waiting server
                System.out.println("The server send : " + line);
                System.out.println();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}