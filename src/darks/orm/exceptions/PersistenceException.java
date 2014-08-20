/**
 * ����:PssistenceExcetpion.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.exceptions;

public class PersistenceException extends SessionException
{
    
    private static final long serialVersionUID = 3090422452271823688L;
    
    public PersistenceException()
    {
        super();
    }
    
    public PersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public PersistenceException(String message)
    {
        super(message);
    }
    
    public PersistenceException(Throwable cause)
    {
        super(cause);
    }
    
}
