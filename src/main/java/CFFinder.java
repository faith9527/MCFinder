import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.*;

public class CFFinder {

    //这个方法需要将之前获得的服务对文件传进去，以及所有函数的信息，返回跨服务函数的具体信息
    public void getFuntionInfo(String all, String cross,int flag) throws Exception {
        //1，先加载两个文件，克隆的信息文件和方法的详细内容文件
        File baseFile = new File("D:\\CloneOrigin\\" + all + ".xml");

        String[] filea = cross.split("-");
        String file1;
        File res = null;


        if(flag == 1){
            file1 = "C-" + filea[0] + "-" + filea[1];
            res = new File("D:\\CrossFunction\\" + file1 + "\\" + cross + ".xml");
        }else{
            file1 = "I-" + filea[0] + "-" + filea[1];
            res = new File("D:\\CrossFunction\\" + file1 + "\\" + cross + ".xml");
        }


        //2,遍历res，对于res中的每一个func，都在func里面找出来，并且放在另一个文件
        SAXReader reader = new SAXReader();
        Document result = reader.read(res);
        Element resRoot = result.getRootElement();
        List<Element> rsl = resRoot.elements("clone");

        //3，对于dirname1指代的所有函数的文件，这里使用IO流来进行解析，存放在map中

        Map<String, List<String>> function = new HashMap<>();//在最外面，使用一个map来存里面每个source，key是source这一行的内容，是一个string，value是一个List<String>

        try {  //接下来就按行读取文件，并存入result中
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(baseFile), "UTF-8"));//构造一个BufferedReader类来读取文件

            String key = null;
            String s = null;
            List<String> value = new ArrayList<>();

            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                if(s.length() == 0){
                    continue;
                }
                if (s.contains("<source") && !s.equals("</source>")) {
                    key = s;
                } else if (s.equals("</source>")) {
                    value.add(s);
                    function.put(key, value);
                    key = null;
                    value = new ArrayList<String>();
                } else {
                    value.add(s);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //4，对于res中的跨服务代码，取出每个source的信息，存在sourceinfo中，每三个为一条信息
        List<String> sourceInfo = new ArrayList<String>();
        for (int i = 0; i < rsl.size(); i++) {//这个循环里，取出了result中的每一个source，里面嵌套一个循环，与function进行对比
            Element source = rsl.get(i);
            Iterator it = source.elementIterator("source");//拿到了第i个source的iterator

            while (it.hasNext()) {
                Element node = (Element) it.next();
                sourceInfo.add(node.attributeValue("file"));
                sourceInfo.add(node.attributeValue("startline"));
                sourceInfo.add(node.attributeValue("endline"));
            }
        }

        //5，取出Listinfo中的信息，然后取出map中对应的内容，将其存在一个String的List中，注意添加结束信息
        int i = 0;
        String temp;
        List<List<String>> list = new ArrayList<>();
        List<String> templist ;
        while (i <= sourceInfo.size() - 3) {
            temp = "<source file=\"" + sourceInfo.get(i) + "\" " + "startline=\"" + sourceInfo.get(i + 1)
                    + "\" " + "endline=\"" + sourceInfo.get(i + 2) + "\">";
            i = i + 3;
            templist = new ArrayList<>();
            templist = function.get(temp);
            if(!templist.get(0).equals(temp)){
                templist.add(0, temp);
            }
            list.add(templist);
        }

        //6,将list写入文件中
        File f = null;
        String path = "D:" + f.separator + "CrossFunction" + f.separator  + file1 + f.separator  + cross + "-f" + ".xml";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            for (List<String> l : list) {
                for (String s : l) {
                    bw.write(s + "\n");
                    bw.flush();
                }
            }
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}










