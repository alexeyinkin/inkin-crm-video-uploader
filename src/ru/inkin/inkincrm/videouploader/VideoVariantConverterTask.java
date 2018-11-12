package ru.inkin.inkincrm.videouploader;

public class VideoVariantConverterTask
        extends TaskGeneric<VideoVariantConverterTaskProcessor>
        implements VideoVariantConverterListener
{
    private VideoVariantConverter videoVariantConverter;

    @Override
    public void process()
    {
        start();
    }

    private void start()
    {
        VideoVariantCreatorTask parent1     = getVideoVariantCreatorTask();
        VideoCreatorTask        parent2     = parent1.getVideoCreatorTask();
        FileTask                fileTask    = parent2.getFileTask();
        VideoVariantSettings    settings    = parent1.getVideoVariantSettings();

        videoVariantConverter = new VideoVariantConverter();
        videoVariantConverter.setFileTask(fileTask);
        videoVariantConverter.setVariantSettings(settings);
        videoVariantConverter.addListener(this);
        videoVariantConverter.start();

        getProcessor().sendNoMoreSubTasks(this); //, VideoSegmentUploaderTask.class);
    }

    public VideoVariantCreatorTask getVideoVariantCreatorTask()
    {
        return (VideoVariantCreatorTask) getTrace().getLastLine().task;
    }

    @Override
    public void onSegmentFileReady(VideoSegmentInfo videoSegmentInfo)
    {
        VideoVariantConverterTaskProcessor  processor = getProcessor();
        VideoSegmentUploaderTask nextTask = processor.createNextTask(this);
                //this, VideoSegmentUploaderTask.class);

        nextTask.setVideoSegmentInfo(videoSegmentInfo);
        processor.sendTaskToNext(nextTask); //, TaskProcessor.NOT_LAST);
    }

    public VideoVariantConverter getConverter()
    {
        return videoVariantConverter;
    }
}