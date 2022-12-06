import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class FinanceManagerTest {
    List<String> values = Arrays.asList("булка", "колбаса");
    public Map<String, List<String>> categories = Map.of(
            "еда", values
    );

    String buy = "{\"title\": \"булка\", \"date\": \"2022.02.08\", \"sum\": 200}";
    public FinanceManager financeManager = new FinanceManager(categories);

    @Test
    public void addBuyTest() {
        Long expectedSum = 200L;

        financeManager.addBuy(buy);
        Long actualSum = financeManager.getSpendEntirePeriod().get("еда");

        Assertions.assertEquals(expectedSum, actualSum);
    }

    @Test
    public void maxCategoryTest() {
        String expected = "{\"maxCategory\":[{\"sum\":200,\"category\":\"еда\"}]}";

        financeManager.addBuy(buy);
        String actual = financeManager.maxCategory();

        Assertions.assertEquals(expected, actual);
    }
}