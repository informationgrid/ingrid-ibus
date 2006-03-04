/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.PlugQueryRequest;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.ibus.registry.SyntaxInterpreter;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.config.Configuration;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.processor.ProcessorPipe;
import de.ingrid.utils.query.IngridQuery;

/**
 * The IBus a centralized Bus that routes queries and return results. Created on
 * 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus implements IBus {

	private static final long serialVersionUID = Bus.class.getName().hashCode();

	private static Log fLogger = LogFactory.getLog(Bus.class);

	// TODO INGRID-398 we need to made the lifetime configurable.
	private Registry fRegistry;

	private ProcessorPipe fProcessorPipe = new ProcessorPipe();

	private IPlugProxyFactory fProxyFactory = null;

	private static Bus fBusInstance = null;

	// private HashMap fProxyPlugCache = new HashMap();

	/**
	 * For deserialization.
	 */
	public Bus() {
		fBusInstance = this;

		boolean iplugAutoActivation = getAutoActivationProperty();
		this.fRegistry = new Registry(100000, iplugAutoActivation);

	}

	private boolean getAutoActivationProperty() {
		Configuration configuration = new Configuration();
		InputStream resourceAsStream = Bus.class
				.getResourceAsStream("/configuration.xml");
		boolean iplugAutoActivation = true;
		if (resourceAsStream != null) {
			try {
				configuration.load(resourceAsStream);
			} catch (IOException e) {
				fLogger.error("unable to load existing configuration", e);
			}
			iplugAutoActivation = configuration.get("iplugAutoActivation",
					"false").equals("true");
		}
		return iplugAutoActivation;
	}

	/**
	 * @param factory
	 */
	public Bus(IPlugProxyFactory factory) {
		Bus.fBusInstance = this;
		this.fProxyFactory = factory;
		boolean iplugAutoActivation = getAutoActivationProperty();
		this.fRegistry = new Registry(100000, iplugAutoActivation);
	}

	/**
	 * Multicast the query to all connected IPlugs and return founded results.
	 * 
	 * @param query
	 * @param hitsPerPage
	 * @param currentPage
	 * @param length
	 * @param maxMilliseconds
	 * @return IngridHits as container for hits and meta data.
	 * @throws Exception
	 */
	public IngridHits search(IngridQuery query, final int hitsPerPage,
			int currentPage, final int length, int maxMilliseconds)
			throws Exception {
		Object monitor = new Object();
		if (fLogger.isDebugEnabled()) {
			fLogger.debug("search for: " + query.toString());
		}
		if (currentPage < 1) {
			currentPage = 1;
		}
		this.fProcessorPipe.preProcess(query);
		// TODO add grouping
		PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(
				query, this.fRegistry);

		ResultSet resultSet = new ResultSet(plugsForQuery.length, monitor);
		int plugsForQueryLength = plugsForQuery.length;
		PlugQueryRequest[] requests = new PlugQueryRequest[plugsForQueryLength];
		for (int i = 0; i < plugsForQueryLength; i++) {

			PlugDescription plugDescription = plugsForQuery[i];
			if (fLogger.isDebugEnabled()) {
				fLogger.debug("forward query to: "
						+ plugDescription.getPlugId() + ": "
						+ plugDescription.getOrganisation());
			}
			final int start = (hitsPerPage * (currentPage - 1));

			IPlug plugProxy = this.fRegistry.getProxyFromCache(plugDescription
					.getPlugId());

			if (null == plugProxy) {
				if (fLogger.isDebugEnabled()) {
					fLogger.debug("Create new connection to IPlug: "
							+ plugDescription.getPlugId());
				}
				try {
					plugProxy = this.fProxyFactory
							.createPlugProxy(plugDescription);
					this.fRegistry.addProxyToCache(plugDescription.getPlugId(),
							plugProxy);
				} catch (Exception e) {
					fLogger.info("removing plugdescription from repository: "
							+ plugDescription.getPlugId() + ": "
							+ plugDescription.getOrganisation(), e);
					this.fRegistry.removePlugFromCache(plugDescription
							.getPlugId());

					continue;
				}
			}
			requests[i] = new PlugQueryRequest(plugProxy, fRegistry,
					plugDescription.getPlugId(), resultSet, query, start,
					length);
			requests[i].start();

		}
		if (plugsForQueryLength > 0) {
			synchronized (monitor) {
				monitor.wait(maxMilliseconds);
				System.out.println("will not wait any more...");
			}
		}
		// stop all threads
		for (int i = 0; i < plugsForQueryLength; i++) {
			if (requests[i] != null) {
				requests[i].interrupt();
			}
			requests[i] = null; // for gc.
		}
		float maxScore = 1.0f;
		int totalHits = 0;
		int count = resultSet.size();
		ArrayList documents = new ArrayList();
		boolean ranked = true;
		for (int i = 0; i < count; i++) {
			IngridHits hits = (IngridHits) resultSet.get(i);
			totalHits += hits.length();
			if (ranked) {
				ranked = hits.isRanked();
				if (ranked && hits.getHits().length > 0) {
					if (maxScore < hits.getHits()[0].getScore()) {
						maxScore = hits.getHits()[0].getScore();
					}
				}

			}
			documents.addAll(Arrays.asList(hits.getHits()));
		}

		IngridHit[] hits = sortLimitNormalize((IngridHit[]) documents
				.toArray(new IngridHit[documents.size()]), hitsPerPage,
				currentPage, length, ranked, maxScore);

		this.fProcessorPipe.postProcess(query, hits);

		return new IngridHits("ibus", totalHits, hits, true);
	}

	private IngridHit[] sortLimitNormalize(IngridHit[] documents,
			int hitsPerPage, int currentPage, int length, boolean ranked,
			float maxScore) {
		// sort
		if (ranked) {
			// first normalize
			float scoreNorm = 1.0f / maxScore;
			int count = documents.length;
			for (int i = 0; i < count; i++) {
				documents[i].setScore(documents[i].getScore() * scoreNorm);
			}

			Arrays.sort(documents, new IngridHitComparator());
		}
		// To remove empty entries?
		length = Math.min(documents.length, length);
		IngridHit[] hits = new IngridHit[length];
		System.arraycopy(documents, 0, hits, 0, length);

		return hits;
	}

	/**
	 * Returns the current IBus instance.
	 * 
	 * @return The IBus instance.
	 */
	public static IBus getInstance() {
		Bus result = null;

		if (null != fBusInstance) {
			result = fBusInstance;
		} else {
			fLogger.error("Bus not yet instantiated.");
		}

		return result;
	}

	/**
	 * @param plugDescription
	 */
	public void addPlugDescription(PlugDescription plugDescription) {
		this.fRegistry.addIPlug(plugDescription);
	}

	/**
	 * @return The iplug registry.
	 */
	public Registry getIPlugRegistry() {
		return this.fRegistry;
	}

	/**
	 * @return The processing pipe.
	 */
	public ProcessorPipe getProccessorPipe() {
		return this.fProcessorPipe;
	}

	// public synchronized void removeIPlug(String iPlugId) {
	// fLogger.debug("Remove IPlug with ID: " + iPlugId);
	// PlugQueryRequest connection = (PlugQueryRequest) this.fProxyPlugCache
	// .remove(iPlugId);
	// if (null != connection) {
	// connection.interrupt();
	// }
	// }

	/**
	 * @param plugId
	 * @return The IPlug description.
	 */
	public PlugDescription getIPlug(String plugId) {
		return this.fRegistry.getPlugDescription(plugId);
	}

	public Record getRecord(IngridHit hit) throws Exception {
		PlugDescription plugDescription = getIPlugRegistry()
				.getPlugDescription(hit.getPlugId());
		IPlug plugProxy = (IPlug) this.fRegistry.getProxyFromCache(hit
				.getPlugId());
		if (null == plugProxy) {
			fLogger.debug("Create new connection to IPlug: "
					+ plugDescription.getPlugId());
			plugProxy = this.fProxyFactory.createPlugProxy(plugDescription);
			this.fRegistry.addProxyToCache(plugDescription.getPlugId(),
					plugProxy);
		}
		if (plugProxy instanceof IRecordLoader) {
			return ((IRecordLoader) plugProxy).getRecord(hit);
		}
		fLogger.warn("plug does not implement record loader: "
				+ plugDescription.getPlugId()
				+ " but was requested to load a record");
		return null;
	}

	/**
	 * @param hit
	 * @return A detailed document of a hit.
	 * @throws Exception
	 */
	public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery,
			String[] requestedFields) throws Exception {
		if (requestedFields == null) {
			requestedFields = new String[0];
		}
		IPlug plugProxy = getPlugProxy(hit.getPlugId());
		try {
			IngridHitDetail detail = plugProxy.getDetail(hit, ingridQuery,
					requestedFields);
			pushMetaData(detail);
			return detail;
		} catch (Exception e) {
			fLogger.error(e.toString());
		}
		// FIXME do we still need to announce any exception in the method
		// signature now?
		return null;
	}

	public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query,
			String[] requestedFields) throws Exception {

		if (requestedFields == null) {
			requestedFields = new String[0];
		}
		// collect requests for plugs
		HashMap hashMap = new HashMap();
		IngridHit hit = null;
		for (int i = 0; i < hits.length; i++) {
			hit = hits[i];
			ArrayList requestHitList = (ArrayList) hashMap.get(hit.getPlugId());
			if (requestHitList == null) {
				requestHitList = new ArrayList();
				hashMap.put(hit.getPlugId(), requestHitList);
			}
			requestHitList.add(hit);
		}
		// send requests and collect response
		Iterator iterator = hashMap.keySet().iterator();
		IPlug plugProxy;
		ArrayList resultList = new ArrayList();
		while (iterator.hasNext()) {
			String plugId = (String) iterator.next();
			ArrayList requestHitList = (ArrayList) hashMap.get(plugId);
			if (requestHitList != null) {
				IngridHit[] requestHits = (IngridHit[]) requestHitList
						.toArray(new IngridHit[requestHitList.size()]);
				plugProxy = getPlugProxy(plugId);
				IngridHitDetail[] responseDetails = plugProxy.getDetails(
						requestHits, query, requestedFields);
				resultList.addAll(Arrays.asList(responseDetails)); // FIXME to
				// improve performance we can use an Array instead of a list
				// here.
			}
		}

		int count = resultList.size();
		IngridHitDetail[] resultDetails = (IngridHitDetail[]) resultList
				.toArray(new IngridHitDetail[resultList.size()]);
		// sort to be in the same order as the requested hits.
		IngridHitDetail[] details = new IngridHitDetail[hits.length];
		for (int i = 0; i < hits.length; i++) {
			String plugId = hits[i].getPlugId();
			int documentId = hits[i].getDocumentId();

			boolean found = false;

			for (int j = 0; j < resultDetails.length; j++) {
				IngridHitDetail detail = resultDetails[j];
				if (detail.getDocumentId() == documentId
						&& detail.getPlugId().equals(plugId)) {
					details[i] = detail;
					pushMetaData(details[i]); // push meta data to details
					found = true;
				}
			}
			if (!found) {
				fLogger
						.error("unablefind details getDetais: "
								+ hit.toString());
				details[i] = new IngridHitDetail(hit, "no details found", ""); // FIXME
			}
		}
		return details;
	}

	private void pushMetaData(IngridHitDetail detail) {
		PlugDescription plugDescription = fRegistry.getPlugDescription(detail
				.getPlugId());
		detail.setOrganisation(plugDescription.getOrganisation());
		detail.setDataSourceName(plugDescription.getDataSourceName());
		detail.setIplugClassName(plugDescription.getIPlugClass());

	}

	/**
	 * @param plugId
	 * @return a proxy from cache or if it was not cached we create a new proxy
	 *         and add it to the cache.
	 * @throws Exception
	 */
	private IPlug getPlugProxy(String plugId) throws Exception {
		IPlug plugProxy = fRegistry.getProxyFromCache(plugId);
		if (null == plugProxy) {
			PlugDescription plugDescription = getIPlugRegistry()
					.getPlugDescription(plugId);
			fLogger.debug("Create new connection to IPlug: "
					+ plugDescription.getPlugId());
			plugProxy = this.fProxyFactory.createPlugProxy(plugDescription);
			this.fRegistry.addProxyToCache(plugDescription.getPlugId(),
					plugProxy);
		}
		return plugProxy;
	}
}
