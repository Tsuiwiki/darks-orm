package darks.orm.core.cache.scope;

import darks.orm.core.cache.CacheContext;

public interface CacheProvider
{
    
    /**
     * ��ʼ��EHCACHE
     */
    public boolean initialize(CacheContext cacheContext);
    
    /**
     * ehcache jar���Ƿ��Ѿ�����
     * 
     * @return
     */
    public boolean isClassLoaded();
    
    public void shutdown();
    
    public boolean isInit();
}
