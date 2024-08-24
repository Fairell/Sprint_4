package ru.yandex.practicum;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.practicum.pages.HomePage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class HomePageFaqTest {

    private final int _indexItem;
    private final String _patternQuestion;
    private final String _patternAnswer;

    protected static HomePage _homePage;

    public HomePageFaqTest(int indexItem, String patternQuestion, String patternAnswer) {
        _indexItem = indexItem;
        _patternQuestion = patternQuestion;
        _patternAnswer = patternAnswer;
    }

    @BeforeClass
    public static void beforeTest() throws Exception {
        _homePage = new HomePage(driverRule.getDriver());
        _homePage.open();
        _homePage.acceptCookies();
    }

    @ClassRule
    public static DriverRule driverRule = new DriverRule();

    @Parameterized.Parameters
    public static Object[] getFaqData() {
        return new Object[][] {
                { 0, "Сколько это стоит? И как оплатить?", "Сутки — 400 рублей. Оплата курьеру — наличными или картой."},
                { 1, "Хочу сразу несколько самокатов! Так можно?", "Пока что у нас так: один заказ — один самокат. Если хотите покататься с друзьями, можете просто сделать несколько заказов — один за другим."},
                { 2, "Как рассчитывается время аренды?", "Допустим, вы оформляете заказ на 8 мая. Мы привозим самокат 8 мая в течение дня. Отсчёт времени аренды начинается с момента, когда вы оплатите заказ курьеру. Если мы привезли самокат 8 мая в 20:30, суточная аренда закончится 9 мая в 20:30."},
                { 3, "Можно ли заказать самокат прямо на сегодня?", "Только начиная с завтрашнего дня. Но скоро станем расторопнее."},
                { 4, "Можно ли продлить заказ или вернуть самокат раньше?", "Пока что нет! Но если что-то срочное — всегда можно позвонить в поддержку по красивому номеру 1010."},
                { 5, "Вы привозите зарядку вместе с самокатом?", "Самокат приезжает к вам с полной зарядкой. Этого хватает на восемь суток — даже если будете кататься без передышек и во сне. Зарядка не понадобится."},
                { 6, "Можно ли отменить заказ?", "Да, пока самокат не привезли. Штрафа не будет, объяснительной записки тоже не попросим. Все же свои."},
                { 7, "Я живу за МКАДом, привезёте?", "Да, обязательно. Всем самокатов! И Москве, и Московской области."}
        };
    }

    @Test
    public void faqTest() throws Exception {
        testHomeFAQ_Item(_indexItem, _patternQuestion, _patternAnswer);
    }

    public void testHomeFAQ_Item(int index, String patternQuestion, String patternAnswer) throws InterruptedException {
        WebDriver _driver = driverRule.getDriver();

        // Запрос на выборку элемента списка по заданному индексу
        String strQuery = String.format("//div[starts-with(@class,'Home_FAQ')]//div[@class='accordion__item'][%d]", index + 1);

        WebElement elemTarget = driverRule.getDriver().findElement(By.xpath(strQuery));
        assertNotNull("Элемент не найден по локатору: " + strQuery, elemTarget);

        // 1. Скроллируем элемент в видимую область
        ((JavascriptExecutor) _driver).executeScript("arguments[0].scrollIntoView();", elemTarget);

        // 2. Получаем заголовок элемента
        String actualQuestion = elemTarget.findElement(By.xpath("div[@role=\"heading\"]")).getText();

        // Сверяем текст заголовка (вопроса) с шаблоном
        assertEquals("Текст вопроса не совпадает с ожидаемым.", patternQuestion, actualQuestion);

        // 3. Кликаем по элементу
        elemTarget.click();

        // 4. Пытаемся получить дочерний элемент содержащий ответ (путём ожидания видимости)
        WebDriverWait wait = new WebDriverWait(_driver, java.time.Duration.ofSeconds(1));
        WebElement resultElement = wait.until(ExpectedConditions.visibilityOf(elemTarget.findElement(By.xpath(".//div[@class=\"accordion__panel\"]"))));
        String actualAnswer = resultElement.getText();

        // Сверяем текст ответа с шаблоном
        assertEquals("Текст ответа элемента не совпадает с образцом.", patternAnswer, actualAnswer);

        // на пол секунды притормаживаем текущий поток (для наглядности)
        Thread.sleep(500);
    }

}
