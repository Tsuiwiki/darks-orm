/**
 * ����:SqlMapExecutor.java
 * ����:������
 * ����ʱ��:2012-6-21
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.core.executor;

import java.sql.SQLException;

public interface SqlMapExecutor
{
    
    public boolean initialize();
    
    public boolean beforeInvoke();
    
    public Object bodyInvoke()
        throws SQLException;
    
    public boolean afterInvoke();
    
    public Object invoke()
        throws SQLException;
}
