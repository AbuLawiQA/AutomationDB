package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.DBUtils;
import utils.ExtentTestNGListener;
import com.aventstack.extentreports.Status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Listeners;

import static utils.ExtentTestNGListener.getTest;

@Listeners(ExtentTestNGListener.class)
public class E2ETestDB {
    protected WebDriver driver;
    List<String> orderedProducts;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        DBUtils.createDBAndTableIfNotExist();

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test(priority = 1, testName = "إضافة اكثر من منتج واتاكد انه تم اضافتهم في قاعدة البيانات")
    public void testMultipleOrdersAndVerifyInDB() throws InterruptedException {
        getTest().get().log(Status.INFO, "🚀 تم تشغيل المتصفح وتم إعداد قاعدة البيانات");

        driver.get("https://www.saucedemo.com/");
        getTest().get().log(Status.INFO, "🔗 فتح موقع saucedemo");

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        getTest().get().log(Status.INFO, "✅ تم تسجيل الدخول بنجاح");

        List<WebElement> products = driver.findElements(By.cssSelector("[data-test='inventory-item-name']"));
        List<WebElement> addButtons = driver.findElements(By.cssSelector(".inventory_item button"));

        orderedProducts = new ArrayList<>();

        for (int i = 0; i < 3 && i < products.size(); i++) {
            String name = products.get(i).getText();
            orderedProducts.add(name);
            addButtons.get(i).click();
            getTest().get().log(Status.INFO, "➕ تم إضافة المنتج: " + name);
        }

        driver.findElement(By.className("shopping_cart_link")).click();
        getTest().get().log(Status.INFO, "🛒 الانتقال إلى صفحة السلة");

        Thread.sleep(2000);

        for (String name : orderedProducts) {
            WebElement item = driver.findElement(By.xpath("//*[text()='" + name + "']"));
            Assert.assertTrue(item.isDisplayed(), "❌ المنتج غير ظاهر في السلة: " + name);
            getTest().get().log(Status.PASS, "✅ المنتج ظاهر في السلة: " + name);

            DBUtils.insertOrder(name);
            Assert.assertTrue(DBUtils.checkOrderExists(name), "❌ المنتج غير موجود في قاعدة البيانات: " + name);
            getTest().get().log(Status.PASS, "📦 المنتج موجود في قاعدة البيانات: " + name);
        }

        getTest().get().log(Status.PASS, "🎉 تم تنفيذ اختبار الإضافة والتحقق من قاعدة البيانات بنجاح");
    }

    @Test(priority = 2, testName = "حذف منتج معين من قاعدة البيانات والتحقق من الحذف")
    public void testDeleteOneProduct() {
        if (orderedProducts == null || orderedProducts.size() < 2) {
            getTest().get().log(Status.FAIL, "🚫 لا يوجد منتج ثاني لحذفه");
            Assert.fail("🚫 لا يوجد منتج ثاني لحذفه!");
        }

        String productToDelete = orderedProducts.get(1);
        getTest().get().log(Status.INFO, "🗑️ محاولة حذف المنتج: " + productToDelete);

        DBUtils.deleteOrderByName(productToDelete);

        boolean existsAfterDelete = DBUtils.checkOrderExists(productToDelete);
        Assert.assertFalse(existsAfterDelete, "❌ المنتج لم يُحذف من قاعدة البيانات: " + productToDelete);

        getTest().get().log(Status.PASS, "✅ تم حذف المنتج بنجاح: " + productToDelete);
    }

/*    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            getTest().get().log(Status.INFO, "🛑 تم إغلاق المتصفح");
        }
    }*/

    @BeforeMethod
    public WebDriver getDriver() {
        return this.driver;
    }
}
