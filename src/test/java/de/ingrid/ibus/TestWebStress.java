/*
 * **************************************************-
 * Ingrid iBus
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2006 by media style GmbH
 * 
 * $Source
 */

package de.ingrid.ibus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

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

    private static final String SEPERATOR = "\t";

    private static final String URL = "http://213.144.28.209/opensearch";

    private static final String URI = "/query?q=1+OR+3+datatype:default+ranking:score";

    private int _responseCode200 = 0;

    private int _responseCodeUnknown = 0;

    protected void setUp() throws Exception {
        getTestContext().setBaseUrl(URL);
    }

    public void testOpensearch() throws Exception {
        DecimalFormat format = new DecimalFormat(".00");
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("webStress.csv")));
        int threadCount = 150;
        int clickCount = 3;
        for (int i = 1; i < threadCount + 1; i++) {
            for (int j = 1; j < clickCount + 1; j++) {
                long time = click(i, j);
                // assertEquals(i, _responseCode200);
                // assertEquals(0, _responseCodeUnknown);

                String line = i + SEPERATOR + j + SEPERATOR + format.format((time / 1000.0)) + SEPERATOR
                        + format.format(((time / 1000.0) / i)) + SEPERATOR + format.format(((i * j) / (time / 1000.0)))
                        + SEPERATOR + _responseCode200 + SEPERATOR + _responseCodeUnknown;
                System.out.println(line);
                writer.println(line);
                writer.flush();
                _responseCode200 = 0;
                _responseCodeUnknown = 0;
            }
        }
        writer.close();
    }

    /**
     * @throws Exception
     */
    public long click(int threadCount, int clickCount) throws Exception {

        long start = System.currentTimeMillis();
        CallThread[] callThreads = new CallThread[threadCount];
        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i] = new CallThread(URI, clickCount);
            callThreads[i].start();
        }

        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i].join();
            if (callThreads[i].getException() != null) {
                System.out.println(callThreads[i].getException().getMessage());
            }
        }

        long end = System.currentTimeMillis();
        return end - start;
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
                    if (tester.getDialog().getResponse().getResponseCode() == 200) {
                        _responseCode200++;
                    } else {
                        _responseCodeUnknown++;
                    }
                }
            } catch (Exception e) {
                this.fException = e;
            }
        }
    }

}
