import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CPFinder {

    //这个方法传入克隆对文件路径信息，然后生成一个关于跨服务克隆对的信息，返回这个文件的路径
    public String findcross(String filename,int loc,int flag) throws DocumentException {
        String[] filea = filename.split("-");
        String file1 = filea[0] + "-" + filea[1];
        String dirname = "D:\\CloneOrigin\\" + file1 + "\\"+ filename + ".xml";

        File f = new File(dirname);
        SAXReader reader = new SAXReader();
        Document document = reader.read(f);
        Element root = document.getRootElement();
        List<Element> list = root.elements("clone");//到这里，我们取到了根标签clone，作为根元素

        //1，历一次文件，并除去有test，exmaple，demo，util等source元素
        List<Element> temp = new ArrayList<Element>();
        List<Element> res = new ArrayList<Element>();


        for(int i = 0;i < list.size();i++){
            Element source = list.get(i);
            Iterator it = source.elementIterator("source");//拿到了第i个source的iterator
            List<String> info = new ArrayList<String>();

            while(it.hasNext()){
                Element node = (Element) it.next();//拿到了source对应的元素
                info.add(node.attributeValue("file"));//source对应的file等信息作为String存储起来
                info.add(node.attributeValue("startline"));
                info.add(node.attributeValue("endline"));
                info.add(node.attributeValue("pic"));
            }

            String first = info.get(0).toLowerCase();
            String second = info.get(4).toLowerCase();

//            //如果发现项目中有这包含这些词，这直接跳过该node
//            if(first.contains("test") || first.contains("example") || first.contains("demo")
//                    || second.contains("test") || second.contains("example") || second.contains("demo")
//                    || first.contains("sample") || second.contains("sample")){
//                continue;
//            }

            /*//更新dif的值，现在由函数传进这个值，这里不再进行判断
            String firstArray[] = first.split("/");
            String secondArray[] = second.split("/");
            int min = firstArray.length < secondArray.length ? firstArray.length : secondArray.length;

            for(int j = 0;j < min;j++){
                if(!firstArray[j].equals(secondArray[j])){
                    if(dif > j){
                        dif = j;
                    }
                    break;
                }
            }*/


            //到这里说明该node是不包含哪些词的，应该放在temp中进行进一步的分析
            temp.add(list.get(i));
        }

        //2，服务的位置为loc，以及除去了无关的代码后，我们可以进行服务的比对,放在一个res里面
        for(int i = 0;i < temp.size();i++) {
            Element source = temp.get(i);
            Iterator it = source.elementIterator("source");//拿到了第i个source的iterator
            List<String> info = new ArrayList<String>();

            while (it.hasNext()) {
                Element node = (Element) it.next();//拿到了source对应的元素
                info.add(node.attributeValue("file"));//source对应的file等信息作为String存储起来
            }

            String first = info.get(0).toLowerCase();
            String second = info.get(1).toLowerCase();

            String firstArray[] = first.split("/");
            String secondArray[] = second.split("/");

            if(flag == 1){
                if(!firstArray[loc].equals(secondArray[loc])){
                    res.add(temp.get(i));
                }
            }else{
                if(firstArray[loc].equals(secondArray[loc])){
                    res.add(temp.get(i));
                }
            }
        }

        //3，获得了最后的res，那么我们就要写进一个结果文件啦

        Document doc= DocumentHelper.createDocument();
        Element resRoot=doc.addElement("clones");

        Iterator<Element> resEle = res.iterator();
        while(resEle.hasNext()){
            Element e = (Element)resEle.next().clone();
            doc.getRootElement().add(e);
        }

        File file = null;//这里控制一下输出文件的位置和名字
        String path,resfile;

        if(flag == 1){
            path = "D:"+f.separator+"CrossFunction"+f.separator+ "C-" + file1 ;
            resfile = "D:"+f.separator+"CrossFunction"+f.separator+ "C-" + file1 + f.separator + filename + ".xml";
        }else{
            path = "D:"+f.separator+"CrossFunction"+f.separator+ "I-" + file1;
            resfile = "D:"+f.separator+"CrossFunction"+f.separator+ "I-" + file1  + f.separator + filename + ".xml";
        }

        File f1 = new File(path);
        if(!f1.exists()){//如果文件夹不存在
            f1.mkdir();//创建文件夹
        }

        f = new File(resfile);
        try {
            f.createNewFile();//则创建fileName这个文件
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(f);
            OutputFormat fmt=OutputFormat.createPrettyPrint();
            XMLWriter write=new XMLWriter(fos,fmt);
            write.write(doc);
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}



