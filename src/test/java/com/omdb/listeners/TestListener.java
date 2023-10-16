package com.omdb.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Slf4j
public class TestListener implements ITestListener {

    public void onTestStart(ITestResult iTestResult) {
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nSTARTING TEST - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }

    public void onTestSuccess(ITestResult iTestResult) {
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nTEST PASSED - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }

    public void onTestFailure(ITestResult iTestResult) {
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nTEST FAILED - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        log.info("Listener - Thread: " + Thread.currentThread().getId() + "\nTEST SKIPPED - " + iTestResult.getMethod().getConstructorOrMethod().getName());
        log.info("\n******************************");
    }
}
