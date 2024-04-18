//package com.example.caloriecounter.services;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//public class DatabaseUpdateWorker extends Worker {
//
////    private long timeMillis;
////    private static  int counter =0 ;
////    private Handler handler;
//    private Context context;
////    public static boolean isStopped;
//
//    private String TAG = "DatabaseUpdateWorker";
//
//    public DatabaseUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        this.context = context;
////        handler = new Handler(Looper.getMainLooper());
////        setProgressAsync(new Data.Builder()
////                .putString("PROGRESS", "Time "+0L+"_"+"Counter: "+0).build());
////        isStopped = false;
//
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
////        timeMillis = System.currentTimeMillis();
////        counter++;
////
////        DailyData dailyData = DailyDataHolder.getInstance().getData();
////        DailyDataDAO dailyDataDAO = new DailyDataDAOImpl();
////        if(dailyData!=null) {
////            dailyDataDAO.update(dailyData).addOnCompleteListener(new OnCompleteListener<Void>() {
////                @Override
////                public void onComplete(@NonNull Task<Void> task) {
////                    Log.d(TAG, "DailyData updated successfully: steps today " + dailyData.getSteps());
////                }
////            });
////        }
////        setProgressAsync(new Data.Builder().putString("PROGRESS", "Time "+timeMillis+"_"+"Counter: "+counter).build());
////        handler.post(new Runnable(){
////            @Override public void run(){
////                Toast.makeText(context, "Time "+timeMillis+"_"+"Counter: "+counter, Toast.LENGTH_SHORT).show();
////            }
////        });
//        return Result.success();
//    }
//}
