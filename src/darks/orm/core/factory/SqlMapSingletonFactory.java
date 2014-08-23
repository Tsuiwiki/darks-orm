/**
 * 
 * Copyright 2014 The Darks ORM Project (Liu lihua)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package darks.orm.core.factory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.dom4j.DocumentException;

import darks.orm.app.Page;
import darks.orm.app.QueryEnumType;
import darks.orm.app.SqlSession;
import darks.orm.core.config.Configuration;
import darks.orm.core.config.sqlmap.SqlMapConfiguration;
import darks.orm.core.data.xml.DDLData;
import darks.orm.core.data.xml.DMLData;
import darks.orm.core.data.xml.DMLData.DMLType;
import darks.orm.core.data.xml.DMLQueryData;
import darks.orm.core.data.xml.DMLQueryData.DMLQueryDataType;
import darks.orm.core.data.xml.DMLUpdateData;
import darks.orm.core.data.xml.InterfaceMethodData;
import darks.orm.core.data.xml.QueryAspectWrapper;
import darks.orm.core.data.xml.SimpleAspectWrapper;
import darks.orm.core.executor.SqlMapExecutor;
import darks.orm.core.session.SessionContext;
import darks.orm.exceptions.ConfigException;
import darks.orm.exceptions.SqlMapQueryException;
import darks.orm.exceptions.SqlMapUpdateException;
import darks.orm.log.Logger;
import darks.orm.log.LoggerFactory;
import darks.orm.util.SqlHelper;

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
            throw new SqlMapUpdateException("SqlMapSingletonFactory::Update id is not allowed to be null");
        }
        else
        {
            dmlData = sqlMapConfig.getDMLData(id);
        }
        if (dmlData == null)
        {
            throw new SqlMapUpdateException("Update the id ('" + id + "') does not exists");
        }
        if (dmlData != null && dmlData.getType() != DMLType.Update)
        {
            throw new SqlMapUpdateException("Update Type is not DMLType.Update");
        }
        DMLUpdateData updateData = dmlData.getUpdateData();
        if (updateData == null)
        {
            throw new SqlMapUpdateException("Update updateData is null");
        }
        String sql = updateData.getSql();
        if (sql == null || "".equals(sql))
        {
            throw new SqlMapUpdateException("Update sql is null/empty");
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
            throw new SqlMapQueryException("Query id is not allowed to be null");
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
            throw new SqlMapQueryException("Query the id ('" + id + "') does not exists");
        }
        if (dmlData.getType() != DMLType.Query)
        {
            throw new SqlMapQueryException("Query Type is not DMLType.Query");
        }
        DMLQueryData queryData = dmlData.getQueryData();
        if (queryData == null)
        {
            throw new SqlMapQueryException("Query queryData is null");
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
            throw new SqlMapQueryException("Query id is not allowed to be null");
        }
        DMLData dmlData = sqlMapConfig.getDMLData(id);
        if (dmlData == null)
        {
            throw new SqlMapQueryException("Query the id ('" + id + "') does not exists");
        }
        if (dmlData.getType() != DMLType.Query)
        {
            throw new SqlMapQueryException("Query Type is not DMLType.Query");
        }
        DMLQueryData queryData = dmlData.getQueryData();
        if (queryData == null)
        {
            throw new SqlMapQueryException("Query queryData is null");
        }
        String sql = queryData.getSql(values);
        if (sql == null)
        {
            throw new SqlMapQueryException("sql does not exists");
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