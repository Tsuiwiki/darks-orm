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

package darks.orm.core.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastConstructor;
import net.sf.cglib.reflect.FastMethod;

public class FastClassData
{
    
    /**
     * ԭʼ�� ��ֵ
     */
    private Class<?> clazz;
    
    /**
     * ������
     */
    private FastClass fastClass;
    
    /**
     * ԭʼ���캯��
     */
    private Constructor<?> constructor;
    
    /**
     * ���ٹ��캯��
     */
    private FastConstructor fastConstructor;
    
    /**
     * ���ٷ�������
     */
    private Map<String, FastMethod> fastMethodMap = new ConcurrentHashMap<String, FastMethod>();
    
    public FastClassData()
    {
        
    }
    
    /**
     * ���ԭʼ��
     * 
     * @return
     */
    public Class<?> getOriginalClass()
    {
        return clazz;
    }
    
    public void setOriginalClass(Class<?> clazz)
    {
        this.clazz = clazz;
    }
    
    public FastClass getFastClass()
    {
        return fastClass;
    }
    
    public void setFastClass(FastClass fastClass)
    {
        this.fastClass = fastClass;
    }
    
    public FastConstructor getFastConstructor()
    {
        return fastConstructor;
    }
    
    public void setFastConstructor(FastConstructor fastConstructor)
    {
        this.fastConstructor = fastConstructor;
    }
    
    public Constructor<?> getConstructor()
    {
        return constructor;
    }
    
    public void setConstructor(Constructor<?> constructor)
    {
        this.constructor = constructor;
    }
    
    /**
     * ��ӿ��ٷ���
     * 
     * @param method ԭʼ����
     * @param fastMethod ���ٷ���
     */
    public void addFastMethod(Method method, FastMethod fastMethod)
    {
        fastMethodMap.put(method.getName(), fastMethod);
    }
    
    /**
     * ��ӿ��ٷ���
     * 
     * @param method ԭʼ����
     * @param fastMethod ���ٷ���
     */
    public FastMethod getFastMethod(String methodName)
    {
        return fastMethodMap.get(methodName);
    }
}