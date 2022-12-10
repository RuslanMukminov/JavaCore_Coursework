import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Main {
    protected static Map<String, Map<String, Long>> financeMap;
    public static Object[] buyFromJsom(String buy,File tsvFile) throws IOException {
        JSONParser parser = new JSONParser();
        String title = new String();
        String date = new String();
        Long sum = 0L;

        try {
            Object obj = parser.parse(buy);
            JSONObject jsonObject = (JSONObject) obj;
            title = (String) jsonObject.get("title");
            date = (String) jsonObject.get("date");
            sum = (Long) jsonObject.get("sum");
        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        String category = titleToCategory(title, tsvFile);
        return new Object[]{category, date, sum};
    }
    public static String titleToCategory(String title, File tsvFile) throws IOException {
        String category = "другое";

        try (BufferedReader br = new BufferedReader(new FileReader(tsvFile))) {
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                String[] s = nextLine.split("\t");
                if (title.equals(s[0])) {
                    category = s[1];
                    break;
                }
            }
        }
        return category;
    }
    public static StringBuilder financeMapToJsonString() {
        StringBuilder jsonString = new StringBuilder();
        String category = "category";
        String sum = "sum";

        jsonString.append("{\n");
        for (Map.Entry<String, Map<String, Long>> kv : financeMap.entrySet()) {
            for (String key : kv.getValue().keySet()) {
                jsonString.append('\"');
                jsonString.append(kv.getKey()).append("\":{\n\"");
                jsonString.append(category).append("\":\"");
                jsonString.append(key).append("\",\n\"");
                jsonString.append(sum).append("\":");
                jsonString.append(kv.getValue().get(key));
                jsonString.append('\n').append("},\n");
            }
        }
        jsonString.append('}');
        return jsonString;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String tsvFileName = "categories.tsv";
        String binFileName = "data.bin";
        File tsvFile = new File(tsvFileName);
        File binFile = new File(binFileName);

        FinanceManager financeManager;

        if (binFile.exists()) {
            financeManager = FinanceManager.loadFromBin(binFile);
        } else {
            financeManager = new FinanceManager();
        }

        try (ServerSocket serverSocket = new ServerSocket(8989)) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    String buy = in.readLine();
                    Object[] valuesBuy = buyFromJsom(buy, tsvFile);
                    financeManager.addBuy(valuesBuy);
                    financeMap = financeManager.maxCategory();
                    out.println(financeMapToJsonString());
                    financeManager.saveBin(binFile);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }
}
