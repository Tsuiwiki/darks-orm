package darks.orm.core.cache.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.ehcache.CacheException;
import darks.orm.core.cache.CacheContext.CacheKeyType;
import darks.orm.core.cache.CacheController;
import darks.orm.core.cache.CacheKey;
import darks.orm.core.cache.CacheList;
import darks.orm.core.cache.CacheObject;
import darks.orm.core.cache.control.CacheControllerFactroy;
import darks.orm.core.cache.strategy.CopyStrategy;
import darks.orm.core.cache.thread.CacheCheckThread;
import darks.orm.core.config.CacheConfiguration;
import darks.orm.core.config.Configuration;
import darks.orm.core.data.EntityData;
import darks.orm.core.data.FieldData;
import darks.orm.core.data.xml.CacheConfigData;
import darks.orm.core.session.SessionContext;
import darks.orm.log.Logger;
import darks.orm.log.LoggerFactory;
import darks.orm.util.ByteHelper;
import darks.orm.util.ThreadHelper;

/**
 * ����:ApplicationCacheFactory ����:������ ����ʱ��:2012.02.15 �汾:1.0.3 alpha
 * ��Ȩ:CopyRight(c)2012 ������ ����Ŀ��������Ȩ������������ ����:Ӧ�ü���Χ���湤��
 */
public class ApplicationCacheFactory implements CacheFactory
{
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationCacheFactory.class);
    
    private static volatile ApplicationCacheFactory instace = null;
    
    private CacheController controller = null;
    
    private Queue<CacheKey> keysList = null;
    
    private ConcurrentMap<CacheKey, Object> entityMap = null;
    
    private ConcurrentMap<CacheKey, CacheList> listMap = new ConcurrentHashMap<CacheKey, CacheList>(64);
    
    private static final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    
    private static final Lock rlock = rwlock.readLock();
    
    private static final Lock wlock = rwlock.writeLock();
    
    private CacheConfigData appConfigData;
    
    private CopyStrategy copyStrategy;
    
    private ApplicationCacheFactory()
    {
        Configuration cfg = SessionContext.getConfigure();
        CacheConfiguration cacheCfg = cfg.getCacheConfig();
        if (cacheCfg == null)
            return;
        appConfigData = cacheCfg.getAppCacheData();
        if (appConfigData == null)
            return;
        int initnum = 0;
        int max = appConfigData.getMaxObject();
        if (max < 1000)
        {
            initnum = max * 2 / 3;
        }
        else if (max < 10000)
        {
            initnum = max * 1 / 5;
        }
        else if (max > 10000)
        {
            initnum = max * 1 / 100;
        }
        keysList = new ConcurrentLinkedQueue<CacheKey>();
        entityMap = new ConcurrentHashMap<CacheKey, Object>(initnum);
        controller = CacheControllerFactroy.getCacheController(appConfigData, keysList, entityMap, listMap);
        if (!appConfigData.isEternal())
        {
            Runnable checkThread = new CacheCheckThread(keysList, entityMap, listMap, appConfigData);
            ThreadHelper.addThread(checkThread);
        }
        
        copyStrategy = appConfigData.getCopyStrategy();
    }
    
    public static CacheFactory getInstance()
    {
        Configuration cfg = SessionContext.getConfigure();
        if (cfg == null)
            return null;
        CacheConfiguration cacheCfg = cfg.getCacheConfig();
        if (cacheCfg == null)
            return null;
        CacheConfigData data = cacheCfg.getAppCacheData();
        if (data == null)
            return null;
        if (instace == null)
        {
            instace = new ApplicationCacheFactory();
        }
        return instace;
    }
    
    /**
     * �������
     * 
     * @param key ����KEY
     * @param obj ����ʵ��
     * @throws Exception
     */
    public void cacheObject(CacheKey key, Object obj)
        throws CacheException
    {
        if (key == null || obj == null)
            return;
        EntityData data = key.getData();
        // �������Ƿ�����л�
        if (data == null || !ByteHelper.isSerializable(data))
            return;
        
        wlock.lock();
        try
        {
            if (obj instanceof List && !appConfigData.isEntirety())
            {
                // ��Ϊ���������ʱ,�����б����
                List list = (List)obj;
                if (list.size() > appConfigData.getMaxObject())
                {
                    throw new CacheException("the size of list cacheing is flowover the max limit");
                }
                FieldData pkfdata = data.getPkField();
                Map<CacheKey, Object> map = new ConcurrentHashMap<CacheKey, Object>();
                List<CacheKey> clist = new ArrayList<CacheKey>();
                // �����б����ж���
                for (Object ob : list)
                {
                    int piId = (Integer)pkfdata.getValue(ob);
                    CacheKey newkey = new CacheKey(data, piId, CacheKeyType.SingleKey);
                    Object value = new CacheObject(copyStrategy, newkey, ob);
                    // ���õ��Ȳ��Դ��뻺��
                    controller.cacheObject(newkey, value);
                    map.put(newkey, value);
                    clist.add(newkey);
                }
                CacheList cacheList = new CacheList(map, clist, key.getCount());
                listMap.put(key, cacheList);
            }
            else
            {
                // �������
                Object value = new CacheObject(copyStrategy, key, obj);
                // ���õ��Ȳ��Դ��뻺��
                controller.cacheObject(key, value);
            }
        }
        catch (Exception e)
        {
            throw new CacheException(e.getMessage(), e);
        }
        finally
        {
            wlock.unlock();
        }
    }
    
    /**
     * ��ö���
     * 
     * @param key ����KEY
     * @return ����ʵ��
     * @throws Exception
     */
    public Object getObject(CacheKey key)
        throws Exception
    {
        if (key.getData() == null)
            return null;
        if (!key.getData().isSerializable())
            return null;
        rlock.lock();
        try
        {
            // ����Ƿ��б�����ȡ��
            if ((key.getCacheKeyType() == CacheKeyType.ListKey || key.getCacheKeyType() == CacheKeyType.PageKey)
                && !appConfigData.isEntirety())
            {
                CacheList cacheList = listMap.get(key);
                if (cacheList == null)
                    return null;
                key.setCount(cacheList.getCount());
                List<CacheKey> clist = cacheList.getList();
                List<Object> list = new ArrayList<Object>(clist.size());
                // ����ȡ����������
                for (CacheKey ckey : clist)
                {
                    CacheObject val = (CacheObject)controller.getObject(ckey);
                    if (val == null)
                        return null;
                    val.setLastIdleTime(System.currentTimeMillis());
                    list.add(val.getObject());
                }
                return list;
            }
            // ����ȡ������
            CacheObject value = (CacheObject)controller.getObject(key);
            if (value == null)
                return null;
            key.setCount(value.getKey().getCount());
            value.setLastIdleTime(System.currentTimeMillis());
            return value.getObject();
        }
        finally
        {
            rlock.unlock();
        }
    }
    
    /**
     * �������Ƿ����ӵ�д˻���KEY�Ķ���
     * 
     * @param key ����KEY
     * @return true���� false������
     */
    public boolean containKey(CacheKey key)
    {
        rlock.lock();
        try
        {
            if ((key.getCacheKeyType() == CacheKeyType.ListKey || key.getCacheKeyType() == CacheKeyType.PageKey)
                && !appConfigData.isEntirety())
            {
                return listMap.containsKey(key);
            }
            return entityMap.containsKey(key);
        }
        finally
        {
            rlock.unlock();
        }
    }
    
    public void flush()
    {
        wlock.lock();
        try
        {
            listMap.clear();
            keysList.clear();
            entityMap.clear();
        }
        finally
        {
            wlock.unlock();
        }
    }
    
    @Override
    public void debug()
    {
        System.out.println(keysList.size() + " " + entityMap.size() + " " + listMap.size());
        for (CacheKey key : keysList)
        {
            System.out.println(key.getId() + " " + key.getCacheKeyType());
        }
    }
}
