/*
 * <FUNAMBOLCOPYRIGHT>
 * Copyright (C) 2014 Funambol.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Funambol.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Funambol MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. Funambol SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * </FUNAMBOLCOPYRIGHT>
 */
package com.funambol.maven.junit;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import com.funambol.bfdunit.SpecChecker;
import com.funambol.bfdunit.SpecRunner;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.surefire.common.junit4.JUnit4RunListener;
import org.apache.maven.surefire.common.junit4.JUnit4RunListenerFactory;
import org.apache.maven.surefire.providerapi.AbstractProvider;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.apache.maven.surefire.report.ConsoleOutputCapture;
import org.apache.maven.surefire.report.ConsoleOutputReceiver;
import org.apache.maven.surefire.report.PojoStackTraceWriter;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.report.SimpleReportEntry;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.SelectorUtils;
import org.apache.maven.surefire.suite.RunResult;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.util.RunOrderCalculator;
import org.apache.maven.surefire.util.ScanResult;
import org.apache.maven.surefire.util.TestsToRun;
import org.apache.maven.surefire.util.internal.StringUtils;

import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * 
 */
public class SpecJUnit4Provider
        extends AbstractProvider {

    private final ClassLoader testClassLoader;

    private final List<org.junit.runner.notification.RunListener> customRunListeners;

    private final SpecChecker specChecker;

    private final String requestedTestMethod;

    private TestsToRun testsToRun;

    private final ProviderParameters providerParameters;

    private final RunOrderCalculator runOrderCalculator;

    private final ScanResult scanResult;

    public SpecJUnit4Provider(ProviderParameters booterParameters) {
        providerParameters = booterParameters;
        testClassLoader = booterParameters.getTestClassLoader();
        scanResult = booterParameters.getScanResult();
        runOrderCalculator = booterParameters.getRunOrderCalculator();
        customRunListeners = JUnit4RunListenerFactory.
                createCustomListeners(booterParameters.getProviderProperties().getProperty("listener"));
        specChecker = new SpecChecker(testClassLoader);
        requestedTestMethod = booterParameters.getTestRequest().getRequestedTestMethod();

    }

    public RunResult invoke(Object forkTestSet)
            throws TestSetFailedException, ReporterException {
        if (testsToRun == null) {
            if (forkTestSet instanceof TestsToRun) {
                testsToRun = (TestsToRun) forkTestSet;
            } else if (forkTestSet instanceof Class) {
                testsToRun = TestsToRun.fromClass((Class) forkTestSet);
            } else {
                testsToRun = scanClassPath();
            }
        }

        final ReporterFactory reporterFactory = providerParameters.getReporterFactory();

        final RunListener reporter = reporterFactory.createReporter();

        ConsoleOutputCapture.startCapture((ConsoleOutputReceiver) reporter);

        JUnit4RunListener jUnit4TestSetReporter = new JUnit4RunListener(reporter);

        Result result = new Result();
        RunNotifier runNotifer = getRunNotifer(jUnit4TestSetReporter, result, customRunListeners);

        runNotifer.fireTestRunStarted(null);

        for (Class aTestsToRun : testsToRun) {
            executeTestSet(aTestsToRun, reporter, runNotifer);
        }

        runNotifer.fireTestRunFinished(result);

        JUnit4RunListener.rethrowAnyTestMechanismFailures(result);

        closeRunNotifer(jUnit4TestSetReporter, customRunListeners);

        return reporterFactory.close();
    }
    
    public Iterator<?> getSuites() {
        testsToRun = scanClassPath();
        return testsToRun.iterator();
    }
    
    // --------------------------------------------------------- private methods

    private void executeTestSet(Class<?> clazz, RunListener reporter, RunNotifier listeners)
            throws ReporterException, TestSetFailedException {
        final ReportEntry report = new SimpleReportEntry(this.getClass().getName(), clazz.getName());

        reporter.testSetStarting(report);

        try {
            if (!StringUtils.isBlank(this.requestedTestMethod)) {
                String actualTestMethod = getMethod(clazz, this.requestedTestMethod);//add by rainLee
                String[] testMethods = StringUtils.split(actualTestMethod, "+");
                execute(clazz, listeners, testMethods);
            } else {//the original way
                execute(clazz, listeners, null);
            }
        } catch (TestSetFailedException e) {
            throw e;
        } catch (Throwable e) {
            reporter.testError(SimpleReportEntry.withException(report.getSourceName(), report.getName(),
                    new PojoStackTraceWriter(report.getSourceName(),
                            report.getName(), e)));
        } finally {
            reporter.testSetCompleted(report);
        }
    }

    private RunNotifier getRunNotifer(org.junit.runner.notification.RunListener main, Result result,
            List<org.junit.runner.notification.RunListener> others) {
        RunNotifier fNotifier = new RunNotifier();
        fNotifier.addListener(main);
        fNotifier.addListener(result.createListener());
        for (org.junit.runner.notification.RunListener listener : others) {
            fNotifier.addListener(listener);
        }
        return fNotifier;
    }

    // I am not entierly sure as to why we do this explicit freeing, it's one of those
    // pieces of code that just seem to linger on in here ;)
    private void closeRunNotifer(org.junit.runner.notification.RunListener main,
            List<org.junit.runner.notification.RunListener> others) {
        RunNotifier fNotifier = new RunNotifier();
        fNotifier.removeListener(main);
        for (org.junit.runner.notification.RunListener listener : others) {
            fNotifier.removeListener(listener);
        }
    }

    private TestsToRun scanClassPath() {
        final TestsToRun scannedClasses = scanResult.applyFilter(specChecker, testClassLoader);
        return (runOrderCalculator != null) 
            ? runOrderCalculator.orderTestClasses(scannedClasses) 
            : scannedClasses;
    }

    private static void execute(Class<?> testClass, RunNotifier fNotifier, String[] testMethods)
            throws TestSetFailedException {
        if (null != testMethods) {
            Method[] methods = testClass.getMethods();
            for (Method method : methods) {
                for (String testMethod : testMethods) {
                    if (SelectorUtils.match(testMethod, method.getName())) {
                        Runner junitTestRunner = SpecRequest.method(testClass, method.getName()).getRunner();
                        junitTestRunner.run(fNotifier);
                    }

                }
            }
            return;
        }

        Runner junitTestRunner = getRunner(testClass);

        junitTestRunner.run(fNotifier);
    }

    /**
     * this method retrive testMethods from String like
     * "com.xx.ImmutablePairTest#testBasic,com.xx.StopWatchTest#testLang315+testStopWatchSimpleGet"
     * <br>
     * and we need to think about cases that 2 or more method in 1 class. we
     * should choose the correct method
     *
     * @param testClass the testclass
     * @param testMethodStr the test method string
     * @return a string ;)
     */
    private static String getMethod(Class testClass, String testMethodStr) {
        String className = testClass.getName();

        if (!testMethodStr.contains("#") && !testMethodStr.contains(",")) {//the original way
            return testMethodStr;
        }
        testMethodStr += ",";//for the bellow  split code
        int beginIndex = testMethodStr.indexOf(className);
        int endIndex = testMethodStr.indexOf(",", beginIndex);
        String classMethodStr
                = testMethodStr.substring(beginIndex, endIndex);//String like "StopWatchTest#testLang315"

        int index = classMethodStr.indexOf('#');
        if (index >= 0) {
            return classMethodStr.substring(index + 1, classMethodStr.length());
        }
        return null;
    }
    
    /**
     * Returns an instance of the runner to be used to run the classes; it always
     * returns an instance of SpecRunner.
     * 
     */
    private static Runner getRunner(final Class c) throws TestSetFailedException {
        try {
            return new SpecRunner(c);
        } catch (InitializationError e) {
            //
            // NOTE: e.getMessage() is null!
            //
            throw new TestSetFailedException(e);
        }
    }
    
}
