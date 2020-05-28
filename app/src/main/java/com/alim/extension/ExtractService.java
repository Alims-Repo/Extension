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
        final Intent inten = new Intent("CrackedURL");
        YouTubeExtractor extractor = new YouTubeExtractor(this) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                try {
                    inten.putExtra("Size", ytFiles.size());
                    inten.putExtra("Name", vMeta.getTitle());
                    inten.putExtra("Thumb", vMeta.getThumbUrl());
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
                    inten.setComponent(new ComponentName("com.alim.snaploader", "com.alim.snaploader.Reciever.LinkReceiver"));
                    sendBroadcast(inten);
                    Toast.makeText(ExtractService.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        extractor.extract(intent.getStringExtra(Intent.EXTRA_TEXT),true,false);
        return super.onStartCommand(intent, flags, startId);
    }
}