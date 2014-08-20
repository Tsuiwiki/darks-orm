/**
 * ����:PythonBuilder.java
 * ����:������
 * ����ʱ��:2012-6-11
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.core.aspect.jython;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import darks.orm.core.data.xml.AspectData;
import darks.orm.util.StringHelper;

public final class PythonBuilder
{
    
    private static final int INIT_HEADER_BUFFER_SIZE = 128;
    
    private static final int INIT_TAIL_BUFFER_SIZE = 64;
    
    private static final int INIT_BODY_BUFFER_SIZE = 256;
    
    public static final String JY_ESPECT_CLASS = "__ESPECT_CLASS";
    
    public static final String JY_ESPECT_BEFORE = "before";
    
    public static final String JY_ESPECT_AFTER = "after";
    
    private static final ConcurrentMap<Integer, String> pyMap = new ConcurrentHashMap<Integer, String>();
    
    public static StringBuffer pythonHead = null;
    
    static
    {
        buildJythonHeader();
    }
    
    private PythonBuilder()
    {
        
    }
    
    public static final void buildJythonHeader()
    {
        pythonHead = new StringBuffer(INIT_HEADER_BUFFER_SIZE);
        // pythonHead.append("import sys\n");
        pythonHead.append("class IAspect:\n");
        pythonHead.append("	def before(self):\n");
        pythonHead.append("		return 1\n");
        pythonHead.append("	def after(self):\n");
        pythonHead.append("		return 1\n");
    }
    
    /**
     * ��ʼ���ű�����
     * 
     * @param className
     * @return
     */
    public static final String buildJythonTail(String className)
    {
        StringBuffer pyContent = new StringBuffer(INIT_TAIL_BUFFER_SIZE);
        pyContent.append(JY_ESPECT_CLASS);
        pyContent.append(" = ");
        pyContent.append(className);
        pyContent.append("()");
        return pyContent.toString();
    }
    
    /**
     * ���������ű�
     * 
     * @param queryData ��ѯ����
     * @param className �ű�������,�ӿ�IAspect��ʵ��
     * @param python �ű�
     * @return �����ű�
     */
    public static String buildJython(AspectData espectData, String className, String python)
    {
        if (espectData == null || python == null)
            return null;
        int key = espectData.hashCode();
        String ret = pyMap.get(key);
        if (ret != null)
            return ret;
        synchronized (espectData)
        {
            ret = pyMap.get(key);
            if (ret != null)
                return ret;
            String tail = buildJythonTail(className);
            StringBuffer pyContent = new StringBuffer(pythonHead.length() + INIT_BODY_BUFFER_SIZE + tail.length());
            pyContent.append(pythonHead);
            // BODY
            python = buildBody(python);
            // python = python.trim();
            pyContent.append(python);
            // TAIL
            pyContent.append("\n");
            pyContent.append(tail);
            ret = pyContent.toString();
            pyMap.put(key, ret);
            return ret;
        }
    }
    
    public static String buildBody(String className, String before, String after)
    {
        StringBuffer buffer = new StringBuffer(INIT_BODY_BUFFER_SIZE);
        buffer.append("class ");
        buffer.append(className);
        buffer.append("(IAspect):\n");
        if (before != null && !"".equals(before))
        {
            buffer.append("\tdef before(self):\n");
            buffer.append(StringHelper.lineTrim(before, "\t\t"));
            buffer.append("\n");
        }
        if (after != null && !"".equals(after))
        {
            buffer.append("\tdef after(self):\n");
            buffer.append("\t\t");
            buffer.append(StringHelper.lineTrim(after, "\t\t"));
            buffer.append("\n");
        }
        return buffer.toString();
    }
    
    public static final String buildBody(String python)
    {
        return StringHelper.lineTrim(python);
    }
    
    public static StringBuffer getPythonHeader()
    {
        return pythonHead;
    }
    
}
