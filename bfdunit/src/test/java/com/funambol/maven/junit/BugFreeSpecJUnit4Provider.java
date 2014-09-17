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
import com.funambol.bfdunit.fakes.BugFreeClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.surefire.booter.BaseProviderFactory;
import org.apache.maven.surefire.testset.TestRequest;
import org.apache.maven.surefire.util.DefaultScanResult;
import org.apache.maven.surefire.util.TestsToRun;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.runner.notification.RunListener;

/**
 * 
 * !!!NOTE!!! because of the way it works (or what I could understand!)
 * we loose the stdout.println().
 */

/*
 * TODO: manage reporterFactory in bug free code; for now I could not understand
 *       how this is managed by serefire and the junit of JUnit4Provider does
 *       not provide any clue. Thereofore for now we just manage the case where
 *       it may be null.
 */
public class BugFreeSpecJUnit4Provider {
    
    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();
    
    private ClassLoader classLoader = null;
    private BaseProviderFactory booterParameters = null;
    
    @Before
    public void setUp() {
        classLoader = this.getClass().getClassLoader();
        
        Properties providerProperties = new Properties();
        providerProperties.setProperty("listener", "com.funambol.maven.junit.SpecAntRunListener");
        
        booterParameters = new BaseProviderFactory(new SpecSurefireRunListener(), Boolean.TRUE);
        booterParameters.setProviderProperties(providerProperties);
        booterParameters.setClassLoaders(classLoader);
        booterParameters.setTestRequest(new TestRequest(null, null, null));
    }

    @Test
    public void createProvider() throws Exception {
        SpecJUnit4Provider provider = new SpecJUnit4Provider(booterParameters);

        //
        // configuration parameters shall be set
        //
        then(get(provider, "providerParameters")).isSameAs(booterParameters);
        then(get(provider, "testClassLoader")).isSameAs(classLoader);
        then(get(provider, "requestedTestMethod")).isNull();
        then(get(provider, "runOrderCalculator")).isNull();
        then(get(provider, "scanResult")).isNotNull();
        then(get(provider, "specChecker")).isNotNull();
        
        List<RunListener> listeners = 
            (List<RunListener>)get(provider, "customRunListeners");
        then(listeners).isNotNull().hasSize(1);
        then(listeners.get(0)).isInstanceOf(SpecAntRunListener.class);
    }
    
    @Test
    public void invokeUsesSpecJUnitProvider() throws Exception {
        //
        // NOTE: I do not like the way this spec is implemented, but I could not
        // find a better way to do it that would make sense.
        //
        // TODO: may be refactor the provider to be more bug free?
        //
        Method m = SpecJUnit4Provider.class.getDeclaredMethod("getRunner", Class.class);
        m.setAccessible(true); Object o = m.invoke(null, BugFreeClass.class);
        then(o).isInstanceOf(SpecRunner.class);
    }
    
    @Test
    public void invokeWithClasspathScanUsesJUnitProvider() throws Exception {
        final List<String> FILES = new ArrayList<String>();
        FILES.add("com.funambol.bfdunit.fakes.BugFreeClass");
        
        SpecJUnit4Provider provider = new SpecJUnit4Provider(booterParameters);
        
        Field f = SpecJUnit4Provider.class.getDeclaredField("scanResult");
        f.setAccessible(true);
        f.set(provider, new DefaultScanResult(FILES));
        
        Method m = SpecJUnit4Provider.class.getDeclaredMethod("scanClassPath");
        m.setAccessible(true); 
        TestsToRun specs = (TestsToRun)m.invoke(provider);
        
        then(specs).isNotNull();
        then(specs.containsAtLeast(1)).isTrue();
        
        then(((Class)(specs.iterator().next())).getName()).isEqualTo("com.funambol.bfdunit.fakes.BugFreeClass");
    }
    
    
    // --------------------------------------------------------- private methods
    
    private Object get(final Object o, final String field) 
    throws NoSuchFieldException, SecurityException, IllegalAccessException {
        Field f = o.getClass().getDeclaredField(field);
        f.setAccessible(true);
        
        return f.get(o);
    } 
}
