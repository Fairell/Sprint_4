package ru.yandex.practicum.pages;

import ru.yandex.practicum.EnvConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

public class HomePage extends PageBase {
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
}
