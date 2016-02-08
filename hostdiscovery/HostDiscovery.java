import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
								if (l[1].contains(" ")) {
									String[] hs = l[1].split(" ");
									for (String h : hs) {
										String hnm = resolveHostName(h);
										if (!hnm.isEmpty())
											System.out.println(hnm);
									}
								} else {
									String h = l[1];
									String hnm = resolveHostName(h);
									if (!hnm.isEmpty())
										System.out.println(hnm);
								}

							} else if (l[0].equals("HostName")) {
								if (l[1].contains(" ")) {
									String[] hs = l[1].split(" ");
									for (String h : hs) {
										String hnm = resolveHostName(h);
										if (!hnm.isEmpty())
											System.out.println(hnm);
									}
								} else {
									String h = l[1];
									String hnm = resolveHostName(h);
									if (!hnm.isEmpty())
										System.out.println(hnm);
								}
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
										String hnm = resolveHostName(h);
										if (!hnm.isEmpty())
											System.out.println(hnm);
									}
								}
							} else {
								if (fStr.contains("!")) {
									fStr = fStr.replace("!", "");
								}
								String hnm = resolveHostName(fStr);
								if (!hnm.isEmpty())
									System.out.println(hnm);
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
										String hnm = resolveHostName(h);
										if (!hnm.isEmpty())
											System.out.println(hnm);
									}
								}
							} else {
								String hnm = resolveHostName(fStr);
								if (!hnm.isEmpty())
									System.out.println(hnm);
							}
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

	public static String resolveHostName(String h) {
		if (h.startsWith("*")) {
			h = h.replace("*", "");
			if (h.startsWith(".")) {
				h = h.replaceFirst(".", "");
			}
		} else if (h.startsWith("[")) {
			h = h.replace("[", "").replace("]", "");
		}
		if (h.contains(":")) {
			h = h.split(":")[0];
		}

		char[] harr = h.toCharArray();
		for (char c : harr) {
			if (!Character.isDigit(c) && c != '.')
				return h;
		}

		return "";
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
											String hnm = resolveHostName(h);
											if (!hnm.isEmpty()) {
												System.out.println(hnm);
											}
										}
									}
								} else {
									String hnm = resolveHostName(listHostnames);
									if (!hnm.isEmpty()) {
										System.out.println(hnm);
									}
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

	public static boolean readFileUsers(String fileName) throws FileNotFoundException {
		BufferedReader br;
		try {
			File f = new File(fileName);
			if (f.exists() && f.isFile()) {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				ArrayList<String> users = new ArrayList<String>();
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty() && !line.startsWith("#")) {
						String[] fields = line.split(":", 2);
						if (fields != null && fields.length > 1) {
							users.add(fields[0]);
						}
					}
				}

				if (users != null && !users.isEmpty()) {
					String currentUserHome = System.getProperty("user.home");
					String currentUserName = System.getProperty("user.name");
					for (String u : users) {
						String otherUserHD = currentUserHome.replaceFirst(currentUserName, u);
						readFileSshConfig(otherUserHD + "/.ssh/config");
						readFileAuthKeys(otherUserHD + "/.ssh/authorized_keys");
						readFileKwnHosts(otherUserHD + "/.ssh/known_hosts");
					}
				}
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

			readFileKwnHosts(System.getProperty("user.home") + "/.ssh/known_hosts");
			readFileKwnHosts("/etc/ssh/ssh_known_hosts");
			readFileKwnHosts("/Users/kvivekanandan/Desktop/ASU/CSE_545_Software_Security/ssh_known_hosts");
			readFileKwnHosts("/Users/kvivekanandan/Desktop/ASU/CSE_545_Software_Security/known_hosts_doupe");

			readFileUsers("/etc/passwd");
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
