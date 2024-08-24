package arseny.study.pages;

import arseny.study.EnvConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;


public class OrderPage extends PageBase {

    //
    // Construction
    //

    public OrderPage(WebDriver driver) {
        super(driver);
    }

    //
    // Overrides
    //

    @Override
    public String getPageName() {
        return "Оформление Заказа";
    }

    @Override
    protected String getPageURL() {
        return EnvConfig.ORDER_PAGE_URL;
    }

    @Override
    protected By getCheckOpenedPageQuery() {
        return By.xpath("//div[starts-with(@class,'Order_Content')]");
    }

    //
    // Test implementations
    //

    public void doOrderFormTest(HashMap<String, Object> params) throws Exception {
        open();

        //
        // Тестируем форму заказа
        //

        try {
            //
            // Заполняем поля формы с текстовым вводом
            //

            fillFormField((Object[])params.get("firstname"));
            fillFormField((Object[])params.get("lastname"));
            fillFormField((Object[])params.get("address"));
            fillFormField((Object[])params.get("phone"));

            //
            // Тестируем выпадающий список станций метро
            //

            var aStationParams = (Object[]) params.get("station");
            clickListItem(
                    String.format("//div[starts-with(@class, 'Order_Form')]/div[%d]", (int) aStationParams[0]),
                    "//ul[contains(@class, 'select-search__options')]",
                    String.format(".//button[contains(.,'%s')]", aStationParams[1])
            );

            //
            System.out.println("Поля формы заказа заполнены.");
        } catch (Exception errFillForm) {
            String message = String.format("Ошибка при заполнении полей формы заказа. Сообщение: %s", errFillForm.getMessage());
            throw new Exception(message);
        }

        Thread.sleep(1000); // для наглядности

        //
        // Нажимаем кнопку "Далее" (к форме "Про аренду")
        //

        WebElement elemNextButton = _driver.findElement(By.xpath("//div[starts-with(@class, 'Order_NextButton')]/button"));
        elemNextButton.click();
        System.out.println("Кнопка \"Далее\" нажата.");

        // Проверяем факт перехода к форме
        boolean isRenFormOpened = checkRentFormOpened();
        if (!isRenFormOpened) {
            throw new Exception("Не удалось перейти к форме \"Про аренду\"!");
        }

        System.out.println("Выполнен переход к форме \"Про аренду\"!");

        //
        // Тестируем форму "Про аренду"
        //

        try {
            //
            // Заполняем поля формы с текстовым вводом
            //

            fillFormField((Object[]) params.get("deliver"));
            fillFormField((Object[]) params.get("comment"));

            //
            // Заполняем поле формы с выпадающем списком "срок аренды"
            //

            var periodParams = (Object[]) params.get("period");
            clickListItem(
                    String.format("//div[starts-with(@class, 'Order_Form')]/div[%d]/div[starts-with(@class, 'Dropdown-control')]//span[contains(@class, 'Dropdown-arrow')]", (int)periodParams[0]),
                    "//div[starts-with(@class, 'Order_Form')]//div[starts-with(@class, 'Dropdown-menu')]",
                    String.format(".//div[contains(.,'%s')]", periodParams[1])
                    );

            //
            // Заполняем поле формы "Цвет самоката"
            //

            var colorParams = (Object[]) params.get("color");
            String checkboxQuery = String.format("//div[starts-with(@class, 'Order_Form')]//div[starts-with(@class, 'Order_Checkboxes')]//input[@id='%s']", colorParams[1]);
            WebElement elemCheckbox = _driver.findElement(By.xpath(checkboxQuery));
            elemCheckbox.click();

            Thread.sleep(500); // для наглядности

            //
            System.out.println("Поля формы \"Про аренду\" заполнены.");
        } catch (Exception errFillForm) {
            String message = String.format("Ошибка при заполнении полей формы \"Про аренду\". Сообщение: %s", errFillForm.getMessage());
            throw new Exception(message);
        }

        //Находим кнопку "Заказать"
        WebElement createOrder = _driver.findElement(By.xpath(".//div[starts-with(@class, 'Order_Buttons')]//button[contains(.,'Заказать')]"));
        //Нажимаем кнопку
        createOrder.click();

        //Ожидаем появления кнопки "Да" в окне принятия заказа
        WebElement acceptOrder = _driver.findElement(By.xpath(".//div[starts-with(@class, 'Order_Modal')]/div[starts-with(@class, 'Order_Buttons')]//button[contains(.,'Да')]"));
        new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOf(acceptOrder));
        //Если кнопка отображается, кликаем на неё
        acceptOrder.click();

        //Ожидаем появления кнопки "Посмотреть статус" в окне статуса оформления заказа
        WebElement checkOrderStatus = _driver.findElement(By.xpath(".//div[starts-with(@class, 'Order_Modal')]/div[starts-with(@class, 'Order_NextButton')]//button[contains(.,'Посмотреть статус')]"));
        new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOf(checkOrderStatus));
        //Если кнопка отображается, кликаем на неё
        checkOrderStatus.click();

        //Ожидаем появления формы с параметрами оформленного заказа
        WebElement order = _driver.findElement(By.xpath(".//div[starts-with(@class, 'Track_OrderColumns')]/div[starts-with(@class, 'Track_OrderInfo')]"));
        new WebDriverWait(_driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOf(order));
        Thread.sleep(1000);
        //System.out.println("Элемент списка станций получен!");

    } // doOrderFormTest

    //
    // Internals
    //

    private void fillFormField(Object[] fieldTestParams) {
        int elemIndex = (int) fieldTestParams[0];
        String elemValue = (String) fieldTestParams[1];
        // получаем соответствующий input элемент и вводим туда текст
        String strQuery = String.format("//div[starts-with(@class, 'Order_Form')]/div[%d]//input", elemIndex);
        try {
            WebElement targetField = _driver.findElement(By.xpath(strQuery));
            targetField.sendKeys(elemValue);
        } catch (Exception err) {
            System.out.printf("Тест поля формы заказа закончился неудачей! Текст ошибки: %s\n", err.getMessage());
        }
    }

    private void clickListItem(String fieldQuery, String waitQuery, String itemQuery) throws InterruptedException {
        // находим поле формы и кликаем по нему
        WebElement elemField = _driver.findElement(By.xpath(fieldQuery));
        elemField.click();

        // ждём появления выпадающего списка
        WebDriverWait waitOfStationList = new WebDriverWait(_driver, java.time.Duration.ofSeconds((3)));
        ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(By.xpath(waitQuery));
        WebElement elemList = waitOfStationList.until(condition);

        Thread.sleep(1000); // для наглядности

        // в списке ищём кнопку (тег: button) с заданным текстом (название станции)
        WebElement elemItem = elemList.findElement(By.xpath(itemQuery));
        elemItem.click();
    }


    private boolean checkRentFormOpened() {
        WebDriverWait wait = new WebDriverWait(_driver, java.time.Duration.ofSeconds((2)));
        By query = By.xpath("//div[starts-with(@class,'Order_Form')]//div[starts-with(@class,'Order_MixedDatePicker')]");
        ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(query);
        try {
            wait.until(condition);
        } catch (TimeoutException errTimeout) {
            return false;
        }
        //
        return true;
    }


}
