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
package com.funambol.bfdunit.fakes;

import com.funambol.bfdunit.Spec;
import org.junit.Test;

/**
 * This class is not meant to be Run as a spec. It is used by BuFreeSpecJUnit4Provider
 * 
 * @author ste
 */
public class BugFreeClass {
    
    @Spec
    public void aFirstSpec() {
    }
    
    @Spec
    public void aSecondSpec() {
    }
    
    @Test
    public void aFirstTest() {
    }
    
    @Test
    @Spec
    public void aSpecAndTest() {
    }
    
}
