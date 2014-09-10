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

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.cglib.reflect.FastMethod;

import darks.orm.annotation.Entity;
import darks.orm.app.SqlSession;
import darks.orm.core.data.EntityData;
import darks.orm.core.data.FieldData;
import darks.orm.core.data.FieldData.FieldFlag;
import darks.orm.core.session.SessionContext;
import darks.orm.exceptions.TransformException;
import darks.orm.util.DataTypeHelper;
import darks.orm.util.ReflectHelper;

/**
 * 
 * 
 * <p>
 * <h1>TransformFactory.java</h1>
 * <p>
 * @author Liu LiHua
 * @version 1.0.0 v05/03/2012 
 * @since JDK1.5
 */
public class TransformFactory
{
    
    public static final String SQL_COUNT_ALIAS = "SQL_COUNT_ALIAS";
    
    public static final int SQL_TRANSMAP_INIT_SIZE = 100;
    
    public static final int SQL_JDBCRET_INIT_SIZE = 50;
    
    public static final int SQL_STRCLASS_INIT_SIZE = 50;
    
    public static final int SQL_COLUMN_INIT_SIZE = 128;
    
    private static final String ENTITY_CASCADE_DIV = "_";
    
    private transient static final ConcurrentMap<String, String> sqlTranMap = new ConcurrentHashMap<String, String>(
        SQL_TRANSMAP_INIT_SIZE);
    
    private transient static final ConcurrentMap<Class<?>, String> jdbcResultMethodMap =
        new ConcurrentHashMap<Class<?>, String>(SQL_JDBCRET_INIT_SIZE);
    
    private transient static final ConcurrentMap<String, Class<?>> stringClassMap =
        new ConcurrentHashMap<String, Class<?>>(SQL_STRCLASS_INIT_SIZE);
    
    private transient static final ConcurrentMap<String, Map<String, Integer>> colsMap =
        new ConcurrentHashMap<String, Map<String, Integer>>(SQL_COLUMN_INIT_SIZE);
    
    private static volatile TransformFactory instance = null;
    
    private static final Lock lock = new ReentrantLock();
    
    private TransformFactory()
    {
        registerJdbcResultMethodMap();
        registerStringClassMap();
    }
    
    /**
     * ע��ResultSet�����ͻ�ȡӳ��
     */
    public void registerJdbcResultMethodMap()
    {
        registerJdbcResultMethod(Integer.class, "getInt");
        registerJdbcResultMethod(int.class, "getInt");
        registerJdbcResultMethod(String.class, "getString");
        registerJdbcResultMethod(Short.class, "getShort");
        registerJdbcResultMethod(short.class, "getShort");
        registerJdbcResultMethod(Long.class, "getLong");
        registerJdbcResultMethod(long.class, "getLong");
        registerJdbcResultMethod(Double.class, "getDouble");
        registerJdbcResultMethod(double.class, "getDouble");
        registerJdbcResultMethod(Date.class, "getDate");
        registerJdbcResultMethod(Time.class, "getTime");
        registerJdbcResultMethod(Timestamp.class, "getTimestamp");
        registerJdbcResultMethod(Blob.class, "getBlob");
        registerJdbcResultMethod(Clob.class, "getClob");
        registerJdbcResultMethod(NClob.class, "getNClob");
        registerJdbcResultMethod(Array.class, "getArray");
        registerJdbcResultMethod(BigDecimal.class, "getBigDecimal");
        registerJdbcResultMethod(Boolean.class, "getBoolean");
        registerJdbcResultMethod(boolean.class, "getBoolean");
        registerJdbcResultMethod(Byte.class, "getByte");
        registerJdbcResultMethod(byte.class, "getByte");
        registerJdbcResultMethod(byte[].class, "getBytes");
        registerJdbcResultMethod(float.class, "getFloat");
        registerJdbcResultMethod(Float.class, "getFloat");
        registerJdbcResultMethod(URL.class, "getURL");
        registerJdbcResultMethod(SQLXML.class, "getSQLXML");
        registerJdbcResultMethod(Ref.class, "getRef");
        registerJdbcResultMethod(RowId.class, "getRowId");
    }
    
    public void registerStringClassMap()
    {
        registerStringClass(Integer.class, "Integer");
        registerStringClass(int.class, "int");
        registerStringClass(String.class, "String");
        registerStringClass(Short.class, "Short");
        registerStringClass(short.class, "short");
        registerStringClass(Long.class, "Long");
        registerStringClass(long.class, "long");
        registerStringClass(Double.class, "Double");
        registerStringClass(double.class, "double");
        registerStringClass(Boolean.class, "Boolean");
        registerStringClass(boolean.class, "boolean");
        registerStringClass(Byte.class, "Byte");
        registerStringClass(byte.class, "byte");
        registerStringClass(byte[].class, "bytes");
        registerStringClass(float.class, "float");
        registerStringClass(Float.class, "Float");
    }
    
    /**
     * ͨ���ַ������ʵ����������
     * 
     * @param strClass ���ַ���
     * @return ��
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public Class<?> stringToEntityClass(String strClass)
        throws ClassNotFoundException
    {
        Class<?> ret = stringClassMap.get(strClass);
        if (ret == null)
        {
            ret = SessionContext.getConfigure().getEntityConfig().getEntity(strClass);
            if (ret != null)
                return ret;
            return SessionContext.getConfigure().getEntityConfig().addEntityConfig(null, strClass);
        }
        return ret;
    }
    
    /**
     * ͨ�����ͻ�ȡ��ȡ��������
     * 
     * @param key ��/��ֵ
     * @return
     */
    public String getJdbcResultMethod(Class<?> key)
    {
        String val = jdbcResultMethodMap.get(key);
        if (val != null)
            return val;
        return "getObject";
    }
    
    /**
     * ע�᷽������ӳ��
     * 
     * @param key ��/��ֵ
     * @param value ��������
     */
    private void registerJdbcResultMethod(Class<?> key, String value)
    {
        jdbcResultMethodMap.put(key, value);
        
    }
    
    /**
     * ע������/��ӳ��
     * 
     * @param cls ��
     * @param className ������
     */
    private void registerStringClass(Class<?> cls, String className)
    {
        stringClassMap.put(className, cls);
    }
    
    /**
     * ��̨ģʽ
     * 
     * @return TransformFactoryʵ��
     */
    public static TransformFactory getInstance()
    {
        if (instance == null)
        {
            lock.lock();
            try
            {
                if (instance == null)
                    instance = new TransformFactory();
            }
            finally
            {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * �������ת����ʵ�岢���浽�б�����
     * 
     * @param <T> �෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @return ����ʵ���༯��
     * @throws Exception
     */
    public <T> List<T> ResultToList(Class<T> c, String sql, ResultSet rs)
        throws Exception
    {
        List<T> list = new ArrayList<T>();
        while (rs.next())
        {
            T n = ResultToBean(c, sql, rs, false);
            list.add(n);
        }
        
        return list;
    }
    
    /**
     * �������ת����ʵ���࣬�����з�ҳ������ҳ��Ľ������
     * 
     * @param <T> ʵ���෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��С
     * @return ʵ�����б�
     * @throws Exception
     */
    public <T> List<T> ResultToPageScroll(Class<T> c, String sql, ResultSet rs, int page, int pageSize)
        throws Exception
    {
        List<T> list = new ArrayList<T>();
        int cur = (page - 1) * pageSize + 1;
        rs.absolute(cur);
        for (int i = 0; i < pageSize; i++)
        {
            T n = ResultToBean(c, sql, rs, false);
            list.add(n);
            if (!rs.next())
                break;
        }
        return list;
    }
    
    /**
     * �������ת����ʵ���࣬�����з�ҳ������ҳ��Ľ������
     * 
     * @param <T> ʵ���෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��С
     * @return ʵ�����б�
     * @throws Exception
     */
    public <T> List<T> ResultToPageForward(Class<T> c, String sql, ResultSet rs, int page, int pageSize)
        throws Exception
    {
        List<T> list = new ArrayList<T>();
        if (!rs.next())
            return list;
        int cur = (page - 1) * pageSize;
        for (int i = 0; i < cur; i++)
        {
            if (!rs.next())
                return list;
        }
        for (int i = 0; i < pageSize; i++)
        {
            T n = ResultToBean(c, sql, rs, false);
            list.add(n);
            if (!rs.next())
                break;
        }
        return list;
    }
    
    /**
     * �����ת����ʵ�壬������¼
     * 
     * @param <T> �෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @return ��ʵ��
     * @throws Exception
     */
    public <T> T ResultToBean(Class<T> c, String sql, ResultSet rs, boolean cursorNext)
        throws Exception
    {
        if (DataTypeHelper.checkClassIsBasicDataType(c))
        {
            return ResultSetToBasicDataType(c, rs, cursorNext);
        }
        return ResultToEntityDataType(c, sql, rs, cursorNext, true, null);
    }
    
    /**
     * �����ת����ʵ�壬������¼
     * 
     * @param <T> �෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @param recursion �Ƿ�ݹ�
     * @return ��ʵ��
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T ResultToEntityDataType(Class<T> c, String sql, ResultSet rs, boolean cursorNext, boolean recursion,
        String alias)
        throws Exception
    {
        sql = sql.toUpperCase();
        boolean flag = c.isAnnotationPresent(Entity.class);
        if (!flag)
            return null;
        Entity classType = (Entity)c.getAnnotation(Entity.class);
        if (classType == null)
            return null;
        
        if (cursorNext)
        {
            if (!rs.next())
                return null;
        }
        
        if (!rs.isAfterLast() && !rs.isBeforeFirst())
        {
            EntityData entityData = ClassFactory.parseClass(c);
            if (entityData == null)
                return null;
            c = (Class<T>)entityData.getClassProxy();
            Object entity = entityData.newInstance();
            
            if (entity == null)
                return null;
            
            // ���������������
            Map<String, Integer> colsIndexMap = cacheSqlColumnIndex(rs, sql);
            // ����ʵ������������
            for (Entry<String, FieldData> entry : entityData.getMapFields().entrySet())
            {
                String key = entry.getKey();
                if (alias != null)
                {
                    key = alias + "_" + key;
                }
                FieldData fdata = entry.getValue();
                if (!fdata.isQueryable() || fdata.getFieldFlag() == FieldFlag.Collection)
                    continue;
                if (!colsIndexMap.containsKey(key))
                {
                    continue;
                }
                
                // �������ʵ�崦��
                if (fdata.getFieldFlag() == FieldFlag.FkEntity)
                {
                    // ��ȡ���ʵ������
                    EntityData fkData = fdata.getFkData();
                    if (fkData == null)
                        continue;
                    FieldData fpkdata = fkData.getPkField();
                    if (fpkdata == null)
                        continue;
                    String tname = fkData.getTableName();
                    tname = tname.toUpperCase();
                    if (sql.indexOf(" " + tname + " ") >= 0 && !tname.equals(entityData.getTableName()) && recursion)
                    { // �жϸ�SQL����Ƿ��ѯ�˸ñ�
                        Class<?> fc = fdata.getFkClass();
                        // �ݹ�ת�����ʵ�����
                        Object obj = this.ResultToEntityDataType(fc, sql, rs, false, false, alias);
                        fdata.setValue(entity, obj);
                    }
                    else
                    {
                        // �����ֵ���������ֶ�
                        Object obj = ReflectHelper.getResultSetValue(rs, fdata.getFieldClass(), key);
                        if (obj == null && DataTypeHelper.checkClassIsNumberBasicDataType(fpkdata.getFieldClass()))
                        {
                            obj = 0;
                        }
                        String fname = fdata.getFkSetMethod();
                        FastMethod fm = ReflectHelper.parseFastMethod(c, fname, fpkdata.getFieldClass());
                        fm.invoke(entity, new Object[] {obj});
                    }
                    continue;
                }
                // ���������ֶ�
                Object obj = ReflectHelper.getResultSetValue(rs, fdata.getFieldClass(), key);
                if (obj == null && DataTypeHelper.checkClassIsNumberBasicDataType(fdata.getFieldClass()))
                {
                    obj = 0;
                }
                fdata.setValue(entity, obj);
                
            }
            return (T)entity;
        }
        return null;
    }
    
    /**
     * �����ת����������������
     * 
     * @param c ����������
     * @param rs �����
     * @param corsorNext �Ƿ����ƹ��
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T ResultSetToBasicDataType(Class<T> c, ResultSet rs, boolean cursorNext)
        throws TransformException
    {
        if (cursorNext)
        {
            try
            {
                if (!rs.next())
                    return null;
            }
            catch (SQLException e)
            {
                throw new TransformException("TransformFactory::ResultSetToBasicDataType " + e.toString(), e);
            }
        }
        return (T)ReflectHelper.getResultSetValue(rs, c, 1);
    }
    
    /**
     * ������ѯת��SQL���
     * 
     * @param sql ԭSQL���
     * @param c ������
     * @param map ������
     * @param pkey ��ֵ
     * @return
     * @throws Exception
     */
    public <T> String transformSQLToCascade(String sql, Class<T> c, Map<String, String> map, StringBuffer pkey)
        throws Exception
    {
        sql = sql.toUpperCase();
        pkey.append("[SQL]");
        pkey.append(sql);
        pkey.append("[C]");
        pkey.append(c.getName());
        String key = pkey.toString();
        if (sqlTranMap.containsKey(key))
        {
            return sqlTranMap.get(key);
        }
        if (sql.indexOf("SELECT") >= 0)
        {
            int from = sql.indexOf("FROM");
            String fieldSql = sql.substring(0, from);
            if (fieldSql.indexOf("*") >= 0)
            {
                sql = sql.substring(from);
                // ���ʵ��������
                EntityData entityData = ClassFactory.parseClass(c);
                ConcurrentMap<String, FieldData> mapNameFields = entityData.getMapNameFields();
                StringBuffer buf = new StringBuffer(256);
                buf.append("SELECT ");
                // ��ȡ���ʵ�����ֶ��ַ���
                if (!map.containsKey(SqlSession.SELF))
                {
                    buf.append(entityData.getFieldString());
                    buf.append(",");
                }
                else
                {
                    String alias = map.get(SqlSession.SELF);
                    buf.append(entityData.getAliasString(alias));
                    buf.append(",");
                }
                // ��ȡ����ʵ�����ֶ��ַ���
                FieldData fieldData = null;
                for (Entry<String, String> entry : map.entrySet())
                {
                    String entityName = entry.getKey();
                    String alias = entry.getValue();
                    if (SqlSession.SELF.equals(entityName))
                        continue;
                    
                    if (entityName.indexOf(ENTITY_CASCADE_DIV) > 0)
                    {
                        String[] attrs = entityName.split(ENTITY_CASCADE_DIV);
                        EntityData data = entityData;
                        for (String attr : attrs)
                        {
                            ConcurrentMap<String, FieldData> dataFields = data.getMapNameFields();
                            fieldData = dataFields.get(attr);
                            data = fieldData.getFkData();
                        }
                    }
                    else
                    {
                        fieldData = mapNameFields.get(entityName);
                    }
                    
                    if (fieldData != null)
                    {
                        if (fieldData.getFieldFlag() == FieldFlag.FkEntity && fieldData.getFkData() != null)
                        {
                            EntityData fkData = fieldData.getFkData();
                            buf.append(fkData.getAliasString(alias, entityName));
                            buf.append(",");
                        }
                        else
                        {
                            throw new Exception("the '" + entityName + "' of Entity is not a foreign key field");
                        }
                    }
                    else
                    {
                        throw new Exception("Entity '" + entityData.getClassName() + "' does not include '"
                            + entityName + "' field");
                    }
                }
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                buf.append(" ");
                buf.append(sql);
                sql = buf.toString();
                sqlTranMap.put(key, sql);
                return sql;
            }
            else
            {
                return sql;
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * ת��������ѯSQL
     * 
     * @param sql ԭSQL
     * @return ת����SQL
     */
    public String transformSqlToCount(String sql)
        throws Exception
    {
        sql = sql.toUpperCase();
        int selectIndex = sql.indexOf("SELECT");
        int fromIndex = sql.indexOf("FROM");
        if (selectIndex < 0 || fromIndex < 0)
        {
            throw new Exception("sql is error");
        }
        String tmp = sql.substring(fromIndex);
        StringBuffer buf = new StringBuffer(tmp.length() + 17);
        buf.append("SELECT COUNT(*) ");
        buf.append(tmp);
        // buf.append(") ");
        // buf.append(SQL_COUNT_ALIAS);
        return buf.toString();
    }
    
    /**
     * �����������ת����ʵ�岢���浽�б�����
     * 
     * @param <T> �෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @return ����ʵ���༯��
     * @throws Exception
     */
    public <T> List<T> cascadeResultToList(Class<T> c, String sql, ResultSet rs, Map<String, String> map)
        throws Exception
    {
        if (rs == null)
            return null;
        sql = sql.toUpperCase();
        boolean flag = c.isAnnotationPresent(Entity.class);
        if (!flag)
            return null;
        List<T> list = new ArrayList<T>();
        Entity classType = (Entity)c.getAnnotation(Entity.class);
        if (classType == null)
            return null;
        ClassFactory.parseClass(c);
        
        // c=ClassFactory.getClass(c.getName());
        while (rs.next())
        {
            T n = cascadeResultToBean(c, sql, rs, map, false);
            list.add(n);
        }
        
        return list;
    }
    
    /**
     * �����������ת����ʵ���࣬�����з�ҳ������ҳ��Ľ������
     * 
     * @param <T> ʵ���෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @param page ��ǰҳ��
     * @param pageSize ��ҳ��С
     * @return ʵ�����б�
     * @throws Exception
     */
    public <T> List<T> cascadeResultToPage(Class<T> c, String sql, ResultSet rs, int page, int pageSize,
        Map<String, String> map)
        throws Exception
    {
        sql = sql.toUpperCase();
        boolean flag = c.isAnnotationPresent(Entity.class);
        if (!flag)
            return null;
        List<T> list = new ArrayList<T>();
        Entity classType = (Entity)c.getAnnotation(Entity.class);
        if (classType == null)
            return null;
        ClassFactory.parseClass(c);
        
        int cur = (page - 1) * pageSize + 1;
        rs.absolute(cur);
        for (int i = 0; i < pageSize; i++)
        {
            
            T n = cascadeResultToBean(c, sql, rs, map, false);
            list.add(n);
            
            if (!rs.next())
                break;
        }
        return list;
    }
    
    /**
     * ���������ת����ʵ�壬������¼
     * 
     * @param <T> �෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @return ��ʵ��
     * @throws Exception
     */
    public <T> T cascadeResultToBean(Class<T> c, String sql, ResultSet rs, Map<String, String> map, boolean corsorNext)
        throws Exception
    {
        return this.cascadeResultToBean(c, sql, rs, map, corsorNext, null);
    }
    
    /**
     * ���������ת����ʵ�壬������¼
     * 
     * @param <T> �෶��
     * @param c ʵ����
     * @param sql SQL��ѯ���
     * @param rs �����
     * @return ��ʵ��
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T cascadeResultToBean(Class<T> c, String sql, ResultSet rs, Map<String, String> map, boolean corsorNext,
        String alias)
        throws Exception
    {
        sql = sql.toUpperCase();
        boolean flag = c.isAnnotationPresent(Entity.class);
        if (!flag)
            return null;
        Entity classType = (Entity)c.getAnnotation(Entity.class);
        if (classType == null)
            return null;
        EntityData entityData = ClassFactory.parseClass(c);
        if (corsorNext)
        {
            if (!rs.next())
                return null;
        }
        if (!rs.isAfterLast() && !rs.isBeforeFirst())
        {
            c = (Class<T>)ClassFactory.getClass(c.getName());
            Object entity = entityData.newInstance();
            // -------
            Map<String, Integer> colsIndexMap = cacheSqlColumnIndex(rs, sql);
            // --------
            for (Entry<String, FieldData> entry : entityData.getMapFields().entrySet())
            {
                String key = entry.getKey();
                if (alias != null)
                {
                    key = alias.toUpperCase() + "_" + key;
                }
                FieldData fdata = entry.getValue();
                
                if (!fdata.isQueryable() || fdata.getFieldFlag() == FieldFlag.Collection)
                {
                    continue;
                }
                if (!fdata.isQueryable())
                    continue;
                if (sql.indexOf(key.toUpperCase()) < 0 && sql.indexOf('*') < 0)
                {
                    continue;
                }
                
                if (!colsIndexMap.containsKey(key))
                {
                    continue;
                }
                
                if (fdata.getFieldFlag() == FieldFlag.FkEntity)
                {
                    EntityData fkData = fdata.getFkData();
                    if (fkData == null)
                        continue;
                    FieldData fpkdata = fkData.getPkField();
                    if (fpkdata == null)
                        continue;
                    Object obj = ReflectHelper.getResultSetValue(rs, fdata.getFieldClass(), key);
                    if (obj == null)
                    {
                        continue;
                    }
                    String fname = fdata.getFkSetMethod();
                    FastMethod fm = ReflectHelper.parseFastMethod(c, fname, fpkdata.getFieldClass());
                    fm.invoke(entity, new Object[] {obj});
                    // -------------------
                    String check = fdata.getFieldName(alias);
                    if (map.containsKey(check))
                    {
                        Class<?> fc = fdata.getFkClass();
                        obj = cascadeResultToBean(fc, sql, rs, map, false, fdata.getFieldName(alias));
                        fdata.setValue(entity, obj);
                    }
                    continue;
                }
                Object obj = ReflectHelper.getResultSetValue(rs, fdata.getFieldClass(), key);
                fdata.setValue(entity, obj);
            }
            return (T)entity;
        }
        return null;
    }
    
    /**
     * ��������������
     * 
     * @param rs �����
     * @param sql SQL��� ��ֵ
     * @return
     * @throws SQLException
     */
    public Map<String, Integer> cacheSqlColumnIndex(ResultSet rs, String sql)
        throws SQLException
    {
        Map<String, Integer> colsIndexMap = colsMap.get(sql);
        if (colsIndexMap == null)
        {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colsCount = rsmd.getColumnCount();
            colsIndexMap = new HashMap<String, Integer>(colsCount);
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
            {
                String key = rsmd.getColumnLabel(i).toUpperCase();
                colsIndexMap.put(key, i);
            }
            colsMap.put(sql, colsIndexMap);
        }
        return colsIndexMap;
    }
}