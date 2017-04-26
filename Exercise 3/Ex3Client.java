import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Ex3Client {

	public static void main(String[] args) throws Exception {

		try (Socket socket = new Socket("codebank.xyz", 38103)) {
			System.out.println("Connected to server.");
			InputStream istream = socket.getInputStream();
			OutputStream ostream = socket.getOutputStream();

			int readBytes = istream.read();
			System.out.println("Reading " + readBytes + " bytes.");
			byte msg[] = new byte[readBytes];

			System.out.print("Data received:");
			for (int i = 0; i < readBytes; i++) {
				int data = istream.read();
				msg[i] = (byte) data;
				if (i % 10 == 0) {
					System.out.print("\n   ");
				}
				if (data < 16) {
					System.out.print(0);
				}
				System.out.printf("%02X", data);
			}

			short sum = checksum(msg);
			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.putShort(sum);
			byte returnMessage[] = bb.array();
			int l = (int) sum;
			l = l & 0x0000FFFF;
			System.out.print("\nChecksum calculated: 0x");
			System.out.printf("%02X", l);
			System.out.println(".");
			ostream.write(returnMessage);

			int response = istream.read();
			if (response == 1) {
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad");
			}
		}
		System.out.println("Disconnected from server.");
	}

	public static short checksum(byte[] b) {
		int i = 0;
		long sum = 0;
		int length = b.length;
		long byteC;

		while (length > 1) {
			byteC = (((b[i] << 8) & 0xFF00) | ((b[i + 1]) & 0xFF));
			sum += byteC;
			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum += 1;
			}
			i += 2;
			length -= 2;
		}
		if (length > 0) {
			sum += (b[b.length - 1] << 8 & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
		}
		sum = ~sum;
		sum = sum & 0xFFFF;
		return (short) sum;
	}
}
