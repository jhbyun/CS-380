
import java.net.*;
import java.io.*;
import java.util.TreeMap;

public class PhysLayerClient{

	public static void main(String[] args) throws IOException{
		try (Socket socket = new Socket("codebank.xyz", 38002)) {
			System.out.println("Connected to server.");
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			
			// Preamble
			double baseline = 0.0;
			for(int i = 0; i < 64; i++){
				int signal = input.read();
				baseline += signal;
			}
			baseline /= 64;
			System.out.printf("Baseline established from preamble: %.2f\n", baseline);
			
			TreeMap<String, String> table = new TreeMap<>();
			initTable(table);
			String[] halfBytes = new String[64];
			boolean prevSignal = false;	
			for(int i = 0; i < 64; i++){
				String fiveBits = "";
				for(int j = 0; j < 5; j++){
					boolean signal = input.read()>baseline;
					fiveBits += (prevSignal==signal)? "0":"1";
					prevSignal = signal;
				}
				halfBytes[i] = table.get(fiveBits);
			}
			
			
			// Combine halfbytes to bytes
			System.out.print("Received 32 bytes: ");
			byte[] b = new byte[32];
			for(int i = 0; i < 32; i++){
				String fHalf = halfBytes[2*i];
				String sHalf = halfBytes[2*i+1];
				System.out.printf("%X", Integer.parseInt(fHalf, 2));
				System.out.printf("%X", Integer.parseInt(sHalf, 2));
				String wholeByte = fHalf + sHalf;
				b[i] = (byte)Integer.parseInt(wholeByte, 2);
			}
			System.out.println();
			output.write(b);
			if (input.read()==1){
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad.");
			}
		}
		System.out.println("Disconnected from server.");
	}

	public static void initTable(TreeMap<String,String> table){
		// 		  5-Bit    4-Bit
		table.put("11110","0000");
		table.put("01001","0001");
		table.put("10100","0010");
		table.put("10101","0011");
		table.put("01010","0100");
		table.put("01011","0101");
		table.put("01110","0110");
		table.put("01111","0111");
		table.put("10010","1000");
		table.put("10011","1001");
		table.put("10110","1010");
		table.put("10111","1011");
		table.put("11010","1100");
		table.put("11011","1101");
		table.put("11100","1110");
		table.put("11101","1111");
	}
}
