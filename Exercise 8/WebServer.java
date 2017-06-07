import java.io.*;
import java.net.*;

public class WebServer {

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		try (ServerSocket serverSocket = new ServerSocket(8080)) {

			try (Socket socket = serverSocket.accept()) {

				try {
					InputStreamReader isr = new InputStreamReader(socket.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String addr = br.readLine();
					String[] split = addr.split(" ");
					File file = new File("./www" + split[1]);

					int response;
					long length;
					if (file.exists())
						response = 200;
					else {
						response = 404;
						file = new File("./www/NotFound.html");
					}

					FileInputStream fis = new FileInputStream(file);
					BufferedReader fr = new BufferedReader(new InputStreamReader(fis));
					length = file.length();

					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					pw.println("HTTP/1.1 " + response + " OK");
					pw.println("Content-type: text/html");
					pw.println("Content-length: " + length);
					pw.println("");

					String line = fr.readLine();
					while (line != null) {
						pw.println(line);
						line = fr.readLine();
					}
					pw.flush();

				} catch (Exception e) {
					
				}

			} catch (Exception e) {

			}

		} catch (IOException e) {

		}
	}
}
