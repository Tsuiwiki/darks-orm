
package darks.orm.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper
{
    
    /**
     * �����Դ�ļ�����·��
     * 
     * @param filePath �ļ�·��
     * @return
     */
    public static String getResourcePath(String filePath)
    {
        String cfgPath = FileHelper.class.getResource(filePath).getFile();
        return cfgPath = cfgPath.replace("%20", " ");
    }
    
    /**
     * ���ͨ�����Դ�ļ�·��
     * 
     * @param filePath �ļ�·��
     * @return
     */
    public static List<String> getRegexResourceFiles(String filePath)
    {
        List<String> list = new LinkedList<String>();
        String dir = "";
        if (filePath.startsWith("/"))
        {
            dir = "/";
            filePath = filePath.substring(1);
        }
        String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File dirFile = new File(root);
        filePath = filePath.replace(".", "\\.");
        filePath = filePath.replace("*", ".*");
        filePath = filePath.replace("?", ".?");
        filePath = "^" + filePath + "$";
        Pattern reg = Pattern.compile(filePath);
        if (dirFile.isDirectory())
        {
            File[] files = dirFile.listFiles();
            for (File f : files)
            {
                if (f.isFile())
                {
                    Matcher m = reg.matcher(f.getName());
                    if (m.matches())
                    {
                        list.add(dir + f.getName());
                    }
                }
            }
        }
        return list;
    }
}
