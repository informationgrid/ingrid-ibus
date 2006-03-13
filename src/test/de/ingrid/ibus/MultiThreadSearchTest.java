package de.ingrid.ibus;

import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

public class MultiThreadSearchTest extends TestCase {

	public void testTreads() throws Exception {
		PlugDescription[] plugDescriptions = new PlugDescription[3];
		Bus bus = new Bus(new DummyProxyFactory());
		for (int i = 0; i < plugDescriptions.length; i++) {
			plugDescriptions[i] = new PlugDescription();
			plugDescriptions[i].setPlugId("" + i);
			plugDescriptions[i].setOrganisation("bla");
			bus.getIPlugRegistry().addIPlug(plugDescriptions[i]);
		}
		IngridQuery query = QueryStringParser.parse("fische ort:halle");
		new TestThread(bus, query).start();
		new TestThread(bus, query).start();

		System.out.println("bal");

	}

	class TestThread extends Thread {

		public TestThread(Bus bus, IngridQuery query) {
			this.bus = bus;
			this.query = query;

		}

		private Bus bus;

		private IngridQuery query;

		public void run() {
			try {
				this.bus.search(this.query, 10, 1, Integer.MAX_VALUE, 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
