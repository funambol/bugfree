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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.maven.surefire.common.junit4.JUnit4TestChecker;

/**
 *
 * @author ste
 */
public class SpecChecker extends JUnit4TestChecker {

    public SpecChecker(ClassLoader testClassLoader) {
        super(testClassLoader);
    }

    @Override
    public boolean checkforTestAnnotatedMethod(Class testClass) {
        for (Method lMethod : testClass.getDeclaredMethods()) {
            for (Annotation lAnnotation : lMethod.getAnnotations()) {
                if (Spec.class.isAssignableFrom(lAnnotation.annotationType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
