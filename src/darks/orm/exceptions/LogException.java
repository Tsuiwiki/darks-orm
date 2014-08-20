/**
 * ����:LogException.java
 * ����:������
 * ����ʱ��:2012-5-3
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.exceptions;

public class LogException extends AssistException
{
    
    private static final long serialVersionUID = -5018489305192232704L;
    
    public LogException()
    {
    }
    
    public LogException(String message)
    {
        super(message);
    }
    
    public LogException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public LogException(Throwable cause)
    {
        super(cause);
    }
}
