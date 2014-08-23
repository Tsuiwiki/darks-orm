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

package darks.orm.core.config;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import darks.orm.core.config.CacheConfiguration.CacheConfigType;
import darks.orm.exceptions.ClassReflectException;
import darks.orm.exceptions.ConfigException;
import darks.orm.log.Logger;
import darks.orm.log.LoggerFactory;
import darks.orm.util.FileHelper;
import darks.orm.util.ReflectHelper;

public final class SessionConfigFactory
{
    
    private static final Logger logger = LoggerFactory.getLogger(SessionConfigFactory.class);
    
    private static final String DEFAULT_CONFIG_PATH = "/darks.xml";
    
    private static final String DEFAULT_CONFIG_NAMESPACE = "http://www.darks.org/schema/darks";
    
    private static Map<String, Class<?>> configMap = new HashMap<String, Class<?>>(3);
    
    static
    {
        configMap.put("jdbc", JdbcConfiguration.class);
        configMap.put("bonecp", BoneCPConfiguration.class);
        configMap.put("jndi", JndiConfiguration.class);
    }
    
    private SessionConfigFactory()
    {
        
    }
    
    /**
     * ��ȡ�����ļ�ʵ��
     * 
     * @return �����ļ�ʵ��
     * @throws ConfigException
     */
    public static Configuration getConfiguration()
        throws ConfigException
    {
        URL url = SessionConfigFactory.class.getResource(DEFAULT_CONFIG_PATH);
        if (url == null)
        {
            throw new ConfigException("'" + DEFAULT_CONFIG_PATH + "' configuration file does not exists.");
        }
        else
        {
            logger.debug("Load '" + DEFAULT_CONFIG_PATH + "' configure file ['" + url.toString() + "']");
        }
        String path = url.getFile();
        path = path.replace("%20", " ");
        return getConfiguration(path);
    }
    
    /**
     * ��ȡ�����ļ�ʵ��
     * 
     * @param configPath �����ļ�·��
     * @return �����ļ�ʵ��
     * @throws ConfigException
     */
    public static Configuration getConfiguration(String configPath)
        throws ConfigException
    {
        Configuration cfg = new Configuration();
        try
        {
            File f = new File(configPath);
            SAXReader reader = new SAXReader();
            Map<String, String> map = new HashMap<String, String>();
            map.put("d", DEFAULT_CONFIG_NAMESPACE);
            reader.getDocumentFactory().setXPathNamespaceURIs(map);
            Document doc = reader.read(f);
            // ����DataSource
            parseDataSourceXpath(doc, cfg);
            // ������־����
            parseLoggerXpath(doc, cfg);
            // ����ʵ������
            parseEntityXpath(doc, cfg);
            // ����SqlMap·������
            parseSqlMapXpath(doc, cfg);
            // ������������
            parseCacheXpath(doc, cfg);
        }
        catch (DocumentException e)
        {
            throw new ConfigException("fail to read document'" + configPath + "' configuration file.");
        }
        return cfg;
    }
    
    /**
     * ����DataSource����
     * 
     * @param doc Document
     * @param cfg Configuration����
     * @throws ConfigException
     */
    private static void parseDataSourceXpath(Document doc, Configuration cfg)
        throws ConfigException
    {
        String xpath = "/d:darks/d:dataSource[@type]";
        List<?> nodes = doc.selectNodes(xpath);
        Iterator<?> it = nodes.iterator();
        while (it.hasNext())
        {
            Element node = (Element)it.next();
            parseDataSourceNode(node, cfg);
        }
        cfg.ensureDataSourceChain();
        cfg.ensureMainDataSourceConfig();
    }
    
    /**
     * ����DataSource����Ԫ��
     * 
     * @param node XMLԪ�ؽڵ�
     * @param cfg Configuration����
     * @throws ConfigException
     */
    private static void parseDataSourceNode(Element node, Configuration cfg)
        throws ConfigException
    {
        DataSourceConfiguration dsConfig = null;
        Class<?> clazz = null;
        if (node == null)
        {
            throw new ConfigException("document does not has 'dataSource' node.");
        }
        String type = node.attributeValue("type");
        if (configMap.containsKey(type))
        {
            clazz = configMap.get(type);
        }
        if (clazz == null)
        {
            throw new ConfigException("DataSource type does not exists");
        }
        try
        {
            dsConfig = (DataSourceConfiguration)ReflectHelper.newFastInstance(clazz);
            dsConfig.setType(type);
            String id = node.attributeValue("id");
            if (id == null)
            {
                id = type;
            }
            String main = node.attributeValue("main");
            boolean isMain = false;
            if (main != null)
            {
                isMain = Boolean.parseBoolean(main);
            }
            String chainref = node.attributeValue("chainref");
            if (chainref != null)
            {
                dsConfig.setNextId(chainref);
            }
            List<?> nodes = node.selectNodes("d:property[@name][@value]");
            Iterator<?> it = nodes.iterator();
            Element el = null;
            Field field = null;
            while (it.hasNext())
            {
                el = (Element)it.next();
                String name = el.attributeValue("name");
                String value = el.attributeValue("value");
                field = ReflectHelper.getField(clazz, name);
                ReflectHelper.setFieldString(field, dsConfig, value);
            }
            el = (Element)node.selectSingleNode("d:resultSet");
            if (el != null)
            {
                ResultSetConfig rsconfig = dsConfig.getResultSetConfig();
                String rsType = el.attributeValue("type");
                String rsSensitive = el.attributeValue("sensitive");
                String rsConcurrency = el.attributeValue("concurrency");
                rsconfig.setType(rsType);
                rsconfig.setConcurrency(rsConcurrency);
                rsconfig.setSensitive(rsSensitive);
            }
            cfg.addDataSourceConfig(id, dsConfig, isMain);
        }
        catch (NoSuchFieldException e)
        {
            throw new ConfigException(e.getMessage(), e);
        }
        catch (ClassReflectException e)
        {
            throw new ConfigException(e.getMessage(), e);
        }
    }
    
    /**
     * ����Logger����
     * 
     * @param doc Document
     * @param cfg Configuration����
     * @throws ConfigException
     */
    private static void parseLoggerXpath(Document doc, Configuration cfg)
        throws ConfigException
    {
        String xpath = "/d:darks/d:logger";
        Element node = (Element)doc.selectSingleNode(xpath);
        if (node == null)
            return;
        Element showSqlNode = (Element)node.selectSingleNode("d:showSql");
        Element showLogNode = (Element)node.selectSingleNode("d:showLog");
        Element writeLogNode = (Element)node.selectSingleNode("d:writeLog");
        LoggerConfiguration config = cfg.Logger();
        if (showSqlNode != null)
        {
            String val = showSqlNode.getTextTrim();
            config.setShowSql(Boolean.parseBoolean(val));
        }
        if (showLogNode != null)
        {
            String val = showLogNode.getTextTrim();
            config.setShowLog(Boolean.parseBoolean(val));
        }
        if (writeLogNode != null)
        {
            String val = writeLogNode.getTextTrim();
            config.setWriteLog(Boolean.parseBoolean(val));
        }
    }
    
    /**
     * ����ʵ������
     * 
     * @param doc Document
     * @param cfg Configuration����
     * @throws ConfigException
     */
    private static void parseEntityXpath(Document doc, Configuration cfg)
        throws ConfigException
    {
        EntityConfiguration entityConfig = cfg.getEntityConfig();
        String xpath = "/d:darks/d:entitys";
        Element node = (Element)doc.selectSingleNode(xpath);
        if (node == null)
            return;
        List<?> nodes = node.selectNodes("d:entity[@class]");
        Iterator<?> it = nodes.iterator();
        while (it.hasNext())
        {
            Element el = (Element)it.next();
            String alias = el.attributeValue("alias");
            String className = el.attributeValue("class");
            if (className == null)
                continue;
            entityConfig.addEntityConfig(alias, className);
        }
    }
    
    /**
     * ����SqlMap����
     * 
     * @param doc Document
     * @param cfg Configuration����
     * @throws ConfigException
     */
    private static void parseSqlMapXpath(Document doc, Configuration cfg)
        throws ConfigException
    {
        List<String> sqlMapsPath = cfg.getSqlMapsPath();
        String xpath = "/d:darks/d:sqlMapGroup";
        Element node = (Element)doc.selectSingleNode(xpath);
        if (node == null)
            return;
        List<?> nodes = node.selectNodes("d:sqlMap");
        Iterator<?> it = nodes.iterator();
        while (it.hasNext())
        {
            Element el = (Element)it.next();
            String path = el.getTextTrim();
            if (path.indexOf("*") >= 0)
            {
                List<String> list = FileHelper.getRegexResourceFiles(path);
                for (String fpath : list)
                {
                    sqlMapsPath.add(fpath);
                }
            }
            else
            {
                if (!"".equals(path))
                {
                    sqlMapsPath.add(path);
                }
            }
        }
    }
    
    /**
     * ������������
     * 
     * @param doc Document
     * @param cfg Configuration����
     * @throws ConfigException
     */
    private static void parseCacheXpath(Document doc, Configuration cfg)
        throws ConfigException
    {
        CacheConfiguration cacheConfig = cfg.getCacheConfig();
        String xpath = "/d:darks/d:cacheGroup";
        Element node = (Element)doc.selectSingleNode(xpath);
        if (node == null)
            return;
        String use = node.attributeValue("use");
        String type = node.attributeValue("type");
        String cacheId = node.attributeValue("cacheId");
        String synchronous = node.attributeValue("synchronous");
        // �Ƿ�ʹ�û���
        if (use != null)
        {
            if ("true".equalsIgnoreCase(use))
                cacheConfig.setUseCache(true);
            else if ("false".equalsIgnoreCase(use))
                cacheConfig.setUseCache(false);
        }
        // ʹ������ �Զ�/�ֶ�
        if (type != null)
        {
            if ("auto".equals(type))
                cacheConfig.setCacheConfigType(CacheConfigType.Auto);
            else if ("manual".equals(type))
                cacheConfig.setCacheConfigType(CacheConfigType.Manual);
        }
        // �Զ�������
        if (cacheId != null)
        {
            cacheConfig.setAutoCacheId(cacheId);
        }
        // �Ƿ��첽����
        if (synchronous != null)
        {
            if ("true".equalsIgnoreCase(synchronous))
                cacheConfig.setSynchronous(true);
            else if ("false".equalsIgnoreCase(synchronous))
                cacheConfig.setSynchronous(false);
        }
        cacheConfig.readCacheConfig(node);
    }
}