/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  $Source:  $
 */

package de.ingrid.ibus;

import java.util.Comparator;

import de.ingrid.utils.IngridHit;

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
    public static final Comparator INGRID_HIT_COMPARATOR = new Comparator() {

        public int compare(Object arg0, Object arg1) {
            if (null == arg0) {
                return -1;
            }

            if (null == arg1) {
                return 1;
            }

            IngridHit hit0 = (IngridHit) arg0;
            IngridHit hit1 = (IngridHit) arg1;

            float score0 = hit0.getScore();
            float score1 = hit1.getScore();
            float scoreDiff = score0 - score1;

            return (scoreDiff < 0) ? 1 : -1;
        }

    };
}
