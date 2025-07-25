package jogotermo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ValidarPalavras {
    private static Set<String> palavrasProibidas = new HashSet<>();

    static {
        carregarPalavrasProibidas();
    }

    private static void carregarPalavrasProibidas() {
        try {
            File arquivo = new File("palavras.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(arquivo);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("palavra");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    palavrasProibidas.add(eElement.getTextContent().toUpperCase());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar palavras proibidas: " + e.getMessage());
        }
    }

    public static boolean palavraValida(String palavra) {
        return !palavrasProibidas.contains(palavra.toUpperCase());
    }
}
