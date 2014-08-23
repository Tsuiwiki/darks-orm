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

package darks.orm.core.aspect.jclass;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import darks.orm.app.SqlSession;
import darks.orm.app.QueryEnumType;
import darks.orm.core.aspect.QueryAspectAdapter;
import darks.orm.core.data.xml.AspectData;
import darks.orm.core.data.xml.QueryAspectWrapper;
import darks.orm.util.ReflectHelper;

public class JavaClassQueryAspect extends QueryAspectAdapter
{
    
    private Class<?> aspectClass = null;
    
    private Object aepectObj = null;
    
    public JavaClassQueryAspect()
    {
        
    }
    
    @Override
    public boolean beforeInvoke(SqlSession dao, AspectData aspectData, QueryAspectWrapper queryWrapper,
        QueryEnumType queryEnumType)
    {
        if (aspectData == null)
            return true;
        String className = aspectData.getClassName();
        return this.invoke("beforeInvoke", className, dao, aspectData, queryWrapper, queryEnumType);
    }
    
    @Override
    public boolean afterInvoke(SqlSession dao, AspectData aspectData, QueryAspectWrapper queryWrapper,
        QueryEnumType queryEnumType)
    {
        if (aspectData == null)
            return true;
        String className = aspectData.getClassName();
        return this.invoke("afterInvoke", className, dao, aspectData, queryWrapper, queryEnumType);
    }
    
    /**
     * �����࣬����ʼ����ʵ��
     * 
     * @param className ��ȫ��
     * @return ��
     */
    private Class<?> getJavaClass(String className)
    {
        if (aspectClass != null && aepectObj != null)
            return aspectClass;
        try
        {
            aspectClass = Class.forName(className);
            aepectObj = ReflectHelper.newFastInstance(aspectClass);
            return aspectClass;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * �������AOP�෽��
     * 
     * @param methodName ��������
     * @param className ��ȫ��
     * @param dao BaseDAO
     * @param aspectData AOP xml�����ļ�����
     * @param queryWrapper ��ѯ����
     * @param queryEnumType ��ѯ����
     * @return �Ƿ����ִ��
     */
    private boolean invoke(String methodName, String className, SqlSession dao, AspectData aspectData,
        QueryAspectWrapper queryWrapper, QueryEnumType queryEnumType)
    {
        if (getJavaClass(className) == null || aepectObj == null)
            return false;
        Class<?>[] params = {SqlSession.class, AspectData.class, QueryAspectWrapper.class, QueryEnumType.class};
        try
        {
            FastClass fastClass = ReflectHelper.getFastClass(aspectClass);
            FastMethod mt = ReflectHelper.getFastMethod(fastClass, methodName, params);
            if (mt == null)
                return false;
            Boolean ret = (Boolean)mt.invoke(aepectObj, new Object[] {dao, aspectData, queryWrapper, queryEnumType});
            return ret;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}