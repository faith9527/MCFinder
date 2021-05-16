import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Counting {
    //这个方法中，读取某个项目某个类型的不不同版本对比文件，并统计其中修改的行数

    //传入项目的名字，进而读取不同的文件
    public void getDis(String file) {
        String filename = file;

        //1，得到对比结果文件的两个路径，其中包含着跨服务和服务内的对比结果文件，分开进行操作
        String C_path = "D:\\CrossFunction\\1comp\\C-" + filename;//后期将bus换为参数
        String I_path = "D:\\CrossFunction\\1comp\\I-" + filename;//后期将bus换为参数


        //2，获得每个文件夹中的文件，并将其读取出来，对应版本号来存储在hashmap中
        File I_root = new File(I_path);
        File[] I_list = I_root.listFiles();

        File C_root = new File(C_path);
        File[] C_list = C_root.listFiles();

        //3,首先是遍历I_list中的文件信息，并分为1，2，3型来进行文件的输出
        List<String> I_name = new ArrayList<>();//记录版本的名称，后面进行函数对信息的匹配

        List<List<List<String>>> I_type1 = new ArrayList<>();//分别存放不同类型的变动文件
        List<List<List<String>>> I_type2 = new ArrayList<>();
        List<List<List<String>>> I_type3 = new ArrayList<>();


        for(File f : I_list){//这里分别遍历I-bus中的bus-5.0.0，bus-5.6.0bus-5.9.0
            I_name.add(f.getName());//存储的是上一级文件名
            File[] all = f.listFiles();//遍历某个文件夹下面的文件

            for(File f1 : all){
                if (f1.getName().contains("-1-f确定的")){//后面要将判断换为不同类型的文件判断
                    I_type1.add(readFile(f1,f.getName()));
                }
                if (f1.getName().contains("-2-f确定的")){//后面要将判断换为不同类型的文件判断
                    I_type2.add(readFile(f1,f.getName()));

                }
                if (f1.getName().contains("-3-f确定的")){//后面要将判断换为不同类型的文件判断
                    I_type3.add(readFile(f1,f.getName()));
                }
            }
        }

        //3.1,对于type中的list，进行去重
        List<List<String>> I_res1 =  distic(I_name,I_type1);
        List<List<String>> I_res2 =  distic(I_name,I_type2);
        List<List<String>> I_res3 =  distic(I_name,I_type3);

        try {
            writeFile(I_res1,1,filename,1);
            writeFile(I_res2,2,filename,1);
            writeFile(I_res3,3,filename,1);
            System.out.println("I-" + filename + "的统计文件生成完毕");
        } catch (IOException e) {
            System.out.println("打印文件出现错误");
            e.printStackTrace();
        }

        //4,进一步是遍历C_list中的文件信息，并分为1，2，3型来进行文件的输出
        List<String> C_name = new ArrayList<>();//记录版本的名称，后面进行函数对信息的匹配

        List<List<List<String>>> C_type1 = new ArrayList<>();//分别存放不同类型的变动文件
        List<List<List<String>>> C_type2 = new ArrayList<>();
        List<List<List<String>>> C_type3 = new ArrayList<>();

        for(File f : C_list){//这里分别遍历I-bus中的bus-5.0.0，bus-5.6.0bus-5.9.0
            C_name.add(f.getName());//存储的是上一级文件名
            File[] all = f.listFiles();//遍历某个文件夹下面的文件

            for(File f1 : all){
                if (f1.getName().contains("-1-f确定的")){//后面要将判断换为不同类型的文件判断
                    C_type1.add(readFile(f1,f.getName()));
                }
                if (f1.getName().contains("-2-f确定的")){//后面要将判断换为不同类型的文件判断
                    C_type2.add(readFile(f1,f.getName()));
                }
                if (f1.getName().contains("-3-f确定的")){//后面要将判断换为不同类型的文件判断
                    C_type3.add(readFile(f1,f.getName()));
                }
            }
        }
        //4.1,对于type中的list，进行去重
        List<List<String>> C_res1 =  distic(C_name,C_type1);
        List<List<String>> C_res2 =  distic(C_name,C_type2);
        List<List<String>> C_res3 =  distic(C_name,C_type3);

        try {
            writeFile(C_res1,1,filename,2);
            writeFile(C_res2,2,filename,2);
            writeFile(C_res3,3,filename,2);
            System.out.println("C-" + filename + "的统计文件生成完毕");
        } catch (IOException e) {
            System.out.println("打印文件出现错误");
            e.printStackTrace();
        }



    }

    //这个方法，按照start和end的标记把函数对读出来，然后按照其中的函数长度信息来取出对应的函数,已验证，没问题
    public  List<List<String>> readFile(File file,String name){
        String s = null;
        List<List<String>> res = new ArrayList<>();
        List<String> init = new ArrayList<>();

        //1,先把所有的内容都取出来放在init里面
        try {  //这里是按行进行读取的
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
            br.readLine();//跳过第一行有文字的那个

            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行，全部读出来放在init里面
                init.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //2，然后在init里取出一个个的函数，以及修改的数量
        int i = 0;
        while(i < init.size()){
            String[] mark = init.get(i).split("-");
            if(mark.length == 5){
                int len1 = Integer.parseInt(mark[1]);
                int len2 = Integer.parseInt(mark[2]);
                int change1 = Integer.parseInt(mark[3]);
                int change2 = Integer.parseInt(mark[4]);

                List<String> first = new ArrayList<>();
                List<String> second = new ArrayList<>();

                first.add(init.get(i + 1));
                first.add(change1 + "--" + name + "--change");
                for(int j = i + 3; j < i + 3 + len1;j++){
                    first.add(init.get(j));
                }

                second.add(init.get(i + 2));
                second.add(change2 + "--" + name + "--change");
                for(int k = i + 3 + len1;k < i + 3 + len1 + len2;k++){
                    second.add(init.get(k));
                }

                res.add(first);
                res.add(second);

                i = i + 3 + len1 + len2;

            }else if(mark.length == 4){
                int len1 = Integer.parseInt(mark[1]);
                int len2 = Integer.parseInt(mark[2]);
                int change = Integer.parseInt(mark[3]);


                List<String> first = new ArrayList<>();
                List<String> second = new ArrayList<>();

                first.add(init.get(i + 1));
                first.add(change + "--" + name + "--len" );
                for(int j = i + 3; j < i + 3 + len1;j++){
                    first.add(init.get(j));
                }

                second.add(init.get(i + 2));
                second.add(change + "--" + name + "--len" );
                for(int k = i + 3 + len1;k < i + 3 + len1 + len2;k++){
                    second.add(init.get(k));
                }

                res.add(first);
                res.add(second);

                i = i + 3 + len1 + len2;
            }
        }
        return  res;
    }

    public List<List<String>> distic(List<String> name,List<List<List<String>>> info) {
        List<List<String>> all = new ArrayList<>();
        List<List<String>> all_dis = new ArrayList<>();
        float change_sum;
        int flag = 0;
        float all_sum = 0;
        float per = 0;

        for(List<List<String>> s1 : info){
            for(List<String> s2 : s1){
                all.add(s2);
            }
        }

        for(List<String> s3 : all){
            flag = 1;
            for(int i = 0;i < all_dis.size();i++){
                if(s3.get(0).equals(all_dis.get(i).get(0))){
                    flag =0;
                }
            }
            if(flag == 1){
                all_dis.add(s3);
                all_sum = all_sum + s3.size() - 2;
            }
        }

        change_sum = 0;
        for (List<String> s4 : all_dis){
            int temp = Integer.parseInt(s4.get(1).split("--")[0]);
            change_sum += temp;
        }

        List<String> sumStr = new ArrayList<>();
        if(change_sum != 0 && all_sum != 0){
            per = change_sum / all_sum * 100;
        }
        sumStr.add("最新版本改变的行数为：" + change_sum + ",改变的函数总数为" + all_dis.size());
        sumStr.add("总的行数为" + all_sum+"，修改的函数占总的函数的行数为："  + per);
        all_dis.add(0,sumStr);

        return all_dis;
    }

    //type1，2，3代表不同克隆类型的结果，flag为1代表I，flag为2代表C
    public void writeFile(List<List<String>> res,int type,String name,int flag) throws IOException {
        String path = null;
        File f = null;
        if(flag == 1){
            path = "D:" + f.separator + "CrossFunction" + f.separator + "1comp" +  f.separator + "I-" + name + f.separator + name + "的所有关于~" + type + "~类型的修改文件.xml";
        }else if(flag == 2){
            path = "D:" + f.separator + "CrossFunction" + f.separator + "1comp" +  f.separator + "C-" + name + f.separator + name + "的所有关于~" + type + "~类型的修改文件.xml";
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
            for (List<String> rl : res) {
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


}
