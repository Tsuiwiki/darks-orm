/**
 * ����:LogException.java
 * ����:������
 * ����ʱ��:2012-5-3
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.exceptions;

public class ClassReflectException extends SessionException
{
    
    private static final long serialVersionUID = 7451647047467286499L;
    
    public ClassReflectException()
    {
    }
    
    public ClassReflectException(String message)
    {
        super(message);
    }
    
    public ClassReflectException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ClassReflectException(Throwable cause)
    {
        super(cause);
    }
}
