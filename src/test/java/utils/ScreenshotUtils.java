package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    public static String captureScreenshot(WebDriver driver, String screenshotName) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // حفظ الصورة داخل test-output/screenshots
        String screenshotDir = System.getProperty("user.dir") + "/test-output/screenshots/";
        String fileName = screenshotName + "_" + timestamp + ".png";
        String destPath = screenshotDir + fileName;

        File directory = new File(screenshotDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("❌ فشل في إنشاء مجلد screenshots داخل test-output");
            }
        }

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(srcFile, new File(destPath));

        // ارجع المسار النسبي لتقرير HTML
//        return "screenshots/" + fileName;
        return "./screenshots/" + screenshotName + "_" + timestamp + ".png";

    }
}