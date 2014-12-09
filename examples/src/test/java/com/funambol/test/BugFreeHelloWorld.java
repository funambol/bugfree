package com.funambol.test;

import com.funambol.bfdunit.Spec;
import static org.assertj.core.api.BDDAssertions.then;

/**
 *
 * @author ste
 */
public class BugFreeHelloWorld {
    
    @Spec
    public void hellowWorld() {
        then(HelloWorld.sayHello()).isEqualTo("hello world");
    }

}
