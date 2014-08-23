
package darks.orm.core.executor;

import java.sql.SQLException;

import darks.orm.app.QueryEnumType;
import darks.orm.app.SqlSession;
import darks.orm.core.factory.SqlSessionFactory;
import darks.orm.exceptions.SqlMapQueryException;

public class SqlMapManualCascadeQueryExecutor extends SqlMapExecutorAdapter
{
    
    private SqlSession session;
    
    private Class<?> resultClass;
    
    private String sql;
    
    private Object[] params;
    
    private QueryEnumType queryEnumType;
    
    private String cacheId;
    
    private int page;
    
    private int pageSize;
    
    private boolean autoCache;
    
    private String attr;
    
    private String alias;
    
    public SqlMapManualCascadeQueryExecutor()
    {
        
    }
    
    public SqlMapManualCascadeQueryExecutor(SqlSession session, Class<?> resultClass, String sql, Object[] params,
        QueryEnumType queryEnumType, String cacheId, int page, int pageSize, boolean autoCache, String attr,
        String alias)
    {
        this.session = session;
        this.resultClass = resultClass;
        this.sql = sql;
        this.params = params;
        this.queryEnumType = queryEnumType;
        this.cacheId = cacheId;
        this.page = page;
        this.pageSize = pageSize;
        this.autoCache = autoCache;
        this.attr = attr;
        this.alias = alias;
    }
    
    @Override
    public Object bodyInvoke()
        throws SQLException
    {
        boolean isClose = false;
        if (session == null || (session.isInited() && session.isClosed()))
        {
            // ���SQL�Ự
            session = SqlSessionFactory.getSession(true);
            isClose = true;
        }
        
        if (cacheId == null || "".equals(cacheId))
            autoCache = true;
        if (attr == null || alias == null)
        {
            throw new SqlMapQueryException("SqlMapFactory::executeQuery Attribute or Alias of cascade query is null");
        }
        // ������������
        String[] entityName = null;
        if (attr.indexOf(",") >= 0)
        {
            entityName = attr.split(",");
        }
        else
        {
            entityName = new String[] {attr};
        }
        // ��������������
        String[] aliass = null;
        if (alias.indexOf(",") >= 0)
        {
            aliass = alias.split(",");
        }
        else
        {
            aliass = new String[] {alias};
        }
        try
        {
            // ִ�в�ѯ
            if (queryEnumType == QueryEnumType.Object)
            { // ��ѯ����
                if (autoCache) // �Զ�����
                    return session.queryCascadeObject(resultClass, sql, entityName, aliass, params);
                else
                    return session.queryCacheCascadeObject(resultClass, cacheId, sql, entityName, aliass, params);
            }
            else if (queryEnumType == QueryEnumType.List || queryEnumType == QueryEnumType.Auto)
            { // ��ѯ�б�,�Զ���ѯĬ��Ϊ�б��ѯ
                if (autoCache) // �Զ�����
                    return session.queryCascadeList(resultClass, sql, entityName, aliass, params);
                else
                    return session.queryCacheCascadeList(resultClass, cacheId, sql, entityName, aliass, params);
            }
            else if (queryEnumType == QueryEnumType.Page)
            { // ��ѯ��ҳ�б�
                if (autoCache) // �Զ�����
                    return session.queryCascadePageList(resultClass, sql, page, pageSize, entityName, aliass, params);
                else
                    return session.queryCacheCascadePageList(resultClass,
                        cacheId,
                        sql,
                        page,
                        pageSize,
                        entityName,
                        aliass,
                        params);
            }
        }
        catch (Exception e)
        {
            throw new SqlMapQueryException(e);
        }
        finally
        {
            if (isClose)
                session.close();
        }
        return null;
    }
    
}
