/**
 * ����:SqlMapSessionSupport.java
 * ����:������
 * ����ʱ��:����01:55:23
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.spring;

import darks.orm.app.SqlSession;
import darks.orm.core.factory.SqlSessionFactory;

public abstract class SqlMapSessionSupport
{
    
    private SqlSession session;
    
    public SqlMapSessionSupport()
    {
        try
        {
            session = SqlSessionFactory.getSession();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public SqlSession getSession()
    {
        return session;
    }
}
