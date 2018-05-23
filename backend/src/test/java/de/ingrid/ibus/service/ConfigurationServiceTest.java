package de.ingrid.ibus.service;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class ConfigurationServiceTest {

    @Test
    public void propertiesEmptyTest() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Properties result = getProperties(new Properties());

        Assert.assertEquals(5, result.size());
        Assert.assertEquals("admin", result.get("codelistrepo.username"));
    }

    @Test
    public void propertiesChangeTest() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Properties props = new Properties();

        props.put("codelistrepo.username", "olaf");
        Properties result = getProperties(props);

        Assert.assertEquals(5, result.size());
        Assert.assertEquals("olaf", result.get("codelistrepo.username"));
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
    public void propertiesRevertTest() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Properties props = new Properties();
//        Assert.assertEquals("8083", new ConfigurationService().getConfiguration().get("server.port"));

        props.put("server.port", "${SERVER_PORT:80}");
        props.put("codelistrepo.username", "${CODELIST_USERNAME:}");
        Properties result = getProperties(props);

        Assert.assertEquals(3, result.size());
        Assert.assertEquals(null, result.get("server.port"));
        Assert.assertEquals(null, result.get("codelistrepo.username"));
    }

    private Properties getProperties(Properties props) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ConfigurationService service = new ConfigurationService();
        Method getModifiedProperties = service.getClass().getDeclaredMethod("getModifiedProperties", Properties.class);
        getModifiedProperties.setAccessible(true);

        return (Properties) getModifiedProperties.invoke(service, props);
    }
}
