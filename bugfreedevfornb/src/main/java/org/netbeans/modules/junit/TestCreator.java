/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.junit;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
//import org.netbeans.modules.junit.plugin.JUnitPlugin.CreateTestParam;
import org.netbeans.modules.gsf.testrunner.plugin.CommonPlugin.CreateTestParam;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author  Marian Petras
 */
public final class TestCreator implements TestabilityJudge {
    
    /**
     * bitmap combining modifiers PUBLIC, PROTECTED and PRIVATE
     *
     * @see  java.lang.reflect.Modifier
     */
    static final Set<Modifier> ACCESS_MODIFIERS
            = EnumSet.of(Modifier.PUBLIC,
                         Modifier.PROTECTED,
                         Modifier.PRIVATE);
    
    /** */
    private final TestGeneratorSetup setup;
    /** */
    private final JUnitVersion junitVersion;
    
    /** Creates a new instance of TestCreator */
    TestCreator(boolean loadDefaults,
                JUnitVersion junitVersion) {
        setup = new TestGeneratorSetup(loadDefaults);
        this.junitVersion = junitVersion;
    }
    
    /** Creates a new instance of TestCreator */
    TestCreator(Map<CreateTestParam, Object> params,
                JUnitVersion junitVersion) {
        setup = new TestGeneratorSetup(params);
        this.junitVersion = junitVersion;
    }
    
    /**
     */
    public void createEmptyTest(FileObject testFileObj) throws IOException {
        AbstractTestGenerator testGenerator;
        switch (junitVersion) {
            case JUNIT3:
                testGenerator = new JUnit3TestGenerator(
                        setup,
                        TestUtil.getSourceLevel(testFileObj));
                break;
            case JUNIT4:
                testGenerator = new JUnit4TestGenerator(setup);
                break;
            default:
                throw new IllegalStateException("junit version not set");//NOI18N
        }
        doModifications(testFileObj, testGenerator);
    }
    
    /**
     * 
     * @return  list of names of created classes
     */
    public void createSimpleTest(ElementHandle<TypeElement> topClassToTest,
                                 FileObject testFileObj,
                                 boolean isNewTestClass) throws IOException {
        AbstractTestGenerator testGenerator;
        switch (junitVersion) {
            case JUNIT3:
                testGenerator = new JUnit3TestGenerator(
                      setup,
                      Collections.singletonList(topClassToTest),
                      null,
                      isNewTestClass,
                      TestUtil.getSourceLevel(testFileObj));
                break;
            case JUNIT4:
                testGenerator = new JUnit4TestGenerator(
                                          setup,
                                          Collections.singletonList(topClassToTest),
                                          null,
                                          isNewTestClass);
                break;
            default:
                throw new IllegalStateException("junit version not set");//NOI18N
        }
        doModifications(testFileObj, testGenerator);
    }
    
    /**
     */
    public List<String> createTestSuite(List<String> suiteMembers,
                                        FileObject testFileObj,
                                        boolean isNewTestClass) throws IOException {
        AbstractTestGenerator testGenerator;
        switch (junitVersion) {
            case JUNIT3:
                testGenerator = new JUnit3TestGenerator(
                      setup,
                      null,
                      suiteMembers,
                      isNewTestClass,
                      TestUtil.getSourceLevel(testFileObj));
                break;
            case JUNIT4:
                testGenerator = new JUnit4TestGenerator(
                                          setup,
                                          null,
                                          suiteMembers,
                                          isNewTestClass);
                break;
            default:
                throw new IllegalStateException("junit version not set");//NOI18N
        }
        doModifications(testFileObj, testGenerator);
        
        return testGenerator.getProcessedClassNames();
    }

    private void doModifications(final FileObject testFileObj,
                                 final AbstractTestGenerator testGenerator)
                                                            throws IOException {
        final JavaSource javaSource = JavaSource.forFileObject(testFileObj);
        javaSource.runUserActionTask(
                new Task<CompilationController>() {
                    public void run(CompilationController parameter) throws Exception {
                        ModificationResult result
                                = javaSource.runModificationTask(testGenerator);
                        result.commit();
                    }
                },
            true);

    }
    
    public TestabilityResult isClassTestable(CompilationInfo compInfo,
                                             TypeElement classElem, long skipTestabilityResultMask) {
        return setup.isClassTestable(compInfo, classElem, skipTestabilityResultMask);
    }

    public boolean isMethodTestable(ExecutableElement method) {
        return setup.isMethodTestable(method);
    }

}
