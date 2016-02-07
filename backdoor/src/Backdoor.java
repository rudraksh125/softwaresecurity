import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kvivekanandan Feb 7, 2016 Backdoor.java
 */

public class Backdoor {

	private static final String OUTPUT = "<html><head><title>nazgul</title></head><body><p>nazgul</p></body></html>";
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
	private int port;
	
	final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

	public Backdoor(int port) {
		this.port = port;
	}

	public void openConnection() throws IOException {
			
		ServerSocket serverSocket = new ServerSocket(port);

		while (true) {
			System.out.println("waiting for connections on " + port);
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection on " + port);
			clientProcessingPool.submit(new Runnable() {

				@Override
				public void run() {
					try {
						DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						String input, output;

						while ((input = in.readLine()) != null) {
							System.out.println(input);
							if (input.isEmpty()) {
								break;
							}
						}
						out.writeBytes(OUTPUT_HEADERS + OUTPUT.length() + OUTPUT_END_OF_HEADERS + OUTPUT);
						in.close();
						out.flush();
						out.close();
						System.out.println("Request processed on " + port);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}

	}

	public static void main(String[] args) {
		int portNumber = Integer.parseInt("4567");
		Backdoor backdoor = new Backdoor(portNumber);
		backdoor.run();

	}

	public void run() {
		try {
			openConnection();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
}
