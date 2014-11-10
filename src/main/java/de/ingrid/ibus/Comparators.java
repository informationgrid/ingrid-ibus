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
 */

package de.ingrid.ibus;

import java.util.Comparator;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;

/**
 * Container for different {@link java.util.Comparator}.
 * 
 * <p/>created on 09.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class Comparators {

    /**
     * Compares the score of two hits.
     */
    public static final Comparator SCORE_HIT_COMPARATOR = new Comparator() {

        public int compare(Object arg0, Object arg1) {
        	if (null == arg0 && null == arg1) {
        		return 0;
        	} else if (null == arg0) {
                return 1;
            } else if (null == arg1) {
                return -1;
            }

            IngridHit hit0 = (IngridHit) arg0;
            IngridHit hit1 = (IngridHit) arg1;

            return Float.compare(hit1.getScore(), hit0.getScore());
        }
    };

    /**
     * Constant for unranked hits comparator position. This denotes the field for comparison in unranked hits.
     */
    public static final String UNRANKED_HITS_COMPARATOR_POSITION = "hits-position";

    /**
     * For comparing two unranked hits.
     */
    public static final Comparator UNRANKED_HITS_COMPARATOR = new Comparator() {

        public int compare(Object o1, Object o2) {
            IngridHits hits1 = (IngridHits) o1;
            IngridHits hits2 = (IngridHits) o2;
            Integer pos1 = (Integer) hits1.get(UNRANKED_HITS_COMPARATOR_POSITION);
            Integer pos2 = (Integer) hits2.get(UNRANKED_HITS_COMPARATOR_POSITION);
            return pos1.compareTo(pos2);
        }
    };
}
