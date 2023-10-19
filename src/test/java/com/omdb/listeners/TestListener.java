package com.omdb.listeners;

import lombok.extern.log4j.Log4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Log4j
public class TestListener implements ITestListener {

    public void onTestStart(ITestResult iTestResult) {
        log.info("\n" +
                "  ____ _____  _    ____ _____ ___ _   _  ____   _____ _____ ____ _____ \n" +
                " / ___|_   _|/ \\  |  _ \\_   _|_ _| \\ | |/ ___| |_   _| ____/ ___|_   _|\n" +
                " \\___ \\ | | / _ \\ | |_) || |  | ||  \\| | |  _    | | |  _| \\___ \\ | |  \n" +
                "  ___) || |/ ___ \\|  _ < | |  | || |\\  | |_| |   | | | |___ ___) || |  \n" +
                " |____/ |_/_/   \\_\\_| \\_\\|_| |___|_| \\_|\\____|   |_| |_____|____/ |_| \n" +
                "                                                                               ");
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nSTARTING TEST - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }

    public void onTestSuccess(ITestResult iTestResult) {
        log.info("\n" +
                "  _____ _____ ____ _____   ____   _    ____ ____  _____ ____  \n" +
                " |_   _| ____/ ___|_   _| |  _ \\ / \\  / ___/ ___|| ____|  _ \\ \n" +
                "   | | |  _| \\___ \\ | |   | |_) / _ \\ \\___ \\___ \\|  _| | | | |\n" +
                "   | | | |___ ___) || |   |  __/ ___ \\ ___) |__) | |___| |_| |\n" +
                "   |_| |_____|____/ |_|   |_| /_/   \\_\\____/____/|_____|____/ \n" +
                "                                                              ");
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nTEST PASSED - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }

    public void onTestFailure(ITestResult iTestResult) {
        log.info("\n" + "" +
                "  _____ _____ ____ _____   _____ _    ___ _     _____ ____  \n" +
                " |_   _| ____/ ___|_   _| |  ___/ \\  |_ _| |   | ____|  _ \\ \n" +
                "   | | |  _| \\___ \\ | |   | |_ / _ \\  | || |   |  _| | | | |\n" +
                "   | | | |___ ___) || |   |  _/ ___ \\ | || |___| |___| |_| |\n" +
                "   |_| |_____|____/ |_|   |_|/_/   \\_\\___|_____|_____|____/ \n" +
                "                                                            ");
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nTEST FAILED - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        log.info("\n" +
                "  _____ _____ ____ _____   ____  _  _____ ____  ____  _____ ____  \n" +
                " |_   _| ____/ ___|_   _| / ___|| |/ /_ _|  _ \\|  _ \\| ____|  _ \\ \n" +
                "   | | |  _| \\___ \\ | |   \\___ \\| ' / | || |_) | |_) |  _| | | | |\n" +
                "   | | | |___ ___) || |    ___) | . \\ | ||  __/|  __/| |___| |_| |\n" +
                "   |_| |_____|____/ |_|   |____/|_|\\_\\___|_|   |_|   |_____|____/ \n" +
                "                                                                  ");
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nTEST SKIPPED - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }
}
