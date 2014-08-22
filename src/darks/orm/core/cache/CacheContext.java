package darks.orm.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import darks.orm.app.Page;
import darks.orm.core.cache.scope.ApplicationCacheFactory;
import darks.orm.core.cache.scope.CacheFactory;
import darks.orm.core.cache.scope.CacheProvider;
import darks.orm.core.cache.scope.EhCacheProvider;
import darks.orm.core.cache.scope.ThreadCacheFactory;
import darks.orm.core.cache.thread.CacheAsynchronousThread;
import darks.orm.core.data.EntityData;
import darks.orm.core.factory.ClassFactory;
import darks.orm.core.session.SessionContext;
import darks.orm.log.Logger;
import darks.orm.log.LoggerFactory;
import darks.orm.util.ThreadHelper;

/**
 * ����:CacheContext ����:������ ����ʱ��:2012.02.15 �汾:1.0.2 alpha ��Ȩ:CopyRight(c)2012 ������
 * ����Ŀ��������Ȩ������������ ����:ȫ�ֿ��ƻ���������
 */
@SuppressWarnings("unchecked")
public class CacheContext
{
    
    private static Logger log = LoggerFactory.getLogger(CacheContext.class);
    
    public static final String SCOPE_APPLICATION = "application";
    
    public static final String SCOPE_THREAD = "thread";
    
    public enum CacheKeyType
    {
        SingleKey, ListKey, PageKey
    }
    
    private List<CacheProvider> cacheProviderList = new ArrayList<CacheProvider>();
    
    private ConcurrentMap<String, CacheFactory> cacheFactorys = null;
    
    private boolean synchronous = true;
    
    public CacheContext()
    {
        cacheFactorys = new ConcurrentHashMap<String, CacheFactory>(3);
        addCacheFactory(SCOPE_APPLICATION, ApplicationCacheFactory.getInstance());
        addCacheFactory(SCOPE_THREAD, ThreadCacheFactory.getInstance());
        EhCacheProvider ehCacheProvider = new EhCacheProvider();
        if (ehCacheProvider.isClassLoaded())
        {
            addCacheProvider(ehCacheProvider);
        }
        synchronous = SessionContext.getConfigure().getCacheConfig().isSynchronous();
    }
    
    /**
     * ��ʼ������������
     */
    public void initialize()
    {
        for (CacheProvider cacheProvider : cacheProviderList)
        {
            if (cacheProvider.isClassLoaded() && !cacheProvider.isInit())
            {
                cacheProvider.initialize(this);
            }
        }
    }
    
    public void shutdown()
    {
        for (CacheProvider cacheProvider : cacheProviderList)
        {
            if (cacheProvider.isClassLoaded() && cacheProvider.isInit())
            {
                cacheProvider.shutdown();
            }
        }
    }
    
    /**
     * ��ӻ��湤��
     * 
     * @param key ������ֵ
     * @param cacheFactory ����ʵ��
     */
    public void addCacheFactory(String key, CacheFactory cacheFactory)
    {
        cacheFactorys.put(key, cacheFactory);
    }
    
    /**
     * �Ƴ����湤��
     * 
     * @param key ������ֵ
     */
    public void removeCacheFactory(String key)
    {
        cacheFactorys.remove(key);
    }
    
    /**
     * ��ӻ��湩Ӧ��
     * 
     * @param cacheProvider
     */
    public void addCacheProvider(CacheProvider cacheProvider)
    {
        cacheProviderList.add(cacheProvider);
    }
    
    /**
     * ���浥������
     * 
     * @param cls ʵ����
     * @param cacheId ��������
     * @param id ʵ������ֵ
     * @param value ʵ��ʵ��
     */
    public <T> void cacheSingle(Class<T> cls, String cacheId, int id, Object value, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.getEntity(cls.getName());
            CacheKey key = new CacheKey(data, id, CacheKeyType.SingleKey);
            key.setCascade(cascade);
            cacheObject(key, cacheId, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * ���浥������
     * 
     * @param cls ʵ����
     * @param cacheId ��������
     * @param sql SQL���
     * @param params ע���������
     * @param value ʵ��ʵ��
     */
    public <T> void cacheSingle(Class<T> cls, String cacheId, String sql, Object[] params, Object value, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.getEntity(cls.getName());
            CacheKey key = new CacheKey(data, sql, params, CacheKeyType.SingleKey);
            key.setCascade(cascade);
            cacheObject(key, cacheId, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * ����ʵ���б�
     * 
     * @param cls ʵ����
     * @param cacheId ��������
     * @param sql SQL���
     * @param params ע���������
     * @param value ʵ��ʵ��
     */
    public <T> void cacheList(Class<T> cls, String cacheId, String sql, Object[] params, Object value, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.getEntity(cls.getName());
            CacheKey key = new CacheKey(data, sql, params, CacheKeyType.ListKey);
            key.setCascade(cascade);
            cacheObject(key, cacheId, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * �����ҳ����
     * 
     * @param cls ʵ����
     * @param cacheId ��������
     * @param sql SQL���
     * @param params ע���������
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��
     * @param count ��¼����
     * @param value ʵ��ʵ��
     */
    public <T> void cachePage(Class<T> cls, String cacheId, String sql, Object[] params, int page, int pageSize,
        int count, Object value, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.getEntity(cls.getName());
            CacheKey key = new CacheKey(sql, params, page, pageSize, data, CacheKeyType.PageKey, count);
            key.setCascade(cascade);
            cacheObject(key, cacheId, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * �������
     * 
     * @param key ����KEY
     * @param cacheId ��������
     * @param value ����ʵ��
     * @throws Exception
     */
    private void cacheObject(CacheKey key, String cacheId, Object value)
        throws Exception
    {
        CacheFactory factory = cacheFactorys.get(cacheId);
        if (factory != null)
        {
            if (synchronous)
            {
                factory.cacheObject(key, value);
            }
            else
            {
                ThreadHelper.addThread(new CacheAsynchronousThread(factory, key, cacheId, value));
            }
        }
    }
    
    /**
     * ��û������
     * 
     * @param key ����KEY
     * @param cacheId ��������
     * @return �������
     * @throws Exception
     */
    public Object getObject(CacheKey key, String cacheId)
        throws Exception
    {
        CacheFactory factory = cacheFactorys.get(cacheId);
        if (factory != null)
        {
            return factory.getObject(key);
        }
        return null;
    }
    
    /**
     * ��õ�������
     * 
     * @param cls ʵ����
     * @param cacheId ��������
     * @param id ����ֵ
     * @return ��������
     */
    public <T> T getSingle(Class<T> cls, String cacheId, int id, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.parseClass(cls);
            CacheKey key = new CacheKey(data, id, CacheKeyType.SingleKey);
            key.setCascade(cascade);
            return (T)getObject(key, cacheId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public <T> T getSingle(Class<T> cls, String cacheId, String sql, Object[] params, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.parseClass(cls);
            CacheKey key = new CacheKey(data, sql, params, CacheKeyType.SingleKey);
            key.setCascade(cascade);
            return (T)getObject(key, cacheId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public <T> List<T> getList(Class<T> cls, String cacheId, String sql, Object[] params, boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.parseClass(cls);
            CacheKey key = new CacheKey(data, sql, params, CacheKeyType.ListKey);
            key.setCascade(cascade);
            return (List<T>)getObject(key, cacheId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public <T> Page<T> getPage(Class<T> cls, String cacheId, String sql, Object[] params, int page, int pageSize,
        boolean cascade)
    {
        try
        {
            EntityData data = ClassFactory.parseClass(cls);
            CacheKey key = new CacheKey(sql, params, page, pageSize, data, CacheKeyType.PageKey, 0);
            key.setCascade(cascade);
            List<T> list = (List<T>)getObject(key, cacheId);
            if (list == null)
                return null;
            Page<T> ret = new Page<T>(list, key.getCount());
            return ret;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public void flushAll()
    {
        for (CacheFactory cacheFactory : cacheFactorys.values())
        {
            cacheFactory.flush();
        }
        log.debug("Cache Flush");
    }
    
    public boolean isSynchronous()
    {
        return synchronous;
    }
    
}
