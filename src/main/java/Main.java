import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    public static TreeMap<String, String> settings = new TreeMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        getSettings();
        boolean loadFile = settings.get("load_enabled").equals("true");
        String nameFile = settings.get("load_fileName");
        if (nameFile.indexOf(".") == -1) {
            nameFile = nameFile + "." + settings.get("load_format");
        } else {
            nameFile = nameFile.substring(0, nameFile.indexOf(".")) + "." + settings.get("load_format");
        }

        File basketTxt = new File(nameFile);
        Basket basket = (loadFile && basketTxt.exists()) ? Basket.loadFromTxtFile(basketTxt, settings) : new Basket(new int[]{59, 150, 243, 30, 580},
                new String[]{"Хлеб", "Масло", "Чай", "Вода", "Колбаса"});

        System.out.println("Список товаров доступных для добавления в корзину:");
        StringBuilder listFood = new StringBuilder();
        String[] food = basket.getFood();
        int[] price = basket.getPrice();
        for (int i = 0; i < food.length; i++) {
            listFood.append((i + 1) + ". " + food[i] + " " + price[i] + " руб/шт \n");
        }
        System.out.println(listFood.toString());
        while (true) {
            System.out.println("Выберите товар путем ввода его номера и количества. Для завершения покупок введите end");
            String inputStr = scanner.nextLine();
            if (inputStr.equals("end")) break;
            else if (inputStr == "") continue;
            String[] parts = inputStr.split(" ");
            if (parts.length < 2) {
                System.out.println("Вы некорректно ввели номер товара и его количество!!");
                continue;
            }
            int numFood = Integer.parseInt(parts[0]) - 1;
            int countFood = Integer.parseInt(parts[1]);
            if (numFood < 0 || numFood > (food.length - 1)) {
                System.out.println("Такого номера товара нет в предложенном перечне");
                continue;
            }
            basket.addToCart(numFood, countFood, settings);
        }
        basket.printCart(listFood, settings);
    }

    public static void getSettings() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("shop.xml"));
            Node root = doc.getDocumentElement();
            readChildNode(root, root.getChildNodes());

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readChildNode(Node parent, NodeList childNode) {
        for (int i = 0; i < childNode.getLength(); i++) {
            Node node = childNode.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType() && !node.getTextContent().equals("\n")) {
                settings.put(parent.getNodeName() + "_" + node.getNodeName(), node.getTextContent());
            }
            readChildNode(node, node.getChildNodes());
        }
    }
}
