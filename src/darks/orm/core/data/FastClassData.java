/**
 * ����:FastClassData.java
 * ����:������
 * ����ʱ��:2012-5-3
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
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
