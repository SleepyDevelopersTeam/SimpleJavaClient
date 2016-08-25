import java.net.*;
import java.util.Random;
import java.io.*;

public class Client {
    public static void main(String[] ar) {
        int serverPort = 9090;
        String address = "127.0.0.1";
        
        Random behaviour = new Random();

        try {
            InetAddress ipAddress = InetAddress.getByName(address); // ip adress
            Socket socket = new Socket(ipAddress, serverPort); // socket

            //streams input and output
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            // string stram
            DataInputStream in = new DataInputStream(sin);//new BufferedInputStream(sin));
            DataOutputStream out = new DataOutputStream(sout);//new BufferedOutputStream(sout));
            
            SUDTProtocol3K net = new SUDTProtocol3K(in, out);

            // keyboard stream
            // BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Client started");
            
            int l = behaviour.nextInt(100000);
            if (l < 0) l = -l;
            byte[] data = new byte[l];
            
            boolean open = true;
            
            boolean b = net.handshake(l); 
            if (!b) throw new Exception("Handshake failed");
            System.out.println("Handshake send with length " + l);

            while (open) {
            	Thread.sleep(50);
                float rnd = behaviour.nextFloat();
                if (rnd < 0.005F)
                {
                	l = behaviour.nextInt(100000);
                	data = new byte[l];
                	b = net.changeDataLength(l);
                	if (!b) throw new Exception("New length set failed");
                	System.out.println("New length: " + l);
                	continue;
                }
                else if (rnd < 0.015F)
                {
                	b = net.sendFoneResetCommand();
                	if (!b) throw new Exception("Fone reset failed");
                	System.out.println("Fone reset");
                	continue;
                }
                else if (rnd < 0.02F && false)
                {
                	b = net.closeConnection();
                	if (!b) throw new Exception("Invalid connection close");
                	System.out.println("Connection closed");
                	open = false;
                	break;
                }
                else
                {
                	//behaviour.nextBytes(data);
                	for (int i=0; i<data.length; i++)
                		data[i] = 22;
                	if (data.length != l) System.out.println("FUCK");
                	b = net.sendData(data);
                	if (!b) throw new Exception("Server cannot receive data!");
                	System.out.print(".");
                	continue;
                }
            }
        } catch (Exception x) {
        	System.out.println("Error occured, stopping");
        	System.out.println(x.getMessage());
            x.printStackTrace();
        }
    }
}