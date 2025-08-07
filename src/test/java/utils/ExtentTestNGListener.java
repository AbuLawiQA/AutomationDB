package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import tests.E2ETestDB;

import java.io.IOException;

public class ExtentTestNGListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        WebDriver driver = extractDriver(result);
        if (driver != null) {
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "pass_" + result.getName());
            test.get().log(Status.PASS, "✅ Test Passed");
            if (screenshotPath != null) {
                test.get().addScreenCaptureFromPath(screenshotPath);
            }
        } else {
            test.get().log(Status.PASS, "✅ Test Passed (no driver found)");
        }
    }


    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = extractDriver(result);
        if (driver != null) {
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "fail_" + result.getName());
            test.get().log(Status.FAIL, "❌ Test Failed: " + result.getThrowable())
                    .addScreenCaptureFromPath(screenshotPath);
        } else {
            test.get().log(Status.FAIL, "❌ Test Failed: " + result.getThrowable() + " (no driver found)");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "⚠️ Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private WebDriver extractDriver(ITestResult result) {
        Object currentClass = result.getInstance();
        if (currentClass instanceof E2ETestDB) {
            return ((E2ETestDB) currentClass).getDriver();
        }
        return null;
    }
}
