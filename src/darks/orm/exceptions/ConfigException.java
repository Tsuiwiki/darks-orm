/**
 * ����:ConfigException.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.exceptions;

public class ConfigException extends AssistException
{
    
    private static final long serialVersionUID = -5386924734523961152L;
    
    public ConfigException()
    {
        super();
    }
    
    public ConfigException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ConfigException(String message)
    {
        super(message);
    }
    
    public ConfigException(Throwable cause)
    {
        super(cause);
    }
    
}
