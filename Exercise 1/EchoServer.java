
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

public final class EchoServer {
	
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(22222);
        System.out.println("Server is Online. You may now run EchoClient.");
        try {
            while (true) {
                new MultiThreadServer(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }
	
	public static class MultiThreadServer extends Thread {
        Socket socket;
        public MultiThreadServer(Socket s) {
            this.socket = s;
        }
        
        public void run() {        	
            try {
            	String address = socket.getInetAddress().getHostAddress();
            	System.out.printf("Client connected: %s%n", address);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw.println("Hi, thanks for connecting! Type 'exit' to close.");
                String input;

                do 
                {
                	input = br.readLine();
                    if (input != null) pw.println("Server> " + input);
                }
                while (!input.trim().equals("exit"));
                System.out.printf("Client disconnected: %s%n", address);  
                socket.close();
            } catch (IOException e) {
            	System.out.println(e);
            }
        }		
	}	
}

