/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.ibus.service;

import java.util.Timer;
import java.util.TimerTask;

import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.PlugDescription;

public class SimulatedLifesign {

    private final Timer timer;

    public SimulatedLifesign(Registry registry, PlugDescription pd) {
        timer = new Timer();
        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                pd.putLong(Registry.LAST_LIFESIGN, System.currentTimeMillis());
                registry.addPlugDescription( pd );
            }
        }, 60*1000, 60*1000 );
    }

    public void close() {
        timer.cancel();
    }
}
