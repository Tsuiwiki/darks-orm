/**
 * 
 * Copyright 2014 The Darks ORM Project (Liu lihua)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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