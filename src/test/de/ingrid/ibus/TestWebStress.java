/*
 * Copyright (c) 1997-2006 by media style GmbH
 * 
 * $Source
 */

package de.ingrid.ibus;

import net.sourceforge.jwebunit.WebTestCase;

/**
 * Test which accesses a running sproose webapp.
 * 
 * <p/>created on 08.09.2006
 * 
 * @version $Revision: 1.1 $
 * @author jz
 * @author $Author: jz ${lastedit}
 */
public class TestWebStress extends WebTestCase {

    protected void setUp() throws Exception {
        getTestContext().setBaseUrl("http://213.144.28.209/ingrid-portal");
    }

    /**
     * @throws Exception
     */
    public void testInfoPage() throws Exception {
        int threadCount = 75;
        int clickCount = 20;
        CallThread[] callThreads = new CallThread[threadCount];
        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i] = new CallThread("/portal/main-search.psml?action=doSearch&q=wasser&ds=1", clickCount);
            callThreads[i].start();
        }
        
        System.out.println("All query threads started.");

        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i].join();
            if (callThreads[i].getException() != null) {
                System.out.println(callThreads[i].getException().getMessage());
            }
        }
        
        System.out.println("All query threads finished.");
    }

    private class CallThread extends Thread {

        private String fPath;

        private int fCount;

        private Exception fException;

        /**
         * @param path
         * @param count
         */
        public CallThread(String path, int count) {
            this.fPath = path;
            this.fCount = count;
        }

        /**
         * @return null or a thrown exception
         */
        public Exception getException() {
            return this.fException;
        }

        public void run() {
            try {
                for (int i = 0; i < this.fCount; i++) {
                    beginAt(this.fPath);
                }
            } catch (Exception e) {
                this.fException = e;
            }
        }
    }
}