/*
 * **************************************************-
 * Ingrid Management iPlug
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.ibus.management.usecase;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;

/**
 * Interface for management iplug use cases.
 * 
 * @author joachim@wemove.com
 */
public interface ManagementUseCase {

    /**
     * Executes a use case of the management iplug.
     * 
     * @param query
     *            The query.
     * @param start
     *            The start of the result set to return.
     * @param length
     *            The length of the result set to return.
     * @param plugId
     *            The plugId of the management iplug.
     * @return The hits according toi the query.
     */
    IngridHit[] execute(IngridQuery query, int start, int length, String plugId);

}
