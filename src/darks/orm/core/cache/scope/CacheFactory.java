package darks.orm.core.cache.scope;

import darks.orm.core.cache.CacheKey;

public interface CacheFactory
{
    
    /**
     * �������
     * 
     * @param key ����KEY
     * @param obj ����ʵ��
     * @throws Exception
     */
    public void cacheObject(CacheKey key, Object obj)
        throws Exception;
    
    /**
     * ��ö���
     * 
     * @param key ����KEY
     * @return ����ʵ��
     * @throws Exception
     */
    public Object getObject(CacheKey key)
        throws Exception;
    
    /**
     * �������Ƿ����ӵ�д˻���KEY�Ķ���
     * 
     * @param key ����KEY
     * @return true���� false������
     */
    public boolean containKey(CacheKey key);
    
    public void flush();
    
    public void debug();
}
