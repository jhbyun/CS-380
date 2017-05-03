
import java.net.Socket;
import java.io.*;

public class Ipv4Client{

	public static void main(String[] args)throws IOException{
		try(Socket socket = new Socket("codebank.xyz", 38003)){
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			OutputStream os = socket.getOutputStream();
			byte[] packet;
			int dataSize = 1;
			for(int i = 1; i < 13; i++){
				dataSize *= 2;
				int totalLength = (20 + dataSize);
				packet = new byte[totalLength];

				packet[0] = 0x45; //version 4 and HeaderLength 5
				packet[1] = 0; //TOS
				packet[2] = (byte)(totalLength >>> 8); //first byte of length
				packet[3] = (byte)(totalLength); //second byte of length
				packet[4] = 0; //first byte of Ident
				packet[5] = 0; //second byte of Ident
				packet[6] = 0x40; //flags and offset
				packet[7] = 0; //offset cont.
				packet[8] = 50; //TTL
				packet[9] = 6; //protocol
				packet[10] = 0; //assume checksum 0 first
				packet[11] = 0; //assume checksum 0 first
				packet[12] = 0; //Source Address = 0
	            packet[13] = 0; //Source Address = 0
	            packet[14] = 0; //Source Address = 0
	            packet[15] = 0; //Source Address = 0
				byte[] destAddr = socket.getInetAddress().getAddress();
				for(int j = 0; j < 4; j++) //destAddr
					packet[j+16] = destAddr[j];
				short checksum = checksum(packet); //calculate checksum
				packet[10] = (byte)(checksum >>> 8); //first byte of checksum
				packet[11] = (byte)(checksum); //second byte of checksum
				os.write(packet);
				String message = br.readLine();
				System.out.println("data length: " + dataSize);
				System.out.println(message);
				System.out.println();
			}
		}
	}

    public static short checksum(byte[] header) {
        long sum = 0;
        int length = header.length;
        int i = 0;
        long total = 0;
        
        while (length > 1) {
            sum = sum + ((header[i] << 8 & 0xFF00) | ((header[i + 1]) & 0x00FF));
            i = i + 2;
            length = length - 2;
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum++;
            }
        }
        if (length > 0) {
            sum += header[i] << 8 & 0xFF00;
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum++;
            }
        }
        total = (~((sum & 0xFFFF) + (sum >> 16))) & 0xFFFF;
        return (short) total;
    }

}

