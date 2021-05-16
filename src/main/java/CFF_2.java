import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CFF_2 {
   /*
    运行nicad之后，对于某个版本，需要生成三个克隆的具体的函数文件，也就是在CrossFunction文件夹中，以
    项目名和版本号来生成文件夹，来存放生成的三个函数文件。

    在一个CloneOrigin文件夹中
    那么需要准备好初始的代码克隆对文件，以项目名-版本号-克隆类型的格式来存放，
    同样准备好代码克隆函数文件，以项目名-版本号-克隆类型-f的格式来存放

            在一个CrossFunc文件夹中
    由MCC_funcrion_simp生成跨服务克隆对文件，以项目名-版本号-克隆类型的格式来存放
    由CFFinder来生成跨服务函数文件，以项目名-版本号-克隆类型-f的格式来存放

    控制台提示请输入1型克隆对，克隆函数；2型克隆对，克隆函数；3型克隆对，克隆函数；服务所在位置；
    这些信息存放在不同的变量当中

    输出的时候，就使用上面的文件信息就行

    */

    public static void main(String[] args){
        while(true){
            System.out.println("输入1进行跨服务文件生成，输入2进行不同版本函数对比，输入3进行函数去重和行数统计，输入0退出程序");
            Scanner scan = new Scanner(System.in);
            int a = scan.nextInt();
            if(a == 1){

                System.out.println("你选择了跨服务文件生成，将得到跨服务代码克隆对和对应的具体函数");
                System.out.println("首先输入项目名（一般形如：jboot）");                String all = scan.next();

                System.out.println("输入服务的位置，打开文件数一下，从0开始");
                int loc= scan.nextInt();

                System.out.println("输入你想进行的操作，1表示寻找跨服务代码克隆，2表示寻找服务内代码克隆");
                int flag = scan.nextInt();

                CPFinder cpFinder = new CPFinder();
                CFFinder cff = new CFFinder();

                File file =  new  File("D:\\CloneOrigin");
                String [] fileName = file.list();
                for(String filename : fileName){
                    if(filename.contains(all)){
                        String first = filename + "-1";
                        String second = filename + "-2";
                        String third = filename + "-3";
                        String func = "\\" + filename + "\\" + filename + "-f";

                        try {
                            System.out.println("———————生成代码克隆对———————");
                            cpFinder.findcross( first,loc,flag);
                            cpFinder.findcross( second,loc,flag);
                            cpFinder.findcross( third,loc,flag);
                        } catch (Exception e) {
                            System.out.println("!!!生成代码克隆对文件出现异常，请排查");
                            e.printStackTrace();
                        }
                        System.out.println("———————代码克隆对生成完毕~~~———————");

                        System.out.println("———————生成代码克隆对对应函数———————");
                        try {
                            cff.getFuntionInfo(func,first,flag);
                            cff.getFuntionInfo(func,second,flag);
                            cff.getFuntionInfo(func,third,flag);
                        } catch (Exception e) {
                            System.out.println("!!!生成克隆函数文件出现异常，请排查");
                            e.printStackTrace();
                        }
                        System.out.println("———————代码克隆对对应函数生成完毕~~~———————");

                    }
                }

            }else if(a == 2){
                System.out.println("请输入最新版本，形如bus-6.2.0");
                String base = scan.next();
                System.out.println("请输入服务位置（打开文件看，从0开始计数）");
                int loc = scan.nextInt();

                String name = base.split("-")[0];
                String cross = "C-" + name;
                String inner = "I-" + name;

                File file =  new  File("D:\\CrossFunction");
                String [] fileName = file.list();

                List<String> crossPro = new ArrayList<>();
                List<String> innerPro = new ArrayList<>();

                for(String filename:fileName){
                    if(filename.contains(cross) && !(filename.equals("C-" + base))){
                        crossPro.add(filename);
                    }
                    if(filename.contains(inner) && !(filename.equals("I-" + base))){
                        innerPro.add(filename);
                    }
                }


                if(crossPro.size() > 0){
                    int size = crossPro.size();
                    compare(size,crossPro,"C-" + base,1,loc);
                }

                if(innerPro.size() > 0){
                    int size = crossPro.size();
                    compare(size,innerPro,"I-" + base,2,loc);
                }


            }else if(a == 3) {
                System.out.println("请输入项目名，如bus");
                String name = scan.next();
                Counting counting = new Counting();
                counting.getDis(name);
            }else if(a == 0){
                System.out.println( "程序停止运行");
                break;
            }
        }
    }


    //size是所有项目-1，然后list是除了base之外的其他项目的项目名，比如C-bus-6.1.0,C-bus-6.1.5
    //base的话是C-bus-6.2.0
    public static  void compare(int size,List<String> list,String base,int flag,int loc){
        Func_compN funcComp = new Func_compN();
        for(int i = 0;i < list.size();i++){
            String proName = list.get(i);

            String base1 = base + "-1-f";
            String base2 = base + "-2-f";
            String base3 = base + "-3-f";

            String comp1 = proName + "-1-f";
            String comp2 = proName + "-2-f";
            String comp3 = proName + "-3-f";

            try {
                System.out.println("生成比对版本" + proName + "的文件");

                funcComp.compare(base3,comp3,flag,loc);
                funcComp.compare(base2,comp2,flag,loc);
                funcComp.compare(base1,comp1,flag,loc);
            } catch (IOException e) {
                System.out.println("生成函数对比文件出现错误！！！");
                e.printStackTrace();
            }


        }

    }
}
