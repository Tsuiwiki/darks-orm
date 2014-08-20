package darks.orm.core.factory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import darks.orm.app.Page;
import darks.orm.app.SqlSession;
import darks.orm.app.QueryEnumType;
import darks.orm.core.config.Configuration;
import darks.orm.core.config.sqlmap.SqlMapConfiguration;
import darks.orm.core.data.xml.DDLData;
import darks.orm.core.data.xml.DMLData;
import darks.orm.core.data.xml.DMLQueryData;
import darks.orm.core.data.xml.DMLUpdateData;
import darks.orm.core.data.xml.InterfaceMethodData;
import darks.orm.core.data.xml.QueryAspectWrapper;
import darks.orm.core.data.xml.SimpleAspectWrapper;
import darks.orm.core.data.xml.DMLData.DMLType;
import darks.orm.core.data.xml.DMLQueryData.DMLQueryDataType;
import darks.orm.core.executor.SqlMapExecutor;
import darks.orm.core.session.SessionContext;
import darks.orm.exceptions.ConfigException;
import darks.orm.exceptions.SqlMapQueryException;
import darks.orm.exceptions.SqlMapUpdateException;
import darks.orm.log.Logger;
import darks.orm.log.LoggerFactory;
import darks.orm.util.LogHelper;
import darks.orm.util.SqlHelper;
import org.dom4j.DocumentException;

public class SqlMapSingletonFactory
{
    
    private static final Logger logger = LoggerFactory.getLogger(SqlMapSingletonFactory.class);
    
    private SqlMapConfiguration sqlMapConfig = null;
    
    private volatile static SqlMapSingletonFactory instance = null;
    
    public static SqlMapSingletonFactory getInstance()
    {
        if (instance == null)
        {
            instance = new SqlMapSingletonFactory();
        }
        return instance;
    }
    
    private SqlMapSingletonFactory()
        throws ConfigException
    {
        try
        {
            SessionContext.build();
            Configuration cfg = SessionContext.getConfigure();
            sqlMapConfig = cfg.getSqlMapConfig();
            sqlMapConfig.loadSqlMap(cfg.getSqlMapsPath());
        }
        catch (DocumentException e)
        {
            throw new ConfigException("InternalContext build config parse fail", e);
        }
    }
    
    public DMLData getDMLData(String id)
    {
        return sqlMapConfig.getDMLData(id);
    }
    
    /**
     * ִ��DDL���
     */
    public void executeDDLMap()
    {
        ConcurrentMap<String, DDLData> ddlMap = sqlMapConfig.getDdlMap();
        TableGeneratorFactory.mergeTable(ddlMap);
    }
    
    /**
     * SQLMAP����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     */
    public Object update(SqlSession session, String id, Object[] params)
        throws SqlMapUpdateException
    {
        DMLData dmlData = null;
        if (id == null)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Update id is not allowed to be null",
                SqlMapUpdateException.class);
        }
        else
        {
            dmlData = sqlMapConfig.getDMLData(id);
        }
        if (dmlData == null)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Update the id ('" + id + "') does not exists",
                SqlMapUpdateException.class);
        }
        if (dmlData != null && dmlData.getType() != DMLType.Update)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Update Type is not DMLType.Update",
                SqlMapUpdateException.class);
        }
        DMLUpdateData updateData = dmlData.getUpdateData();
        if (updateData == null)
        {
            LogHelper.except(logger, "SqlMapSingletonFactory::Update updateData is null", SqlMapUpdateException.class);
        }
        String sql = updateData.getSql();
        if (sql == null || "".equals(sql))
        {
            LogHelper.except(logger, "SqlMapSingletonFactory::Update sql is null/empty", SqlMapUpdateException.class);
        }
        boolean isClose = false;
        if (session == null)
        {
            session = SqlSessionFactory.getSession(true);
            isClose = true;
        }
        try
        {
            InterfaceMethodData idata = ClassFactory.getInterfaceClass(id);
            Map<String, Integer> dataMap = null;
            if (idata != null)
                dataMap = idata.getArgumentsMap();
            params = SqlHelper.buildSqlParams(sql, dataMap, params);
            sql = SqlHelper.filterSql(sql);
            
            if (updateData.getAspectData() == null)
            {
                session.executeUpdate(sql, params);
            }
            else
            {
                SimpleAspectWrapper sqlMapAspectData = new SimpleAspectWrapper(sql, params);
                SqlMapExecutor exec = ExecutorFactory.getSqlMapUpdateExecutor(session, updateData, sqlMapAspectData);
                if (exec.initialize())
                {
                    return exec.invoke();
                }
            }
        }
        catch (SQLException e)
        {
            throw new SqlMapUpdateException("SqlMapSingletonFactory::Update sql excetption", e);
        }
        finally
        {
            if (isClose)
                session.close();
        }
        return null;
    }
    
    /**
     * ���ݲ�ѯ������ͽ���SQLMAP��ѯ
     * 
     * @param session BASEDAOʵ��
     * @param id SQLMAP���
     * @param queryEnumType ��ѯ�������ö��
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ����
     * @throws Exception
     */
    public Object query(SqlSession session, String id, QueryEnumType queryEnumType, int page, int pageSize,
        Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        if (id == null)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Query id is not allowed to be null",
                SqlMapQueryException.class);
        }
        DMLData dmlData = sqlMapConfig.getDMLData(id);
        return query(session, id, dmlData, queryEnumType, page, pageSize, values, params);
    }
    
    private Object query(SqlSession session, String id, DMLData dmlData, QueryEnumType queryEnumType, int page,
        int pageSize, Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        if (dmlData == null)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Query the id ('" + id + "') does not exists",
                SqlMapQueryException.class);
        }
        if (dmlData.getType() != DMLType.Query)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Query Type is not DMLType.Query",
                SqlMapQueryException.class);
        }
        DMLQueryData queryData = dmlData.getQueryData();
        if (queryData == null)
        {
            LogHelper.except(logger, "SqlMapSingletonFactory::Query queryData is null", SqlMapQueryException.class);
        }
        QueryEnumType qtype = queryData.getQueryType();
        if (queryData.getQueryType() == QueryEnumType.Auto)
        {
            qtype = queryEnumType;
        }
        if (qtype == QueryEnumType.Object)
        {
            return queryObject(session, id, values, params);
        }
        else if (qtype == QueryEnumType.List)
        {
            return queryList(session, id, values, params);
        }
        else if (qtype == QueryEnumType.Page)
        {
            return queryPageList(session, id, page, pageSize, values, params);
        }
        return null;
    }
    
    /**
     * SQLMAP��ѯ��������
     * 
     * @param session BASEDAOʵ��
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��������
     * @throws Exception
     */
    public Object queryObject(SqlSession session, String id, Object[] params)
        throws SqlMapQueryException
    {
        return queryObject(session, id, null, params);
    }
    
    /**
     * SQLMAP��ѯ��������
     * 
     * @param session BASEDAOʵ��
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ��������
     * @throws Exception
     */
    public Object queryObject(SqlSession session, String id, Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        return queryForType(session, QueryEnumType.Object, id, values, params);
    }
    
    /**
     * SQLMAP��ѯ�б����
     * 
     * @param session BASEDAOʵ��
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return �б����
     * @throws Exception
     */
    public List<?> queryList(SqlSession session, String id, Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        return (List<?>)queryForType(session, QueryEnumType.List, id, values, params);
    }
    
    /**
     * SQLMAP��ѯ��ҳ����
     * 
     * @param session BASEDAOʵ��
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ��ҳ����
     * @throws Exception
     */
    public Page<?> queryPageList(SqlSession session, String id, int page, int pageSize, Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        return (Page<?>)queryForType(session, QueryEnumType.Page, id, page, pageSize, values, params);
    }
    
    /**
     * ���ݲ�ѯ���ͽ���SQLMAP��ѯ
     * 
     * @param session BASEDAOʵ��
     * @param queryEnumType ��ѯ����ö��
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����
     * @return ��ѯ�������
     * @throws Exception
     */
    public Object queryForType(SqlSession session, QueryEnumType queryEnumType, String id, Object[] values,
        Object[] params)
        throws SqlMapQueryException
    {
        return queryForType(session, queryEnumType, id, 0, 0, values, params);
    }
    
    /**
     * ���ݲ�ѯ���ͽ���SQLMAP��ѯ
     * 
     * @param session BASEDAOʵ��
     * @param queryEnumType ��ѯ����ö��
     * @param id SQLMAP���
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��С
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����
     * @return ��ѯ�������
     */
    public Object queryForType(SqlSession session, QueryEnumType queryEnumType, String id, int page, int pageSize,
        Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        if (id == null)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Query id is not allowed to be null",
                SqlMapUpdateException.class);
        }
        DMLData dmlData = sqlMapConfig.getDMLData(id);
        if (dmlData == null)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Query the id ('" + id + "') does not exists",
                SqlMapUpdateException.class);
        }
        if (dmlData.getType() != DMLType.Query)
        {
            LogHelper.except(logger,
                "SqlMapSingletonFactory::Query Type is not DMLType.Query",
                SqlMapQueryException.class);
        }
        DMLQueryData queryData = dmlData.getQueryData();
        if (queryData == null)
        {
            LogHelper.except(logger, "SqlMapSingletonFactory::Query queryData is null", SqlMapQueryException.class);
        }
        String sql = queryData.getSql(values);
        if (sql == null)
        {
            LogHelper.except(logger, "[SQLMAP]sql does not exists", SqlMapQueryException.class);
        }
        String attr = queryData.getAttribute();
        String alias = queryData.getAlias();
        
        try
        {
            InterfaceMethodData idata = ClassFactory.getInterfaceClass(id);
            Map<String, Integer> dataMap = null;
            if (idata != null)
                dataMap = idata.getArgumentsMap();
            params = SqlHelper.buildSqlParams(sql, dataMap, params);
        }
        catch (SQLException e)
        {
            throw new SqlMapQueryException(e);
        }
        sql = SqlHelper.filterSql(sql);
        if (sql.indexOf("?") < 0
            && queryData.getAspectData() == null
            && (queryData.getQueryDataType() == DMLQueryDataType.Select || queryData.getQueryDataType() == DMLQueryDataType.Constitute))
            params = null;
        
        QueryAspectWrapper queryWrapper =
            new QueryAspectWrapper(sql, params, values, page, pageSize, alias, attr, queryData.isAutoCascade());
        SqlMapExecutor exec =
            ExecutorFactory.getSqlMapQueryExecutor(session, dmlData, queryData, queryWrapper, queryEnumType);
        if (exec.initialize())
        {
            try
            {
                return exec.invoke();
            }
            catch (SQLException e)
            {
                throw new SqlMapQueryException("SqlMapExecutor execute error", e);
            }
        }
        return null;
        
    }
    
    public SqlMapConfiguration getSqlmapconfig()
    {
        return sqlMapConfig;
    }
    
}
