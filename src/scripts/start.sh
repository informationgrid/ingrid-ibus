#!/bin/sh
# 
# Environment Variables
#
#   INGRID_JAVA_HOME Overrides JAVA_HOME.
#
#   INGRID_HEAPSIZE  heap to use in mb, if not setted we use 1000.
#
#   INGRID_OPTS      addtional java runtime options


CLASS=de.ingrid.ibus.BusServer
sh starter.sh $CLASS --descriptor conf/jxta.properties --adminpassword --adminport --busurl 

