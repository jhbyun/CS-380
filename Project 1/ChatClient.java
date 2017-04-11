import java.util.*;
import java.io.*;
import java.net.*;


public class ChatClient {

	static Socket client;
	static boolean auth = false;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		// Default host and port
		client = new Socket("codebank.xyz", 38001);
		System.out.println("You are now connected to "
				+ client.getInetAddress());
		PrintStream out = new PrintStream(client.getOutputStream());
		Scanner keyboard = new Scanner(System.in);
		
		// Prompt the user for a Username
		System.out.println("Enter a Username:");
		String username = keyboard.nextLine();
		out.println(username);

		System.out.println("Type to chat or 'exit' to quit:");
		updater chatUp = new updater();
		chatUp.start();

		while (true) {
			String input = keyboard.nextLine();

			// Type "exit" to exit client
			out.println(input);
			if (input.equalsIgnoreCase("exit")) {
				out.println("Client is now disconnecting.");
				System.exit(0);
			}
		}
	}
	
	// Threading for updating the chat
	public static class updater extends Thread {
		public void run() {
			while (true) {
				try {
					updateChat();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void updateChat() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		String output = br.readLine();

		if (auth == false) {
			if (output.contentEquals("Name in use.")) {
				client.close();
				System.out.println("Name is in use, closing connection.");
				System.exit(0);
			}
			auth = true;
			System.out.println(output);
		} else {
			System.out.println(output);
		}
	}
}

