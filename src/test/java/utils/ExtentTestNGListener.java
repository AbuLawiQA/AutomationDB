package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentTestNGListener implements ITestListener {
    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ThreadLocal<ExtentTest> getTest() {
        return test;
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "✅ Test Passed");

        try {
            Object currentClass = result.getInstance();
            String screenshotPath = ScreenshotUtils.captureScreenshot(
                    ((tests.E2ETestDB) currentClass).getDriver(),
                    "pass_" + result.getMethod().getMethodName()
            );
            test.get().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            test.get().log(Status.WARNING, "Unable to attach screenshot on success: " + e.getMessage());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "❌ Test Failed: " + result.getThrowable());

        try {
            Object currentClass = result.getInstance();
            String screenshotPath = ScreenshotUtils.captureScreenshot(
                    ((tests.E2ETestDB) currentClass).getDriver(),
                    "fail_" + result.getMethod().getMethodName()
            );
            test.get().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            test.get().log(Status.WARNING, "Unable to attach screenshot on failure: " + e.getMessage());
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
}
