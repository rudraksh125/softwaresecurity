import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static boolean readFileSshConfig(String fileName) throws FileNotFoundException {
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

	public static boolean readFileAuthKeys(String fileName) throws FileNotFoundException {
		BufferedReader br;
		try {
			File f = new File(fileName);
			if (f.exists() && f.isFile()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty() && !line.trim().startsWith("#")) {
						Pattern frm = Pattern.compile("from=\".*\"");
						Matcher m = frm.matcher(line);
						if (m.find()) {
							String fStr = m.group(0);
							// System.out.println(fStr);
							fStr = fStr.replace("from=\"", "").replace("\"", "");
							if (fStr.contains(",")) {
								String[] hs = fStr.split(",");
								if (hs != null && hs.length > 0) {
									for (String h : hs) {
										if (h.contains("!")) {
											h = h.replace("!", "");
										}
										if (h.startsWith("*")) {
											h = h.replace("*", "");
											if (h.startsWith(".")) {
												h = h.replaceFirst(".", "");
											}
										}
										System.out.println(h);
									}
								}
							}
						}

						frm = Pattern.compile("permitopen=\".*\"");
						m = frm.matcher(line);
						while (m.find()) {
							String fStr = m.group(0);
							// System.out.println(fStr);
							fStr = fStr.replaceAll("permitopen=\"", "").replace("\"", "");
							if (fStr.contains(",")) {
								String[] hs = fStr.split(",");
								if (hs != null && hs.length > 0) {
									for (String h : hs) {
										String hstnm = h.substring(0, h.indexOf(':'));
										System.out.println(hstnm);
									}
								}
							}
							// else {
							// System.out.println(fStr);
							// }
						}

						// options, bits, exponent, modulus, comment
						// options, keytype, base64-encoded key, comment

						String comment = line.substring(line.trim().lastIndexOf(' ') + 1);
						if (comment.matches(".*@.*")) {
							String host = comment.substring(comment.indexOf('@') + 1);
							System.out.println(host);
						} else if (comment.contains(".") && !comment.contains("/")) {
							System.out.println(comment);
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

	// markers (optional), hostnames, bits, exponent, modulus, comment
	public static boolean readFileKwnHosts(String fileName) throws FileNotFoundException {
		BufferedReader br;
		try {
			File f = new File(fileName);
			if (f.exists() && f.isFile()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty() && !line.trim().startsWith("#")) {

						String markerLine = line.trim();
						if (markerLine.startsWith("@cert-authority ")) {
							markerLine = line.replaceFirst("@cert-authority ", "");
						}
						if (markerLine.startsWith("@revoked ")) {
							markerLine = line.replaceFirst("@revoked ", "");
						}
						if (!markerLine.startsWith("|")) {
							String[] listhn = markerLine.split(" ", 2);
							if (listhn != null && listhn.length > 0) {
								String listHostnames = listhn[0];
								if (listHostnames.contains(",")) {
									String[] hn = listHostnames.split(",");
									if (hn != null && hn.length > 0) {
										for (String h : hn) {
											if (h.startsWith("*")) {
												h = h.replace("*", "");
												if (h.startsWith(".")) {
													h = h.replaceFirst(".", "");
												}
											}
											System.out.println(h);
										}
									}
								} else {
									if (listHostnames.startsWith("*")) {
										listHostnames = listHostnames.replace("*", "");
										if (listHostnames.startsWith(".")) {
											listHostnames = listHostnames.replaceFirst(".", "");
										}
									}
									if (!listHostnames.isEmpty())
										System.out.println(listHostnames);
								}

							}
						}

						String comment = markerLine.substring(markerLine.lastIndexOf(' ') + 1);
						if (comment.matches(".*@.*")) {
							String host = comment.substring(comment.indexOf('@') + 1);
							System.out.println(host);
						} else if (comment.contains(".") && !comment.contains("/")) {
							System.out.println(comment);
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
			readFileAuthKeys(System.getProperty("user.home") + "/.ssh/authorized_keys");
			readFileAuthKeys("/Users/kvivekanandan/Desktop/ASU/CSE_545_Software_Security/c_authorized_keys");
			readFileKwnHosts(System.getProperty("user.home") +"/.ssh/known_hosts");
			readFileKwnHosts("/etc/ssh/ssh_known_hosts");
			readFileKwnHosts("/Users/kvivekanandan/Desktop/ASU/CSE_545_Software_Security/ssh_known_hosts");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
