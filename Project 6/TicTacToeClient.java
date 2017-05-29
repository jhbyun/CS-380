import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		try (Socket socket = new Socket("codebank.xyz", 38006)) {
			System.out.println("Connected to Server.");
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

			Scanner input = new Scanner(System.in);
			System.out.print("\nEnter a username: ");
			String username = input.nextLine();
			ConnectMessage name = new ConnectMessage(username);
			os.writeObject(name);
			System.out.println("\nWelcome to TicTacToeClient, " + username + "!");

			// Send CommandMessage to start a new game with the server
			CommandMessage command = new CommandMessage(CommandMessage.Command.NEW_GAME);
			os.writeObject(command);
			System.out.println("Starting a New Game...");

			// Get server's board response and print board
			Object response = is.readObject();
			BoardMessage board = (BoardMessage) response;
			printBoard(board.getBoard());
			System.out.println();

			// Start Playing
			byte row, col;
			MoveMessage move;

			while (true) {
				System.out.print("Enter row (0-2): ");
				row = input.nextByte();
				System.out.print("Enter column (0-2): ");
				col = input.nextByte();
				System.out.println();

				// Send Move Message
				move = new MoveMessage(row, col);
				os.writeObject(move);

				// Get server's board response and print board
				response = is.readObject();

				if (response instanceof ErrorMessage) {
					System.out.println("\n" + ((ErrorMessage) response).getError());
					System.out.println();
				} else if (response instanceof BoardMessage) {
					board = (BoardMessage) response;
					printBoard(board.getBoard());
					System.out.println();
				}

				if (board.getStatus() == BoardMessage.Status.IN_PROGRESS) {
					continue;
				} else {
					System.out.println("Outcome: " + board.getStatus());
					System.out.println("GAME OVER!");
					socket.close();
					break;
				}
			}
		}
	}

	// Print the board
	private static void printBoard(byte[][] board) {
		System.out.println("\nBoard: You are X\n");
		for (int i = 0; i < board.length; i++) {
			System.out.print("   ");
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == ((byte) 0)) {
					System.out.print("- ");
				} else if (board[i][j] == ((byte) 1)) {
					System.out.print("X ");
				} else if (board[i][j] == ((byte) 2)) {
					System.out.print("O ");
				}
			}
			System.out.println();
		}
	}
}
