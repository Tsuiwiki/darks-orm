package darks.orm.core.cache.strategy;

import darks.orm.core.cache.CacheKey;
import darks.orm.core.cache.CacheObject;

public class RefCopyStrategy implements CopyStrategy
{
    
    private static final long serialVersionUID = 2321965412872827152L;
    
    /**
     * �����
     * 
     * @param cacheObject �������
     * @param key ��ֵ
     * @param value ֵ
     * @return
     * @throws Exception
     */
    @Override
    public Object read(CacheObject cacheObject, CacheKey key, Object value)
        throws Exception
    {
        if (value == null)
            return null;
        return value;
    }
    
    /**
     * ����д
     * 
     * @param cacheObject �������
     * @param key ��ֵ
     * @param value ֵ
     * @return
     * @throws Exception
     */
    @Override
    public Object write(CacheObject cacheObject, CacheKey key, Object value)
        throws Exception
    {
        return value;
    }
    
}
