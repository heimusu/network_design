//完了：乱数を生成・切り上げしてdouble型にキャスト,一様分布クラスの作成,任意の範囲での乱数生成,簡単なFIFOの実装
//タスク：到着時刻・サービス時刻の処理，経過時間での繰り返し実装

import java.math.BigDecimal;
import java.util.Map;
import java.util.LinkedHashMap;

class montecarlo{
    public static void main(String[] args){
        double t = 1.0; //マイクロ秒単位
        int A =64; //システム容量
        //double arrivingTime = uniform_distribution(0.0,5.0); //到着間隔
        //double serviceTime = uniform_distribution(0.0,5.5); //パケットのサービス時間
        int rejectCount = 0;//棄却パケット数
        int arriveCount = 0;//到着パケット数
        double rejectRate = 0.0;//棄却率
        int counter = 0;
        int point = 0;
        double delay = 0.0;
        double delayAverage = 0.0;
        int dataAverage = 0;
        Map<Integer,String>map = new LinkedHashMap<>();
        //待ち行列理論変数
        double lambda = 10.0;
        double myu = 20.0;
        double arrivingTime = poisson_distribution(lambda,t);
        double serviceTime = characteristic_distribution(myu,t);
        System.out.printf("arrivingTime %f\n",arrivingTime);
        System.out.printf("ServiceTime %f\n",serviceTime);

        System.out.println("-----------------------------------");
        //到着時刻・サービス時刻の決定
        for(counter = 0; counter < 5; counter++){
            System.out.printf("時刻t = %f\n",t);
            //判定のために四捨五入してキャスト
            int arriveInt = casting(arrivingTime);
            int serviceInt = casting(serviceTime);
            System.out.printf("arriveInt: %d\n",arriveInt);
            System.out.printf("serviceInt: %d\n",serviceInt);
            //離脱時間が早い場合
            if(arriveInt >= serviceInt || serviceInt == 0 || arriveInt == serviceInt){
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
                    serviceTime = characteristic_distribution(myu,t);
                    map.remove(point);
                    point--;
                }
                else{
                    serviceTime = characteristic_distribution(myu,t);
                }
            }
            //到着時刻が早い場合
            else if(serviceInt > arriveInt){
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
                        serviceTime = poisson_distribution(lambda,t);
                    }
                }
                else if(point > A){
                    rejectCount++;
                    arriveCount--;
                    point--;
                }
                arrivingTime = poisson_distribution(lambda,t);
                t += arrivingTime;
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
        double rho = lambda / myu;
        double averageGuestCount = rho / (1.0 - rho);
        double averageServiceTime = 1.0 / myu;
        double averageResponseTime = (1.0 / (1.0 - rho)) * averageServiceTime;
        System.out.printf("Rho: %f\n",rho);
        System.out.printf("averageGuestCount: %f\n",averageGuestCount);
        System.out.printf("averageResponseTime: %f\n",averageResponseTime);
    }
    //一様分布クラス
    public static double uniform_distribution(double a, double b){
        double x;
        x = a + (b - a) * Math.random();
        BigDecimal bd = new BigDecimal(x);
        BigDecimal bd2 = bd.setScale(2,BigDecimal.ROUND_UP);
        double res = bd2.doubleValue();

        return res;
    }
    //指数分布クラス
    public static double characteristic_distribution(double myu, double t){
        double tau;
        //tau = -1.0 / lambda * Math.log(1.0 - Math.random());
        tau = 1.0 - Math.exp(-myu * t);
        //System.out.println(tau);
        /*
        BigDecimal bd = new BigDecimal(tau);
        BigDecimal bd2 = bd.setScale(2,BigDecimal.ROUND_UP);
        double res = bd2.doubleValue();
        */

        //return res;
        return tau;
    }
    //ポアソン分布クラス
    public static double poisson_distribution(double lambda, double t){
        double p;
        p = 1.0 - Math.exp(-lambda * t);
        /*
        BigDecimal bd = new BigDecimal(p);
        BigDecimal bd2 = bd.setScale(2,BigDecimal.ROUND_UP);
        double res = bd2.doubleValue();
        */

        //return res;
        return p;
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
