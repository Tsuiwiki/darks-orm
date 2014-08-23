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

package darks.orm.app.factory;

import java.util.List;

import darks.orm.app.Page;
import darks.orm.app.QueryEnumType;
import darks.orm.core.factory.SqlMapSingletonFactory;
import darks.orm.exceptions.SqlMapQueryException;

public class StaticSqlMapFactory
{
    
    /**
     * ִ��DDL���
     */
    public static void executeDDLMap()
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
    public static void update(String id, Object... params)
        throws Exception
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        sqlmapFactory.update(null, id, params);
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
    public static Object query(String id, QueryEnumType queryEnumType, Object... params)
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
    public static Object query(String id, QueryEnumType queryEnumType, Object[] values, Object[] params)
        throws SqlMapQueryException
    {
        if (queryEnumType == QueryEnumType.Page || queryEnumType == QueryEnumType.Auto)
        {
            throw new SqlMapQueryException("StaticSqlMapFactory::query queryEnumType can not use Page/Auto,"
                + "please change to another query method with page/pageSize");
        }
        
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        return sqlmapFactory.query(null, id, queryEnumType, 0, 0, values, params);
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
    public static Object query(String id, QueryEnumType queryEnumType, int page, int pageSize, Object[] values,
        Object[] params)
        throws Exception
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        return sqlmapFactory.query(null, id, queryEnumType, page, pageSize, values, params);
    }
    
    /**
     * SQLMAP��ѯ��������
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��������
     * @throws Exception
     */
    public static Object queryObject(String id, Object... params)
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
    public static Object queryObject(String id, Object[] values, Object[] params)
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
    @SuppressWarnings("rawtypes")
    public static List queryList(String id, Object... params)
        throws Exception
    {
        return (List)queryForType(QueryEnumType.List, id, null, params);
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
    @SuppressWarnings("rawtypes")
    public static List queryList(String id, Object[] values, Object[] params)
        throws Exception
    {
        return (List)queryForType(QueryEnumType.List, id, values, params);
    }
    
    /**
     * SQLMAP��ѯ��ҳ����
     * 
     * @param id SQLMAP���
     * @param params ע�����(����)
     * @return ��ҳ����
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Page queryPageList(String id, int page, int pageSize, Object... params)
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
    @SuppressWarnings("rawtypes")
    public static Page queryPageList(String id, int page, int pageSize, Object[] values, Object[] params)
        throws Exception
    {
        return (Page)queryForType(QueryEnumType.Page, id, page, pageSize, values, params);
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
    public static Object queryForType(QueryEnumType queryEnumType, String id, Object[] params)
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
    public static Object queryForType(QueryEnumType queryEnumType, String id, Object[] values, Object[] params)
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
    public static Object queryForType(QueryEnumType queryEnumType, String id, int page, int pageSize, Object[] values,
        Object[] params)
        throws Exception
    {
        SqlMapSingletonFactory sqlmapFactory = SqlMapSingletonFactory.getInstance();
        return sqlmapFactory.queryForType(null, queryEnumType, id, page, pageSize, values, params);
    }
    
}