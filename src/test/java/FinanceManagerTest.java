import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class FinanceManagerTest {
    public final String food = "еда";
    public final String other = "другое";

    public Object[] valuesBuy = new Object[]{food, "2022.08.02", 200L};
    public FinanceManager financeManager = new FinanceManager();

    @Test
    public void getMaxOfMapTest() {
        Map<String, Long> subMap = Map.of(food, 200L, other, 300L);
        Long expected = 300L;
        Long actual = financeManager.getMaxOfMap(subMap).get(other);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void maxCategoryOfPeriodTest() {
        Map<String, Map<String, Long>> spendCategoryOfPeriod = Map.of(
                food, Map.of("2022", 200L),
                other, Map.of("2022", 300L)
        );
        String period = "2022";
        Long expected = 300L;
        Long actual = financeManager.maxCategoryOfPeriod(spendCategoryOfPeriod, period).get(other);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void addBuyTest() {
        Long expectedSum = 200L;

        financeManager.addBuy(valuesBuy);
        Long actualSum = financeManager.getSpendEntirePeriod().get(food);

        Assertions.assertEquals(expectedSum, actualSum);
    }

    @Test
    public void maxCategoryTest() {
        int expectedSize = 4;

        financeManager.addBuy(valuesBuy);
        int actualSize = financeManager.maxCategory().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }
}