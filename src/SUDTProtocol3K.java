import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// class for client
public class SUDTProtocol3K
{
	private static final byte DATA = 0x00;
	
	private static final byte HELLO_SERVER = 0x1E;
	private static final byte HELLO_CLIENT = 0x1A;
	
	private static final byte DATA_RECEIVED = 0x2D;
	
	private static final byte LENGTH_CHANGE = 0x31;
	private static final byte FONE_RESET = 0x3F;
	private static final byte COMMAND_EXECUTED = 0x3E;
	
	private static final byte GB_SERVER = 0x45;
	private static final byte GB_CLIENT = 0x4C;
	
	private static final byte ERROR = 0x66;
	
	
	private DataOutputStream out;
	private DataInputStream in;
	
	private int dataLen;
	
	public SUDTProtocol3K(DataInputStream in, DataOutputStream out)
	{
		this.out = out;
		this.in = in;
	}
	
	public boolean handshake(int dataLen) throws IOException
	{
		writeCommand(HELLO_SERVER);
		if (!awaitCommand(HELLO_CLIENT)) return false;
		out.writeInt(dataLen);
		out.flush();
		this.dataLen = dataLen;
		return true;
	}
	
	public boolean sendData(byte[] data) throws IOException
	{
		writeCommand(DATA);
		if (data.length != dataLen) throw new Error("Data lengths differ!");
		
		out.write(data);
		out.flush();
		return awaitCommand(DATA_RECEIVED);
	}
	
	public boolean sendFoneResetCommand() throws IOException
	{
		writeCommand(FONE_RESET);
		return awaitCommand(COMMAND_EXECUTED);
	}
	
	public boolean changeDataLength(int newLen) throws IOException
	{
		writeCommand(LENGTH_CHANGE);
		out.writeInt(newLen);
		out.flush();
		dataLen = newLen;
		return awaitCommand(COMMAND_EXECUTED);
	}
	
	public boolean closeConnection() throws IOException
	{
		writeCommand(GB_SERVER);
		return awaitCommand(GB_CLIENT);
	}
	
	public void sendInternalError() throws IOException
	{
		writeCommand(ERROR);
	}
	
	private boolean awaitCommand(byte command) throws IOException
	{
		byte response = in.readByte();
		if (response == command) return true;
		System.out.println("Unexpected answer: awaited " + command + ", got " + response);
		out.writeByte(ERROR);
		out.flush();
		return false;
	}
	private void writeCommand(byte answer) throws IOException
	{
		out.writeByte(answer);
		out.flush();
	}
}
