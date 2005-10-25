/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.processor.impl.StatisticPostProcessor;
import de.ingrid.utils.processor.impl.StatisticPreProcessor;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * BusTest
 * 
 * <p/>created on 07.09.2005
 * 
 * @version $Revision: $
 * @author sg
 * @author $Author jz ${lastedit}
 * 
 */
public class BusTest extends TestCase {

    private Bus bus;

    private PlugDescription[] plugDescriptions = new PlugDescription[3];

    /**
     * Call setUp for the feature tests
     * 
     * @throws Exception
     * 
     */
    public BusTest() throws Exception {
        setUp();
    }

    protected void setUp() throws Exception {
        this.bus = new Bus(new DummyProxyFactory());
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setPlugId("" + i);
            this.bus.getIPlugRegestry().addIPlug(this.plugDescriptions[i]);
        }

    }

    /**
     * @throws Exception
     */
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        IngridDocument[] documents = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(this.plugDescriptions.length, documents.length);
    }

    /**
     * @throws Exception
     */
    public void testSearchWithStatisticProcessors() throws Exception {
        TestAppender appender = new TestAppender();
        Logger.getLogger(StatisticPreProcessor.class).addAppender(appender);
        Logger.getLogger(StatisticPostProcessor.class).addAppender(appender);
        this.bus.getProccessorPipe().addPreProcessor(new StatisticPreProcessor());
        this.bus.getProccessorPipe().addPostProcessor(new StatisticPostProcessor());

        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(2, appender.fLogMessageCount);
        Logger.getLogger(StatisticPostProcessor.class).removeAppender(appender);
    }

    private class TestAppender implements Appender {

        /**
         * 
         */
        public int fLogMessageCount = 0;

        public void addFilter(Filter arg0) {
            //
        }

        public Filter getFilter() {
            return null;
        }

        public void clearFilters() {
            //
        }

        public void close() {
            //
        }

        public void doAppend(LoggingEvent arg0) {
            this.fLogMessageCount++;
        }

        public String getName() {
            return null;
        }

        public void setErrorHandler(ErrorHandler arg0) {
            //
        }

        public ErrorHandler getErrorHandler() {
            return null;
        }

        public void setLayout(Layout arg0) {
            //
        }

        public Layout getLayout() {
            return null;
        }

        public void setName(String arg0) {
            //
        }

        public boolean requiresLayout() {
            return false;
        }

    }
}
