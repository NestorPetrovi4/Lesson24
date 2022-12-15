import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class ClientLog {
    private ArrayList<String[]> baskets = new ArrayList<>();

    public void log(int productNum, int amount) {
        baskets.add(new String[]{Integer.toString(productNum + 1), Integer.toString(amount)});
    }

    public void exportAsCSV(File txtFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile, true))) {
            if (txtFile.length() == 0 && !baskets.isEmpty()) {
                writer.writeNext(new String[]{"productNum", "amount"}, false);
            }
            writer.writeAll(baskets, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
