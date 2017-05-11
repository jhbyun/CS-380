import java.net.*;
import java.util.Random;
import java.io.*;

public class Ipv6Client{

	public static void main(String[] args)throws IOException{
		try(Socket socket = new Socket("codebank.xyz", 38004)){
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			byte[] packet;
			for(int i = 1; i < 13; i++){
				int dataSize = (int)Math.pow(2.0, i);
				short totalLength = (short)(40 + dataSize);
				packet = new byte[totalLength];
				new Random().nextBytes(packet); //assign random data first
				//now header
				packet[0] = 0b01100000; //version 6 and TrafficClass
				packet[1] = 0; //TrafficClass cont'd and FlowLabel
				packet[2] = 0; //FlowLabel
				packet[3] = 0; //FlowLabel
				packet[4] = (byte)((dataSize & 0xFF00) >> 8); //first byte of PayloadLen
				packet[5] = (byte)(dataSize & 0x00FF); //second byte of PayloadLen
				packet[6] = 17; //NextHeader
				packet[7] = 20; //HopLimit
				byte[] ipv4Source = new byte[4];
				new Random().nextBytes(ipv4Source); //a random IPv4 address
				for(int j = 8; j < 18; j++) //80 0s for sourceAddr
					packet[j] = 0;
				for(int j = 18; j < 20; j++) //16 1s for sourceAddress
					packet[j] = (byte)0xFF;
				for(int j = 0; j < 4; j++) //4 byte IPv4 address
					packet[j+20] = ipv4Source[j];
				byte[] ipv4Dest = socket.getInetAddress().getAddress();
				for(int j = 24; j < 34; j++) //80 0s for destAddr
					packet[j] = 0;
				for(int j = 34; j < 36; j++) //16 1s for destAddr
					packet[j] = (byte)0xFF;
				for(int j = 0; j < 4; j++) //4 byte IPv4 address
					packet[j+36] = ipv4Dest[j];
				out.write(packet);
				byte[] code = new byte[4];
				in.read(code);
				System.out.println("data length: " + dataSize);
				System.out.print("Response: 0x");
				for(byte e: code)
					System.out.printf("%X", e);
				System.out.println("\n");
			}
		}
	}
}
