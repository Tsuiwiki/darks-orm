/**
 * ����:StringHelper.java
 * ����:������
 * ����ʱ��:2012-5-30
 * �汾:1.0.0 alpha 
 * ��Ȩ:CopyRight(c)2012 ������  ����Ŀ��������Ȩ������������  
 * ����:
 */

package darks.orm.util;

public class StringHelper
{
    
    /**
     * ȥ��ÿ����Ч�ַ�
     * 
     * @param s �ַ���
     * @return
     */
    public static String lineTrim(String s)
    {
        return lineTrim(s, null);
    }
    
    /**
     * ȥ��ÿ����Ч�ַ�
     * 
     * @param s �ַ���
     * @return
     */
    public static String lineTrim(String s, String lineEx)
    {
        int len = s.length();
        int st = 0;
        int start = 0;
        
        while ((st < len))
        {
            if (s.charAt(st) >= ' ')
            {
                break;
            }
            else if (s.charAt(st) == '\n' || s.charAt(len - 1) == '\r')
            {
                start = st + 1;
            }
            st++;
        }
        
        while ((st < len))
        {
            if (s.charAt(len - 1) <= ' ')
                len--;
            else
                break;
        }
        s = s.substring(start, len);
        
        String[] args = null;
        if (s.indexOf("\n") >= 0)
        {
            args = s.split("\n");
        }
        else
        {
            args = new String[] {s};
        }
        st = 0;
        for (int i = 0; i < args[0].length(); i++)
        {
            if (s.charAt(i) <= ' ')
            {
                st++;
            }
            else
            {
                break;
            }
        }
        StringBuffer buf = new StringBuffer();
        for (String line : args)
        {
            line = line.substring(st);
            if (lineEx != null)
            {
                buf.append(lineEx);
            }
            buf.append(line);
            buf.append("\n");
        }
        return buf.toString();
    }
    
    /**
     * ��д���ַ�
     * 
     * @param type �ַ���
     * @return
     */
    public static String upperHeadWord(String type)
    {
        String head = type.substring(0, 1);
        return type.replaceFirst(head, head.toUpperCase());
    }
}
