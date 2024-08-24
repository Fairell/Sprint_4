package arseny.study;

import arseny.study.pages.HomePage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;


@RunWith(Parameterized.class)
public class FullScenarioTest  {
    private final String _buttonKey;
    private final String _scopeClassPrefix;
    private final String _buttonClassPrefix;
    private final HashMap<String, Object> _orderFormParams;


    private static boolean _isStarted = false;

    private static HomePage _homePage = null;

    @ClassRule
    public static DriverRule driverRule = new DriverRule();

    @BeforeClass
    public static void prepareForTest()  {
        _homePage = new HomePage(driverRule.getDriver());
    }

    @AfterClass
    public static void finish() {
        _isStarted = false;
        //
        System.out.println("GoOrderTest - ФИНИШ\n");
    }

    @Parameterized.Parameters
    public static Object[] getSumData() {
        return new Object[][]{
                {"Верхняя кнопка", "Header_Nav", "Button_Button", new HashMap<String, Object>() {
                    {
                        put("firstname", new Object[]{1, "Вася"});
                        put("lastname", new Object[]{2, "Пупкин"});
                        put("address", new Object[]{3, "г. Москва, ул. им. Гаврилы, д. 11"});
                        put("station", new Object[]{4, "Профсоюзная"});
                        put("phone", new Object[]{5, "+79101235678"});
                        put("deliver", new Object[]{1, "16.08.2024"});
                        put("period", new Object[]{2, "сутки"});
                        put("color", new Object[]{3, "grey"});
                        put("comment", new Object[]{4, "Привет курьеру!"});
                    }
                }
                },
                {"Нижняя кнопка", "Home_FinishButton", "Button_Button", new HashMap<String, Object>() {
                    {
                        put("firstname", new Object[]{1, "Петя"});
                        put("lastname", new Object[]{2, "Кукушкин"});
                        put("address", new Object[]{3, "г. Москва, ул. Потерявшихся, д. 013"});
                        put("station", new Object[]{4, "Митино"});
                        put("phone", new Object[]{5, "+71116667788"});
                        put("deliver", new Object[]{1, "22.09.2024"});
                        put("period", new Object[]{2, "четверо суток"});
                        put("color", new Object[]{3, "black"});
                        put("comment", new Object[]{4, ""});
                    }
                }
                }
        };
    }

    //

    public FullScenarioTest(String targetKey, String scopeClassPrefix, String buttonClassPrefix, HashMap<String, Object> orderFormParams) {
        _buttonKey = targetKey;
        _scopeClassPrefix = scopeClassPrefix;
        _buttonClassPrefix = buttonClassPrefix;
        _orderFormParams = orderFormParams;
    }

    @Test
    public void doTest() {
        if (!_isStarted) {
            _isStarted = true;
            System.out.println("\nGoOrderTest - СТАРТ\n");
        }

        try {
            System.out.printf("ТЕСТ - %s\n", _buttonKey);
            _homePage.doGoOrderButtonTest(_scopeClassPrefix, _buttonClassPrefix, _orderFormParams);
            System.out.println();
        } catch (Exception err) {
            System.out.printf("ОШИБКА! Во время выпонения doGoButtonTest: \"%s\"\n\n", err.getMessage());
        }
    }

}
