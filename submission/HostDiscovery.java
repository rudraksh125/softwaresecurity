import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author kvivekanandan Feb 6, 2016 HostDiscovery.java
 */

public class HostDiscovery {
	public static boolean readFile(String filename) throws FileNotFoundException {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if(!line.isEmpty() && !line.startsWith("#")){
					String t = line.trim().replaceAll("\\s+"," ");
					if(t.contains("#")){
						t = t.split("#")[0];
					}
					String[] tokens = t.split(" ");
					for(int i=1;i<tokens.length;i++){
						System.out.println(tokens[i]);
					}					
				}
			}
			br.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		try {
			readFile("/etc/hosts");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
