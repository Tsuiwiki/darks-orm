package darks.orm.core.aspect.jython;

import darks.orm.app.QueryEnumType;
import darks.orm.core.data.xml.AspectData;
import darks.orm.core.data.xml.SimpleAspectWrapper;
import darks.orm.core.data.xml.QueryAspectWrapper;
import darks.orm.core.data.xml.AspectData.AspectType;
import darks.orm.core.factory.JythonFactory;
import darks.orm.exceptions.JythonAspectException;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class PythonParser
{
    
    public static String JY_ESPECT_DATA = "__DATA";
    
    /**
     * ����XML�ű�
     * 
     * @param queryData ��ѯ����
     * @param smEspectData ע������
     * @param methodType ������������ JY_ESPECT_BEFORE or JY_ESPECT_AFTER
     * @return �Ƿ����ִ��
     */
    public boolean parse(AspectData aspectData, SimpleAspectWrapper simpleWrapper, QueryEnumType queryEnumType,
        String methodType)
        throws JythonAspectException
    {
        if (aspectData == null)
            return true;
        PythonInterpreter pyInter = initPythonInterpreter(simpleWrapper);
        if (aspectData.getAspectType() == AspectType.JYTHON)
        {
            String content = PythonBuilder.buildJython(aspectData, aspectData.getClassName(), aspectData.getContent());
            try
            {
                pyInter.exec(content);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new JythonAspectException(e);
            }
        }
        else if (aspectData.getAspectType() == AspectType.PYFILE)
        {
            pyInter.exec(PythonBuilder.getPythonHeader().toString());
            pyInter.execfile(aspectData.getContent());
            pyInter.exec(PythonBuilder.buildJythonTail(aspectData.getClassName()));
        }
        else
        {
            return true;
        }
        PyObject espectObj = pyInter.get(PythonBuilder.JY_ESPECT_CLASS);
        PyObject retObj = espectObj.invoke(methodType);
        
        if (retObj == null || retObj instanceof PyNone)
        {
            return false;
        }
        else
        {
            Integer ret = (Integer)JythonFactory.getInstance().pyObjectToObject(retObj, Integer.class);
            if (ret == 0)
                return false;
            else
                return true;
        }
        
    }
    
    /**
     * ��ʼ��Python���沢���PythonInterpreter
     * 
     * @param queryData ��ѯ����
     * @param sql SQL���
     * @param values ѡ��ֵ
     * @param params ����
     * @param page ��ǰҳ
     * @param pageSize ��ҳ��С
     * @return PythonInterpreter ʵ��
     */
    public PythonInterpreter initPythonInterpreter(SimpleAspectWrapper simpleWrapper)
    {
        PythonInterpreter pyInter = JythonFactory.getInstance().getInterpreter();
        if (simpleWrapper instanceof QueryAspectWrapper)
        {
            pyInter.set(JY_ESPECT_DATA, (QueryAspectWrapper)simpleWrapper);
        }
        else
        {
            pyInter.set(JY_ESPECT_DATA, simpleWrapper);
        }
        return pyInter;
    }
    
}
