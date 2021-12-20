import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProductDetail {
    private final WebElement element;
    private final int price;

    public ProductDetail(final WebElement element) {
        this.element = element;
        this.price = extractProductPrice(element);
    }

    public WebElement getElement() {
        return element;
    }

    public int getPrice() {
        return price;
    }

    private int extractProductPrice(WebElement element) {
        final WebElement priceTag = element.findElement(By.cssSelector("div > p:nth-child(3)"));
        return Integer.parseInt(getPriceFromString(priceTag));
    }

    private String getPriceFromString(WebElement priceTag) {
        final String[] s = priceTag.getText().split(" ");
        if(s.length > 2){
            return s[2];
        }
        return s[1];
    }
}
