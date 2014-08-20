package darks.orm.app.factory;

import java.util.List;

import darks.orm.app.Page;
import darks.orm.app.SqlSession;
import darks.orm.app.QueryEnumType;

public interface SqlMapFactory
{
    
    /**
     * ִ��DDL���
     */
    public void executeDDLMap();
    
    /**
     * SQLMAP����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     */
    public void update(String id, Object... params)
        throws Exception;
    
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
        throws Exception;
    
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
        throws Exception;
    
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
        throws Exception;
    
    /**
     * SQLMAP��ѯ��������
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��������
     * @throws Exception
     */
    public Object queryObject(String id, Object... params)
        throws Exception;
    
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
        throws Exception;
    
    /**
     * SQLMAP��ѯ�б����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return �б����
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public List queryList(String id, Object... params)
        throws Exception;
    
    /**
     * SQLMAP��ѯ�б����
     * 
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return �б����
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public List queryList(String id, Object[] values, Object[] params)
        throws Exception;
    
    /**
     * SQLMAP��ѯ��ҳ����,����queryPageListA
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��ҳ����
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public Page queryPageList(String id, int page, int pageSize, Object... params)
        throws Exception;
    
    /**
     * SQLMAP��ѯ��ҳ����
     * 
     * @param id SQLMAP���
     * @param values ѡ������ֵ,����<select> <constitute>��ǩ����
     * @param params ע�����(����)
     * @return ��ҳ����
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public Page queryPageList(String id, int page, int pageSize, Object[] values, Object[] params)
        throws Exception;
    
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
        throws Exception;
    
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
        throws Exception;
    
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
        throws Exception;
    
    public SqlSession getDao();
    
    public void setDao(SqlSession dao);
    
}
