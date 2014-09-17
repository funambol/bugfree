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

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.surefire.report.ConsoleOutputReceiver;
import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.suite.RunResult;

/**
 *
 * @author ste
 */
public class SpecSurefireRunListener 
implements RunListener, ConsoleOutputReceiver, ReporterFactory {
    
    List<ReportEntry> entries = new ArrayList<ReportEntry>();

    public void testSetStarting(ReportEntry re) {
        add(re);
    }

    public void testSetCompleted(ReportEntry re) {
        add(re);
    }

    public void testStarting(ReportEntry re) {
        add(re);
    }

    public void testSucceeded(ReportEntry re) {
        add(re);
    }

    public void testAssumptionFailure(ReportEntry re) {
        add(re);
    }

    public void testError(ReportEntry re) {
        add(re);
    }

    public void testFailed(ReportEntry re) {
        add(re);
    }

    public void testSkipped(ReportEntry re) {
        add(re);
    }

    public void writeTestOutput(byte[] bytes, int off, int len, boolean stdout) {
       //System.out.write(bytes, off, len);
    }

    // --------------------------------------------------------- ReporterFactory
    
    public RunListener createReporter() {
        return new SpecSurefireRunListener();
    }
    
    public RunResult close() {
        return null;
    }
    
    // --------------------------------------------------------- private methods
    
    private void add(ReportEntry re) {
        System.out.println(re);
        if (re != null) {
            entries.add(re);
        }
    }

}
