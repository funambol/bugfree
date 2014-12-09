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

import com.funambol.bfdunit.SpecRunner;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.requests.ClassRequest;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Runner;

/**
 *
 * @author ste
 */
public class SpecClassRequest extends SpecRequest {
    private final Object runnerLock = new Object();
    private final Class<?> testClass;
    private final boolean canUseSuiteMethod;
    private volatile Runner runner;
    
    public SpecClassRequest(Class<?> testClass, boolean canUseSuiteMethod) {
        this.testClass = testClass;
        this.canUseSuiteMethod = canUseSuiteMethod;
    }

    public SpecClassRequest(Class<?> testClass) {
        this(testClass, true);
    }
    
    @Override
    public Runner getRunner() {
        if (runner == null) {
            synchronized (runnerLock) {
                if (runner == null) {
                    try {
                        runner = new SpecRunner(testClass);
                    } catch (Throwable e) {
                        return new ErrorReportingRunner(testClass, e);
                    }
                }
            }
        }
        return runner;
    }
}
