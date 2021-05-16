import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Func_compN {

    public  void compare(String base,String com,int judge,int loc) throws IOException {

        //1,我们对于CCFinder中得到的结果，以项目名作为输入，目前对比的是是否有共同修改，所以需要读取克隆对和函数对
        //传进来base是C-bus-6.2.0-3-f,com是C-bus-6.1.0-3-f
        String firstArray[] = base.split("-");
        String secondArray[] = com.split("-");

        File baseCloneFile = new File("D:\\CrossFunction\\" + firstArray[0] + "-" + firstArray[1] + "-" + firstArray[2] + "\\" + firstArray[1] + "-" + firstArray[2] + "-" + firstArray[3] + ".xml");
        File comCloneFile = new File("D:\\CrossFunction\\" + secondArray[0] + "-" + secondArray[1] + "-" + secondArray[2] + "\\" +  secondArray[1] + "-" + secondArray[2] + "-" + secondArray[3] +".xml");

        File baseFunFile = new File("D:\\CrossFunction\\" + firstArray[0] + "-" + firstArray[1] + "-" + firstArray[2] +"\\" + firstArray[1] + "-" + firstArray[2] + "-" + firstArray[3] + "-" + firstArray[4] + ".xml");
        File comFunFile = new File("D:\\CrossFunction\\" + secondArray[0] + "-" + secondArray[1] + "-" + secondArray[2] + "\\" + secondArray[1] + "-" + secondArray[2] + "-" + secondArray[3] + "-" + secondArray[4]+ ".xml");

        List<List<String>> baseCloneList = readCloFile(baseCloneFile);
        List<List<String>> comCloneList = readCloFile(comCloneFile);

        List<List<String>> baseFunList = readFunFile(baseFunFile);
        List<List<String>> comFunList = readFunFile(comFunFile);


        //2，需要对两个版本的克隆对文件进行比对信息的添加
        //传进方法的是bus-6.2.0-3和bus-6.2.0-3-f
        //这里就是对克隆对文件进行信息的条件，比如：
        // <clone nlines="47" similarity="72">
        //    <source file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498" pcid="25830"/>
        //    <source file="../experiments/java/bus-6.2.0/bus/bus-core/src/main/java/org/aoju/bus/core/toolkit/UriKit.java" startline="1245" endline="1294" pcid="8403"/>
        //</clone>
        //public Map < String, String > parseStringToMap
        //方法后面的参数
        //public static Map < String, List < String > > decodeObj
        //方法后面的参数
        List<List<String>> newBaseCloneList = matchFunName(baseCloneList,baseFunList);
        List<List<String>> newComCloneList = matchFunName(comCloneList,comFunList);


        //writeTest(newBaseCloneList,baseCloneFile.getName() + "base文件的克隆对信息文件");
        //writeTest(newComCloneList,comCloneFile.getName() + "Com文件的克隆对信息文件");

        //3，需要对两个函数文件内容，按照上一步的结果按base顺序存放在一个list中
        //也就是遍历了newBaseCloneList的信息，并在newComCloneList中去寻找对应的信息，存在对应就添加，不存在就加空值
        List<List<String>> adjustCom = new ArrayList<>();
        List<List<String>> perhapesBase = new ArrayList<>();
        List<List<String>> perhapesCom = new ArrayList<>();
        int flag;

        for(List<String> baseclone : newBaseCloneList ){
            List<List<String>> tempBase = new ArrayList<>();
            List<List<String>> tempCom = new ArrayList<>();

            //上面两个是服务名 + 类名 + 方法名；下面两个是方法参数
            String firstBaseInfo = getService(baseclone.get(1),loc) + baseclone.get(4);
            String secondBaseInfo = getService(baseclone.get(2),loc) + baseclone.get(6);
            String firstBaseVar = baseclone.get(5);
            String secondBaseVar = baseclone.get(7);

           /* 参数相同即为完全匹配，直接放在temp里面，参数相同只可能匹配一次。若有完全匹配出站，则将tempBase和tempCom清空，结束循环
            参数不同放在tempBase和tempCom中去，若对于某个Base在顺序中获得一个匹配，则倒序中不再进行匹配

            顺序匹配：
              1，参数相同
              清空两个temp，放进temp，
              结束循环
              2，参数不同
              标志设置为1，放进两temp

            倒序匹配
              1，参数相同
              清空两个temp，放进temp，
              结束循环
              2，参数不同
              若标志为1，则跳过，否则放入两个temp*/

            List<String> temp = new ArrayList<>();
            for(List<String> comclone : newComCloneList){

                flag = 0;//防止在tempCom中重复添加

                //上面两个是服务名 + 类名 + 方法名；下面两个是方法参数
                String firstComInfo = getService(comclone.get(1),loc)+ comclone.get(4);
                String secondComInfo = getService(comclone.get(2),loc) + comclone.get(6);
                String firstComVar = comclone.get(5);
                String secondComVar = comclone.get(7);

                //顺序匹配
                if(firstBaseInfo.equals(firstComInfo) && secondBaseInfo.equals(secondComInfo)){
                    if(firstBaseVar.equals(firstComVar) && secondBaseVar.equals(secondComVar)){
                        tempBase.clear();
                        tempCom.clear();
                        temp = comclone;
                        break;

                    }else{
                        tempBase.add(baseclone);
                        tempCom.add(comclone);
                        flag = 1;
                    }
                }
                if (firstBaseInfo.equals(secondComInfo) && secondBaseInfo.equals(firstComInfo)){
                    List<String> revTemp = new ArrayList<>();
                    if(firstBaseVar.equals(secondComVar) && secondBaseInfo.equals(firstComInfo)){
                        tempBase.clear();
                        tempCom.clear();
                        revTemp.add(comclone.get(0));
                        revTemp.add(comclone.get(2));
                        revTemp.add(comclone.get(1));
                        revTemp.add(comclone.get(3));
                        revTemp.add(comclone.get(6));
                        revTemp.add(comclone.get(7));
                        revTemp.add(comclone.get(4));
                        revTemp.add(comclone.get(5));
                        temp = revTemp;
                        break;
                    }else{
                        if(flag == 0){
                            revTemp.add(comclone.get(0));
                            revTemp.add(comclone.get(2));
                            revTemp.add(comclone.get(1));
                            revTemp.add(comclone.get(3));
                            revTemp.add(comclone.get(6));
                            revTemp.add(comclone.get(7));
                            revTemp.add(comclone.get(4));
                            revTemp.add(comclone.get(5));

                            tempBase.add(baseclone);
                            tempCom.add(revTemp);
                        }
                    }
                }
            }
            if(!temp.isEmpty()){
                adjustCom.add(temp);
            }else{
                temp = null;
                adjustCom.add(temp);
            }
            if(tempBase.size() > 0 && tempCom.size() > 0){
                for(int i = 0;i < tempBase.size();i++){
                    perhapesBase.add(tempBase.get(i));
                }
                for(int i = 0;i < tempCom.size();i++){
                    perhapesCom.add(tempCom.get(i));
                }
            }
        }
        //writeTest(adjustCom,baseCloneFile.getName() + "base文件的匹配文件");
        //writeTest(perhapesBase,baseCloneFile.getName() + "疑似base文件的匹配文件");
        //writeTest(perhapesCom,baseCloneFile.getName() + "疑似com文件的匹配文件");

        //4，从上我们得到了newBaseCloneList和adjustCom，以及perhapesBase和perhapesCom，我们便可以对克隆对对应的方法进行判断

        //result中存放克隆对以及函数修改的具体信息，funinfo中最新版本改动的函数信息
        List<List<String>> result = new ArrayList<>();//这里要放克隆对信息，以及比较的函数信息
        List<List<String>> funInfo = new ArrayList<>();//这里记录最新版本中的函数的改变情况

        //首先是newBaseCloneList和adjustCom的比对
        int lenChange = 0,infoChange = 0;
        int i = 0;
        int l = newBaseCloneList.size();

        for(i = 0;i < l;i++){
            if(adjustCom.get(i)== null){
                continue;
            }
            //取出newBaseCloneList和adjustCom的一条信息，这里取出的是克隆对的信息，然后split，取出的是
            //file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498"
            String[] temp1 = newBaseCloneList.get(i).get(1).trim().split(" ");
            String[] temp2 = adjustCom.get(i).get(1).trim().split(" ");
            String[] temp3 = newBaseCloneList.get(i).get(2).trim().split(" ");
            String[] temp4 = adjustCom.get(i).get(2).trim().split(" ");

            //对比的就是s1与ss1对应的方法，s2和ss2对应的方法
            String s1 = temp1[1] + " " + temp1[2] + " " + temp1[3];
            String ss1 = temp2[1] + " " + temp2[2] + " " + temp2[3];
            String s2 = temp3[1] + " " + temp3[2] + " " + temp3[3];
            String ss2 = temp4[1] + " " + temp4[2] + " " + temp4[3];
            //取出对应的方法，这是注意，对比的时候是sf1对ssf1，sf2对ssf2。对比时，先比较长度，若两对的长度都不一样，那就是
            //发生过改变，接着对比改动，若两对都进行修改过，则先记录，放在result中，先放sf1，ssf1，sf2，ssf2，再放函数体
            List<String> sf1 = getFun(s1,baseFunList);
            List<String> ssf1 = getFun(ss1,comFunList);
            List<String> sf2 = getFun(s2,baseFunList);
            List<String> ssf2 = getFun(ss2,comFunList);

            //从temp[2] tempp[3]中取出数字，来进行进一步的判断
            int sn1s = getNum(temp1[2]);
            int sn1e = getNum(temp1[3]);

            int ssn1s = getNum(temp2[2]);
            int ssn1e = getNum(temp2[3]);

            int sn2s = getNum(temp3[2]);
            int sn2e = getNum(temp3[3]);

            int ssn2s = getNum(temp4[2]);
            int ssn2e = getNum(temp4[3]);


            if((sf1.size() != ssf1.size()) || (sf2.size() != ssf2.size())){
                //这里有可能会出现误报，需要进行进一步的处理
                if( (sn1e == ssn1e && sn1s == ssn1s) || (sn2e == ssn2e && sn2s == ssn2s)
                        || sn1e - sn1s == ssn1e - ssn1s || sn2e - sn2s == ssn2e - ssn2s ){
                    continue;
                }

                lenChange++;
                int change = Math.abs(sf1.size()-ssf1.size());
                String changeInfo = Integer.toString(change);

                List<String> temp = new ArrayList<>();
                temp.add(s1);
                temp.add(ss1);
                temp.add(s2);
                temp.add(ss2);
                temp.add(changeInfo);
                result.add(temp);
                result.add(sf1);
                result.add(ssf1);
                result.add(sf2);
                result.add(ssf2);

                //输出函数对文件时，做好标记，便于后期的处理
                List<String> start = new ArrayList<>();
                start.add("start" + "-" + sf1.size() + "-" +  sf2.size() + "-" + changeInfo);
                funInfo.add(start);

                List<String> info = new ArrayList<>();
                info.add(s1);
                info.add(s2);

                funInfo.add(info);
                funInfo.add(sf1);
                funInfo.add(sf2);

            }else if((sf1.size() == ssf1.size()) && (sf2.size() == ssf2.size())) {

                if( (sn1e == ssn1e && sn1s == ssn1s) && (sn2e == ssn2e && sn2s == ssn2s)){
                    continue;
                }

                List<Integer> mark1 = compare(sf1, ssf1);
                List<Integer> mark2 = compare(sf2, ssf2);

                if (mark1.size() == 0 || mark2.size() == 0) {
                    continue;
                } else {
                    infoChange++;
                    List<String> temp = new ArrayList<>();

                    temp.add(s1);
                    temp.add(ss1);
                    temp.add(s2);
                    temp.add(ss2);
                    result.add(temp);

                    result.add(markInfo(sf1,mark1));
                    result.add(markInfo(ssf1,mark1));
                    result.add(markInfo(sf2,mark2));
                    result.add(markInfo(ssf2,mark2));

                    //输出函数对文件时，做好标记，便于后期的处理
                    List<String> start = new ArrayList<>();
                    start.add("start" + "-" +  sf1.size() + "-" +  sf2.size() + "-" +  mark1.size() + "-" + mark2.size());
                    funInfo.add(start);

                    List<String> info = new ArrayList<>();
                    info.add(s1);
                    info.add(s2);

                    funInfo.add(info);
                    funInfo.add(sf1);
                    funInfo.add(sf2);

                }
            }
        }

        List<String> info = new ArrayList<>();
        String s_1 = "长度改变的函数数量有：" + lenChange + "内容改变的函数数量有：" + infoChange;
        info.add(s_1);
        result.add(0,info);

        List<String> info1 = new ArrayList<>();
        String s_2 = "以下是" + base + "对比" + com + "发生函数变化的内容";
        info1.add(s_2);
        funInfo.add(0,info1);

        writeResult(result,judge,base,com,firstArray,secondArray,"确定");
        writeFun(funInfo,judge,base,com,firstArray,secondArray,"确定");

        result.clear();
        funInfo.clear();
        //然后是perhapesBase和perhapesCom的对比
        i = 0;
        l = perhapesBase.size();
        for(i = 0;i < l;i++){
            // <clone nlines="47" similarity="72">
            //    <source file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498" pcid="25830"/>
            //    <source file="../experiments/java/bus-6.2.0/bus/bus-core/src/main/java/org/aoju/bus/core/toolkit/UriKit.java" startline="1245" endline="1294" pcid="8403"/>
            //</clone>

            String[] temp1 = perhapesBase.get(i).get(1).trim().split(" ");
            String[] temp2 = perhapesCom.get(i).get(1).trim().split(" ");
            String[] temp3 = perhapesBase.get(i).get(2).trim().split(" ");
            String[] temp4 = perhapesCom.get(i).get(2).trim().split(" ");

            //file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498"
            //对比的就是s1与ss1对应的方法，s2和ss2对应的方法
            String s1 = temp1[1] + " " + temp1[2] + " " + temp1[3];
            String ss1 = temp2[1] + " " + temp2[2] + " " + temp2[3];
            String s2 = temp3[1] + " " + temp3[2] + " " + temp3[3];
            String ss2 = temp4[1] + " " + temp4[2] + " " + temp4[3];

            //从temp[2] tempp[3]中取出数字，来进行进一步的判断
            int sn1s = getNum(temp1[2]);
            int sn1e = getNum(temp1[3]);

            int ssn1s = getNum(temp2[2]);
            int ssn1e = getNum(temp2[3]);

            int sn2s = getNum(temp3[2]);
            int sn2e = getNum(temp3[3]);

            int ssn2s = getNum(temp4[2]);
            int ssn2e = getNum(temp4[3]);


            //取出对应的方法，这是注意，对比的时候是sf1对ssf1，sf2对ssf2。对比时，先比较长度，若两对的长度都不一样，那就是
            //发生过改变，接着对比改动，若两对都进行修改过，则先记录，放在result中，先放sf1，ssf1，sf2，ssf2，再放函数体
            List<String> sf1 = getFun(s1,baseFunList);
            List<String> ssf1 = getFun(ss1,comFunList);
            List<String> sf2 = getFun(s2,baseFunList);
            List<String> ssf2 = getFun(ss2,comFunList);

            if((sf1.size() != ssf1.size()) || (sf2.size() != ssf2.size())){
                if( (sn1e == ssn1e && sn1s == ssn1s) || (sn2e == ssn2e && sn2s == ssn2s)
                    || sn1e - sn1s == ssn1e - ssn1s || sn2e - sn2s == ssn2e - ssn2s ){
                    continue;
                }

                List<String> temp = new ArrayList<>();
                temp.add(s1);
                temp.add(ss1);
                temp.add(s2);
                temp.add(ss2);
                result.add(temp);
                result.add(sf1);
                result.add(ssf1);
                result.add(sf2);
                result.add(ssf2);

                List<String> mid = new ArrayList<>();
                mid.add(s1);
                mid.add(s2);
                funInfo.add(mid);
                funInfo.add(sf1);
                funInfo.add(sf2);

            }else if((sf1.size() == ssf1.size()) && (sf2.size() == ssf2.size())) {
                if( (sn1e == ssn1e && sn1s == ssn1s) && (sn2e == ssn2e && sn2s == ssn2s)){
                    continue;
                }

                List<Integer> mark1 = compare(sf1, ssf1);
                List<Integer> mark2 = compare(sf2, ssf2);

                if (mark1.size() == 0 || mark2.size() == 0) {
                    continue;
                } else {
                    List<String> temp = new ArrayList<>();

                    temp.add(s1);
                    temp.add(ss1);
                    temp.add(s2);
                    temp.add(ss2);
                    result.add(temp);

                    result.add(markInfo(sf1,mark1));
                    result.add(markInfo(ssf1,mark1));
                    result.add(markInfo(sf2,mark1));
                    result.add(markInfo(ssf2,mark1));

                    List<String> mid = new ArrayList<>();
                    mid.add(s1);
                    mid.add(s2);
                    funInfo.add(mid);
                    funInfo.add(sf1);
                    funInfo.add(sf2);
                }
            }
        }
        writeResult(result,judge,base,com,firstArray,secondArray,"疑似");
        writeFun(funInfo,judge,base,com,firstArray,secondArray,"疑似");
    }

    public List<Integer> compare(List<String> first,List<String> second){
        int k = first.size();
        List<Integer> mark = new ArrayList<>();

        //这里要判断，如果添加的mark信息是连续的几个数字，并且这个连续是大于4的，这个就不当做一个正常的修改操作
        int count = 0;//用count值来进行记录连续的数目
        int temp = 0;//用这个来存放上一个值来进行对比

        for(int i = 2;i < k;i++){
            if(!(first.get(i).trim()).equals(second.get(i).trim())){
                char[] c1 = first.get(i).trim().toCharArray();
                char[] c2 = second.get(i).trim().toCharArray();
                int sum1 = 0;
                int sum2 = 0;
                for(char a : c1){
                    sum1 += a;
                }
                for(char a : c2){
                    sum2 += a;
                }
                if(sum1 == sum2){
                    continue;
                }

                if(mark.size() > 0){
                    temp = mark.get(mark.size() - 1);
                    if(temp + 1 == i){
                        count ++;
                    }else{
                        count = 0;
                    }
                }

                if(count >=3){
                    mark.clear();
                    return  mark;
                }else{
                    mark.add(i);
                }
            }
        }
        return mark;

    }





    public  List<String> getFun(String info,List<List<String>> funInfo){
        List<String> s = null;
        for(List<String> l : funInfo){
            String head = l.get(0);
            if(head.contains(info)){
                s = l;
                break;
            }
        }
        return s;
    }




    public  List<List<String>> matchFunName(List<List<String>> baselists,List<List<String>> funlists){

        for(List<String> baseClone : baselists){
            String[] temp1 = baseClone.get(1).trim().split(" ");// <source file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498" pcid="25830"/>

            String[] temp2 = baseClone.get(2).trim().split(" ");


            String first = temp1[0] + " " +  temp1[1] + " " + temp1[2] + " " +  temp1[3];// <source file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498"
            String second = temp2[0] + " " +  temp2[1] + " " + temp2[2] + " " +  temp2[3];//<source file="../experiments/java/bus-6.2.0/bus/bus-core/src/main/java/org/aoju/bus/core/toolkit/UriKit.java" startline="1245" endline="1294"

            //在函数文件中， 去查询对应的函数名
            for(List<String> baseFun : funlists){
                String temp3 = baseFun.get(0);
                if(temp3.contains(first)){
                    String funName = getFunName(baseFun.get(1));//这里操作的是方法头，如：public Map < String, String > parseStringToMap (String str, boolean decode) {
                    String funVar = getFunVar(baseFun.get(1));
                    baseClone.add(4,funName );//添加在克隆对文件的后面，作为比对信息出现，如：public Map < String, String > parseStringToMap
                    baseClone.add(5,funVar);//将方法的参数添加在后面，比如(String str, boolean decode)
                    break;
                }
            }
            for(List<String> baseFun : funlists){
                String temp3 = baseFun.get(0);
                if(temp3.contains(second)){
                    String funName = getFunName(baseFun.get(1));//这里操作的就是另一个方法头
                    String funVar = getFunVar(baseFun.get(1));
                    baseClone.add(6,funName);
                    baseClone.add(7,funVar);

                    break;
                }

            }

        }
        return baselists;
    }



    public  List<List<String>> readFunFile(File file){
        String s = null;
        String begin=null;
        List<List<String>> result = new ArrayList<>();
        List<String> function = new ArrayList<>();
        try {  //这里是按行进行读取的
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                if(s.contains("<source")){
                    begin = s;
                }else if(!s.contains("<source") && !s.contains("</source>")){
                    function.add(s);
                }else if(s.contains("</source>")){
                    function.add(0,begin);
                    function.add(s);
                    result.add(function);
                    begin = null;
                    function = new ArrayList<>();
                }

            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }


    public  List<List<String>> readCloFile(File file){
        String s = null;
        String begin=null;
        List<List<String>> result = new ArrayList<>();
        List<String> clone = new ArrayList<>();
        try {  //这里是按行进行读取的
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                if(s.contains("<clone")){
                    begin = s;
                }else if(!s.contains("<clone") && !s.contains("</clone>") && !s.equals("") && !s.equals("<clones>")  && !s.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") && !s.equals("</clones>")){
                    clone.add(s);
                }else if(s.contains("</clone>")){
                    clone.add(0,begin);
                    clone.add(s);
                    result.add(clone);
                    begin = null;
                    clone = new ArrayList<>();
                }

            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    public  String getFunName(String s){
        String[] strings = s.split(" ");
        String  result = "";
        int i = 0;
        int k = 0;
        while(k < strings.length){//
            if(strings[k].contains("(")){
                i = k;
                break;
            }
            k++;
        }
        k = 0;
        while(k < i){
            result  = result + strings[k] + " ";
            k++;
        }
        return result;
    }

    public  String getFunVar(String s){
        String[] strings = s.split(" ");
        String  result = "";
        int i = 0;
        int k = 0;
        while(k < strings.length){//
            if(strings[k].contains("(")){
                i = k;
                break;
            }
            k++;
        }
        k = strings.length;
        while( i < k){
            result  = result + strings[i] + " ";
            i++;
        }
        return result;
    }


    //传进来的内容是 <source file="../experiments/java/bus-6.2.0/bus/bus-oauth/src/main/java/org/aoju/bus/oauth/provider/AbstractProvider.java" startline="430" endline="498" pcid="25830"/>
    //此处做调整，返回的信息是：服务名+类名
    public  String getService(String s,int loc){
        String[] strings = s.trim().split(" ");
        String s1 = strings[1];
        String[] strings1 = s1.split("/");
        return strings1[loc] + strings1[strings1.length - 1] + strings1[strings1.length - 2] + strings1[strings1.length - 3];
    }

    public List<String> markInfo(List<String> info,List<Integer> mark){
        List<String> res = new ArrayList<>();
        res = info;
        for(Integer m:mark){
            if(res.get(m).contains("!-!-!")){
                continue;
            }
            res.set(m,"!-!-!" + res.get(m));
        }
        return res;
    }
    //生成对比文件的函数，注意最后一个参数说明目前生成的是确定还是疑似
    public void writeResult(List<List<String>> result,int judge,String base,String com,String[] firstArray,String[] secondArray,String type) throws IOException {
        String path = null;
        File f = null;
        if(judge == 1){
            String p = "D:" + f.separator + "CrossFunction" + f.separator + "1comp" +  f.separator + "C-" + firstArray[1] +  f.separator + secondArray[1] + "-" +  secondArray[2];

            File dir = new File(p);
            dir.mkdirs();
            path = p + f.separator + "基于" + base + type +"对比"+ com + ".xml";

        }else{
            String p = "D:" + f.separator + "CrossFunction" + f.separator + "1comp" +  f.separator + "I-" + firstArray[1] +  f.separator + secondArray[1] + "-" +  secondArray[2];

            File dir = new File(p);
            dir.mkdirs();
            path = p + f.separator +"基于" + base + type + "对比"+ com + ".xml";
        }

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
            for (List<String> rl : result) {
                for (String s : rl) {
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

    //生成对比文件的函数，注意最后一个参数说明目前生成的是确定还是疑似
    public void writeFun(List<List<String>> funInfo,int judge,String base,String com,String[] firstArray,String[] secondArray,String type) throws IOException {
        File f = null;
        String path1 = null;

        if(judge == 1){
            path1 = "D:" + f.separator + "CrossFunction" + f.separator + "1comp" +  f.separator + "C-" + firstArray[1] + f.separator + secondArray[1] + "-" + secondArray[2] + f.separator + base + type + "的函数变动文件.xml";
        }else{
            path1 = "D:" + f.separator + "CrossFunction" + f.separator + "1comp" +  f.separator + "I-" + firstArray[1] + f.separator + secondArray[1] + "-" + secondArray[2] + f.separator + base + type + "的函数变动文件.xml";
        }
        System.out.println(path1);

        File file1 = new File(path1);
        if (!file1.exists()) {
            file1.createNewFile();
        }
        FileOutputStream fos1 = null;
        OutputStreamWriter osw1 = null;
        BufferedWriter bw1 = null;

        try {
            fos1 = new FileOutputStream(file1,true);
            osw1 = new OutputStreamWriter(fos1, "UTF-8");
            bw1 = new BufferedWriter(osw1);
            for (List<String> r2 : funInfo) {
                for (String ss : r2) {
                    bw1.write(ss + "\n");
                    bw1.flush();
                }
            }
            System.out.println("生成" + type +"函数变动文件" + base);
            bw1.close();
            osw1.close();
            fos1.close();

        } catch (Exception e) {
            System.out.println("生成" + type +"函数变动文件失败" + base);
            e.printStackTrace();
        }


    }

    public void writeTest(List<List<String>> list,String name){
        File file = new File("C:\\Users\\15215\\Desktop\\" + name + ".xml");

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            for (List<String> rl : list) {
                if(rl == null){
                    bw.write("这里啥都没有");
                    continue;
                }else{
                    for (String s : rl) {
                        bw.write(s + "\n");
                        bw.flush();
                    }
                }

            }
            bw.close();
            osw.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //这里传入startline="？？？" 或者  endline="？？？"
    public int getNum(String info){
        String[] inf = info.split("=");
        String s = inf[1];
        String num = s.substring(1,s.length()-1);
        return Integer.parseInt(num);
    }
}
