package darks.orm.core.cache.strategy;

import java.io.Serializable;

import darks.orm.core.cache.CacheKey;
import darks.orm.core.cache.CacheObject;

public interface CopyStrategy extends Serializable
{
    
    /**
     * ����д
     * 
     * @param cacheObject �������
     * @param key ��ֵ
     * @param value ֵ
     * @return
     * @throws Exception
     */
    public Object write(CacheObject cacheObject, CacheKey key, Object value)
        throws Exception;
    
    /**
     * �����
     * 
     * @param cacheObject �������
     * @param key ��ֵ
     * @param value ֵ
     * @return
     * @throws Exception
     */
    public Object read(CacheObject cacheObject, CacheKey key, Object value)
        throws Exception;
    
}
