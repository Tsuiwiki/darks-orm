package darks.orm.app.factory;

import java.util.List;

import darks.orm.app.Page;
import darks.orm.app.QueryEnumType;
import darks.orm.app.SqlSession;
import darks.orm.core.factory.SqlMapSingletonFactory;
import darks.orm.exceptions.SqlMapQueryException;

public class GenericSqlMapFactory implements SqlMapFactory
{
    
    private SqlSession dao = null;
    
    public GenericSqlMapFactory(SqlSession dao)
    {
        this.dao = dao;
    }
    
    /**
     * ִ��DDL���
     */
    public void executeDDLMap()
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        sqlmapFactory.executeDDLMap();
    }
    
    /**
     * SQLMAP����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     */
    public void update(String id, Object... params)
        throws Exception
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        sqlmapFactory.update(dao, id, params);
    }
    
    /**
     * ���ݲ�ѯ������ͽ���SQLMAP��ѯ
     * 
     * @param id SQLMAP���
     * @param queryEnumType ��ѯ�������ö��
     * @param params ע�����(����)
     * @return ����
     * @throws Exception
     */
    public Object query(String id, QueryEnumType queryEnumType, Object... params)
        throws Exception
    {
        return query(id, queryEnumType, null, params);
    }
    
    /**
     * ���ݲ�ѯ������ͽ���SQLMAP��ѯ
     * 
     * @param id SQLMAP���
     * @param queryEnumType ��ѯ�������ö��
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ����
     * @throws Exception
     */
    public Object query(String id, QueryEnumType queryEnumType, Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        if (queryEnumType == QueryEnumType.Page || queryEnumType == QueryEnumType.Auto)
        {
            throw new SqlMapQueryException("GenericSqlMapFactory::query queryEnumType can not use Page/Auto,please change to another query method with page/pageSize");
        }
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        return sqlmapFactory.query(dao, id, queryEnumType, 0, 0, values, params);
    }
    
    /**
     * ���ݲ�ѯ������ͽ���SQLMAP��ѯ
     * 
     * @param id SQLMAP���
     * @param queryEnumType ��ѯ�������ö��
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��С
     * @return ����
     * @throws Exception
     */
    public Object query(String id, QueryEnumType queryEnumType, int page, int pageSize, Object[] values, Object[] params)
        throws Exception
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        return sqlmapFactory.query(dao, id, queryEnumType, page, pageSize, values, params);
    }
    
    /**
     * SQLMAP��ѯ��������
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��������
     * @throws Exception
     */
    public Object queryObject(String id, Object... params)
        throws Exception
    {
        return queryObject(id, null, params);
    }
    
    /**
     * SQLMAP��ѯ��������
     * 
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ��������
     * @throws Exception
     */
    public Object queryObject(String id, Object[] values, Object[] params)
        throws Exception
    {
        return queryForType(QueryEnumType.Object, id, values, params);
    }
    
    /**
     * SQLMAP��ѯ�б����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return �б����
     * @throws Exception
     */
    public List<?> queryList(String id, Object... params)
        throws Exception
    {
        return (List<?>)queryForType(QueryEnumType.List, id, null, params);
    }
    
    /**
     * SQLMAP��ѯ�б����
     * 
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return �б����
     * @throws Exception
     */
    public List<?> queryList(String id, Object[] values, Object[] params)
        throws Exception
    {
        return (List<?>)queryForType(QueryEnumType.List, id, values, params);
    }
    
    /**
     * SQLMAP��ѯ��ҳ����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��ҳ����
     * @throws Exception
     */
    public Page<?> queryPageList(String id, int page, int pageSize, Object... params)
        throws Exception
    {
        return queryPageList(id, page, pageSize, null, params);
    }
    
    /**
     * SQLMAP��ѯ��ҳ����
     * 
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ��ҳ����
     * @throws Exception
     */
    public Page<?> queryPageList(String id, int page, int pageSize, Object[] values, Object[] params)
        throws Exception
    {
        return (Page<?>)queryForType(QueryEnumType.Page, id, page, pageSize, values, params);
    }
    
    /**
     * ���ݲ�ѯ���ͽ���SQLMAP��ѯ
     * 
     * @param queryEnumType ��ѯ����ö��
     * @param id SQLMAP���
     * @param params ע�����
     * @return ��ѯ�������
     * @throws Exception
     */
    public Object queryForType(QueryEnumType queryEnumType, String id, Object[] params)
        throws Exception
    {
        return queryForType(queryEnumType, id, 0, 0, null, params);
    }
    
    /**
     * ���ݲ�ѯ���ͽ���SQLMAP��ѯ
     * 
     * @param queryEnumType ��ѯ����ö��
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����
     * @return ��ѯ�������
     * @throws Exception
     */
    public Object queryForType(QueryEnumType queryEnumType, String id, Object[] values, Object[] params)
        throws Exception
    {
        return queryForType(queryEnumType, id, 0, 0, values, params);
    }
    
    /**
     * ���ݲ�ѯ���ͽ���SQLMAP��ѯ
     * 
     * @param queryEnumType ��ѯ����ö��
     * @param id SQLMAP���
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��С
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����
     * @return ��ѯ�������
     * @throws Exception
     */
    public Object queryForType(QueryEnumType queryEnumType, String id, int page, int pageSize, Object[] values,
        Object[] params)
        throws Exception
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        return sqlmapFactory.queryForType(dao, queryEnumType, id, page, pageSize, values, params);
    }
    
    public SqlSession getDao()
    {
        return dao;
    }
    
    public void setDao(SqlSession dao)
    {
        this.dao = dao;
    }
    
}
