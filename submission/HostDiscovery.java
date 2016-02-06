import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author kvivekanandan Feb 6, 2016 HostDiscovery.java
 */

public class HostDiscovery {
	public static boolean readFileEtcHosts(String filename) throws FileNotFoundException {
		BufferedReader br;
		try {
			File f = new File(filename);
			if (f.exists() && f.isFile()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty() && !line.trim().startsWith("#")) {
						String t = line.trim().replaceAll("\\s+", " ");
						if (t.contains("#")) {
							t = t.split("#")[0];
						}
						String[] tokens = t.split(" ");
						for (int i = 1; i < tokens.length; i++) {
							System.out.println(tokens[i]);
						}
					}
				}
				br.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean readFileSshConfig(String fileName) {
		BufferedReader br;
		try {
			File f = new File(fileName);
			if (f.exists() && f.isFile()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty() && !line.trim().startsWith("#")) {
						String[] l = line.trim().split(" ", 2);
						if (l != null && l.length > 1) {
							if (l[0].equals("Host")) {
								System.out.println(l[1]);
							} else if (l[0].equals("HostName")) {
								System.out.println(l[1]);
							}
						}
					}
				}
				br.close();
				return true;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public static void main(String[] args) {
		try {
			readFileEtcHosts("/etc/hosts");
			readFileSshConfig(System.getProperty("user.home") + "/.ssh/config");
			readFileSshConfig("/etc/ssh/ssh_config");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
