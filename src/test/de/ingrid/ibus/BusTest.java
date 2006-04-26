/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
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

	private static final String ORGANISATION = "a organisation";

	private Bus bus;

	private PlugDescription[] plugDescriptions = new PlugDescription[3];

	protected void setUp() throws Exception {

		this.bus = new Bus(new DummyProxyFactory());
		for (int i = 0; i < this.plugDescriptions.length; i++) {
			this.plugDescriptions[i] = new PlugDescription();
			this.plugDescriptions[i].setPlugId("" + i);
			this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addField("ort");
			this.bus.getIPlugRegistry().addIPlug(this.plugDescriptions[i]);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testSearch() throws Exception {
		IngridQuery query = QueryStringParser.parse("fische ort:halle");
		IngridHits hits = this.bus
				.search(query, 10, 1, Integer.MAX_VALUE, 1000);
		assertEquals(this.plugDescriptions.length, hits.getHits().length);
	}
    
    /**
     * @throws Exception
     */
    public void testFieldSearch() throws Exception {
        this.plugDescriptions[this.plugDescriptions.length-1].addField("aField");
        IngridQuery query = QueryStringParser.parse("aField:halle");
        IngridHits hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(1, hits.getHits().length);
    }

	/**
	 * @throws Exception
	 */
	public void testSearchWithStatisticProcessors() throws Exception {
		// TODO: make this test log implementation independent
		this.bus.getProccessorPipe().addPreProcessor(
				new StatisticPreProcessor());
		this.bus.getProccessorPipe().addPostProcessor(
				new StatisticPostProcessor());

		IngridQuery query = QueryStringParser.parse("fische ort:halle");
		this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
	}

	/**
	 * Test the instanciation process.
	 */
	public void testAddIPlug() {
		PlugDescription pd = new PlugDescription();
		pd.setPlugId("bla");
		this.bus.addPlugDescription(pd);
		PlugDescription[] pds = this.bus.getIPlugRegistry().getAllIPlugs();
		assertEquals(4, pds.length);
	}

	/**
	 * @throws Exception
	 */
	public void testGetHitDetail() throws Exception {

		IngridQuery query = QueryStringParser.parse("fische ort:halle");
		IngridHits hits = this.bus
				.search(query, 10, 1, Integer.MAX_VALUE, 1000);
		assertEquals(this.plugDescriptions.length, hits.getHits().length);
		IngridHit[] hitArray = hits.getHits();
		for (int i = 0; i < hitArray.length; i++) {
			IngridHit hit = hitArray[i];
			IngridHitDetail details = this.bus.getDetail(hit, query,
					new String[0]);
			assertNotNull(details);
			assertEquals(DummyIPlug.TITLE, details.getTitle());

			String plugId = details.getPlugId();
			PlugDescription plugDescription = this.bus.getIPlugRegistry()
					.getPlugDescription(plugId);
			assertEquals(ORGANISATION, plugDescription.getOrganisation());
		}

	}

	/**
	 * @throws Exception
	 */
	public void testGetHitDetails() throws Exception {
		IngridQuery query = QueryStringParser.parse("fische ort:halle");
		IngridHits hits = this.bus
				.search(query, 10, 1, Integer.MAX_VALUE, 1000);
		
		assertEquals(this.plugDescriptions.length, hits.getHits().length);
		IngridHit[] hitArray = hits.getHits();
		IngridHitDetail[] details = this.bus.getDetails(hitArray, query,
				new String[0]);

		for (int i = 0; i < details.length; i++) {
			IngridHitDetail detail = details[i];
			assertEquals(detail.getDocumentId(), hitArray[i].getDocumentId());
		}

	}
}
