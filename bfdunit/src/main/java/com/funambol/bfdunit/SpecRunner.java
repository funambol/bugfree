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

import java.util.List;
import java.util.Collections;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 */
public class SpecRunner extends BlockJUnit4ClassRunner {

    public SpecRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
    
    // ------------------------------------------------------- protected methods
    
    /**
     * Returns the methods that run tests. Default implementation returns all
     * methods annotated with {@code @Test} on this class and superclasses that
     * are not overridden.
     */
    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        TestClass c = getTestClass();
        List<FrameworkMethod> specs = c.getAnnotatedMethods(Spec.class);
        List<FrameworkMethod> tests = c.getAnnotatedMethods(Test.class);
        
        for (FrameworkMethod m: tests) {
            if (!specs.contains(m)) {
                specs.add(m);
            }
        }
        return specs; 
    }
    
    /**
     * Adds to {@code errors} for each method annotated with {@code @Test}that
     * is not a public, void instance method with no arguments.
     */
    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(Spec.class, false, errors);
        validatePublicVoidNoArgMethods(Test.class, false, errors);
    }
    
    /**
     * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
     * has the {@code expecting} attribute, return normally only if {@code next}
     * throws an exception of the correct type, and throw an exception
     * otherwise.
     */
    @Override
    protected Statement possiblyExpectingExceptions(FrameworkMethod method,
            Object test, Statement next) {
        Test testAnnotation = method.getAnnotation(Test.class);
        if (testAnnotation != null) {
            return super.possiblyExpectingExceptions(method, test, next);
        }
        
        Spec specAnnotation = method.getAnnotation(Spec.class);
        return expectsException(specAnnotation) ? new ExpectException(next,
                getExpectedException(specAnnotation)) : next;
    }
    
    // --------------------------------------------------------- private methods
    
    private Class<? extends Throwable> getExpectedException(Spec annotation) {
        if (annotation == null || annotation.expected() == Spec.None.class) {
            return null;
        } else {
            return annotation.expected();
        }
    }

    private boolean expectsException(Spec annotation) {
        return getExpectedException(annotation) != null;
    }
}
