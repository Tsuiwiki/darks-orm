/**
 * ����:PluginHelper.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.util;

public class PluginHelper
{
    
    public static boolean isRegJythonPlugin()
    {
        try
        {
            Class<?> cls = Class.forName("org.python.util.PythonInterpreter");
            if (cls == null)
                return false;
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public static boolean isRegSpringRequestPlugin()
    {
        try
        {
            Class<?> cls = Class.forName("org.springframework.web.context.request.RequestContextListener");
            if (cls == null)
                return false;
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public static boolean isRegSpringContextPlugin()
    {
        try
        {
            Class<?> cls = Class.forName("org.springframework.context.ApplicationContext");
            if (cls == null)
                return false;
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
}
