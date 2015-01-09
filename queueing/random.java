class queueing{
    public static void main(String[] args){
        int counter = 0;
        //待ち行列理論変数
        double lambda = 1.01;
        double myu = 0.99;
        int K = 10;
        double rho = lambda / myu;
        double p = 0.0;
        double elk = 0.0;

        System.out.println("-----------------------------------");
        System.out.printf("rho: %f\n",rho);
        //到着時刻・サービス時刻の決定
        for(K = 10; K <= 200; K += 10){
            elk = 0.0;
            p = 0.0;
            System.out.printf("K: %d\n",K);
            if(rho != 1){
                for(counter = 0; counter < 3000; counter++){
                    p = (1.0 - rho) / (1.0 - Math.pow(rho,K+1));
                    elk += p;
                }
            }
            else{
                System.out.println("rho = 1");
                for(counter = 0; counter < 3000; counter++){
                    p = 1.0 / (K + 1);
                    elk += p;
                }
            }
            System.out.println("-----------------------------------");
            //棄却率の計算
            double waitTime = elk / lambda;
            System.out.printf("pk: %f\n",p);
            System.out.printf("AverageGuestCount: %f\n",elk);
            System.out.printf("AverageWaitngTime: %f\n",waitTime);
        }
    }
}
