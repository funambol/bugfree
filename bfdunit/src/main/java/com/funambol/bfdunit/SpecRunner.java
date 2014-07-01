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

import com.funambol.bfdunit.Spec.None;
import java.util.List;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

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
        return getTestClass().getAnnotatedMethods(Spec.class);
    }
    
    /**
     * Adds to {@code errors} for each method annotated with {@code @Test}that
     * is not a public, void instance method with no arguments.
     */
    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(Spec.class, false, errors);
    }
    
    /**
     * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
     * has the {@code expecting} attribute, return normally only if {@code next}
     * throws an exception of the correct type, and throw an exception
     * otherwise.
     */
    protected Statement possiblyExpectingExceptions(FrameworkMethod method,
            Object test, Statement next) {
        Spec annotation = method.getAnnotation(Spec.class);
        return expectsException(annotation) ? new ExpectException(next,
                getExpectedException(annotation)) : next;
    }
    
    // --------------------------------------------------------- private methods
    
    private Class<? extends Throwable> getExpectedException(Spec annotation) {
        if (annotation == null || annotation.expected() == None.class) {
            return null;
        } else {
            return annotation.expected();
        }
    }

    private boolean expectsException(Spec annotation) {
        return getExpectedException(annotation) != null;
    }

    
    private long getTimeout(Spec annotation) {
        if (annotation == null) {
            return 0;
        }
        return annotation.timeout();
    }
    
}
