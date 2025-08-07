// ملف: utils/ScreenshotUtils.java
package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String screenshotDir = System.getProperty("user.dir") + "/test-output/screenshots/";
        String fullPath = screenshotDir + fileName;

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.createDirectories(Path.of(screenshotDir));
            Files.copy(src.toPath(), Path.of(fullPath), StandardCopyOption.REPLACE_EXISTING);
            return fullPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
