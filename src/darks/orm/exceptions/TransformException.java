/**
 * ����:SessionException.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.exceptions;

public class TransformException extends SessionException
{
    
    private static final long serialVersionUID = -2988528111588417451L;
    
    public TransformException()
    {
        super();
    }
    
    public TransformException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public TransformException(String message)
    {
        super(message);
    }
    
    public TransformException(Throwable cause)
    {
        super(cause);
    }
    
}
