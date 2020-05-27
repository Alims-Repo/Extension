package com.alim.extension;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import com.alim.extension.YTExtractor.VideoMeta;
import com.alim.extension.YTExtractor.YouTubeExtractor;
import com.alim.extension.YTExtractor.YtFile;

public class ExtractService extends Service {

    public ExtractService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String LINK = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (LINK.contains("youtube.com/watch?v="))
            LINK = LINK.substring(LINK.indexOf("youtube.com/watch?v=")+20);
        else if (LINK.contains("://youtu.be/"))
            LINK = LINK.substring(LINK.indexOf("://youtu.be/")+12);
        final String url = LINK;
        final Intent inten = new Intent("CrackedURL");
        new YouTubeExtractor(this) {
            @SuppressLint("CheckResult")
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                try {
                    inten.putExtra("Size", ytFiles.size());
                    inten.putExtra("Name", vMeta.getTitle());
                    Log.println(Log.ASSERT,"URL", vMeta.getTitle());
                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);
                        inten.putExtra("Url : " + i, ytFile.getUrl());
                        inten.putExtra("Tag : " + i, Integer.valueOf(itag));
                    }
                    inten.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    inten.setComponent(new ComponentName("com.alim.snaploader","com.alim.snaploader.Reciever.LinkReceiver"));
                    sendBroadcast(inten);
                } catch (Exception e) {
                    inten.putExtra("ERROR", e.toString());
                    inten.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    inten.setComponent(new ComponentName("com.alim.snaploader","com.alim.snaploader.Reciever.LinkReceiver"));
                    sendBroadcast(inten);
                    Toast.makeText(ExtractService.this, e.toString(), Toast.LENGTH_SHORT).show();
                    /*try {
                        com.commit451.youtubeextractor.YouTubeExtractor extractor
                                = new com.commit451.youtubeextractor.YouTubeExtractor.Builder().build();
                        extractor.extract(url)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<YouTubeExtraction>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Toast.makeText(ExtractService.this, "onSubscribe", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(YouTubeExtraction youTubeExtraction) {
                                        //.streams.filterIsInstance<Stream.VideoStream>()[1].url
                                        inten.putExtra("Size", youTubeExtraction.getStreams().size());
                                        inten.putExtra("Name", youTubeExtraction.getTitle());
                                        for (int i = 0; i <youTubeExtraction.getStreams().size(); i++) {
                                            inten.putExtra("Url : " + i, youTubeExtraction.getStreams().get(i).toString());
                                            Log.println(Log.ASSERT,"URL", youTubeExtraction.getStreams().get(i).toString());
                                        }
                                        inten.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                        inten.setComponent(new ComponentName("com.alim.snaploader","com.alim.snaploader.Reciever.LinkReceiver"));
                                        //sendBroadcast(inten);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.println(Log.ASSERT,"URL", url);
                                        Toast.makeText(ExtractService.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } catch (Exception ex) {
                        inten.putExtra("ERROR", ex.toString());
                        inten.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        inten.setComponent(new ComponentName("com.alim.snaploader","com.alim.snaploader.Reciever.LinkReceiver"));
                        sendBroadcast(inten);
                        Toast.makeText(ExtractService.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }*/
                }

            }
        }.extract(intent.getStringExtra(Intent.EXTRA_TEXT),true,false);
        return super.onStartCommand(intent, flags, startId);
    }
}