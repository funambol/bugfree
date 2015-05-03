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
package com.funambol.bfdunit;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

/**
 *
 * @author ste
 */
@RunWith(SpecRunner.class)
public class BugFreeSpecRunner {
    
    @Spec
    public void annotation_using_run_with() throws Exception {
        //
        // If we do not have any errors we are good
        //
    }

    //
    // computeTestMethod
    // -----------------
    //
    
    @Spec
    public void compute_test_methods_returns_specs_and_tests() throws Exception {
        SpecRunner r = 
            new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClass.class);
        
        then(r.computeTestMethods()).hasSize(4);
    }
    
    @Spec
    public void compute_test_methods_returns_tests() throws Exception {
        SpecRunner r = 
            new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClassWithTestOnly.class);
        
        then(r.computeTestMethods()).hasSize(2);
    }
    
    @Spec
    public void compute_test_methods_returns_specs() throws Exception {
        SpecRunner r = 
            new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClassWithSpecOnly.class);
        
        then(r.computeTestMethods()).hasSize(1);
    }
    
    //
    // validateTestMethods
    // -------------------
    //
    // NOTE: it is not very clear how validateTestMethods is used, therefore
    // we write a few specs only for now
    //
    @Spec
    public void validate_test_methods_validates_specs_and_tests() throws Exception {
        List<Throwable> errors = new ArrayList<>();
        
        SpecRunner r = 
            new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClass.class);
        
        r.validateTestMethods(errors);
        
        then(errors).isEmpty();
    }
    
    @Spec
    public void validate_test_methods_validates_specs() throws Exception {
        List<Throwable> errors = new ArrayList<>();
        
        SpecRunner r = 
            new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClassWithSpecOnly.class);
        
        r.validateTestMethods(errors);
        
        then(errors).isEmpty();
    }
    
    //
    // possiblyExpectingExceptions
    // ---------------------------
    //
    
    @Spec
    public void possibly_expecting_exceptions_returns_methods_with_expectation() throws Exception {
        SpecRunner r = 
            new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClassWithTestOnly.class);
        List<FrameworkMethod> tests = r.computeTestMethods();
        then(r.possiblyExpectingExceptions(tests.get(0), null, null)).isNotNull();
        
        r = new SpecRunner(com.funambol.bfdunit.fakes.BugFreeClassWithSpecOnly.class);
        tests = r.computeTestMethods();
        then(r.possiblyExpectingExceptions(tests.get(0), null, null)).isNotNull();
    }

}
