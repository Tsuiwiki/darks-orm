package darks.orm.core.config.sqlmap;

import java.util.Iterator;

import darks.orm.core.data.xml.DDLData;
import darks.orm.core.data.xml.DDLData.DDLType;
import darks.orm.util.UUIDHelper;
import org.dom4j.Attribute;
import org.dom4j.Element;

@SuppressWarnings("unchecked")
public class DDLConfigReader
{
    
    private SqlMapConfiguration sqlMapConfig;
    
    public DDLConfigReader(SqlMapConfiguration sqlMapConfig)
    {
        this.sqlMapConfig = sqlMapConfig;
    }
    
    /**
     * ��ȡDDL�����ļ�
     * 
     * @param element Ԫ��
     */
    public void reader(Element element)
    {
        // ��ȡDDL����
        for (Iterator<Attribute> it = element.attributeIterator(); it.hasNext();)
        {
            try
            {
                Attribute at = it.next();
                String name = at.getName().trim();
                String value = at.getValue().trim();
                if ("schema".equalsIgnoreCase(name))
                {
                    sqlMapConfig.setSchema(value);
                }
                else if ("catalog".equalsIgnoreCase(name))
                {
                    sqlMapConfig.setCatalog(value);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        // ��ȡDDL��Ԫ��
        for (Iterator<Element> it = element.elementIterator(); it.hasNext();)
        {
            try
            {
                Element el = it.next();
                String name = el.getName().trim();
                if ("create".equalsIgnoreCase(name))
                {
                    readCreate(el);
                }
                else if ("alter".equalsIgnoreCase(name))
                {
                    readAlter(el);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * ��ȡ<create>��ǩ
     * 
     * @param element Ԫ��
     */
    public void readCreate(Element element)
    {
        readLabel(element, DDLType.Create);
    }
    
    /**
     * ��ȡ<alter>��ǩ
     * 
     * @param element Ԫ��
     */
    public void readAlter(Element element)
    {
        readLabel(element, DDLType.Alter);
    }
    
    /**
     * ��ȡ��ǩ
     * 
     * @param element Ԫ��
     * @param type ��ǩ����
     */
    public void readLabel(Element element, DDLType type)
    {
        DDLData ddlData = new DDLData();
        String sql = element.getTextTrim();
        String strRndId = UUIDHelper.getUUID();
        ddlData.setId(strRndId);
        ddlData.setSql(sql);
        ddlData.setType(type);
        for (Iterator<Attribute> it = element.attributeIterator(); it.hasNext();)
        {
            try
            {
                Attribute at = it.next();
                String name = at.getName().trim();
                String value = at.getValue().trim();
                if ("id".equalsIgnoreCase(name))
                {
                    ddlData.setId(value);
                }
                else if ("tableName".equalsIgnoreCase(name))
                {
                    ddlData.setTableName(value);
                }
                else if ("checkTable".equalsIgnoreCase(name))
                {
                    if ("true".equalsIgnoreCase(value))
                        ddlData.setCheckTable(true);
                    else if ("false".equalsIgnoreCase(value))
                        ddlData.setCheckTable(false);
                }
                else if ("autoRunable".equalsIgnoreCase(name))
                {
                    if ("true".equalsIgnoreCase(value))
                        ddlData.setAutoRunable(true);
                    else if ("false".equalsIgnoreCase(value))
                        ddlData.setAutoRunable(false);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        sqlMapConfig.addDDLData(ddlData.getId(), ddlData);
    }
    
    public SqlMapConfiguration getSqlMapConfig()
    {
        return sqlMapConfig;
    }
    
    public void setSqlMapConfig(SqlMapConfiguration sqlMapConfig)
    {
        this.sqlMapConfig = sqlMapConfig;
    }
    
}
