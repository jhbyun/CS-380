
import java.io.*;
import java.util.zip.CRC32;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Ex2Client {

	public static void main(String[] args) throws Exception {

		try (Socket socket = new Socket("codebank.xyz", 38102)) {
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			OutputStream out = socket.getOutputStream();

			int[] array = new int[100];
			byte[] bArray = new byte[100];
			int b1, b0;
			ByteBuffer bb = ByteBuffer.allocate(4);

			System.out.println("Connected to Server.");
			System.out.print("Received bytes: ");

			for (int i = 0; i < 100; i++) {
				if (i % 10 == 0)
					System.out.print("\n\t");
				b1 = is.read();
				b0 = is.read();
				array[i] = ((b1 << 4) | b0);
				System.out.printf("%02X", array[i]);
				bArray[i] = (byte) array[i];
			}

			CRC32 crc = new CRC32();
			crc.update(bArray, 0, 100);
			long code = crc.getValue();
			System.out.printf("\nGenerated CRC32: %08X.\n", code);

			bb.putInt((int) code);
			byte[] writeToServer = bb.array();
			out.write(writeToServer);
			int response = br.read();
			if (response == 1) {
				System.out.println("Response good.");
			}
			System.out.println("Disconnected from server.");

		}
	}
}
