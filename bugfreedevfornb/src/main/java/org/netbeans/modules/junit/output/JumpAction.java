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

package org.netbeans.modules.junit.output;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.gsf.testrunner.api.CallstackFrameNode;
import org.netbeans.modules.gsf.testrunner.api.TestsuiteNode;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Marian Petras
 */
final class JumpAction extends AbstractAction {

    /** */
    private final Node node;
    /** */
    private final String callstackFrameInfo;

    /** Creates a new instance of JumpAction */
    public JumpAction(Node node, String callstackFrameInfo) {
        this.node = node;
        this.callstackFrameInfo = callstackFrameInfo;
    }

    /**
     * If the <code>callstackFrameInfo</code> is not <code>null</code>,
     * tries to jump to the callstack frame source code. Otherwise does nothing.
     */
    public void actionPerformed(ActionEvent e) {
        if (node instanceof TestsuiteNode){
            OutputUtils.openTestsuite((TestsuiteNode)node);
        } else if (node instanceof CallstackFrameNode){
            OutputUtils.openCallstackFrame(node, callstackFrameInfo);
        } else if (node instanceof JUnitTestMethodNode){
            if (((JUnitTestMethodNode) node).getTestcase().getTrouble() != null) {
                // method failed, find failing line within the testMethod using the stacktrace
                OutputUtils.openCallstackFrame(node, "");
            } else {
                OutputUtils.openTestMethod((JUnitTestMethodNode) node);
            }
        }
    }

    @Override
    public Object getValue(String key) {
        if (key.equals(Action.NAME)) {
            return NbBundle.getMessage(JumpAction.class, "LBL_GotoSource"); //NOI18N
        }else{
            return super.getValue(key);
        }
    }

}
