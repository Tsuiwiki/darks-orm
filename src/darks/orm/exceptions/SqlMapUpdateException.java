/**
 * ����:SqlMapUpdateException.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.exceptions;

public class SqlMapUpdateException extends UpdateException
{
    
    private static final long serialVersionUID = -6594243214035691748L;
    
    public SqlMapUpdateException()
    {
        super();
    }
    
    public SqlMapUpdateException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public SqlMapUpdateException(String message)
    {
        super(message);
    }
    
    public SqlMapUpdateException(Throwable cause)
    {
        super(cause);
    }
    
}
