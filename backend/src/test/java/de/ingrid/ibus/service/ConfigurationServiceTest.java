/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class ConfigurationServiceTest {

    @Test
    public void propertiesEmptyTest() throws Exception {
        Properties result = getProperties(new Properties());

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("value3Mod", result.get("key3"));
        Assert.assertEquals("value4New", result.get("key4"));
    }

    @Test
    public void propertiesChangeTest() throws Exception {
        Properties props = new Properties();

        props.put("key4", "value4Mod");
        Properties result = getProperties(props);

        Assert.assertEquals(2, result.size());
        Assert.assertEquals("value3Mod", result.get("key3"));
        Assert.assertEquals("value4Mod", result.get("key4"));
    }

    /**
     * A property that is reverted to the original system property is removed from the user override property.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    @Test
    public void propertiesRevertTest() throws Exception {
        Properties props = new Properties();
//        Assert.assertEquals("8083", new ConfigurationService().getConfiguration().get("server.port"));

        props.put("key3", "value3");
        Properties result = getProperties(props);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("value4New", result.get("key4"));
    }

    private Properties getProperties(Properties props) throws Exception {
        ConfigurationService service = new ConfigurationService();
        Method getModifiedProperties = service.getClass().getDeclaredMethod("getModifiedProperties", Properties.class);
        Field serviceProperties = service.getClass().getDeclaredField("properties");
        Field servicePropertiesSystem = service.getClass().getDeclaredField("propertiesSystem");
        getModifiedProperties.setAccessible(true);
        serviceProperties.setAccessible(true);
        servicePropertiesSystem.setAccessible(true);

        Properties propsSystem = new Properties();
        propsSystem.put("key1", "value1");
        propsSystem.put("key2", "value2");
        propsSystem.put("key3", "value3");

        Properties propsAll = new Properties();
        propsAll.put("key1", "value1");
        propsAll.put("key2", "value2");
        propsAll.put("key3", "value3Mod");
        propsAll.put("key4", "value4New");

        serviceProperties.set(service, propsAll);
        servicePropertiesSystem.set(service, propsSystem);

        return (Properties) getModifiedProperties.invoke(service, props);
    }
}
