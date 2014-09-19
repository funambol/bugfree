How to use BugFreeToolkit
-------------------------

1) create your maven project

2) add a test dependency as follows:

    <dependency>
        <groupId>org.apache.maven.surefire</groupId>
        <artifactId>surefire-junit4</artifactId>
        <version>2.16</version>
        <type>jar</type>
        <scope>test</scope>
    </dependency>

  This dependency is necessary because we will excluded it in the surefire 
  pluging configuration, otherwise surefire would pick it up as a provider to 
  use and bug free specs will be executed twice (once by the bugfreetoolkit 
  provider and once by the standard JUnit provider.

3) add a test dependency for bugfreetoolkit (in addition to junit and make sure 
   to use the internal Funambol repository):
 
    <dependency>
        <groupId>com.funambol</groupId>
        <artifactId>bugfreetoolkit</artifactId>
        <version>1.2</version>
        <scope>test</scope>
    </dependency>

4) configure the surefire plugin as follows:

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
        <dependencies>
            <!--
                This triggers the use of the bugfreetoolkit's provider
            -->
            <dependency>
                <groupId>com.funambol</groupId>
                <artifactId>bugfreetoolkit</artifactId>
                <version>1.2</version>
                <exclusions>
                    <exclusion>
                        <artifactId>surefire-junit4</artifactId>
                        <groupId>org.apache.maven.surefire</groupId>
                    </exclusion>
                </exclusions>
            </dependency>
        </dependencies>
  </plugin>
  
5) Use @Spec instead of @Test

NOTES:

1) see the test project for an example on hot to use it
2) KNOWN ISSUE: Using only the Spec annotation, the functions Run focused test
   and Debug focused test do not work any more in Netbeans. I am trying to
   understand why.

