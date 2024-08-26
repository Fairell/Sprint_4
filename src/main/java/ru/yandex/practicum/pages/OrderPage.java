package ru.yandex.practicum.pages;

import ru.yandex.practicum.EnvConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

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
}
