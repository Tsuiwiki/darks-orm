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

package darks.orm.core.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import darks.orm.annotation.Query;
import darks.orm.annotation.Query.QueryType;
import darks.orm.app.SqlSession;
import darks.orm.core.data.MethodData;
import darks.orm.core.factory.ClassFactory;
import darks.orm.core.factory.SqlSessionFactory;
import darks.orm.exceptions.ClassReflectException;
import darks.orm.log.Logger;
import darks.orm.log.LoggerFactory;
import darks.orm.util.StringHelper;

/**
 * Method interrupter used to Intercept {@linkplain darks.orm.annotation.Query Query} annotation
 */
@SuppressWarnings("unchecked")
public class QueryMethodInterceptor implements MethodInterceptor, Serializable
{
    
    private transient static final Logger logger = LoggerFactory.getLogger(QueryMethodInterceptor.class);
    
    private static final long serialVersionUID = 1322619765770865185L;
    
    // private boolean isOwn=false;
    
    public QueryMethodInterceptor()
    {
    }
    
    /**
     * ����������
     */
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable
    {
        
        try
        {
            
            Query qy = (Query)method.getAnnotation(Query.class);
            if (qy == null)
            {
                return proxy.invokeSuper(obj, args);
            }
            else
            {
                return this.QueryInterceptor(obj, method, args, proxy);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * �Զ����ѯ����
     * 
     * @param obj ʵ�����
     * @param method ���ط���
     * @param args ��������
     * @param proxy ��������
     * @return ���ض���
     * @throws Throwable
     */
    private Object QueryInterceptor(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable
    {
        
        Query qy = (Query)method.getAnnotation(Query.class);
        String sql = qy.SQL();
        String[] params = qy.paramType();
        Class cobj = obj.getClass();
        
        MethodData methodData = ClassFactory.getMethod(method.toGenericString());
        List<Object> newargs = new ArrayList<Object>(params.length);
        for (String s : params)
        {
            if (s.startsWith("#"))
            { // ��ע�����Ϊ��������
                int index = getArgumentIndex(methodData, s);
                if (index > 0)
                {
                    newargs.add(args[index - 1]);
                }
                else
                {
                    continue;
                }
            }
            else
            { // ע�����Ϊʵ������
                String mtName = "get" + StringHelper.upperHeadWord(s);
                Method mt = cobj.getMethod(mtName);
                if (mt == null)
                {
                    throw new ClassReflectException("There is no method named '" + mtName + "' in class '" + cobj + "'");
                }
                Object val = mt.invoke(obj);
                newargs.add(val);
            }
        }
        // ��ûỰ
        boolean isOwn = false;
        if (SqlSessionFactory.isCurrentSessionClosed())
        {
            isOwn = true;
        }
        SqlSession session = SqlSessionFactory.getSession();
        
        Class c = qy.resultType();
        if (qy.queryType() == QueryType.ListType)
        {
            List list = session.queryList(c, sql, newargs.toArray());
            if (isOwn)
            {
                session.close();
            }
            return list;
        }
        else if (qy.queryType() == QueryType.SingleType)
        {
            Object ob = null;
            ob = session.queryBySQL(c, sql, newargs.toArray());
            if (isOwn)
            {
                session.close();
            }
            return ob;
        }
        
        return null;
    }
    
    /**
     * ��ò�������
     * 
     * @param methodData ��������ʵ��
     * @param param ������/����λ��
     * @return ����
     * @throws Exception
     */
    public int getArgumentIndex(MethodData methodData, String param)
        throws Exception
    {
        try
        {
            int index = -1;
            String argName = param.substring(1);
            if (methodData != null)
            {
                index = methodData.getArgumentIndex(argName);
            }
            if (index < 0)
            {
                index = Integer.parseInt(argName);
            }
            return index;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}