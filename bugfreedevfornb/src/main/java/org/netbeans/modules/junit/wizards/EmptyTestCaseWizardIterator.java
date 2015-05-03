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
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2007 Sun
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

package org.netbeans.modules.junit.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.SourceGroupModifier;
import org.netbeans.modules.java.testrunner.GuiUtils;
import org.netbeans.modules.junit.JUnitPluginTrampoline;
import org.netbeans.modules.junit.JUnitSettings;
import org.netbeans.modules.junit.TestUtil;
import org.netbeans.modules.junit.plugin.JUnitPlugin;
import org.netbeans.modules.gsf.testrunner.plugin.CommonPlugin.CreateTestParam;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;
//XXX: retouche
//import org.netbeans.jmi.javamodel.*;
//import org.netbeans.modules.javacore.api.JavaModel;

/**
 * @author  Marian Petras
 */
@SuppressWarnings("serial")
public class EmptyTestCaseWizardIterator
        implements TemplateWizard.Iterator {

    /** */
    private static EmptyTestCaseWizardIterator instance;

    /** */
    private TemplateWizard wizard;

    /** index of step &quot;Name &amp; Location&quot; */
    private static final int INDEX_TARGET = 2;

    /** name of panel &quot;Name &amp; Location&quot; */
    private final String nameTarget = NbBundle.getMessage(
            EmptyTestCaseWizardIterator.class,
            "LBL_panel_Target");                                        //NOI18N
    /** index of the current panel */
    private int current;
    /** registered change listeners */
    private List<ChangeListener> changeListeners;   //PENDING - what is this useful for?
    /** panel for choosing name and target location of the test class */
    private WizardDescriptor.Panel<WizardDescriptor> targetPanel;
    private Project lastSelectedProject = null;
    /** */
    private WizardDescriptor.Panel optionsPanel;

    /**
     */
    public void addChangeListener(ChangeListener l) {
        if (changeListeners == null) {
            changeListeners = new ArrayList<ChangeListener>(2);
        }
        changeListeners.add(l);
    }

    /**
     */
    public void removeChangeListener(ChangeListener l) {
        if (changeListeners != null) {
            changeListeners.remove(l);
            if (changeListeners.isEmpty()) {
                changeListeners = null;
            }
        }
    }

    /**
     * Notifies all registered listeners about a change.
     *
     * @see  #addChangeListener
     * @see  #removeChangeListener
     */
    private void fireChange() {
        if (changeListeners != null) {
            ChangeEvent e = new ChangeEvent(this);
            for (ChangeListener l : changeListeners) {
                l.stateChanged(e);
            }
        }
    }

    /**
     */
    public boolean hasPrevious() {
        return current > INDEX_TARGET;
    }

    /**
     */
    public boolean hasNext() {
        return current < INDEX_TARGET;
    }

    /**
     */
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        current--;
    }

    /**
     */
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        current++;
    }

    /**
     */
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        switch (current) {
            case INDEX_TARGET:
                return getTargetPanel();
            default:
                throw new IllegalStateException();
        }
    }

    @NbBundle.Messages("MSG_WizardInitializationError=There was an error initializing the wizard.")
    private WizardDescriptor.Panel<WizardDescriptor> getTargetPanel() {
	if(wizard == null) {
	    targetPanel = new StepProblemMessage(null, Bundle.MSG_WizardInitializationError());
	    return targetPanel;
	}
        final Project project = Templates.getProject(wizard);
        if (targetPanel == null || project != lastSelectedProject) {
            Collection<SourceGroup> sourceGroups = Utils.getTestTargets(project, true);
            if (sourceGroups.isEmpty()) {
                if (SourceGroupModifier.createSourceGroup(project, JavaProjectConstants.SOURCES_TYPE_JAVA, JavaProjectConstants.SOURCES_HINT_TEST) != null) {
                    sourceGroups = Utils.getTestTargets(project, true);
                }
            }
            if (sourceGroups.isEmpty()) {
                targetPanel = new StepProblemMessage(
                        project,
                        NbBundle.getMessage(EmptyTestCaseWizardIterator.class,
                                            "MSG_NoTestSourceGroup"));  //NOI18N
            } else {
                SourceGroup[] testSrcGroups;
                sourceGroups.toArray(
                        testSrcGroups = new SourceGroup[sourceGroups.size()]);
                if (optionsPanel == null) {
                    optionsPanel = new EmptyTestStepLocation();
                }
                targetPanel = JavaTemplates.createPackageChooser(project,
                                                                 testSrcGroups,
                                                                 optionsPanel);
            }
            lastSelectedProject = project;
        }

        return targetPanel;
    }

    /**
     */
    public String name() {
        switch (current) {
            case INDEX_TARGET:
                return nameTarget;
            default:
                throw new AssertionError(current);
        }
    }

    private void loadSettings(TemplateWizard wizard) {
        JUnitSettings settings = JUnitSettings.getDefault();
        
        wizard.putProperty(GuiUtils.CHK_SETUP,
                           Boolean.valueOf(settings.isGenerateSetUp()));
        wizard.putProperty(GuiUtils.CHK_TEARDOWN,
                           Boolean.valueOf(settings.isGenerateTearDown()));
        wizard.putProperty(GuiUtils.CHK_BEFORE_CLASS,
                           Boolean.valueOf(settings.isGenerateClassSetUp()));
        wizard.putProperty(GuiUtils.CHK_AFTER_CLASS,
                           Boolean.valueOf(settings.isGenerateClassTearDown()));
        wizard.putProperty(GuiUtils.CHK_HINTS,
                           Boolean.valueOf(settings.isBodyComments()));
    }

    private void saveSettings(TemplateWizard wizard) {
        JUnitSettings settings = JUnitSettings.getDefault();
        
        settings.setGenerateSetUp(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_SETUP)));
        settings.setGenerateTearDown(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_TEARDOWN)));
        settings.setGenerateClassSetUp(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_BEFORE_CLASS)));
        settings.setGenerateClassTearDown(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_AFTER_CLASS)));
        settings.setBodyComments(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_HINTS)));
    }

    /**
     * <!-- PENDING -->
     */
    public void initialize(TemplateWizard wiz) {
        this.wizard = wiz;
        current = INDEX_TARGET;
        loadSettings(wiz);
        

        String [] panelNames =  new String [] {
          NbBundle.getMessage(EmptyTestCaseWizardIterator.class,"LBL_panel_chooseFileType"),
          NbBundle.getMessage(EmptyTestCaseWizardIterator.class,"LBL_panel_Target")};

        ((javax.swing.JComponent)getTargetPanel().getComponent()).putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, panelNames); 
        ((javax.swing.JComponent)getTargetPanel().getComponent()).putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(0)); 


    }

    /**
     * <!-- PENDING -->
     */
    public void uninitialize(TemplateWizard wiz) {
        this.wizard = null;
        
        targetPanel = null;
        lastSelectedProject = null;
        optionsPanel = null;
        
        changeListeners = null;
    }

    public Set<DataObject> instantiate(TemplateWizard wizard) throws IOException {
        saveSettings(wizard);
        
        /* collect and build necessary data: */
        String name = Templates.getTargetName(wizard);
        FileObject targetFolder = Templates.getTargetFolder(wizard);
        
        Map<CreateTestParam, Object> params
                = TestUtil.getSettingsMap(false);
        params.put(CreateTestParam.CLASS_NAME,
                   Templates.getTargetName(wizard));
                
        /* create the test class: */
        final JUnitPlugin plugin = TestUtil.getPluginForProject(
                                                Templates.getProject(wizard));
        
        if (!JUnitPluginTrampoline.DEFAULT.createTestActionCalled(
                                            plugin,
                                            new FileObject[] {targetFolder})) {
            return null;
        }

        /*
         * The JUnitPlugin instance must be initialized _before_ field
         * JUnitPluginTrampoline.DEFAULT gets accessed.
         * See issue #74744.
         */
        final FileObject[] testFileObjects
                = JUnitPluginTrampoline.DEFAULT.createTests(
                     plugin,
                     null,
                     targetFolder,
                     params);
        
        if (testFileObjects == null) {
            throw new IOException();
        }
        
        DataObject testDataObject;
        try {
            testDataObject = DataObject.find(testFileObjects[0]);
        } catch (DataObjectNotFoundException ex) {
            throw new IOException();
        }
        
        return Collections.singleton(testDataObject);
    }

    /**
     */
    public static EmptyTestCaseWizardIterator singleton() {
        if (instance == null) {
            // PENDING - it should not be kept forever
            instance = new EmptyTestCaseWizardIterator();
        }
        return instance;
    }

}
