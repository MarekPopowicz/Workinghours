import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Klasa której zadaniem jest odczytanie z pliku tekstowego danych do wypełnienia raportu.
 *
 * @author Marek Popowicz, Grudzień 2018.
 */

public class DataWriterReader {


    /**
     *
     * @param fileName plik tekstowy z zadaniami.
     * @return tablica łańcuchów
     */

    public String[] readDataFromFile(String fileName) {
        File file = new File((new File(fileName).getAbsolutePath()));

        List<String> dataList = new ArrayList<>();
        String[] data = null;
        if(fileName == "") return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.startsWith("#") || sCurrentLine.isEmpty()) continue;
                if (!sCurrentLine.startsWith("I-WR-") || sCurrentLine.length()>23) continue;
                dataList.add(sCurrentLine.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Nie można odczytać pliku: \n" + fileName, "Informacja", JOptionPane.INFORMATION_MESSAGE);
        }

        if (!dataList.isEmpty()) {
            data = new String[dataList.size()];
            data = dataList.toArray(data);
        }
        return data;
    }

    /**
     * Zpisuje kartę czasu pracy do pliku.
     * @param data dane do zapisu
     * @param filename nazwa pliku wynikowego
     * @return
     */

    public boolean writeDataToFile(String[] data, String filename) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filename, "UTF-8");
            for (int i = 0; i < data.length; i++) {
                writer.println(data[i]);
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(DataWriterReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    /**
     * Odczytuje dni wolne zdefiniowane w pliku tekstowym w archiwum JAR.
     * @param filename nazwa pliku
     * @return tablica łańcuchów
     * @throws IOException
     */

    public String[] readFromJARFile(String filename) throws IOException {
        InputStream is = getClass().getResourceAsStream(filename);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        List<String> dataList = new ArrayList<>();
        String[] data = null;

        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            dataList.add(line.trim());
        }
        br.close();
        isr.close();
        is.close();

        if (!dataList.isEmpty()) {
            data = new String[dataList.size()];
            data = dataList.toArray(data);
        }
        return data;
    }


}
