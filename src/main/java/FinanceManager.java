import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FinanceManager {
    private final String OTHER = "другое";
    private Map<String, List<String>> categories = new HashMap<>();
    private Map<String, Long> spendEntirePeriod = new HashMap<>();

    public FinanceManager(File tsvFile) throws IOException {
        readTsv(tsvFile);
        initialSpendEntirePeriod();
    }

    public FinanceManager(Map<String, List<String>> categories) {
        this.categories = categories;
        initialSpendEntirePeriod();
    }

    public Map<String, Long> getSpendEntirePeriod() {
        return spendEntirePeriod;
    }

    public void readTsv(File tsvFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(tsvFile))) {
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                List<String> list = new ArrayList<>();
                String[] s = nextLine.split("\t");

                if (categories.containsKey(s[1])) {
                    list = categories.get(s[1]);
                }
                list.add(s[0]);
                categories.put(s[1], list);
            }
        }
    }

    public void initialSpendEntirePeriod() {
        for (String k : categories.keySet()) {
            spendEntirePeriod.put(k, 0L);
        }
        spendEntirePeriod.put(OTHER, 0L);
    }

    public void addBuy(String buy) {
        JSONParser parser = new JSONParser();
        String category = new String();

        try {
            Object obj = parser.parse(buy);
            JSONObject jsonObject = (JSONObject) obj;
            String title = (String) jsonObject.get("title");
            Long sum = (Long) jsonObject.get("sum");

            for (String k : categories.keySet()) {
                if (categories.get(k).contains(title)) {
                    category = k;
                    break;
                }
            }
            if (category.isEmpty()) {
                category = OTHER;
            }
            sum += spendEntirePeriod.get(category);
            spendEntirePeriod.put(category, sum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String maxCategory() {
        JSONObject obj = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Optional<Long> maxValue = spendEntirePeriod.values().stream().max(Long::compareTo);

        if (maxValue.isPresent()) {
            Long maxSum = maxValue.get();
            for (Map.Entry<String, Long> kv : spendEntirePeriod.entrySet()) {
                if (Objects.equals(kv.getValue(), maxSum)) {

                    JSONObject subObj = new JSONObject();
                    subObj.put("category", kv.getKey());
                    subObj.put("sum", kv.getValue());

                    jsonArray.add(subObj);
                }
            }
        }
        obj.put("maxCategory", jsonArray);

        return obj.toJSONString();
    }

}
