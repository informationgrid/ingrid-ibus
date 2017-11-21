package de.ingrid.ibus.service;

import java.util.Timer;
import java.util.TimerTask;

import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.PlugDescription;

public class SimulatedLifesign {

    public SimulatedLifesign(Registry registry, PlugDescription pd) {
        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                pd.putLong(Registry.LAST_LIFESIGN, System.currentTimeMillis());
                registry.addPlugDescription( pd );
            }
        }, 60*1000, 60*1000 );
    }
}
