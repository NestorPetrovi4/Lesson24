import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.TreeMap;

public class Basket {
    private String[] food;
    private int[] price;
    private int[] baskets;
    private int sumFood;
    private ClientLog clientLog = new ClientLog();

    public Basket(int[] price, String[] food) {
        this.food = food;
        this.price = price;
        baskets = new int[food.length];
    }

    public void addToCart(int productNum, int amount, TreeMap<String, String> settings) {
        baskets[productNum] += amount;
        sumFood += amount * price[productNum];
        boolean saveFile = settings.get("save_enabled").equals("true");
        String nameFile = settings.get("save_fileName");
        if (nameFile.indexOf(".") == -1) {
            nameFile = nameFile + "." + settings.get("save_format");
        } else {
            nameFile = nameFile.substring(0, nameFile.indexOf(".")) + "." + settings.get("save_format");
        }
        if (saveFile) {
            File basketTxt = new File(nameFile);
            saveTxt(basketTxt, settings);
        }
        clientLog.log(productNum, amount);
    }

    public int[] getPrice() {
        return price;
    }

    public String[] getFood() {
        return food;
    }

    public int[] getBaskets() {
        return baskets;
    }

    public int getSumFood() {
        return sumFood;
    }

    public void printCart(StringBuilder listFood, TreeMap<String, String> settings) {
        if (sumFood == 0) {
            System.out.println("Ваша корзина пуста");
        } else {
            System.out.println("Ваш заказ: \n");
            listFood.setLength(0);
            for (int i = 0; i < baskets.length; i++) {
                if (!(baskets[i] == 0)) {
                    listFood.append(food[i] + " " + baskets[i] + " шт. * " + price[i] + " руб = " + (baskets[i] * price[i]) + " руб. \n");
                }
            }
            listFood.append("Итоговая сумма покупки = " + sumFood + " руб");
            System.out.println(listFood.toString());
        }
        boolean saveFile = settings.get("log_enabled").equals("true");
        String nameFile = settings.get("log_fileName");
        if (saveFile) {
            clientLog.exportAsCSV(new File(nameFile));
        }
    }

    public void setBaskets(int[] baskets) {
        this.baskets = baskets;
        sumFood = 0;
        for (int i = 0; i < baskets.length; i++) {
            sumFood = sumFood + (baskets[i] * price[i]);
        }
    }

    public void saveTxt(File textFile, TreeMap<String, String> settings) {
        String contentString = "";
        if (settings.get("save_format").equals("json")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            contentString = gson.toJson(this);
        } else {
            contentString = getStringToArray();
        }
        try (FileOutputStream fos = new FileOutputStream(textFile)) {
            // перевод строки в массив байтов
            byte[] bytes = contentString.getBytes();
            // запись байтов в файл
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String getStringToArray() {
        String bask = "";
        String priceStr = "";
        String foodStr = "";
        for (int i = 0; i < baskets.length; i++) {
            bask = bask + baskets[i] + ((i == baskets.length - 1) ? "" : " ");
            priceStr = priceStr + price[i] + ((i == baskets.length - 1) ? "" : " ");
            foodStr = foodStr + food[i] + ((i == baskets.length - 1) ? "" : " ");
        }
        return bask + "\n" + priceStr + "\n" + foodStr;
    }

    public static Basket loadFromTxtFile(File textFile, TreeMap<String, String> settings) {
        if (settings.get("load_format").equals("json")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String textJson = "";
            try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
                textJson = reader.readLine();
                reader.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            Basket basket = gson.fromJson(textJson, Basket.class);
            return basket;
        } else {
            String[] baskArr;
            String[] priceArr;
            String[] foodArr;
            try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
                baskArr = reader.readLine().split(" ");
                priceArr = reader.readLine().split(" ");
                foodArr = reader.readLine().split(" ");
            } catch (IOException ex) {
                baskArr = new String[0];
                priceArr = new String[0];
                foodArr = new String[0];
                System.out.println(ex.getMessage());
            }
            int[] price = new int[foodArr.length];
            int[] bask = new int[foodArr.length];
            for (int i = 0; i < foodArr.length; i++) {
                price[i] = Integer.parseInt(priceArr[i]);
                bask[i] = Integer.parseInt(baskArr[i]);
            }
            Basket basket = new Basket(price, foodArr);
            basket.setBaskets(bask);
            return basket;
        }
    }
}
