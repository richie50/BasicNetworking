import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Calendar;


public class PingClient {
	private static final int AVERAGE_DELAY = 100; // milliseconds

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String input = new String();
		String[] in = new String[3];
		DatagramPacket clientPackets = null;
		DatagramPacket serverPackets = null;
		DatagramSocket client = new DatagramSocket();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Use CTRL C to exit . . . . ");
		System.out.printf(">");
		while ((input = reader.readLine()) != null) {
			in = input.split(" ");
			if (in.length < 3 || !in[0].equals("ping")) {
				if(in.length == 1 || in.length == 2 ){
					throw new IllegalArgumentException("Usage <ping> <destination IP address> <port number>");
				}
				throw new IllegalArgumentException(
						"Usage ping <destination missing> <port number missing> . . . ."
								+ in[0] + " " + in[1] + " " + in[2]);
			}
			byte[] sendBuffer = new byte[1024];
			byte[] recBuffer = new byte[1024];
			InetAddress ip = InetAddress.getByName(in[1]);
			int portNumber = Integer.parseInt(in[2]);
			int  sequenceNumber= 0; // send ping 10 times
			while (sequenceNumber < 10) {
				java.util.Date date = Calendar.getInstance().getTime();
				String s = "PING ";
				StringBuilder sb = new StringBuilder(s);
				sb.append(sequenceNumber);
				sb.append(" ");
				sb.append(date);
				String ss = new String(sb.toString());
				// create a new datagram
				sendBuffer = ss.getBytes();
				clientPackets = new DatagramPacket(sendBuffer,
						sendBuffer.length, ip, portNumber);
				client.send(clientPackets); //  send the packet the first packet if 
				client.setSoTimeout(1000); // set a timeout for the packet to one second due to blocking an non blocking I/O operations
				// re-sending data after a timeout
				// for every PING packet we receive output its data
				serverPackets = new DatagramPacket(recBuffer, recBuffer.length);
				try {
					client.receive(serverPackets);
					String rec = printData(serverPackets);
					System.out.println(rec);
					//System.out.println(rec.length() +" bytes received");
				} catch (SocketTimeoutException ex) {
					System.out.println(s + " " + sequenceNumber + " Request Timed out");
					// PLEASE NOTE I ADDED THIS LINE TO RESEND THE PACKET AFTER A TIME OUT 
					//feel free to add this code back in.
					//client.send(clientPackets); 
					 
				}
				Thread.sleep(500);
				sequenceNumber++;
			}
			for (int i = 0; i < in.length; i++) {
				in[i] = new String();// flush 
			}
			System.out.println("Use CTRL C to exit . . . . ");
			System.out.printf(">");
		}
		client.close();
	}

	/*
	 * Print ping data to the standard output stream.
	 */
	private static String printData(DatagramPacket request) throws Exception {
		// Obtain references to the packet's array of bytes.
		byte[] buf = request.getData();
		// Wrap the bytes in a byte array input stream,
		// so that you can read the data as a stream of bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		// Wrap the byte array output stream in an input stream reader,
		// so you can read the data as a stream of characters.
		InputStreamReader isr = new InputStreamReader(bais);
		// Wrap the input stream reader in a bufferred reader,
		// so you can read the character data a line at a time.
		// (A line is a sequence of chars terminated by any combination of \r
		// and \n.)
		BufferedReader br = new BufferedReader(isr);
		// The message data is contained in a single line, so read this line.
		String line = br.readLine();
		// Print host address and data received from it.
		return "Received from " + request.getAddress().getHostAddress() + ": "
				+ new String(line);
	}

}
