package com.funambol.test;

import com.funambol.bfdunit.Spec;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;

/**
 *
 * @author ste
 */
public class BugFreeHelloWorld {
    
    @Spec
    public void hellowWorldSpec() {
        then(HelloWorld.sayHello("spec")).isEqualTo("hello world from a spec");
    }
    
    @Test
    public void hellowWorldTest() {
        then(HelloWorld.sayHello("test")).isEqualTo("hello world from a test");
    }
    
    @Spec(expected = IllegalArgumentException.class)
    public void exception() {
        throw new IllegalArgumentException();
    }
    

}
