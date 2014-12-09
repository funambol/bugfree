package com.funambol.maven.junit;

import org.junit.internal.requests.ClassRequest;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;

/**
 * A <code>Request</code> is an abstract description of tests to be run. Older versions of
 * JUnit did not need such a concept--tests to be run were described either by classes containing
 * tests or a tree of {@link  org.junit.Test}s. However, we want to support filtering and sorting,
 * so we need a more abstract specification than the tests themselves and a richer
 * specification than just the classes.
 *
 * <p>The flow when JUnit runs tests is that a <code>Request</code> specifies some tests to be run -&gt;
 * a {@link org.junit.runner.Runner} is created for each class implied by the <code>Request</code> -&gt;
 * the {@link org.junit.runner.Runner} returns a detailed {@link org.junit.runner.Description}
 * which is a tree structure of the tests to be run.
 *
 * @since 4.0
 */
public abstract class SpecRequest extends Request {
    /**
     * Create a <code>Request</code> that, when processed, will run a single test.
     * This is done by filtering out all other tests. This method is used to support rerunning
     * single tests.
     *
     * @param clazz the class of the test
     * @param methodName the name of the test
     * @return a <code>Request</code> that will cause a single test be run
     */
    public static Request method(Class<?> clazz, String methodName) {
        Description method = Description.createTestDescription(clazz, methodName);
        return aClass(clazz).filterWith(method);
    }
    
    /**
     * Create a <code>Request</code> that, when processed, will run all the tests
     * in a class. The odd name is necessary because <code>class</code> is a reserved word.
     *
     * @param clazz the class containing the tests
     * @return a <code>Request</code> that will cause all tests in the class to be run
     */
    public static Request aClass(Class<?> clazz) {
        return new SpecClassRequest(clazz);
    }
   
    /**
     * Create a <code>Request</code> that, when processed, will run all the tests
     * in a class. If the class has a suite() method, it will be ignored.
     *
     * @param clazz the class containing the tests
     * @return a <code>Request</code> that will cause all tests in the class to be run
     */
    public static Request classWithoutSuiteMethod(Class<?> clazz) {
        return new SpecClassRequest(clazz, false);
    }

    /**
     * Returns a {@link Runner} for this Request
     *
     * @return corresponding {@link Runner} for this Request
     */
    public abstract Runner getRunner();

}
