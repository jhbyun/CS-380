import java.io.*;
import java.net.Socket;
import java.util.Random;

public final class UdpClient {
	
	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket("codebank.xyz", 38005)) {
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();

			byte[] handshake = createIPv4handShake();
			os.write(handshake);
			System.out.println("Handshake Response: 0x" + Integer.toHexString(is.read()).toUpperCase()
					+ Integer.toHexString(is.read()).toUpperCase() + Integer.toHexString(is.read()).toUpperCase()
					+ Integer.toHexString(is.read()).toUpperCase());

			int port = (is.read() << 8);
			port += is.read();
			System.out.println("Port Number Received: " + port + "\n");

			int length = 1;
			int sum = 0;
			for (int i = 1; i < 13; i++) {
				length *= 2;
				System.out.println("Sending packet with " + length + " bytes of data");

				byte[] packet = createIPv4UDP(length, port);
				long start = System.currentTimeMillis();
				os.write(packet);
				System.out.println("Response: 0x" + Integer.toHexString(is.read()).toUpperCase()
						+ Integer.toHexString(is.read()).toUpperCase() + Integer.toHexString(is.read()).toUpperCase()
						+ Integer.toHexString(is.read()).toUpperCase());

				long end = System.currentTimeMillis();
				long RTT = end - start;
				System.out.println("RTT: " + RTT + "ms" + "\n");
				sum += RTT;
			}
			double avg = sum / 12.00;
			System.out.print("Average RTT: ");
			System.out.printf("%.2f", avg);
			System.out.println("ms\n");

		}
	}

	public static short checksum(byte[] b) {
		long sum = 0;
		int count = 20;
		long byteC;
		int i = 0;
		while (count > 1) {
			byteC = (((b[i] << 8) & 0xFF00) | ((b[i + 1]) & 0xFF));
			sum += byteC;
			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum += 1;
			}
			i += 2;
			count -= 2;
		}
		if (count > 0) {
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

	// creates handshake IPv4 packet
	public static byte[] createIPv4handShake() {
		byte[] packet = new byte[24];
		packet[0] = (byte) 69;
		packet[1] = (byte) 0;
		packet[2] = (byte) 0;
		packet[3] = (byte) 24;
		packet[4] = (byte) 0;
		packet[5] = (byte) 0;
		packet[6] = (byte) 64;
		packet[7] = (byte) 0;
		packet[8] = (byte) 50;
		packet[9] = (byte) 17;
		packet[10] = (byte) 0;
		packet[11] = (byte) 0;
		packet[12] = (byte) 134;
		packet[13] = (byte) 71;
		packet[14] = (byte) 249;
		packet[15] = (byte) 228;
		packet[16] = (byte) 52;
		packet[17] = (byte) 37;
		packet[18] = (byte) 88;
		packet[19] = (byte) 154;
		packet[20] = (byte) 222;
		packet[21] = (byte) 173;
		packet[22] = (byte) 190;
		packet[23] = (byte) 239;
		int checksum = (int) checksum(packet);
		checksum = checksum & 0x0000FFFF;
		packet[10] = (byte) (checksum >> 8);
		packet[11] = (byte) checksum;
		return packet;
	}

	// creates IPv4/UDP packet
	public static byte[] createIPv4UDP(int size, int destPort) {
		byte[] packet = new byte[28 + size];
		packet[0] = (byte) 69;
		packet[1] = (byte) 0;
		packet[2] = (byte) ((28 + size) >> 8);
		packet[3] = (byte) (28 + size);
		packet[4] = (byte) 0;
		packet[5] = (byte) 0;
		packet[6] = (byte) 64;
		packet[7] = (byte) 0;
		packet[8] = (byte) 50;
		packet[9] = (byte) 17;
		packet[10] = (byte) 0;
		packet[11] = (byte) 0;
		packet[12] = (byte) 134;
		packet[13] = (byte) 71;
		packet[14] = (byte) 249;
		packet[15] = (byte) 228;
		packet[16] = (byte) 52;
		packet[17] = (byte) 37;
		packet[18] = (byte) 88;
		packet[19] = (byte) 154;
		packet[20] = (byte) 0;
		packet[21] = (byte) 0;
		packet[22] = (byte) (destPort >> 8);
		packet[23] = (byte) destPort;
		packet[24] = (byte) ((size + 8) >> 8);
		packet[25] = (byte) (size + 8);
		packet[26] = (byte) 0;
		packet[27] = (byte) 0;
		for (int i = 28; i < packet.length; i++) {
			packet[i] = randByte();
		}

		// IP checksum
		int checksum = (int) checksum(packet);
		checksum = checksum & 0x0000FFFF;
		packet[10] = (byte) (checksum >> 8);
		packet[11] = (byte) checksum;

		// UDP checksum
		int UDPchecksum = (int) getUDPchecksum(packet, size);
		UDPchecksum = UDPchecksum & 0x0000FFFF;
		packet[26] = (byte) (UDPchecksum >> 8);
		packet[27] = (byte) UDPchecksum;

		return packet;
	}

	public static byte randByte() {
		Random rand = new Random();
		return (byte) rand.nextInt(256);
	}

	public static short getUDPchecksum(byte[] packet, int dataLength) {
		byte[] ph = new byte[20 + dataLength];
		ph[0] = packet[12];
		ph[1] = packet[13];
		ph[2] = packet[14];
		ph[3] = packet[15];
		ph[4] = packet[16];
		ph[5] = packet[17];
		ph[6] = packet[18];
		ph[7] = packet[19];
		ph[8] = (byte) 0;
		ph[9] = packet[9];
		ph[10] = packet[24];
		ph[11] = packet[25];
		ph[12] = packet[20];
		ph[13] = packet[21];
		ph[14] = packet[22];
		ph[15] = packet[23];
		ph[16] = packet[24];
		ph[17] = packet[25];
		ph[18] = packet[26];
		ph[19] = packet[27];
		for (int i = 0; i < dataLength; i++) {
			ph[20 + i] = packet[28 + i];
		}

		long sum = 0;
		int count = ph.length;
		long byteC;
		int i = 0;
		while (count > 1) {
			byteC = (((ph[i] << 8) & 0xFF00) | ((ph[i + 1]) & 0xFF));
			sum += byteC;
			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum += 1;
			}
			i += 2;
			count -= 2;
		}
		if (count > 0) {
			sum += (ph[ph.length - 1] << 8 & 0xFF00);
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
