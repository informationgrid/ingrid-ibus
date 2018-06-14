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
