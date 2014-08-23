
package darks.orm.core.config;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import darks.orm.exceptions.ConfigException;

public class EntityConfiguration
{
    
    private static final int ENTITYMAP_INIT_SIZE = 32;
    
    private static ConcurrentMap<String, Class<?>> entityMap = new ConcurrentHashMap<String, Class<?>>(
        ENTITYMAP_INIT_SIZE);
    
    public EntityConfiguration()
    {
        
    }
    
    /**
     * ���ʵ������
     * 
     * @param alias ʵ�����
     * @param className ʵ������
     * @throws ConfigException
     */
    public Class<?> addEntityConfig(String alias, String className)
        throws ConfigException
    {
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new ConfigException(className + " does not exists.", e);
        }
        if (alias == null || "".equals(alias))
        {
            entityMap.put(className, clazz);
        }
        else
        {
            entityMap.put(alias, clazz);
            entityMap.put(className, clazz);
        }
        return clazz;
    }
    
    public Collection<Class<?>> getEntitys()
    {
        return entityMap.values();
    }
    
    public Class<?> getEntity(String key)
    {
        return entityMap.get(key);
    }
}
