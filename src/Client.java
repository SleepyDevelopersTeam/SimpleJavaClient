import java.net.*;
import java.util.Random;
import java.io.*;

public class Client {
	static byte[] data;
	
	static void allocate(int newSize)
	{
		data = new byte[newSize];
		for (int i = 0; i < newSize; i++)
		{
			data[i] = (byte) (i % 10);
		}
	}
	
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
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            
            SUDTProtocol3K net = new SUDTProtocol3K(in, out);

            // keyboard stream
            // BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Client started");
            
            int l = behaviour.nextInt(100000);
            if (l < 0) l = -l;
            //byte[] data = new byte[l];
            allocate(l);
            
            boolean open = true;
            
            boolean b = net.handshake(l); 
            assert b: "Handshake failed";
            System.out.println("Handshake send with length " + l);

            while (open) {
            	Thread.sleep(50);
                float rnd = behaviour.nextFloat();
                if (rnd < 0.005F)
                {
                	l = behaviour.nextInt(100000);
                	allocate(l);
                	b = net.changeDataLength(l);
                	assert b : "New length set failed";
                	System.out.println("New length: " + l);
                	continue;
                }
                else if (rnd < 0.015F)
                {
                	b = net.sendFoneResetCommand();
                	assert b : "Fone reset failed";
                	System.out.println("Fone reset");
                	continue;
                }
                else if (rnd < 0.02F)
                {
                	b = net.closeConnection();
                	assert b : "Invalid connection close";
                	System.out.println("Connection closed");
                	open = false;
                	break;
                }
                else
                {
                	//behaviour.nextBytes(data);
                	b = net.sendData(data);
                	assert b: "Data sending error";
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