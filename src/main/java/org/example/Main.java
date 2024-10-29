package org.example;

import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static void tellAdvice(){
        Random random=new Random();
        int randNum=random.nextInt(10);

        if(randNum>5){
            System.out.println("You know me well but you have to understand me fully");
        }else{
            System.out.println("You don't know anything about me, What he said is almost correct");
        }
    }

    private static List<Integer> topKElements(Integer[] nums, int k){
        HashMap<Integer,Integer> count=new HashMap<>();
        List<List<Integer>> freq=new ArrayList<>();

        // 0,1,2,3,4,5,6
        //       [1,2]
        // [1,1,1,1,1,1] [1,1,1,2,2,2]

        for (int i=0;i<nums.length;i++){
            freq.add(new ArrayList<>());
        }

        System.out.println(freq);
        for(int num:nums){
            count.put(num,count.getOrDefault(num,0)+1);
        }

        count.forEach((key,val)->freq.get(val).add(key));
        // 1,3
        List<Integer> res=new ArrayList<>();
        for(int i=nums.length-1;i>0;i--){
            for(int num:freq.get(i)){
                res.add(num);
                if(res.size()==k)
                    return res;
            }
        }
        return res;
    }

    private static int fib(int n){
        if(n<=1)
            return n;
        return fib(n-1)+fib(n-2);
    }

    public static void printAlphabets(boolean cased){
        int intialCount=cased?65:97;
        for (int i = 0; i < 26; i++) {
            System.out.println((char) (intialCount+i));
        }
    }

    public static int maxSubArr(int[] nums,int k){
        int maxSum=0;
        for(int i=0;i<k;i++){
            maxSum+=nums[i];
        }
        int window_sum=maxSum;
        for(int i=k;i<nums.length;i++){
            window_sum+=nums[i]-nums[i-k];
            maxSum=Math.max(window_sum,maxSum);
        }
        return maxSum;
    }

    private static int kthMaxFreqElement(int[] array,int k){
        HashMap<Integer,Integer> freqMap=new HashMap<>();
        for(int num:array){
            freqMap.put(num,freqMap.getOrDefault(num,0)+1);
        } //o(n)

        return -1;
    }

    private static int helper(int[] prices,int i,int n,boolean buy,int k){
        if(i==n)
            return 0;
        if(k==0)
            return 0;
        int take,dont_take;
        if(buy){
            // Case when we can either sell or skip selling today
            dont_take=helper(prices,i+1,n,true,k);
            take=prices[i]+helper(prices,i+1,n,false,k-1);
        }else{
            dont_take=helper(prices,i+1,n,false,k);
            take=-prices[i]+helper(prices,i+1,n,true,k-1);
        }
        return Math.max(dont_take,take);
    }

    private static int findMaxProfit(int[] prices,int fee){
        List<List<List<Integer>>> dp = new ArrayList<>(prices.length);
        for (int i = 0; i < prices.length; i++) {
            List<List<Integer>> innerList = new ArrayList<>(2);
            for (int j = 0; j < 2; j++) {
                List<Integer> innermostList = new ArrayList<>();
                for (int k = 0; k < 3; k++) {
                    innermostList.add(-1);
                }
                innerList.add(innermostList);
            }
            dp.add(innerList);
        }
        return helper(prices,0,prices.length,false,2);
    }

    public static void main(String[] args) {
//        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//        // to see how IntelliJ IDEA suggests fixing it.
//        System.out.printf("Hello and welcome!");
//
//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("i = " + i);
//        }
//        printAlphabets(false);
//        int[] nums={1,1,2,3,4,2,1};
//
////        System.out.println((kthMaxFreqElement(nums, 2)));
//        System.out.println(fib(5));
//        // 0,1,1,2,3,5
//        List<Double> numsList=new ArrayList<>();
//        numsList.add(10D);
//        numsList.add(20D);
//        Iterator iterator=numsList.iterator();
//
//        while(iterator.hasNext()){
//            System.out.println(iterator.next());
//        }
        System.out.println(topKElements(new Integer[]{1, 1, 1, 2, 2, 2, 3}, 2));
//        Iterator it=List.of("K","l").iterator();
//
//        while (it.hasNext()){
//            System.out.println(it.next());
//        }
//        tellAdvice();
        int[] prices={1,3,2,8,4,9};
        System.out.println(findMaxProfit(prices,2));
    }
}