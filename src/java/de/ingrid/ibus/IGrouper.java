package de.ingrid.ibus;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

public interface IGrouper {

    IngridHits groupHits(IngridQuery query, IngridHit[] hits, int hitsPerPage, int totalHits, int startHit) throws Exception;
}
