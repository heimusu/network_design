//完了：乱数を生成・切り上げしてdouble型にキャスト,一様分布クラスの作成,任意の範囲での乱数生成,簡単なFIFOの実装
//タスク：到着時刻・サービス時刻の処理，経過時間での繰り返し実装

import java.math.BigDecimal;
import java.util.Map;
import java.util.LinkedHashMap;

class montecarlo{
    public static void main(String[] args){
        double t = 0.0; //マイクロ秒単位
        int A =64; //バッファ内データ数
        double arrivingTime = uniform_distribution(0.0,5.0); //到着間隔
        double serviceTime = uniform_distribution(0.0,5.5); //パケットのサービス時間
        //double arrivingTime = characteristic_distribution(0.9);
        //double serviceTime = characteristic_distribution(1.1);
        int rejectCount = 0;//棄却パケット数
        int arriveCount = 0;//到着パケット数
        double rejectRate = 0.0;//棄却率
        int counter = 0;
        int point = 0;
        double delay = 0.0;
        double delayAverage = 0.0;
        int dataAverage = 0;
        Map<Integer,String>map = new LinkedHashMap<>();
        System.out.println("-----------------------------------");
        //到着時刻・サービス時刻の決定
        for(counter = 0; counter < 1000000; counter++){
            System.out.printf("時刻t = %f\n",t);
            //判定のために四捨五入してキャスト
            int arriveInt = casting(arrivingTime);
            int serviceInt = casting(serviceTime);
            System.out.printf("arriveInt: %d\n",arriveInt);
            System.out.printf("serviceInt: %d\n",serviceInt);
            //離脱時間が早い場合
            if(arriveInt >= serviceInt || serviceInt == 0){
                /*
                arriveCount++;
                t += serviceTime;
                arrivingTime -= serviceTime;
                delay -= serviceTime; //0以下は0
                if(casting(delay) <= 0){
                    delay = 0.0;
                }
                //バッファにあれば…
                if(point > 0){
                    serviceTime = uniform_distribution(0.0,10.0);
                    map.remove(point);
                    point--;
                }
                else{
                    //バッファが空
                    //serviceTime = Double.MAX_VALUE;
                    serviceTime = 0.0;
                }*/
                System.out.println("Update ServiceTime");
                t += serviceTime;
                arrivingTime -= serviceTime;
                if(casting(arrivingTime) <= 0){
                    arrivingTime = 0.0;
                }
                arriveCount++;
                delay -= serviceTime;
                if(casting(delay) <= 0){
                    delay = 0.0;
                }
                System.out.printf("Delay time: %f\n",delay);
                if(point > 0){
                    serviceTime = uniform_distribution(0.0,5.5);
                    //serviceTime = characteristic_distribution(1.1);
                    map.remove(point);
                    point--;
                }
                else{
                    serviceTime = uniform_distribution(0.0,5.0);;
                }
            }
            //到着時刻が早い場合
            else if(serviceInt > arriveInt){
                /*
                map.put(counter,"packet");
                point++;
                arriveCount++;
                t += arrivingTime;
                if(serviceTime != Double.MAX_VALUE){
                    serviceTime -= arrivingTime;
                }
                delay -= arrivingTime;//0以下の処理
                if(casting(delay) <= 0){
                    delay = 0.0;
                }
                arrivingTime = 0.0;
                if(point <= A){
                    delayAverage += delay;
                    delay += serviceTime;
                    //パケットがない
                    if(serviceTime == 0.0){
                        //新たな実行時間
                        serviceTime = uniform_distribution(0.0,10.0);
                    }
                }
                else if(point > A){
                    rejectCount++;
                }
                */
                System.out.println("Update Arrivingtime");
                arriveCount++;
                t += arrivingTime;
                serviceInt = casting(serviceTime);
                if(serviceInt != 0){
                    serviceTime -= arrivingTime;
                }
                delay -= arrivingTime;
                if(casting(delay) <= 0){
                    delay = 0.0;
                }
                map.put(point,"Arriving packet");
                point++;
                if(point < A){
                    delayAverage += delay;
                    delay += serviceTime;
                    if(casting(serviceTime) == 0){
                        serviceTime = uniform_distribution(0.0,5.5);
                        //serviceTime = characteristic_distribution(1.1);
                    }
                }
                else if(point > A){
                    rejectCount++;
                    arriveCount--;
                    point--;
                }
                arrivingTime = uniform_distribution(0.0,5.5);
                //arrivingTime = characteristic_distribution(0.9);
                dataAverage += point;
            }
            //修了判定
            System.out.printf("point: %d\n",point);
            System.out.println("-----------------------------------");
        }
        System.out.println("-----------------------------------");
        //棄却率の計算
        rejectRate = (double)(counter - arriveCount) / (double)counter;
        //現在時刻
        System.out.printf("t = %f \n",t);
        System.out.printf("point = %d\n",point);
        System.out.printf("rejectCount = %d\n",rejectCount);
        System.out.printf("rejectRate = %f\n",rejectRate);
        System.out.printf("arriveCount = %d\n",arriveCount);
        System.out.println(delayAverage / counter);
        System.out.println((double)dataAverage / t);
    }
    //一様分布クラス
    public static double uniform_distribution(double a, double b){
        double x;
        //double a = 0.0;
        //double b = 5.0;
        x = a + (b - a) * Math.random();
        BigDecimal bd = new BigDecimal(x);
        BigDecimal bd2 = bd.setScale(2,BigDecimal.ROUND_UP);
        double res = bd2.doubleValue();

        return res;
    }
    //指数分布クラス
    public static double characteristic_distribution(double lambda){
        double tau;
        tau = (-1.0 / lambda) * Math.log(1.0 - Math.random());
        System.out.println(tau);
        BigDecimal bd = new BigDecimal(tau);
        BigDecimal bd2 = bd.setScale(2,BigDecimal.ROUND_UP);
        double res = bd2.doubleValue();

        return res;
    }

    //四捨五入してint型にキャスト
    public static int casting(double db){
        BigDecimal bd = new BigDecimal(db);
        BigDecimal bd2 = bd.setScale(1,BigDecimal.ROUND_UP);
        double res = bd2.doubleValue();
        return (int)res;
    }

        /*
        double val = Math.random();
        BigDecimal bd = new BigDecimal(val);
        BigDecimal bd2 = bd.setScale(2,BigDecimal.ROUND_UP);//小数点第三位切り上げ
        r = bd2.doubleValue();
        System.out.println(r);
        double test = uniform_distribution();
        System.out.println(test);
        */

        /*
        //LinkedHashMapを使ったFIFOの実装
        Map<String,String> map = new LinkedHashMap<>();
        map.put("a","aaa");
        map.put("b","bbb");
        map.put("c","ccc");
        for(String key : map.keySet()){
            System.out.printf("key = %s , value = %s \n",key,map.get(key));
        }
        */

}
