package ru.yandex.practicum;

import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.pages.HomePage;
import ru.yandex.practicum.pages.OrderPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(Parameterized.class)
public class FullScenarioTest {
    private final String _scopeClassPrefix;
    private final String _buttonClassPrefix;
    private final HashMap<String, Object> _orderFormParams;
    private static WebDriver _driver;

    @BeforeClass
    public static void acceptCookie() throws Exception {
        _driver = driverRule.getDriver();
        HomePage homepage = new HomePage(_driver);  // Создаем объект страницы
        homepage.open();
        homepage.acceptCookies();
    }

    @ClassRule
    public static DriverRule driverRule = new DriverRule();

    public FullScenarioTest(String scopeClassPrefix, String buttonClassPrefix, HashMap<String, Object> orderFormParams) {
        _scopeClassPrefix = scopeClassPrefix;
        _buttonClassPrefix = buttonClassPrefix;
        _orderFormParams = orderFormParams;
    }

    @Parameterized.Parameters
    public static Object[] getSumData() {
        return new Object[][]{
                {"Header_Nav", "Button_Button", new HashMap<String, Object>() {
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
                {"Home_FinishButton", "Button_Button", new HashMap<String, Object>() {
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

    @Test
    public void doTest() throws Exception {
        _driver = driverRule.getDriver();
        doGoOrderButtonTest(_scopeClassPrefix, _buttonClassPrefix, _orderFormParams);
    }

    public void doGoOrderButtonTest(String scopeClassPrefix, String buttonClassPrefix, HashMap<String, Object> orderFormParams) throws Exception {
        HomePage homepage = new HomePage(_driver);  // Создаем объект страницы
        homepage.open();
        Thread.sleep(1000);

        // Тестируем кнопку перехода на страницу формы заказа
        String strQuery = String.format("//div[starts-with(@class,'%s')]//button[starts-with(@class,'%s')]", scopeClassPrefix, buttonClassPrefix);
        WebElement elemButton = _driver.findElement(By.xpath(strQuery));
        assertNotNull("Элемент кнопки не найден по локатору: " + strQuery, elemButton);

        // Нажимаем кнопку
        ((JavascriptExecutor) _driver).executeScript("arguments[0].scrollIntoView();", elemButton);
        new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOf(elemButton));
        elemButton.click();

        OrderPage orderPage = new OrderPage(_driver);  // Создаем объект страницы
        // Ждём открытия страницы оформления заказа
        boolean bOrderPageOpened = orderPage.isOpenedEnsure();
        assertTrue("Страница оформления заказа не открылась.", bOrderPageOpened);

        // Тестируем форму заказа
        doOrderFormTest(orderFormParams);
    }

    public void doOrderFormTest(HashMap<String, Object> params) throws Exception {
        OrderPage orderPage = new OrderPage(_driver);  // Создаем объект страницы
        orderPage.open();

        // Тестируем форму заказа
        // Заполняем поля формы с текстовым вводом
        fillFormField((Object[]) params.get("firstname"));
        fillFormField((Object[]) params.get("lastname"));
        fillFormField((Object[]) params.get("address"));
        fillFormField((Object[]) params.get("phone"));

        // Тестируем выпадающий список станций метро
        var aStationParams = (Object[]) params.get("station");
        clickListItem(
                String.format("//div[starts-with(@class, 'Order_Form')]/div[%d]", (int) aStationParams[0]),
                "//ul[contains(@class, 'select-search__options')]",
                String.format(".//button[contains(.,'%s')]", aStationParams[1])
        );

        // Нажимаем кнопку "Далее" (к форме "Про аренду")
        WebElement elemNextButton = _driver.findElement(By.xpath("//div[starts-with(@class, 'Order_NextButton')]/button"));
        elemNextButton.click();

        // Проверяем факт перехода к форме
        boolean isRentFormOpened = checkRentFormOpened();
        assertTrue("Не удалось перейти к форме \"Про аренду\"!", isRentFormOpened);

        // Тестируем форму "Про аренду"
        // Заполняем поля формы с текстовым вводом
        fillFormField((Object[]) params.get("deliver"));
        fillFormField((Object[]) params.get("comment"));

        // Заполняем поле формы с выпадающим списком "срок аренды"
        var periodParams = (Object[]) params.get("period");
        clickListItem(
                String.format("//div[starts-with(@class, 'Order_Form')]/div[%d]/div[starts-with(@class, 'Dropdown-control')]//span[contains(@class, 'Dropdown-arrow')]", (int) periodParams[0]),
                "//div[starts-with(@class, 'Order_Form')]//div[starts-with(@class, 'Dropdown-menu')]",
                String.format(".//div[contains(.,'%s')]", periodParams[1])
        );

        // Заполняем поле формы "Цвет самоката"
        var colorParams = (Object[]) params.get("color");
        String checkboxQuery = String.format("//div[starts-with(@class, 'Order_Form')]//div[starts-with(@class, 'Order_Checkboxes')]//input[@id='%s']", colorParams[1]);
        WebElement elemCheckbox = _driver.findElement(By.xpath(checkboxQuery));
        elemCheckbox.click();

        // Находим кнопку "Заказать"
        WebElement createOrder = _driver.findElement(By.xpath(".//div[starts-with(@class, 'Order_Buttons')]//button[contains(.,'Заказать')]"));
        assertNotNull("Кнопка 'Заказать' не найдена!", createOrder);
        createOrder.click();

        // Ожидаем появления кнопки "Да" в окне принятия заказа
        WebElement acceptOrder = new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//div[starts-with(@class, 'Order_Modal')]/div[starts-with(@class, 'Order_Buttons')]//button[contains(.,'Да')]")));
        assertNotNull("Кнопка 'Да' в окне принятия заказа не найдена!", acceptOrder);
        acceptOrder.click();

        // Ожидаем появления кнопки "Посмотреть статус" в окне статуса оформления заказа
        WebElement checkOrderStatus = new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//div[starts-with(@class, 'Order_Modal')]/div[starts-with(@class, 'Order_NextButton')]//button[contains(.,'Посмотреть статус')]")));
        assertNotNull("Кнопка 'Посмотреть статус' не найдена!", checkOrderStatus);
        checkOrderStatus.click();

        // Ожидаем появления формы с параметрами оформленного заказа
        WebElement orderInfo = new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//div[starts-with(@class, 'Track_OrderColumns')]/div[starts-with(@class, 'Track_OrderInfo')]")));
        assertNotNull("Форма с параметрами оформленного заказа не найдена!", orderInfo);
    }

    // Internals
// Заполняем поля формы с текстовым вводом
    private void fillFormField(Object[] fieldTestParams) {
        int elemIndex = (int) fieldTestParams[0];
        String elemValue = (String) fieldTestParams[1];
        // получаем соответствующий input элемент и вводим туда текст
        String strQuery = String.format("//div[starts-with(@class, 'Order_Form')]/div[%d]//input", elemIndex);
        WebElement targetField = _driver.findElement(By.xpath(strQuery));
        assertNotNull("Поле для ввода не найдено по локатору: " + strQuery, targetField);
        targetField.sendKeys(elemValue);
    }

    // Выбор элемента из выпадающего списка
    private void clickListItem(String fieldQuery, String waitQuery, String itemQuery) throws InterruptedException {
        WebElement elemField = _driver.findElement(By.xpath(fieldQuery));
        assertNotNull("Поле для выбора элемента не найдено по локатору: " + fieldQuery, elemField);
        elemField.click();

        WebDriverWait waitOfStationList = new WebDriverWait(_driver, java.time.Duration.ofSeconds(3));
        WebElement elemList = waitOfStationList.until(ExpectedConditions.presenceOfElementLocated(By.xpath(waitQuery)));
        assertNotNull("Выпадающий список не найден по локатору: " + waitQuery, elemList);

        Thread.sleep(1000); // для наглядности

        WebElement elemItem = elemList.findElement(By.xpath(itemQuery));
        assertNotNull("Элемент списка не найден по локатору: " + itemQuery, elemItem);
        elemItem.click();
    }

    // Проверка открытия формы аренды
    private boolean checkRentFormOpened() {
        WebDriverWait wait = new WebDriverWait(_driver, java.time.Duration.ofSeconds(2));
        By query = By.xpath("//div[starts-with(@class,'Order_Form')]//div[starts-with(@class,'Order_MixedDatePicker')]");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(query));
            return true;
        } catch (TimeoutException errTimeout) {
            return false;
        }
    }
}
