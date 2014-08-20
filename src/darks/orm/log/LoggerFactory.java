/**
 * ����:LoggerFactroy.java
 * ����:������
 * ����ʱ��:2012-5-3
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.log;

import java.lang.reflect.Constructor;
import darks.orm.exceptions.LogException;
import darks.orm.util.LogHelper;

public class LoggerFactory
{
    
    private static volatile Constructor<? extends Logger> logConstructor;
    
    static
    {
        setLogConstructor("darks.orm.log.impl.Slf4jLogger");
        setLogConstructor("darks.orm.log.impl.Log4jLogger");
        setLogConstructor("darks.orm.log.impl.Jdk14Logger");
        setLogConstructor("darks.orm.log.impl.StdIOLogger");
        setLogConstructor("darks.orm.log.impl.NoOpLogger");
    }
    
    public static Logger getLogger(Class<?> aClass)
    {
        return getLogger(aClass.getName());
    }
    
    public static Logger getLogger(String logger)
    {
        try
        {
            return (Logger)logConstructor.newInstance(new Object[] {logger});
        }
        catch (Throwable t)
        {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void setLogConstructor(String className)
    {
        if (logConstructor == null)
        {
            try
            {
                Class<?> implClass = Class.forName(className);
                Constructor<?> candidate = implClass.getConstructor(new Class[] {String.class});
                candidate.newInstance(new Object[] {LoggerFactory.class.getName()});
                LogHelper.println("Logging initialized using '" + className + "' adapter.");
                logConstructor = (Constructor<? extends Logger>)candidate;
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
}
