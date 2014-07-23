How to use BFDUnit
------------------

1) create your maven project
2) add bug free configuration for surefire as usual:

  <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>2.14.1</version>
      <configuration>
	  <includes>
	      <include>**/BG*.java</include>
	      <include>**/BugFree*.java</include>
	      <include>**/*BugFree.java</include>
	      <include>**/*BF.java</include>
	  </includes>
      </configuration>
  </plugin>
  
 3) add a test dependency for bfdunit (in addition to junit and make sure to use the internal 
    Funambol repository):
 
  <dependency>
    <groupId>com.funambol</groupId>
    <artifactId>bfdunit</artifactId>
    <version>1.0</version>
    <scope>test</scope>
  </dependency>
  
4) Annotate your BugFree classes with the annotation:

   @RunWith(com.funambol.bfdunit.SpecRunner)

5) Use @Spec instead of @Test

For example:

   package com.funambol.test;

   import com.funambol.bfdunit.Spec;
   import com.funambol.bfdunit.SpecRunner;
   import static junit.framework.Assert.assertEquals;
   import org.junit.runner.RunWith;

   @RunWith(SpecRunner.class)
   public class BugFreeHelloWorld {
   
    @Spec
    public void hellowWorld() {
        assertEquals("hello world", HelloWorld.sayHello());
    }
}

