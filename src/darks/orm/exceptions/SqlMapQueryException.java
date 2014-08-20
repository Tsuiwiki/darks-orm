/**
 * ����:SqlMapQueryException.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:sqlmap��ѯ�쳣
 */

package darks.orm.exceptions;

public class SqlMapQueryException extends QueryException
{
    
    private static final long serialVersionUID = 3828227271511439855L;
    
    public SqlMapQueryException()
    {
        super();
    }
    
    public SqlMapQueryException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public SqlMapQueryException(String message)
    {
        super(message);
    }
    
    public SqlMapQueryException(Throwable cause)
    {
        super(cause);
    }
    
}
