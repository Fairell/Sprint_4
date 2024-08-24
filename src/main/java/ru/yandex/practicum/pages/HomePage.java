package ru.yandex.practicum.pages;

import ru.yandex.practicum.EnvConfig;
import static org.junit.Assert.assertEquals;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;

public class HomePage extends PageBase {
    private boolean isCookiesNotAccepted = true;
    //
    // Construction
    //

    public HomePage(WebDriver driver) {
        super(driver);
    }

    //
    // Overrides
    //

    @Override
    public String getPageName() {
        return "Домашняя";
    }

    @Override
    protected String getPageURL() {
        return EnvConfig.BASE_URL;
    }

    @Override
    protected By getCheckOpenedPageQuery() {
        return By.xpath("//div[starts-with(@class,'Home_HomePage')]");
    }

    public void testHomeFAQ_Item(int index, String patternQuestion, String patternAnswer) throws InterruptedException {

        // Выводим в консоль заголовок информационного блока
        System.out.printf("testHomeFAQ_Item. Индекс %d\n", index + 1);

        // Запрос на выборку элемента списка по заданнному индексу
        // Примечание. В XPath индексы отсчитываются с 1 (а не с 0)
        String strQuery = String.format("//div[starts-with(@class,'Home_FAQ')]//div[@class='accordion__item'][%d]", index + 1);

        try {
            WebElement elemTarget = _driver.findElement(By.xpath(strQuery));
            // Элемент найден, начинаем тестирование

            // 1. Скроллируем элемент в видимую область
            ((JavascriptExecutor) _driver).executeScript("arguments[0].scrollIntoView();", elemTarget);

            // 2. Получаем заголовок элемента
            String actualQuestion = elemTarget.findElement(By.xpath("div[@role=\"heading\"]")).getText();

            // Сверяем текст заголовка (вопроса) с шаблоном
            try {
                assertEquals(patternQuestion, actualQuestion);
            } catch (AssertionError err) {
                System.out.println("Ошибка: " + err.getMessage());
            }

            // 3. Кликаем по элементу
            elemTarget.click();

            // 4. Пытаемся получить дочерний элемент содержащий ответ (путём ожидания видимости)
            // подготавливаем объект WebDriverWait и условие ожидания (объект ExpectedCondition)
            WebDriverWait wait = new WebDriverWait(_driver, java.time.Duration.ofSeconds((1)));
            ExpectedCondition<WebElement> condition = ExpectedConditions.visibilityOf(elemTarget.findElement(By.xpath(".//div[@class=\"accordion__panel\"]")));

            // ждём появление элемента в соответствии с условием
            try {
                WebElement resultElement = wait.until(condition);
                String actualAnswer = resultElement.getText();

                // Сверяем текст ответа с шаблоном
                assertEquals("Текст ответа элемента не совпадает с образцом.", patternAnswer, actualAnswer);
            } catch (TimeoutException errTimeout) {
                System.out.println("Истекло время ожидания элемента \"ответа\".");
            }

            // Завершаем блок отчёта
            System.out.println("Элемент обработан.\n");
        } catch (NoSuchElementException errNoSuchElement) {
            System.out.printf("Ошибка! Элемент не найден!. Локатор: \"%s\" ", strQuery);
        }

        // на пол секунды притормаживаем текущий поток (для наглядности)
        Thread.sleep(500);
    }

    public void doGoOrderButtonTest(String scopeClassPrefix, String buttonClassPrefix, HashMap<String, Object> orderFormParams) throws Exception {
        //Открываем страницу
        open();
        if(isCookiesNotAccepted) {
            acceptCookies();
            isCookiesNotAccepted = false;
        }

        /* -------------------------------------------------- */
        /* Тестируем кнопку перехода на страницу формы заказа */
        OrderPage orderPage;

        String strQuery = String.format("//div[starts-with(@class,'%s')]//button[starts-with(@class,'%s')]", scopeClassPrefix, buttonClassPrefix);
        try {
            WebElement elemButton = _driver.findElement(By.xpath(strQuery));

            // нажимаем кнопку
            ((JavascriptExecutor) _driver).executeScript("arguments[0].scrollIntoView();", elemButton);
            elemButton.click();

            // ждём открытия страницы оформления заказа
            orderPage = new OrderPage(_driver);
            boolean bOrderPageOpened = orderPage.isOpenedEnsure();
            if (!bOrderPageOpened) {
                throw new Exception(String.format("Не удалось открыть страницу \"%s\".", orderPage.getPageName()));
            }

            // переход на страницу осуществлён, можно переходить к тестированию формы заказа
            System.out.printf("Выполнен переход на страницу %s!\n", orderPage.getPageName());
        } catch (NoSuchElementException errNoSuchElement) {
            System.out.printf("Ошибка! Элемент (button) не найден!. Локатор: \"%s\"\n", strQuery);
            return;
        } catch (TimeoutException errTimeout) {
            System.out.println("Страница Оформления Заказа - НЕ открылась!");
            return;
        }

        /* Тестируем форму заказа */

        orderPage.doOrderFormTest(orderFormParams);
    }
}
