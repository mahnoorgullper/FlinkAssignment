import com.thoughtworks.selenium.webdriven.commands.SeleniumSelect;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.asserts.SoftAssert;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Program {

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");

        System.setProperty("webdriver.chrome.driver", dir + "\\chromedriver_win32\\chromedriver.exe");
        final WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("https://weathershopper.pythonanywhere.com/");

        final WebElement tempElement = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/span"));
        final String temperature = tempElement.getText();
        final String[] tempSplit = temperature.split(" ");
        int degree = Integer.parseInt(tempSplit[0]);
        if (degree < 19) {
            clickCategory(driver, "/html/body/div[1]/div[3]/div[1]/a/button");
        } else {
            //For Sunscreen
            clickCategory(driver, "/html/body/div[1]/div[3]/div[2]/a/button");
        }

//        driver.close();
    }

    @Test
    private static void clickCategory(WebDriver driver, String s) {
        SoftAssert softAssert = new SoftAssert();
        driver.findElement(By.xpath(s)).click();
        //For moisturizer
        List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@class, 'text-center col-4')]"));
        LinkedList<ProductDetail> aloes = new LinkedList<>();
        LinkedList<ProductDetail> almond = new LinkedList<>();
        LinkedList<ProductDetail> spf30 = new LinkedList<>();
        LinkedList<ProductDetail> spf50 = new LinkedList<>();
        for (WebElement element : elements) {
            final String productText = element.getText();
//            System.out.println(element.getText());
            if (productText.contains("Aloe")) {
                aloes.add(new ProductDetail(element));
            }
            if (productText.contains("almond")) {
                almond.add(new ProductDetail(element));
            }
            if (productText.contains("SPF-30")) {
                spf30.add(new ProductDetail(element));
            }
            if (productText.contains("SPF-50")) {
                spf50.add(new ProductDetail(element));
            }
        }
        addToCart(aloes);
        addToCart(almond);
        addToCart(spf30);
        addToCart(spf50);
        if (almond.size() == 0) {
            softAssert.assertNull("assertion failed");

        }
        if (aloes.size() == 0) {
            softAssert.assertNull("assertion failed");
        }
        driver.findElement(By.xpath("/html/body/nav/ul/button")).click();


        //get prices of items and then sum
//        int Price = Integer.parseInt(driver.findElement(By.xpath("/html/body/div[1]/div[2]/table/tbody/tr[1]/td[2]")).getText());
//        System.out.println(Price);
//        //get price 2
//        int Price2 = Integer.parseInt(driver.findElement(By.xpath("/html/body/div[1]/div[2]/table/tbody/tr[2]/td[2]")).getText());
//        System.out.println(Price2);
//        int sum = Price + Price2;
//        //get actual sum
//        String TotalPrice = driver.findElement(By.id("total")).getText();
//        final String[] priceSplit = TotalPrice.split(" ");
//        int priceFinal = Integer.parseInt(priceSplit[2]);
//        System.out.println(priceFinal);
//        if (sum == priceFinal) {
        //click on payment option
        driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/button/span")).click();
        driver.switchTo().frame(0);
        //Add stripe details
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys("mahnoor@gmail.com");
        driver.findElement(By.id("card_number")).sendKeys("4242");
        driver.findElement(By.id("card_number")).sendKeys("4242");
        driver.findElement(By.id("card_number")).sendKeys("4242");
        driver.findElement(By.id("card_number")).sendKeys("4242");
        driver.findElement(By.id("cc-exp")).sendKeys("03");
        driver.findElement(By.id("cc-exp")).sendKeys("24");
        driver.findElement(By.id("cc-csc")).sendKeys("333");
        driver.findElement(By.id("billing-zip")).sendKeys("46000");
        driver.findElement(By.id("submitButton")).click();

        driver.switchTo().defaultContent();
        try {
//verify success message after payment
            String text = driver.findElement(By.className("text-justify")).getText();
            String ExpectedTitle = "Your payment was successful. You should receive a follow-up call from our sales team.";
            Assert.assertEquals(ExpectedTitle, text);
        } catch (Exception e) {
            softAssert.assertNull("assertion failed");
        }
    }

    private static void addToCart(LinkedList<ProductDetail> products) {
        if (!products.isEmpty()) {
            final Optional<ProductDetail> min = products.stream()
                    .min(Comparator.comparingInt(ProductDetail::getPrice));
            final WebElement element = min.get().getElement();
            element.findElement(By.cssSelector("button")).click();
        }
    }

}
