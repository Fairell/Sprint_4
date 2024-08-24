package ru.yandex.practicum.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class PageBase {
    protected final WebDriver _driver;

    //
    // Construction
    //

    public PageBase(WebDriver driver) {
        _driver = driver;
    }

    //
    // Public methods
    //


    //Открытие страницы
    public void open() throws Exception {
        if (!isOpened()) {
            _driver.get(getPageURL());
            //
            boolean bOpened = isOpenedEnsure();
            if (!bOpened) {
                throw new Exception(String.format("Не удалось открыть страницу \"%s\"", getPageName()));
            }
        }
    }

    //Проверяем фактическое отрытие страницы
    public boolean isOpened() {
        try {
            this._driver.findElement(getCheckOpenedPageQuery());
        } catch (NoSuchElementException err) {
            return  false;
        }
        //
        return  true;
    }

    //Принимаем куки на странице
    public void acceptCookies() {
        _driver.findElement(By.xpath(".//button[contains(@class, 'App_CookieButton')]")).click();
    }

    public boolean isOpenedEnsure () {
        WebDriverWait wait = new WebDriverWait(_driver, java.time.Duration.ofSeconds((3)));
        ExpectedCondition<WebElement> condition = ExpectedConditions.visibilityOfElementLocated(getCheckOpenedPageQuery());
        try {
            wait.until(condition);
        } catch (TimeoutException errTimeout) {
            return false;
        }
        //
        return true;
    }

    //
    // Internals
    //

    public abstract String getPageName();
    protected abstract String getPageURL();
    protected abstract By getCheckOpenedPageQuery();

} // class PageBase
