import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {

        // Create a new instance of the HtmlUnitDriver

        FirefoxDriver driver =new FirefoxDriver();

        // Navigate to the website
        String domainName = "shopee.tw";
        String url = "https://www.similarweb.com/zh-tw/website/" + domainName;
        driver.get(url);

        // Wait for the elements to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        List<WebElement> genderList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("wa-demographics__gender-legend-item-title")));
        for (WebElement gender : genderList) {
            System.out.println(gender.getText());
        }

        // Print the text of the element

        // Close the driver
        driver.quit();
    }
}
