package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.DBUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class E2ETestDB {
    protected WebDriver driver;
    List<String> orderedProducts;

   /* ExtentReports extent;
    ExtentTest test;*/

    @BeforeClass
    public void setup() {
  //     extent = ExtentManager.getInstance();

        WebDriverManager.chromedriver().setup();

        DBUtils.createDBAndTableIfNotExist();

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test(priority = 1, testName = "إضافة اكثر من منتج واتاكد انه منتج تم اضافتهم في القاعدة البيانات")
    public void testMultipleOrdersAndVerifyInDB() throws InterruptedException {
      //  test = extent.createTest("إضافة منتجات والتحقق منها في قاعدة البيانات");

        driver.get("https://www.saucedemo.com/");
    //    test.info("فتح موقع saucedemo");

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
     //   test.info("تم تسجيل الدخول بنجاح");

        List<WebElement> products = driver.findElements(By.cssSelector("[data-test='inventory-item-name']"));
        List<WebElement> addButtons = driver.findElements(By.cssSelector(".inventory_item button"));

        orderedProducts = new ArrayList<>();

        for (int i = 0; i < 3 && i < products.size(); i++) {
            String name = products.get(i).getText();
            orderedProducts.add(name);
            addButtons.get(i).click();
       //     test.info("تم إضافة المنتج للسلة: " + name);
        }

        driver.findElement(By.className("shopping_cart_link")).click();
      //  test.info("الانتقال إلى صفحة السلة");

        Thread.sleep(2000);

        for (String name : orderedProducts) {
            WebElement item = driver.findElement(By.xpath("//*[text()='" + name + "']"));
            Assert.assertTrue(item.isDisplayed(), "❌ المنتج غير ظاهر في السلة: " + name);
      //      test.pass("المنتج ظاهر في السلة: " + name);

            DBUtils.insertOrder(name);
            Assert.assertTrue(DBUtils.checkOrderExists(name), "❌ المنتج غير موجود في قاعدة البيانات: " + name);
      //      test.pass("تم التحقق من وجود المنتج في قاعدة البيانات: " + name);
        }

    //    test.pass("تمت إضافة والتحقق من 3 منتجات بنجاح!");
    }

    @Test(priority = 2, testName = "حذف منتج معين من قاعدة البيانات والتحقق من الحذف")
    public void testDeleteOneProduct() {
     //   test = extent.createTest("حذف منتج من قاعدة البيانات والتحقق من الحذف");

        if (orderedProducts == null || orderedProducts.size() < 2) {
        //    test.fail("🚫 لا يوجد منتج ثاني لحذفه!");
            Assert.fail("🚫 لا يوجد منتج ثاني لحذفه!");
        }

        String productToDelete = orderedProducts.get(1);
      //  test.info("محاولة حذف المنتج: " + productToDelete);

        DBUtils.deleteOrderByName(productToDelete);

        boolean existsAfterDelete = DBUtils.checkOrderExists(productToDelete);
        Assert.assertFalse(existsAfterDelete, "❌ المنتج لم يُحذف من قاعدة البيانات: " + productToDelete);

     //   test.pass("✅ تم حذف المنتج بنجاح: " + productToDelete);
    }
/*
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
   //     extent.flush();
    }*/
    @BeforeMethod
    public WebDriver getDriver() {
        return this.driver;
    }
}


