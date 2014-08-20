/**
 * ����:QueryException.java
 * ����:������
 * ����ʱ��:2012-5-13
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:��ѯ�쳣
 */

package darks.orm.exceptions;

public class QueryException extends AssistException
{
    
    private static final long serialVersionUID = -6175693598200491665L;
    
    public QueryException()
    {
        super();
    }
    
    public QueryException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public QueryException(String message)
    {
        super(message);
    }
    
    public QueryException(Throwable cause)
    {
        super(cause);
    }
    
}
