/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.junit.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.gsf.testrunner.api.TestsuiteNode;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author answer
 */
public class JUnitTestsuiteNode extends TestsuiteNode{

    public JUnitTestsuiteNode(String suiteName, boolean filtered) {
        super(suiteName, filtered);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        Action preferred = getPreferredAction();
        if (preferred != null) {
            actions.add(preferred);
        }

        FileObject testFO = ((JUnitTestSuite)getSuite()).getSuiteFO();
        if (testFO != null){
            ActionProvider actionProvider = OutputUtils.getActionProvider(testFO);
            DataObject testDO = null;
            try {
                testDO = DataObject.find(testFO);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (actionProvider != null && testDO != null){
                List supportedActions = Arrays.asList(actionProvider.getSupportedActions());
                Lookup nodeContext = Lookups.singleton(testDO);

                if (supportedActions.contains(ActionProvider.COMMAND_TEST_SINGLE) &&
                        actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, nodeContext)) {
                    actions.add(new TestMethodNodeAction(actionProvider,
                                                         nodeContext,
                                                         ActionProvider.COMMAND_TEST_SINGLE,
                                                         "LBL_RerunTest"));     //NOI18N
                }
                if (supportedActions.contains(ActionProvider.COMMAND_DEBUG_TEST_SINGLE) &&
                        actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, nodeContext)) {
                    actions.add(new TestMethodNodeAction(actionProvider,
                                                         nodeContext,
                                                         ActionProvider.COMMAND_DEBUG_TEST_SINGLE,
                                                         "LBL_DebugTest"));     //NOI18N
                }
            }
        }
        
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return new JumpAction(this, null);
    }

}
