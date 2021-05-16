public class test {
    public static void main(String[] args) {
        String s1 = "asd ij{ }";
        String s2 = "ij{ } asd  ";

        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();

        int sum1 = 0;
        for(char a : c1){
            sum1 = sum1 + a;
        }

        int sum2 = 0;
        for(char a : c2){
            sum2 = sum2 + a;
        }

        System.out.println(sum1 + "   " + sum2);

    }
}
